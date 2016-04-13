package dk.bemyndigelsesregister;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * Created by obj on 07-04-2016.
 */
public class MigrationSettings {

    private String jdbcDriver;
    private String jdbcUrl;
    private String jdbcUsername;
    private String jdbcPassword;

    private Date changedSinceDate;
    private Date conversionDate;

    private int skipKeys;
    private int minimumFutureDays;
    private int maximumFutureDays;

    private String filename;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public MigrationSettings() throws IOException {
        InputStream in = null;
        try {
            Properties properties = new Properties();
            in = new FileInputStream("migration.properties");
            properties.load(in);
            in.close();

            jdbcDriver = properties.getProperty("jdbc.driver");
            jdbcUrl = properties.getProperty("jdbc.url");
            jdbcUsername = properties.getProperty("jdbc.username");
            jdbcPassword = properties.getProperty("jdbc.password");
            changedSinceDate = getPropertyAsDate(properties, "changedSinceDate");
            conversionDate = getPropertyAsDate(properties, "conversionDate");
            if (conversionDate == null)
                conversionDate = new Date(System.currentTimeMillis());
            skipKeys = getPropertyAsInt(properties, "skipKeys");
            minimumFutureDays = getPropertyAsInt(properties, "minimumFutureDays");
            maximumFutureDays = getPropertyAsInt(properties, "maximumFutureDays");
            filename = "bem_migration_" + properties.getProperty("changedSinceDate") + ".csv";
        } finally {
            if (in != null)
                in.close();
        }
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getJdbcUsername() {
        return jdbcUsername;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public Date getChangedSinceDate() {
        return changedSinceDate;
    }

    public Date getConversionDate() {
        return conversionDate;
    }

    public int getSkipKeys() {
        return skipKeys;
    }

    public int getMinimumFutureDays() {
        return minimumFutureDays;
    }

    public int getMaximumFutureDays() {
        return maximumFutureDays;
    }

    public String getFilename() {
        return filename;
    }

    private Date getPropertyAsDate(Properties properties, String property) {
        String value = properties.getProperty(property);
        try {
            return new Date(dateFormat.parse(value).getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    private int getPropertyAsInt(Properties properties, String property) {
        String value = properties.getProperty(property);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Settings:\n" +
                "jdbcDriver=" + jdbcDriver + '\n' +
                "jdbcUrl=" + jdbcUrl + '\n' +
                "jdbcUsername=" + jdbcUsername + '\n' +
                "jdbcPassword=" + jdbcPassword + '\n' +
                "changedSinceDate=" + changedSinceDate + '\n' +
                "conversionDate=" + conversionDate + '\n' +
                "skipKeys=" + skipKeys + '\n' +
                "minimumFutureDays=" + minimumFutureDays + "\n" +
                "maximumFutureDays=" + maximumFutureDays;
    }
}
