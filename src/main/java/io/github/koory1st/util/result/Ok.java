package io.github.koory1st.util.result;

import org.jetbrains.annotations.NotNull;

public class Ok<T, E> extends Result<T, E> {
    private Ok(T ok) {
        super(ok, null, true);
    }

    public static <T, E> Ok<T, E> of(@NotNull T ok) {
        return new Ok<>(ok);
    }

    public static <T, E> Ok<T, E> of() {
        return new Ok<>(null);
    }
}
