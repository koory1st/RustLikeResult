package io.github.koory1st.util.result;

public class ResultPanicException extends RuntimeException {
    public ResultPanicException(String msg) {
        super(msg);
    }

    public ResultPanicException() {
        super();
    }
}
