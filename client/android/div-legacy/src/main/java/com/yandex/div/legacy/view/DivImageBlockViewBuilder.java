package com.yandex.div.legacy.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.DivImageBlock;
import com.yandex.div.core.images.DivImageLoader;
import com.yandex.div.core.images.LoadReference;
import com.yandex.div.legacy.R;
import com.yandex.div.legacy.dagger.DivLegacyScope;
import com.yandex.div.legacy.dagger.LegacyNames;
import com.yandex.div.view.pooling.ViewPool;
import javax.inject.Inject;
import javax.inject.Named;

@DivLegacyScope
public class DivImageBlockViewBuilder extends DivElementDataViewBuilder<DivImageBlock> {

    private static final String FACTORY_TAG_IMAGE = "DivImageBlockViewBuilder.IMAGE";

    @NonNull
    private final Context mThemedContext;
    @NonNull
    private final ViewPool mViewPool;
    @NonNull
    private final DivImageLoader mImageLoader;

    @Inject
    DivImageBlockViewBuilder(@NonNull @Named(LegacyNames.THEMED_CONTEXT) Context themedContext,
                             @NonNull ViewPool viewPool,
                             @NonNull DivImageLoader imageLoader) {
        super();
        mThemedContext = themedContext;
        mViewPool = viewPool;
        mImageLoader = imageLoader;

        mViewPool.register(FACTORY_TAG_IMAGE, () -> new RatioImageView(mThemedContext), 8);
    }

    @Nullable
    @Override
    protected View build(@NonNull DivView divView, @NonNull DivImageBlock block) {
        RatioImageView imageView = mViewPool.obtain(FACTORY_TAG_IMAGE);
        imageView.setId(R.id.div_image);
        imageView.setRatio(DivViewUtils.getImageRatio(block.image));
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LoadReference loadReference = mImageLoader.loadImage(block.image.imageUrl.toString(), imageView);
        divView.addLoadReference(loadReference, imageView);

        return imageView;
    }
}
