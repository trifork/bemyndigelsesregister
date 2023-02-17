package dk.bemyndigelsesregister.ws;

public class RequestContext {
    private static final ThreadLocal<RequestContext> currentRequestContext = new ThreadLocal<>();

    public static RequestContext get() {
        RequestContext requestContext = currentRequestContext.get();
        if (requestContext == null) {
            requestContext = new RequestContext();
            currentRequestContext.set(requestContext);
        }
        return requestContext;
    }

    public static void clear() {
        currentRequestContext.remove();
    }

    private String actingUser;
    private String messageId;

    public String getActingUser() {
        return actingUser;
    }

    public void setActingUser(String actingUser) {
        this.actingUser = actingUser;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
