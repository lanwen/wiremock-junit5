package ru.lanwen.wiremock.config;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;

/**
 * @author SourcePond (Roland Hauser)
 */
@Value
@Builder
@NonFinal
public class CustomizationContext {
    ExtensionContext extensionContext;
    ParameterContext parameterContext;
}
