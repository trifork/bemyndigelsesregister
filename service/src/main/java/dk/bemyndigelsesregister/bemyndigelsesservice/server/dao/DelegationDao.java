package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.ExpirationInfo;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Status;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.List;

/**
 * BEM 2.0 bemyndigelse
 */
public interface DelegationDao {
    Delegation get(long id);

    void save(Delegation delegation);

    List<Delegation> list();

    Delegation findByCode(String code);

    List<Delegation> findByDelegatorCpr(String delegatorCpr, DateTime effectiveFrom, DateTime effectiveTo);

    List<Delegation> findByDelegateeCpr(String delegateeCpr, DateTime effectiveFrom, DateTime effectiveTo);

    List<Delegation> findInPeriod(String system, String delegatorCpr, String delegateeCpr, String delegateeCvr, String role, Status state, DateTime effectiveFrom, DateTime effectiveTo);

    List<Delegation> findByCodes(Collection<String> codes);

    List<Long> findByModifiedInPeriod(DateTime fromIncluding, DateTime toExcluding);

    /**
     * findWithAsterisk finds delegations for a system containing asterisk permission
     *
     * @param systemCode system to find delegations for
     * @param validDate  date that the delegations should be valid on or after
     * @return list of delegation ids
     */
    List<Long> findWithAsterisk(String systemCode, DateTime validDate);

    ExpirationInfo getExpirationInfo(String cpr, int days);

    int cleanup(DateTime beforeDate, int maxRecords);
}
