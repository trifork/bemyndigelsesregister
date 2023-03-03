package dk.bemyndigelsesregister.util;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateUtilsTest {
    @Test
    public void testPlusDays() {
        Instant original = Instant.now();

        Instant plus2Days = DateUtils.plusDays(original, 2);
        assertEquals(2, DateUtils.daysBetween(original, plus2Days));
    }
}
