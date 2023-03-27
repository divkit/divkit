package com.yandex.div.internal.widget.tabs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import com.yandex.div.core.font.DivTypefaceProvider;
import com.yandex.div.core.view2.divs.tabs.DivTabsBinderKt;
import com.yandex.div.internal.core.ExpressionSubscriber;
import com.yandex.div.internal.viewpool.PseudoViewPool;
import com.yandex.div.internal.viewpool.ViewFactory;
import com.yandex.div.internal.viewpool.ViewPool;
import com.yandex.div.json.expressions.ExpressionResolver;
import com.yandex.div2.DivTabs;

import java.util.List;

public class TabTitlesLayoutView<ACTION>
        extends BaseIndicatorTabLayout
        implements BaseDivTabbedCardUi.AbstractTabBar<ACTION> {

    private static final String FACTORY_TAG_TAB_HEADER = "TabTitlesLayoutView.TAB_HEADER";

    @Nullable
    private Host<ACTION> mHost;
    @Nullable
    private List<? extends BaseDivTabbedCardUi.Input.TabBase<ACTION>> mDataList;

    @NonNull
    private final PseudoViewPool mDefaultViewPool;
    @NonNull
    private ViewPool mViewPool;
    @NonNull
    private String mTabHeaderTag;
    @Nullable
    private DivTabs.TabTitleStyle mTabTitleStyle;
    @Nullable
    private OnScrollChangedListener mOnScrollChangedListener;
    private boolean mShouldDispatchScroll = false;

    public TabTitlesLayoutView(Context context) {
        this(context, null, 0);
    }

    public TabTitlesLayoutView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabTitlesLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTabMode(BaseIndicatorTabLayout.MODE_SCROLLABLE);
        setTabIndicatorHeight(0);
        setOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                if (mHost == null) {
                    return;
                }

                int pos = tab.getPosition();
                mHost.setCurrentPage(pos, false);
            }

            @Override
            public void onTabUnselected(Tab tab) {
                // not implemented
            }

            @Override
            public void onTabReselected(Tab tab) {
                if (mHost == null) {
                    return;
                }

                int pos = tab.getPosition();
                BaseDivTabbedCardUi.Input.TabBase<ACTION> tabBase;
                if (mDataList != null) {
                    tabBase = mDataList.get(pos);

                    ACTION action = tabBase == null ? null : tabBase.getActionable();
                    if (action != null) {
                        mHost.onActiveTabClicked(action, pos);
                    }
                }
            }
        });

        mDefaultViewPool = new PseudoViewPool();
        mDefaultViewPool.register(FACTORY_TAG_TAB_HEADER, new TabViewFactory(getContext()), 0);

        mViewPool = mDefaultViewPool;
        mTabHeaderTag = FACTORY_TAG_TAB_HEADER;
    }

    @Override
    public void setViewPool(@NonNull ViewPool viewPool, @NonNull String tabHeaderTag) {
        mViewPool = viewPool;
        mTabHeaderTag = tabHeaderTag;
    }

    @Override
    protected TabView createTabView(@NonNull Context context) {
        return mViewPool.obtain(mTabHeaderTag);
    }

    @Override
    public void setHost(@NonNull Host<ACTION> host) {
        mHost = host;
    }

    @Override
    public void setData(@NonNull List<? extends BaseDivTabbedCardUi.Input.TabBase<ACTION>> dataList, int pos,
                        @NonNull ExpressionResolver resolver, @NonNull ExpressionSubscriber subscriber) {
        mDataList = dataList;
        removeAllTabs();
        final int size = dataList.size();
        final int posToSelect = pos >= 0 && pos < size ? pos : 0;
        for (int i = 0; i < size; ++i) {
            Tab tab = newTab().setText(dataList.get(i).getTitle());
            observeTabTitleStyle(tab.getTabView(), resolver, subscriber);
            addTab(tab, i == posToSelect);
        }
    }

    private void observeTabTitleStyle(TabView tabView, ExpressionResolver resolver, ExpressionSubscriber subscriber) {
        if (mTabTitleStyle == null) {
            return;
        }
        DivTabsBinderKt.observeStyle(tabView, mTabTitleStyle, resolver, subscriber);
    }

    @Override
    public void manuallyScroll(int pos) {
        selectTab(pos);
    }

    @Override
    public void setIntermediateState(int pos, float positionOffset) {
        // not implemented
    }

    @Override
    public void fixScrollPosition(int pos) {
        selectTab(pos);
    }

    @Override
    public void setTabColors(@ColorInt int activeTextColor, @ColorInt int activeBackgroundColor,
                             @ColorInt int inactiveTextColor, @ColorInt int inactiveBackgroundColor) {
        setTabTextColors(inactiveTextColor, activeTextColor);
        setSelectedTabIndicatorColor(activeBackgroundColor);
        setTabBackgroundColor(inactiveBackgroundColor);
    }

    @Nullable
    @Override
    public ViewPager.OnPageChangeListener getCustomPageChangeListener() {
        TabLayoutOnPageChangeListener pageChangeListener = getPageChangeListener();
        pageChangeListener.reset();
        return pageChangeListener;
    }

    @Override
    public void resetScroll() {
        scrollTo(0, 0);
        manuallyScroll(0);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedListener != null && mShouldDispatchScroll) {
            mOnScrollChangedListener.onScrolled();
            mShouldDispatchScroll = false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = super.dispatchTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mShouldDispatchScroll = true;
        }
        return result;
    }

    public void setOnScrollChangedListener(@Nullable OnScrollChangedListener onScrollChangedListener) {
        mOnScrollChangedListener = onScrollChangedListener;
    }

    @Override
    public void setTypefaceProvider(@NonNull DivTypefaceProvider typefaceProvider) {
        bindTypefaceProvider(typefaceProvider);
    }

    public void setTabTitleStyle(@Nullable DivTabs.TabTitleStyle tabTitleStyle) {
        mTabTitleStyle = tabTitleStyle;
    }

    /**
     * Listener for title layout scrolls
     */
    public interface OnScrollChangedListener {
        void onScrolled();
    }

    public static class TabViewFactory implements ViewFactory<TabView> {

        @NonNull
        private final Context mContext;

        public TabViewFactory(@NonNull Context context) {
            mContext = context;
        }

        @NonNull
        @Override
        public TabView createView() {
            return new TabView(mContext);
        }
    }
}
