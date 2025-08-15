package com.yandex.div.internal.widget.tabs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.widget.ViewDragHelper;
import androidx.viewpager.widget.ViewPager;
import com.yandex.div.internal.util.NestedHorizontalScrollCompanion;
import com.yandex.div.internal.widget.OnInterceptTouchEventListener;
import com.yandex.div.internal.widget.OnInterceptTouchEventListenerHost;

import java.util.Set;

public class ScrollableViewPager extends RtlViewPager implements OnInterceptTouchEventListenerHost {

    private final NestedHorizontalScrollCompanion mNestedScrollCompanion = new NestedHorizontalScrollCompanion(this);

    @Nullable
    private ViewDragHelper mViewDragHelper;
    private boolean mIsScrollEnabled = true;
    private boolean mIsEdgeScrollEnabled = true;
    private boolean mIsSwipeLocked = false;
    private boolean mIsScrollLocked = false;
    @Nullable
    private Set<Integer> mDisabledPages;
    @Nullable
    private OnInterceptTouchEventListener mOnInterceptTouchEventListener;

    public ScrollableViewPager(@NonNull Context context) {
        this(context, null);
    }

    public ScrollableViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Nullable
    @Override
    public OnInterceptTouchEventListener getOnInterceptTouchEventListener() {
        return mOnInterceptTouchEventListener;
    }

    @Override
    public void setOnInterceptTouchEventListener(@Nullable OnInterceptTouchEventListener listener) {
        mOnInterceptTouchEventListener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mNestedScrollCompanion.dispatchOnScrollChanged();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = super.dispatchTouchEvent(ev);
        mNestedScrollCompanion.dispatchTouchEventAfterSuperCall(ev);
        return result;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return processTouchEvent(event) && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent event) {
        boolean intercepted = false;
        if (mOnInterceptTouchEventListener != null) {
            intercepted = mOnInterceptTouchEventListener.onInterceptTouchEvent(this, event);
        }
        return intercepted || (processTouchEvent(event) && super.onInterceptTouchEvent(event));
    }

    private boolean processTouchEvent(@NonNull MotionEvent event) {
        if (!mIsEdgeScrollEnabled && mViewDragHelper != null) {
            final int action = event.getAction();
            final boolean pointerUp =
                    (action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN;
            if (pointerUp) {
                mIsSwipeLocked = false;
            }
            mViewDragHelper.processTouchEvent(event);
        }
        if (mDisabledPages != null) {
            mIsScrollLocked = mIsScrollEnabled && mDisabledPages.contains(getCurrentItem());
        }
        return !mIsSwipeLocked && !mIsScrollLocked && mIsScrollEnabled;
    }

    public void setEdgeScrollEnabled(boolean enabled) {
        mIsEdgeScrollEnabled = enabled;
        if (!enabled) {
            mViewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
                @Override
                public boolean tryCaptureView(View view, int i) {
                    return false;
                }

                @Override
                public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                    super.onEdgeDragStarted(edgeFlags, pointerId);
                    mIsSwipeLocked = (edgeFlags & ViewDragHelper.EDGE_RIGHT) != 0
                            || (edgeFlags & ViewDragHelper.EDGE_LEFT) != 0;
                }
            });
            mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_RIGHT | ViewDragHelper.EDGE_LEFT);
        }
    }

    public void setScrollEnabled(boolean enabled) {
        mIsScrollEnabled = enabled;
    }

    public void setDisabledScrollPages(@Nullable Set<Integer> disabledPages) {
        mDisabledPages = disabledPages;
    }
}
