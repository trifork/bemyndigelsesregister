package dk.bemyndigelsesregister.service;

import dk.sds.nsp.security.SecurityContext;

public interface AuditLogger {
    void log(String method, String delegateeCpr, SecurityContext securityContext);
}