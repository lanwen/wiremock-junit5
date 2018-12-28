package ru.lanwen.wiremock.config;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author SourcePond (Roland Hauser)
 */
public class ResponseTemplateTransformerWireMockConfigFactoryTest {
    private WiremockConfigFactory.ResponseTemplateTransformerWireMockConfigFactory factory = new WiremockConfigFactory.ResponseTemplateTransformerWireMockConfigFactory();

    @Test
    public void create() {
        WireMockConfiguration config = factory.create();

        Map<String, ResponseTemplateTransformer> map = config.extensionsOfType(ResponseTemplateTransformer.class);
        assertFalse(map.isEmpty());

        ResponseTemplateTransformer transformer = map.get("response-template");
        assertNotNull(transformer);

        assertTrue(transformer.applyGlobally());
    }
}
