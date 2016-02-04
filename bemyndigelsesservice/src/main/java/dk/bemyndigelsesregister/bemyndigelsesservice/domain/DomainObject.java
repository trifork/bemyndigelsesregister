package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import org.joda.time.DateTime;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class DomainObject {
    @Id
    private Long internalId;
    protected DateTime sidstModificeret;
    protected String sidstModificeretAf;

    //<editor-fold desc="GettersAndSetters">
    public Long getInternalId() {
        return internalId;
    }

    public void setInternalId(Long internalId) {
        this.internalId = internalId;
    }

    public DateTime getSidstModificeret() {
        return sidstModificeret;
    }

    public void setSidstModificeret(DateTime sidstModificeret) {
        this.sidstModificeret = sidstModificeret;
    }

    public String getSidstModificeretAf() {
        return sidstModificeretAf;
    }

    public void setSidstModificeretAf(String sidstModificeretAf) {
        this.sidstModificeretAf = sidstModificeretAf;
    }
    //</editor-fold>
}
