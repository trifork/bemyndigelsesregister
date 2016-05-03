package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import com.trifork.dgws.*;
import com.trifork.dgws.annotations.Protected;
import dk.bemyndigelsesregister.bemyndigelsesservice.BemyndigelsesService;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Metadata;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.DelegationManager;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.MetadataManager;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ServiceTypeMapper;
import dk.bemyndigelsesregister.shared.service.SystemService;
import dk.nsi.bemyndigelse._2016._01._01.*;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapHeader;

import javax.inject.Inject;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;

@Repository("bemyndigelsesService")
@Endpoint
public class BemyndigelsesServiceImpl implements BemyndigelsesService {
    private static Logger logger = Logger.getLogger(BemyndigelsesServiceImpl.class);
    @Inject
    SystemService systemService;
    @Inject
    DelegationManager delegationManager;
    @Inject
    MetadataManager metadataManager;
    @Inject
    DgwsRequestContext dgwsRequestContext;
    @Inject
    ServiceTypeMapper typeMapper;

    @Inject
    WhitelistChecker whitelistChecker;


    public BemyndigelsesServiceImpl() {
    }

    void authorizeOperationForCpr(String whitelist, String errorMessage, String... authorizedCprs) {
        Set<String> authorizedCprSet = new HashSet<>(Arrays.asList(authorizedCprs));
        IdCardData idCardData = dgwsRequestContext.getIdCardData();
        if (idCardData.getIdCardType() == IdCardType.SYSTEM) {
            IdCardSystemLog systemLog = dgwsRequestContext.getIdCardSystemLog();
            if (systemLog.getCareProviderIdType() != CareProviderIdType.CVR_NUMBER) {
                throw new IllegalAccessError("Attempted to access operation using system id card, but the CareProviderIdType was not CVR, it was " + systemLog.getCareProviderIdType());
            }
            String cvr = systemLog.getCareProviderId();
            if (!whitelistChecker.isSystemWhitelisted(whitelist, cvr)) {
                throw new IllegalAccessError("Attempted to access operation using system id card, but the whitelist " + whitelist + " did not contain id card CVR [" + cvr + "]");
            }
        } else if (idCardData.getIdCardType() == IdCardType.USER) {
            IdCardUserLog userLog = dgwsRequestContext.getIdCardUserLog();
            if (userLog == null || !authorizedCprSet.contains(userLog.cpr)) {
                logger.info("Failed to authorize user id card. Authorized CPRs: " + authorizedCprSet + ". CPR in ID card: [" + (userLog != null ? userLog.cpr : null) + "]");
                throw new IllegalAccessError(errorMessage);
            }
        } else {
            throw new IllegalAccessError("Could not authorize ID card, it was neither a user or system id card");
        }
    }

    private DateTime nullableDateTime(XMLGregorianCalendar xmlDate) {
        return xmlDate != null ? new DateTime(xmlDate.toGregorianCalendar(), DateTimeZone.UTC) : null;
    }

    @Override
    @Protected
    @Transactional
    @ResponsePayload
    public CreateDelegationsResponse createDelegations(@RequestPayload CreateDelegationsRequest request, SoapHeader soapHeader) {
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
                    createDelegation.getState(),
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
    @Protected
    @Transactional
    @ResponsePayload
    public GetDelegationsResponse getDelegations(@RequestPayload GetDelegationsRequest request, SoapHeader soapHeader) {
        Collection<Delegation> delegations = new ArrayList<>();

        String delegatorCpr = request.getDelegatorCpr();
        String delegateeCpr = request.getDelegateeCpr();
        String delegationId = request.getDelegationId();

        // check arguments
        if ((delegatorCpr != null ? 1 : 0) + (delegateeCpr != null ? 1 : 0) + (delegationId != null ? 1 : 0) != 1) {
            throw new IllegalArgumentException("A single argument must be supplied, i.e. exactly one of delegatorCpr, delegateeCpr or delegationId must not be null");
        }

        // invoke correct method on manager
        if (delegatorCpr != null) {
            List<Delegation> list = delegationManager.getDelegationsByDelegatorCpr(delegatorCpr);
            if (list != null) {
                delegations.addAll(list);
            }
        } else if (delegateeCpr != null) {
            List<Delegation> list = delegationManager.getDelegationsByDelegateeCpr(delegateeCpr);
            if (list != null) {
                delegations.addAll(list);
            }
        } else {
            Delegation d = delegationManager.getDelegation(delegationId);
            if (d != null) {
                delegations.add(d);
            }
        }

        // return the result
        final GetDelegationsResponse response = new GetDelegationsResponse();
        for (Delegation delegation : delegations) {
            if (delegation.getEffectiveTo() == null || delegation.getEffectiveFrom().isBefore(delegation.getEffectiveTo()))
                response.getDelegation().add(typeMapper.toDelegationType(delegation));
        }
        return response;
    }

    @Override
    @Protected
    @Transactional
    @ResponsePayload
    public DeleteDelegationsResponse deleteDelegations(@RequestPayload DeleteDelegationsRequest request, SoapHeader soapHeader) {
        String delegatorCpr = request.getDelegatorCpr();
        String delegateeCpr = request.getDelegateeCpr();
        List<String> delegationIds = request.getListOfDelegationIds().getDelegationId();
        XMLGregorianCalendar xmlDate = request.getDeletionDate();
        DateTime deletionDate = xmlDate == null ? null : new DateTime(xmlDate.toGregorianCalendar().getTimeInMillis());

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
        if (deletionDate != null && deletionDate.isBeforeNow())
            throw new IllegalArgumentException("DeletionDate cannot be in the past");

        // authorize
        if (delegatorCpr != null)
            authorizeOperationForCpr("deleteDelegations", "IDCard CPR was different from DelegatorCpr", delegatorCpr);
        else
            authorizeOperationForCpr("deleteDelegations", "IDCard CPR was different from DelegateeCpr", delegateeCpr);

        // invoke manager
        List<String> result = new LinkedList<>();
        for (String delegationId : delegationIds) {
            String deleted = delegationManager.deleteDelegation(delegatorCpr, delegateeCpr, delegationId, deletionDate);
            if (deleted != null) result.add(deleted);
        }

        // return result
        final DeleteDelegationsResponse response = new DeleteDelegationsResponse();
        response.getDelegationId().addAll(result);
        return response;
    }

    @Override
    @Transactional
    @ResponsePayload
    @Protected(whitelist = "bemyndigelsesservice.indlaesMetadata")
    public PutMetadataResponse putMetadata(@RequestPayload PutMetadataRequest request, SoapHeader soapHeader) {
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
}
