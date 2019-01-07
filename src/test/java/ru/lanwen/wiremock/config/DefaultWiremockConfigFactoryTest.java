package ru.lanwen.wiremock.config;

import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.Test;
import ru.lanwen.wiremock.config.WiremockConfigFactory.DefaultWiremockConfigFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author SourcePond (Roland Hauser)
 */
public class DefaultWiremockConfigFactoryTest {
    private final DefaultWiremockConfigFactory factory = new DefaultWiremockConfigFactory();

    @Test
    void create() {
        WireMockConfiguration config = factory.create();
        assertEquals(0, config.portNumber());
        assertEquals(Slf4jNotifier.class, config.notifier().getClass());
    }
}
