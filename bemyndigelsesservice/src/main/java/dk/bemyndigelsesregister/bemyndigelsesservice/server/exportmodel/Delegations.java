package dk.bemyndigelsesregister.bemyndigelsesservice.server.exportmodel;

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

    public void addDelegation(dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation delegation, Set<String> permissionCodes) {
        if (delegations == null)
            delegations = new LinkedList<>();

        int n = 0;
        for (String permissionCode : permissionCodes) {
            String uuid = String.format("%s-%03d", delegation.getCode(), ++n); // create a unique id for each exported "Bemyndigelse" by concatenating delegation uuid and an incremental number
            delegations.add(new Delegation(uuid, delegation.getDelegatorCpr(), delegation.getDelegateeCpr(), delegation.getDelegateeCvr(), delegation.getSystemCode(), delegation.getState().value(), delegation.getRoleCode(), permissionCode, delegation.getCreated(), delegation.getLastModified(), delegation.getEffectiveFrom(), delegation.getEffectiveTo()));
        }

        recordCount = delegations.size();
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

    public int getRecordCount() {
        return recordCount;
    }
}
