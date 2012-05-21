package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

public class DelegerbarRettighed extends DomainObject {
    private Arbejdsfunktion arbejdsfunktion;
    private Domaene domaene;
    private String kode;
    private LinkedSystem system;

    public Arbejdsfunktion getArbejdsfunktion() {
        return arbejdsfunktion;
    }

    public void setArbejdsfunktion(Arbejdsfunktion arbejdsfunktion) {
        this.arbejdsfunktion = arbejdsfunktion;
    }

    public Domaene getDomaene() {
        return domaene;
    }

    public void setDomaene(Domaene domaene) {
        this.domaene = domaene;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public LinkedSystem getSystem() {
        return system;
    }

    public void setSystem(LinkedSystem system) {
        this.system = system;
    }
}
