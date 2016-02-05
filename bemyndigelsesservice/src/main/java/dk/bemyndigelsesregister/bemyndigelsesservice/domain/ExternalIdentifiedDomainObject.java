package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ExternalIdentifiedDomainObject extends DomainObject {
    @Column(name = "kode")
    private String domainId;

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String id) {
        this.domainId = id;
    }
}
