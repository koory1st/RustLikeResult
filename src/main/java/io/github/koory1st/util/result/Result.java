/*
 *  Result.java, 2022-01-04
 *
 *  Copyright 2022  Koory1st, Inc. All rights reserved.
 */

package io.github.koory1st.util.result;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Result is a type that represents either success (Ok) or failure (Err).
 *
 * @param <T> success (Ok)
 * @param <E> failure (Err)
 */
public abstract class Result<T, E> {
    public static final String CANT_APPLY_FUNCTION_A_EMPTY_OK = "Can't applying a function to a Empty Ok.";
    public static final String EMPTY_STRING = "";
    public static final String ERR = "Err";
    public static final String EXPECT_FMT = "%s: %s";
    public static final String OK = "Ok";
    public static final String TO_STRING_FMT = "%s(%s)";
    public static final String TO_STRING_QUOTE_FMT = "%s(\"%s\")";
    public static final String UNWRAP_ERR_PANIC_STR = "called `Result.unwrapErr()` on an `Ok` value: %s";
    public static final String UNWRAP_PANIC_STR = "called `Result.unwrap()` on an `Err` value: %s";
    private final E err;
    private final T ok;
    private final boolean okFlg;

    protected Result(T ok, E err, boolean okFlg) {
        this.ok = ok;
        this.err = err;
        this.okFlg = okFlg;
    }

    /**
     * @param res res
     * @param <U> U
     * @return `res` if the result is [`Ok`], otherwise returns the [`Err`] value of `self`.
     */
    @NotNull
    public <U> Result<U, ?> and(@NotNull Result<U, ?> res) {
        if (this.isOk()) {
            return res;
        }

        return Err.of(this.err);
    }

    /**
     * @return true if the result is Ok.
     */
    public boolean isOk() {
        return okFlg;
    }

    /**
     * Calls `op` if the result is [`Ok`], otherwise returns the [`Err`] value of `self`.
     *
     * @param op  op
     * @param <U> U
     * @return return
     */
    @NotNull
    public <U> Result<U, E> andThen(@NotNull Function<T, Result<U, E>> op) {
        if (this.isErr()) {
            return Err.of(this.err);
        }

        if (this.ok().isEmpty()) {
            throw new ResultPanicException(CANT_APPLY_FUNCTION_A_EMPTY_OK);
        }

        return op.apply(this.ok().get());
    }

    /**
     * @return true if the result is Err.
     */
    public boolean isErr() {
        return !okFlg;
    }

    /**
     * Converts from Result&lt;T, E&gt; to Optional&lt;T&gt;.
     *
     * @return Option&lt;T&gt;
     */
    @NotNull
    public Optional<T> ok() {
        return Optional.ofNullable(ok);
    }

    /**
     * @param value given value
     * @return true if the result is an Ok value containing the given value.
     */
    public boolean contains(@NotNull T value) {
        if (!okFlg) {
            return false;
        }

        // if ok is empty, return false
        if (ok().isEmpty()) {
            return false;
        }

        return Objects.equals(ok, value);
    }

    /**
     * @param value the given value
     * @return true if the result is an Err value containing the given value.
     */
    public boolean containsErr(@NotNull String value) {
        if (okFlg) {
            return false;
        }

        //noinspection ConstantConditions
        if (value == null) {
            return false;
        }

        return Objects.equals(value, err);
    }

    @Override
    public boolean equals(Object obj2Compare) {
        if (!(obj2Compare instanceof Result)) {
            return false;
        }

        //noinspection rawtypes
        Result obj2CompareResult = (Result) obj2Compare;
        if (this.isOk() != obj2CompareResult.isOk()) {
            return false;
        }

        if (this.isOk()) {
            return this.ok().equals(obj2CompareResult.ok());
        }

        return this.err().equals(obj2CompareResult.err());
    }

    /**
     * Converts from Result&lt;T, E&gt; to Option&lt;E&gt;.
     *
     * @return Optional&lt;E&gt;
     */
    @NotNull
    public Optional<E> err() {
        if (isErr()) {
            return Optional.of(err);
        }
        return Optional.ofNullable(err);
    }

    @Override
    @NotNull
    public String toString() {
        if (this.isOk()) {
            if (ok instanceof String) {
                return String.format(TO_STRING_QUOTE_FMT, OK, ok);
            }
            return String.format(TO_STRING_FMT, OK, ok);
        }

        if (err instanceof String) {
            return String.format(TO_STRING_QUOTE_FMT, ERR, err);
        }
        return String.format(TO_STRING_FMT, ERR, err);
    }

    /**
     * @param msg passed message
     * @return the contained Ok value
     * @throws ResultPanicException if the value is an Err, with a message including the passed message, and the content of the Err.
     */
    @Nullable
    public T expect(@NotNull String msg) throws ResultPanicException {
        if (okFlg) {
            return ok;
        }

        String errString = err().isEmpty() ? EMPTY_STRING : err().get().toString();

        throw new ResultPanicException(String.format(EXPECT_FMT, msg, errString));
    }

    /**
     * @param msg passed message
     * @return the contained Err value.
     * @throws ResultPanicException if the value is an Ok, with a panic message including the passed message, and the content of the Ok.
     */
    @NotNull
    public E expectErr(@NotNull String msg) throws ResultPanicException {
        if (!okFlg) {
            return err;
        }

        String errString = ok().isEmpty() ? EMPTY_STRING : ok().get().toString();

        throw new ResultPanicException(String.format(EXPECT_FMT, msg, errString));
    }

    /**
     * Converts from Result&lt;Result&lt;T, E&gt;, E&gt; to Result&lt;T, E&gt;
     *
     * @return Result&lt;T, E&gt;
     */
    @NotNull
    public Result<T, E> flatten() {
        if (!okFlg) {
            return Err.of(err);
        }

        if (ok().isEmpty()) {
            return this;
        }

        if (ok instanceof Result) {
            //noinspection unchecked,rawtypes
            return (Result) ok;
        }
        return this;
    }

    /**
     * Maps a Result&lt;T, E&gt; to Result&lt;U, E&gt;
     * by applying a function to a contained Ok value, leaving an Err value untouched.
     * This function can be used to compose the results of two functions.
     *
     * @param mapFunction mapFunction
     * @param <U>         U
     * @return mapped Result&lt;U, E&gt;
     */
    @NotNull
    public <U> Result<U, E> map(Function<T, U> mapFunction) {
        if (this.isErr()) {
            return Err.of(this.err);
        }

        if (this.ok().isEmpty()) {
            throw new ResultPanicException(CANT_APPLY_FUNCTION_A_EMPTY_OK);
        }

        return Ok.of(mapFunction.apply(this.ok));
    }

    /**
     * Maps a `Result&lt;T, E&gt;` to `Result&lt;T, F&gt;` by applying a function to a contained [`Err`] value
     *
     * @param mapFunction mapFunction
     * @param <F>         F
     * @return Result&lt;T, F&gt;
     */
    @NotNull
    public <F> Result<T, F> mapErr(@NotNull Function<E, F> mapFunction) {
        if (this.isOk()) {
            return Ok.of(this.ok);
        }

        return Err.of(mapFunction.apply(err));
    }

    /**
     * Returns the provided default (if Err), or applies a function to the contained value (if Ok)
     *
     * @param defaultValue default value
     * @param mapFunction  mapFunction
     * @param <U>          U
     * @return mapped U
     */
    @NotNull
    public <U> U mapOr(@NotNull U defaultValue, @NotNull Function<T, U> mapFunction) {
        if (this.isErr()) {
            return defaultValue;
        }

        if (this.ok().isEmpty()) {
            throw new ResultPanicException(CANT_APPLY_FUNCTION_A_EMPTY_OK);
        }

        return mapFunction.apply(ok);
    }

    /**
     * Returns the provided default (if Err), or applies a function to the contained value (if Ok),
     *
     * @param defaultFunction defaultFunction
     * @param mapFunction     mapFunction
     * @param <U>             U
     * @return mapped U
     */
    @NotNull
    public <U> U mapOrElse(@NotNull Function<E, U> defaultFunction, @NotNull Function<T, U> mapFunction) {
        if (this.isErr()) {
            return defaultFunction.apply(err);
        }

        if (this.ok().isEmpty()) {
            throw new ResultPanicException(CANT_APPLY_FUNCTION_A_EMPTY_OK);
        }

        return mapFunction.apply(ok);
    }

    /**
     * @param res res
     * @param <F> F
     * @return `res` if the result is [`Err`], otherwise returns the [`Ok`] value of `self`.
     */
    @NotNull
    public <F> Result<?, F> or(@NotNull Result<?, F> res) {
        if (this.isOk()) {
            return Ok.of(this.ok);
        }

        return res;
    }

    /**
     * Calls `op` if the result is [`Err`], otherwise returns the [`Ok`] value of `self`.
     *
     * @param op  op
     * @param <F> F
     * @return result
     */
    @NotNull
    public <F> Result<T, F> orElse(@NotNull Function<E, Result<T, F>> op) {
        if (this.isOk()) {
            return Ok.of(ok);
        }

        return op.apply(err);
    }

    /**
     * @return the contained Ok value, consuming the self value.
     * @throws ResultPanicException if the value is an Err, with a message provided by the Errs value.
     */
    @Nullable
    public T unwrap() throws ResultPanicException {
        if (okFlg) {
            return ok;
        }

        throw new ResultPanicException(String.format(UNWRAP_PANIC_STR, err));
    }

    /**
     * @return the contained [`Err`] value
     * @throws ResultPanicException if the value is an [`Ok`], with a custom panic message provided by the [`Ok`]'s value.
     */
    @NotNull
    public E unwrapErr() throws ResultPanicException {
        if (isErr()) {
            return err;
        }

        throw new ResultPanicException(String.format(UNWRAP_ERR_PANIC_STR, ok));
    }

    /**
     * @param defaultValue default
     * @return the contained [`Ok`] value or a provided default.
     */
    @Nullable
    public T unwrapOr(@Nullable T defaultValue) {
        if (isErr()) {
            return defaultValue;
        }
        return ok;
    }

    /**
     * @param op a closure
     * @return the contained [`Ok`] value or computes it from a closure.
     */
    @Nullable
    public T unwrapOrElse(@NotNull Function<E, T> op) {
        if (isOk()) {
            return ok;
        }
        return op.apply(err);
    }
}
