package ru.lanwen.wiremock.ext;

import com.github.tomakehurst.wiremock.WireMockServer;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.lanwen.wiremock.ext.WiremockResolver.Wiremock;
import ru.lanwen.wiremock.ext.WiremockUriResolver.WiremockUri;
import ru.lanwen.wiremock.testlib.Eticket;
import ru.lanwen.wiremock.testlib.TicketApi;
import ru.lanwen.wiremock.testlib.TicketEndpoint;

import java.util.Collection;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static ru.lanwen.wiremock.testlib.TicketEndpoint.X_TEST_METHOD_NAME_HEADER;
import static ru.lanwen.wiremock.testlib.TicketEndpoint.X_TICKET_ID_HEADER;

/**
 * @author lanwen (Kirill Merkushev)
 */
@ExtendWith({
        WiremockResolver.class,
        WiremockUriResolver.class
})
class WiremockResolverTest {

    @Test
    void shouldCreateTicket(@Wiremock(customizer = TicketEndpoint.class) WireMockServer server, @WiremockUri String uri) {
        TicketApi api = TicketApi.connect(uri);

        Response response = api.create(new Eticket());
        Collection<String> testMethodNames = response.headers().get(X_TEST_METHOD_NAME_HEADER);
        assertThat("testMethodNames", testMethodNames, hasSize(1));
        assertThat("shouldCreateTicket", is(testMethodNames.iterator().next()));

        Collection<String> ids = response.headers().get(X_TICKET_ID_HEADER);

        assertThat("ids", ids, hasSize(1));

        Eticket ticket = api.get(ids.iterator().next());

        assertThat(ticket, is(notNullValue()));
        assertThat("response content", ticket.getUuid(), is(ids.iterator().next()));
    }
}
