package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import org.joda.time.DateTime;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class DomainObject {
    @Id
    private Long id;
    protected DateTime sidstModificeret;
    protected String sidstModificeretAf;

    //<editor-fold desc="GettersAndSetters">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
