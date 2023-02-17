package dk.bemyndigelsesregister.domain;

public abstract class ExternalIdentifiedDomainObject extends DomainObject {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return getCode();
    }
}
