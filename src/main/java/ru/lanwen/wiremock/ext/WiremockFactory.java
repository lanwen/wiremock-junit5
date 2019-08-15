package ru.lanwen.wiremock.ext;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import ru.lanwen.wiremock.config.CustomizationContext;
import ru.lanwen.wiremock.config.CustomizationContext.CustomizationContextBuilder;
import ru.lanwen.wiremock.config.WiremockConfigFactory;
import ru.lanwen.wiremock.config.WiremockCustomizer;
import ru.lanwen.wiremock.ext.WiremockResolver.Wiremock;

import static java.lang.String.format;

/**
 * @author SourcePond (Roland Hauser)
 */
class WiremockFactory {

    public WireMockServer createServer(final Wiremock mockedServer) {
        return new WireMockServer(createFactoryInstance(mockedServer).create());
    }

    public WireMockServer createServer(final Wiremock mockedServer, ExtensionContext extensionContext) {
        return new WireMockServer(createFactoryInstance(mockedServer).create(extensionContext));
    }

    private WiremockConfigFactory createFactoryInstance(final Wiremock mockedServer) {
        try {
            return mockedServer.factory().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new ParameterResolutionException(
                    format("Can't create config with given factory %s", mockedServer.factory()),
                    e
            );
        }
    }

    public CustomizationContextBuilder createContextBuilder() {
        return CustomizationContext.builder();
    }

    public WiremockCustomizer createCustomizer(final Wiremock mockedServer) {
        try {
            return mockedServer.customizer().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new ParameterResolutionException(
                    format("Can't customize server with given customizer %s", mockedServer.customizer()),
                    e
            );
        }
    }
}
