package ru.lanwen.wiremock.ext;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static ru.lanwen.wiremock.ext.Validate.validState;

/**
 * @author SourcePond (Roland Hauser)
 */
public class ValidateTest {
    private static final String EXPECTED_MESSAGE = "Expected message";

    @Test
    public void instantiationNotAllowed() {
        Executable instantiationNotAllowed = () -> {
            Constructor<Validate> constructor = Validate.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            try {
                constructor.newInstance();
            } catch (final InvocationTargetException e) {
                throw e.getTargetException();
            }
        };
        assertThrows(UnsupportedOperationException.class, instantiationNotAllowed, "Exception expected");
    }

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
