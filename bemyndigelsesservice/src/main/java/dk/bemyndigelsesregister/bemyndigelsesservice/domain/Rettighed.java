package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

public class Rettighed extends DomainObject {
    private String domaene;
    private String system;
    private String rettighedskode;
    private String beskrivelse;

    protected Rettighed(Long id) {
        super(id);
    }
}
