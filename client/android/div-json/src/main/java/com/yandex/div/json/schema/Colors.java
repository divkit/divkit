package com.yandex.div.json.schema;

import android.graphics.Color;
import android.text.TextUtils;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Colors {

    private Colors() {
        // prevent instantiation
    }

    @ColorInt
    @Nullable
    public static Integer parseColor(@Nullable final String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }

        try {
            return parseNonNullColor(text);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @ColorInt
    public static int parseNonNullColor(@NonNull String text) throws IllegalArgumentException {
        String toParse;
        if (text.charAt(0) == '#') {
            // Given format should be one of #RGB/#ARGB or #AARRGGBB/#RRGGBB
            // Last two are successfully parsable by android.graphics.Color.parseColor(String), we should take
            // care of first two formats
            if (text.length() == 5) {
                // Assuming correct format #ARGB, transform it to #AARRGGBB
                char a = text.charAt(1);
                char r = text.charAt(2);
                char g = text.charAt(3);
                char b = text.charAt(4);
                toParse = new String(new char[]{'#', a, a, r, r, g, g, b, b});
            } else if (text.length() == 4) {
                // Assuming correct format #RGB, transform it to #RRGGBB
                char r = text.charAt(1);
                char g = text.charAt(2);
                char b = text.charAt(3);
                toParse = new String(new char[]{'#', r, r, g, g, b, b});
            } else {
                // Assuming correct format #AARRGGBB/#RRGGBB
                toParse = text;
            }
        } else {
            toParse = text;
        }

        return Color.parseColor(toParse);
    }
}
