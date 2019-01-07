package ru.lanwen.wiremock.testlib;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Logger;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Blackbox API
 *
 * @author lanwen (Merkushev Kirill)
 */
public interface TicketApi {

    @RequestLine("GET /ticket/{id}")
    Eticket get(@Param("id") String uid); // can't return both status and eticket

    @RequestLine("POST /ticket")
    Response create(Eticket uid);

    /**
     * Constructs ready-to use client
     *
     * @param uri base uri
     * @return instance of api class
     */
    static TicketApi connect(String uri) {
        ObjectMapper mapper = new ObjectMapper()
                .disable(FAIL_ON_UNKNOWN_PROPERTIES);

        return Feign.builder()
                .decoder(new JacksonDecoder(mapper))
                .encoder(new JacksonEncoder(mapper))
                .logger(new Slf4jLogger(TicketApi.class))
                .logLevel(Logger.Level.FULL)
                .target(TicketApi.class, uri);
    }
}
