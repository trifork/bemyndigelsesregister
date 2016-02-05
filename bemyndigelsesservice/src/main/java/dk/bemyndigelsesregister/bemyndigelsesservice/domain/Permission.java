package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="rettighed")
public class Permission extends ExternalIdentifiedDomainObject {
    @ManyToOne
    @Column(name="linkedSystem")
    private DelegatingSystem delegatingSystem;

    @Column(name="beskrivelse")
    private String description;

    public Permission() {
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

    public static Permission createForTest(final String domainId) {
        return new Permission() {{
            setDomainId(domainId);
        }};
    }
}
