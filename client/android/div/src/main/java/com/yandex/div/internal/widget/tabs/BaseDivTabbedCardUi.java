package com.yandex.div.internal.widget.tabs;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.CallSuper;
import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.yandex.div.core.expression.ExpressionSubscriber;
import com.yandex.div.core.font.DivTypefaceProvider;
import com.yandex.div.core.util.Assert;
import com.yandex.div.core.util.Log;
import com.yandex.div.internal.util.Views;
import com.yandex.div.internal.viewpool.ViewPool;
import com.yandex.div.internal.widget.tabs.ViewPagerFixedSizeLayout.HeightCalculator;
import com.yandex.div.json.expressions.ExpressionResolver;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A UI component that renders a tabbed view. For input it takes {@link BaseDivTabbedCardUi.Input}
 * with list {@link TAB_DATA}. Each {@link TAB_DATA} gets converted into a stateful object
 * {@link TAB_VIEW} with the general management from {@link PagerAdapter}.
 *
 * @param <TAB_DATA> data source for a tab; it must provide tab's title and whatever other data
 * @param <TAB_VIEW> stateful object that wraps actual view; can be just a plain View
 */
public abstract class BaseDivTabbedCardUi<TAB_DATA extends BaseDivTabbedCardUi.Input.TabBase<ACTION>, TAB_VIEW, ACTION> {

    private static final String TAG = "BaseDivTabbedCardUi";
    private static final int NO_POS = -1;

    @NonNull
    private final ViewPool mViewPool;
    @NonNull
    private final View mView;
    @NonNull
    private final AbstractTabBar<ACTION> mAbstractTabBar;
    @NonNull
    private final BaseTabTitleBarHost mTabTitleBarHost;
    @NonNull
    protected final ScrollableViewPager mPager;
    @NonNull
    private HeightCalculatorFactory mHeightCalculatorFactory;
    @Nullable
    private final ViewPagerFixedSizeLayout mViewPagerFixedSizeLayout;
    @Nullable
    private HeightCalculator mViewPagerHeightCalculator;
    @NonNull
    private final Map<ViewGroup, Binding> mBindings = new ArrayMap<>();
    /**
     * mBindingByPosition is populated both by PagerAdapter.instantiateItem() and
     * measureTabHeight() so that tab bindings created by one can be reused by other.
     * Unlike mBindingByPosition mBindings contains only items created by PagerAdapter.
     */
    @NonNull
    private final Map<Integer, Binding> mBindingByPosition = new ArrayMap<>();
    @NonNull
    private final String mTabHeaderTag;
    @NonNull
    private final String mTabItemTag;
    @NonNull
    private final ActiveTabClickListener<ACTION> mActiveTabClickListener;

    private final PagerAdapter mPagerAdapter = new PagerAdapter() {
        private static final String KEY_CHILD_STATES = "div_tabs_child_states";
        @Nullable
        private SparseArray<Parcelable> mChildStates;

        @Override
        public int getCount() {
            return mCurrentData == null ? 0 : mCurrentData.getTabs().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.d(TAG, "instantiateItem pos " + position);
            ViewGroup child;
            Binding binding = mBindingByPosition.get(position);
            if (binding != null) {
                // Binding was already created on measuring step.
                child = binding.mContainer;
                Assert.assertNull(binding.mContainer.getParent());
            } else {
                child = mViewPool.obtain(mTabItemTag);
                TAB_DATA tabData = mCurrentData.getTabs().get(position);
                binding = new Binding(child, tabData, position);
                mBindingByPosition.put(position, binding);
            }
            container.addView(child);
            mBindings.put(child, binding);
            if (position == mPager.getCurrentItem()) {
                binding.bind();
            }
            if (mChildStates != null) {
                child.restoreHierarchyState(mChildStates);
            }

            return child;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ViewGroup view = (ViewGroup) object;
            mBindings.remove(view).unbind();
            mBindingByPosition.remove(position);

            Log.d(TAG, "destroyItem pos " + position);
            container.removeView(view);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public Parcelable saveState() {
            SparseArray<Parcelable> childStates = new SparseArray<>(mBindings.size());
            for (ViewGroup child : mBindings.keySet()) {
                child.saveHierarchyState(childStates);
            }
            Bundle bundle = new Bundle();
            bundle.putSparseParcelableArray(KEY_CHILD_STATES, childStates);
            return bundle;
        }

        @Override
        public void restoreState(@Nullable Parcelable state, @Nullable ClassLoader loader) {
            if (!(state instanceof Bundle)) {
                mChildStates = null;
                return;
            }
            Bundle stateBundle = ((Bundle) state);
            stateBundle.setClassLoader(getClass().getClassLoader());
            mChildStates = stateBundle.getSparseParcelableArray(KEY_CHILD_STATES);
        }
    };

    /**
     * Details on actual listener calls.
     * External switch:
     * onPageScrollStateChanged 2 SCROLL_STATE_SETTLING
     * onPageSelected 3
     * onPageScrolled 0.9984374
     * onPageScrolled 0.0
     * onPageScrollStateChanged 0 SCROLL_STATE_IDLE
     * <p/>
     * Drag (not enough)
     * onPageScrolled 0.021874905
     * onPageScrollStateChanged 2 SCROLL_STATE_SETTLING
     * onPageSelected 6
     * onPageScrolled 0.1640625
     * onPageSelected 5
     * onPageScrolled 0.015625
     * onPageScrolled 0.0
     * onPageScrollStateChanged 0 SCROLL_STATE_IDLE
     * <p/>
     * Drag (enough)
     * onPageScrolled 0.015625
     * onPageScrollStateChanged 2 SCROLL_STATE_SETTLING
     * onPageSelected 6
     * onPageScrolled 0.0062499046
     * onPageScrolled 0.0
     * onPageScrollStateChanged 0 SCROLL_STATE_IDLE
     * <p/>
     * Init
     * onPageScrolled 0.0
     */

    private boolean mTabTitleBarIgnoreScrollEvents = false;
    private Input<TAB_DATA> mCurrentData = null;

    private boolean mInSetData = false;

    public BaseDivTabbedCardUi(@NonNull ViewPool viewPool,
                        @NonNull View view,
                        @NonNull TabbedCardConfig tabbedCardConfig,
                        @NonNull HeightCalculatorFactory heightCalculatorFactory,
                        @NonNull TabTextStyleProvider textStyleProvider,
                        @Nullable ViewPager.OnPageChangeListener extendedListener,
                        @NonNull ActiveTabClickListener<ACTION> activeTabClickListener) {
        mViewPool = viewPool;
        mView = view;
        mHeightCalculatorFactory = heightCalculatorFactory;
        mActiveTabClickListener = activeTabClickListener;

        mTabTitleBarHost = new BaseTabTitleBarHost();

        mTabHeaderTag = tabbedCardConfig.getTabHeaderTag();
        mTabItemTag = tabbedCardConfig.getTabItemTag();

        mAbstractTabBar = Views.findViewAndCast(mView, tabbedCardConfig.getCardTitleContainerScrollerId());
        mAbstractTabBar.setHost(mTabTitleBarHost);
        mAbstractTabBar.setTypefaceProvider(textStyleProvider.getTypefaceProvider());
        mAbstractTabBar.setViewPool(viewPool, mTabHeaderTag);

        mPager = Views.findViewAndCast(mView, tabbedCardConfig.getCardPagerContainerId());
        mPager.setAdapter(null);
        mPager.clearOnPageChangeListeners();
        mPager.addOnPageChangeListener(new PagerChangeListener());
        ViewPager.OnPageChangeListener customPageChangeListener = mAbstractTabBar.getCustomPageChangeListener();
        if (customPageChangeListener != null) {
            mPager.addOnPageChangeListener(customPageChangeListener);
        }
        if (extendedListener != null) {
            mPager.addOnPageChangeListener(extendedListener);
        }
        mPager.setScrollEnabled(tabbedCardConfig.isViewPagerScrollable());
        mPager.setEdgeScrollEnabled(tabbedCardConfig.isViewPagerEdgeScrollable());
        mPager.setPageTransformer(false, new DataBindingTransformer());

        final int pageContainerHelperId = tabbedCardConfig.getCardPagerContainerHelperId();
        mViewPagerFixedSizeLayout = Views.findViewAndCast(mView, pageContainerHelperId);
        initializeViewPagerFixedSizeLayout();
    }

    private void initializeViewPagerFixedSizeLayout() {
        if (mViewPagerFixedSizeLayout == null) {
            return;
        }

        final ViewGroup view = mViewPool.obtain(mTabItemTag);
        mViewPagerHeightCalculator = mHeightCalculatorFactory.getCardHeightCalculator(view, this::measureTabHeight, this::getTabCount);
        mViewPagerFixedSizeLayout.setHeightCalculator(mViewPagerHeightCalculator);
    }

    public void setData(@Nullable Input<TAB_DATA> data, @NonNull ExpressionResolver resolver, @NonNull ExpressionSubscriber subscriber) {
        final int newPos = findCorrespondingTab(mPager.getCurrentItem(), data);

        mBindingByPosition.clear();
        mCurrentData = data; // actually this changes mPagerAdapter contents
        if (mPager.getAdapter() != null) {
            mInSetData = true;
            try {
                mPagerAdapter.notifyDataSetChanged(); // need to be called before abstractTabBar.setData, or we get crash
            } finally {
                mInSetData = false;
            }
        }

        List<? extends TAB_DATA> tabs;
        if (data == null) {
            tabs = Collections.emptyList();
        } else {
            tabs = data.getTabs();
        }

        mAbstractTabBar.setData(tabs, newPos, resolver, subscriber); // this also changes ViewPager position, so should go before setAdapter

        // if it is the first launch - set adapter and use nested restore state functionality
        // pager.setAdapter has side effect of restoring correct ViewPager position, so don't call setCurrentItem or tabBar.setData after it
        if (mPager.getAdapter() == null) {
            mPager.setAdapter(mPagerAdapter);
        } else {
            if (!tabs.isEmpty() && newPos != NO_POS) {
                mPager.setCurrentItem(newPos);
                // Normally pager notifies title bar about every tab change, but not this time, since
                // we might not actually change current item value.
                mAbstractTabBar.manuallyScroll(newPos);
            }
        }

        requestViewPagerLayout();
    }

    public void setDisabledScrollPages(@NonNull Set<Integer> disabledPages) {
        mPager.setDisabledScrollPages(disabledPages);
    }

    void setTabColors(@ColorInt int activeTextColor,
                      @ColorInt int activeBackgroundColor,
                      @ColorInt int inactiveTextColor,
                      @ColorInt int inactiveBackgroundColor) {
        mAbstractTabBar.setTabColors(activeTextColor, activeBackgroundColor, inactiveTextColor, inactiveBackgroundColor);
    }

    @NonNull
    protected abstract TAB_VIEW bindTabData(@NonNull ViewGroup tabView, @NonNull TAB_DATA tab, int tabNumber);

    protected abstract void unbindTabData(@NonNull TAB_VIEW tabView);

    protected abstract void fillMeasuringTab(@NonNull ViewGroup view, @NonNull TAB_DATA tab, final int tabNumber);

    protected void recycleMeasuringTabChildren(@NonNull ViewGroup view) {
    }

    public void requestViewPagerLayout() {
        Log.d(TAG, "requestViewPagerLayout");
        if (mViewPagerHeightCalculator != null) {
            mViewPagerHeightCalculator.dropMeasureCache();
        }
        if (mViewPagerFixedSizeLayout != null) {
            mViewPagerFixedSizeLayout.requestLayout();
        }
    }

    private int findCorrespondingTab(int oldTabPos, Input<TAB_DATA> newData) {
        // Simply keep integer value. Theoretically we can match data of actual tabs.
        if (newData == null) {
            return NO_POS;
        }
        int size = newData.getTabs().size();

        // When size==0 value of size-1 is effectively NO_POS.
        return Math.min(oldTabPos, size - 1);
    }

    private int getTabCount() {
        if (mCurrentData == null) {
            return 0;
        }

        return mCurrentData.getTabs().size();
    }

    private int measureTabHeight(@NonNull ViewGroup tabView, int width, int tabIndex) {
        if (mCurrentData == null) {
            return -1;
        }

        final int collapsiblePaddingBottom;
        if (mViewPagerFixedSizeLayout != null) {
            collapsiblePaddingBottom = mViewPagerFixedSizeLayout.getCollapsiblePaddingBottom();
        } else {
            collapsiblePaddingBottom = 0;
        }

        List<? extends TAB_DATA> tabs = mCurrentData.getTabs();
        Assert.assertTrue("Tab index is out ouf bounds!", tabIndex >= 0 && tabIndex < tabs.size());

        TAB_DATA tab = tabs.get(tabIndex);
        Integer tabHeight = tab.getTabHeight();
        if (tabHeight != null) {
            return tabHeight + collapsiblePaddingBottom;
        }

        Binding binding = mBindingByPosition.get(tabIndex);
        if (binding == null) {
            tabView = mViewPool.obtain(mTabItemTag);
            binding = new Binding(tabView, tab, tabIndex);
            mBindingByPosition.put(tabIndex, binding);
        } else {
            tabView = binding.mContainer;
        }
        binding.bind();
        tabView.forceLayout();
        tabView.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int result = tabView.getMeasuredHeight() + collapsiblePaddingBottom;
        return result;
    }

    @CallSuper
    public void saveInstanceState(@NonNull SparseArray<Parcelable> container) {
        if (mViewPagerHeightCalculator != null) {
            mViewPagerHeightCalculator.saveInstanceState(container);
        }
    }

    @CallSuper
    public void restoreInstanceState(@NonNull SparseArray<Parcelable> container) {
        if (mViewPagerHeightCalculator != null) {
            mViewPagerHeightCalculator.restoreInstanceState(container);
        }
    }

    /**
     * A generic input, that is described as a list of tabs. The {@link BaseDivTabbedCardUi}
     * only requires tab to provide tab's title, all other specifics are fixed elsewhere.
     */
    public interface Input<TAB extends Input.TabBase> {
        @NonNull
        List<? extends TAB> getTabs();

        /**
         * Tabtitle data class
         * @param <ACTION> - actionable for tab title
         */
        interface TabBase<ACTION> {
            String getTitle();
            @Nullable
            ACTION getActionable();
            //@Nullable
            //TextStyle getTextStyle(); take dep from legacy TextStyle.
            @Nullable
            Integer getTabHeight();
        }

        /**
         * A possible implementation of the tab concept: a plain list of items.
         */
        interface SimpleTab<ITM, ACTION> extends TabBase<ACTION> {
            @NonNull
            ITM getItem();
        }
    }

    /**
     * Config for tabs - id of views, scrollable flags
     */
    public static class TabbedCardConfig {
        @IdRes
        private final int mCardTitleContainerScrollerId;
        @IdRes
        private final int mCardPagerContainerId;
        @IdRes
        private final int mCardPagerContainerHelperId;
        private final boolean mIsViewPagerScrollable;
        private final boolean mIsViewPagerEdgeScrollable;
        @NonNull
        private final String mTabHeaderTag;
        @NonNull
        private final String mTabItemTag;

        public TabbedCardConfig(@IdRes int cardTitleContainerScrollerId,
                         @IdRes int cardPagerContainerId,
                         @IdRes int cardPagerContainerHelperId,
                         boolean isViewPagerScrollable,
                         boolean isViewEdgePagerScrollable,
                         @NonNull String tabHeaderTag,
                         @NonNull String tabItemTag) {
            mCardTitleContainerScrollerId = cardTitleContainerScrollerId;
            mCardPagerContainerId = cardPagerContainerId;
            mCardPagerContainerHelperId = cardPagerContainerHelperId;
            mIsViewPagerScrollable = isViewPagerScrollable;
            mIsViewPagerEdgeScrollable = isViewEdgePagerScrollable;
            mTabHeaderTag = tabHeaderTag;
            mTabItemTag = tabItemTag;
        }

        @IdRes
        int getCardTitleContainerScrollerId() {
            return mCardTitleContainerScrollerId;
        }

        @IdRes
        int getCardPagerContainerId() {
            return mCardPagerContainerId;
        }

        @IdRes
        int getCardPagerContainerHelperId() {
            return mCardPagerContainerHelperId;
        }

        boolean isViewPagerScrollable() {
            return mIsViewPagerScrollable;
        }

        boolean isViewPagerEdgeScrollable() {
            return mIsViewPagerEdgeScrollable;
        }

        @NonNull
        String getTabHeaderTag() {
            return mTabHeaderTag;
        }

        @NonNull
        String getTabItemTag() {
            return mTabItemTag;
        }
    }

    private class BaseTabTitleBarHost implements AbstractTabBar.Host<ACTION> {
        @Override
        public void setCurrentPage(int pos, boolean muteEvents) {
            if (muteEvents) {
                mTabTitleBarIgnoreScrollEvents = true;
            }
            mPager.setCurrentItem(pos);
        }

        @Override
        public void onActiveTabClicked(@NonNull ACTION action, int tabPosition) {
            mActiveTabClickListener.onActiveTabClicked(action, tabPosition);
        }
    }

    public interface ActiveTabClickListener<ACTION> {
        void onActiveTabClicked(@NonNull ACTION action, int tabPosition);
    }

    private class PagerChangeListener implements ViewPager.OnPageChangeListener {

        int mCurrentState = ViewPager.SCROLL_STATE_IDLE;

        @Override
        public void onPageSelected(int position) {
            if (mViewPagerHeightCalculator == null) {
                mPager.requestLayout();
            } else if (mCurrentState == ViewPager.SCROLL_STATE_IDLE) {
                fixViewPagerHeightOnScrollEnd(position);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mCurrentState != ViewPager.SCROLL_STATE_IDLE) {
                updateViewPagerHeightOnScroll(position, positionOffset);
            }
            if (mTabTitleBarIgnoreScrollEvents) {
                return;
            }
            mAbstractTabBar.setIntermediateState(position, positionOffset);
        }

        private void updateViewPagerHeightOnScroll(int position, float positionOffset) {
            if (mViewPagerFixedSizeLayout == null || mViewPagerHeightCalculator == null) {
                return;
            }
            if (mViewPagerHeightCalculator.shouldRequestLayoutOnScroll(position, positionOffset)) {
                mViewPagerHeightCalculator.setPositionAndOffsetForMeasure(position, positionOffset);
                if (mViewPagerFixedSizeLayout.isInLayout()) {
                    mViewPagerFixedSizeLayout.post(mViewPagerFixedSizeLayout::requestLayout);
                } else {
                    mViewPagerFixedSizeLayout.requestLayout();
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mCurrentState = state;
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                int pos = mPager.getCurrentItem();
                fixViewPagerHeightOnScrollEnd(pos);
                if (!mTabTitleBarIgnoreScrollEvents) {
                    mAbstractTabBar.fixScrollPosition(pos);
                }
                mTabTitleBarIgnoreScrollEvents = false;
            }
        }

        private void fixViewPagerHeightOnScrollEnd(int position) {
            if (mViewPagerHeightCalculator != null && mViewPagerFixedSizeLayout != null) {
                mViewPagerHeightCalculator.setPositionAndOffsetForMeasure(position, 0);
                mViewPagerFixedSizeLayout.requestLayout();
            }
        }
    }

    private class DataBindingTransformer implements ViewPager.PageTransformer {
        @Override
        public void transformPage(View page, float position) {
            if (mInSetData) {
                /**
                 * MOBSEARCHANDROID-32776: transformPage with position=0 may be called from inside
                 * PageAdapter.notifyDataSetChanged() for invisible pages.
                 */
                return;
            }

            if (position > -1.0f && position < 1.0f) {
                Binding binding = mBindings.get(page);
                if (binding != null) {
                    binding.bind();
                }
            }
        }
    }

    private class Binding {
        @NonNull
        private final ViewGroup mContainer;
        @NonNull
        private final TAB_DATA mData;
        private final int mPosition;

        @Nullable
        private TAB_VIEW mView;

        private Binding(@NonNull ViewGroup container, @NonNull TAB_DATA data, int position) {
            mContainer = container;
            mData = data;
            mPosition = position;
        }

        void bind() {
            if (mView != null) {
                return;
            }
            mView = bindTabData(mContainer, mData, mPosition);
        }

        void unbind() {
            if (mView == null) {
                return;
            }
            unbindTabData(mView);
            mView = null;
        }
    }

    public interface AbstractTabBar<ACTION> {
        void setViewPool(@NonNull ViewPool viewPool, @NonNull String tabHeaderTag);
        void setHost(@NonNull Host<ACTION> host);
        void setData(@NonNull List<? extends BaseDivTabbedCardUi.Input.TabBase<ACTION>> dataList, int pos,
                     @NonNull ExpressionResolver resolver, @NonNull ExpressionSubscriber subscriber);
        void manuallyScroll(int pos);
        void setIntermediateState(int pos, float positionOffset);
        void fixScrollPosition(int pos);

        void setTabColors(@ColorInt int activeTextColor, @ColorInt int activeBackgroundColor,
                          @ColorInt int inactiveTextColor, @ColorInt int inactiveBackgroundColor);
        @Nullable
        ViewPager.OnPageChangeListener getCustomPageChangeListener();
        void resetScroll();
        void setTypefaceProvider(@NonNull DivTypefaceProvider typefaceProvider);

        interface Host<ACTION> {
            void setCurrentPage(int pos, boolean muteEvents);

            void onActiveTabClicked(@NonNull ACTION action, int tabPosition);
        }
    }
}
