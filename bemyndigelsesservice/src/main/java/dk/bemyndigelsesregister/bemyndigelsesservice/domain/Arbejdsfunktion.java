package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

public class Arbejdsfunktion extends DomainObject {
    private String domaene;
    private String system;
    private String arbejdsfunktion;
    private String beskrivelse;

    public Arbejdsfunktion(Long id) {
        super(id);
    }
}
