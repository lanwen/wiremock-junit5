package ru.lanwen.wiremock.ext;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

/**
 * @author SourcePond (Roland Hauser)
 */
public class DefaultCustomizableTest {
    private WireMockServer server = mock(WireMockServer.class);
    private ExtensionContext extensionContext = mock(ExtensionContext.class);
    private ParameterContext parameterContext = mock(ParameterContext.class);
    private DefaultCustomizable customizable = new DefaultCustomizable();

    @Test
    public void getServer() {
        customizable.setServer(server);
        assertSame(server, customizable.getServer());
    }

    @Test
    public void getExtensionContext() {
        customizable.setExtensionContext(extensionContext);
        assertSame(extensionContext, customizable.getExtensionContext());
    }

    @Test
    public void getParameterContext() {
        customizable.setParameterContext(parameterContext);
        assertSame(parameterContext, customizable.getParameterContext());
    }
}
