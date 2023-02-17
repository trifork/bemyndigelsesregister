package dk.bemyndigelsesregister.domain;

import java.time.Instant;

import java.util.HashSet;
import java.util.Set;

/**
 * BEM 2.0 bemyndigelse
 */
public class Delegation extends ExternalIdentifiedDomainObject {
    protected String delegatorCpr;
    protected String delegateeCpr;
    protected String delegateeCvr;
    protected String systemCode;
    protected String roleCode;
    protected Status state;
    protected Set<DelegationPermission> delegationPermissions;
    protected Instant created;
    protected Instant effectiveFrom;
    protected Instant effectiveTo;

    private int versionsid;

    public Delegation() {
    }

    public String getDelegatorCpr() {
        return delegatorCpr;
    }

    public void setDelegatorCpr(String value) {
        this.delegatorCpr = value;
    }

    public String getDelegateeCpr() {
        return delegateeCpr;
    }

    public void setDelegateeCpr(String value) {
        this.delegateeCpr = value;
    }

    public String getDelegateeCvr() {
        return delegateeCvr;
    }

    public void setDelegateeCvr(String value) {
        this.delegateeCvr = value;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public Status getState() {
        return state;
    }

    public void setState(Status value) {
        this.state = value;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant value) {
        this.created = value;
    }

    public Instant getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(Instant value) {
        this.effectiveFrom = value;
    }

    public Instant getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(Instant value) {
        this.effectiveTo = value;
    }

    public int getVersionsid() {
        return versionsid;
    }

    public void setVersionsid(int versionsid) {
        this.versionsid = versionsid;
    }

    public Set<DelegationPermission> getDelegationPermissions() {
        if (delegationPermissions == null) {
            delegationPermissions = new HashSet<>();
        }
        return delegationPermissions;
    }

    public void setDelegationPermissions(Set<DelegationPermission> delegationPermissions) {
        this.delegationPermissions = delegationPermissions;
    }

    public boolean hasAsteriskPermission() {
        if (delegationPermissions != null) {
            for (DelegationPermission delegationPermission : delegationPermissions) {
                if (delegationPermission.getPermissionCode().equals(Metadata.ASTERISK_PERMISSION_CODE))
                    return true;
            }
        }
        return false;
    }
}
