package ru.lanwen.wiremock.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;

/**
 * @author SourcePond (Roland Hauser)
 */
public interface Customizable {

    WireMockServer getServer();

    ExtensionContext getExtensionContext();

    ParameterContext getParameterContext();
}
