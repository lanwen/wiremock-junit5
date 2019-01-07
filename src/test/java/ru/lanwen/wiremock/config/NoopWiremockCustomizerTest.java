package ru.lanwen.wiremock.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Test;
import ru.lanwen.wiremock.config.WiremockCustomizer.NoopWiremockCustomizer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author SourcePond (Roland Hauser)
 */
public class NoopWiremockCustomizerTest {
    private WireMockServer server = mock(WireMockServer.class);
    private CustomizationContext customizable = mock(CustomizationContext.class);
    private WiremockCustomizer customizer = new NoopWiremockCustomizer();

    @Test
    void customize() throws Exception {
        customizer.customize(server);
        verifyZeroInteractions(server, customizable);
    }

    @Test
    void customizeWithContext() throws Exception {
        customizer.customize(server, customizable);
        verifyZeroInteractions(server, customizable);
    }
}
