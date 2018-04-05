package ru.lanwen.wiremock.config;

import ru.lanwen.wiremock.ext.WiremockResolver;


/**
 * Helps to createServer reusable customizer for injected wiremock server
 *
 * @author lanwen (Merkushev Kirill)
 * @see WiremockResolver.Wiremock
 */
public interface WiremockCustomizer {

    void customize(Customizable customizable) throws Exception;

    class NoopWiremockCustomizer implements WiremockCustomizer {
        @Override
        public void customize(final Customizable customizable) {
            // noop
        }
    }
}
