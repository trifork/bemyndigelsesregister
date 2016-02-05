package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="domane")
public class Domain extends ExternalIdentifiedDomainObject {
    public Domain() {
    }
}
