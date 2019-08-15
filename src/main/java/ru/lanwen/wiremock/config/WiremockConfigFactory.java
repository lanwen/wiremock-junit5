package ru.lanwen.wiremock.config;

import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.lanwen.wiremock.ext.WiremockResolver;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

/**
 * You can create custom config to init wiremock server in test.
 *
 * @author lanwen (Merkushev Kirill)
 * @see WiremockResolver.Wiremock
 */
public interface WiremockConfigFactory {

    /**
     * Create config to be used by injected to test method wiremock
     *
     * @return config for wiremock
     */
    default WireMockConfiguration create() {
        return options().dynamicPort().notifier(new Slf4jNotifier(true));
    }

    default WireMockConfiguration create(ExtensionContext context) {
        return create();
    }

    /**
     * By default creates config with dynamic port only and notifier.
     */
    class DefaultWiremockConfigFactory implements WiremockConfigFactory {}

    /**
     * By default creates config with dynamic port only, notifier and Templating Response enabled.
     */
    class ResponseTemplateTransformerWireMockConfigFactory extends DefaultWiremockConfigFactory {

        @Override
        public WireMockConfiguration create() {
            return super.create()
                    // enable Templating Response!
                    // @see : http://wiremock.org/docs/response-templating/
                    .extensions(new ResponseTemplateTransformer(true));
        }
    }
}
