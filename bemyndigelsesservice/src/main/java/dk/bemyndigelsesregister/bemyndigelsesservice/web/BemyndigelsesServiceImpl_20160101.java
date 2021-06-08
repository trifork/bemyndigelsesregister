package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import com.trifork.dgws.annotations.Protected;
import dk.bemyndigelsesregister.bemyndigelsesservice.BemyndigelsesService;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Metadata;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Status;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.RequestContext;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.RequestType;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ServiceTypeMapper_20160101;
import dk.nsi.bemyndigelse._2016._01._01.*;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapHeader;

import javax.inject.Inject;
import java.util.*;

@Repository("bemyndigelsesService")
@Endpoint
public class BemyndigelsesServiceImpl_20160101 extends AbstractServiceImpl implements BemyndigelsesService {
    private static Logger logger = Logger.getLogger(BemyndigelsesServiceImpl_20160101.class);

    @Inject
    ServiceTypeMapper_20160101 typeMapper;

    public BemyndigelsesServiceImpl_20160101() {
        super(logger);
    }

    @Override
    @Protected(minAuthLevel = 4)
    @Transactional
    @ResponsePayload
    public CreateDelegationsResponse createDelegations(@RequestPayload CreateDelegationsRequest request, SoapHeader soapHeader) {
        RequestContext.get().setRequestType(RequestType.CREATE);

        // auditlog call - one for each delegatee
        Set<String> delegateeCprs = new HashSet<>();
        for (CreateDelegationsRequest.Create createDelegation : request.getCreate()) {
            delegateeCprs.add(createDelegation.getDelegateeCpr());
        }
        for (String delegateeCpr : delegateeCprs) {
            auditLogger.log("Opret bemyndigelser", delegateeCpr);
        }
        logCallOfOldService("Opret bemyndigelser");

        Collection<Delegation> delegations = new ArrayList<>();

        for (CreateDelegationsRequest.Create createDelegation : request.getCreate()) {
            if (createDelegation.getState().equals(State.ANMODET)) {
                authorizeOperationForCpr("createDelegation", "IDCard CPR was different from both DelegatorCpr and DelegateeCpr", createDelegation.getDelegatorCpr(), createDelegation.getDelegateeCpr());
            } else {
                authorizeOperationForCpr("createDelegation", "IDCard CPR was different from DelegatorCpr", createDelegation.getDelegatorCpr());
            }
            logger.debug("Creating Delegation: " + createDelegation.toString());
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
            logger.debug("Got delegation with code = [" + delegation.getCode() + "]");

            delegations.add(delegation);
        }

        final CreateDelegationsResponse response = new CreateDelegationsResponse();
        for (Delegation delegation : delegations) {
            response.getDelegation().add(typeMapper.toDelegationType(delegation));
        }
        return response;
    }

    @Override
    @Protected(minAuthLevel = 4)
    @Transactional
    @ResponsePayload
    public GetDelegationsResponse getDelegations(@RequestPayload GetDelegationsRequest request, SoapHeader soapHeader) {
        RequestContext.get().setRequestType(RequestType.GET_METADATA);

        logCallOfOldService("Hent bemyndigelser");

        Collection<Delegation> delegations = getDelegationsCommon(request.getDelegatorCpr(), request.getDelegateeCpr(), request.getDelegationId());

        final GetDelegationsResponse response = new GetDelegationsResponse();
        for (Delegation delegation : delegations) {
            response.getDelegation().add(typeMapper.toDelegationType(delegation));
        }
        return response;
    }

    @Override
    @Protected(minAuthLevel = 4)
    @Transactional
    @ResponsePayload
    public DeleteDelegationsResponse deleteDelegations(@RequestPayload DeleteDelegationsRequest request, SoapHeader soapHeader) {
        RequestContext.get().setRequestType(RequestType.DELETE);

        logCallOfOldService("Slet bemyndigelser");

        List<String> result = deleteDelegationsCommon(request.getDelegatorCpr(), request.getDelegateeCpr(), request.getListOfDelegationIds().getDelegationId(), request.getDeletionDate());

        final DeleteDelegationsResponse response = new DeleteDelegationsResponse();
        response.getDelegationId().addAll(result);
        return response;
    }

    @Override
    @Transactional
    @ResponsePayload
    // @Protected(whitelist = "bemyndigelsesservice.indlaesMetadata")
    public PutMetadataResponse putMetadata(@RequestPayload PutMetadataRequest request, SoapHeader soapHeader) {
        RequestContext.get().setRequestType(RequestType.PUT_METADATA);

        auditLogger.log("Indlæs metadata", null);

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
    }

    @Override
    @ResponsePayload
    public GetMetadataResponse getMetadata(@RequestPayload GetMetadataRequest request, SoapHeader soapHeader) {
        RequestContext.get().setRequestType(RequestType.GET_METADATA);

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
    }

    private void logCallOfOldService(String method) {
        if (dgwsRequestContext != null && dgwsRequestContext.getIdCardSystemLog() != null) {
            logger.warn("bemyndigelse_2016_01_01 called. Method=" + method + " System=" + dgwsRequestContext.getIdCardSystemLog().getItSystemName() + " Careprovider=" + dgwsRequestContext.getIdCardSystemLog().getCareProviderId() + " " + dgwsRequestContext.getIdCardSystemLog().getCareProviderName());
        }
    }
}
