package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class MessageRetransmission extends DomainObject {
    private String messageID;
    @Lob
    private String messageResponse;

    private String implementationBuild;

    @SuppressWarnings("UnusedDeclaration")
    public MessageRetransmission() {
    }

    public MessageRetransmission(String messageID, String messageResponse, String implementationBuild) {
        this.messageID = messageID;
        this.messageResponse = messageResponse;
        this.implementationBuild = implementationBuild;
        this.lastModified = DateTime.now();
    }

    //<editor-fold desc="GettersAndSetters">

    public String getMessageID() {
        return messageID;
    }

    @SuppressWarnings("UnusedDeclaration used by ebean")
    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessageResponse() {
        return messageResponse;
    }

    @SuppressWarnings("UnusedDeclaration used by ebean")
    public void setMessageResponse(String messageResponse) {
        this.messageResponse = messageResponse;
    }

    public String getImplementationBuild() {
        return implementationBuild;
    }

    @SuppressWarnings("UnusedDeclaration used by ebean")
    public void setImplementationBuild(String implementationBuild) {
        this.implementationBuild = implementationBuild;
    }
    //</editor-fold>
}
