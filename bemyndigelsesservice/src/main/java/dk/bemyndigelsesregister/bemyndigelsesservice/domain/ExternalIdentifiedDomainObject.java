package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ExternalIdentifiedDomainObject extends DomainObject {
    private String kode;
    private String id;

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
