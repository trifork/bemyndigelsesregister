package dk.bemyndigelsesregister.bemyndigelsesservice.web.request;

public class HentBemyndigelserRequest {
    protected String bemyndigende;
    protected String bemyndigede;

//<editor-fold desc="GettersAndSetters">

    public String getBemyndigende() {
        return bemyndigende;
    }

    public void setBemyndigende(String bemyndigende) {
        this.bemyndigende = bemyndigende;
    }

    public String getBemyndigede() {
        return bemyndigede;
    }

    public void setBemyndigede(String bemyndigede) {
        this.bemyndigede = bemyndigede;
    }

//</editor-fold>
}
