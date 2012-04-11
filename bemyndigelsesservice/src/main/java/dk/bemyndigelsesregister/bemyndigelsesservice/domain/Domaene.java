package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;

@Entity
public class Domaene extends DomainObject {
    private String domaene;

    public Domaene() {
    }

    public String getDomaene() {
        return domaene;
    }

    public void setDomaene(String domaene) {
        this.domaene = domaene;
    }

}
