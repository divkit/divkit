package com.yandex.div.legacy.view.tab;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.alicekit.core.utils.Views;
import com.yandex.alicekit.core.views.HeightCalculatorFactory;
import com.yandex.alicekit.core.views.MaxCardHeightCalculator;
import com.yandex.div.DivAction;
import com.yandex.div.legacy.DivBlockWithId;
import com.yandex.div.DivContainerBlock;
import com.yandex.div.DivNumericSize;
import com.yandex.div.DivTabsBlock;
import com.yandex.div.legacy.DivAutoLogger;
import com.yandex.div.legacy.DivLogger;
import com.yandex.div.legacy.DivTextStyleProvider;
import com.yandex.div.legacy.R;
import com.yandex.div.legacy.dagger.DivLegacyScope;
import com.yandex.div.legacy.dagger.LegacyNames;
import com.yandex.div.legacy.state.LegacyDivViewState;
import com.yandex.div.legacy.state.LegacyTabsState;
import com.yandex.div.legacy.view.ContainerDivBlockViewBuilder;
import com.yandex.div.legacy.view.DivBaseViewBuilder;
import com.yandex.div.legacy.view.DivView;
import com.yandex.div.legacy.view.DivViewLegacyUtils;
import com.yandex.div.legacy.view.TextStyle;
import com.yandex.div.view.pooling.ViewPool;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Builder for DivTabsBlock
 */
@DivLegacyScope
public class TabsDivBlockViewBuilder extends DivBaseViewBuilder<DivTabsBlock> {

    private static final String FACTORY_TAG_TAB_LAYOUT = "TabsDivBlockViewBuilder.TAB_LAYOUT";
    private static final String FACTORY_TAG_TAB_HEADER = "TabsDivBlockViewBuilder.TAB_HEADER";
    private static final String FACTORY_TAG_TAB_ITEM = "TabsDivBlockViewBuilder.TAB_ITEM";

    @NonNull
    private final Context mContext;
    @NonNull
    private final ViewPool mViewPool;
    @NonNull
    private final DivTextStyleProvider mTextStyleProvider;
    @NonNull
    private final DivAutoLogger mAutoLogger;
    @NonNull
    private final ContainerDivBlockViewBuilder mContainerBuilder;
    @NonNull
    private final DivLogger mDivLogger;
    @Nullable
    private DivTabsBlock mBlock;

    @Inject
    TabsDivBlockViewBuilder(@NonNull @Named(LegacyNames.THEMED_CONTEXT) Context context,
                            @NonNull ViewPool viewPool,
                            @NonNull DivTextStyleProvider textStyleProvider,
                            @NonNull DivAutoLogger autoLogger,
                            @NonNull ContainerDivBlockViewBuilder containerBuilder,
                            @NonNull DivLogger divLogger) {
        super();

        mContext = context;
        mViewPool = viewPool;
        mTextStyleProvider = textStyleProvider;
        mAutoLogger = autoLogger;
        mContainerBuilder = containerBuilder;
        mDivLogger = divLogger;

        mViewPool.register(FACTORY_TAG_TAB_LAYOUT, () -> new TabsLayout(mContext), 2);
        mViewPool.register(FACTORY_TAG_TAB_HEADER, new TabTitlesLayoutView.TabViewFactory(mContext), 24);
        mViewPool.register(FACTORY_TAG_TAB_ITEM, () -> new TabItemLayout(mContext), 4);
    }

    @Override
    @NonNull
    protected View build(@NonNull DivView divView, @NonNull DivTabsBlock divData) {
        mBlock = divData;
        TabsLayout view = mViewPool.obtain(FACTORY_TAG_TAB_LAYOUT);
        view.getTitleLayout().setOnScrollChangedListener(() -> mDivLogger.logTabTitlesScroll(divView));
        BaseDivTabbedCardUi.ActiveTabClickListener<DivAction> actionActiveTabClickListener = (action, position) -> {
            divView.handleUri(action.url);
            mDivLogger.logActiveTabTitleClicked(divView, position, action);
        };
        DivTabsUiImpl divTabsUi = new DivTabsUiImpl(mViewPool, view, getTabbedCardLayoutIds(),
                                                    MaxCardHeightCalculator::new,
                                                    divView, mTextStyleProvider, mAutoLogger,
                                                    actionActiveTabClickListener);
        divTabsUi.setDisabledScrollPages(getDisabledScrollPages(divData));
        divTabsUi.setData(() -> {
            List<DivSimpleTab> list = new ArrayList<>(divData.items.size());
            for (DivTabsBlock.Item item : divData.items) {
                list.add(new DivSimpleTab(item, view.getResources().getDisplayMetrics()));
            }
            return list;
        });
        divTabsUi.setTabColors(divData.activeTabColor, divData.activeTabBgColor, divData.inactiveTabColor);
        View separator = Views.findViewAndCast(view, R.id.div_tabs_divider);
        separator.setVisibility(divData.hasDelimiter ? View.VISIBLE : View.GONE);
        separator.setBackgroundColor(divData.delimiterColor);
        return view;
    }

    @NonNull
    private static Set<Integer> getDisabledScrollPages(@NonNull DivTabsBlock divData) {
        Set<Integer> result = new HashSet<>();
        for (int i = 0; i < divData.items.size(); ++i) {
            if (hasGalleryInContainer(divData.items.get(i).content)) {
                result.add(i);
            }
        }
        return result;
    }

    private static boolean hasGalleryInContainer(@NonNull DivContainerBlock divContainer) {
        for (DivContainerBlock.Children child : divContainer.children) {
            if (child.asDivGalleryBlock() != null) {
                return true;
            }
            DivContainerBlock containerChild = child.asDivContainerBlock();
            if (containerChild != null && hasGalleryInContainer(containerChild)) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    private BaseDivTabbedCardUi.TabbedCardConfig getTabbedCardLayoutIds() {
        return new BaseDivTabbedCardUi.TabbedCardConfig(R.id.base_tabbed_title_container_scroller,
                                                        R.id.div_tabs_pager_container,
                                                        R.id.div_tabs_container_helper,
                                                        true,
                                                        false,
                                                        FACTORY_TAG_TAB_HEADER,
                                                        FACTORY_TAG_TAB_ITEM);
    }

    private class DivTabsUiImpl extends BaseDivTabbedCardUi<BaseDivTabbedCardUi.Input.SimpleTab<DivTabsBlock.Item, DivAction>, ViewGroup, DivAction> {

        @NonNull
        private final DivView mDivView;

        DivTabsUiImpl(@NonNull ViewPool viewPool,
                      @NonNull View view,
                      @NonNull TabbedCardConfig tabbedCardConfig,
                      @NonNull HeightCalculatorFactory heightCalculatorFactory,
                      @NonNull DivView divView,
                      @NonNull DivTextStyleProvider textStyleProvider,
                      @NonNull DivAutoLogger autoLogger,
                      @NonNull ActiveTabClickListener<DivAction> activeTabClickListener) {
            super(
                    viewPool,
                    view,
                    tabbedCardConfig,
                    heightCalculatorFactory,
                    textStyleProvider,
                    autoLogger,
                    null,
                    activeTabClickListener
            );
            mDivView = divView;
        }

        @Override
        public void setData(@Nullable Input<Input.SimpleTab<DivTabsBlock.Item, DivAction>> data) {
            super.setData(data);
            setupCurrentPage(); // can to switch to page only after pages are populated
        }

        @NonNull
        @Override
        protected ViewGroup bindTabData(@NonNull ViewGroup tabView, @NonNull Input.SimpleTab<DivTabsBlock.Item, DivAction> tab, int tabNumber) {
            tabView.removeAllViews();
            DivContainerBlock divData = tab.getItem().content;
            mDivView.setActionHandlerForView(tabView, divData.action);
            addContainerView(tabView, divData, tabNumber);
            return tabView;
        }

        private void setupCurrentPage() {
            LegacyDivViewState viewState = mDivView.getCurrentState();
            Assert.assertNotNull(viewState);
            DivTabsBlock block = mBlock;
            Assert.assertNotNull(block);
            if (viewState == null || block == null) {
                return;
            }
            LegacyTabsState blockState = viewState.getBlockState(block.getBlockId());
            if (blockState != null) {
                mPager.setCurrentItem(blockState.getCurrentPage());
            }
            mPager.addOnPageChangeListener(new UpdateStatePageChangeListener(block, viewState, mDivView, mDivLogger));
        }

        @Override
        protected void unbindTabData(@NonNull ViewGroup viewGroup) {
            viewGroup.removeAllViews();
        }

        @Override
        protected void fillMeasuringTab(@NonNull ViewGroup tabView, @NonNull Input.SimpleTab<DivTabsBlock.Item, DivAction> tab, int tabNumber) {
            tabView.removeAllViews();
            DivContainerBlock divData = tab.getItem().content;
            addContainerView(tabView, divData, tabNumber);
        }

        private void addContainerView(@NonNull ViewGroup tabView, @NonNull DivContainerBlock divData, int tabNumber) {
            if (mBlock == null) {
                Assert.fail("mBlock should have been initialized in the build() method");
            }
            View view = mContainerBuilder.build(mDivView, divData, DivBlockWithId.appendId(mBlock.getBlockId(),
                                                                                           String.valueOf(tabNumber)));
            tabView.addView(view);
        }

    }

    private static class UpdateStatePageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        @NonNull
        private final DivTabsBlock mBlock;
        @NonNull
        private final LegacyDivViewState mDivViewState;
        @NonNull
        private final DivLogger mDivLogger;
        @NonNull
        private final DivView mDivView;

        UpdateStatePageChangeListener(@NonNull DivTabsBlock block, @NonNull LegacyDivViewState divViewState,
                                      @NonNull DivView divView, @NonNull DivLogger divLogger) {
            mBlock = block;
            mDivViewState = divViewState;
            mDivLogger = divLogger;
            mDivView = divView;
        }

        @Override
        public void onPageSelected(int page) {
            mDivViewState.putBlockState(mBlock.getBlockId(), new LegacyTabsState(page));
            mDivLogger.logTabPageChanged(mDivView, page);
        }
    }

    private static class DivSimpleTab implements BaseDivTabbedCardUi.Input.SimpleTab<DivTabsBlock.Item, DivAction> {

        private final DivTabsBlock.Item mItem;
        private final DisplayMetrics mMetrics;

        DivSimpleTab(DivTabsBlock.Item item, DisplayMetrics metrics) {
            mItem = item;
            mMetrics = metrics;
        }

        @Override
        public String getTitle() {
            return mItem.title.text;
        }

        @Nullable
        @Override
        public DivAction getActionable() {
            return mItem.title.action;
        }

        @Nullable
        @Override
        public TextStyle getTextStyle() {
            // will be support in future releases
            return null;
        }

        @Nullable
        @Override
        public Integer getTabHeight() {
            DivNumericSize divSize = mItem.content.height.asDivNumericSize();
            return divSize != null ? DivViewLegacyUtils.divSizeToLayoutParamsSize(divSize, mMetrics) : null;
        }

        @NonNull
        @Override
        public DivTabsBlock.Item getItem() {
            return mItem;
        }
    }
}
