package ru.lanwen.wiremock.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import ru.lanwen.wiremock.ext.WiremockResolver;


/**
 * Helps to create reusable customizer for injected wiremock server
 *
 * @author lanwen (Merkushev Kirill)
 * @see WiremockResolver.Wiremock
 */
public interface WiremockCustomizer {

    default void customize(WireMockServer server) throws Exception {
        // noop
    }

    default void customize(WireMockServer server, CustomizationContext ctx) throws Exception {
        customize(server);
    }

    class NoopWiremockCustomizer implements WiremockCustomizer {

    }
}
