package ru.lanwen.wiremock.ext;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import ru.lanwen.wiremock.config.CustomizationContext;
import ru.lanwen.wiremock.config.WiremockConfigFactory;
import ru.lanwen.wiremock.config.WiremockCustomizer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static ru.lanwen.wiremock.ext.Validate.validState;
import static ru.lanwen.wiremock.ext.WiremockResolver.Wiremock.StopServer.AFTER_ALL;
import static ru.lanwen.wiremock.ext.WiremockResolver.Wiremock.StopServer.AFTER_EACH;

/**
 * @author lanwen (Merkushev Kirill)
 */
@Slf4j
public class WiremockResolver implements ParameterResolver, AfterEachCallback, AfterAllCallback {
    static final String WIREMOCK_PORT = "wiremock.port";

    private final WiremockFactory wiremockFactory;
    protected WireMockServer server;
    protected Wiremock.StopServer stopServer;

    public WiremockResolver() {
        this(new WiremockFactory());
    }

    WiremockResolver(final WiremockFactory wiremockFactory) {
        this.wiremockFactory = wiremockFactory;
    }

    @Override
    public void afterEach(ExtensionContext testExtensionContext) {
        if (AFTER_EACH == stopServer) {
            stopServerWhenIsRunning();
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        if (AFTER_ALL == stopServer) {
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

        stopServer = mockedServer.stopserver();

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
         * @return whenever to stop the server: AFTER_EACH or AFTER_ALL
         */
        StopServer stopserver() default AFTER_EACH;

        /**
         * Defines whenever to stop the server: AFTER_EACH or AFTER_ALL
         */
        enum StopServer {
            /**
             * Should stop the server after each test method has been invoked.
             */
            AFTER_EACH,

            /**
             * Should stop the server after all tests have been invoked.
             */
            AFTER_ALL
        }
    }
}
