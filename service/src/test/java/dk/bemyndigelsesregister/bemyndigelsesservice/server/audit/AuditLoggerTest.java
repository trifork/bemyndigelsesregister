package dk.bemyndigelsesregister.bemyndigelsesservice.server.audit;

import dk.bemyndigelsesregister.bemyndigelsesservice.server.RequestContext;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.SystemService;
import dk.nsi.fmk.auditlog.data.proto.AuditLog;
import dk.nsi.fmk.moduleframework.data.ModuleFramework;
import dk.sds.nsp.security.SecurityContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuditLoggerTest {
    @InjectMocks
    AuditLogger auditLogger = new AuditLogger(true, true) {
        @Override
        protected void sendAuditLog(ModuleFramework.RequestContext reqCtx, AuditLog.AuditLogEntry logEntry, AuditLog.AuditLogEntryId auditLogEntryId) {
            AuditLoggerTest.this.reqCtx = reqCtx;
            AuditLoggerTest.this.logEntry = logEntry;
            AuditLoggerTest.this.logEntryId = auditLogEntryId;
        }
    };

    @Mock
    SystemService systemService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        RequestContext.get().setMessageId("testId");
    }

    private ModuleFramework.RequestContext reqCtx;
    private AuditLog.AuditLogEntry logEntry;
    private AuditLog.AuditLogEntryId logEntryId;

    @Test
    public void willCreateAuditLog() {
        String TEST_CPR = "testcpr";
        String TEST_GIVENNAME = "test";
        String TEST_SURNAME = "testesen";
        String TEST_EMAIL = "test@test.com";
        String TEST_OCCUPATION = "testoccupation";
        String TEST_ROLE = "testrole";
        String TEST_AUTH = "testauth";
        String TEST_SYSTEM = "testsystem";
        String TEST_CVR = "testcvr";
        String TEST_PROVIDER = "testprovider";
        String TEST_METHOD = "willCreateAuditLog";
        String TEST_VERSION = "testversion";

//        IdCardData data = new IdCardData(IdCardType.USER, 4);
//        IdCardUserLog userLog = new IdCardUserLog(TEST_CPR, TEST_GIVENNAME, TEST_SURNAME, TEST_EMAIL, TEST_ROLE, TEST_OCCUPATION, TEST_AUTH);
//        IdCardSystemLog systemLog = new IdCardSystemLog(TEST_SYSTEM, CareProviderIdType.CVR_NUMBER, TEST_CVR, TEST_PROVIDER);
//
//        when(dgwsRequestContext.getIdCardData()).thenReturn(data);
//        when(dgwsRequestContext.getIdCardUserLog()).thenReturn(userLog);
//        when(dgwsRequestContext.getIdCardSystemLog()).thenReturn(systemLog);
        when(systemService.getImplementationVersion()).thenReturn(TEST_VERSION);
        SecurityContext securityContext = new SecurityContext() {
            @Override
            public Optional<Ticket> getTicket() {
                return Optional.empty();
            }

            @Override
            public Optional<Message> getMessage() {
                return Optional.empty();
            }

            @Override
            public Optional<User> getActingUser() {
                return Optional.of(new User() {
                    @Override
                    public UserType getUserType() {
                        return null;
                    }

                    @Override
                    public PersonIdentifierFormat getIdentifierFormat() {
                        return null;
                    }

                    @Override
                    public String getIdentifier() {
                        return TEST_CPR;
                    }

                    @Override
                    public Optional<String> getGivenName() {
                        return Optional.of(TEST_GIVENNAME);
                    }

                    @Override
                    public Optional<String> getSurname() {
                        return Optional.of(TEST_SURNAME);
                    }

                    @Override
                    public Optional<Credentials> getCredentials() {
                        return Optional.of(new Credentials() {
                            @Override
                            public Optional<String> getAuthorizationCode() {
                                return Optional.of(TEST_AUTH);
                            }

                            @Override
                            public Optional<String> getEducationCode() {
                                return Optional.empty();
                            }

                            @Override
                            public Optional<String> getNationalRole() {
                                return Optional.empty();
                            }

                            @Override
                            public Optional<String> getUnverifiedRole() {
                                return Optional.of(TEST_ROLE);
                            }

                            @Override
                            public Set<String> getPowerOfAttorneyPrivileges() {
                                return null;
                            }
                        });
                    }
                });
            }

            @Override
            public Optional<User> getPrincipalUser() {
                return Optional.empty();
            }

            @Override
            public Optional<Organisation> getOrganisation() {
                return Optional.of(new Organisation() {
                    @Override
                    public OrganisationIdentifierFormat getIdentifierFormat() {
                        return null;
                    }

                    @Override
                    public String getIdentifier() {
                        return TEST_CVR;
                    }

                    @Override
                    public Optional<String> getName() {
                        return Optional.of(TEST_PROVIDER);
                    }
                });
            }

            @Override
            public Optional<Client> getClient() {
                return Optional.of(new Client() {
                    @Override
                    public Optional<String> getName() {
                        return Optional.of(TEST_SYSTEM);
                    }
                });
            }
        };

        Object id = auditLogger.log(TEST_METHOD, TEST_CPR, securityContext);

        assertNotNull(id);
        assertNotNull(reqCtx);
        assertNotNull(logEntry);
        assertNotNull(logEntryId);
        assertEquals(TEST_METHOD, logEntry.getMethod());
//        assertEquals(4, logEntry.getAuthLevel()); //TODO: Insert line again
        assertEquals(TEST_AUTH, logEntry.getAuthorizationNumber());
        assertEquals(TEST_CVR, logEntry.getCvr());
        assertEquals(TEST_PROVIDER, logEntry.getOrganisationName());
        assertEquals(TEST_SYSTEM, logEntry.getSystem());
        assertEquals(TEST_CPR, logEntry.getCpr());
        assertEquals(TEST_ROLE, logEntry.getRole());
        assertEquals("BEM", logEntry.getServerSystemName());
        assertEquals(TEST_VERSION, logEntry.getServiceVersion());
    }
}
