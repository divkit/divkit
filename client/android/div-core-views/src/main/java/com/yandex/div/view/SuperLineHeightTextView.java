package com.yandex.div.view;

import android.content.Context;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * For some reason line height is ignored on one-line TextViews in Android.
 */
public class SuperLineHeightTextView extends EllipsizedTextView {
    private int mLineSpacingExtraTop = 0;
    private int mLineSpacingExtraBottom = 0;
    private boolean mShouldAddExtraSpacing = true;

    public SuperLineHeightTextView(Context context) {
        this(context, null, 0);
    }

    public SuperLineHeightTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperLineHeightTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        if (!isInternalTextChange()) {
            invalidateTextPadding();
        }
    }

    private void invalidateTextPadding() {
        mShouldAddExtraSpacing = true;
        mLineSpacingExtraTop = 0;
        mLineSpacingExtraBottom = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (shouldReapplyExtraSpacing()) {
            invalidateTextPadding();
        }
        if (fixLineHeight()) {
            int measuredHeightAndState = getMeasuredHeightAndState();
            measuredHeightAndState = MeasureSpec.makeMeasureSpec(
                    MeasureSpec.getSize(measuredHeightAndState) + mLineSpacingExtraTop + mLineSpacingExtraBottom,
                    MeasureSpec.getMode(measuredHeightAndState)
            );
            super.setMeasuredDimension(getMeasuredWidthAndState(), measuredHeightAndState);
        }
        setLastMeasuredHeight(getMeasuredHeight());
    }

    private boolean shouldReapplyExtraSpacing() {
        final int lastMeasuredHeight = getLastMeasuredHeight();
        if (lastMeasuredHeight == NOT_SET) {
            return false;
        }

        if (mLineSpacingExtraTop == 0 && mLineSpacingExtraBottom == 0) {
            return false;
        }

        return lastMeasuredHeight - getMeasuredHeight() != 0;
    }

    @Override
    public int getCompoundPaddingTop() {
        return super.getCompoundPaddingTop() + mLineSpacingExtraTop;
    }

    @Override
    public int getCompoundPaddingBottom() {
        return super.getCompoundPaddingBottom() + mLineSpacingExtraBottom;
    }

    /**
     * For some reason line height is ignored on one-line TextViews in Android.
     *
     * Note: this method must be called after the width is measured
     */
    private boolean fixLineHeight() {
        final int availableWidth = availableWidth();
        final CharSequence text = getText();
        final Layout textLayout = getLayout();
        float lineSpacingExtra = getLineSpacingExtra();

        if (textLayout == null) {
            return false;
        }

        if (mShouldAddExtraSpacing && availableWidth > 0 && lineSpacingExtra > 0 && !TextUtils.isEmpty(text) &&
                getLayout().getLineCount() == 1) {
            mLineSpacingExtraTop = Math.round(lineSpacingExtra / 2f);
            mLineSpacingExtraBottom = (int) lineSpacingExtra / 2;
            mShouldAddExtraSpacing = false;
            return true;
        }
        return false;
    }
}
