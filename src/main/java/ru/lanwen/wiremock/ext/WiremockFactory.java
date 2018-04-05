package ru.lanwen.wiremock.ext;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import ru.lanwen.wiremock.config.WiremockCustomizer;
import ru.lanwen.wiremock.config.Customizable;
import ru.lanwen.wiremock.ext.WiremockResolver.Wiremock;

import static java.lang.String.format;

/**
 * @author SourcePond (Roland Hauser)
 */
class WiremockFactory {

    public WireMockServer createServer(final Wiremock mockedServer) {
        try {
            return new WireMockServer(mockedServer.factory().newInstance().create());
        } catch (ReflectiveOperationException e) {
            throw new ParameterResolutionException(
                    format("Can't createServer config with given factory %s", mockedServer.factory()),
                    e
            );
        }
    }

    public DefaultCustomizable createCustomizable() {
        return new DefaultCustomizable();
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
