package dk.bemyndigelsesregister.bemyndigelsesservice.web;

public class OpretGodkendtBemyndigelseRequest {
    private String bemyndigende;
    private String bemyndigede;
    private String bemyndigedeCvr;
    private String system;
    private String arbejdsFunktion;
    private String rettighedskode;

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

    public String getBemyndigedeCvr() {
        return bemyndigedeCvr;
    }

    public void setBemyndigedeCvr(String bemyndigedeCvr) {
        this.bemyndigedeCvr = bemyndigedeCvr;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getArbejdsFunktion() {
        return arbejdsFunktion;
    }

    public void setArbejdsFunktion(String arbejdsFunktion) {
        this.arbejdsFunktion = arbejdsFunktion;
    }

    public String getRettighedskode() {
        return rettighedskode;
    }

    public void setRettighedskode(String rettighedskode) {
        this.rettighedskode = rettighedskode;
    }

//</editor-fold>

}
