package dk.bemyndigelsesregister;

import org.apache.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Migration of delegations from old data structure to new
 */
public class Migration {
    private static Logger logger = Logger.getLogger(Migration.class);
    private static final Date FAR_FUTURE = new Date(200, 0, 1); // 2100-01-01

    private enum Command {find, migrate, all}

    private MigrationSettings settings;

    public static void main(String[] args) {
        logger.info("---------------------");
        logger.info("BEM 2 migration start");
        logger.info("---------------------");

        if (args != null && args.length > 0) {
            Command command = Command.valueOf(args[0]);
            if (command != null) {
                new Migration(command);
                System.exit(0);
            }
        }
        logger.error("Missing parameter");
        System.exit(-1);
    }

    private Migration(Command command) {
        try {
            settings = new MigrationSettings();
            logger.info(settings);
        } catch (IOException e) {
            logger.error("Error loading properties from file", e);
            System.exit(-1);
        }

        try {
            Class.forName(settings.getJdbcDriver());
        } catch (Exception e) {
            logger.error("Error loading MySQL driver", e);
            System.exit(-1);
        }

        try {
            switch (command) {
                case find:
                    dumpDelegationKeys();
                    break;

                case migrate:
                    migrateDelegations();
                    break;

                case all:
                    dumpDelegationKeys();
                    migrateDelegations();
                    break;
            }
        } catch (Exception ex) {
            logger.error("An exception occurred during migration", ex);
        }
    }


// ------------------------------------------------------------------------------------------------------------------
// dump to file

    private void dumpDelegationKeys() throws SQLException, ParseException, IOException {
        List<Key> list = findKeysOfChangedDelegations();
        if (list != null && !list.isEmpty()) {
            dumpDelegationKeysToFile(settings.getFilename(), list);
        }
    }

    private List<Key> findKeysOfChangedDelegations() throws SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            stmt = con.prepareStatement("SELECT DISTINCT linked_system_id, arbejdsfunktion_id, bemyndigende_cpr, bemyndigede_cpr, bemyndigede_cvr, status FROM bemyndigelse WHERE sidst_modificeret >= ? ORDER BY linked_system_id, arbejdsfunktion_id, bemyndigende_cpr, bemyndigede_cpr, bemyndigede_cvr, status");
            stmt.setDate(1, settings.getChangedSinceDate());

            logger.info("Fetching keys of delegations changed since " + settings.getChangedSinceDate());
            rs = stmt.executeQuery();

            List<Key> list = new LinkedList<>();

            while (rs.next()) {
                Key key = new Key(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));
                list.add(key);
            }

            logger.info("Found " + list.size() + " keys");
            return list;
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (con != null)
                    con.close();
            } catch (Exception ignored) {
            }
        }
    }

    private void dumpDelegationKeysToFile(String filename, List<Key> list) throws IOException {
        FileWriter fw = null;
        try {
            File file = new File(filename);
            logger.info("Writing " + list.size() + " keys to file " + file.getAbsolutePath());
            fw = new FileWriter(file);
            for (Key key : list) {
                fw.write(key.toString());
                fw.write("\n");
            }
            logger.info("Done creating file");
        } finally {
            if (fw != null)
                fw.close();
        }
    }

// ------------------------------------------------------------------------------------------------------------------
// migration

    private void migrateDelegations() throws IOException, SQLException {
        File file = new File(settings.getFilename());

        logger.info("Processing delegation keys in file " + file.getAbsolutePath());

        LineNumberReader reader = null;

        // count lines
        int totalKeys = 0;
        try {
            reader = new LineNumberReader(new BufferedReader(new FileReader(file)));
            String line = reader.readLine();
            while (line != null) {
                totalKeys += 1;
                line = reader.readLine();
            }
        } finally {
            if (reader != null) {
                reader.close();
                reader = null;
            }
        }

        logger.info("  No. of keys: " + totalKeys);
        if (settings.getSkipKeys() > 0)
            logger.info("  Skipping " + settings.getSkipKeys() + " keys");


        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            reader = new LineNumberReader(new BufferedReader(new FileReader(file)));

            int lineNo = 0;
            String line = reader.readLine();
            while (line != null) {
                lineNo++;
                if (lineNo >= settings.getSkipKeys()) {
                    if (!line.isEmpty()) {
                        logger.info(lineNo + "/" + totalKeys);
                        Key key = new Key(line);
                        List<Delegation> oldDelegations = getOldDelegations(connection, key);
                        if (oldDelegations != null && !oldDelegations.isEmpty()) {
                            List<Delegation> newDelegations = determineNewDelegations(key, oldDelegations);
                            if (newDelegations != null && !newDelegations.isEmpty()) {
                                clearPreviousNewDelegations(connection, key);
                                saveNewDelegations(connection, key, newDelegations);
                                connection.commit();
                            }
                        }
                    }
                }
                line = reader.readLine();
            }
            logger.info("Migration done");
        } catch (Exception ex) {
            logger.error("Caught Exception, rolling back", ex);
            if (connection != null && !connection.isClosed())
                connection.rollback();
        } finally {
            if (reader != null)
                reader.close();
            if (connection != null)
                connection.close();
        }
    }


    private List<Delegation> getOldDelegations(Connection connection, Key key) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT b.kode, b.godkendelsesdato, b.gyldig_fra, b.gyldig_til, b.sidst_modificeret, b.sidst_modificeret_af, r.kode, a.kode, s.kode" +
                    " FROM bemyndigelse b" +
                    " JOIN arbejdsfunktion a ON a.id = b.arbejdsfunktion_id" +
                    " JOIN linked_system s ON s.id = b.linked_system_id" +
                    " JOIN rettighed r ON r.id = b.rettighed_id" +
                    " WHERE b.linked_system_id = ? AND b.arbejdsfunktion_id = ? AND bemyndigende_cpr = ? AND bemyndigede_cpr = ? AND status = ? AND bemyndigede_cvr " + (key.bemyndigede_cvr == null ? "IS NULL" : "= ?");

            stmt = connection.prepareStatement(sql);
            // + " AND (gyldig_til IS NULL OR gyldig_til > ?)");
            stmt.setLong(1, key.linked_system_id);
            stmt.setLong(2, key.arbejdsfunktion_id);
            stmt.setString(3, key.bemyndigende_cpr);
            stmt.setString(4, key.bemyndigede_cpr);
            stmt.setString(5, key.status);
            if (key.bemyndigede_cvr != null)
                stmt.setString(6, key.bemyndigede_cvr);
//            stmt.setDate(7, conversionDate);

            rs = stmt.executeQuery();

            List<Delegation> delegations = new LinkedList<>();
            while (rs.next()) {
                Delegation d = new Delegation(key);
                d.kode = rs.getString(1);
                d.godkendelsesdato = rs.getDate(2);
                d.gyldig_fra = rs.getDate(3);
                d.gyldig_til = rs.getDate(4);
                d.sidst_modificeret = rs.getDate(5);
                d.sidst_modificeret_af = rs.getString(6);
                d.gl_rettighed_kode = rs.getString(7);
                d.arbejdsfunktion_kode = rs.getString(8);
                d.linked_system_kode = rs.getString(9);

                if (d.gyldig_til == null || d.gyldig_til.after(FAR_FUTURE)) {
                    d.gyldig_til = FAR_FUTURE;
                }

                delegations.add(d);
            }

            logger.debug("  " + key + ": " + delegations.size() + " delegations"); // valid after " + conversionDate);
            for (Delegation d : delegations) {
                logger.debug("    " + d.toString());
            }

            return delegations;
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * create a set of BEM2 delegations from list of old delegations
     * Important: This method implements the actual conversion logic, but right now it's a dummy implementation
     *
     * @param key            Key of BEM2 delegation
     * @param oldDelegations list of old delegations
     */
    private List<Delegation> determineNewDelegations(Key key, List<Delegation> oldDelegations) {
        Date latestModifiedDate = null;
        String linked_system_kode = null;
        String arbejdsfunktion_kode = null;


        // 1. Fjern alle "tomme" perioder, dvs. dem hvor fradato = tildato
        List<Delegation> list1 = new LinkedList<>();
        for (Delegation d : oldDelegations) {
            if (linked_system_kode == null)
                linked_system_kode = d.linked_system_kode;
            if (arbejdsfunktion_kode == null)
                arbejdsfunktion_kode = d.arbejdsfunktion_kode;
            if (d.gyldig_fra.before(d.gyldig_til)) {
                list1.add(d);
                if (latestModifiedDate == null || d.sidst_modificeret.after(latestModifiedDate))
                    latestModifiedDate = d.sidst_modificeret;
            } else
                logger.debug("  Removed empty delegation " + d);
        }

        // 2. Merge alle A, B hvor og A.kode = B.kode og
        //    - A begynder før B og slutter i B
        //    -   eller
        //    - A er helt indeholdt i B
        //    til C, hvor C har den mindste fradato og største tildato af A og B
        boolean change = true;
        while (change) {
            change = false;
            for (Iterator<Delegation> i = list1.iterator(); i.hasNext(); ) {
                Delegation d = i.next();
                for (Delegation d2 : list1) {
                    boolean remove = false;
                    if (d != d2 && d.gl_rettighed_kode.equals(d2.gl_rettighed_kode)) {
                        if (d.gyldig_fra.before(d2.gyldig_fra) && !d.gyldig_til.before(d2.gyldig_fra) && d.gyldig_til.before(d2.gyldig_til)) { // d starter før og slutter i d2
                            logger.debug("  Expanding period " + d + " - " + d2);
                            d2.gyldig_fra = d.gyldig_fra;
                            remove = true;
                        } else if (!d.gyldig_fra.before(d2.gyldig_fra) && !d.gyldig_til.after(d2.gyldig_til)) { // d indeholdt i d2
                            logger.debug("  Expanding period " + d + " - " + d2);
                            remove = true;
                        }
                        if (remove) {
                            logger.debug("    New period " + d2);
                            i.remove();
                            change = true;
                            break;
                        }
                    }
                }
            }
        }

        // 3. Sorter datoer
        Set<Date> dates = new HashSet<>();
        for (Delegation d : list1) {
            if (d.gyldig_til.after(d.gyldig_fra)) {
                dates.add(d.gyldig_fra);
                dates.add(d.gyldig_til);
            }
        }
        List<Date> sortedDates = new LinkedList<>(dates);
        Collections.sort(sortedDates);

        // 4. Fordel permissions på datoer
        Map<Date, Set<String>> map = new HashMap<>();
        for (Date date : sortedDates) {
            for (Delegation d : list1) {
                if (!d.gyldig_fra.after(date) && d.gyldig_til.after(date)) {
                    Set<String> list = map.get(date);
                    if (list == null) {
                        list = new HashSet<>();
                        map.put(date, list);
                    }
                    list.add(d.gl_rettighed_kode);
                }
            }
        }

        // 5. Lav nye delegations
        List<Delegation> newDelegations = new LinkedList<>();

        Date oldDate = null;
        for (Date d : sortedDates) {
            if (oldDate != null) {
                if (daysInFuture(oldDate) <= settings.getMinimumFutureDays()) {
                    Delegation newDelegation = new Delegation(key);
                    Set<String> permissions = map.get(oldDate);
                    if (permissions != null && !permissions.isEmpty()) {
                        newDelegation.nye_rettighed_koder = new LinkedList<>(permissions);
                        newDelegation.sidst_modificeret = latestModifiedDate;
                        newDelegation.sidst_modificeret_af = "konvertering";
                        newDelegation.kode = UUID.randomUUID().toString();
                        newDelegation.linked_system_kode = linked_system_kode;
                        newDelegation.arbejdsfunktion_kode = arbejdsfunktion_kode;

                        // fix date interval
                        newDelegation.gyldig_fra = oldDate;
                        Date toDate = d;
                        if (d.after(settings.getConversionDate())) { // in future?
                            if (daysInFuture(d) > settings.getMaximumFutureDays()) {
                                newDelegation.sidst_modificeret = settings.getConversionDate();
                                toDate = calcSpecialToDate(settings.getConversionDate(), key.bemyndigede_cpr);
                                logger.debug("  Truncated toDate " + d + " to " + toDate);
                            }
                        }
                        newDelegation.gyldig_til = toDate;

                        newDelegations.add(newDelegation);
                        logger.info("  NEW " + newDelegation);
                    }
                } else
                    logger.debug("  Dropped creating delegations starting " + oldDate);
            }
            oldDate = d;
        }

        return newDelegations;
    }

    /**
     * beregn 2 år fra nu + dagen efter næste fødselsdato på bemyndigede
     *
     * @param bemyndigede_cpr cprnr på bemyndigede
     * @return justeret dato
     */
    private static Date calcSpecialToDate(Date concersionDate, String bemyndigede_cpr) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(concersionDate);
        cal.add(Calendar.YEAR, 2);

        long millis = cal.getTimeInMillis();

        int day = Integer.valueOf(bemyndigede_cpr.substring(0, 2));
        int month = Integer.valueOf(bemyndigede_cpr.substring(2, 4)) - 1;
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.add(Calendar.DAY_OF_YEAR, 1);

        if (cal.getTimeInMillis() < millis)
            cal.add(Calendar.YEAR, 1);

        return new Date(cal.getTimeInMillis());
    }

    private long daysInFuture(Date date) {
        long diff = date.getTime() - settings.getConversionDate().getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }


    /**
     * delete previously migrated delegations with the same key - allows rerun
     *
     * @param connection jdbc connection
     * @param key        key of delegations
     * @throws SQLException
     */
    private void clearPreviousNewDelegations(Connection connection, Key key) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs;
        try {

            String sql = "SELECT b.id FROM bemyndigelse20 b" +
                    " WHERE b.linked_system_kode = (select s.kode from linked_system s where s.id = ?)" +
                    " AND b.arbejdsfunktion_kode = (select a.kode from arbejdsfunktion a where a.id = ?)" +
                    " AND b.bemyndigende_cpr = ? AND b.bemyndigede_cpr = ? AND b.status = ?" +
                    " AND bemyndigede_cvr " + (key.bemyndigede_cvr == null ? "IS NULL" : "= ?");
            stmt = connection.prepareStatement(sql);
            stmt.setLong(1, key.linked_system_id);
            stmt.setLong(2, key.arbejdsfunktion_id);
            stmt.setString(3, key.bemyndigende_cpr);
            stmt.setString(4, key.bemyndigede_cpr);
            stmt.setString(5, key.status);
            if (key.bemyndigede_cvr != null)
                stmt.setString(6, key.bemyndigede_cvr);

            StringBuilder ids = new StringBuilder();
            rs = stmt.executeQuery();
            while (rs.next()) {
                if (ids.length() > 0)
                    ids.append(",");
                ids.append(rs.getLong(1));
            }
            rs.close();
            stmt.close();
            stmt = null;

            if (ids.length() > 0) {
                stmt = connection.prepareStatement("DELETE FROM bemyndigelse20_rettighed WHERE bemyndigelse20_id IN (" + ids + ")");
                stmt.executeUpdate();
                stmt.close();
                stmt = null;

                stmt = connection.prepareStatement("DELETE FROM bemyndigelse20 WHERE id IN (" + ids + ")");
                stmt.executeUpdate();
            }
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (Exception ignored) {
            }
        }
    }

    private void saveNewDelegations(Connection connection, Key key, List<Delegation> delegations) throws SQLException {
        PreparedStatement stmt = null;

        try {
            for (Delegation d : delegations) {
                stmt = connection.prepareStatement("INSERT INTO bemyndigelse.bemyndigelse20 (linked_system_kode, arbejdsfunktion_kode, bemyndigende_cpr, bemyndigede_cpr, bemyndigede_cvr, status, kode, godkendelsesdato, gyldig_fra, gyldig_til, versionsid, sidst_modificeret, sidst_modificeret_af)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

                stmt.setString(1, d.linked_system_kode);
                stmt.setString(2, d.arbejdsfunktion_kode);
                stmt.setString(3, key.bemyndigende_cpr);
                stmt.setString(4, key.bemyndigede_cpr);
                stmt.setString(5, key.bemyndigede_cvr);
                stmt.setString(6, key.status);
                stmt.setString(7, d.kode);
                stmt.setDate(8, d.godkendelsesdato);
                stmt.setDate(9, d.gyldig_fra);
                stmt.setDate(10, d.gyldig_til);
                stmt.setLong(11, 1L);
                stmt.setDate(12, d.sidst_modificeret);
                stmt.setString(13, d.sidst_modificeret_af);

                stmt.executeUpdate();

                Long id;
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getLong(1);
                } else {
                    throw new SQLException("Creating delegation failed, no ID obtained.");
                }

                stmt.close();
                stmt = null;

                for (String permission : d.nye_rettighed_koder) {
                    stmt = connection.prepareStatement("INSERT INTO bemyndigelse.bemyndigelse20_rettighed (bemyndigelse20_id, rettighed_kode, sidst_modificeret, sidst_modificeret_af)" +
                            " VALUES (?, ?, ?, ?)");

                    stmt.setLong(1, id);
                    stmt.setString(2, permission);
                    stmt.setDate(3, d.sidst_modificeret);
                    stmt.setString(4, d.sidst_modificeret_af);

                    stmt.executeUpdate();
                    stmt.close();
                    stmt = null;
                }
            }
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (Exception ignored) {
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------------

    private Connection getConnection() throws SQLException {
        Connection con;
        con = DriverManager.getConnection(settings.getJdbcUrl(), settings.getJdbcUsername(), settings.getJdbcPassword());
        return con;
    }

    private class Key {
        long linked_system_id;
        long arbejdsfunktion_id;
        String bemyndigende_cpr;
        String bemyndigede_cpr;
        String bemyndigede_cvr;
        String status;

        public Key(long linked_system_id, long arbejdsfunktion_id, String bemyndigende_cpr, String bemyndigede_cpr, String bemyndigede_cvr, String status) {
            this.linked_system_id = linked_system_id;
            this.arbejdsfunktion_id = arbejdsfunktion_id;
            this.bemyndigende_cpr = bemyndigende_cpr;
            this.bemyndigede_cpr = bemyndigede_cpr;
            this.bemyndigede_cvr = bemyndigede_cvr;
            this.status = status;
        }

        public Key(String line) {
            String[] items = line.split(";");
            this.linked_system_id = Long.valueOf(items[0]);
            this.arbejdsfunktion_id = Long.valueOf(items[1]);
            this.bemyndigende_cpr = items[2];
            this.bemyndigede_cpr = items[3];
            this.bemyndigede_cvr = (items[4] != null && !items[4].isEmpty()) ? items[4] : null;
            this.status = items[5];
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (linked_system_id != key.linked_system_id) return false;
            if (arbejdsfunktion_id != key.arbejdsfunktion_id) return false;
            if (!bemyndigende_cpr.equals(key.bemyndigende_cpr)) return false;
            if (!bemyndigede_cpr.equals(key.bemyndigede_cpr)) return false;
            if (bemyndigede_cvr != null ? !bemyndigede_cvr.equals(key.bemyndigede_cvr) : key.bemyndigede_cvr != null)
                return false;
            return status.equals(key.status);

        }

        @Override
        public int hashCode() {
            int result = (int) (linked_system_id ^ (linked_system_id >>> 32));
            result = 31 * result + (int) (arbejdsfunktion_id ^ (arbejdsfunktion_id >>> 32));
            result = 31 * result + bemyndigende_cpr.hashCode();
            result = 31 * result + bemyndigede_cpr.hashCode();
            result = 31 * result + (bemyndigede_cvr != null ? bemyndigede_cvr.hashCode() : 0);
            result = 31 * result + status.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return linked_system_id + ";" + arbejdsfunktion_id + ";" + bemyndigende_cpr + ";" + bemyndigede_cpr + ";" + (bemyndigede_cvr != null ? bemyndigede_cvr : "") + ";" + status;
        }
    }


    private class Delegation {
        Key key;
        String kode;
        Date godkendelsesdato;
        Date gyldig_fra;
        Date gyldig_til;
        Date sidst_modificeret;
        String sidst_modificeret_af;
        String gl_rettighed_kode;
        List<String> nye_rettighed_koder;
        String arbejdsfunktion_kode;
        String linked_system_kode;

        public Delegation(Key key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return "Delegation{" +
                    "gyldig_fra=" + gyldig_fra +
                    ", gyldig_til=" + gyldig_til +
                    (gl_rettighed_kode != null ? ", gl_rettighed_kode='" + gl_rettighed_kode + '\'' : "") +
                    (nye_rettighed_koder != null && !nye_rettighed_koder.isEmpty() ? ", nye_rettighed_koder='" + nye_rettighed_koder + '\'' : "") +
                    '}';
        }
    }
}
