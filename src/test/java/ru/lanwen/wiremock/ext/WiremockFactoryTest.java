package ru.lanwen.wiremock.ext;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import ru.lanwen.wiremock.config.CustomizationContext.CustomizationContextBuilder;
import ru.lanwen.wiremock.config.WiremockConfigFactory;
import ru.lanwen.wiremock.config.WiremockCustomizer;
import ru.lanwen.wiremock.ext.WiremockResolver.Wiremock;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author SourcePond (Roland Hauser)
 */
public class WiremockFactoryTest {

    public static class StubClass implements WiremockConfigFactory, WiremockCustomizer {

        @Override
        public WireMockConfiguration create() {
            return OPTIONS;
        }

        @Override
        public void customize(WireMockServer server) {
            // noop
        }
    }

    private static class PrivateClassNotAllowed implements WiremockConfigFactory, WiremockCustomizer {

        @Override
        public WireMockConfiguration create() {
            return null;
        }

        @Override
        public void customize(WireMockServer server) {
            // noop
        }
    }

    private static final WireMockConfiguration OPTIONS = options();
    private final Wiremock mockedServer = mock(Wiremock.class);
    private final WiremockFactory factory = new WiremockFactory();

    @BeforeEach
    void setup() {
        when(mockedServer.factory()).thenReturn((Class) StubClass.class);
        when(mockedServer.customizer()).thenReturn((Class) StubClass.class);
    }

    @Test
    void createServer() {
        WireMockServer srv1 = factory.createServer(mockedServer);
        WireMockServer srv2 = factory.createServer(mockedServer);
        assertNotNull(srv1);
        assertNotNull(srv2);
        assertSame(OPTIONS, srv1.getOptions());
        assertSame(OPTIONS, srv2.getOptions());
        assertNotSame(srv1, srv2);
    }

    @Test
    void configFactoryCouldNotBeInstantiated() {
        when(mockedServer.factory()).thenReturn((Class) PrivateClassNotAllowed.class);
        assertThrows(ParameterResolutionException.class,
                () -> factory.createServer(mockedServer),
                "Can't create config with given factory class ru.lanwen.wiremock.ext.WiremockFactoryTest$PrivateClassNotAllowed");
    }

    @Test
    void createContextBuilder() {
        CustomizationContextBuilder builder1 = factory.createContextBuilder();
        CustomizationContextBuilder builder2 = factory.createContextBuilder();
        assertNotNull(builder1);
        assertNotNull(builder2);
        assertNotSame(builder1, builder2);
    }

    @Test
    void createCustomizer() {
        WiremockCustomizer customizer1 = factory.createCustomizer(mockedServer);
        WiremockCustomizer customizer2 = factory.createCustomizer(mockedServer);
        assertNotNull(customizer1);
        assertNotNull(customizer2);
        assertNotSame(customizer1, customizer2);
    }

    @Test
    void customizerCouldNotBeInstantiated() {
        when(mockedServer.customizer()).thenReturn((Class) PrivateClassNotAllowed.class);
        assertThrows(ParameterResolutionException.class,
                () -> factory.createCustomizer(mockedServer),
                "Can't customize server with given customizer class ru.lanwen.wiremock.ext.WiremockFactoryTest$PrivateClassNotAllowed");
    }
}
