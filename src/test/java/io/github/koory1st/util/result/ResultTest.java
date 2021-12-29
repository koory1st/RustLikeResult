package io.github.koory1st.util.result;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResultTest {

    @Test
    void contains() {
        var v = Result.Ok(2);
        Assertions.assertFalse(v.contains(null));

        var w = Result.Ok();
        Assertions.assertFalse(w.contains(2));

        var x = Result.Ok(2);
        Assertions.assertTrue(x.contains(2));

        var y = Result.Ok(3);
        Assertions.assertFalse(y.contains(2));

        var z = Result.Err("Some error message");
        Assertions.assertFalse(z.contains(2));
    }

    @Test
    void containsErr() {
        var v = Result.Ok(2);
        Assertions.assertFalse(v.containsErr("Some error message"));

        var w = Result.Err("Some error message");
        Assertions.assertFalse(w.containsErr(null));

        var x = Result.Err(null);
        Assertions.assertFalse(x.containsErr("Some error message"));

        var y = Result.Err("Some error message");
        Assertions.assertTrue(y.containsErr("Some error message"));

        var z = Result.Err("Some other error message");
        Assertions.assertFalse(z.containsErr("Some error message"));
    }

    @Test
    void err() {
        var x = Result.Ok(2);
        Assertions.assertTrue(x.err().isEmpty());

        var y = Result.Err("Nothing here");
        Assertions.assertEquals("Nothing here", y.err().orElseThrow());
    }

    @Test
    void expect() {
        var x = Result.Err("emergency failure");
        try {
            x.expect("Testing expect");
            Assertions.fail();
        } catch (RuntimeException e) {
            Assertions.assertEquals("Testing expect: emergency failure", e.getMessage());
        }

        var y = Result.Ok(2);
        Assertions.assertEquals(2, y.expect("normal"));

        var z = Result.Err(null);
        try {
            z.expect("Testing expect");
            Assertions.fail();
        } catch (RuntimeException e) {
            Assertions.assertEquals("Testing expect: ", e.getMessage());
        }
    }

    @Test
    void isErr() {
        var x = Result.Ok(-3);
        Assertions.assertFalse(x.isErr());

        var y = Result.Err("Some error message");
        Assertions.assertTrue(y.isErr());
    }

    @Test
    void isOk() {
        var x = Result.Ok(-3);
        Assertions.assertTrue(x.isOk());

        var y = Result.Err("Some error message");
        Assertions.assertFalse(y.isOk());
    }

    @Test
    void ok() {
        var x = Result.Ok(2);
        Assertions.assertEquals(2, x.ok().orElseThrow());

        var y = Result.Err("Nothing here");
        Assertions.assertTrue(y.ok().isEmpty());

        var z = Result.Ok();
        Assertions.assertTrue(z.isOk());
    }

    @Test
    void unwrap() {
        var x = Result.Ok(2);
        Assertions.assertEquals(2, x.unwrap());

        var y = Result.Err("emergency failure");
        try {
            y.unwrap();
            Assertions.fail();
        } catch (RuntimeException e) {
            Assertions.assertEquals("emergency failure", e.getMessage());
        }

        var z = Result.Err(null);
        try {
            z.unwrap();
            Assertions.fail();
        } catch (RuntimeException e) {
            Assertions.assertNull(e.getMessage());
        }
    }

}