package com.yandex.div.core.font;

import android.graphics.Typeface;
import androidx.annotation.NonNull;

public enum DivTypefaceType {
    REGULAR, MEDIUM, BOLD, LIGHT;

    public Typeface getTypeface(@NonNull DivTypefaceProvider typefaceProvider) {
        switch (this) {
            case BOLD: return typefaceProvider.getBold();
            case MEDIUM: return typefaceProvider.getMedium();
            case LIGHT: return typefaceProvider.getLight();
            default: return typefaceProvider.getRegular();
        }
    }
}
