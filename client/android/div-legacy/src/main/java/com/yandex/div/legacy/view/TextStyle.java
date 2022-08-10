package com.yandex.div.legacy.view;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import com.yandex.alicekit.core.views.EllipsizingTextView;
import com.yandex.alicekit.core.views.EllipsizingUtils;
import javax.inject.Provider;

public class TextStyle {

    @NonNull
    private final Provider<Typeface> mTypefaceProvider;
    @ColorRes
    private final int mColor;

    @DimenRes
    private final int mTextSizeDimenRes;
    private final int mTextAlignment;

    @DimenRes
    private final int mLetterSpacingDimenRes;
    private final double mLetterSpacing;

    @DimenRes
    private final int mLineSpaceExtra;
    @Px
    private final int mLineHeight;

    @Nullable
    private final Integer mMaxLines;
    private final char mEllipsis;

    @Px
    private int mTextSize;

    private TextStyle(@NonNull Provider<Typeface> typefaceProvider, @ColorRes int color, @Px int textSize, @DimenRes int textSizeDimenRes,
                      @DimenRes int letterSpacingDimenRes, int textAlignment, double letterSpacing,
                      @DimenRes int lineSpaceExtra, int lineHeight, @Nullable Integer maxLines, char ellipsis) {
        mTypefaceProvider = typefaceProvider;
        mColor = color;
        mTextSize = textSize;
        mTextSizeDimenRes = textSizeDimenRes;
        mTextAlignment = textAlignment;
        mLetterSpacingDimenRes = letterSpacingDimenRes;
        mLetterSpacing = letterSpacing;
        mLineSpaceExtra = lineSpaceExtra;
        mLineHeight = lineHeight;
        mMaxLines = maxLines;
        mEllipsis = ellipsis;
    }

    public TextStyle(@NonNull TextStyle styleToCopy, Integer maxLines) {
        this(styleToCopy.mTypefaceProvider, styleToCopy.mColor, styleToCopy.mTextSize, styleToCopy.mTextSizeDimenRes,
             styleToCopy.mLetterSpacingDimenRes, styleToCopy.mTextAlignment, styleToCopy.mLetterSpacing,
             styleToCopy.mLineSpaceExtra,
             styleToCopy.mLineHeight, maxLines, styleToCopy.mEllipsis);
    }

    @Px
    public int getTextSize() {
        return mTextSize;
    }

    public void apply(@NonNull TextView textView) {
        textView.setTypeface(mTypefaceProvider.get());
        textView.setTextColor(ContextCompat.getColor(textView.getContext(), mColor));

        final Resources resources = textView.getResources();
        if (mTextSize <= 0) {
            mTextSize = resources.getDimensionPixelSize(mTextSizeDimenRes);
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        if (mLineHeight > 0) {
            int lineSpacingExtra = mLineHeight - textView.getLineHeight();
            textView.setLineSpacing(lineSpacingExtra, 1);
        } else {
            textView.setLineSpacing(resources.getDimensionPixelSize(mLineSpaceExtra), 1);
        }
        textView.setTextAlignment(mTextAlignment);
        float letterSpacing = (float) mLetterSpacing;
        if (mLetterSpacingDimenRes != 0) {
            final TypedValue outValue = new TypedValue();
            resources.getValue(mLetterSpacingDimenRes, outValue, true);
            letterSpacing = outValue.getFloat();
        }
        textView.setLetterSpacing(letterSpacing);
    }

    void applyWithEllipsizig(@NonNull AppCompatTextView textView) {
        apply(textView);
        if (mMaxLines != null) {
            textView.setMaxLines(mMaxLines);
        }
        if (textView instanceof EllipsizingTextView) {
            ((EllipsizingTextView) textView).setEllipsis(mEllipsis);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (object == this) {
            return true;
        }

        if (!(object instanceof TextStyle)) {
            return false;
        }

        TextStyle style = (TextStyle) object;

        if (!(mColor == style.mColor && mTextSize == style.mTextSize && mLetterSpacingDimenRes == style.mLetterSpacingDimenRes &&
                mLineSpaceExtra == style.mLineSpaceExtra && mTypefaceProvider.get().equals(
                style.mTypefaceProvider.get()))) {
            return false;
        }

        return (mMaxLines == null && style.mMaxLines == null) ||
                (mMaxLines != null && style.mMaxLines != null && mMaxLines.equals(style.mMaxLines));
    }

    @Override
    public int hashCode() {
        return mTypefaceProvider.get().hashCode();
    }

    public static class Builder {

        private Provider<Typeface> mTypefaceProvider;
        private int mColor;
        private int mTextSize = 0;
        private int mLetterSpacingDimenRes;
        private double mLetterSpacing;
        private int mLineSpaceExtra;
        private int mLineHeight;
        private int mTextSizeDimenRes;
        private int mTextAlignment = View.TEXT_ALIGNMENT_TEXT_START;
        private Integer mMaxLines = null;
        private char mEllipsis = EllipsizingUtils.ELLIPSIS;

        @NonNull
        public Builder setTypefaceProvider(@NonNull Provider<Typeface> typefaceProvider) {
            mTypefaceProvider = typefaceProvider;
            return this;
        }

        @NonNull
        public Builder setColor(int color) {
            mColor = color;
            return this;
        }

        @NonNull
        public Builder setTextSize(int textSize) {
            mTextSize = textSize;
            return this;
        }

        @NonNull
        public Builder setLetterSpacing(double letterSpacing) {
            mLetterSpacing = letterSpacing;
            return this;
        }

        @NonNull
        public Builder setLetterSpacingDimenRes(@DimenRes int letterSpacing) {
            mLetterSpacingDimenRes = letterSpacing;
            return this;
        }

        @NonNull
        public Builder setLineSpaceExtra(@DimenRes int lineSpaceExtra) {
            mLineSpaceExtra = lineSpaceExtra;
            return this;
        }

        @NonNull
        public Builder setLineHeight(int lineHeight) {
            mLineHeight = lineHeight;
            return this;
        }

        @NonNull
        public Builder setTextSizeDimenRes(int textSizeDimenRes) {
            mTextSizeDimenRes = textSizeDimenRes;
            return this;
        }

        @NonNull
        public Builder setTextAlignment(int textAlignment) {
            mTextAlignment = textAlignment;
            return this;
        }

        @NonNull
        public Builder setMaxLines(@Nullable Integer maxLines) {
            mMaxLines = maxLines;
            return this;
        }

        @NonNull
        public Builder setEllipsis(char ellipsis) {
            mEllipsis = ellipsis;
            return this;
        }

        @NonNull
        public TextStyle createTextStyle() {
            return new TextStyle(mTypefaceProvider, mColor, mTextSize, mTextSizeDimenRes, mLetterSpacingDimenRes,
                                 mTextAlignment, mLetterSpacing,
                                 mLineSpaceExtra, mLineHeight, mMaxLines, mEllipsis);
        }
    }
}
