package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ExternalIdentifiedDomainObject extends DomainObject {
    private String kode;

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }
}
