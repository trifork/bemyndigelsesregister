package dk.bemyndigelsesregister.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class InstantAdapter extends XmlAdapter<String, Instant> {
    private static final ZoneId timeZone = ZoneOffset.UTC;
    private static final DateTimeFormatter utcFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(timeZone);

    public Instant unmarshal(String s) throws Exception {
        if (s == null) {
            return null;
        }

        if (s.endsWith("Z")) {
            return LocalDateTime.parse(s, utcFormatter).atZone(timeZone).toInstant();
        }

        return LocalDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME).atZone(ZoneId.systemDefault()).toInstant();
    }

    public String marshal(Instant instant) throws Exception {
        if (instant == null) {
            return null;
        }
        return utcFormatter.format(instant);
    }
}
