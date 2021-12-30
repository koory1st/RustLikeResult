package io.github.koory1st.util.result;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Result is a type that represents either success (Ok) or failure (Err).
 *
 * @param <T> success (Ok)
 * @param <E> failure (Err)
 */
public abstract class Result<T, E> {
    public static final String EMPTY_STRING = "";
    public static final String ERR_S_CONTENT_IS_EMPTY = "Err's content is empty.";
    public static final String EXPECT_FMT = "%s: %s";
    private final E err;
    private final T ok;
    private final boolean okFlg;

    protected Result(T ok, E err, boolean okFlg) {
        this.ok = ok;
        this.err = err;
        this.okFlg = okFlg;
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
     * @return true if the result is Ok.
     */
    public boolean isOk() {
        return okFlg;
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
