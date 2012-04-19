package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class MessageReplay extends DomainObject {
    private String messageID;
    @Lob
    private String messageResponse;

    @SuppressWarnings("UnusedDeclaration")
    public MessageReplay() {
    }

    public MessageReplay(String messageID, String messageResponse) {
        this.messageID = messageID;
        this.messageResponse = messageResponse;
    }

    //<editor-fold desc="GettersAndSetters">

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessageResponse() {
        return messageResponse;
    }

    public void setMessageResponse(String messageResponse) {
        this.messageResponse = messageResponse;
    }
    //</editor-fold>
}
