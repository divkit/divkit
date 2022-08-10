package com.yandex.div.view.tabs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import com.yandex.div.core.views.R;

/**
 * An auxiliary view, that embraces {@link ViewPager} and controls its height by setting
 * its own height. The height value is calculated at the latest moment possible. It is assumed
 * that a calculator delegate would iterate over pager contents and estimate its height.
 */
public class ViewPagerFixedSizeLayout extends FrameLayout {
    @Nullable
    private HeightCalculator mHeightCalculator;

    private int mCollapsiblePaddingBottom = 0;

    public ViewPagerFixedSizeLayout(Context context) {
        super(context);
    }

    public ViewPagerFixedSizeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray style =
                context.obtainStyledAttributes(attrs, R.styleable.ViewPagerFixedSizeLayout);
        mCollapsiblePaddingBottom =
                style.getDimensionPixelSize(R.styleable.ViewPagerFixedSizeLayout_collapsiblePaddingBottom, 0);
        style.recycle();
    }

    public ViewPagerFixedSizeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray style =
                context.obtainStyledAttributes(attrs, R.styleable.ViewPagerFixedSizeLayout, defStyleAttr, 0);
        mCollapsiblePaddingBottom =
                style.getDimensionPixelSize(R.styleable.ViewPagerFixedSizeLayout_collapsiblePaddingBottom, 0);
        style.recycle();
    }

    public void setHeightCalculator(@Nullable HeightCalculator heightCalculator) {
        mHeightCalculator = heightCalculator;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mHeightCalculator != null) {
            int newHeight = mHeightCalculator.measureHeight(widthMeasureSpec, heightMeasureSpec);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setCollapsiblePaddingBottom(int padding) {
        if (mCollapsiblePaddingBottom != padding) {
            mCollapsiblePaddingBottom = padding;
            // TODO(gulevsky): notify padding changed
        }
    }

    public int getCollapsiblePaddingBottom() {
        return mCollapsiblePaddingBottom;
    }

    public interface HeightCalculator {
        void setPositionAndOffsetForMeasure(int position, float positionOffset);
        int measureHeight(int widthMeasureSpec, int heightMeasureSpec);
        boolean shouldRequestLayoutOnScroll(int position, float positionOffset);
        void dropMeasureCache();
        void saveInstanceState(@NonNull SparseArray<Parcelable> container);
        void restoreInstanceState(@NonNull SparseArray<Parcelable> container);
    }
}
