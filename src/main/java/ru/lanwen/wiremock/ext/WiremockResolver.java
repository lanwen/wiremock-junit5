package ru.lanwen.wiremock.ext;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import ru.lanwen.wiremock.config.CustomizationContext;
import ru.lanwen.wiremock.config.WiremockConfigFactory;
import ru.lanwen.wiremock.config.WiremockCustomizer;
import ru.lanwen.wiremock.config.WiremockShutdownStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static ru.lanwen.wiremock.config.WiremockShutdownStrategy.AFTER_ALL;
import static ru.lanwen.wiremock.config.WiremockShutdownStrategy.AFTER_EACH;
import static ru.lanwen.wiremock.ext.Validate.validState;

/**
 * @author lanwen (Merkushev Kirill)
 */
@Slf4j
public class WiremockResolver implements ParameterResolver, AfterEachCallback, AfterAllCallback {
    static final String WIREMOCK_PORT = "wiremock.port";

    private final WiremockFactory wiremockFactory;
    protected WireMockServer server;
    protected WiremockShutdownStrategy shutdownStrategy;

    public WiremockResolver() {
        this(new WiremockFactory());
    }

    WiremockResolver(final WiremockFactory wiremockFactory) {
        this.wiremockFactory = wiremockFactory;
    }

    @Override
    public void afterEach(ExtensionContext testExtensionContext) {
        if (AFTER_EACH == shutdownStrategy) {
            stopServerWhenIsRunning();
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        if (AFTER_ALL == shutdownStrategy) {
            stopServerWhenIsRunning();
        }
    }

    private void stopServerWhenIsRunning() {
        if (server != null && server.isRunning()) {
            server.resetRequests();
            server.resetToDefaultMappings();
            log.info("Stopping Wiremock server on localhost:{}", server.port());
            server.stop();
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext context) {
        return parameterContext.getParameter().isAnnotationPresent(Wiremock.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        validState(
                !ofNullable(server).map(WireMockServer::isRunning).orElse(false),
                "Can't inject more than one server"
        );

        Wiremock mockedServer = parameterContext.getParameter().getAnnotation(Wiremock.class);

        shutdownStrategy = mockedServer.shutdownStrategy();

        server = wiremockFactory.createServer(mockedServer);
        server.start();

        CustomizationContext customizationContext = wiremockFactory.createContextBuilder()
                .parameterContext(parameterContext)
                .extensionContext(extensionContext)
                .build();

        try {
            wiremockFactory.createCustomizer(mockedServer).customize(server, customizationContext);
        } catch (Exception e) {
            throw new ParameterResolutionException(
                    format("Can't customize server with given customizer %s", mockedServer.customizer()),
                    e
            );
        }

        ExtensionContext.Store store = extensionContext.getStore(Namespace.create(WiremockResolver.class));
        store.put(WIREMOCK_PORT, server.port());

        log.info("Started Wiremock server on localhost:{}", server.port());
        return server;
    }

    /**
     * Enables injection of Wiremock server to test.
     * Helps to configure instance with {@link #factory} and {@link #customizer} methods
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Wiremock {
        /**
         * @return class which defines on how to create config
         */
        Class<? extends WiremockConfigFactory> factory() default WiremockConfigFactory.DefaultWiremockConfigFactory.class;

        /**
         * @return class which defines on how to customize server after start
         */
        Class<? extends WiremockCustomizer> customizer() default WiremockCustomizer.NoopWiremockCustomizer.class;

        /**
         * @return whenever to shutdown the server: AFTER_EACH or AFTER_ALL
         */
        WiremockShutdownStrategy shutdownStrategy() default WiremockShutdownStrategy.AFTER_EACH;
    }
}
