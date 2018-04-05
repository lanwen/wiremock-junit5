package ru.lanwen.wiremock.ext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static ru.lanwen.wiremock.ext.Validate.validState;

/**
 * @author SourcePond (Roland Hauser)
 */
public class ValidateTest {
    private static final String EXPECTED_MESSAGE = "Expected message";

    @Test
    public void verifyValidState() {
        // Should not throw an exception
        validState(true, EXPECTED_MESSAGE);

        try {
            validState(false, EXPECTED_MESSAGE);
            fail("Exception expected");
        } catch (final IllegalStateException e) {
            assertEquals(EXPECTED_MESSAGE, e.getMessage());
        }
    }
}
