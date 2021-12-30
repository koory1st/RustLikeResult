package io.github.koory1st.util.result;

import org.jetbrains.annotations.NotNull;

public class Ok<T> extends Result<T, Void> {
    private Ok(T ok) {
        super(ok, null, true);
    }

    public static <T> Ok<T> build(@NotNull T ok) {
        return new Ok<>(ok);
    }

    public static <T> Ok<T> build() {
        return new Ok<>(null);
    }
}
