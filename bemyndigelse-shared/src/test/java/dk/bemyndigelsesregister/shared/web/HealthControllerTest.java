package dk.bemyndigelsesregister.shared.web;

import dk.bemyndigelsesregister.shared.service.SystemService;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
