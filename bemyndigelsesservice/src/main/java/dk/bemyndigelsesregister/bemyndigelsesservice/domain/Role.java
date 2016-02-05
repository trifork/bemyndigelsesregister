package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="arbejdsfunktion")
public class Role extends ExternalIdentifiedDomainObject {
    @ManyToOne
    @Column(name = "linked_system_id")
    private DelegatingSystem delegatingSystem;
    @Column(name = "beskrivelse")
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

    public static Role createForTest(final String domainId) {
        return new Role() {{
            setDomainId(domainId);
        }};
    }




}