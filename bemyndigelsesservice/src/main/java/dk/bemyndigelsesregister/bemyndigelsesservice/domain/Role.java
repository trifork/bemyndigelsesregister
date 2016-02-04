package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Role extends ExternalIdentifiedDomainObject {
    @ManyToOne
    private DelegatingSystem delegatingSystem;
    private String description;

    public Role() {
    }

    //<editor-fold desc="GettersAndSetters">
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDelegatingSystem(DelegatingSystem delegatingSystem) {
        this.delegatingSystem = delegatingSystem;
    }

    public DelegatingSystem getDelegatingSystem() {
        return delegatingSystem;
    }
    //</editor-fold>

    public static Role createForTest(final String uuid) {
        return new Role() {{
            setUUID(uuid);
        }};
    }




}