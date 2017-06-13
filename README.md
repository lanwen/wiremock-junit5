# Wiremock JUnit5

Simple extension to inject ready-to-use wiremock server to JUnit5 test


## Start Guide

1. Add dependency

```
ru.lanwen.wiremock:wiremock-junit5:${wiremock-junit5.version}
```

2. Create JUnit5 test:

```java
@ExtendWith({
        WiremockResolver.class,
        WiremockUriResolver.class
})
class WiremockJUnit5Test {

    @Test
    void shouldInjectWiremock(@Wiremock WireMockServer server, @WiremockUri String uri) {
        customize(server); // your setup
        SomeApiClient api = SomeApiClient.connect(uri);

        Response response = api.call();
        assertThat(response.headers(), hasSize(1));
    }
}

```

### Reuse customization

With `ru.lanwen.wiremock.config.WiremockCustomizer` and `ru.lanwen.wiremock.config.WiremockConfigFactory`
you can reuse logic of initial setup.

Please look into test for example.
