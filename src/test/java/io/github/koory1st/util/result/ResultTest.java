package io.github.koory1st.util.result;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResultTest {

    @Test
    void contains() {
        var v = Ok.of(2);
        Assertions.assertFalse(v.contains(null));

        var w = Ok.of();
        Assertions.assertFalse(w.contains(2));

        var x = Ok.of(2);
        Assertions.assertTrue(x.contains(2));

        var y = Ok.of(3);
        Assertions.assertFalse(y.contains(2));

        var z = Err.of("Some error message");
        Assertions.assertFalse(z.contains(2));
    }

    @Test
    void containsErr() {
        var v = Ok.of(2);
        Assertions.assertFalse(v.containsErr("Some error message"));

        var w = Err.of("Some error message");
        Assertions.assertFalse(w.containsErr(null));

        var y = Err.of("Some error message");
        Assertions.assertTrue(y.containsErr("Some error message"));

        var z = Err.of("Some other error message");
        Assertions.assertFalse(z.containsErr("Some error message"));
    }

    @Test
    void equals() {
        Assertions.assertEquals(Ok.of(1), Ok.of(1));
        Assertions.assertEquals(Ok.of(), Ok.of());
        Assertions.assertNotEquals(Ok.of(1), Ok.of(2));
        Assertions.assertNotEquals(Ok.of(1), Ok.of("1"));
        Assertions.assertNotEquals(Ok.of(1), Ok.of());
        Assertions.assertNotEquals(Ok.of(1), Err.of("1"));
        Assertions.assertNotEquals(Ok.of(), Err.of("1"));
        Assertions.assertEquals(Err.of("1"), Err.of("1"));
        Assertions.assertNotEquals(Ok.of(1), new Object());

    }

    @Test
    void err() {
        var x = Ok.of(2);
        Assertions.assertTrue(x.err().isEmpty());

        var y = Err.of("Nothing here");
        Assertions.assertEquals("Nothing here", y.err().orElseThrow());
    }

    @Test
    void expect() {
        var x = Err.of("emergency failure");
        try {
            x.expect("Testing expect");
            Assertions.fail();
        } catch (ResultPanicException e) {
            Assertions.assertEquals("Testing expect: emergency failure", e.getMessage());
        }

        var y = Ok.of(2);
        Assertions.assertEquals(2, y.expect("normal"));

        var z = Err.of("");
        try {
            z.expect("Testing expect");
            Assertions.fail();
        } catch (ResultPanicException e) {
            Assertions.assertEquals("Testing expect: ", e.getMessage());
        }
    }

    @Test
    void expectErr() {
        var x = Ok.of(2);
        try {
            x.expectErr("Testing expectErr");
            Assertions.fail();
        } catch (ResultPanicException e) {
            Assertions.assertEquals("Testing expectErr: 2", e.getMessage());
        }

        var y = Err.of("Some error message");
        Assertions.assertEquals("Some error message", y.expectErr("normal"));

        var z = Ok.of();
        try {
            z.expectErr("Testing expectErr");
            Assertions.fail();
        } catch (ResultPanicException e) {
            Assertions.assertEquals("Testing expectErr: ", e.getMessage());
        }
    }

    @Test
    void flatten() {
        var v = Ok.of("hello");
        Assertions.assertEquals(Ok.of("hello"), v.flatten());
        Assertions.assertNotEquals(Ok.of("hello1"), v.flatten());

        var w = Ok.of();
        Assertions.assertEquals(Ok.of(), w.flatten());
        Assertions.assertNotEquals(Ok.of("hello1"), w.flatten());

        var x = Ok.of(Ok.of("hello"));
        Assertions.assertEquals(Ok.of("hello"), x.flatten());
        Assertions.assertNotEquals(Ok.of("hello1"), x.flatten());

        var y = Ok.of(Err.of(6));
        Assertions.assertEquals(Err.of(6), y.flatten());
        Assertions.assertNotEquals(Err.of(7), y.flatten());

        var z = Err.of(6);
        Assertions.assertEquals(Err.of(6), z.flatten());
        Assertions.assertNotEquals(Err.of(7), y.flatten());
    }

    @Test
    void isErr() {
        var x = Ok.of(-3);
        Assertions.assertFalse(x.isErr());

        var y = Err.of("Some error message");
        Assertions.assertTrue(y.isErr());
    }

    @Test
    void isOk() {
        var x = Ok.of(-3);
        Assertions.assertTrue(x.isOk());

        var y = Err.of("Some error message");
        Assertions.assertFalse(y.isOk());
    }

    @Test
    void ok() {
        var x = Ok.of(2);
        Assertions.assertEquals(2, x.ok().orElseThrow());

        var y = Err.of("Nothing here");
        Assertions.assertTrue(y.ok().isEmpty());

        var z = Ok.of();
        Assertions.assertTrue(z.isOk());
    }

    @Test
    void unwrap() {
        var x = Ok.of(2);
        Assertions.assertEquals(2, x.unwrap());

        var y = Err.of("emergency failure");
        try {
            y.unwrap();
            Assertions.fail();
        } catch (ResultPanicException e) {
            Assertions.assertEquals("emergency failure", e.getMessage());
        }

        var z = Err.of("");
        try {
            z.unwrap();
            Assertions.fail();
        } catch (ResultPanicException e) {
            Assertions.assertEquals("", e.getMessage());
        }

        try {
            //noinspection ConstantConditions
            var w = Err.of(null);
            w.unwrap();
            Assertions.fail();
        } catch (ResultPanicException e) {
            Assertions.assertEquals("Can't set a null to an Err's Content.", e.getMessage());
        }
    }

}