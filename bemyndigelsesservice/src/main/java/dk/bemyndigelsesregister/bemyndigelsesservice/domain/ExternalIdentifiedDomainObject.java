package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ExternalIdentifiedDomainObject extends DomainObject {
    private String kode;
    private String uuid;

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String id) {
        this.uuid = id;
    }
}
