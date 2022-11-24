package com.yandex.div.internal.widget.tabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.ViewCompat;
import com.yandex.div.core.font.DivTypefaceProvider;
import com.yandex.div.core.font.DivTypefaceType;

/**
 * View that represents tab title inside {@link BaseIndicatorTabLayout}.
 */
public final class TabView extends AppCompatTextView {

    private static final String ELLIPSIS_PLACEHOLDER_TO_MEASURE_WIDTH = "...";

    @Nullable
    private DivTypefaceProvider mTypefaceProvider;
    @StyleRes
    private int mTextAppearance;
    private boolean mBoldTextOnSelection;
    private boolean mEllipsizeEnabled;

    @NonNull
    private MaxWidthProvider mMaxWidthProvider = () -> Integer.MAX_VALUE;
    @Nullable
    private OnUpdateListener mOnUpdateListener;
    @Nullable
    private BaseIndicatorTabLayout.Tab mTab;
    @Nullable
    private DivTypefaceType mInactiveTypefaceType;
    @Nullable
    private DivTypefaceType mActiveTypefaceType;

    private boolean mIsActiveType;

    public TabView(@NonNull Context context) {
        this(context, null);
    }

    public TabView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        setClickable(true);
        setMaxLines(1);
        setSingleLine(true);

        OnClickListener defaultAspectJLoggingOnClickListener = v -> {};
        setOnClickListener(defaultAspectJLoggingOnClickListener);
    }

    public void setTabPadding(int start, int top, int end, int bottom) {
        ViewCompat.setPaddingRelative(this, start, top, end, bottom);
    }

    void setTextTypeface(@Nullable DivTypefaceProvider typefaceProvider, @StyleRes int textAppearance) {
        mTypefaceProvider = typefaceProvider;
        mTextAppearance = textAppearance;
        setupTypeface();
    }

    public void setInactiveTypefaceType(@Nullable DivTypefaceType inactiveTypefaceType) {
        mInactiveTypefaceType = inactiveTypefaceType;
    }

    public void setActiveTypefaceType(@Nullable DivTypefaceType activeTypefaceType) {
        mActiveTypefaceType = activeTypefaceType;
    }

    void setTextColorList(@Nullable ColorStateList textColorList) {
        if (textColorList != null) {
            setTextColor(textColorList);
        }
    }

    void setBoldTextOnSelection(boolean boldTextOnSelection) {
        mBoldTextOnSelection = boldTextOnSelection;
    }

    void setEllipsizeEnabled(boolean ellipsizeEnabled) {
        mEllipsizeEnabled = ellipsizeEnabled;
        setEllipsize(ellipsizeEnabled ? TextUtils.TruncateAt.END : null);
    }

    public void setMaxWidthProvider(@NonNull MaxWidthProvider provider) {
        mMaxWidthProvider = provider;
    }

    void setOnUpdateListener(@Nullable OnUpdateListener listener) {
        mOnUpdateListener = listener;
    }

    @Override
    public boolean performClick() {
        final boolean value = super.performClick();

        if (mTab != null) {
            mTab.select();
            return true;
        } else {
            return value;
        }
    }

    @Override
    public void setSelected(boolean selected) {
        final boolean changed = (isSelected() != selected);
        super.setSelected(selected);

        setTypefaceType(selected);

        if (mBoldTextOnSelection && changed) {
            setupTypeface();
        }

        if (changed && selected) {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
        }
    }

    private void setupTypeface() {
        final boolean selected = isSelected();
        // Note: setTypeface must be called after setTextAppearance
        if (!selected) {
            //noinspection deprecation
            setTextAppearance(getContext(), mTextAppearance);
        }
    }

    @Nullable
    private Typeface getDefaultTypeface() {
        if (mTypefaceProvider != null) {
            if (mIsActiveType) {
                if (mActiveTypefaceType != null) {
                    return mActiveTypefaceType.getTypeface(mTypefaceProvider);
                }
            } else {
                if (mInactiveTypefaceType != null) {
                    return mInactiveTypefaceType.getTypeface(mTypefaceProvider);
                }
            }
        }
        return mTypefaceProvider != null ? mTypefaceProvider.getMedium() : null;
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        // This view masquerades as an action bar tab.
        //noinspection deprecation
        event.setClassName(ActionBar.Tab.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        // This view masquerades as an action bar tab.
        //noinspection deprecation
        info.setClassName(ActionBar.Tab.class.getName());
    }

    /**
     * Sets typeface type.
     * @param isActiveType
     */
    public void setTypefaceType(boolean isActiveType) {
        final boolean changed = (mIsActiveType != isActiveType);
        mIsActiveType = isActiveType;
        if (changed) {
            requestLayout();
        }
    }

    @Override
    public void onMeasure(final int origWidthMeasureSpec, final int origHeightMeasureSpec) {
        // Hack: setup typeface if needed without requestLayout() & invalidate()
        final TextPaint textPaint = getPaint();
        if (textPaint != null) {
            final Typeface typeface = getDefaultTypeface();
            if (typeface != null) {
                textPaint.setTypeface(typeface);
            }
        }

        if (!mEllipsizeEnabled) {
            super.onMeasure(origWidthMeasureSpec, origHeightMeasureSpec);
            return;
        }

        final int specWidthSize = MeasureSpec.getSize(origWidthMeasureSpec);
        final int specWidthMode = MeasureSpec.getMode(origWidthMeasureSpec);
        final int maxWidth = mMaxWidthProvider.getMaxWidth();

        final int widthMeasureSpec;

        if (maxWidth > 0 && (specWidthMode == MeasureSpec.UNSPECIFIED
                || specWidthSize > maxWidth)) {
            // If we have a max width and a given spec which is either unspecified or
            // larger than the max width, update the width spec using the same mode
            //noinspection Range
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST);
        } else {
            // Else, use the original width spec
            widthMeasureSpec = origWidthMeasureSpec;
        }

        super.onMeasure(widthMeasureSpec, origHeightMeasureSpec);

        fixTextEllipsis(widthMeasureSpec, origHeightMeasureSpec);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setAlpha(enabled ? 1.0f : 0.2f);
    }

    @SuppressLint("WrongCall")
    private void fixTextEllipsis(final int widthMeasureSpec, final int heightMeasureSpec) {
        final Layout layout = getLayout();
        if (layout == null || layout.getEllipsisCount(0) <= 0) {
            return;
        }

        if (mTab == null) {
            return;
        }

        CharSequence text = mTab.getText();
        if (text == null) {
            return;
        }

        final TextPaint textPaint = layout.getPaint();
        if (textPaint == null) {
            return;
        }

        TransformationMethod trans = getTransformationMethod();
        if (trans != null) {
            text = trans.getTransformation(text, this);
        }

        if (text == null) {
            return;
        }

        final int currentWidth = (int) layout.getLineMax(0);
        final float ellipsisWidth = textPaint.measureText(ELLIPSIS_PLACEHOLDER_TO_MEASURE_WIDTH);
        text = TextUtils.ellipsize(text, textPaint, currentWidth - ellipsisWidth, TextUtils.TruncateAt.END);

        setText(text);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    void setTab(@Nullable final BaseIndicatorTabLayout.Tab tab) {
        if (tab != mTab) {
            mTab = tab;
            update();
        }
    }

    void reset() {
        setTab(null);
        setSelected(false);
    }

    void update() {
        setText(mTab == null ? null : mTab.getText());

        if (mOnUpdateListener != null) {
            mOnUpdateListener.onUpdated(this);
        }
    }

    interface OnUpdateListener {
        void onUpdated(@NonNull TabView tabView);
    }

    interface MaxWidthProvider {
        int getMaxWidth();
    }
}
