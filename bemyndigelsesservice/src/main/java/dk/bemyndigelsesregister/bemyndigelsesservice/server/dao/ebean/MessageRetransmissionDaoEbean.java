package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.MessageRetransmission;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.MessageRetransmissionDao;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MessageRetransmissionDaoEbean extends SupportDao<MessageRetransmission> implements MessageRetransmissionDao {
    private final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public MessageRetransmissionDaoEbean() {
        super(MessageRetransmission.class);
    }

    @Override
    public MessageRetransmission getByMessageIDAndImplementationBuild(String messageID, String implementationBuild) {
        return query().where().eq("message_id", messageID).eq("implementation_build", implementationBuild).findUnique();
    }

    @Override
    @Transactional
    public int cleanup(DateTime beforeDate, int maxRecords) {
        String sql = "delete from message_retransmission where sidst_modificeret is null or sidst_modificeret < '" + beforeDate.toString(DATETIME_FORMATTER) + "'";
        if (maxRecords > 0) {
            sql += " limit " + maxRecords;
        }
        return ebeanServer.createUpdate(MessageRetransmission.class, sql).execute();
    }
}
