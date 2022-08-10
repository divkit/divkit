package com.yandex.div.legacy.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.alicekit.core.utils.Views;
import com.yandex.div.DivSeparatorBlock;
import com.yandex.div.DivSize;
import com.yandex.div.legacy.R;
import com.yandex.div.legacy.dagger.DivLegacyScope;
import com.yandex.div.view.SeparatorView;
import javax.inject.Inject;

@DivLegacyScope
public class SeparatorDivViewBuilder extends DivElementDataViewBuilder<DivSeparatorBlock> {

    @Inject
    SeparatorDivViewBuilder() {
        super();
    }

    @Override
    @NonNull
    protected View build(@NonNull DivView divView, @NonNull DivSeparatorBlock divData) {
        SeparatorView separatorView = createView(divView, divData);
        if (divData.hasDelimiter) {
            separatorView.setDividerGravity(Gravity.END);
            separatorView.setDividerColor(divData.delimiterColor);
            separatorView.setDividerHeightResource(R.dimen.div_separator_delimiter_height);
        }

        return separatorView;
    }

    private SeparatorView createView(@NonNull DivView divView, @NonNull DivSeparatorBlock divData) {
        Context context = divView.getContext();
        SeparatorView separatorView = new SeparatorView(context);

        @DivSize String size = divData.size;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, getDimensionOffset(context, getViewHeightRes(size)));
        layoutParams.weight = DivSize.MATCH_PARENT.equals(size) ? divData.weight : 0;
        separatorView.setLayoutParams(layoutParams);

        Views.setPadding(separatorView, R.dimen.div_horizontal_padding, Views.VIEW_SIDE_LEFT);
        Views.setPadding(separatorView, R.dimen.div_horizontal_padding, Views.VIEW_SIDE_RIGHT);

        return separatorView;
    }

    @DimenRes
    private int getViewHeightRes(@NonNull @DivSize String size) {
        switch (size) {
            case DivSize.XXS:
                return R.dimen.div_separator_height_xxs;
            case DivSize.XS:
                return R.dimen.div_separator_height_xs;
            case DivSize.S:
                return R.dimen.div_separator_height_s;
            case DivSize.M:
                return R.dimen.div_separator_height_m;
            case DivSize.L:
                return R.dimen.div_separator_height_l;
            case DivSize.XL:
                return R.dimen.div_separator_height_xl;
            case DivSize.MATCH_PARENT:
                return R.dimen.div_padding_zero;
            default:
                Assert.fail("Unsupported size " + size);
                return R.dimen.div_separator_height_m;
        }
    }
}
