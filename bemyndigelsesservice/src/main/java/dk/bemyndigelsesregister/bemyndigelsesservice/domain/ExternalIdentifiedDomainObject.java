package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ExternalIdentifiedDomainObject extends DomainObject {
    @Column(name = "kode")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String id) {
        this.code = id;
    }
}
