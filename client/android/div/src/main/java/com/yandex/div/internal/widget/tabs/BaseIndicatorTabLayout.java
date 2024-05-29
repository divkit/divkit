package com.yandex.div.internal.widget.tabs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.util.Pools;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.yandex.div.R;
import com.yandex.div.core.font.DivTypefaceProvider;
import com.yandex.div.core.util.ViewsKt;
import com.yandex.div.core.view2.divs.BaseDivViewExtensionsKt;
import com.yandex.div.core.view2.reuse.InputFocusTracker;
import com.yandex.div.internal.Log;
import com.yandex.div.internal.util.NestedHorizontalScrollCompanion;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING;
import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE;
import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_SETTLING;

/**
 * TabLayout provides a horizontal layout to display tabs.
 *
 * <p>Population of the tabs to display is
 * done through {@link Tab} instances. You create tabs via {@link #newTab()}. From there you can
 * change the tab's label via {@link Tab#setText(int)}
 * respectively. To display the tab, you need to add it to the layout via one of the
 * {@link #addTab(Tab)} methods. For example:
 * <pre>
 * TabLayout tabLayout = ...;
 * tabLayout.addTab(tabLayout.newTab().setText("Tab 1"));
 * tabLayout.addTab(tabLayout.newTab().setText("Tab 2"));
 * tabLayout.addTab(tabLayout.newTab().setText("Tab 3"));
 * </pre>
 * You should set a listener via {@link #setOnTabSelectedListener(OnTabSelectedListener)} to be
 * notified when any tab's selection state has been changed.
 * <p>
 * If you're using a {@link ViewPager} together
 * with this layout, you can use {@link #setupWithViewPager(ViewPager)} to link the two together.
 * This layout will be automatically populated from the {@link PagerAdapter}'s page titles.</p>
 *
 * <p>You can also add items to TabLayout in your layout through the use of {@link TabItem}.
 * An example usage is like so:</p>
 *
 * <pre>
 * &lt;android.support.design.widget.TabLayout
 *         android:layout_height=&quot;wrap_content&quot;
 *         android:layout_width=&quot;match_parent&quot;&gt;
 *
 *     &lt;android.support.design.widget.TabItem
 *             android:text=&quot;@string/tab_text&quot;/&gt;
 *
 * &lt;/android.support.design.widget.TabLayout&gt;
 * </pre>
 *
 *
 * @see <a href="http://www.google.com/design/spec/components/tabs.html">Tabs</a>
 *
 * @attr ref android.support.design.R.styleable#TabLayout_tabPadding
 * @attr ref android.support.design.R.styleable#TabLayout_tabPaddingStart
 * @attr ref android.support.design.R.styleable#TabLayout_tabPaddingTop
 * @attr ref android.support.design.R.styleable#TabLayout_tabPaddingEnd
 * @attr ref android.support.design.R.styleable#TabLayout_tabPaddingBottom
 * @attr ref android.support.design.R.styleable#TabLayout_tabContentStart
 * @attr ref android.support.design.R.styleable#TabLayout_tabMinWidth
 * @attr ref android.support.design.R.styleable#TabLayout_tabMaxWidth
 * @attr ref android.support.design.R.styleable#TabLayout_tabTextAppearance
 */
@SuppressWarnings("checkstyle:all")
public class BaseIndicatorTabLayout extends HorizontalScrollView {
    private static final int INVALID_WIDTH = -1;
    private static final int DEFAULT_HEIGHT = 44; // dps
    private static final int TAB_MIN_WIDTH_MARGIN = 56; //dps

    private static final int ANIMATION_DURATION = 300;
    private static final TimeInterpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();

    private static final Pools.Pool<Tab> sTabPool = new Pools.SynchronizedPool<>(16);

    public static final int UNDEFINED_COLOR = -1;

    /**
     * Scrollable tabs display a subset of tabs at any given moment, and can contain longer tab
     * labels and a larger number of tabs. They are best used for browsing contexts in touch
     * interfaces when users don’t need to directly compare the tab labels.
     *
     * @see #setTabMode(int)
     * @see #getTabMode()
     */
    public static final int MODE_SCROLLABLE = 0;

    /**
     * Fixed tabs display all tabs concurrently and are best used with content that benefits from
     * quick pivots between tabs. The maximum number of tabs is limited by the view’s width.
     * Fixed tabs have equal width, based on the widest tab label.
     *
     * @see #setTabMode(int)
     * @see #getTabMode()
     */
    public static final int MODE_FIXED = 1;

    @IntDef(value = {MODE_SCROLLABLE, MODE_FIXED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {}

    /**
     * Callback interface invoked when a tab's selection state changes.
     */
    public interface OnTabSelectedListener {

        /**
         * Called when a tab enters the selected state.
         *
         * @param tab The tab that was selected
         */
        void onTabSelected(Tab tab);

        /**
         * Called when a tab exits the selected state.
         *
         * @param tab The tab that was unselected
         */
        void onTabUnselected(Tab tab);

        /**
         * Called when a tab that is already selected is chosen again by the user. Some applications
         * may use this action to return to the top level of a category.
         *
         * @param tab The tab that was reselected.
         */
        void onTabReselected(Tab tab);
    }

    private final ArrayList<Tab> mTabs = new ArrayList<>();
    private Tab mSelectedTab;

    private final OvalIndicators mTabIndicators;

    private int mTabPaddingStart;
    private int mTabPaddingTop;
    private int mTabPaddingEnd;
    private int mTabPaddingBottom;

    private long mAnimationDuration = ANIMATION_DURATION;

    private int mTabTextAppearance;
    private DivTypefaceProvider mTypefaceProvider = DivTypefaceProvider.DEFAULT;
    private ColorStateList mTabTextColors;
    private boolean mTabTextBoldOnSelection;

    private int mTabMaxWidth = Integer.MAX_VALUE;
    private final int mRequestedTabMinWidth;
    private final int mRequestedTabMaxWidth;
    private final int mScrollableTabMinWidth;

    private final boolean mIsTabEllipsizeEnabled;
    private final boolean mTabScrollPaddingEnabled;
    private final int mTabScrollPadding;
    private final NestedHorizontalScrollCompanion mNestedScrollCompanion = new NestedHorizontalScrollCompanion(this);

    private int mContentInsetStart;
    private int mContentInsetEnd;

    private int mMode;

    private OnTabSelectedListener mOnTabSelectedListener;

    private ValueAnimator mScrollAnimator;

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private DataSetObserver mPagerAdapterObserver;
    private TabLayoutOnPageChangeListener mPageChangeListener;
    private final TabTitleDelimitersController mTabTitleDelimitersController;
    @Nullable
    private InputFocusTracker mInputFocusTracker;

    // Pool we use as a simple RecyclerBin
    @NonNull
    private final Pools.Pool<TabView> mTabViewPool = new Pools.SimplePool<>(12);

    public BaseIndicatorTabLayout(Context context) {
        this(context, null);
    }

    public BaseIndicatorTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("PrivateResource")
    public BaseIndicatorTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Disable the Scroll Bar
        setHorizontalScrollBarEnabled(false);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabLayout,
                                                      defStyleAttr, R.style.Widget_Div_BaseIndicatorTabLayout);

        TypedArray b = context.obtainStyledAttributes(attrs, R.styleable.BaseIndicatorTabLayout, 0, 0);
        int indicatorPaddingTop = b.getDimensionPixelSize(R.styleable.BaseIndicatorTabLayout_tabIndicatorPaddingTop, 0);
        int indicatorPaddingBottom = b.getDimensionPixelSize(R.styleable.BaseIndicatorTabLayout_tabIndicatorPaddingBottom, 0);
        mTabTextBoldOnSelection = b.getBoolean(R.styleable.BaseIndicatorTabLayout_tabTextBoldOnSelection, false);
        mContentInsetEnd = b.getDimensionPixelSize(R.styleable.BaseIndicatorTabLayout_tabContentEnd, 0);
        mIsTabEllipsizeEnabled = b.getBoolean(R.styleable.BaseIndicatorTabLayout_tabEllipsizeEnabled, true);
        mTabScrollPaddingEnabled = b.getBoolean(R.styleable.BaseIndicatorTabLayout_tabScrollPaddingEnabled, false);
        mTabScrollPadding = b.getDimensionPixelSize(R.styleable.BaseIndicatorTabLayout_tabScrollPadding, 0);
        b.recycle();

        mTabIndicators = new OvalIndicators(context, indicatorPaddingTop, indicatorPaddingBottom);
        super.addView(mTabIndicators, 0, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));

        //todo Indicator height is currently not supported
        mTabIndicators.setIndicatorHeight(
                a.getDimensionPixelSize(R.styleable.TabLayout_tabIndicatorHeight, 0));
        mTabIndicators.setSelectedIndicatorColor(a.getColor(R.styleable.TabLayout_tabIndicatorColor, 0));
        mTabIndicators.setIndicatorColor(a.getColor(R.styleable.TabLayout_tabBackground, 0));

        mTabTitleDelimitersController = new TabTitleDelimitersController(getContext(), mTabIndicators);

        mTabPaddingStart = mTabPaddingTop = mTabPaddingEnd = mTabPaddingBottom = a
                .getDimensionPixelSize(R.styleable.TabLayout_tabPadding, 0);
        mTabPaddingStart = a.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingStart,
                                                   mTabPaddingStart);
        mTabPaddingTop = a.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingTop,
                                                 mTabPaddingTop);
        mTabPaddingEnd = a.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingEnd,
                                                 mTabPaddingEnd);
        mTabPaddingBottom = a.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingBottom,
                                                    mTabPaddingBottom);

        mTabTextAppearance = a.getResourceId(R.styleable.TabLayout_tabTextAppearance,
                                             R.style.TextAppearance_Div_Tab);

        // Text colors/sizes come from the text appearance first
        final TypedArray ta = context.obtainStyledAttributes(mTabTextAppearance,
                                                             androidx.appcompat.R.styleable.TextAppearance);
        try {
            mTabTextColors = ta.getColorStateList(androidx.appcompat.R.styleable.TextAppearance_android_textColor);
        } finally {
            ta.recycle();
        }

        if (a.hasValue(R.styleable.TabLayout_tabTextColor)) {
            // If we have an explicit text color set, use it instead
            mTabTextColors = a.getColorStateList(R.styleable.TabLayout_tabTextColor);
        }

        if (a.hasValue(R.styleable.TabLayout_tabSelectedTextColor)) {
            // We have an explicit selected text color set, so we need to make merge it with the
            // current colors. This is exposed so that developers can use theme attributes to set
            // this (theme attrs in ColorStateLists are Lollipop+)
            final int selected = a.getColor(R.styleable.TabLayout_tabSelectedTextColor, 0);
            mTabTextColors = createColorStateList(mTabTextColors.getDefaultColor(), selected);
        }

        mRequestedTabMinWidth = a.getDimensionPixelSize(R.styleable.TabLayout_tabMinWidth,
                                                        INVALID_WIDTH);
        mRequestedTabMaxWidth = a.getDimensionPixelSize(R.styleable.TabLayout_tabMaxWidth,
                                                        INVALID_WIDTH);
        mContentInsetStart = a.getDimensionPixelSize(R.styleable.TabLayout_tabContentStart, 0);
        mMode = a.getInt(R.styleable.TabLayout_tabMode, MODE_FIXED);
        a.recycle();

        // TODO add attr for these
        final Resources res = getResources();
        mScrollableTabMinWidth = res.getDimensionPixelSize(R.dimen.tab_scrollable_min_width);

        // Now apply the tab mode and gravity
        applyModeAndGravity();
    }

    /**
     * Sets the tab indicator's color for the currently selected tab.
     *
     * @param color color to use for the indicator
     * @attr ref android.support.design.R.styleable#TabLayout_tabIndicatorColor
     */
    public void setSelectedTabIndicatorColor(@ColorInt int color) {
        mTabIndicators.setSelectedIndicatorColor(color);
    }

    /**
     * Sets the tab background color.
     *
     * @param backgroundColor background color for the tab
     * @attr ref android.support.design.R.styleable#TabLayout_tabBackgroundColor
     */
    public void setTabBackgroundColor(@ColorInt int backgroundColor) {
        mTabIndicators.setIndicatorColor(backgroundColor);
    }

    /**
     * Sets the tab indicator's corners.
     * Each corner receives two radius values [X, Y].
     * The corners are ordered top-left, top-right, bottom-right, bottom-left.
     *
     * @param radii Array of 8 values, 4 pairs of [X,Y] radii
     */
    public void setTabIndicatorCornersRadii(@NonNull float[] radii) {
        mTabIndicators.setIndicatorCornersRadii(radii);
    }

    /**
     * Sets the item spacing between tabs.
     *
     * @param itemSpacing value of item spacing between tabs
     */
    public void setTabItemSpacing(int itemSpacing) {
        mTabIndicators.setItemSpacing(itemSpacing);
    }

    public void setAnimationType(AnimationType animationType) {
        mTabIndicators.setAnimationType(animationType);
    }

    public void setAnimationDuration(long duration) {
        mAnimationDuration = duration;
    }

    /**
     * Sets the tab indicator's height for the currently selected tab.
     * todo Indicator height is currently not supported.
     *
     * @param height height to use for the indicator in pixels
     * @attr ref android.support.design.R.styleable#TabLayout_tabIndicatorHeight
     */
    public void setTabIndicatorHeight(int height) {
        mTabIndicators.setIndicatorHeight(height);
    }

    /**
     * Set the scroll position of the tabs. This is useful for when the tabs are being displayed as
     * part of a scrolling container such as {@link ViewPager}.
     * <p>
     * Calling this method does not update the selected tab, it is only used for drawing purposes.
     *
     * @param position current scroll position
     * @param positionOffset Value from [0, 1) indicating the offset from {@code position}.
     * @param updateSelectedText Whether to update the text's selected state.
     */
    public void setScrollPosition(int position, float positionOffset, boolean updateSelectedText) {
        setScrollPosition(position, positionOffset, updateSelectedText, true);
    }

    private void setScrollPosition(int position, float positionOffset, boolean updateSelectedText,
                                   boolean updateIndicatorPosition) {
        final int roundedPosition = Math.round(position + positionOffset);
        if (roundedPosition < 0 || roundedPosition >= mTabIndicators.getChildCount()) {
            return;
        }

        // Set the indicator position, if enabled
        if (updateIndicatorPosition) {
            mTabIndicators.setSelectedIndicatorPositionFromTabPosition(position, positionOffset);
        }

        // Now update the scroll position, canceling any running animation
        if (mScrollAnimator != null && mScrollAnimator.isRunning()) {
            mScrollAnimator.cancel();
        }
        scrollTo(calculateScrollXForTab(position, positionOffset), 0);

        if (updateSelectedText) {
            setSelectedTabView(roundedPosition);
        }
    }

    /**
     * Set tab title paddings
     * @param start
     * @param top
     * @param end
     * @param bottom
     */
    public void setTabPaddings(int start, int top, int end, int bottom) {
        mTabPaddingStart = start;
        mTabPaddingTop = top;
        mTabPaddingEnd = end;
        mTabPaddingBottom = bottom;
        requestLayout();
    }

    /**
     * Add a tab to this layout. The tab will be added at the end of the list.
     * If this is the first tab to be added it will become the selected tab.
     *
     * @param tab Tab to add
     */
    public void addTab(@NonNull Tab tab) {
        addTab(tab, mTabs.isEmpty());
    }

    /**
     * Add a tab to this layout. The tab will be inserted at <code>position</code>.
     * If this is the first tab to be added it will become the selected tab.
     *
     * @param tab The tab to add
     * @param position The new position of the tab
     */
    @SuppressWarnings("unused")
    public void addTab(@NonNull Tab tab, int position) {
        addTab(tab, position, mTabs.isEmpty());
    }

    /**
     * Add a tab to this layout. The tab will be added at the end of the list.
     *
     * @param tab Tab to add
     * @param setSelected True if the added tab should become the selected tab.
     */
    public void addTab(@NonNull Tab tab, boolean setSelected) {
        if (tab.mParent != this) {
            throw new IllegalArgumentException("Tab belongs to a different TabLayout.");
        }

        addTabView(tab, setSelected);
        configureTab(tab, mTabs.size());
        if (setSelected) {
            tab.select();
        }
    }

    /**
     * Add a tab to this layout. The tab will be inserted at <code>position</code>.
     *
     * @param tab The tab to add
     * @param position The new position of the tab
     * @param setSelected True if the added tab should become the selected tab.
     */
    public void addTab(@NonNull Tab tab, int position, boolean setSelected) {
        if (tab.mParent != this) {
            throw new IllegalArgumentException("Tab belongs to a different TabLayout.");
        }

        addTabView(tab, position, setSelected);
        configureTab(tab, position);
        if (setSelected) {
            tab.select();
        }
    }

    private void addTabFromItemView(@NonNull TabItem item) {
        final Tab tab = newTab();
        if (item.text != null) {
            tab.setText(item.text);
        }
        addTab(tab);
    }

    /**
     * Set the {@link OnTabSelectedListener} that will
     * handle switching to and from tabs.
     *
     * @param onTabSelectedListener Listener to handle tab selection events
     */
    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        mOnTabSelectedListener = onTabSelectedListener;
    }

    /**
     * Create and return a new {@link Tab}. You need to manually add this using
     * {@link #addTab(Tab)} or a related method.
     *
     * @return A new Tab
     * @see #addTab(Tab)
     */
    @NonNull
    public Tab newTab() {
        Tab tab = sTabPool.acquire();
        if (tab == null) {
            tab = new Tab();
        }
        tab.mParent = this;
        tab.mView = getTabView(tab);
        return tab;
    }

    /**
     * Returns the number of tabs currently registered with the action bar.
     *
     * @return Tab count
     */
    public int getTabCount() {
        return mTabs.size();
    }

    /**
     * Returns the tab at the specified index.
     */
    @Nullable
    public Tab getTabAt(int index) {
        return mTabs.get(index);
    }

    /**
     * Returns the position of the current selected tab.
     *
     * @return selected tab position, or {@code -1} if there isn't a selected tab.
     */
    public int getSelectedTabPosition() {
        return mSelectedTab != null ? mSelectedTab.getPosition() : -1;
    }

    /**
     * Remove a tab from the layout. If the removed tab was selected it will be deselected
     * and another tab will be selected if present.
     *
     * @param tab The tab to remove
     */
    @SuppressWarnings("unused")
    public void removeTab(Tab tab) {
        if (tab.mParent != this) {
            throw new IllegalArgumentException("Tab does not belong to this TabLayout.");
        }

        removeTabAt(tab.getPosition());
    }

    /**
     * Remove a tab from the layout. If the removed tab was selected it will be deselected
     * and another tab will be selected if present.
     *
     * @param position Position of the tab to remove
     */
    public void removeTabAt(int position) {
        final int selectedTabPosition = mSelectedTab != null ? mSelectedTab.getPosition() : 0;
        removeTabViewAt(position);

        final Tab removedTab = mTabs.remove(position);
        if (removedTab != null) {
            removedTab.reset();
            sTabPool.release(removedTab);
        }

        final int newTabCount = mTabs.size();
        for (int i = position; i < newTabCount; i++) {
            mTabs.get(i).setPosition(i);
        }

        if (selectedTabPosition == position) {
            selectTab(mTabs.isEmpty() ? null : mTabs.get(Math.max(0, position - 1)));
        }
    }

    /**
     * Remove all tabs from the action bar and deselect the current tab.
     */
    public void removeAllTabs() {
        // Remove all the views
        for (int i = mTabs.size() - 1; i >= 0; i--) {
            removeTabViewAt(i);
        }

        for (final Iterator<Tab> i = mTabs.iterator(); i.hasNext();) {
            final Tab tab = i.next();
            i.remove();
            tab.reset();
            sTabPool.release(tab);
        }

        mSelectedTab = null;
    }

    /**
     * Set the behavior mode for the Tabs in this layout. The valid input options are:
     * <ul>
     * <li>{@link #MODE_FIXED}: Fixed tabs display all tabs concurrently and are best used
     * with content that benefits from quick pivots between tabs.</li>
     * <li>{@link #MODE_SCROLLABLE}: Scrollable tabs display a subset of tabs at any given moment,
     * and can contain longer tab labels and a larger number of tabs. They are best used for
     * browsing contexts in touch interfaces when users don’t need to directly compare the tab
     * labels. This mode is commonly used with a {@link ViewPager}.</li>
     * </ul>
     *
     * @param mode one of {@link #MODE_FIXED} or {@link #MODE_SCROLLABLE}.
     *
     * @attr ref android.support.design.R.styleable#TabLayout_tabMode
     */
    public void setTabMode(@Mode int mode) {
        if (mode != mMode) {
            mMode = mode;
            applyModeAndGravity();
        }
    }

    /**
     * Returns the current mode used by this {@link BaseIndicatorTabLayout}.
     *
     * @see #setTabMode(int)
     */
    @Mode
    public int getTabMode() {
        return mMode;
    }

    /**
     * Sets the text colors for the different states (normal, selected) used for the tabs.
     *
     * @see #getTabTextColors()
     */
    public void setTabTextColors(@Nullable ColorStateList textColor) {
        if (mTabTextColors != textColor) {
            mTabTextColors = textColor;

            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, n = mTabs.size(); i < n; i++) {
                TabView tabView = mTabs.get(i).getTabView();
                if (tabView != null) {
                    tabView.setTextColorList(mTabTextColors);
                }
            }
        }
    }

    /**
     * Gets the text colors for the different states (normal, selected) used for the tabs.
     */
    @SuppressWarnings("unused")
    @Nullable
    public ColorStateList getTabTextColors() {
        return mTabTextColors;
    }

    @ColorInt
    public int getSelectedTabTextColor() {
        return mTabTextColors.getColorForState(SELECTED_STATE_SET, UNDEFINED_COLOR);
    }

    /**
     * Sets the text colors for the different states (normal, selected) used for the tabs.
     *
     * @attr ref android.support.design.R.styleable#TabLayout_tabTextColor
     * @attr ref android.support.design.R.styleable#TabLayout_tabSelectedTextColor
     */
    @SuppressWarnings("unused")
    public void setTabTextColors(int normalColor, int selectedColor) {
        setTabTextColors(createColorStateList(normalColor, selectedColor));
    }

    /**
     * Sets the dividers between tabs titles.
     *
     * @attr bitmap - image bitmap, which will be used as a divider.
     * @attr width - width of divider.
     * @atte height - height of divider.
     */
    public void setTabDelimiter(Bitmap bitmap, int width, int height) {
        mTabTitleDelimitersController.updateTitleDelimiters(bitmap, width, height);
    }

    /**
     * The one-stop shop for setting up this {@link BaseIndicatorTabLayout} with a {@link ViewPager}.
     *
     * <p>This method will link the given ViewPager and this TabLayout together so that any
     * changes in one are automatically reflected in the other. This includes adapter changes,
     * scroll state changes, and clicks. The tabs displayed in this layout will be populated
     * from the ViewPager adapter's page titles.</p>
     *
     * <p>After this method is called, you will not need this method again unless you want
     * to change the linked ViewPager.</p>
     *
     * <p>If the given ViewPager is non-null, it needs to already have a
     * {@link PagerAdapter} set.</p>
     *
     * @param viewPager The ViewPager to link, or {@code null} to clear any previous link.
     */
    @SuppressWarnings("unused")
    public void setupWithViewPager(@Nullable final ViewPager viewPager) {
        if (mViewPager != null && mPageChangeListener != null) {
            // If we've already been setup with a ViewPager, remove us from it
            mViewPager.removeOnPageChangeListener(mPageChangeListener);
        }

        if (viewPager != null) {
            final PagerAdapter adapter = viewPager.getAdapter();
            if (adapter == null) {
                throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
            }

            mViewPager = viewPager;

            // Add our custom OnPageChangeListener to the ViewPager
            if (mPageChangeListener == null) {
                mPageChangeListener = new TabLayoutOnPageChangeListener(this);
            }
            mPageChangeListener.reset();
            viewPager.addOnPageChangeListener(mPageChangeListener);

            // Now we'll add a tab selected listener to set ViewPager's current item
            setOnTabSelectedListener(new ViewPagerOnTabSelectedListener(viewPager));

            // Now we'll populate ourselves from the pager adapter
            setPagerAdapter(adapter, true);
        } else {
            // We've been given a null ViewPager so we need to clear out the internal state,
            // listeners and observers
            mViewPager = null;
            setOnTabSelectedListener(null);
            setPagerAdapter(null, true);
        }
    }

    @NonNull
    public TabLayoutOnPageChangeListener getPageChangeListener() {
        if (mPageChangeListener == null) {
            mPageChangeListener = new TabLayoutOnPageChangeListener(this);
        }
        return mPageChangeListener;
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        // Only delay the pressed state if the tabs can scroll
        return getTabScrollRange() > 0;
    }

    protected void onTabViewCreated(@NonNull TextView view) {
    }

    protected void onTabViewUpdated(@NonNull TextView view) {
    }

    public void setTabsEnabled(boolean enabled) {
        for (int i = 0; i < mTabs.size(); i++) {
            mTabs.get(i).mView.setEnabled(enabled);
        }
    }

    private int getTabScrollRange() {
        return Math.max(0, mTabIndicators.getWidth() - getWidth() - getPaddingLeft()
                - getPaddingRight());
    }

    private void setPagerAdapter(@Nullable final PagerAdapter adapter, final boolean addObserver) {
        if (mPagerAdapter != null && mPagerAdapterObserver != null) {
            // If we already have a PagerAdapter, unregister our observer
            mPagerAdapter.unregisterDataSetObserver(mPagerAdapterObserver);
        }

        mPagerAdapter = adapter;

        if (addObserver && adapter != null) {
            // Register our observer on the new adapter
            if (mPagerAdapterObserver == null) {
                mPagerAdapterObserver = new PagerAdapterObserver();
            }
            adapter.registerDataSetObserver(mPagerAdapterObserver);
        }

        // Finally make sure we reflect the new adapter
        populateFromPagerAdapter();
    }

    private void populateFromPagerAdapter() {
        removeAllTabs();

        if (mPagerAdapter != null) {
            final int adapterCount = mPagerAdapter.getCount();
            for (int i = 0; i < adapterCount; i++) {
                addTab(newTab().setText(mPagerAdapter.getPageTitle(i)), false);
            }

            // Make sure we reflect the currently set ViewPager item
            if (mViewPager != null && adapterCount > 0) {
                final int curItem = mViewPager.getCurrentItem();
                if (curItem != getSelectedTabPosition() && curItem < getTabCount()) {
                    selectTab(getTabAt(curItem));
                }
            }
        } else {
            removeAllTabs();
        }
    }

    private TabView getTabView(@NonNull final Tab tab) {
        TabView tabView = mTabViewPool.acquire();
        if (tabView == null) {
            tabView = createTabView(getContext());
            configureTabView(tabView);
            onTabViewCreated(tabView);
        }
        tabView.setTab(tab);
        tabView.setFocusable(true);
        tabView.setMinimumWidth(getTabMinWidth());
        return tabView;
    }

    protected TabView createTabView(@NonNull Context context) {
        return new TabView(context);
    }

    private void configureTabView(@NonNull TabView tabView) {
        tabView.setTabPadding(mTabPaddingStart, mTabPaddingTop, mTabPaddingEnd, mTabPaddingBottom);
        tabView.setTextTypeface(mTypefaceProvider, mTabTextAppearance);
        tabView.setInputFocusTracker(mInputFocusTracker);
        tabView.setTextColorList(mTabTextColors);
        tabView.setBoldTextOnSelection(mTabTextBoldOnSelection);
        tabView.setEllipsizeEnabled(mIsTabEllipsizeEnabled);
        tabView.setMaxWidthProvider(this::getTabMaxWidth);
        tabView.setOnUpdateListener(this::onTabViewUpdated);
    }

    private void configureTab(Tab tab, int position) {
        tab.setPosition(position);
        mTabs.add(position, tab);

        final int count = mTabs.size();
        for (int i = position + 1; i < count; i++) {
            mTabs.get(i).setPosition(i);
        }
    }

    private void addTabView(Tab tab, boolean setSelected) {
        final TabView tabView = tab.mView;
        mTabIndicators.addView(tabView, createLayoutParamsForTabs());
        mTabTitleDelimitersController.tabAdded(mTabIndicators.getChildCount() - 1);
        if (setSelected) {
            tabView.setSelected(true);
        }
    }

    private void addTabView(Tab tab, int position, boolean setSelected) {
        final TabView tabView = tab.mView;
        int positionInLayout = mTabIndicators.getTabPositionInLayout(position);
        mTabIndicators.addView(tabView, positionInLayout, createLayoutParamsForTabs());
        mTabTitleDelimitersController.tabAdded(positionInLayout);
        if (setSelected) {
            tabView.setSelected(true);
        }
    }

    @Override
    public void addView(View child) {
        addViewInternal(child);
    }

    @Override
    public void addView(View child, int index) {
        addViewInternal(child);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        addViewInternal(child);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        addViewInternal(child);
    }

    private void addViewInternal(final View child) {
        if (child instanceof TabItem) {
            addTabFromItemView((TabItem) child);
        } else {
            throw new IllegalArgumentException("Only TabItem instances can be added to TabLayout");
        }
    }

    private LinearLayout.LayoutParams createLayoutParamsForTabs() {
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        updateTabViewLayoutParams(lp);
        return lp;
    }

    private void updateTabViewLayoutParams(LinearLayout.LayoutParams lp) {
        lp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        lp.weight = 0;
    }


    @SuppressLint("SwitchIntDef")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // If we have a MeasureSpec which allows us to decide our height, try and use the default
        // height
        final int idealHeight = BaseDivViewExtensionsKt.dpToPx(
                DEFAULT_HEIGHT,
                getResources().getDisplayMetrics()
        ) + getPaddingTop() + getPaddingBottom();
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.AT_MOST:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        Math.min(idealHeight, MeasureSpec.getSize(heightMeasureSpec)),
                        MeasureSpec.EXACTLY);
                break;
            case MeasureSpec.UNSPECIFIED:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(idealHeight, MeasureSpec.EXACTLY);
                break;

            case MeasureSpec.EXACTLY:
            default:
                // do not modify height;
                break;
        }

        final int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED) {
            // If we don't have an unspecified width spec, use the given size to calculate
            // the max tab width
            mTabMaxWidth = mRequestedTabMaxWidth > 0
                    ? mRequestedTabMaxWidth
                    : specWidth - BaseDivViewExtensionsKt.dpToPx(
                            TAB_MIN_WIDTH_MARGIN,
                            getResources().getDisplayMetrics()
                    );
        }

        // Now super measure itself using the (possibly) modified height spec
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getChildCount() == 1) {
            // If we're in fixed mode then we need to make the tab strip is the same width as us
            // so we don't scroll
            final View child = getChildAt(0);
            boolean remeasure;

            switch (mMode) {
                case MODE_FIXED:
                    // Resize the child so that it doesn't scroll
                    remeasure = child.getMeasuredWidth() != getMeasuredWidth();
                    break;
                case MODE_SCROLLABLE:
                default:
                    // We only need to resize the child if it's smaller than us. This is similar
                    // to fillViewport
                    remeasure = child.getMeasuredWidth() < getMeasuredWidth();
                    break;
            }

            if (remeasure) {
                // Re-measure the child with a widthSpec set to be exactly our measure width
                int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, getPaddingTop()
                        + getPaddingBottom(), child.getLayoutParams().height);
                int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                        getMeasuredWidth(), MeasureSpec.EXACTLY);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }

    @Override
    protected void onSizeChanged(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
        if (oldWidth != 0 && oldWidth != newWidth) {
            setScrollToSelectedTab();
        }
    }

    private void removeTabViewAt(int position) {
        final TabView view = (TabView) mTabIndicators.getChildAt(position);
        int positionInLayout = mTabIndicators.getTabPositionInLayout(position);
        mTabIndicators.removeViewAt(positionInLayout);
        mTabTitleDelimitersController.tabRemoved(positionInLayout);
        if (view != null) {
            view.reset();
            mTabViewPool.release(view);
        }
        requestLayout();
    }

    private void animateToTab(int newPosition) {
        if (newPosition == Tab.INVALID_POSITION) {
            return;
        }

        if (getWindowToken() == null || !ViewsKt.isActuallyLaidOut(this)
                || mTabIndicators.childrenNeedLayout()) {
            // If we don't have a window token, or we haven't been laid out yet just draw the new
            // position now
            setScrollPosition(newPosition, 0f, true);
            return;
        }

        final int startScrollX = getScrollX();
        final int targetScrollX = calculateScrollXForTab(newPosition, 0);

        if (startScrollX != targetScrollX) {
            if (mScrollAnimator == null) {
                mScrollAnimator = ValueAnimator.ofInt();
                mScrollAnimator.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
                mScrollAnimator.setDuration(mAnimationDuration);
                mScrollAnimator.addUpdateListener(animator -> scrollTo((Integer) animator.getAnimatedValue(), 0));
            }

            mScrollAnimator.setIntValues(startScrollX, targetScrollX);
            mScrollAnimator.start();
        }

        // Now animate the indicator
        mTabIndicators.animateSelectedIndicatorToPosition(newPosition, mAnimationDuration);
    }

    private void setSelectedTabView(int position) {
        final int tabCount = mTabIndicators.getChildCount();
        int layoutPosition = mTabIndicators.getTabPositionInLayout(position);
        if (layoutPosition < tabCount && !mTabIndicators.getChildAt(layoutPosition).isSelected()) {
            for (int i = 0; i < tabCount; i++) {
                final View child = mTabIndicators.getChildAt(i);
                child.setSelected(i == layoutPosition);
            }
        }
    }

    void selectTab(Tab tab) {
        selectTab(tab, true);
    }

    public void selectTab(int pos) {
        if (getSelectedTabPosition() != pos) {
            Tab tab = getTabAt(pos);
            if (tab != null) {
                tab.select();
            }
        }
    }

    void selectTab(Tab tab, boolean updateIndicator) {
        if (mSelectedTab == tab) {
            if (mSelectedTab != null) {
                if (mOnTabSelectedListener != null) {
                    mOnTabSelectedListener.onTabReselected(mSelectedTab);
                }
                animateToTab(tab.getPosition());
            }
        } else {
            if (updateIndicator) {
                final int newPosition = tab != null ? tab.getPosition() : Tab.INVALID_POSITION;
                if (newPosition != Tab.INVALID_POSITION) {
                    setSelectedTabView(newPosition);
                }
                if ((mSelectedTab == null || mSelectedTab.getPosition() == Tab.INVALID_POSITION)
                        && newPosition != Tab.INVALID_POSITION) {
                    // If we don't currently have a tab, just draw the indicator
                    setScrollPosition(newPosition, 0f, true);
                } else {
                    animateToTab(newPosition);
                }
            }
            if (mSelectedTab != null && mOnTabSelectedListener != null) {
                mOnTabSelectedListener.onTabUnselected(mSelectedTab);
            }
            mSelectedTab = tab;
            if (mSelectedTab != null && mOnTabSelectedListener != null) {
                mOnTabSelectedListener.onTabSelected(mSelectedTab);
            }
        }
    }

    private void setScrollToSelectedTab() {
        if (mSelectedTab == null) {
            return;
        }

        final int position = mSelectedTab.getPosition();
        if (position == Tab.INVALID_POSITION) {
            return;
        }

        setScrollPosition(position, 0f, true);
    }

    private int calculateScrollXForTab(int position, float positionOffset) {
        if (mMode == MODE_SCROLLABLE) {
            final View selectedChild = mTabIndicators.getTab(position);

            if (selectedChild == null) {
                return 0;
            }

            final int selectedWidth = selectedChild.getWidth();

            if (mTabScrollPaddingEnabled) {
                return selectedChild.getLeft() - mTabScrollPadding;
            } else {
                final View nextChild = position + 1 < mTabIndicators.getChildCount()
                        ? mTabIndicators.getChildAt(position + 1)
                        : null;

                final int nextWidth = nextChild != null ? nextChild.getWidth() : 0;

                return selectedChild.getLeft()
                        + ((int) ((selectedWidth + nextWidth) * positionOffset * 0.5f))
                        + (selectedChild.getWidth() / 2)
                        - (getWidth() / 2);
            }
        }
        return 0;
    }

    private void applyModeAndGravity() {
        int paddingStart = 0;
        int paddingEnd = 0;
        if (mMode == MODE_SCROLLABLE) {
            // If we're scrollable, or fixed at start, inset using padding
            paddingStart = Math.max(0, mContentInsetStart - mTabPaddingStart);
            paddingEnd = Math.max(0, mContentInsetEnd - mTabPaddingEnd);
        }
        ViewCompat.setPaddingRelative(mTabIndicators, paddingStart, 0, paddingEnd, 0);

        switch (mMode) {
            case MODE_FIXED:
                mTabIndicators.setGravity(Gravity.CENTER_HORIZONTAL);
                break;
            case MODE_SCROLLABLE:
            default:
                mTabIndicators.setGravity(GravityCompat.START);
                break;
        }

        updateTabViews(true);
    }

    private void updateTabViews(final boolean requestLayout) {
        for (int i = 0; i < mTabIndicators.getChildCount(); i++) {
            View child = mTabIndicators.getChildAt(i);
            if (!(child instanceof TabView)) continue;
            child.setMinimumWidth(getTabMinWidth());
            updateTabViewLayoutParams((LinearLayout.LayoutParams) child.getLayoutParams());
            if (requestLayout) {
                child.requestLayout();
            }
        }
    }

    @NonNull
    @MainThread
    public void bindTypefaceProvider(@NonNull DivTypefaceProvider typefaceProvider) {
        mTypefaceProvider = typefaceProvider;
    }

    public void setFocusTracker(InputFocusTracker focusTracker) {
        mInputFocusTracker = focusTracker;
    }

    /**
     * A tab in this layout. Instances can be created via {@link #newTab()}.
     */
    public static final class Tab {

        /**
         * An invalid position for a tab.
         *
         * @see #getPosition()
         */
        static final int INVALID_POSITION = -1;

        @Nullable
        private CharSequence mText;
        private int mPosition = INVALID_POSITION;

        private BaseIndicatorTabLayout mParent;
        private TabView mView;

        private Tab() {
        }

        /**
         * Return the current position of this tab in the action bar.
         *
         * @return Current position, or {@link #INVALID_POSITION} if this tab is not currently in
         * the action bar.
         */
        public int getPosition() {
            return mPosition;
        }

        void setPosition(int position) {
            mPosition = position;
        }

        /**
         * Return the text of this tab.
         *
         * @return The tab's text
         */
        @Nullable
        public CharSequence getText() {
            return mText;
        }

        /**
         * Set the text displayed on this tab. Text may be truncated if there is not room to display
         * the entire string.
         *
         * @param text The text to display
         * @return The current instance for call chaining
         */
        @NonNull
        public Tab setText(@Nullable CharSequence text) {
            mText = text;
            updateView();
            return this;
        }

        /**
         * Set the text displayed on this tab. Text may be truncated if there is not room to display
         * the entire string.
         *
         * @param resId A resource ID referring to the text that should be displayed
         * @return The current instance for call chaining
         */
        @NonNull
        public Tab setText(@StringRes int resId) {
            if (mParent == null) {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            }
            return setText(mParent.getResources().getText(resId));
        }

        /**
         * Select this tab. Only valid if the tab has been added to the action bar.
         */
        public void select() {
            if (mParent == null) {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            }
            mParent.selectTab(this);
        }

        @Nullable
        public TabView getTabView() {
            return mView;
        }

        /**
         * Returns true if this tab is currently selected.
         */
        public boolean isSelected() {
            if (mParent == null) {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            }
            return mParent.getSelectedTabPosition() == mPosition;
        }

        private void updateView() {
            if (mView != null) {
                mView.update();
            }
        }

        private void reset() {
            mParent = null;
            mView = null;
            mText = null;
            mPosition = INVALID_POSITION;
        }
    }

    public static final float UNDEFINED_RADIUS = -1f;

    public enum AnimationType {
        SLIDE,
        FADE,
        NONE
    }

    static class OvalIndicators extends LinearLayout {
        protected int mIndicatorHeight;

        private static final int UNDEFINED_COLOR = -1;

        protected int mSelectedColor = UNDEFINED_COLOR;
        protected int mUnselectedColor = UNDEFINED_COLOR;

        protected int mSelectedPosition = -1;

        protected float mOffset;

        protected int mItemSpacing = 0;

        protected int[] mIndicatorsLeft;
        protected int[] mIndicatorsRight;

        protected float[] mCornerRadii;

        protected int mSelectedIndicatorLeft = -1;
        protected int mSelectedIndicatorRight = -1;

        private int mSize;

        protected ValueAnimator mSelectedIndicatorAnimator;

        private final Paint mIndicatorPaint;
        private final Path mClipPath;
        private final RectF mIndicatorRect;

        private final int mPaddingTop;
        private final int mPaddingBottom;

        private boolean mHasDelimiters;

        private float mOpacity = 1f;
        private int mFutureSelectedPosition = -1;

        private AnimationType mAnimationType = AnimationType.SLIDE;

        private OvalIndicators(Context context, int paddingTop, int paddingBottom) {
            super(context);

            setId(R.id.tab_sliding_oval_indicator);

            setWillNotDraw(false);

            mSize = getChildCount();
            if (mHasDelimiters) mSize = (mSize + 1) / 2;
            initIndicatorArrays(mSize);

            mIndicatorPaint = new Paint();
            mIndicatorPaint.setAntiAlias(true);

            mIndicatorRect = new RectF();
            mPaddingTop = paddingTop;
            mPaddingBottom = paddingBottom;

            mClipPath = new Path();
            mCornerRadii = new float[8];
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            updateIndicatorsPosition();
            if (mSelectedIndicatorAnimator != null && mSelectedIndicatorAnimator.isRunning()) {
                // If we're currently running an animation, lets cancel it and start a
                // new animation with the remaining duration
                mSelectedIndicatorAnimator.cancel();
                final float fraction = mSelectedIndicatorAnimator.getAnimatedFraction();
                final int duration = Math.round((1f - fraction) * mSelectedIndicatorAnimator.getDuration());
                animateSelectedIndicatorToPosition(mFutureSelectedPosition, duration);
            }
        }

        @Override
        public void addView(View child, int index, ViewGroup.LayoutParams params) {
            int childCount = getChildCount();
            if (index < 0) {
                //See ViewGroup.addViewInner()
                index = childCount;
            }
            if (index == 0) {
                //If the child is added to the beginning
                if (childCount != 0) {
                    //If view group has some first child, adding to him left margin
                    View prevFirstChild = getChildAt(0);
                    ViewGroup.LayoutParams prevFirstChildParams = prevFirstChild.getLayoutParams();
                    updateViewLayout(prevFirstChild, setLeftMargin(prevFirstChildParams, mItemSpacing));
                }
                //Add new first child
                super.addView(child, index, setLeftMargin(params, 0));
            } else {
                //Add new child with left margin
                super.addView(child, index, setLeftMargin(params, mItemSpacing));
            }
        }

        private MarginLayoutParams setLeftMargin(ViewGroup.LayoutParams params, int leftMargin) {
            MarginLayoutParams lp = (MarginLayoutParams) params;
            lp.leftMargin = leftMargin;
            return lp;
        }

        @Override
        public void draw(Canvas canvas) {
            float height = getHeight();
            if (mUnselectedColor != UNDEFINED_COLOR) {
                // Draw unselected indicators
                for (int i = 0, z = mSize; i < z; i++) {
                    drawRoundRect(canvas,
                                  mIndicatorsLeft[i],
                                  mIndicatorsRight[i],
                                  height,
                                  mUnselectedColor, 1f);
                }
            }
            if (mSelectedColor != UNDEFINED_COLOR) {
                int selected = getTabPositionInLayout(mSelectedPosition);
                int future = getTabPositionInLayout(mFutureSelectedPosition);
                // Draw selected indicator
                switch (mAnimationType) {
                    case FADE:
                        drawRoundRect(canvas,
                                      mIndicatorsLeft[selected],
                                      mIndicatorsRight[selected],
                                      height,
                                      mSelectedColor, mOpacity);
                        if (mFutureSelectedPosition != -1) {
                            drawRoundRect(canvas,
                                          mIndicatorsLeft[future],
                                          mIndicatorsRight[future],
                                          height,
                                          mSelectedColor, 1f - mOpacity);
                        }
                        break;
                    case SLIDE:
                        drawRoundRect(canvas,
                                      mSelectedIndicatorLeft,
                                      mSelectedIndicatorRight,
                                      height,
                                      mSelectedColor, 1f);
                        break;
                    default:
                        drawRoundRect(canvas,
                                      mIndicatorsLeft[selected],
                                      mIndicatorsRight[selected],
                                      height,
                                      mSelectedColor, 1f);
                        break;
                }
            }
            super.draw(canvas);
        }

        private void drawRoundRect(Canvas canvas, int left, int right, float height, int color, float opacity) {
            if (left >= 0 && right > left) {
                mIndicatorRect.set(left, mPaddingTop, right, height - mPaddingBottom);

                float indicatorWidth = mIndicatorRect.width();
                float indicatorHeight = mIndicatorRect.height();
                float[] radii = new float[8];
                for (int i = 0; i < 8; i++) {
                    radii[i] = clampCornerRadius(mCornerRadii[i], indicatorWidth, indicatorHeight);
                }
                mClipPath.reset();
                mClipPath.addRoundRect(mIndicatorRect, radii, Path.Direction.CW);
                mClipPath.close();

                mIndicatorPaint.setColor(color);
                int alpha = Math.round(mIndicatorPaint.getAlpha() * opacity);
                mIndicatorPaint.setAlpha(alpha);
                canvas.drawPath(mClipPath, mIndicatorPaint);
            }
        }

        boolean childrenNeedLayout() {
            for (int i = 0, z = getChildCount(); i < z; i++) {
                final View child = getChildAt(i);
                if (child.getWidth() <= 0) {
                    return true;
                }
            }
            return false;
        }

        void setSelectedIndicatorColor(@ColorInt int color) {
            if (mSelectedColor != color) {
                if (isTransparentColor(color)) {
                    mSelectedColor = UNDEFINED_COLOR;
                } else {
                    mSelectedColor = color;
                }
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        void setAnimationType(AnimationType animationType) {
            if (mAnimationType != animationType) {
                mAnimationType = animationType;
                if (mSelectedIndicatorAnimator != null && mSelectedIndicatorAnimator.isRunning()) {
                    mSelectedIndicatorAnimator.cancel();
                }
            }
        }

        void setIndicatorColor(@ColorInt int color) {
            if (mUnselectedColor != color) {
                if (isTransparentColor(color)) {
                    mUnselectedColor = UNDEFINED_COLOR;
                } else {
                    mUnselectedColor = color;
                }
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        void setIndicatorHeight(int height) {
            if (mIndicatorHeight != height) {
                mIndicatorHeight = height;
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        void setIndicatorCornersRadii(@NonNull float[] radii) {
            if (!Arrays.equals(mCornerRadii, radii)) {
                mCornerRadii = radii;
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        void setItemSpacing(int itemSpacing) {
            if (itemSpacing != mItemSpacing) {
                mItemSpacing = itemSpacing;
                for (int i = 1, z = getChildCount(); i < z; i++) {
                    final View child = getChildAt(i);
                    updateViewLayout(child, setLeftMargin(child.getLayoutParams(), mItemSpacing));
                }
            }
        }

        void setContainsDelimiters(boolean containsDelimiters) {
            if (mHasDelimiters != containsDelimiters) {
                mHasDelimiters = containsDelimiters;
                updateOpacity();
                updateIndicatorsPosition();
            }
        }

        boolean hasDelimiters() {
            return mHasDelimiters;
        }

        View getTab(int position) {
            return getChildAt(getTabPositionInLayout(position));
        }

        void animateSelectedIndicatorToPosition(int position, long duration) {
            if (mSelectedIndicatorAnimator != null && mSelectedIndicatorAnimator.isRunning()) {
                mSelectedIndicatorAnimator.cancel();
                float fraction = mSelectedIndicatorAnimator.getAnimatedFraction();
                duration = Math.round((1f - fraction) * mSelectedIndicatorAnimator.getDuration());
            }
            final View targetView = getTab(position);
            if (targetView == null) {
                // If we don't have a view, just update the position now and return
                updateIndicatorsPosition();
                return;
            }
            switch (mAnimationType) {
                case SLIDE:
                    startSelectedIndicatorSlideAnimation(position,
                                                         duration,
                                                         mSelectedIndicatorLeft,
                                                         mSelectedIndicatorRight,
                                                         targetView.getLeft(),
                                                         targetView.getRight());
                    break;
                case FADE:
                    startSelectedIndicatorFadeAnimation(position, duration);
                    break;
                default:
                    setSelectedIndicatorPositionFromTabPosition(position, 0f);
                    break;
            }
        }

        void setSelectedIndicatorPositionFromTabPosition(int position, float positionOffset) {
            if (mSelectedIndicatorAnimator != null && mSelectedIndicatorAnimator.isRunning()) {
                mSelectedIndicatorAnimator.cancel();
            }

            mSelectedPosition = position;
            mOffset = positionOffset;
            updateIndicatorsPosition();
            updateOpacity();
        }

        protected void updateIndicatorsPosition() {
            View title;
            int left, right;
            int selectedLeft, selectedRight;
            int childCount = getChildCount();
            if (childCount != mSize) {
                initIndicatorArrays(childCount);
            }

            int selectedPosition = getTabPositionInLayout(mSelectedPosition);
            for (int titleIndex = 0; titleIndex < childCount; titleIndex++) {
                title = getChildAt(titleIndex);
                if (!(title instanceof TabView)) continue;
                left = -1;
                right = -1;
                selectedLeft = -1;
                selectedRight = -1;
                if (title.getWidth() > 0) {
                    left = title.getLeft();
                    right = title.getRight();
                    selectedLeft = left;
                    selectedRight = right;
                    if (mAnimationType == AnimationType.SLIDE) {
                        if (titleIndex == selectedPosition && mOffset > 0f && titleIndex < childCount - 1) {
                            // Draw the selection partway between the tabs
                            int nextChild = mHasDelimiters ? titleIndex + 2 : titleIndex + 1;
                            View nextTitle = getChildAt(nextChild);
                            selectedLeft = (int) (mOffset * nextTitle.getLeft() + (1.0f - mOffset) * left);
                            selectedRight = (int) (mOffset * nextTitle.getRight() + (1.0f - mOffset) * right);
                        }
                    }
                }
                setUnselectedIndicatorPosition(titleIndex, left, right);
                if (titleIndex == selectedPosition) {
                    setSelectedIndicatorPosition(selectedLeft, selectedRight);
                }
            }
        }

        private int getTabPositionInLayout(int position) {
            return mHasDelimiters && position != -1 ? position * 2 : position;
        }

        protected void updateOpacity() {
            float newOpacity = 1.0f - mOffset;
            if (newOpacity != mOpacity) {
                mOpacity = newOpacity;
                int nextPosition = mSelectedPosition + 1;
                mFutureSelectedPosition = nextPosition < mSize ? nextPosition : -1;
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        protected void setUnselectedIndicatorPosition(int index, int left, int right) {
            int currentLeft = mIndicatorsLeft[index];
            int currentRight = mIndicatorsRight[index];
            if (left != currentLeft || right != currentRight) {
                // If the indicator's left/right has changed, invalidate
                mIndicatorsLeft[index] = left;
                mIndicatorsRight[index] = right;
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        protected void setSelectedIndicatorPosition(int left, int right) {
            if (left != mSelectedIndicatorLeft || right != mSelectedIndicatorRight) {
                // If the indicator's left/right has changed, invalidate
                mSelectedIndicatorLeft = left;
                mSelectedIndicatorRight = right;
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        protected void startSelectedIndicatorSlideAnimation(int position, long duration,
                                                            int startLeft, int startRight,
                                                            int targetLeft, int targetRight) {
            if (startLeft != targetLeft || startRight != targetRight) {
                ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
                animator.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
                animator.setDuration(duration);
                animator.addUpdateListener(animator1 -> {
                    final float fraction = animator1.getAnimatedFraction();
                    setSelectedIndicatorPosition(
                            lerp(startLeft, targetLeft, fraction),
                            lerp(startRight, targetRight, fraction)
                    );
                    ViewCompat.postInvalidateOnAnimation(this);
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    private boolean mHasCancel = false;

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mHasCancel = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!mHasCancel) {
                            mSelectedPosition = mFutureSelectedPosition;
                            mOffset = 0f;
                        }
                    }
                });
                mFutureSelectedPosition = position;
                mSelectedIndicatorAnimator = animator;
                mSelectedIndicatorAnimator.start();
            }
        }

        protected void startSelectedIndicatorFadeAnimation(int position, long duration) {
            if (position != mSelectedPosition) {
                ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
                animator.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
                animator.setDuration(duration);
                animator.addUpdateListener(animator1 -> {
                    final float fraction = animator1.getAnimatedFraction();
                    mOpacity = 1f - fraction;
                    ViewCompat.postInvalidateOnAnimation(this);
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    private boolean mHasCancel = false;

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mHasCancel = true;
                        mOpacity = 1f;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!mHasCancel) {
                            mSelectedPosition = mFutureSelectedPosition;
                            mOffset = 0;
                        }
                    }
                });
                mFutureSelectedPosition = position;
                mSelectedIndicatorAnimator = animator;
                mSelectedIndicatorAnimator.start();
            }
        }

        private void initIndicatorArrays(int size) {
            mSize = size;
            mIndicatorsLeft = new int[mSize];
            mIndicatorsRight = new int[mSize];
            for (int i = 0; i < mSize; i++) {
                mIndicatorsLeft[i] = -1;
                mIndicatorsRight[i] = -1;
            }
        }

        private static int lerp(int startValue, int endValue, float fraction) {
            return startValue + Math.round(fraction * (float) (endValue - startValue));
        }

        private static float clampCornerRadius(float cornerRadius, float width, float height) {
            if (height <= 0 || width <= 0) {
                return 0.0f;
            }
            float maxCornerRadius = Math.min(height, width) / 2;
            if (cornerRadius == UNDEFINED_RADIUS) {
                return maxCornerRadius;
            }
            if (cornerRadius > maxCornerRadius) {
                Log.e("BaseIndicatorTabLayout", "Corner radius is too big");
            }
            return Math.min(cornerRadius, maxCornerRadius);
        }

        private static boolean isTransparentColor(@ColorInt int color) {
            //Channel 8 bit, alpha channel [24, 31] bits
            return (color >> 24) == 0;
        }
    }

    private static ColorStateList createColorStateList(int defaultColor, int selectedColor) {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];
        int i = 0;

        states[i] = SELECTED_STATE_SET;
        colors[i] = selectedColor;
        i++;

        // Default enabled state
        states[i] = EMPTY_STATE_SET;
        colors[i] = defaultColor;

        return new ColorStateList(states, colors);
    }

    private int getTabMinWidth() {
        if (mRequestedTabMinWidth != INVALID_WIDTH) {
            // If we have been given a min width, use it
            return mRequestedTabMinWidth;
        }
        // Else, we'll use the default value
        return mMode == MODE_SCROLLABLE ? mScrollableTabMinWidth : 0;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        // We don't care about the layout params of any views added to us, since we don't actually
        // add them. The only view we add is the SlidingTabStrip, which is done manually.
        // We return the default layout params so that we don't blow up if we're given a TabItem
        // without android:layout_* values.
        return generateDefaultLayoutParams();
    }

    private int getTabMaxWidth() {
        return mTabMaxWidth;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mNestedScrollCompanion.dispatchOnScrollChanged();
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        mNestedScrollCompanion.dispatchOnOverScrolled(clampedX);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = super.dispatchTouchEvent(ev);
        mNestedScrollCompanion.dispatchTouchEventAfterSuperCall(ev);
        return result;
    }

    /**
     * A {@link ViewPager.OnPageChangeListener} class which contains the
     * necessary calls back to the provided {@link BaseIndicatorTabLayout} so that the tab position is
     * kept in sync.
     *
     * <p>This class stores the provided TabLayout weakly, meaning that you can use
     * {@link ViewPager#addOnPageChangeListener(ViewPager.OnPageChangeListener)
     * addOnPageChangeListener(OnPageChangeListener)} without removing the listener and
     * not cause a leak.
     */
    public static class TabLayoutOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private final WeakReference<BaseIndicatorTabLayout> mTabLayoutRef;
        private int mPreviousScrollState;
        private int mScrollState;

        TabLayoutOnPageChangeListener(BaseIndicatorTabLayout tabLayout) {
            mTabLayoutRef = new WeakReference<>(tabLayout);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mPreviousScrollState = mScrollState;
            mScrollState = state;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            final BaseIndicatorTabLayout tabLayout = mTabLayoutRef.get();
            if (tabLayout != null) {
                // Only update scroll if we're not settling, or we are settling after
                // being dragged
                final boolean updateScroll = mScrollState != SCROLL_STATE_SETTLING || mPreviousScrollState == SCROLL_STATE_DRAGGING;
                if (updateScroll) {
                    tabLayout.setScrollPosition(position, positionOffset, true, true);
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            final BaseIndicatorTabLayout tabLayout = mTabLayoutRef.get();
            if (tabLayout != null && tabLayout.getSelectedTabPosition() != position) {
                // Select the tab, only updating the indicator if we're not being dragged/settled
                // (since onPageScrolled will handle that).
                final boolean updateIndicator = mScrollState == SCROLL_STATE_IDLE
                        || (mScrollState == SCROLL_STATE_SETTLING
                        && mPreviousScrollState == SCROLL_STATE_IDLE);
                tabLayout.selectTab(tabLayout.getTabAt(position), updateIndicator);
            }
        }

        public void reset() {
            mPreviousScrollState = mScrollState = SCROLL_STATE_IDLE;
        }
    }

    /**
     * A {@link OnTabSelectedListener} class which contains the necessary calls back
     * to the provided {@link ViewPager} so that the tab position is kept in sync.
     */
    public static class ViewPagerOnTabSelectedListener implements OnTabSelectedListener {
        private final ViewPager mViewPager;

        ViewPagerOnTabSelectedListener(ViewPager viewPager) {
            mViewPager = viewPager;
        }

        @Override
        public void onTabSelected(Tab tab) {
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(Tab tab) {
            // No-op
        }

        @Override
        public void onTabReselected(Tab tab) {
            // No-op
        }
    }

    private class PagerAdapterObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            populateFromPagerAdapter();
        }

        @Override
        public void onInvalidated() {
            populateFromPagerAdapter();
        }
    }
}
