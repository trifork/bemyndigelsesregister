package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.WhitelistType;

public class WhitelistDaoEbeanTest extends DaoUnitTestSupport {
	@Test
	public void canFindExisting() {
		assertTrue(whitelistDao.exists("test", WhitelistType.SYSTEM_CVR, "1"));
		assertFalse(whitelistDao.exists("test2", WhitelistType.SYSTEM_CVR, "1"));
		assertFalse(whitelistDao.exists("test", WhitelistType.USER_CVR_CPR, "1"));
	}
}