package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

public class StatusType extends DomainObject {
    private String status;

    protected StatusType(Long id) {
        super(id);
    }

    //<editor-fold desc="GettersAndSetters">
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    //</editor-fold>
}
