package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

public abstract class DomainObject {
    private Long id;

    protected DomainObject(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
