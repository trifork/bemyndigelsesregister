package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import org.joda.time.DateTime;

public class Bemyndigelse extends DomainObject {
    private String kode;
    private String bemyndigendeCpr;
    private String bemyndigedeCpr;
    private String bemyndigedeCvr;
    private String system;
    private Arbejdsfunktion arbejdsfunktion;
    private Rettighed rettighed;
    private StatusType status;
    private DateTime godkendelsesdato;
    private DateTime gyldigFra;
    private DateTime gyldigTil;
    private int versionsid;

    protected Bemyndigelse(Long id) {
        super(id);
    }
}
