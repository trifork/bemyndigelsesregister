package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;

@Entity
public class StatusType extends DomainObject {

    private String status;

    public StatusType() {
    }

    //<editor-fold desc="GettersAndSetters">
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    //</editor-fold>
}
