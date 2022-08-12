package com.yandex.div.legacy.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.div.DivAlignment;
import com.yandex.div.DivAlignmentVertical;
import com.yandex.div.DivBackground;
import com.yandex.div.DivContainerBlock;
import com.yandex.div.DivNumericSize;
import com.yandex.div.DivPredefinedSize;
import com.yandex.div.core.images.DivImageLoader;
import com.yandex.div.legacy.DivViewBuilder;
import com.yandex.div.legacy.dagger.DivLegacyScope;
import com.yandex.div.legacy.dagger.LegacyNames;
import com.yandex.div.view.pooling.ViewPool;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Build view for {@link com.yandex.div.DivContainerBlock}
 */
@DivLegacyScope
public class ContainerDivBlockViewBuilder extends DivBaseViewBuilder<DivContainerBlock> {

    private static final String FACTORY_TAG_CONTAINER = "ContainerDivBlockViewBuilder.CONTAINER";
    private static final String FACTORY_TAG_SHADOW_FRAME = "ContainerDivBlockViewBuilder.SHADOW_FRAME";
    private static final String FACTORY_TAG_BORDER_FRAME = "ContainerDivBlockViewBuilder.BORDER_FRAME";

    @NonNull
    private final Context mContext;
    @NonNull
    private final ViewPool mViewPool;
    @NonNull
    private final DivImageLoader mDivImageLoader;
    @NonNull
    private final Lazy<DivViewBuilder> mViewBuilder;

    @Inject
    ContainerDivBlockViewBuilder(@NonNull @Named(LegacyNames.CONTEXT) Context context,
                                 @NonNull ViewPool viewPool,
                                 @NonNull DivImageLoader imageLoader,
                                 @NonNull Lazy<DivViewBuilder> viewBuilder) {
        super();
        mContext = context;
        mViewPool = viewPool;
        mDivImageLoader = imageLoader;
        mViewBuilder = viewBuilder;

        mViewPool.register(FACTORY_TAG_CONTAINER, () -> new LinearLayout(mContext), 4);
        mViewPool.register(FACTORY_TAG_SHADOW_FRAME, () -> new ContainerShadowLayout(mContext), 4);
        mViewPool.register(FACTORY_TAG_BORDER_FRAME, () -> new ContainerBorderLayout(mContext), 4);
    }

    @Override
    @NonNull
    protected View build(@NonNull DivView divView, @NonNull DivContainerBlock divData) {
        final LinearLayout containerView = mViewPool.obtain(FACTORY_TAG_CONTAINER);
        ViewGroup.LayoutParams containerLayoutParams = createLayoutParams(divView, divData);
        containerView.setLayoutParams(containerLayoutParams);
        containerView.setOrientation(DivContainerBlock.Direction.VERTICAL.equals(divData.direction)
                                             ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);

        containerView.setGravity(processGravity(divData));

        DivViewBuilder viewBuilder = mViewBuilder.get();
        viewBuilder.build(divView, containerView, divData, divData.getBlockId());

        for (int i = 0; i < containerView.getChildCount(); i++) {
            View child = containerView.getChildAt(i);
            LinearLayout.LayoutParams layoutParams = makeLayoutParams(divData.direction, child.getLayoutParams());
            if (layoutParams != null) {
                child.setLayoutParams(layoutParams);
            }
        }

        processBackgroundData(divView, divData, containerView);

        ViewGroup rootView = createRootView(divData.frame);
        if (rootView != null) {
            rootView.setLayoutParams(containerLayoutParams);
            rootView.addView(containerView);
            return rootView;
        }

        return containerView;
    }

    @Nullable
    private static LinearLayout.LayoutParams makeLayoutParams(@Nullable String direction, @Nullable ViewGroup.LayoutParams original) {
        if (DivContainerBlock.Direction.HORIZONTAL.equals(direction)) {
            LinearLayout.LayoutParams layoutParams = original instanceof LinearLayout.LayoutParams
                    ? (LinearLayout.LayoutParams) original
                    : new LinearLayout.LayoutParams(original);
            layoutParams.weight = layoutParams.width == LinearLayout.LayoutParams.MATCH_PARENT ? 1 : layoutParams.weight;
            return layoutParams;
        }
        return null;
    }

    private int processGravity(@NonNull DivContainerBlock divBlock) {
        int gravity = 0;
        switch (divBlock.alignmentHorizontal) {
            case DivAlignment.LEFT:
                gravity = Gravity.LEFT;
                break;
            case DivAlignment.CENTER:
                gravity = Gravity.CENTER_HORIZONTAL;
                break;
            case DivAlignment.RIGHT:
                gravity = Gravity.RIGHT;
                break;
            default:
                Assert.fail("Unsupported container gravity");
                break;
        }
        switch (divBlock.alignmentVertical) {
            case DivAlignmentVertical.TOP:
                gravity |= Gravity.TOP;
                break;
            case DivAlignmentVertical.CENTER:
                gravity |= Gravity.CENTER_VERTICAL;
                break;
            case DivAlignmentVertical.BOTTOM:
                gravity |= Gravity.BOTTOM;
                break;
            default:
                Assert.fail("Unsupported container gravity");
                break;
        }
        return gravity;
    }

    @Nullable
    private ViewGroup createRootView(@Nullable DivContainerBlock.Frame frame) {
        if (frame == null) {
            return null;
        }

        if (DivContainerBlock.Frame.Style.SHADOW.equals(frame.style)) {
            return mViewPool.obtain(FACTORY_TAG_SHADOW_FRAME);
        }

        final RoundedCornersWithStrokeLayout containerView = mViewPool.obtain(FACTORY_TAG_BORDER_FRAME);

        if (DivContainerBlock.Frame.Style.ONLY_ROUND_CORNERS.equals(frame.style)) {
            containerView.setStrokeWidth(0);
        } else if (frame.color != null) { // BORDER
            containerView.setStrokeColor(frame.color);
        }

        return containerView;
    }

    @NonNull
    private static LinearLayout.LayoutParams createLayoutParams(@NonNull DivView divView, @NonNull DivContainerBlock divBlock) {
        int width = getSize(divView, divBlock.width.asDivNumericSize(), divBlock.width.asDivPredefinedSize());
        int height = getSize(divView, divBlock.height.asDivNumericSize(), divBlock.height.asDivPredefinedSize());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        if (width == LinearLayout.LayoutParams.MATCH_PARENT || height == LinearLayout.LayoutParams.MATCH_PARENT) {
            lp.weight = 1;
        }
        return lp;
    }

    private static int getSize(@NonNull DivView divView,
                        @Nullable DivNumericSize numericSize,
                        @Nullable DivPredefinedSize predefinedSize) {
        if (numericSize != null) {
            DisplayMetrics displayMetrics = divView.getResources().getDisplayMetrics();
            return DivViewUtils.divSizeToLayoutParamsSize(numericSize, displayMetrics);
        } else if (predefinedSize != null) {
            if (DivPredefinedSize.Value.WRAP_CONTENT.equals(predefinedSize.value)) {
                return LinearLayout.LayoutParams.WRAP_CONTENT;
            } else if (DivPredefinedSize.Value.MATCH_PARENT.equals(predefinedSize.value)) {
                return LinearLayout.LayoutParams.MATCH_PARENT;
            }
            Assert.fail("No predefined size");
            return -1;
        }
        Assert.fail("No size defined");
        return -1;
    }

    private void processBackgroundData(@NonNull DivView divView, @NonNull DivContainerBlock divBlockData, @NonNull View view) {
        final List<DivBackground> backgroundData = divBlockData.background;
        if (backgroundData == null || backgroundData.isEmpty()) {
            return;
        }
        final List<Drawable> drawableList = new ArrayList<>(backgroundData.size());
        //noinspection Convert2streamapi
        for (final DivBackground background : backgroundData) {
            Drawable drawable = DivViewUtils.divBackgroundToDrawable(background, mDivImageLoader, divView);
            if (drawable != null) {
                drawableList.add(drawable);
            }
        }

        if (drawableList.isEmpty()) {
            return;
        }

        final LayerDrawable layerDrawable = new LayerDrawable(drawableList.toArray(new Drawable[drawableList.size()]));
        view.setBackground(layerDrawable);
    }
}
