package dk.bemyndigelsesregister.ws;

import dk.bemyndigelsesregister.domain.Delegation;
import dk.bemyndigelsesregister.domain.*;
import dk.bemyndigelsesregister.mapper.ServiceTypeMapper;
import dk.bemyndigelsesregister.service.DelegationManager;
import dk.bemyndigelsesregister.service.MetadataManager;
import dk.nsi.bemyndigelse._2017._08._01.DelegatingSystem;
import dk.nsi.bemyndigelse._2017._08._01.*;
import dk.sds.nsp.security.SecurityContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.server.endpoint.annotation.SoapAction;

import java.time.Instant;
import java.util.*;

@Endpoint
public class DelegationWebService extends AbstractWebService implements DelegationPortType {
    private static final Logger log = LogManager.getLogger(DelegationWebService.class);

    @Autowired
    private DelegationManager delegationManager;

    @Autowired
    private MetadataManager metadataManager;

    @Autowired
    private ServiceTypeMapper typeMapper;

    @Override
    @SoapAction("http://nsi.dk/bemyndigelse/2017/08/01#CreateDelegations")
    @ResponsePayload
    public CreateDelegationsResponse createDelegations20170801(@RequestPayload CreateDelegationsRequest request) throws DGWSFault {
        try {
            SecurityContext securityContext = getSecurityContext();
            checkSecurityTicket(securityContext);

            // auditlog call - one for each delegatee
            Set<String> delegateeCprs = new HashSet<>();
            for (CreateDelegationsRequest.Create createDelegation : request.getCreates()) {
                delegateeCprs.add(createDelegation.getDelegateeCpr());
            }
            for (String delegateeCpr : delegateeCprs) {
                auditLog("Opret bemyndigelser", delegateeCpr, securityContext);
            }

            Collection<Delegation> delegations = new ArrayList<>();

            for (CreateDelegationsRequest.Create createDelegation : request.getCreates()) {
                if (createDelegation.getState().equals(State.ANMODET)) {
                    authorizeOperationForCpr(securityContext, "CPR for calling user was different from both DelegatorCpr and DelegateeCpr", createDelegation.getDelegatorCpr(), createDelegation.getDelegateeCpr());
                } else {
                    authorizeOperationForCpr(securityContext, "CPR for calling user was different from DelegatorCpr", createDelegation.getDelegatorCpr());
                }

                // check role
                Metadata metadata = metadataManager.getMetadata(Domain.DEFAULT_DOMAIN, createDelegation.getSystemId()); // throws exception if system is unknown
                Role role = metadata.getRole(createDelegation.getRoleId());
                checkCallingRole(role, securityContext);

                // create delegation
                log.debug("Creating Delegation: " + createDelegation);
                final Delegation delegation = delegationManager.createDelegation(
                        createDelegation.getSystemId(),
                        createDelegation.getDelegatorCpr(),
                        createDelegation.getDelegateeCpr(),
                        createDelegation.getDelegateeCvr(),
                        createDelegation.getRoleId(),
                        Status.fromValue(createDelegation.getState().value()),
                        createDelegation.getListOfPermissionIds().getPermissionIds(),
                        createDelegation.getEffectiveFrom(),
                        createDelegation.getEffectiveTo());

                delegations.add(delegation);
            }

            final CreateDelegationsResponse response = new CreateDelegationsResponse();
            for (Delegation delegation : delegations) {
                response.getDelegations().add(typeMapper.toDelegationType(delegation));
            }

            return response;
        } catch (SecurityException | IllegalArgumentException ex) {
            throw createException(ex, true);
        } catch (Exception ex) {
            throw createException(ex, false);
        } finally {
            RequestContext.clear();
        }
    }

    @Override
    @SoapAction("http://nsi.dk/bemyndigelse/2017/08/01#GetDelegations")
    @ResponsePayload
    public GetDelegationsResponse getDelegations20170801(@RequestPayload GetDelegationsRequest request) throws DGWSFault {
        try {
            SecurityContext securityContext = getSecurityContext();
            checkSecurityTicket(securityContext);

            Collection<Delegation> delegations = getDelegationsCommon(request.getDelegatorCpr(), request.getDelegateeCpr(), request.getDelegationId(), request.getEffectiveFrom(), request.getEffectiveTo(), securityContext);

            final GetDelegationsResponse response = new GetDelegationsResponse();
            for (Delegation delegation : delegations) {
                response.getDelegations().add(typeMapper.toDelegationType(delegation));
            }

            return response;
        } catch (SecurityException | IllegalArgumentException ex) {
            throw createException(ex, true);
        } catch (Exception ex) {
            throw createException(ex, false);
        } finally {
            RequestContext.clear();
        }
    }

    private List<Delegation> getDelegationsCommon(String delegatorCpr, String delegateeCpr, String delegationId, Instant effectiveFrom, Instant effectiveTo, SecurityContext securityContext) {
        auditLog("Hent bemyndigelser", delegateeCpr, securityContext);

        List<Delegation> delegations = new LinkedList<>();

        // check arguments
        if ((delegatorCpr != null ? 1 : 0) + (delegateeCpr != null ? 1 : 0) + (delegationId != null ? 1 : 0) != 1) {
            throw new IllegalArgumentException("A single argument must be supplied, i.e. exactly one of delegatorCpr, delegateeCpr or delegationId must not be null");
        }

        // authorize
        if (delegatorCpr != null)
            authorizeOperationForCpr(securityContext, "CPR for calling user was different from DelegatorCpr", delegatorCpr);
        else if (delegateeCpr != null)
            authorizeOperationForCpr(securityContext, "CPR for calling user was different from DelegateeCpr", delegateeCpr);

        // invoke correct method on manager
        if (delegatorCpr != null) {
            List<Delegation> list = delegationManager.getDelegationsByDelegatorCpr(delegatorCpr, effectiveFrom, effectiveTo);
            if (list != null) {
                delegations.addAll(list);
            }
        } else if (delegateeCpr != null) {
            List<Delegation> list = delegationManager.getDelegationsByDelegateeCpr(delegateeCpr, effectiveFrom, effectiveTo);
            if (list != null) {
                delegations.addAll(list);
            }
        } else {
            Delegation d = delegationManager.getDelegation(delegationId);
            if (d != null) {
                delegations.add(d);
            }
        }

        List<Delegation> result = new LinkedList<>();
        for (Delegation delegation : delegations) {
            if (delegation.getEffectiveTo() == null || delegation.getEffectiveFrom().isBefore(delegation.getEffectiveTo())) {
                result.add(delegation);
            }
        }

        return result;
    }


    @Override
    @SoapAction("http://nsi.dk/bemyndigelse/2017/08/01#DeleteDelegations")
    @ResponsePayload
    public DeleteDelegationsResponse deleteDelegations20170801(@RequestPayload DeleteDelegationsRequest request) throws DGWSFault {
        try {
            SecurityContext securityContext = getSecurityContext();
            checkSecurityTicket(securityContext);

            List<String> result = deleteDelegationsCommon(request.getDelegatorCpr(), request.getDelegateeCpr(), request.getListOfDelegationIds().getDelegationIds(), request.getDeletionDate(), securityContext);

            final DeleteDelegationsResponse response = new DeleteDelegationsResponse();
            response.getDelegationIds().addAll(result);

            return response;
        } catch (SecurityException | IllegalArgumentException ex) {
            throw createException(ex, true);
        } catch (Exception ex) {
            throw createException(ex, false);
        } finally {
            RequestContext.clear();
        }
    }

    private List<String> deleteDelegationsCommon(String delegatorCpr, String delegateeCpr, List<String> delegationIds, Instant deletionDate, SecurityContext securityContext) {
        auditLog("Slet bemyndigelser", delegateeCpr, securityContext);

        // check arguments
        if (delegatorCpr == null) {
            if (delegateeCpr == null) {
                throw new IllegalArgumentException("Exactly one of delegatorCpr and delegateeCpr must be supplied");
            }
        } else {
            if (delegateeCpr != null) {
                throw new IllegalArgumentException("Exactly one of delegatorCpr and delegateeCpr must be supplied");
            }
        }
        if (delegationIds == null || delegationIds.isEmpty())
            throw new IllegalArgumentException("List of delegationIds must not be empty");
        if (deletionDate != null && deletionDate.isBefore(Instant.now()))
            throw new IllegalArgumentException("DeletionDate cannot be in the past");

        // authorize
        if (delegatorCpr != null)
            authorizeOperationForCpr(securityContext, "CPR for calling user was different from DelegatorCpr", delegatorCpr);
        else
            authorizeOperationForCpr(securityContext, "CPR for calling user was different from DelegateeCpr", delegateeCpr);

        // invoke manager
        List<String> result = new LinkedList<>();
        for (String delegationId : delegationIds) {
            String deleted = delegationManager.deleteDelegation(delegatorCpr, delegateeCpr, delegationId, deletionDate);
            if (deleted != null) result.add(deleted);
        }
        return result;
    }

    @Override
    @SoapAction("http://nsi.dk/bemyndigelse/2017/08/01#PutMetadata")
    @ResponsePayload
    public PutMetadataResponse putMetadata20170801(@RequestPayload PutMetadataRequest request) throws DGWSFault {
        try {
            SecurityContext securityContext = getSecurityContext();
            checkSecurityTicket(securityContext, false, "bemyndigelsesservice.indlaesMetadata");

            auditLog("Indlæs metadata", null, securityContext);

            String domainCode = request.getDomain();
            if (domainCode == null || domainCode.trim().isEmpty())
                throw new IllegalArgumentException("Domain must be specified in the request");

            String systemCode = request.getSystemId();
            if (systemCode == null || systemCode.trim().isEmpty())
                throw new IllegalArgumentException("System must be specified in the request");

            Metadata metadata = new Metadata(domainCode, systemCode, request.getSystemLongName());

            if (request.getPermissions() != null) {
                for (SystemPermission permission : request.getPermissions()) {
                    if (permission.getPermissionId().contains(Metadata.ASTERISK_PERMISSION_CODE))
                        throw new IllegalArgumentException("Permission [*] for system [" + systemCode + "]: All current and future permissions are supported, but must be specified as EnableAsteriskPermission=true in request");

                    metadata.addPermission(permission.getPermissionId(), permission.getPermissionDescription());
                }
            }

            if (request.getRoles() != null) {
                for (DelegatingRole role : request.getRoles()) {
                    metadata.addRole(role.getRoleId(), role.getRoleDescription(), role.getEducationCodes() != null ? role.getEducationCodes().getEducationCodes() : null);

                    if (role.getDelegatablePermissions() != null) {
                        for (String permissionCode : role.getDelegatablePermissions().getPermissionIds()) {
                            if (permissionCode.contains(Metadata.ASTERISK_PERMISSION_CODE))
                                throw new IllegalArgumentException("DelegatablePermission [" + permissionCode + "] for role [" + role.getRoleId() + "]: All current and future permissions are supported, but if used, delegation of this permission is implied for all roles, and cannot be explicitly specified as delegatable.");

                            String permissionDescription = null;
                            if (request.getPermissions() != null) {
                                for (SystemPermission c : request.getPermissions()) {
                                    if (c.getPermissionId().equals(permissionCode)) {
                                        permissionDescription = c.getPermissionDescription();
                                        break;
                                    }
                                }
                            }
                            metadata.addDelegatablePermission(role.getRoleId(), permissionCode, permissionDescription, true);
                        }
                    }

                    if (role.getUndelegatablePermissions() != null) {
                        for (String permissionCode : role.getUndelegatablePermissions().getPermissionIds()) {
                            if (permissionCode.contains(Metadata.ASTERISK_PERMISSION_CODE))
                                throw new IllegalArgumentException("UndelegatablePermission [" + permissionCode + "] for role [" + role.getRoleId() + "]: All current and future permissions are supported, but if used, delegation of this permission is implied for all roles, and cannot be explicitly specified as undelegatable.");

                            String permissionDescription = null;
                            if (request.getPermissions() != null) {
                                for (SystemPermission c : request.getPermissions()) {
                                    if (c.getPermissionId().equals(permissionCode)) {
                                        permissionDescription = c.getPermissionDescription();
                                        break;
                                    }
                                }
                            }
                            metadata.addDelegatablePermission(role.getRoleId(), permissionCode, permissionDescription, false);
                        }
                    }
                }
            }

            if (request.isEnableAsteriskPermission()) {
                // create asterisk permission and add it as delegatable to all roles
                metadata.addPermission(Metadata.ASTERISK_PERMISSION_CODE, Metadata.ASTERISK_PERMISSION_DESCRIPTION);
                for (DelegatingRole role : request.getRoles()) {
                    metadata.addDelegatablePermission(role.getRoleId(), Metadata.ASTERISK_PERMISSION_CODE, Metadata.ASTERISK_PERMISSION_DESCRIPTION, true);
                }
            }

            String result = metadataManager.putMetadata(metadata, request.isDryRun());

            PutMetadataResponse response = new PutMetadataResponse();
            response.setResult(result);
            return response;
        } catch (SecurityException | IllegalArgumentException ex) {
            throw createException(ex, true);
        } catch (Exception ex) {
            throw createException(ex, false);
        } finally {
            RequestContext.clear();
        }
    }

    @Override
    @SoapAction("http://nsi.dk/bemyndigelse/2017/08/01#GetMetadata")
    @ResponsePayload
    public GetMetadataResponse getMetadata20170801(@RequestPayload GetMetadataRequest request) throws DGWSFault {
        try {
            // note - no securitycheck for this method

            Metadata metadata = metadataManager.getMetadata(request.getDomain(), request.getSystemId());

            GetMetadataResponse response = new GetMetadataResponse();

            response.setDomain(metadata.getDomain().getCode());

            DelegatingSystem system = new DelegatingSystem();
            system.setSystemId(metadata.getSystem().getCode());
            system.setSystemLongName(metadata.getSystem().getDescription());
            response.setSystem(system);

            boolean asteriskPermission = false;
            if (metadata.getPermissions() != null) {
                for (Permission c : metadata.getPermissions()) {
                    if (Metadata.ASTERISK_PERMISSION_CODE.equals(c.getCode())) {
                        asteriskPermission = true;
                    }
                    SystemPermission permission = new SystemPermission();
                    permission.setPermissionId(c.getCode());
                    permission.setPermissionDescription(c.getDescription());
                    response.getPermissions().add(permission);
                }
            }
            response.setEnableAsteriskPermission(asteriskPermission); // it's optional in the response, but set anyway, for sake of completeness

            if (metadata.getRoles() != null) {
                for (Role c : metadata.getRoles()) {
                    DelegatingRole role = new DelegatingRole();
                    role.setRoleId(c.getCode());
                    role.setRoleDescription(c.getDescription());

                    if (Boolean.TRUE.equals(request.isIncludeEducationCodes())) { // preserve backward compatibility
                        role.setEducationCodes(mapEducationCodes(c.getEducationCodes()));
                    }

                    if (metadata.getDelegatablePermissions() != null) {
                        for (DelegatablePermission dp : metadata.getDelegatablePermissions(c.getCode())) {
                            if (dp.isDelegatable()) {
                                if (role.getDelegatablePermissions() == null) {
                                    role.setDelegatablePermissions(new DelegatingRole.DelegatablePermissions());
                                }
                                role.getDelegatablePermissions().getPermissionIds().add(dp.getPermission().getCode());
                            } else {
                                if (role.getUndelegatablePermissions() == null) {
                                    role.setUndelegatablePermissions(new DelegatingRole.UndelegatablePermissions());
                                }
                                role.getUndelegatablePermissions().getPermissionIds().add(dp.getPermission().getCode());
                            }
                        }
                    }

                    response.getRoles().add(role);
                }
            }

            return response;
        } catch (SecurityException | IllegalArgumentException ex) {
            throw createException(ex, true);
        } catch (Exception ex) {
            throw createException(ex, false);
        } finally {
            RequestContext.clear();
        }
    }

    @Override
    @SoapAction("http://nsi.dk/bemyndigelse/2017/08/01#GetAllMetadata")
    @ResponsePayload
    public GetAllMetadataResponse getAllMetadata20170801(@RequestPayload GetAllMetadataRequest request) throws DGWSFault {
        try {
            List<Metadata> metadataList = metadataManager.getAllMetadata(request.getDomain());

            GetAllMetadataResponse response = new GetAllMetadataResponse();
            response.setDomain(request.getDomain());

            if (metadataList != null) {
                for (Metadata metadata : metadataList) {
                    GetAllMetadataResponse.Metadata m = new GetAllMetadataResponse.Metadata();

                    DelegatingSystem system = new DelegatingSystem();
                    system.setSystemId(metadata.getSystem().getCode());
                    system.setSystemLongName(metadata.getSystem().getDescription());
                    m.setSystem(system);

                    boolean asteriskPermission = false;
                    if (metadata.getPermissions() != null) {
                        for (Permission c : metadata.getPermissions()) {
                            if (Metadata.ASTERISK_PERMISSION_CODE.equals(c.getCode())) {
                                asteriskPermission = true;
                            }
                            SystemPermission permission = new SystemPermission();
                            permission.setPermissionId(c.getCode());
                            permission.setPermissionDescription(c.getDescription());
                            m.getPermissions().add(permission);
                        }
                    }
                    m.setEnableAsteriskPermission(asteriskPermission); // it's optional in the response, but set anyway, for sake of completeness

                    if (metadata.getRoles() != null) {
                        for (Role c : metadata.getRoles()) {
                            DelegatingRole role = new DelegatingRole();
                            role.setRoleId(c.getCode());
                            role.setRoleDescription(c.getDescription());

                            if (Boolean.TRUE.equals(request.isIncludeEducationCodes())) { // preserve backward compatibility
                                role.setEducationCodes(mapEducationCodes(c.getEducationCodes()));
                            }

                            if (metadata.getDelegatablePermissions() != null) {
                                for (DelegatablePermission dp : metadata.getDelegatablePermissions(c.getCode())) {
                                    if (dp.isDelegatable()) {
                                        if (role.getDelegatablePermissions() == null) {
                                            role.setDelegatablePermissions(new DelegatingRole.DelegatablePermissions());
                                        }
                                        role.getDelegatablePermissions().getPermissionIds().add(dp.getPermission().getCode());
                                    } else {
                                        if (role.getUndelegatablePermissions() == null) {
                                            role.setUndelegatablePermissions(new DelegatingRole.UndelegatablePermissions());
                                        }
                                        role.getUndelegatablePermissions().getPermissionIds().add(dp.getPermission().getCode());
                                    }
                                }
                            }

                            m.getRoles().add(role);
                        }
                    }

                    response.getMetadatas().add(m);
                }
            }

            return response;
        } catch (SecurityException | IllegalArgumentException ex) {
            throw createException(ex, true);
        } catch (Exception ex) {
            throw createException(ex, false);
        } finally {
            RequestContext.clear();
        }
    }

    @Override
    @SoapAction("http://nsi.dk/bemyndigelse/2017/08/01#GetExpirationInfo")
    @ResponsePayload
    public GetExpirationInfoResponse getExpirationInfo20170801(@RequestPayload GetExpirationInfoRequest request) throws DGWSFault {
        try {
            SecurityContext securityContext = getSecurityContext();
            checkSecurityTicket(securityContext);

            auditLog("Hent information om udløbne bemyndigelser", null, securityContext);

            String delegatorCpr = request.getDelegatorCpr();

            // authorize
            authorizeOperationForCpr(securityContext, "CPR for calling user was different from DelegatorCpr", delegatorCpr);

            ExpirationInfo info = delegationManager.getExpirationInfo(delegatorCpr, request.getDays());

            GetExpirationInfoResponse response = new GetExpirationInfoResponse();
            response.setDelegatorCpr(delegatorCpr);
            response.setDelegationCount(info.getDelegationCount());
            response.setDelegateeCount(info.getDelegateeCount());
            response.setDaysToFirstExpiration(info.getDaysToFirstExpiration());
            response.setFirstExpiryDelegationCount(info.getFirstExpiryDelegationCount());
            response.setFirstExpiryDelegateeCount(info.getFirstExpiryDelegateeCount());

            return response;
        } catch (SecurityException | IllegalArgumentException ex) {
            throw createException(ex, true);
        } catch (Exception ex) {
            throw createException(ex, false);
        } finally {
            RequestContext.clear();
        }
    }

    private DelegatingRole.EducationCodes mapEducationCodes(List<String> educationCodes) {
        if (educationCodes == null || educationCodes.isEmpty()) {
            return null;
        }
        DelegatingRole.EducationCodes mapped = new DelegatingRole.EducationCodes();
        for (String educationCode : educationCodes) {
            mapped.getEducationCodes().add(educationCode);
        }
        return mapped;
    }

    private RuntimeException createException(Exception ex, boolean isWarning) {
        if (isWarning) {
            log.warn(ex.getMessage());
        } else {
            log.error(ex.getMessage(), ex);
        }

        if (ex instanceof RuntimeException) {
            return (RuntimeException) ex;
        }

        return new RuntimeException(ex.getMessage(), ex);
    }
}
