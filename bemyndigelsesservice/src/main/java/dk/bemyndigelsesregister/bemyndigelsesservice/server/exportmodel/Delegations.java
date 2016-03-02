package dk.bemyndigelsesregister.bemyndigelsesservice.server.exportmodel;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegationPermission;

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;

/**
 * RootElement for data exported by ExportJob
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Bemyndigelser", namespace = "http://nsi.dk/bemyndigelser/2012/04/")
public class Delegations {
    @XmlElement(name = "Bemyndigelse")
    private List<Delegation> delegations;

    @XmlAttribute(name = "Dato")
    private String date;

    @XmlAttribute(name = "TimeStamp")
    private String timeStamp;

    @XmlAttribute(name = "Version")
    private String version;

    @XmlAttribute(name = "AntalPost")
    @XmlSchemaType(name = "positiveInteger")
    private int recordCount;

    public void addDelegation(dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation delegation) {
        if (delegation.getDelegationPermissions() != null && !delegation.getDelegationPermissions().isEmpty()) {
            if (delegations == null)
                delegations = new LinkedList<>();

            for (DelegationPermission permission : delegation.getDelegationPermissions())
                delegations.add(new Delegation(delegation.getDelegateeCpr(), delegation.getSystemCode(), delegation.getState().value(), delegation.getRoleCode(), permission.getPermissionCode(), delegation.getCreated(), delegation.getLastModified(), delegation.getEffectiveFrom(), delegation.getEffectiveTo()));

            recordCount = delegations.size();
        }
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
