package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class MessageReplay extends DomainObject {
    private String messageID;
    @Lob
    private byte[] messageResponse;

    public MessageReplay() {
    }

    public MessageReplay(String messageID, byte[] messageResponse) {
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

    public byte[] getMessageResponse() {
        return messageResponse;
    }

    public void setMessageResponse(byte[] messageResponse) {
        this.messageResponse = messageResponse;
    }
    //</editor-fold>
}
