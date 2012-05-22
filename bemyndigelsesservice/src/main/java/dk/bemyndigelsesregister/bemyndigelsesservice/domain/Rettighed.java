package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;

@Entity
public class Rettighed extends ExternalIdentifiedDomainObject {
    private Domaene domaene;
    private LinkedSystem linkedSystem;
    private String beskrivelse;

    public Rettighed() {
    }

    //<editor-fold desc="GettersAndSetters">

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public void setLinkedSystem(LinkedSystem linkedSystem) {
        this.linkedSystem = linkedSystem;
    }

    public LinkedSystem getLinkedSystem() {
        return linkedSystem;
    }

    public void setDomaene(Domaene domaene) {
        this.domaene = domaene;
    }

    public Domaene getDomaene() {
        return domaene;
    }

    //</editor-fold>

    public static Rettighed createForTest(final String kode) {
        return new Rettighed() {{
            setKode(kode);
        }};
    }
}
