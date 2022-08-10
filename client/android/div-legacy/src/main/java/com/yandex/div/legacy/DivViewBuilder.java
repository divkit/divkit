package com.yandex.div.legacy;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.div.DivBaseBlock;
import com.yandex.div.DivButtonsBlock;
import com.yandex.div.DivContainerBlock;
import com.yandex.div.DivData;
import com.yandex.div.DivFooterBlock;
import com.yandex.div.DivGalleryBlock;
import com.yandex.div.DivImageBlock;
import com.yandex.div.DivSeparatorBlock;
import com.yandex.div.DivTableBlock;
import com.yandex.div.DivTabsBlock;
import com.yandex.div.DivTitleBlock;
import com.yandex.div.DivTrafficBlock;
import com.yandex.div.DivUniversalBlock;
import com.yandex.div.legacy.dagger.DivLegacyScope;
import com.yandex.div.legacy.view.ButtonsDivBlockViewBuilder;
import com.yandex.div.legacy.view.ContainerDivBlockViewBuilder;
import com.yandex.div.legacy.view.DivBaseViewBuilder;
import com.yandex.div.legacy.view.DivImageBlockViewBuilder;
import com.yandex.div.legacy.view.DivTableBlockViewBuilder;
import com.yandex.div.legacy.view.DivView;
import com.yandex.div.legacy.view.DivViewUtils;
import com.yandex.div.legacy.view.FooterDivViewBuilder;
import com.yandex.div.legacy.view.GalleryDivViewBuilder;
import com.yandex.div.legacy.view.SeparatorDivViewBuilder;
import com.yandex.div.legacy.view.TitleDivBlockViewBuilder;
import com.yandex.div.legacy.view.TrafficDivViewBuilder;
import com.yandex.div.legacy.view.UniversalDivViewBuilder;
import com.yandex.div.legacy.view.tab.TabsDivBlockViewBuilder;
import java.util.List;
import javax.inject.Inject;

@DivLegacyScope
public class DivViewBuilder {

    @NonNull
    private final ButtonsDivBlockViewBuilder mButtonViewBuilder;
    @NonNull
    private final ContainerDivBlockViewBuilder mContainerViewBuilder;
    @NonNull
    private final FooterDivViewBuilder mFooterViewBuilder;
    @NonNull
    private final GalleryDivViewBuilder mGalleryViewBuilder;
    @NonNull
    private final DivImageBlockViewBuilder mImageViewBuilder;
    @NonNull
    private final SeparatorDivViewBuilder mSeparatorViewBuilder;
    @NonNull
    private final DivTableBlockViewBuilder mTableViewBuilder;
    @NonNull
    private final TabsDivBlockViewBuilder mTabsViewBuilder;
    @NonNull
    private final TitleDivBlockViewBuilder mTitleViewBuilder;
    @NonNull
    private final TrafficDivViewBuilder mTrafficViewBuilder;
    @NonNull
    private final UniversalDivViewBuilder mUniversalViewBuilder;
    @NonNull
    private final DivInternalLogger mLogger;

    @Inject
    DivViewBuilder(@NonNull ButtonsDivBlockViewBuilder buttonViewBuilder,
                   @NonNull ContainerDivBlockViewBuilder containerViewBuilder,
                   @NonNull FooterDivViewBuilder footerViewBuilder,
                   @NonNull GalleryDivViewBuilder galleryViewBuilder,
                   @NonNull DivImageBlockViewBuilder imageViewBuilder,
                   @NonNull SeparatorDivViewBuilder separatorViewBuilder,
                   @NonNull DivTableBlockViewBuilder tableViewBuilder,
                   @NonNull TabsDivBlockViewBuilder tabsViewBuilder,
                   @NonNull TitleDivBlockViewBuilder titleViewBuilder,
                   @NonNull TrafficDivViewBuilder trafficViewBuilder,
                   @NonNull UniversalDivViewBuilder universalViewBuilder,
                   @NonNull DivInternalLogger logger) {
        mButtonViewBuilder = buttonViewBuilder;
        mContainerViewBuilder = containerViewBuilder;
        mFooterViewBuilder = footerViewBuilder;
        mGalleryViewBuilder = galleryViewBuilder;
        mImageViewBuilder = imageViewBuilder;
        mSeparatorViewBuilder = separatorViewBuilder;
        mTableViewBuilder = tableViewBuilder;
        mTabsViewBuilder = tabsViewBuilder;
        mTitleViewBuilder = titleViewBuilder;
        mTrafficViewBuilder = trafficViewBuilder;
        mUniversalViewBuilder = universalViewBuilder;
        mLogger = logger;
    }

    public void build(@NonNull DivView divView, @NonNull ViewGroup container, @NonNull DivData.State state, @NonNull String path) {
        final List<DivData.State.Block> blocks = state.blocks;
        final Visitor visitor = new Visitor(divView, path);

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, n = blocks.size(); i < n; i++) {
            View child = visitor.visit(blocks.get(i));
            if (child != null) {
                container.addView(child);
            }
        }
    }

    public void build(@NonNull DivView divView, @NonNull ViewGroup container, @NonNull DivContainerBlock block, @NonNull String path) {
        final List<DivContainerBlock.Children> children = block.children;
        final Visitor visitor = new Visitor(divView, path);

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, n = children.size(); i < n; i++) {
            View child = visitor.visit(children.get(i));
            if (child != null) {
                container.addView(child);
            }
        }
    }

    private class Visitor extends DivVisitor<View> {

        @NonNull
        private final DivView mDivView;
        @NonNull
        private final String mPath;

        private int mNumVisited = 0;

        private Visitor(@NonNull DivView divView, @NonNull String path) {
            mDivView = divView;
            mPath = path;
        }

        @Nullable
        @Override
        public View visit(@NonNull DivData.State.Block block) {
            View view = super.visit(block);
            mNumVisited++;
            return view;
        }

        @Nullable
        @Override
        public View visit(@NonNull DivContainerBlock.Children block) {
            View view = super.visit(block);
            mNumVisited++;
            return view;
        }

        @Nullable
        @Override
        public View visit(@NonNull DivButtonsBlock divData) {
            if (divData.items.isEmpty()) {
                return null;
            }
            return buildDivView(divData, mButtonViewBuilder);
        }

        @Nullable
        @Override
        public View visit(@NonNull DivContainerBlock divData) {
            return buildDivView(divData, mContainerViewBuilder);
        }

        @Nullable
        @Override
        public View visit(@NonNull DivFooterBlock divData) {
            return buildDivView(divData, mFooterViewBuilder);
        }

        @Nullable
        @Override
        public View visit(@NonNull DivGalleryBlock divData) {
            return buildDivView(divData, mGalleryViewBuilder);
        }

        @Nullable
        @Override
        public View visit(@NonNull DivImageBlock divData) {
            if (!LegacyDivDataUtils.isDivImageValid(divData.image)) {
                return null;
            }
            return buildDivView(divData, mImageViewBuilder);
        }

        @Nullable
        @Override
        public View visit(@NonNull DivSeparatorBlock divData) {
            return buildDivView(divData, mSeparatorViewBuilder);
        }

        @Nullable
        @Override
        public View visit(@NonNull DivTableBlock divData) {
            return buildDivView(divData, mTableViewBuilder);
        }

        @Nullable
        @Override
        protected View visit(@NonNull DivTabsBlock divData) {
            return buildDivView(divData, mTabsViewBuilder);
        }

        @Nullable
        @Override
        public View visit(@NonNull DivTitleBlock divData) {
            if (!LegacyDivDataUtils.isValidBlock(divData)) {
                return null;
            }
            return buildDivView(divData, mTitleViewBuilder);
        }

        @Nullable
        @Override
        public View visit(@NonNull DivTrafficBlock divData) {
            if (!LegacyDivDataUtils.isValidBlock(divData)) {
                return null;
            }
            return buildDivView(divData, mTrafficViewBuilder);
        }

        @Nullable
        @Override
        public View visit(@NonNull DivUniversalBlock divData) {
            return buildDivView(divData, mUniversalViewBuilder);
        }

        @Nullable
        private <B extends DivBaseBlock> View buildDivView(@NonNull B data, @NonNull DivBaseViewBuilder<B> builder) {
            View view;
            try {
                view = builder.build(mDivView, data, DivBlockWithId.appendId(mPath, String.valueOf(mNumVisited)));
            } catch (RuntimeException cause) {
                mLogger.logViewBuildingFailure(mDivView, data);
                Assert.fail("Div view building failed", cause);
                return null;
            }

            if (view == null) {
                return null;
            }

            if (!(data instanceof DivGalleryBlock)) {
                DivViewUtils.applyPadding(data.paddingModifier, view);
            }
            if (data.action != null) {
                mDivView.setActionHandlerForView(view, data.action);
            }
            return view;
        }
    }
}
