package dk.bemyndigelsesregister.dao;

import dk.bemyndigelsesregister.domain.Delegation;
import dk.bemyndigelsesregister.domain.ExpirationInfo;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

/**
 * BEM 2.0 bemyndigelse
 */
public interface DelegationDAO {
    Delegation get(long id);

    void save(Delegation delegation);

    List<Delegation> list();

    Delegation findByCode(String code);

    List<Delegation> findByDelegatorCpr(String delegatorCpr, Instant effectiveFrom, Instant effectiveTo);

    List<Delegation> findByDelegateeCpr(String delegateeCpr, Instant effectiveFrom, Instant effectiveTo);

    List<Delegation> findInPeriod(String system, String delegatorCpr, String delegateeCpr, String delegateeCvr, String role, Instant effectiveFrom, Instant effectiveTo);

    List<Delegation> findByCodes(Collection<String> codes);

    List<Long> findByModifiedInPeriod(Instant fromIncluding, Instant toExcluding, List<String> skipList);

    /**
     * findWithAsterisk finds delegations for a system containing asterisk permission
     *
     * @param systemCode system to find delegations for
     * @param validDate  date that the delegations should be valid on or after
     * @return list of delegation ids
     */
    List<Long> findWithAsterisk(String systemCode, Instant validDate);

    List<Long> findExpiredBefore(Instant date, int maxRecords);

    ExpirationInfo getExpirationInfo(String cpr, int days);

    void remove(long id);
}
