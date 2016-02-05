package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.MappedSuperclass;

/**
 * Created by obj on 05-02-2016.
 */
@MappedSuperclass
public class ExternalIdentifiedDomainObject10  extends DomainObject {
    private String kode;

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }
}
