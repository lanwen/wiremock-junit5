package ru.lanwen.wiremock.config;

import org.junit.jupiter.api.Test;
import ru.lanwen.wiremock.config.WiremockCustomizer.NoopWiremockCustomizer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author SourcePond (Roland Hauser)
 */
public class NoopWiremockCustomizerTest {
    private Customizable customizable = mock(Customizable.class);
    private NoopWiremockCustomizer customizer = new NoopWiremockCustomizer();

    @Test
    public void customize() {
        customizer.customize(customizable);
        verifyZeroInteractions(customizable);
    }
}
