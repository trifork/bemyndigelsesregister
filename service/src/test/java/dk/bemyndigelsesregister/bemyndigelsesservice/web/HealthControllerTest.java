package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import dk.bemyndigelsesregister.bemyndigelsesservice.server.SystemService;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class HealthControllerTest {
    SystemService systemService = Mockito.mock(SystemService.class);

    HealthController healthController = new HealthController() {{
        systemService = HealthControllerTest.this.systemService;
    }};

    @Test
    public void healthWillReturnOK() throws Exception {
        assertEquals("OK", healthController.health());
    }

    @Test
    public void willReturnCommitVersion() throws Exception {
        when(systemService.getImplementationBuild()).thenReturn("CommitSHA");
        assertEquals("CommitSHA", systemService.getImplementationBuild());
    }
}
