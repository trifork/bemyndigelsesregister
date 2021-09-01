package dk.bemyndigelsesregister.bemyndigelsesservice.server;

/**
 * Holder for request specific data
 */
public final class RequestContext {
    private static final ThreadLocal<RequestContext> currentRequestContext = new ThreadLocal<RequestContext>();

    private String messageId;

    public RequestContext() {
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public static RequestContext get() {
        RequestContext requestContext = currentRequestContext.get();
        if (requestContext == null) {
            requestContext = new RequestContext();
            currentRequestContext.set(requestContext);
        }
        return requestContext;
    }

}
