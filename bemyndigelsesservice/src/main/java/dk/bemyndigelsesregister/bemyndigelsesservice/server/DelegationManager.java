package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import org.joda.time.DateTime;

import java.util.List;

/**
 * BEM 2.0 bemyndigelse
 * Created by obj on 02-02-2016.
 */
public interface DelegationManager {

    Delegation createDelegation(String delegatorCpr, String delegateeCpr, String delegateeCvr, String roleId, String state, String systemId, List<String> permissionIds, DateTime effectiveFrom, DateTime effectiveTo);

}
