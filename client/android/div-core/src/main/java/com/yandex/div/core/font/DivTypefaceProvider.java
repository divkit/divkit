package com.yandex.div.core.font;

import android.graphics.Typeface;
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
