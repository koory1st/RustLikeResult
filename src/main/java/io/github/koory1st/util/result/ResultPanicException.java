/*
 *  ResultPanicException.java, 2022-01-04
 *
 *  Copyright 2022  Koory1st, Inc. All rights reserved.
 */

package io.github.koory1st.util.result;

public class ResultPanicException extends RuntimeException {
    public ResultPanicException(String msg) {
        super(msg);
    }
}
