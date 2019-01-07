package ru.lanwen.wiremock.config;

import ru.lanwen.wiremock.ext.WiremockResolver;

/**
 * Defines whenever to shutdown the server: AFTER_EACH or AFTER_ALL test execution.
 *
 * @author Vincent Palau
 * @see WiremockResolver.Wiremock
 */
public enum WiremockShutdownStrategy {

    /**
     * Should shutdown the server after each test method has been invoked.
     */
    AFTER_EACH,

    /**
     * Should shutdown the server after all tests have been invoked.
     */
    AFTER_ALL
}
