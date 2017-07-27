package dk.bemyndigelsesregister.bemyndigelsesservice.server.audit;

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

/*
        public static void setForTestPurposes(AuditLogVO auditlogVo) {
            RequestContext context = currentRequestContext.get();
            if (context == null){
                context = new RequestContext(auditlogVo);
                currentRequestContext.set(context);
            }
            else
                context.setAuditLogVO(auditlogVo);
        }
*/

    public static RequestContext get() {
        RequestContext requestContext = currentRequestContext.get();
        if (requestContext == null) {
            requestContext = new RequestContext();
            currentRequestContext.set(requestContext);
        }
        return requestContext;
    }

}
