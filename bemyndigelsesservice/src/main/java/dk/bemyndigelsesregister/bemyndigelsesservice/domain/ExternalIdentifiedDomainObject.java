package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ExternalIdentifiedDomainObject extends DomainObject {
    private String kode;
    @Column(name = "kode")
    private String domainId;

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String id) {
        this.domainId = id;
    }
}
