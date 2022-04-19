package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import java.util.List;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Whitelist;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.WhitelistType;

public interface WhitelistDao {
	List<Whitelist> get(String name, WhitelistType type, String subjectId);
	boolean exists(String name, WhitelistType type, String subjectId);
}
