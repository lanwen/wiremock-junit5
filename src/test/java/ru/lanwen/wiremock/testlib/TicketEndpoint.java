package ru.lanwen.wiremock.testlib;

import com.github.tomakehurst.wiremock.WireMockServer;
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

    @Override
    public void customize(WireMockServer server) {
        String uuid = UUID.randomUUID().toString();

        server.stubFor(
                post(urlPathEqualTo("/ticket"))
                        .willReturn(aResponse()
                                .withStatus(201)
                                .withHeader(
                                        "Location",
                                        format("http://localhost:%s/ticket/%s", server.port(), uuid)
                                )
                                .withHeader(X_TICKET_ID_HEADER, uuid))
        );

        server.stubFor(
                get(urlPathEqualTo("/ticket/" + uuid))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withHeader(X_TICKET_ID_HEADER, uuid)
                                .withBody(format("{ \"uuid\": \"%s\" }", uuid))
                        )
        );
    }
}
