package io.github.koory1st.util.result;

public class Err<T, E> extends Result<T, E> {
    private Err(E err) {
        super(null, err, false);
    }

    public static <T, E> Err<T, E> build(E err) {
        return new Err<>(err);
    }
}
