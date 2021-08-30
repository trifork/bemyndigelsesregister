package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "domaene")
public class Domain extends ExternalIdentifiedDomainObject {
    public Domain() {
    }
}
