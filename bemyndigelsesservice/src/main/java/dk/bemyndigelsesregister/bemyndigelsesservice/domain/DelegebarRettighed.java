package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

public class DelegebarRettighed extends DomainObject {
    private String domaene;
    private String system;
    private Arbejdsfunktion arbejdsfunktion;
    private Rettighed Rettighedskode;

    public DelegebarRettighed(Long id) {
        super(id);
    }
}
