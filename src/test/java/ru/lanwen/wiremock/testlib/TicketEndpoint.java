package ru.lanwen.wiremock.testlib;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.lanwen.wiremock.config.CustomizationContext;
import ru.lanwen.wiremock.config.WiremockCustomizer;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static java.lang.String.format;

/**
 * @author lanwen (Merkushev Kirill)
 */
public class TicketEndpoint implements WiremockCustomizer {

    public static final String X_TICKET_ID_HEADER = "X-Ticket-ID";
    public static final String X_TEST_METHOD_NAME_HEADER = "X-TestMethodName";

    @Override
    public void customize(WireMockServer server, CustomizationContext customizationContext) {
        ExtensionContext context = customizationContext.getExtensionContext();
        String uuid = UUID.randomUUID().toString();
        String testMethodName = context.getTestMethod().get().getName();

        server.stubFor(
                post(urlPathEqualTo("/ticket"))
                        .willReturn(aResponse()
                                .withStatus(201)
                                .withHeader(
                                        "Location",
                                        format("http://localhost:%s/ticket/%s", server.port(), uuid)
                                )
                                .withHeader(X_TEST_METHOD_NAME_HEADER, testMethodName)
                                .withHeader(X_TICKET_ID_HEADER, uuid))
        );

        server.stubFor(
                get(urlPathEqualTo("/ticket/" + uuid))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withHeader(X_TEST_METHOD_NAME_HEADER, testMethodName)
                                .withHeader(X_TICKET_ID_HEADER, uuid)
                                .withBody(format("{ \"uuid\": \"%s\" }", uuid))
                        )
        );
    }
}
