package com.yandex.div.legacy.view;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.div.DivFooterBlock;
import com.yandex.div.core.images.DivImageLoader;
import com.yandex.div.legacy.DivTextStyleProvider;
import com.yandex.div.legacy.LegacyDivDataUtils;
import com.yandex.div.legacy.R;
import com.yandex.div.legacy.dagger.DivLegacyScope;
import com.yandex.div.legacy.dagger.LegacyNames;
import com.yandex.div.view.pooling.ViewPool;
import javax.inject.Inject;
import javax.inject.Named;

@DivLegacyScope
public class FooterDivViewBuilder extends DivElementDataViewBuilder<DivFooterBlock> {

    private static final String FACTORY_TAG_FOOTER = "FooterDivViewBuilder.FOOTER";

    @NonNull
    private final Context mThemedContext;
    @NonNull
    private final ViewPool mViewPool;
    @NonNull
    private final DivImageLoader mImageLoader;
    @NonNull
    private final DivTextStyleProvider mTextStyleProvider;

    @Inject
    FooterDivViewBuilder(@NonNull @Named(LegacyNames.THEMED_CONTEXT) Context themedContext,
                         @NonNull ViewPool viewPool,
                         @NonNull DivImageLoader imageLoader,
                         @NonNull DivTextStyleProvider textStyleProvider,
                         @NonNull TextViewFactory textViewFactory) {
        super();
        mThemedContext = themedContext;
        mViewPool = viewPool;
        mImageLoader = imageLoader;
        mTextStyleProvider = textStyleProvider;

        mViewPool.register(FACTORY_TAG_FOOTER, () -> createTextView(textViewFactory,
                                                                    mThemedContext,
                                                                    R.attr.legacyFooterStyle,
                                                                    R.id.div_footer), 8);
    }

    public static boolean isValidBlock(@NonNull DivFooterBlock divData) {
        return LegacyDivDataUtils.isTextOnlyDiv(divData.text, divData.image)
                || LegacyDivDataUtils.isDivImageValid(divData.image);
    }

    @Override
    @Nullable
    protected View build(@NonNull DivView divView, @NonNull DivFooterBlock divData) {
        if (!isValidBlock(divData)) {
            Assert.fail("Unexpected element [" + divData + "]");
            return null;
        }

        final AppCompatTextView itemView = mViewPool.obtain(FACTORY_TAG_FOOTER);

        TextStyle textStyle = mTextStyleProvider.getTextStyle(divData.textStyle);
        textStyle.applyWithEllipsizig(itemView);

        if (LegacyDivDataUtils.isTextOnlyDiv(divData.text, divData.image)) {
            itemView.setText(divData.text);
        } else if (LegacyDivDataUtils.isDivImageValid(divData.image)) {
            bind(divView, mImageLoader, itemView, divData.text, divData.image,
                    R.dimen.div_horizontal_padding, R.dimen.div_horizontal_padding,
                    R.dimen.div_footer_image_size, R.dimen.div_footer_image_size);
        } else {
            Assert.fail("How come? Check that #isValidBlock method code is up to date!");
            return null;
        }

        return itemView;
    }
}
