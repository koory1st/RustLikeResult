/*
 *  Err.java, 2022-01-04
 *
 *  Copyright 2022  Koory1st, Inc. All rights reserved.
 */

package io.github.koory1st.util.result;

import org.jetbrains.annotations.NotNull;


public class Err<T, E> extends Result<T, E> {
    private Err(E err) {
        super(null, err, false);
    }

    @NotNull
    public static <T, E> Err<T, E> of(@NotNull E err) {
        //noinspection ConstantConditions
        if (err == null) {
            throw new ResultPanicException("Can't set a null to an Err's Content.");
        }
        return new Err<>(err);
    }
}
