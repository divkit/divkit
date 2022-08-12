package com.yandex.div.legacy.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.alicekit.core.utils.DrawableUtils;
import com.yandex.alicekit.core.utils.Views;
import com.yandex.alicekit.core.views.EllipsizingTextView;
import com.yandex.div.DivContainerBlock;
import com.yandex.div.DivGalleryBlock;
import com.yandex.div.DivNumericSize;
import com.yandex.div.DivPaddingModifier;
import com.yandex.div.DivPosition;
import com.yandex.div.DivPredefinedSize;
import com.yandex.div.core.images.DivImageLoader;
import com.yandex.div.core.images.LoadReference;
import com.yandex.div.legacy.DivBlockWithId;
import com.yandex.div.legacy.DivLogger;
import com.yandex.div.legacy.DivTextStyleProvider;
import com.yandex.div.legacy.LegacyDivImageDownloadCallback;
import com.yandex.div.legacy.R;
import com.yandex.div.legacy.dagger.DivLegacyScope;
import com.yandex.div.legacy.dagger.LegacyNames;
import com.yandex.div.legacy.state.LegacyDivViewState;
import com.yandex.div.legacy.state.LegacyGalleryState;
import com.yandex.div.legacy.state.LegacyUpdateStateScrollListener;
import com.yandex.div.util.DivKitImageUtils;
import com.yandex.div.view.PaddingItemDecoration;
import com.yandex.div.view.pooling.ViewPool;
import com.yandex.images.CachedBitmap;
import javax.inject.Inject;
import javax.inject.Named;

@DivLegacyScope
public class GalleryDivViewBuilder extends DivBaseViewBuilder<DivGalleryBlock> {

    private static final String FACTORY_TAG_GALLERY = "GalleryDivViewBuilder.GALLERY";
    private static final String FACTORY_TAG_ITEM = "GalleryDivViewBuilder.ITEM";
    private static final String FACTORY_TAG_TAIL = "GalleryDivViewBuilder.TAIL";

    @NonNull
    private final Context mContext;
    @NonNull
    private final ViewPool mViewPool;
    @NonNull
    private final DivImageLoader mImageLoader;
    @NonNull
    private final DivTextStyleProvider mTextStyleProvider;
    @NonNull
    private final ContainerDivBlockViewBuilder mContainerBuilder;
    @NonNull
    private final DivLogger mDivLogger;

    @Inject
    GalleryDivViewBuilder(@NonNull @Named(LegacyNames.CONTEXT) Context context,
                          @NonNull ViewPool viewPool,
                          @NonNull DivImageLoader imageLoader,
                          @NonNull DivTextStyleProvider textStyleProvider,
                          @NonNull ContainerDivBlockViewBuilder containerBuilder,
                          @NonNull DivLogger divLogger) {
        super();
        mContext = context;
        mViewPool = viewPool;
        mImageLoader = imageLoader;
        mTextStyleProvider = textStyleProvider;
        mContainerBuilder = containerBuilder;
        mDivLogger = divLogger;

        mViewPool.register(FACTORY_TAG_GALLERY, () -> createGalleryView(mContext), 2);
        mViewPool.register(FACTORY_TAG_ITEM, () -> createItemView(mContext), 8);
        mViewPool.register(FACTORY_TAG_TAIL, () -> createTailView(mContext), 2);
    }

    @Override
    @NonNull
    protected View build(@NonNull DivView divView, @NonNull DivGalleryBlock divData) {
        RecyclerView galleryView = mViewPool.obtain(FACTORY_TAG_GALLERY);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
        galleryView.setLayoutManager(layoutManager);

        GalleryAdapter adapter = new GalleryAdapter(divView, divData);
        galleryView.setAdapter(adapter);

        setupScrollState(divView, divData, galleryView, layoutManager);

        Resources resources = mContext.getResources();
        galleryView.addItemDecoration(divData.tail != null
                                              ? buildPaddingDecorationWithTail(resources, divData)
                                              : buildPaddingDecorationNoTail(resources, divData));

        return galleryView;
    }

    private void setupScrollState(@NonNull DivView divView,
                                  @NonNull DivGalleryBlock block,
                                  @NonNull RecyclerView recyclerView,
                                  @NonNull LinearLayoutManager layoutManager) {
        LegacyDivViewState viewState = divView.getCurrentState();
        Assert.assertNotNull(viewState);
        if (viewState == null) {
            return;
        }

        LegacyGalleryState blockState = viewState.getBlockState(block.getBlockId());
        if (blockState != null) {
            layoutManager.scrollToPositionWithOffset(blockState.getVisibleItemIndex(), blockState.getScrollOffset());
        }
        recyclerView.addOnScrollListener(
                new LegacyUpdateStateScrollListener(block.getBlockId(), viewState, layoutManager)
        );
        recyclerView.addOnScrollListener(new ScrollListener(divView, layoutManager));
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {
        @NonNull
        private final DivView mDivView;
        @NonNull
        private final LinearLayoutManager mLayoutManager;
        private final int mMinimumSignificantDx;

        private int mTotalDx = 0;
        private boolean mAlreadyLogged = false;

        ScrollListener(@NonNull DivView divView, @NonNull LinearLayoutManager layoutManager) {
            mDivView = divView;
            mLayoutManager = layoutManager;
            mMinimumSignificantDx = divView.getConfig().getLogCardScrollSignificantThreshold();
        }

        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                mAlreadyLogged = false;
            }
        }

        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int minimumDx = mMinimumSignificantDx > 0
                    ? mMinimumSignificantDx
                    : mLayoutManager.getWidth() / 20;
            mTotalDx += Math.abs(dx);
            if (mTotalDx > minimumDx) {
                mTotalDx = 0;
                if (!mAlreadyLogged) {
                    mAlreadyLogged = true;
                    mDivLogger.logGalleryScroll(mDivView);
                }
            }
        }
    }

    @NonNull
    private RecyclerView.ItemDecoration buildPaddingDecorationNoTail(@NonNull Resources resources, @NonNull DivGalleryBlock divData) {
        @Px int paddingLeft = resources.getDimensionPixelOffset(R.dimen.div_gallery_horizontal_padding);
        @Px int paddingRight = paddingLeft;

        DivPaddingModifier paddingModifier = divData.paddingModifier;
        if (paddingModifier != null) {
            final int paddingSize = resources.getDimensionPixelOffset(DivViewUtils.getPaddingDimenResBySize(paddingModifier.size));

            if (DivPosition.LEFT.equals(paddingModifier.position)) {
                paddingLeft = paddingSize;
            } else {
                paddingRight = paddingSize;
            }
        }

        @Px int itemInternalHorizontalPadding = getItemInternalHorizontalPadding(resources);
        @Px final int midPadding = getPaddingBetweenItems(divData.paddingBetweenItems, resources);
        @Px final int paddingTop = divSizeToPixelSize(divData.paddingTop, resources);
        @Px final int paddingBottom = divSizeToPixelSize(divData.paddingBottom, resources);
        return new PaddingItemDecoration(paddingLeft - itemInternalHorizontalPadding, midPadding, paddingRight - itemInternalHorizontalPadding,
                                         paddingTop, paddingBottom);
    }

    @NonNull
    private RecyclerView.ItemDecoration buildPaddingDecorationWithTail(@NonNull Resources resources, @NonNull DivGalleryBlock divData) {
        @Px int itemInternalHorizontalPadding = getItemInternalHorizontalPadding(resources);
        @Px int tailPaddingRight = resources.getDimensionPixelOffset(R.dimen.div_gallery_tail_horizontal_padding);
        @Px final int tailPaddingLeft = tailPaddingRight - itemInternalHorizontalPadding;

        @Px int paddingLeft = resources.getDimensionPixelOffset(R.dimen.div_gallery_horizontal_padding);

        DivPaddingModifier paddingModifier = divData.paddingModifier;
        if (paddingModifier != null) {
            final int paddingSize = resources.getDimensionPixelOffset(DivViewUtils.getPaddingDimenResBySize(paddingModifier.size));

            if (DivPosition.LEFT.equals(paddingModifier.position)) {
                paddingLeft = paddingSize;
            } else {
                tailPaddingRight = paddingSize;
            }
        }

        @Px final int midPadding = getPaddingBetweenItems(divData.paddingBetweenItems, resources);
        @Px final int paddingTop = divSizeToPixelSize(divData.paddingTop, resources);
        @Px final int paddingBottom = divSizeToPixelSize(divData.paddingBottom, resources);
        return new GalleryWithTailItemDecoration(paddingLeft - itemInternalHorizontalPadding, midPadding,
                                                 tailPaddingLeft, tailPaddingRight,
                                                 paddingTop, paddingBottom);
    }

    private static int getItemInternalHorizontalPadding(@NonNull Resources resources) {
        return resources.getDimensionPixelOffset(R.dimen.div_gallery_horizontal_internal_item_padding);
    }

    private static int getPaddingBetweenItems(@NonNull DivNumericSize paddingBetweenItems, Resources resources) {
        final int midPadding = divSizeToPixelSize(paddingBetweenItems, resources)
                - 2 * getItemInternalHorizontalPadding(resources);

        return Math.max(midPadding, 0);
    }

    private static int divSizeToPixelSize(@NonNull DivNumericSize divSize, @NonNull Resources resources) {
        return DivViewUtils.divSizeToLayoutParamsSize(divSize, resources.getDisplayMetrics());
    }

    @NonNull
    private static RecyclerView createGalleryView(@NonNull Context context) {
        RecyclerView galleryView = new RecyclerView(context);
        galleryView.setId(R.id.div_gallery);
        galleryView.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING);

        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        galleryView.setLayoutParams(layoutParams);
        return galleryView;
    }

    @NonNull
    private static View createItemView(@NonNull Context context) {
        LinearLayout itemLayout = new LinearLayout(context);
        itemLayout.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        itemLayout.setGravity(Gravity.CENTER);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        return itemLayout;
    }

    @NonNull
    private static View createTailView(@NonNull Context context) {
        return new GalleryTailLayout(context);
    }

    private class GalleryAdapter extends RecyclerView.Adapter<GalleryItemViewHolder> {
        private static final int VIEW_TYPE_ITEM = 0;
        private static final int VIEW_TYPE_TAIL = 1;

        @NonNull
        private final DivView mDivView;
        @NonNull
        private final DivGalleryBlock mDivData;

        private int mMaxHeight = -1;

        GalleryAdapter(@NonNull DivView divView, @NonNull DivGalleryBlock divData) {
            mDivView = divView;
            mDivData = divData;
        }

        private void calculateMaxItemsHeight(@NonNull View view) {
            DivNumericSize tempHeight = null;
            for (DivContainerBlock divBlock : mDivData.items) {
                DivNumericSize height = divBlock.height.asDivNumericSize();
                if (tempHeight == null || (height != null && height.value > tempHeight.value)) {
                    tempHeight = height;
                }
            }

            if (tempHeight != null) {
                mMaxHeight = divSizeToPixelSize(tempHeight, view.getResources());
            }
        }

        @Override
        public GalleryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = viewType == VIEW_TYPE_ITEM ? mViewPool.obtain(FACTORY_TAG_ITEM) : mViewPool.obtain(FACTORY_TAG_TAIL);
            if (mMaxHeight == -1) {
                calculateMaxItemsHeight(parent);
            }

            if (mMaxHeight > 0) {
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mMaxHeight);
                view.setLayoutParams(lp);
            }
            return new GalleryItemViewHolder(view, mDivView, mDivData);
        }

        @Override
        public void onBindViewHolder(@NonNull GalleryItemViewHolder holder, int position) {
            if (getItemViewType(position) == VIEW_TYPE_ITEM) {
                DivContainerBlock divData = mDivData.items.get(position);
                holder.bind(divData, position, getItemInternalHorizontalPadding(mDivView.getResources()));
            } else if (mDivData.tail != null) {
                holder.bind(mDivData.tail);
            } else {
                Assert.fail("Internal error, gallery tail is null");
            }
        }

        @Override
        public int getItemCount() {
            int count = mDivData.items.size();
            return mDivData.tail == null ? count : count + 1;
        }

        @Override
        public int getItemViewType(int position) {
            return position < mDivData.items.size() ? VIEW_TYPE_ITEM : VIEW_TYPE_TAIL;
        }
    }

    private class GalleryItemViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final DivView mDivView;
        @NonNull
        private final DivGalleryBlock mDivData;
        private final int mBackgroundStrokeSize;

        GalleryItemViewHolder(@NonNull View itemView, @NonNull DivView divView, @NonNull DivGalleryBlock divData) {
            super(itemView);
            mDivView = divView;
            mDivData = divData;
            mBackgroundStrokeSize = mDivView.getResources().getDimensionPixelSize(R.dimen.div_gallery_tail_image_stroke_size);
        }

        public void bind(@NonNull DivContainerBlock divData, int position, int horizontalMargin) {
            ViewGroup viewGroup = (ViewGroup) itemView;
            viewGroup.removeAllViews();

            View containerView = mContainerBuilder.build(mDivView, divData, DivBlockWithId.appendId(mDivData.getBlockId(), String.valueOf(position)));

            DivPredefinedSize width = divData.width.asDivPredefinedSize();
            if (width != null && DivPredefinedSize.Value.MATCH_PARENT.equals(width.value)) {
                Views.setWidth(itemView, RecyclerView.LayoutParams.MATCH_PARENT);
            }

            mDivView.setActionHandlerForView(itemView, divData.action);

            viewGroup.addView(containerView);

            LinearLayout.LayoutParams marginLayoutParams = new LinearLayout.LayoutParams(containerView.getLayoutParams());
            marginLayoutParams.setMargins(horizontalMargin, 0, horizontalMargin, 0);
            containerView.setLayoutParams(marginLayoutParams);
        }

        public void bind(@NonNull DivGalleryBlock.Tail tailData) {
            EllipsizingTextView textView = itemView.findViewById(R.id.div_gallery_tail_text);
            if (!TextUtils.isEmpty(tailData.text)) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(tailData.text);
                TextStyle textStyle = mTextStyleProvider.getTextStyle(tailData.textStyle);
                textStyle.applyWithEllipsizig(textView);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else {
                textView.setVisibility(View.GONE);
            }

            mDivView.setActionHandlerForView(itemView, tailData.action);

            ImageView imageView = itemView.findViewById(R.id.div_gallery_tail_icon);
            final DivGalleryBlock.Tail.Icon icon = tailData.icon;

            if (icon.imageUrl != null) {

                LoadReference loadReference = mImageLoader.loadImage(
                        icon.imageUrl.toString(),
                        DivKitImageUtils.toDivKitCallback(new LegacyDivImageDownloadCallback(mDivView) {
                            @UiThread
                            @Override
                            public void onSuccess(@NonNull CachedBitmap cachedBitmap) {
                                imageView.setImageBitmap(cachedBitmap.getBitmap());
                            }
                        }));
                mDivView.addLoadReference(loadReference, imageView);
            } else {
                imageView.setBackground(createIconBackground(icon.fillColor, icon.borderColor));
                imageView.setImageDrawable(createIcon(icon.iconColor));
            }
        }

        @NonNull
        private Drawable createIconBackground(@ColorInt int fillColor, @ColorInt int borderColor) {
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.OVAL);
            shape.setColor(fillColor);
            shape.setDither(true);

            if (borderColor != fillColor) {
                shape.setStroke(mBackgroundStrokeSize, borderColor);
            }
            return shape;
        }

        @Nullable
        private Drawable createIcon(@ColorInt int color) {
            Drawable arrowDrawable = DrawableUtils.getDrawable(mDivView.getContext(), R.drawable.div_gallery_tail_arrow);
            if (arrowDrawable == null) {
                Assert.fail("Vector drawable parsing error");
                return null;
            }

            arrowDrawable.mutate();

            // need to wrap to change vector drawable color
            Drawable wrappedDrawable = DrawableCompat.wrap(arrowDrawable);
            DrawableCompat.setTint(wrappedDrawable, color);
            DrawableCompat.setTintMode(wrappedDrawable, PorterDuff.Mode.SRC_IN);

            int size = mDivView.getResources().getDimensionPixelSize(R.dimen.div_gallery_tail_arrow_size);
            wrappedDrawable.setBounds(0, 0, size, size);
            return wrappedDrawable;
        }
    }
}
