package ru.lanwen.wiremock.ext;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.mockito.InOrder;
import ru.lanwen.wiremock.config.CustomizationContext;
import ru.lanwen.wiremock.config.CustomizationContext.CustomizationContextBuilder;
import ru.lanwen.wiremock.config.WiremockCustomizer;
import ru.lanwen.wiremock.ext.WiremockResolver.Wiremock;

import java.lang.reflect.Parameter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static ru.lanwen.wiremock.ext.WiremockResolver.WIREMOCK_PORT;

/**
 * @author SourcePond (Roland Hauser)
 */
public class WiremockResolverUnitTest {
    private static final int EXPECTED_PORT = 8088;
    private WiremockFactory wiremockFactory = mock(WiremockFactory.class);
    private WireMockServer server = mock(WireMockServer.class);
    private CustomizationContextBuilder customizationContextBuilder = mock(CustomizationContextBuilder.class);
    private CustomizationContext customizationContext = mock(CustomizationContext.class);
    private WiremockCustomizer customizer = mock(WiremockCustomizer.class);
    private ExtensionContext extensionContext = mock(ExtensionContext.class);
    private ParameterContext parameterContext = mock(ParameterContext.class);
    private Namespace namespace = create(WiremockResolver.class);
    private Store store = mock(Store.class);
    private WiremockResolver resolver = new WiremockResolver(wiremockFactory);
    private Wiremock mockedServer;
    private Parameter serverParameter;

    private void supportedMethod(@Wiremock WireMockServer server) {

    }

    private void unsupportedMethod(WireMockServer server) {

    }

    @BeforeEach
    public void setup() throws Exception {
        serverParameter = getClass().getDeclaredMethod("supportedMethod", WireMockServer.class).getParameters()[0];
        mockedServer = serverParameter.getAnnotation(Wiremock.class);
        when(wiremockFactory.createServer(mockedServer)).thenReturn(server);
        when(wiremockFactory.createContextBuilder()).thenReturn(customizationContextBuilder);
        when(wiremockFactory.createCustomizer(mockedServer)).thenReturn(customizer);
        when(customizationContextBuilder.extensionContext(extensionContext)).thenReturn(customizationContextBuilder);
        when(customizationContextBuilder.parameterContext(parameterContext)).thenReturn(customizationContextBuilder);
        when(customizationContextBuilder.build()).thenReturn(customizationContext);
        when(server.isRunning()).thenReturn(true);
        when(server.port()).thenReturn(EXPECTED_PORT);
        when(parameterContext.getParameter()).thenReturn(serverParameter);
        when(extensionContext.getStore(namespace)).thenReturn(store);
    }

    @Test
    public void verifyDefaultConstructor() {
        // Make code coverage happy
        new WiremockResolver();
    }

    @Test
    public void afterEachServerIsNull() throws Exception {
        resolver.afterEach(extensionContext);
        verifyZeroInteractions(extensionContext);
    }

    @Test
    public void afterEachServerIsNotRunning() throws Exception {
        resolver.resolveParameter(parameterContext, extensionContext);
        when(server.isRunning()).thenReturn(false);
        resolver.afterEach(extensionContext);
        verify(extensionContext).getStore(namespace);
        verifyNoMoreInteractions(extensionContext);
    }

    @Test
    public void afterEach() throws Exception {
        resolver.resolveParameter(parameterContext, extensionContext);
        resolver.afterEach(extensionContext);
        final InOrder order = inOrder(server);
        order.verify(server).resetRequests();
        order.verify(server).resetToDefaultMappings();
        order.verify(server).stop();
    }

    @Test
    public void supportsParameter() throws Exception {
        assertTrue(resolver.supportsParameter(parameterContext, extensionContext));
        serverParameter = getClass().getDeclaredMethod("unsupportedMethod", WireMockServer.class).getParameters()[0];
        when(parameterContext.getParameter()).thenReturn(serverParameter);
        assertFalse(resolver.supportsParameter(parameterContext, extensionContext));
    }

    @Test
    public void resolveParameterFailed() throws Exception {
        final Exception expected = new Exception();
        doThrow(expected).when(customizer).customize(server, customizationContext);
        try {
            resolver.resolveParameter(parameterContext, extensionContext);
            fail("Exception expected");
        } catch (final ParameterResolutionException e) {
            assertSame(expected, e.getCause());
        }
    }

    @Test
    public void resolveParameter() throws Exception {
        assertSame(server, resolver.resolveParameter(parameterContext, extensionContext));
        final InOrder order = inOrder(server, customizer, extensionContext, store);
        order.verify(server).start();
        order.verify(customizer).customize(server, customizationContext);
        order.verify(extensionContext).getStore(namespace);
        order.verify(store).put(WIREMOCK_PORT, EXPECTED_PORT);
    }
}
