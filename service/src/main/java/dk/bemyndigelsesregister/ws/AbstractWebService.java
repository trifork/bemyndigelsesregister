package dk.bemyndigelsesregister.ws;

import dk.bemyndigelsesregister.dao.WhitelistDAO;
import dk.bemyndigelsesregister.domain.Role;
import dk.bemyndigelsesregister.domain.WhitelistType;
import dk.bemyndigelsesregister.service.AuditLogger;
import dk.sds.nsp.security.Security;
import dk.sds.nsp.security.SecurityContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

public abstract class AbstractWebService {
    private static final Logger log = LogManager.getLogger(AbstractWebService.class);

    @Autowired
    private AuditLogger auditLogger;

    @Autowired
    private WhitelistDAO whitelistDAO;

    @Value("${rolevalidation.enabled}")
    private String roleValidationEnabled;

    protected SecurityContext getSecurityContext() {
        try {
            return Security.getSecurityContext();
        } catch (Exception ex) {
            throw new SecurityException("Service must be called with DGWS or IDWS security");
        }
    }

    protected void checkSecurityTicket(SecurityContext securityContext) {
        checkSecurityTicket(securityContext, true, null);
    }

    protected void checkSecurityTicket(SecurityContext securityContext, boolean userRequired, String whitelist) {
        Optional<SecurityContext.Ticket> ticketOptional;
        try {
            ticketOptional = securityContext.getTicket();
        } catch (Exception ex) { // avoid NPE in NspSecurityContext.java:113
            throw new SecurityException("No security ticket is present");
        }
        if (ticketOptional.isEmpty()) {
            throw new SecurityException("No security ticket is present");
        }
        SecurityContext.Ticket ticket = ticketOptional.get();
        if (!ticket.isValid()) {
            throw new SecurityException("Invalid security ticket");
        }
        if (userRequired && securityContext.getActingUser().isEmpty()) {
            throw new SecurityException("Calling user not found in security context");
        }
        if (whitelist != null) {
            if (securityContext.getOrganisation().isEmpty()) {
                throw new SecurityException("Calling organisation not found in security context");
            }

            SecurityContext.Organisation organisation = securityContext.getOrganisation().get();
            if (organisation.getIdentifierFormat() != SecurityContext.Organisation.OrganisationIdentifierFormat.CVR) {
                throw new SecurityException("Unsupported organisation identifier format " + organisation.getIdentifierFormat() + ". Only CVR is supported");
            }

            if (!whitelistDAO.exists(whitelist, WhitelistType.SYSTEM_CVR, organisation.getIdentifier())) {
                throw new SecurityException("Organisation " + organisation.getIdentifier() + " not whitelisted for " + whitelist);
            }
        }

        if (securityContext.getMessage().isPresent()) {
            SecurityContext.Message message = securityContext.getMessage().get();
            if (message.getIdentifier().isPresent()) {
                RequestContext.get().setMessageId(message.getIdentifier().get());
            }
        }

        RequestContext.get().setActingUser(getCallingUser(securityContext));
    }

    protected void checkCallingRole(Role role, SecurityContext securityContext) {
        if (role != null) { // unknown roles are handled by createDelegationObject in DelegationManagerImpl
            List<String> educationCodes = role.getEducationCodes();
            if (educationCodes != null && !educationCodes.isEmpty()) {

                String educationCodeFromSecurityContext = null;
                if (securityContext.getActingUser().isPresent()) {
                    SecurityContext.User user = securityContext.getActingUser().get();
                    if (user.getCredentials().isPresent()) {
                        SecurityContext.User.Credentials credentials = user.getCredentials().get();
                        if (credentials.getEducationCode().isPresent()) {
                            educationCodeFromSecurityContext = credentials.getEducationCode().get();
                            for (String educationCode : educationCodes) {
                                if (educationCode.equalsIgnoreCase(educationCodeFromSecurityContext)) {
                                    return;
                                }
                            }
                        }
                    }
                }

                String message = "Role/EducationCode mismatch. Required educationCode = " + educationCodes + ", educationCode from SecurityContext = " + educationCodeFromSecurityContext;
                if (Boolean.parseBoolean(roleValidationEnabled)) {
                    throw new SecurityException(message);
                } else {
                    log.warn(message);
                }
            }
        }
    }


    private String getCallingUser(SecurityContext securityContext) {
        StringBuilder b = new StringBuilder();

        if (securityContext.getActingUser().isPresent()) {
            SecurityContext.User user = securityContext.getActingUser().get();
            if (user.getGivenName().isPresent()) {
                b.append(user.getGivenName().get());
            }
            if (user.getSurname().isPresent()) {
                if (b.length() > 0) {
                    b.append(' ');
                }
                b.append(user.getSurname().get());
            }
        } else {
            if (securityContext.getOrganisation().isPresent()) {
                SecurityContext.Organisation org = securityContext.getOrganisation().get();
                if (org.getName().isPresent()) {
                    b.append(org.getName().get());
                }
            }
        }

        return b.length() <= 255 ? b.toString() : b.substring(0, 255); // fit in "sidst_modificeret_af" columns
    }

    protected void authorizeOperationForCpr(SecurityContext securityContext, String errorMessage, String... authorizedCprs) {
        Set<String> authorizedCprSet = new HashSet<>(Arrays.asList(authorizedCprs));

        if (!securityContext.getActingUser().isPresent()) {
            throw new SecurityException("Calling user not found in security context");
        }

        String cprnr = securityContext.getActingUser().get().getIdentifier();
        if (!authorizedCprSet.contains(cprnr)) {
            log.info("Failed to authorize user. Authorized CPRs: " + authorizedCprSet + ". CPR in securityContext: [" + cprnr + "]");
            throw new SecurityException(errorMessage);
        }
    }

    protected void auditLog(String message, String cprnr, SecurityContext securityContext) {
        auditLogger.log(message, cprnr, securityContext);
    }
}
