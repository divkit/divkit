package com.yandex.div.json;

import androidx.annotation.NonNull;
import com.yandex.div.internal.Assert;
import com.yandex.div.internal.Log;

public interface ParsingErrorLogger {

    ParsingErrorLogger LOG = (e) -> {
        if (Log.isEnabled()) {
            Log.e("ParsingErrorLogger", "An error occurred during parsing process", e);
        }
    };

    ParsingErrorLogger ASSERT = (e) -> {
        if (Assert.isEnabled()) {
            Assert.fail(e.getMessage(), e);
        }
    };

    void logError(@NonNull Exception e);

    default void logTemplateError(@NonNull Exception e, @NonNull String templateId) {
        logError(e);
    }
}
