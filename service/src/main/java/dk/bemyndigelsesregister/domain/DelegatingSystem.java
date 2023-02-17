package dk.bemyndigelsesregister.domain;

public class DelegatingSystem extends ExternalIdentifiedDomainObject {
    private String description;

    private Domain domain;

    public DelegatingSystem() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }
}
