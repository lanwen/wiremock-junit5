package ru.lanwen.wiremock.ext;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.lanwen.wiremock.ext.Validate.validState;

/**
 * @author SourcePond (Roland Hauser)
 */
public class ValidateTest {
    private static final String EXPECTED_MESSAGE = "Expected message";

    @Test
    void instantiationNotAllowed() {
        assertThrows(UnsupportedOperationException.class, () -> {
            Constructor<Validate> constructor = Validate.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            try {
                constructor.newInstance();
            } catch (final InvocationTargetException e) {
                throw e.getTargetException();
            }
        }, "Exception expected");
    }

    @Test
    void verifyValidState() {
        // Should not throw an exception
        validState(true, EXPECTED_MESSAGE);
        assertThrows(IllegalStateException.class, () -> validState(false, EXPECTED_MESSAGE), EXPECTED_MESSAGE);
    }
}
