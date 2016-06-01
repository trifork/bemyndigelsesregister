package dk.bemyndigelsesregister.bemyndigelsesservice.server.exportmodel;

import org.joda.time.DateTime;

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

    public void addDelegation(String uuid, String delegatorCpr, String delegateeCpr, String delegateeCvr, String system, String status, String role, String permission, DateTime approvalDate, DateTime modifiedDate, DateTime effectiveFrom, DateTime effectiveTo) {
        if (delegations == null)
            delegations = new LinkedList<>();

        delegations.add(new Delegation(uuid, delegatorCpr, delegateeCpr, delegateeCvr, system, status, role, permission, approvalDate, modifiedDate, effectiveFrom, effectiveTo));
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
