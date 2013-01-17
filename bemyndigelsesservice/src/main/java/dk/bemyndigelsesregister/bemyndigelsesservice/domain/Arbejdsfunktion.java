package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Arbejdsfunktion extends ExternalIdentifiedDomainObject {
    @ManyToOne
    private LinkedSystem linkedSystem;
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

    public void setLinkedSystem(LinkedSystem linkedSystem) {
        this.linkedSystem = linkedSystem;
    }

    public LinkedSystem getLinkedSystem() {
        return linkedSystem;
    }
    //</editor-fold>

    public static Arbejdsfunktion createForTest(final String kode) {
        return new Arbejdsfunktion() {{
            setKode(kode);
        }};
    }
}
