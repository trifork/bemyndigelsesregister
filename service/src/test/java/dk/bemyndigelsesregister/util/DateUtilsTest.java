package dk.bemyndigelsesregister.util;

import org.junit.jupiter.api.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateUtilsTest {
    @Test
    public void testXmlGregorianCalendarConversion() {
        Instant original = Instant.now();

        XMLGregorianCalendar cal = DateUtils.toXmlGregorianCalendar(original);
        Instant backAgain = DateUtils.toInstant(cal);

        // it's not possible to compare directly, since some precision is lost
        String format = "yyyy-MM-dd HH:mm:ss.SSS";
        assertEquals(DateUtils.format(original, format), DateUtils.format(backAgain, format));
    }

    @Test
    public void testPlusDays() {
        Instant original = Instant.now();

        Instant plus2Days = DateUtils.plusDays(original, 2);
        assertEquals(2, DateUtils.daysBetween(original, plus2Days));
    }
}
