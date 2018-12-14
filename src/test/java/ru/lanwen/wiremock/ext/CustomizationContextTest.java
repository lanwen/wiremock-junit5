package ru.lanwen.wiremock.ext;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import ru.lanwen.wiremock.config.CustomizationContext;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static ru.lanwen.wiremock.config.CustomizationContext.builder;

/**
 * @author SourcePond (Roland Hauser)
 */
public class CustomizationContextTest {
    private final ExtensionContext extensionContext = mock(ExtensionContext.class);
    private final ParameterContext parameterContext = mock(ParameterContext.class);
    private final CustomizationContext customizationContext = builder()
            .parameterContext(parameterContext)
            .extensionContext(extensionContext)
            .build();

    @Test
    public void getExtensionContext() {
        assertSame(extensionContext, customizationContext.getExtensionContext());
    }

    @Test
    public void getParameterContext() {
        assertSame(parameterContext, customizationContext.getParameterContext());
    }

    @Test
    public void verifyToString() {
        final String toString = customizationContext.toString();
        assertTrue(toString.contains(CustomizationContext.class.getSimpleName()));
        assertTrue(toString.contains("extensionContext"));
        assertTrue(toString.contains("parameterContext"));
    }
}
