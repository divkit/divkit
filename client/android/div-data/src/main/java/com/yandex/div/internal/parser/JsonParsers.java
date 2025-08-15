package com.yandex.div.internal.parser;

import androidx.annotation.NonNull;
import kotlin.jvm.functions.Function1;

public class JsonParsers {

    @NonNull
    private static final ValueValidator<?> ALWAYS_VALID = (any) -> true;
    @NonNull
    private static final ValueValidator<String> ALWAYS_VALID_STRING = (any) -> true;
    @NonNull
    private static final ListValidator<?> ALWAYS_VALID_LIST = (any) -> true;
    @NonNull
    private static final Function1<?,?> AS_IS = (any) -> any;

    private JsonParsers() {
        /* do not initialize */
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T> ValueValidator<T> alwaysValid() {
        return (ValueValidator<T>) ALWAYS_VALID;
    }

    @NonNull
    static ValueValidator<String> alwaysValidString() {
        return ALWAYS_VALID_STRING;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T> ListValidator<T> alwaysValidList() {
        return (ListValidator<T>) ALWAYS_VALID_LIST;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T> Function1<T, T> doNotConvert() {
        return (Function1<T, T>) AS_IS;
    }
}
