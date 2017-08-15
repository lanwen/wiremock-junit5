package ru.lanwen.wiremock.ext;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author lanwen (Merkushev Kirill)
 */
public class WiremockUriResolver implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().isAnnotationPresent(WiremockUri.class)
                && String.class.isAssignableFrom(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext context) {
        ExtensionContext.Store store = context.getStore(ExtensionContext.Namespace.create(WiremockResolver.class));

        return "http://localhost:" + store.get(WiremockResolver.WIREMOCK_PORT);
    }


    /**
     * To target host:port injection
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface WiremockUri {
    }
}
