package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import org.joda.time.DateTime;

public abstract class DomainObject {
    private Long id;
    protected DateTime sidstModificeret;
    protected String sidstModificeretAf;

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
