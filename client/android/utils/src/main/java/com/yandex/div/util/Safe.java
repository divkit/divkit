package com.yandex.div.util;

import androidx.annotation.Nullable;

/**
 * Contains methods to safely compare objects.
 */
public final class Safe {

    private Safe() {
    }

    /**
     * Compares two objects using {@link Object#equals(Object)} method.
     *
     * It is an easy way to call {@link Object#equals(Object)} when both operands might by null. Note that nulls are equal.
     *
     * Secondly it allows to limit the over-generic interface of {@link Object#equals(Object)} that compares anything with anything.
     * Specify generic parameter T explicitly to be sure to compare compatible values.
     */
    public static <T> boolean equal(@Nullable T one, @Nullable T another) {
        return one == null ? another == null : one.equals(another);
    }

    public static <T> boolean notEqual(@Nullable T one, @Nullable T another) {
        return !Safe.equal(one, another);
    }
}
