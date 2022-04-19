package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import java.util.List;

import org.springframework.stereotype.Repository;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Whitelist;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.WhitelistType;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.WhitelistDao;

@Repository
public class WhitelistDaoEbean extends SupportDao<Whitelist>implements WhitelistDao {

	protected WhitelistDaoEbean() {
		super(Whitelist.class);
	}

	@Override
	public List<Whitelist> get(String name, WhitelistType type, String subjectId) {
        return query().where().eq("name", name).eq("whitelistType", type).eq("subjectId", subjectId).findList();
	}

	@Override
	public boolean exists(String name, WhitelistType type, String subjectId) {
		return !get(name, type, subjectId).isEmpty();
	}
}
