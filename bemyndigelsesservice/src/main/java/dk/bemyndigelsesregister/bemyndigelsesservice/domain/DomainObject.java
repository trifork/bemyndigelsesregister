package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import org.joda.time.DateTime;

public abstract class DomainObject {
    private Long id;
    protected DateTime sidstModificeret;
    protected String sidstModificeretAf;

    protected DomainObject(Long id) {
        this.id = id;
    }

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
