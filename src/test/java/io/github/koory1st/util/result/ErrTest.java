package io.github.koory1st.util.result;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ErrTest {

    @Test
    void build() {
        var x = Err.build("test");

        try {
            //noinspection ConstantConditions
            var build = Err.build(null);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals("Can't set a null to an Err's Content.", e.getMessage());
        }
    }
}