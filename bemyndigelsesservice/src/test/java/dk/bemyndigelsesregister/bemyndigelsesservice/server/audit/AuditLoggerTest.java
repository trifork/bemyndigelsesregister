package dk.bemyndigelsesregister.bemyndigelsesservice.server.audit;

import com.trifork.dgws.*;
import dk.bemyndigelsesregister.shared.service.SystemService;
import dk.nsi.fmk.auditlog.data.proto.AuditLog;
import dk.nsi.fmk.moduleframework.data.ModuleFramework;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

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
    DgwsRequestContext dgwsRequestContext;

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
        String TEST_ROLE = "testrole";
        String TEST_AUTH = "testauth";
        String TEST_SYSTEM = "testsystem";
        String TEST_CVR = "testcvr";
        String TEST_PROVIDER = "testprovider";
        String TEST_METHOD = "willCreateAuditLog";
        String TEST_BUILD = "testbuild";

        IdCardData data = new IdCardData(IdCardType.USER, 4);
        IdCardUserLog userLog = new IdCardUserLog(TEST_CPR, TEST_GIVENNAME, TEST_SURNAME, TEST_EMAIL, TEST_ROLE, null, TEST_AUTH);
        IdCardSystemLog systemLog = new IdCardSystemLog(TEST_SYSTEM, CareProviderIdType.CVR_NUMBER, TEST_CVR, TEST_PROVIDER);

        when(dgwsRequestContext.getIdCardData()).thenReturn(data);
        when(dgwsRequestContext.getIdCardUserLog()).thenReturn(userLog);
        when(dgwsRequestContext.getIdCardSystemLog()).thenReturn(systemLog);
        when(systemService.getImplementationBuild()).thenReturn(TEST_BUILD);

        Object id = auditLogger.log(TEST_METHOD);

        assertNotNull(id);
        assertNotNull(reqCtx);
        assertNotNull(logEntry);
        assertNotNull(logEntryId);
        assertEquals(TEST_METHOD, logEntry.getMethod());
        assertEquals(4, logEntry.getAuthLevel());
        assertEquals(TEST_AUTH, logEntry.getAuthorizationNumber());
        assertEquals(TEST_CVR, logEntry.getCvr());
        assertEquals(TEST_PROVIDER, logEntry.getOrganisationName());
        assertEquals(TEST_SYSTEM, logEntry.getSystem());
        assertEquals(TEST_CPR, logEntry.getCpr());
        assertEquals(TEST_ROLE, logEntry.getRole());
        assertEquals(TEST_EMAIL, logEntry.getAdditionalUserInfo());
        assertEquals("BEM", reqCtx.getClientSystem());
        assertEquals(TEST_BUILD, reqCtx.getClientVersionOfService());
    }
}
