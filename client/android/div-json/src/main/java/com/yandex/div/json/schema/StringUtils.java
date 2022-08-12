package com.yandex.div.json.schema;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import java.util.Map;
import java.util.regex.Pattern;

public class StringUtils {

    private static final Map<String, Pattern> sPatterns = new ArrayMap<>();

    public static boolean isMatched(@NonNull String str, @NonNull String pattern) {
        Pattern p;
        synchronized (sPatterns) {
            p = sPatterns.get(pattern);
            if (p == null) {
                p = Pattern.compile(pattern);
                sPatterns.put(pattern, p);
            }
        }
        return p.matcher(str).matches();
    }
}
