package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import dk.bemyndigelsesregister.bemyndigelsesservice.BemyndigelsesService_20170801;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.ExpirationInfo;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Metadata;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Status;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ServiceTypeMapper_20170801;
import dk.nsi.bemyndigelse._2017._08._01.*;
import dk.sds.nsp.security.Security;
import dk.sds.nsp.security.SecurityContext;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapHeader;

import javax.inject.Inject;
import java.util.*;

@Repository("bemyndigelsesService_2017_08_01")
@Endpoint
public class BemyndigelsesServiceImpl_20170801 extends AbstractServiceImpl implements BemyndigelsesService_20170801 {
    private static Logger logger = Logger.getLogger(BemyndigelsesServiceImpl_20170801.class);

    @Inject
    ServiceTypeMapper_20170801 typeMapper;

    public BemyndigelsesServiceImpl_20170801() {
        super(logger);
    }

    @Override
    @Transactional
    @ResponsePayload
    public CreateDelegationsResponse createDelegations(@RequestPayload CreateDelegationsRequest request, SoapHeader soapHeader) {
        try {
            SecurityContext securityContext = Security.getSecurityContext();
            checkSecurityTicket(securityContext);

            // auditlog call - one for each delegatee
            Set<String> delegateeCprs = new HashSet<>();
            for (CreateDelegationsRequest.Create createDelegation : request.getCreate()) {
                delegateeCprs.add(createDelegation.getDelegateeCpr());
            }
            for (String delegateeCpr : delegateeCprs) {
                auditLogger.log("Opret bemyndigelser", delegateeCpr, securityContext);
            }

            Collection<Delegation> delegations = new ArrayList<>();

            for (CreateDelegationsRequest.Create createDelegation : request.getCreate()) {
                if (createDelegation.getState().equals(State.ANMODET)) {
                    authorizeOperationForCpr(securityContext, "CPR for calling user was different from both DelegatorCpr and DelegateeCpr", createDelegation.getDelegatorCpr(), createDelegation.getDelegateeCpr());
                } else {
                    authorizeOperationForCpr(securityContext, "CPR for calling user was different from DelegatorCpr", createDelegation.getDelegatorCpr());
                }
                logger.debug("Creating Delegation: " + createDelegation);
                final Delegation delegation = delegationManager.createDelegation(
                        createDelegation.getSystemId(),
                        createDelegation.getDelegatorCpr(),
                        createDelegation.getDelegateeCpr(),
                        createDelegation.getDelegateeCvr(),
                        createDelegation.getRoleId(),
                        Status.fromValue(createDelegation.getState().value()),
                        createDelegation.getListOfPermissionIds().getPermissionId(),
                        nullableDateTime(createDelegation.getEffectiveFrom()),
                        nullableDateTime(createDelegation.getEffectiveTo()));

                delegations.add(delegation);
            }

            final CreateDelegationsResponse response = new CreateDelegationsResponse();
            for (Delegation delegation : delegations) {
                response.getDelegation().add(typeMapper.toDelegationType(delegation));
            }

            return response;
        } catch (SecurityException | IllegalArgumentException ex) {
            throw createException(ex, true);
        } catch (Exception ex) {
            throw createException(ex, false);
        }
    }

    @Override
    @Transactional
    @ResponsePayload
    public GetDelegationsResponse getDelegations(@RequestPayload GetDelegationsRequest request, SoapHeader soapHeader) {
        try {
            SecurityContext securityContext = Security.getSecurityContext();
            checkSecurityTicket(securityContext);

            Collection<Delegation> delegations = getDelegationsCommon(request.getDelegatorCpr(), request.getDelegateeCpr(), request.getDelegationId(), request.getEffectiveFrom(), request.getEffectiveTo(), securityContext);

            final GetDelegationsResponse response = new GetDelegationsResponse();
            for (Delegation delegation : delegations) {
                response.getDelegation().add(typeMapper.toDelegationType(delegation));
            }

            return response;
        } catch (SecurityException | IllegalArgumentException ex) {
            throw createException(ex, true);
        } catch (Exception ex) {
            throw createException(ex, false);
        }
    }

    @Override
    @Transactional
    @ResponsePayload
    public DeleteDelegationsResponse deleteDelegations(@RequestPayload DeleteDelegationsRequest request, SoapHeader soapHeader) {
        try {
            SecurityContext securityContext = Security.getSecurityContext();
            checkSecurityTicket(securityContext);

            List<String> result = deleteDelegationsCommon(request.getDelegatorCpr(), request.getDelegateeCpr(), request.getListOfDelegationIds().getDelegationId(), request.getDeletionDate(), securityContext);

            final DeleteDelegationsResponse response = new DeleteDelegationsResponse();
            response.getDelegationId().addAll(result);

            return response;
        } catch (SecurityException | IllegalArgumentException ex) {
            throw createException(ex, true);
        } catch (Exception ex) {
            throw createException(ex, false);
        }
    }


    @Override
    @Transactional
    @ResponsePayload
    public PutMetadataResponse putMetadata(@RequestPayload PutMetadataRequest request, SoapHeader soapHeader) {
        try {
            SecurityContext securityContext = Security.getSecurityContext();
            checkSecurityTicket(securityContext, false, "bemyndigelsesservice.indlaesMetadata");

            auditLogger.log("Indlæs metadata", null, securityContext);

            String domainCode = request.getDomain();
            if (domainCode == null || domainCode.trim().isEmpty())
                throw new IllegalArgumentException("Domain must be specified in the request");

            String systemCode = request.getSystemId();
            if (systemCode == null || systemCode.trim().isEmpty())
                throw new IllegalArgumentException("System must be specified in the request");

            Metadata metadata = new Metadata(domainCode, systemCode, request.getSystemLongName());

            if (request.getPermission() != null) {
                for (SystemPermission permission : request.getPermission()) {
                    if (permission.getPermissionId().contains(Metadata.ASTERISK_PERMISSION_CODE))
                        throw new IllegalArgumentException("Permission [*] for system [" + systemCode + "]: All current and future permissions are supported, but must be specified as EnableAsteriskPermission=true in request");

                    metadata.addPermission(permission.getPermissionId(), permission.getPermissionDescription());
                }
            }

            if (request.getRole() != null) {
                for (DelegatingRole role : request.getRole()) {
                    metadata.addRole(role.getRoleId(), role.getRoleDescription());

                    if (role.getDelegatablePermissions() != null) {
                        for (String permissionCode : role.getDelegatablePermissions().getPermissionId()) {
                            if (permissionCode.contains(Metadata.ASTERISK_PERMISSION_CODE))
                                throw new IllegalArgumentException("DelegatablePermission [" + permissionCode + "] for role [" + role.getRoleId() + "]: All current and future permissions are supported, but if used, delegation of this permission is implied for all roles, and cannot be explicitly specified as delegatable.");

                            String permissionDescription = null;
                            if (request.getPermission() != null) {
                                for (SystemPermission c : request.getPermission()) {
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
                        for (String permissionCode : role.getUndelegatablePermissions().getPermissionId()) {
                            if (permissionCode.contains(Metadata.ASTERISK_PERMISSION_CODE))
                                throw new IllegalArgumentException("UndelegatablePermission [" + permissionCode + "] for role [" + role.getRoleId() + "]: All current and future permissions are supported, but if used, delegation of this permission is implied for all roles, and cannot be explicitly specified as undelegatable.");

                            String permissionDescription = null;
                            if (request.getPermission() != null) {
                                for (SystemPermission c : request.getPermission()) {
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
                for (DelegatingRole role : request.getRole()) {
                    metadata.addDelegatablePermission(role.getRoleId(), Metadata.ASTERISK_PERMISSION_CODE, Metadata.ASTERISK_PERMISSION_DESCRIPTION, true);
                }
            }

            metadataManager.putMetadata(metadata);

            return new PutMetadataResponse();
        } catch (SecurityException | IllegalArgumentException ex) {
            throw createException(ex, true);
        } catch (Exception ex) {
            throw createException(ex, false);
        }
    }

    @Override
    @ResponsePayload
    public GetExpirationInfoResponse getExpirationInfo(@RequestPayload GetExpirationInfoRequest request, SoapHeader soapHeader) {
        try {
            SecurityContext securityContext = Security.getSecurityContext();
            checkSecurityTicket(securityContext);

            auditLogger.log("Hent information om udløbne bemyndigelser", null, securityContext);

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
        }
    }

    @Override
    @ResponsePayload
    public GetMetadataResponse getMetadata(@RequestPayload GetMetadataRequest request, SoapHeader soapHeader) {
        try {
            // note - no securitycheck for this method

            Metadata metadata = metadataManager.getMetadata(request.getDomain(), request.getSystemId());

            GetMetadataResponse response = new GetMetadataResponse();

            response.setDomain(metadata.getDomainCode());

            DelegatingSystem system = new DelegatingSystem();
            system.setSystemId(metadata.getSystem().getCode());
            system.setSystemLongName(metadata.getSystem().getDescription());
            response.setSystem(system);

            boolean asteriskPermission = false;
            if (metadata.getPermissions() != null) {
                for (Metadata.CodeAndDescription c : metadata.getPermissions()) {
                    if (Metadata.ASTERISK_PERMISSION_CODE.equals(c.getCode())) {
                        asteriskPermission = true;
                    }
                    SystemPermission permission = new SystemPermission();
                    permission.setPermissionId(c.getCode());
                    permission.setPermissionDescription(c.getDescription());
                    response.getPermission().add(permission);
                }
            }
            response.setEnableAsteriskPermission(asteriskPermission); // it's optional in the response, but set anyway, for sake of completeness

            if (metadata.getRoles() != null) {
                for (Metadata.CodeAndDescription c : metadata.getRoles()) {
                    DelegatingRole role = new DelegatingRole();
                    role.setRoleId(c.getCode());
                    role.setRoleDescription(c.getDescription());

                    if (metadata.getDelegatablePermissions() != null) {
                        for (Metadata.DelegatablePermission dp : metadata.getDelegatablePermissions(c.getCode())) {
                            if (dp.isDelegatable()) {
                                if (role.getDelegatablePermissions() == null) {
                                    role.setDelegatablePermissions(new DelegatingRole.DelegatablePermissions());
                                }
                                role.getDelegatablePermissions().getPermissionId().add(dp.getPermissionCode());
                            } else {
                                if (role.getUndelegatablePermissions() == null) {
                                    role.setUndelegatablePermissions(new DelegatingRole.UndelegatablePermissions());
                                }
                                role.getUndelegatablePermissions().getPermissionId().add(dp.getPermissionCode());
                            }
                        }
                    }

                    response.getRole().add(role);
                }
            }

            return response;
        } catch (SecurityException | IllegalArgumentException ex) {
            throw createException(ex, true);
        } catch (Exception ex) {
            throw createException(ex, false);
        }
    }


    @Override
    @ResponsePayload
    public GetAllMetadataResponse getAllMetadata(@RequestPayload GetAllMetadataRequest request, SoapHeader soapHeader) {
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
                        for (Metadata.CodeAndDescription c : metadata.getPermissions()) {
                            if (Metadata.ASTERISK_PERMISSION_CODE.equals(c.getCode())) {
                                asteriskPermission = true;
                            }
                            SystemPermission permission = new SystemPermission();
                            permission.setPermissionId(c.getCode());
                            permission.setPermissionDescription(c.getDescription());
                            m.getPermission().add(permission);
                        }
                    }
                    m.setEnableAsteriskPermission(asteriskPermission); // it's optional in the response, but set anyway, for sake of completeness

                    if (metadata.getRoles() != null) {
                        for (Metadata.CodeAndDescription c : metadata.getRoles()) {
                            DelegatingRole role = new DelegatingRole();
                            role.setRoleId(c.getCode());
                            role.setRoleDescription(c.getDescription());

                            if (metadata.getDelegatablePermissions() != null) {
                                for (Metadata.DelegatablePermission dp : metadata.getDelegatablePermissions(c.getCode())) {
                                    if (dp.isDelegatable()) {
                                        if (role.getDelegatablePermissions() == null) {
                                            role.setDelegatablePermissions(new DelegatingRole.DelegatablePermissions());
                                        }
                                        role.getDelegatablePermissions().getPermissionId().add(dp.getPermissionCode());
                                    } else {
                                        if (role.getUndelegatablePermissions() == null) {
                                            role.setUndelegatablePermissions(new DelegatingRole.UndelegatablePermissions());
                                        }
                                        role.getUndelegatablePermissions().getPermissionId().add(dp.getPermissionCode());
                                    }
                                }
                            }

                            m.getRole().add(role);
                        }
                    }

                    response.getMetadata().add(m);
                }
            }

            return response;
        } catch (SecurityException | IllegalArgumentException ex) {
            throw createException(ex, true);
        } catch (Exception ex) {
            throw createException(ex, false);
        }
    }

    private RuntimeException createException(Exception ex, boolean isWarning) {
        if (isWarning) {
            logger.warn(ex.getMessage());
        } else {
            logger.error(ex.getMessage(), ex);
        }

        if (ex instanceof RuntimeException) {
            return (RuntimeException) ex;
        }

        return new RuntimeException(ex.getMessage(), ex);
    }
}
