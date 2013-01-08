package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import javax.inject.Inject;

import org.springframework.stereotype.Repository;

import com.trifork.dgws.WhitelistChecker;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.WhitelistType;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.WhitelistDao;

@Repository
public class WhitelistCheckerDefault implements WhitelistChecker {
	
    @Inject
    WhitelistDao whitelistDao;

	@Override
	public boolean isSystemWhitelisted(String whitelist, String cvr) {
		return whitelistDao.exists(whitelist, WhitelistType.SYSTEM_CVR, cvr);
	}

	@Override
	public boolean isUserWhitelisted(String whitelist, String cvr, String cpr) {
		return whitelistDao.exists(whitelist, WhitelistType.USER_CVR_CPR, "CVR:"+cvr+"-CPR:"+cpr);
	}
}
