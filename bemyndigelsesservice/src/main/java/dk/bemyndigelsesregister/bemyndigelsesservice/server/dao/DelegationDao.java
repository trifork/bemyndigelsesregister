package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;

import java.util.Collection;
import java.util.List;

/**
 * BEM 2.0 bemyndigelse
 * Created by obj on 02-02-2016.
 */
public interface DelegationDao {
    Delegation get(long id);

    void save(Delegation bemyndigelse);

    List<Delegation> list();

    List<Delegation> findByDelegatorCpr(String delegatorCpr);

    List<Delegation> findByDelegateeCpr(String delegateeCpr);

    List<Delegation> findByDomainIds(Collection<String> domainIds);
}
