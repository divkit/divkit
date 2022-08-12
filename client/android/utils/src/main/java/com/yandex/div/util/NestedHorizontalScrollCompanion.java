package com.yandex.div.util;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.HorizontalScrollView;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

/**
 * Helper class that enables nested-scrolling.
 */
public class NestedHorizontalScrollCompanion {
    private static final int DX_UNCONSUMED = 1;

    private final View mTarget;
    private boolean mCanDispatchNestedScroll;
    private final float mSlop;

    private float mDownX;
    private float mDownY;

    public NestedHorizontalScrollCompanion(@NonNull View target) {
        this(target, getScaledTouchSlop(target));
    }

    @VisibleForTesting
    NestedHorizontalScrollCompanion(@NonNull View target, float slop) {
        mTarget = target;
        ViewCompat.setNestedScrollingEnabled(mTarget, true);
        mSlop = slop;
    }

    /**
     * ViewPager requires special handling because it doesn't dispatch overscroll.
     */
    public NestedHorizontalScrollCompanion(@NonNull ViewPager target) {
        this((View) target, getScaledTouchSlop(target));
    }

    @VisibleForTesting
    NestedHorizontalScrollCompanion(@NonNull ViewPager target, float slop) {
        this((View) target, slop);
        target.addOnPageChangeListener(new NestedScrollPageChangeListener(target));
    }

    /**
     * Trigger at {@link View#onScrollChanged(int, int, int, int)}.
     */
    public void dispatchOnScrollChanged() {
        mCanDispatchNestedScroll = false;
    }

    /**
     * Trigger at {@link View#onOverScrolled(int, int, boolean, boolean)} if view dispatches this method.
     * For ViewPager this method is not required, corresponding listener will be added automatically.
     */
    public void dispatchOnOverScrolled(boolean clampedX) {
        if (mCanDispatchNestedScroll && clampedX) {
            ViewCompat.dispatchNestedScroll(mTarget, 0, 0, DX_UNCONSUMED, 0, null);
        }
    }

    /**
     * Trigger after super call at {@link HorizontalScrollView#dispatchTouchEvent(MotionEvent)}
     * (otherwise super call will reset start of nested scroll).
     */
    public void dispatchTouchEventAfterSuperCall(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mCanDispatchNestedScroll = false;
                ViewCompat.stopNestedScroll(mTarget);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(event.getX() - mDownX);
                float dy = Math.abs(event.getY() - mDownY);
                if (!mCanDispatchNestedScroll && dx >= mSlop && dx > dy) {
                    mCanDispatchNestedScroll = true;
                    ViewCompat.startNestedScroll(mTarget, ViewCompat.SCROLL_AXIS_HORIZONTAL);
                }
                break;
        }
    }

    private static int getScaledTouchSlop(@NonNull View target) {
        return ViewConfiguration.get(target.getContext()).getScaledTouchSlop();
    }

    private class NestedScrollPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        private final ViewPager mViewPager;
        private int mScrollState = -1;
        private float mPreviousPageOffset;

        private NestedScrollPageChangeListener(ViewPager viewPager) {
            mViewPager = viewPager;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) mPreviousPageOffset = -1;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            boolean isLastPosition = position == mViewPager.getAdapter().getCount() - 1;
            boolean isEdgePosition = position == 0 || isLastPosition;
            if (isEdgePosition &&
                    mScrollState == ViewPager.SCROLL_STATE_DRAGGING &&
                    mPreviousPageOffset == 0f &&
                    positionOffset == 0f) {
                dispatchOnOverScrolled(true);
            }
            mPreviousPageOffset = positionOffset;
        }
    }
}
