package com.yandex.div.view.menu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import com.yandex.div.core.util.Assert;
import com.yandex.div.core.views.R;
import com.yandex.div.util.Views;
import com.yandex.div.view.NonScrollImageView;

/**
 * Wraps given {@link android.view.View} into {@link android.widget.FrameLayout}
 * with overflow popup menu view item on it.
 */
public class OverflowMenuWrapper {
    @NonNull
    private final Context mContext;
    @NonNull
    private final View mWrappedView;

    @Nullable
    private final ViewGroup mParentView;

    private int mOverflowGravity = Gravity.TOP | Gravity.LEFT;
    @ColorInt
    private int mOverflowColor = Color.WHITE;
    @IntRange(from = 0, to = 255)
    private int mOverflowAlpha = 0xFF;
    private int mMenuGravity = Gravity.LEFT | Gravity.BOTTOM;
    @DrawableRes
    private int mButtonResourceId = R.drawable.ic_more_vert_white_24dp;
    @Nullable
    private Listener mListener;
    @Nullable
    private View[] mHorizontallyCompetingViews = null;
    @Nullable
    private View[] mVerticallyCompetingViews = null;
    @Nullable
    private View mResultView;
    @Nullable
    private ImageView mOverflowMenuImageView;
    private boolean mValid = false;

    @DimenRes
    private final int mMenuHorizontalMargin;
    @DimenRes
    private final int mMenuVerticalMargin;

    @Nullable
    private PopupMenu mPopupMenu;

    public OverflowMenuWrapper(@NonNull Context context, @NonNull View wrapView, @Nullable ViewGroup parent) {
        this(context, wrapView, parent, R.dimen.overflow_menu_margin_horizontal, R.dimen.overflow_menu_margin_vertical);
    }

    public OverflowMenuWrapper(@NonNull Context context, @NonNull View wrapView, @Nullable ViewGroup parent,
                               @DimenRes int menuHorizontalMargin, @DimenRes int menuVerticalMargin) {
        mContext = context;
        mWrappedView = wrapView;
        mParentView = parent;
        mMenuHorizontalMargin = menuHorizontalMargin;
        mMenuVerticalMargin = menuVerticalMargin;
    }

    /**
     * Sets gravity of overflow view. It must be combination of two flags:
     * {@link Gravity#TOP} or {@link Gravity#BOTTOM} and {@link Gravity#LEFT} or {@link Gravity#RIGHT}
     *
     * @param gravity view gravity flags from {@link Gravity} class.
     * @return {@code this} instance.
     */
    @NonNull
    public OverflowMenuWrapper overflowGravity(int gravity) {
        mOverflowGravity = gravity;
        return this;
    }

    /**
     * Sets gravity of popup menu.
     *
     * @param gravity view gravity flags from {@link Gravity} class.
     * @return {@code this} instance.
     */
    @NonNull
    public OverflowMenuWrapper menuGravity(int gravity) {
        mMenuGravity = gravity;
        return this;
    }

    /**
     * Sets color of overflow view.
     *
     * @param color view color
     * @return {@code this} instance.
     */
    @NonNull
    public OverflowMenuWrapper color(@ColorInt int color) {
        mOverflowColor = color;
        return this;
    }

    /**
     * Sets alpha of overflow view.
     *
     * @param alpha view alpha from [0 .. 255] range
     * @return {@code this} instance.
     */
    @NonNull
    public OverflowMenuWrapper alpha(@IntRange(from = 0, to = 255) int alpha) {
        mOverflowAlpha = alpha;
        return this;
    }

    /**
     * Sets event listener. Look at {@link Listener this} for listenable events.
     *
     * @param listener event listener
     * @return {@code this} instance.
     * @see Listener
     */
    @NonNull
    public OverflowMenuWrapper listener(@NonNull Listener listener) {
        mListener = listener;
        return this;
    }

    /**
     * Sets competing views - views that may overlap overflow view horizontally.
     * After {@link #getView()} call, additional padding will be supplied for these views to prevent
     * this overlap.
     *
     * @param competingViews views that may overlap overflow view.
     * @return {@code this} instance.
     */
    @NonNull
    public OverflowMenuWrapper horizontallyCompetingViews(@NonNull View... competingViews) {
        mHorizontallyCompetingViews = competingViews;
        return this;
    }

    /**
     * Sets competing views - views that may overlap overflow view vertically.
     * After {@link #getView()} call, additional padding will be supplied for these views to prevent
     * this overlap.
     *
     * @param competingViews views that may overlap overflow view.
     * @return {@code this} instance.
     */
    @NonNull
    public OverflowMenuWrapper verticallyCompetingViews(@NonNull View... competingViews) {
        mVerticallyCompetingViews = competingViews;
        return this;
    }

    /**
     * Sets the resource id for menu button.
     *
     * @param buttonResourceId resource id.
     * @return {@code this} instance.
     */
    @NonNull
    public OverflowMenuWrapper buttonResourceId(@DrawableRes int buttonResourceId) {
        mButtonResourceId = buttonResourceId;
        return this;
    }

    /**
     * Marks overflow wrapper as not valid, making next {@link #getView()}
     * call reapply style attributes.
     */
    public void invalidate() {
        mValid = false;
    }

    @NonNull
    public View getView() {
        if (mValid && mResultView != null) {
            return mResultView;
        }

        if (mResultView == null || mOverflowMenuImageView == null) {
            mOverflowMenuImageView = createOverflowMenuImageView();
            mResultView = createWrapperView(mOverflowMenuImageView);
        }

        Assert.assertFalse(mValid);

        mOverflowMenuImageView.setImageDrawable(createMenuDrawable(mOverflowMenuImageView));
        // Setup overflow menu
        mOverflowMenuImageView.setOnClickListener(getOnMenuClickListener());

        mValid = true;
        return mResultView;
    }

    public View.OnClickListener getOnMenuClickListener() {
        return view -> {
            final PopupMenu popupMenu = new PopupMenu(view.getContext(), view, mMenuGravity);
            if (mListener != null) {
                mListener.onMenuCreated(popupMenu);
            }
            popupMenu.show();
            if (mListener != null) {
                mListener.onPopupShown();
            }
            mPopupMenu = popupMenu;
        };
    }

    public void setMenuVisibility(int visibility) {
        if (mValid) {
            Assert.assertNotNull("mResultView is null in setMenuVisibility", mResultView);
            mOverflowMenuImageView.setVisibility(visibility);
        }
    }

    public void redrawMenuIcon() {
        if (mValid) {
            Assert.assertNotNull("mResultView is null in redrawMenuIcon", mResultView);
            mOverflowMenuImageView.setImageDrawable(createMenuDrawable(mOverflowMenuImageView));
        }
    }

    @NonNull
    protected Bitmap getBitmapResource(@DrawableRes int resId, @NonNull View useOnView) {
        final Resources resources = mContext.getResources();
        return BitmapFactory.decodeResource(resources, resId);
    }

    @NonNull
    private Drawable createMenuDrawable(View useOnView) {
        final Resources resources = mContext.getResources();
        final BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, getBitmapResource(mButtonResourceId, useOnView));
        final Drawable overflowDrawable = bitmapDrawable.mutate();
        overflowDrawable.setColorFilter(mOverflowColor, PorterDuff.Mode.SRC_IN);
        overflowDrawable.setAlpha(mOverflowAlpha);
        return overflowDrawable;
    }

    @NonNull
    private View createWrapperView(@NonNull ImageView overflowMenuImageView) {
        final FrameLayout frameLayout = new FrameLayout(mContext);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        frameLayout.addView(mWrappedView);
        frameLayout.addView(overflowMenuImageView);

        // Setup competing views padding to prevent overlapping
        if (mHorizontallyCompetingViews != null) {
            final boolean isOnRight = (mOverflowGravity & Gravity.RIGHT) != 0;

            for (final View view : mHorizontallyCompetingViews) {
                Views.setPadding(view, R.dimen.overflow_menu_size, isOnRight ? Views.VIEW_SIDE_RIGHT : Views.VIEW_SIDE_LEFT);
            }
        }

        if (mVerticallyCompetingViews != null) {
            final boolean isOnTop = (mOverflowGravity & Gravity.TOP) != 0;

            for (final View view : mVerticallyCompetingViews) {
                Views.setPadding(view, R.dimen.overflow_menu_size, isOnTop ? Views.VIEW_SIDE_TOP : Views.VIEW_SIDE_BOTTOM);
            }
        }

        return frameLayout;
    }

    private ImageView createOverflowMenuImageView() {
        final Resources resources = mContext.getResources();
        final NonScrollImageView overflowMenu = new NonScrollImageView(mContext);
        final FrameLayout.LayoutParams overflowMenuParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                                         ViewGroup.LayoutParams.WRAP_CONTENT);
        overflowMenuParams.gravity = mOverflowGravity;
        overflowMenu.setLayoutParams(overflowMenuParams);
        overflowMenu.setId(R.id.overflow_menu);

        final int horizontalMargin = resources.getDimensionPixelSize(mMenuHorizontalMargin);
        final int verticalMargin = resources.getDimensionPixelSize(mMenuVerticalMargin);
        overflowMenu.setPadding(horizontalMargin, verticalMargin, horizontalMargin, 0);

        return overflowMenu;
    }

    public void dismiss() {
        if (mPopupMenu != null) {
            mPopupMenu.dismiss();
            mPopupMenu = null;
        }
    }

    public interface Listener {

        /**
         * Called when the popup menu is created. Used to initialize menu.
         */
        void onMenuCreated(@NonNull PopupMenu popupMenu);

        /**
         * Called when popup menu was shown.
         */
        void onPopupShown();

        class Simple implements Listener {

            @Override
            public void onMenuCreated(@NonNull PopupMenu popupMenu) {
            }

            @Override
            public void onPopupShown() {
            }
        }
    }
}
