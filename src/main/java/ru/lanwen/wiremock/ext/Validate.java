package ru.lanwen.wiremock.ext;

import lombok.experimental.UtilityClass;

/**
 * @author lanwen (Kirill Merkushev)
 */
@UtilityClass
class Validate {

    static void validState(boolean stateValid, String message) {
        if (!stateValid) {
            throw new IllegalStateException(message);
        }
    }
}
