package ru.lanwen.wiremock.ext;


import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import ru.lanwen.wiremock.config.Customizable;

/**
 * @author SourcePond (Roland Hauser)
 */
class DefaultCustomizable implements Customizable {
    private WireMockServer server;
    private ExtensionContext extensionContext;
    private ParameterContext parameterContext;

    public void setServer(WireMockServer server) {
        this.server = server;
    }

    public void setExtensionContext(ExtensionContext extensionContext) {
        this.extensionContext = extensionContext;
    }

    public void setParameterContext(ParameterContext parameterContext) {
        this.parameterContext = parameterContext;
    }

    @Override
    public WireMockServer getServer() {
        return server;
    }

    @Override
    public ExtensionContext getExtensionContext() {
        return extensionContext;
    }

    @Override
    public ParameterContext getParameterContext() {
        return parameterContext;
    }
}
