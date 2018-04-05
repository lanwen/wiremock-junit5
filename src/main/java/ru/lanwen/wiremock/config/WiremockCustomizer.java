package ru.lanwen.wiremock.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.lanwen.wiremock.ext.WiremockResolver;


/**
 * Helps to create reusable customizer for injected wiremock server
 *
 * @author lanwen (Merkushev Kirill)
 * @see WiremockResolver.Wiremock
 */
public interface WiremockCustomizer {

    void customize(WireMockServer server, ExtensionContext context);

    class NoopWiremockCustomizer implements WiremockCustomizer {
        @Override
        public void customize(final WireMockServer server, final ExtensionContext context) {
            // noop
        }
    }
}
