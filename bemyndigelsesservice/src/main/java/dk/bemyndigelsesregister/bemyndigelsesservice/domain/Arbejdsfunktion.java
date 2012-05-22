package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class Arbejdsfunktion extends ExternalIdentifiedDomainObject {
    @OneToOne
    private Domaene domaene;
    @OneToOne
    private LinkedSystem LinkedSystem;
    private String beskrivelse;

    public Arbejdsfunktion() {
    }

    //<editor-fold desc="GettersAndSetters">
    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public void setDomaene(Domaene domaene) {
        this.domaene = domaene;
    }

    public Domaene getDomaene() {
        return domaene;
    }

    public void setLinkedSystem(LinkedSystem linkedSystem) {
        this.LinkedSystem = linkedSystem;
    }

    public LinkedSystem getLinkedSystem() {
        return LinkedSystem;
    }
    //</editor-fold>

    public static Arbejdsfunktion createForTest(final String kode) {
        return new Arbejdsfunktion() {{
            setKode(kode);
        }};
    }
}
