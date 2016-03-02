package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.nsi.bemyndigelse._2016._01._01.State;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.List;

/**
 * BEM 2.0 bemyndigelse
 * Created by obj on 02-02-2016.
 */
public interface DelegationDao {
    Delegation get(long id);

    void save(Delegation delegation);

    List<Delegation> list();

    List<Delegation> findByDelegatorCpr(String delegatorCpr);

    List<Delegation> findByDelegateeCpr(String delegateeCpr);

    Delegation findById(String delegationId);

    List<Delegation> findByInPeriod(String system, String delegatorCpr, String delegateeCpr, String delegateeCvr, String role, State state, DateTime effectiveFrom, DateTime effectiveTo);

    List<Delegation> findByIds(Collection<String> delegationIds);

    List<Delegation> findByDomainIds(Collection<String> domainIds);

    List<Delegation> findByLastModifiedGreaterThanOrEquals(DateTime lastModified);
}
