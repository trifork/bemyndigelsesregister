package dk.bemyndigelsesregister.service;

import dk.bemyndigelsesregister.dao.DelegationDAO;
import dk.bemyndigelsesregister.dao.DelegationPermissionDAO;
import dk.bemyndigelsesregister.domain.Delegation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class DumpRestoreManagerImpl implements DumpRestoreManager {
    private static final Logger log = LoggerFactory.getLogger(DumpRestoreManagerImpl.class);

    @Autowired
    private DelegationDAO delegationDAO;

    @Autowired
    private DelegationPermissionDAO delegationPermissionDAO;

    @Override
    public List<String> resetPatients(List<String> identifiers) {
        List<String> result = new LinkedList<>();

        for (String identifier : identifiers) {
            log.info("Resetting person with identifier: {}", identifier);

            List<Delegation> delegations = delegationDAO.findByDelegatorCpr(identifier, null, null);
            if (delegations != null) {
                int deleteCount = 0;

                for (Delegation delegation : delegations) {
                    log.info("  Removing {} delegation to delegatee {} with id {}", delegation.getSystemCode(), delegation.getDelegateeCpr(), delegation.getId());

                    delegationPermissionDAO.removeByDelegationId(delegation.getId());
                    delegationDAO.remove(delegation.getId());

                    deleteCount++;
                }

                if (deleteCount > 0) {
                    result.add(identifier);
                }
            }
        }

        return result;
    }
}
