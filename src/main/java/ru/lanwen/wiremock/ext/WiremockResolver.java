package ru.lanwen.wiremock.ext;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestExtensionContext;
import ru.lanwen.wiremock.config.WiremockConfigFactory;
import ru.lanwen.wiremock.config.WiremockCustomizer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

import static java.lang.String.format;

/**
 * @author lanwen (Merkushev Kirill)
 */
@Slf4j
public class WiremockResolver implements ParameterResolver, AfterEachCallback {
    static final String WIREMOCK_PORT = "wiremock.port";

    private WireMockServer server;

    @Override
    public void afterEach(TestExtensionContext testExtensionContext) throws Exception {
        if (server == null || !server.isRunning()) {
            return;
        }

        server.resetRequests();
        server.resetToDefaultMappings();
        log.info("Stopping wiremock server on localhost:{}", server.port());
        server.stop();
    }

    @Override
    public boolean supports(ParameterContext parameterContext, ExtensionContext context) {
        return parameterContext.getParameter().isAnnotationPresent(Wiremock.class);
    }

    @Override
    public Object resolve(ParameterContext parameterContext, ExtensionContext context) {
        Validate.validState(
                !Optional.ofNullable(server).map(WireMockServer::isRunning).orElse(false),
                "Can't inject more than one server"
        );

        Wiremock mockedServer = parameterContext.getParameter().getAnnotation(Wiremock.class);


        try {
            server = new WireMockServer(
                    mockedServer.factory().newInstance().create()
            );
        } catch (ReflectiveOperationException e) {
            throw new ParameterResolutionException(
                    format("Can't create config with given factory %s", mockedServer.factory()),
                    e
            );
        }

        server.start();

        try {
            mockedServer.customizer().newInstance().customize(server);
        } catch (ReflectiveOperationException e) {
            throw new ParameterResolutionException(
                    format("Can't customize server with given customizer %s", mockedServer.customizer()),
                    e
            );
        }

        ExtensionContext.Store store = context.getStore(Namespace.create(WiremockResolver.class));
        store.put(WIREMOCK_PORT, server.port());

        log.info("Started wiremock server on localhost:{}", server.port());
        return server;
    }

    /**
     * Enables injection of wiremock server to test.
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
    }
}
