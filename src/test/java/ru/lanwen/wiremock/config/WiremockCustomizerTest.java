package ru.lanwen.wiremock.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author SourcePond (Roland Hauser)
 */
public class WiremockCustomizerTest {
    private WireMockServer server = mock(WireMockServer.class);
    private CustomizationContext customizable = mock(CustomizationContext.class);
    private WiremockCustomizer customizer = mock(WiremockCustomizer.class);

    @BeforeEach
    public void setup() throws Exception {
        doCallRealMethod().when(customizer).customize(server);
        doCallRealMethod().when(customizer).customize(server, customizable);
    }

    @Test
    public void customize() throws Exception {
        customizer.customize(server);
        verifyZeroInteractions(server, customizable);
    }

    @Test
    public void customizeWithContext() throws Exception {
        customizer.customize(server, customizable);
        verifyZeroInteractions(server, customizable);
    }
}
