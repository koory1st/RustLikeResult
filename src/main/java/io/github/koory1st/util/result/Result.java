package io.github.koory1st.util.result;

import java.util.Objects;
import java.util.Optional;

/**
 * Result is a type that represents either success (Ok) or failure (Err).
 *
 * @param <T> success (Ok)
 * @param <E> failure (Err)
 */
public class Result<T, E> {
    public static final String EMPTY_STRING = "";
    public static final String EXPECT_FMT = "%s: %s";
    private final E err;
    private final T ok;
    private final boolean okFlg;

    private Result(T ok, E err, boolean okFlg) {
        this.ok = ok;
        this.err = err;
        this.okFlg = okFlg;
    }

    /**
     * Construct an Err Result
     *
     * @param err Err content
     * @return an Err Result
     */
    public static <Void, E> Result<Void, E> Err(E err) {
        return new Result<>(null, err, false);
    }

    /**
     * Construct an Ok Result
     *
     * @param ok Ok content
     * @return an Ok Result
     */
    public static <T, Void> Result<T, Void> Ok(T ok) {
        return new Result<>(ok, null, true);
    }

    /**
     * Construct an Ok Result without Ok content
     *
     * @return an Ok Result
     */
    public static <Void> Result<Void, Void> Ok() {
        return new Result<>(null, null, true);
    }

    /**
     * @param value given value
     * @return true if the result is an Ok value containing the given value.
     */
    public boolean contains(T value) {
        if (!okFlg) {
            return false;
        }

        // if value is null, return false
        if (value == null) {
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
    public Optional<T> ok() {
        return Optional.ofNullable(ok);
    }

    /**
     * @param msg passed message
     * @return the contained Ok value
     * @throws RuntimeException if the value is an Err, with a message including the passed message, and the content of the Err.
     */
    public T expect(String msg) throws RuntimeException {
        if (okFlg) {
            return ok().orElse(null);
        }

        String errString = err().isEmpty() ? EMPTY_STRING : err().get().toString();

        throw new RuntimeException(String.format(EXPECT_FMT, msg, errString));
    }

    /**
     * Converts from Result<T, E> to Option<E>.
     *
     * @return Optional<E>
     */
    public Optional<E> err() {
        return Optional.ofNullable(err);
    }

    /**
     * @return true if the result is Err.
     */
    public boolean isErr() {
        return !okFlg;
    }

    /**
     * @return true if the result is Ok.
     */
    public boolean isOk() {
        return okFlg;
    }

    /**
     * @return the contained Ok value, consuming the self value.
     * @throws RuntimeException if the value is an Err, with a message provided by the Err’s value.
     */
    public T unwrap() throws RuntimeException {
        if (okFlg) {
            return ok().orElse(null);
        }

        err().ifPresent(e -> {
            throw new RuntimeException(e.toString());
        });

        throw new RuntimeException();
    }
}
