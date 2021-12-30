package io.github.koory1st.util.result;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResultTest {

    @Test
    void contains() {
        var v = Ok.build(2);
        Assertions.assertFalse(v.contains(null));

        var w = Ok.build();
        Assertions.assertFalse(w.contains(2));

        var x = Ok.build(2);
        Assertions.assertTrue(x.contains(2));

        var y = Ok.build(3);
        Assertions.assertFalse(y.contains(2));

        var z = Err.build("Some error message");
        Assertions.assertFalse(z.contains(2));
    }

    @Test
    void containsErr() {
        var v = Ok.build(2);
        Assertions.assertFalse(v.containsErr("Some error message"));

        var w = Err.build("Some error message");
        Assertions.assertFalse(w.containsErr(null));

        var x = Err.build(null);
        Assertions.assertFalse(x.containsErr("Some error message"));

        var y = Err.build("Some error message");
        Assertions.assertTrue(y.containsErr("Some error message"));

        var z = Err.build("Some other error message");
        Assertions.assertFalse(z.containsErr("Some error message"));
    }

    @Test
    void err() {
        var x = Ok.build(2);
        Assertions.assertTrue(x.err().isEmpty());

        var y = Err.build("Nothing here");
        Assertions.assertEquals("Nothing here", y.err().orElseThrow());
    }

    @Test
    void expect() {
        var x = Err.build("emergency failure");
        try {
            x.expect("Testing expect");
            Assertions.fail();
        } catch (ResultPanicException e) {
            Assertions.assertEquals("Testing expect: emergency failure", e.getMessage());
        }

        var y = Ok.build(2);
        Assertions.assertEquals(2, y.expect("normal"));

        var z = Err.build(null);
        try {
            z.expect("Testing expect");
            Assertions.fail();
        } catch (ResultPanicException e) {
            Assertions.assertEquals("Testing expect: ", e.getMessage());
        }
    }

    @Test
    void expectErr() {
        var x = Ok.build(2);
        try {
            x.expectErr("Testing expectErr");
            Assertions.fail();
        } catch (ResultPanicException e) {
            Assertions.assertEquals("Testing expectErr: 2", e.getMessage());
        }

        var y = Err.build("Some error message");
        Assertions.assertEquals("Some error message", y.expectErr("normal"));

        var z = Ok.build();
        try {
            z.expectErr("Testing expectErr");
            Assertions.fail();
        } catch (ResultPanicException e) {
            Assertions.assertEquals("Testing expectErr: ", e.getMessage());
        }
    }

    @Test
    void isErr() {
        var x = Ok.build(-3);
        Assertions.assertFalse(x.isErr());

        var y = Err.build("Some error message");
        Assertions.assertTrue(y.isErr());
    }

    @Test
    void isOk() {
        var x = Ok.build(-3);
        Assertions.assertTrue(x.isOk());

        var y = Err.build("Some error message");
        Assertions.assertFalse(y.isOk());
    }

    @Test
    void ok() {
        var x = Ok.build(2);
        Assertions.assertEquals(2, x.ok().orElseThrow());

        var y = Err.build("Nothing here");
        Assertions.assertTrue(y.ok().isEmpty());

        var z = Ok.build();
        Assertions.assertTrue(z.isOk());
    }

    @Test
    void unwrap() {
        var x = Ok.build(2);
        Assertions.assertEquals(2, x.unwrap());

        var y = Err.build("emergency failure");
        try {
            y.unwrap();
            Assertions.fail();
        } catch (ResultPanicException e) {
            Assertions.assertEquals("emergency failure", e.getMessage());
        }

        var z = Err.build(null);
        try {
            z.unwrap();
            Assertions.fail();
        } catch (ResultPanicException e) {
            Assertions.assertNull(e.getMessage());
        }
    }

}