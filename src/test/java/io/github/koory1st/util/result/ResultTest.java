package io.github.koory1st.util.result;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

class ResultTest {

    @Test
    void and() {
        Assertions.assertEquals(Err.of("late error"), Ok.of(2).and(Err.of("late error")));

        Assertions.assertEquals(Err.of("early error"), Err.of("early error").and(Ok.of("foo")));

        Assertions.assertEquals(Err.of("not a 2"), Err.of("not a 2").and(Err.of("late error")));

        Assertions.assertEquals(Ok.of("different result type"), Ok.of(2).and(Ok.of("different result type")));
    }

    @Test
    void andThen() {
        Function<Integer, Result<Integer, Integer>> sq = x -> Ok.of(x * x);
        Function<Integer, Result<Integer, Integer>> err = x -> Err.of(x);

        Result<Integer, Integer> w = Ok.of(2);
        Assertions.assertEquals(Ok.of(16), w.andThen(sq).andThen(sq));

        Result<Integer, Integer> x = Ok.of(2);
        Assertions.assertEquals(Err.of(4), x.andThen(sq).andThen(err));

        Result<Integer, Integer> y = Ok.of(2);
        Assertions.assertEquals(Err.of(2), y.andThen(err).andThen(sq));

        Result<Integer, Integer> z = Err.of(3);
        Assertions.assertEquals(Err.of(3), z.andThen(sq).andThen(err));

        Result<Integer, Integer> t = Ok.of();

        try {
            Result<Integer, Integer> re = t.andThen(sq);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals("Can't applying a function to a Empty Ok.", e.getMessage());
        }
    }

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
    void map() {
        var x = Ok.of(2);
        Assertions.assertEquals(Ok.of(4), x.map(value -> value * 2));
        Assertions.assertNotEquals(Ok.of(5), x.map(value -> value * 2));
        Assertions.assertEquals(Ok.of(8), Ok.of(4).map(value -> value * 2));
        Result<Integer, String> y = Err.of("an error");
        Assertions.assertEquals(Err.of("an error"), y.map(value -> value * 2));
        Assertions.assertNotEquals(Ok.of(8), y.map(value -> value * 2));

        Ok<Integer, String> z = Ok.of();

        try {
            z.map(value -> value * 2);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals("Can't applying a function to a Empty Ok.", e.getMessage());
        }
    }

    @Test
    void mapErr() {
        Function<Integer, String> stringify = x -> String.format("error code: %s", x);
        Result<Integer, Integer> x = Ok.of(2);
        Assertions.assertEquals(x.mapErr(stringify), Ok.of(2));

        Result<Integer, Integer> y = Err.of(13);
        Assertions.assertEquals(y.mapErr(stringify), Err.of("error code: 13"));
    }

    @Test
    void mapOr() {
        var x = Ok.of("Foo");
        Assertions.assertEquals(3, x.mapOr(42, String::length));

        Result<String, String> y = Err.of("Bar");
        Assertions.assertEquals(42, y.mapOr(42, String::length));

        Ok<Integer, String> z = Ok.of();
        try {
            z.mapOr(43, value -> value * 2);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals("Can't applying a function to a Empty Ok.", e.getMessage());
        }
    }

    @Test
    void mapOrElse() {
        var k = 21;
        var x = Ok.of("Foo");
        Assertions.assertEquals(3, x.mapOrElse(e -> k * 2, String::length));

        Result<String, String> y = Err.of("Bar");
        Assertions.assertEquals(42, y.mapOrElse(e -> k * 2, String::length));

        Ok<Integer, String> z = Ok.of();
        try {
            z.mapOrElse(e -> k * 2, value -> value * 2);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals("Can't applying a function to a Empty Ok.", e.getMessage());
        }
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
    void or() {
        Assertions.assertEquals(Ok.of(2), Ok.of(2).or(Err.of("late error")));

        Assertions.assertEquals(Ok.of(2), Err.of("early error").or(Ok.of(2)));

        Assertions.assertEquals(Err.of("late error"), Err.of("not a 2").or(Err.of("late error")));

        Assertions.assertEquals(Ok.of(100), Ok.of(2).and(Ok.of(100)));
    }

    @Test
    void orElse() {
        Function<Integer, Result<Integer, Integer>> sq = x -> Ok.of(x * x);
        Function<Integer, Result<Integer, Integer>> err = Err::of;

        Result<Integer, Integer> w = Ok.of(2);
        Assertions.assertEquals(Ok.of(2), w.orElse(sq).orElse(sq));

        Result<Integer, Integer> x = Ok.of(2);
        Assertions.assertEquals(Ok.of(2), x.orElse(err).orElse(sq));

        Result<Integer, Integer> y = Err.of(3);
        Assertions.assertEquals(Ok.of(9), y.orElse(sq).orElse(err));

        Result<Integer, Integer> z = Err.of(3);
        Assertions.assertEquals(Err.of(3), z.orElse(err).orElse(err));
    }

    @Test
    void testEquals() {
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
    void testToString() {
        Assertions.assertEquals("Ok(1)", Ok.of(1).toString());
        Assertions.assertEquals("Ok(\"1\")", Ok.of("1").toString());
        Assertions.assertEquals("Err(1)", Err.of(1).toString());
        Assertions.assertEquals("Err(\"1\")", Err.of("1").toString());
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
            Assertions.assertEquals("called `Result.unwrap()` on an `Err` value: emergency failure", e.getMessage());
        }

        var z = Err.of("");
        try {
            z.unwrap();
            Assertions.fail();
        } catch (ResultPanicException e) {
            Assertions.assertEquals("called `Result.unwrap()` on an `Err` value: ", e.getMessage());
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

    @Test
    void unwrapErr() {
        var x = Ok.of(2);
        try {
            x.unwrapErr();
            Assertions.fail();
        } catch (ResultPanicException e) {
            Assertions.assertEquals("called `Result.unwrapErr()` on an `Ok` value: 2", e.getMessage());
        }

        var y = Err.of("emergency failure");
        Assertions.assertEquals("emergency failure", y.unwrapErr());
    }

    @Test
    void unwrapOr() {
        var defaultValue = 2;
        var x = Ok.of(9);
        Assertions.assertEquals(9, x.unwrapOr(defaultValue));

        var y = Err.of("error");
        Assertions.assertEquals(defaultValue, y.unwrapOr(defaultValue));
    }

}