package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;

@Entity
public class LinkedSystem extends ExternalIdentifiedDomainObject {
    public LinkedSystem() {
    }

    public static LinkedSystem createForTest(final String kode) {
        return new LinkedSystem() {{
            setKode(kode);
        }};
    }
}
