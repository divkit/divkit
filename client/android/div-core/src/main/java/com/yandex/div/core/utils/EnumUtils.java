package com.yandex.div.core.utils;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Contains methods to safely work with enums.
 */
public class EnumUtils {

    private EnumUtils() { /* do not instantiate */ }

    /**
     * Finds enum value that matches 'enumName' or returns null if didn't find
     * @param <T> any enum
     * @return enum item if found or null otherwise
     */
    @Nullable
    public static <T extends Enum<T>> T findEnum(@Nullable String enumName, @NonNull Class<T> enumType) {
        if (!TextUtils.isEmpty(enumName)) {
            for (T value : enumType.getEnumConstants()) {
                if (value.name().equals(enumName)) {
                    return value;
                }
            }
        }

        return null;
    }
}
