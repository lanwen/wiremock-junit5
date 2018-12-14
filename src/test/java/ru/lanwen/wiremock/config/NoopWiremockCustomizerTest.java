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
    private final WireMockServer server = mock(WireMockServer.class);
    private final CustomizationContext customizable = mock(CustomizationContext.class);
    private final WiremockCustomizer customizer = new NoopWiremockCustomizer();

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
