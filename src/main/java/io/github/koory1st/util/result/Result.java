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
    public static final String CAN_T_MAP_A_EMPTY_OK = "Can't map a Empty Ok.";
    public static final String EMPTY_STRING = "";
    public static final String ERR = "Err";
    public static final String EXPECT_FMT = "%s: %s";
    public static final String OK = "Ok";
    public static final String TO_STRING_FMT = "%s(%s)";
    public static final String TO_STRING_QUOTE_FMT = "%s(\"%s\")";
    private final E err;
    private final T ok;
    private final boolean okFlg;

    protected Result(T ok, E err, boolean okFlg) {
        this.ok = ok;
        this.err = err;
        this.okFlg = okFlg;
    }

    /**
     * Returns `res` if the result is [`Ok`], otherwise returns the [`Err`] value of `self`.
     *
     * @param res res
     * @return Result
     */
    public <U> Result<?, E> and(Result<U, E> res) {
        if (this.isOk()) {
            return res;
        }

        return this;
    }

    /**
     * @return true if the result is Ok.
     */
    public boolean isOk() {
        return okFlg;
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

        return Objects.equals(ok().get(), value);
    }

    /**
     * Converts from Result<T, E> to Optional<T>.
     *
     * @return Option<T>
     */
    @NotNull
    public Optional<T> ok() {
        return Optional.ofNullable(ok);
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

        //noinspection OptionalGetWithoutIsPresent
        return Objects.equals(value, err().get());
    }

    /**
     * Converts from Result<T, E> to Option<E>.
     *
     * @return Optional<E>
     */
    @NotNull
    public Optional<E> err() {
        if (isErr()) {
            return Optional.of(err);
        }
        return Optional.ofNullable(err);
    }

    /**
     * @return true if the result is Err.
     */
    public boolean isErr() {
        return !okFlg;
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

    @Override
    public String toString() {
        if (this.isOk()) {
            T content = ok().orElse(null);
            if (content instanceof String) {
                return String.format(TO_STRING_QUOTE_FMT, OK, content);
            }
            return String.format(TO_STRING_FMT, OK, content);
        }

        E content = err().orElse(null);
        if (content instanceof String) {
            return String.format(TO_STRING_QUOTE_FMT, ERR, content);
        }
        return String.format(TO_STRING_FMT, ERR, content);
    }

    /**
     * @param msg passed message
     * @return the contained Ok value
     * @throws ResultPanicException if the value is an Err, with a message including the passed message, and the content of the Err.
     */
    @Nullable
    public T expect(@NotNull String msg) throws ResultPanicException {
        if (okFlg) {
            return ok().orElse(null);
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
            //noinspection OptionalGetWithoutIsPresent
            return err().get();
        }

        String errString = ok().isEmpty() ? EMPTY_STRING : ok().get().toString();

        throw new ResultPanicException(String.format(EXPECT_FMT, msg, errString));
    }

    /**
     * Converts from Result<Result<T, E>, E> to Result<T, E>
     *
     * @return Result<T, E>
     */
    public Result<T, E> flatten() {
        if (!okFlg) {
            return Err.of(err);
        }

        Optional<T> okOptional = ok();
        if (okOptional.isEmpty()) {
            return this;
        }

        T ok = okOptional.get();
        if (ok instanceof Result) {
            //noinspection unchecked,rawtypes
            return (Result) ok;
        }
        return this;
    }

    /**
     * Maps a Result<T, E> to Result<U, E>
     * by applying a function to a contained Ok value, leaving an Err value untouched.
     * This function can be used to compose the results of two functions.
     *
     * @param mapFunction mapFunction
     * @return mapped Result<U, E>
     */
    public <U> Result<U, E> map(Function<T, U> mapFunction) {
        if (this.isErr()) {
            //noinspection unchecked
            return (Result<U, E>) this;
        }

        if (this.ok().isEmpty()) {
            throw new ResultPanicException(CAN_T_MAP_A_EMPTY_OK);
        }

        //noinspection unchecked
        return Ok.of(mapFunction.apply(this.ok().get()));
    }

    /**
     * Returns the provided default (if Err), or applies a function to the contained value (if Ok)
     *
     * @param defaultValue default value
     * @param mapFunction  mapFunction
     * @return mapped U
     */
    public <U> U mapOr(U defaultValue, Function<T, U> mapFunction) {
        if (this.isErr()) {
            return defaultValue;
        }

        if (this.ok().isEmpty()) {
            throw new ResultPanicException(CAN_T_MAP_A_EMPTY_OK);
        }

        return mapFunction.apply(this.ok().get());
    }

    /**
     * Returns the provided default (if Err), or applies a function to the contained value (if Ok),
     *
     * @param defaultFunction defaultFunction
     * @param mapFunction     mapFunction
     * @return mapped U
     */
    public <U> U mapOrElse(Function<E, U> defaultFunction, Function<T, U> mapFunction) {
        if (this.isErr()) {
            //noinspection OptionalGetWithoutIsPresent
            return defaultFunction.apply(this.err().get());
        }

        if (this.ok().isEmpty()) {
            throw new ResultPanicException(CAN_T_MAP_A_EMPTY_OK);
        }

        return mapFunction.apply(this.ok().get());
    }

    /**
     * @return the contained Ok value, consuming the self value.
     * @throws ResultPanicException if the value is an Err, with a message provided by the Err’s value.
     */
    @Nullable
    public T unwrap() throws ResultPanicException {
        if (okFlg) {
            return ok().orElse(null);
        }

        //noinspection OptionalGetWithoutIsPresent
        throw new ResultPanicException(err().get().toString());
    }
}
