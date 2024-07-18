package com.yandex.div.core.font;

import android.graphics.Typeface;
import android.os.Build;

import androidx.annotation.Nullable;
import com.yandex.div.core.annotations.PublicApi;

/**
 * Provides custom font typefaces.
 */
@PublicApi
public interface DivTypefaceProvider {

    DivTypefaceProvider DEFAULT = new DivTypefaceProvider() {
        @Nullable
        @Override
        public Typeface getRegular() {
            return null;
        }

        @Nullable
        @Override
        public Typeface getMedium() {
            return null;
        }

        @Nullable
        @Override
        public Typeface getLight() {
            return null;
        }

        @Nullable
        @Override
        public Typeface getBold() {
            return null;
        }

        @Nullable
        @Override
        public Typeface getTypefaceFor(int weight) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return Typeface.create(Typeface.DEFAULT, weight, false);
            } else {
                return DivTypefaceProvider.super.getTypefaceFor(weight);
            }
        }

        @Nullable
        @Override
        public Typeface getRegularLegacy() {
            return null;
        }
    };

    /**
     * Returns typeface for text elements that require regular font.
     */
    @Nullable
    Typeface getRegular();

    /**
     * Returns typeface for text elements that require medium font.
     */
    @Nullable
    Typeface getMedium();

    /**
     * Returns typeface for text elements that require light font.
     */
    @Nullable
    Typeface getLight();

    /**
     * Returns typeface for text elements that require bold font.
     */
    @Nullable
    Typeface getBold();

    /**
     * Returns typeface for text elements that require bold font.
     */
    @Nullable
    default Typeface getTypefaceFor(int weight) {
        if (weight >= 0 && weight < 350) {
            return getLight();
        } else if (weight >= 350 && weight < 450) {
            return getRegular();
        } else if (weight >= 450 && weight < 600) {
            return getMedium();
        } else {
            return getBold();
        }
    }

    /**
     * Returns typeface for text elements that require regular font.
     * Also includes italic variant. To be used only in legacy elements.
     *
     * @deprecated Use {@link #getRegular()} instead for non-italic font.
     */
    @Nullable
    @Deprecated
    default Typeface getRegularLegacy() {
        return getRegular();
    }
}
