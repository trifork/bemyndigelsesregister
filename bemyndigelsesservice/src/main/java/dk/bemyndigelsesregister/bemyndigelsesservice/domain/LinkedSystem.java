package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;

@Entity
public class LinkedSystem extends DomainObject {
    private String system;

    public LinkedSystem() {
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getSystem() {
        return system;
    }

}
