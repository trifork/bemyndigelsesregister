package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

public class Authorization extends DomainObject {
    private String name;

    public Authorization(Long id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
