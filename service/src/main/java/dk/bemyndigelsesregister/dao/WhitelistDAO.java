package dk.bemyndigelsesregister.dao;

import dk.bemyndigelsesregister.domain.Whitelist;
import dk.bemyndigelsesregister.domain.WhitelistType;

import java.util.List;

public interface WhitelistDAO {
	List<Whitelist> get(String name, WhitelistType type, String subjectId);
	boolean exists(String name, WhitelistType type, String subjectId);
}
