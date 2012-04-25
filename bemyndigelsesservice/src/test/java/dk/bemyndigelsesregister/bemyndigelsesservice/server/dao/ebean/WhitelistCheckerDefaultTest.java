package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import org.junit.Test;

import javax.inject.Inject;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

public class WhitelistCheckerDefaultTest extends DaoUnitTestSupport {
    @Inject
    WhitelistCheckerDefault whitelistChecker;

    @Test
    public void canFetchTestDataCvrNumbers() throws Exception {
        Set<String> testWhitelist = whitelistChecker.getLegalCvrNumbers("test");
        assertEquals(2, testWhitelist.size());
        assertThat(testWhitelist, hasItems("1", "2"));
    }
}
