package ru.lanwen.wiremock.ext;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.lanwen.wiremock.config.CustomizationContext;
import ru.lanwen.wiremock.config.CustomizationContext.CustomizationContextBuilder;
import ru.lanwen.wiremock.config.WiremockCustomizer;
import ru.lanwen.wiremock.ext.WiremockResolver.Wiremock;

import java.lang.reflect.Parameter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

/**
 * @author SourcePond (Roland Hauser)
 */
@ExtendWith(MockitoExtension.class)
public class WiremockResolverUnitTest {
    @Mock
    private WiremockFactory wiremockFactory;
    @Mock
    private WireMockServer server;
    @Mock
    private CustomizationContextBuilder customizationContextBuilder;
    @Mock
    private CustomizationContext customizationContext;
    @Mock
    private WiremockCustomizer customizer;
    @Mock
    private ExtensionContext extensionContext;
    @Mock
    private ParameterContext parameterContext;
    @Mock
    private Wiremock mockedServer;
    private Parameter serverParameter;
    private WiremockResolver resolver;

    private void supportedMethod(@Wiremock WireMockServer server) {

    }

    private void unsupportedMethod(WireMockServer server) {

    }

    @BeforeEach
    void setup() throws Exception {
        serverParameter = getClass().getDeclaredMethod("supportedMethod", WireMockServer.class).getParameters()[0];
        mockedServer = serverParameter.getAnnotation(Wiremock.class);
        resolver = new WiremockResolver(wiremockFactory);
    }

    @Test
    void verifyDefaultConstructor() {
        // Make code coverage happy
        new WiremockResolver();
    }

    @Test
    void afterEachServerIsNull() throws Exception {
        resolver.afterEach(extensionContext);
        verifyZeroInteractions(extensionContext);
    }

    @Test
    void afterEachServerWhenNotNullButNotRunning() throws Exception {
        when(server.isRunning()).thenReturn(false);

        // set server directly avoiding to call method resolver.resolveParameter()
        resolver.server = server;

        resolver.afterEach(extensionContext);
        verifyZeroInteractions(extensionContext);
        verify(server).isRunning();
    }

    @Test
    void supportsParameter() throws Exception {
        when(parameterContext.getParameter()).thenReturn(serverParameter);

        assertTrue(resolver.supportsParameter(parameterContext, extensionContext));
        serverParameter = getClass().getDeclaredMethod("unsupportedMethod", WireMockServer.class).getParameters()[0];
        when(parameterContext.getParameter()).thenReturn(serverParameter);
        assertFalse(resolver.supportsParameter(parameterContext, extensionContext));
    }

    @Test
    void resolveParameterFailed() throws Exception {
        when(wiremockFactory.createServer(mockedServer)).thenReturn(server);
        when(wiremockFactory.createContextBuilder()).thenReturn(customizationContextBuilder);
        when(wiremockFactory.createCustomizer(mockedServer)).thenReturn(customizer);
        when(customizationContextBuilder.extensionContext(extensionContext)).thenReturn(customizationContextBuilder);
        when(customizationContextBuilder.parameterContext(parameterContext)).thenReturn(customizationContextBuilder);
        when(customizationContextBuilder.build()).thenReturn(customizationContext);
        when(parameterContext.getParameter()).thenReturn(serverParameter);

        final Exception expected = new Exception();
        doThrow(expected).when(customizer).customize(server, customizationContext);
        try {
            resolver.resolveParameter(parameterContext, extensionContext);
            fail("Exception expected");
        } catch (final ParameterResolutionException e) {
            assertSame(expected, e.getCause());
        }
    }
}
