package com.yandex.div.legacy.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.div.DivButtonsBlock;
import com.yandex.div.DivTextStyle;
import com.yandex.div.core.images.DivImageLoader;
import com.yandex.div.core.images.LoadReference;
import com.yandex.div.legacy.Alignment;
import com.yandex.div.legacy.DivTextStyleProvider;
import com.yandex.div.legacy.LegacyDivDataUtils;
import com.yandex.div.legacy.R;
import com.yandex.div.legacy.dagger.DivLegacyScope;
import com.yandex.div.legacy.dagger.LegacyNames;
import com.yandex.div.view.pooling.ViewPool;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

@DivLegacyScope
public class ButtonsDivBlockViewBuilder extends DivElementDataViewBuilder<DivButtonsBlock> {

    private static final String FACTORY_TAG_TEXT_BUTTON = "ButtonsDivBlockViewBuilder.TEXT_BUTTON";
    private static final String FACTORY_TAG_IMAGE_BUTTON = "ButtonsDivBlockViewBuilder.IMAGE_BUTTON";
    private static final String FACTORY_TAG_BUTTON_WRAPPER = "ButtonsDivBlockViewBuilder.BUTTON_WRAPPER";

    @NonNull
    private final Context mThemedContext;
    @NonNull
    private final ViewPool mViewPool;
    @NonNull
    private final DivImageLoader mImageLoader;
    @NonNull
    private final DivTextStyleProvider mTextStyleProvider;

    @Inject
    ButtonsDivBlockViewBuilder(@NonNull @Named(LegacyNames.THEMED_CONTEXT) Context themedContext,
                               @NonNull ViewPool viewPool,
                               @NonNull DivImageLoader imageLoader,
                               @NonNull DivTextStyleProvider textStyleProvider) {
        super();
        mThemedContext = themedContext;
        mViewPool = viewPool;
        mImageLoader = imageLoader;
        mTextStyleProvider = textStyleProvider;

        mViewPool.register(FACTORY_TAG_TEXT_BUTTON, this::createTextButton, 8);
        mViewPool.register(FACTORY_TAG_IMAGE_BUTTON, this::createImageButton, 8);
        mViewPool.register(FACTORY_TAG_BUTTON_WRAPPER, this::createButtonWrapper, 8);
    }

    public static boolean isValidBlock(@NonNull DivButtonsBlock divButtonsBlock) {
        return !getValidButtons(divButtonsBlock).isEmpty();
    }

    @Override
    @Nullable
    protected View build(@NonNull DivView divView, @NonNull DivButtonsBlock divData) {
        List<DivButtonsBlock.Item> buttonsList = getValidButtons(divData);

        if (buttonsList.isEmpty()) {
            return null;
        }

        final Alignment alignment = DivViewUtils.divAlignmentToAlignment(divData.alignment);

        if (buttonsList.size() == 1) {
            return buildSingleButton(divView, divData, alignment);
        }
        return buildMultipleButtons(divView, buttonsList, alignment);
    }

    @NonNull
    private static List<DivButtonsBlock.Item> getValidButtons(@NonNull DivButtonsBlock divData) {
        if (divData.items.isEmpty()) {
            return Collections.emptyList();
        }

        List<DivButtonsBlock.Item> buttonsList = new ArrayList<>();
        for (DivButtonsBlock.Item item : divData.items) {
            if (LegacyDivDataUtils.isDivImageValid(item.image) || LegacyDivDataUtils.isDivTextValid(item.text)) {
                buttonsList.add(item);
            }
        }

        return buttonsList;
    }

    private static boolean isImageButton(@NonNull DivButtonsBlock.Item divButtonItem) {
        return LegacyDivDataUtils.isImageOnlyDiv(divButtonItem.text, divButtonItem.image);
    }

    @NonNull
    private View buildSingleButton(@NonNull DivView divView, @NonNull DivButtonsBlock divData, @NonNull Alignment alignment) {
        DivButtonsBlock.Item buttonElementData = divData.items.get(0);

        View view = isImageButton(buttonElementData) ? mViewPool.obtain(FACTORY_TAG_IMAGE_BUTTON) : mViewPool.obtain(FACTORY_TAG_TEXT_BUTTON);
        if (divData.isFullwidth) {
            FrameLayout wrapper = mViewPool.obtain(FACTORY_TAG_BUTTON_WRAPPER);

            bindButtonContent(divView, view, buttonElementData);
            bindButtonAppearance(divView, wrapper, buttonElementData);
            bindFullsizeButton(view);

            wrapper.addView(view);
            view = wrapper;
        } else {
            bindButtonContent(divView, view, buttonElementData);
            bindButtonAppearance(divView, view, buttonElementData);
            bindSingleButtonAlignment(view, alignment);
        }

        Resources resources = view.getResources();
        int horizontalPadding = resources.getDimensionPixelOffset(R.dimen.div_horizontal_padding);
        int verticalPadding = resources.getDimensionPixelOffset(R.dimen.div_button_text_vertical_padding);

        FrameLayout rootLayout = new FrameLayout(view.getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = horizontalPadding;
        layoutParams.rightMargin = horizontalPadding;
        layoutParams.topMargin = verticalPadding;
        layoutParams.bottomMargin = verticalPadding;

        rootLayout.setLayoutParams(layoutParams);
        rootLayout.addView(view);
        divView.setActionHandlerForView(rootLayout, buttonElementData.action);
        return rootLayout;
    }

    @NonNull
    private TextView createTextButton() {
        TextView textButton = new TextView(mThemedContext, null, R.attr.legacyButtonTextStyle);
        int height = mThemedContext.getResources().getDimensionPixelSize(R.dimen.div_button_height);
        textButton.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height));
        return textButton;
    }

    @NonNull
    private ImageView createImageButton() {
        ImageView imageButton = new ImageView(mThemedContext, null, R.attr.legacyButtonImageStyle);
        int size = mThemedContext.getResources().getDimensionPixelSize(R.dimen.div_button_height);
        imageButton.setLayoutParams(new FrameLayout.LayoutParams(size, size));
        return imageButton;
    }

    @NonNull
    private FrameLayout createButtonWrapper() {
        FrameLayout wrapper = new FrameLayout(mThemedContext);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        wrapper.setLayoutParams(layoutParams);
        return wrapper;
    }

    @Nullable
    private Drawable createBackground(@NonNull DivButtonsBlock.Item data) {
        final Drawable background = ContextCompat.getDrawable(mThemedContext, R.drawable.button_background);
        if (background == null) {
            return null;
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            DrawableCompat.setTint(background, data.backgroundColor);
        } else {
            background.mutate().setColorFilter(data.backgroundColor, PorterDuff.Mode.SRC_IN);
        }
        return background;
    }

    private void bindButtonContent(@NonNull DivView divView, @NonNull View view, @NonNull DivButtonsBlock.Item data) {
        if (isImageButton(data)) {
            bindImageContent(divView, (ImageView) view, data);
        } else {
            bindTextContent(divView, (TextView) view, data);
        }
    }

    private void bindImageContent(@NonNull DivView divView, @NonNull ImageView imageView, @NonNull DivButtonsBlock.Item data) {
        //noinspection ConstantConditions
        LoadReference loadReference = mImageLoader.loadImage(data.image.imageUrl.toString(), imageView);
        divView.addLoadReference(loadReference, imageView);
    }

    private void bindTextContent(@NonNull DivView divView, @NonNull TextView textView, @NonNull DivButtonsBlock.Item data) {
        TextStyle buttonStyle = mTextStyleProvider.getTextStyle(DivTextStyle.TEXT_M);
        buttonStyle.apply(textView);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);   // override alignment from text style

        if (LegacyDivDataUtils.isTextOnlyDiv(data.text, data.image)) {
            textView.setText(data.text);
        } else if (LegacyDivDataUtils.isTextAndImageDiv(data.text, data.image)) {
            //noinspection ConstantConditions
            bind(
                    divView,
                    mImageLoader,
                    textView,
                    data.text,
                    data.image,
                    R.dimen.div_button_text_horizontal_image_padding,
                    R.dimen.div_button_text_horizontal_padding,
                    R.dimen.div_button_image_size,
                    R.dimen.div_button_image_size
            );
        }
    }

    private void bindButtonAppearance(@NonNull DivView divView, @NonNull View view, @NonNull DivButtonsBlock.Item data) {
        view.setBackground(createBackground(data));
        divView.setActionHandlerForView(view, data.action);
    }

    private void bindSingleButtonAlignment(@NonNull View view, @NonNull Alignment alignment) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();

        switch (alignment) {
            case LEFT:
                layoutParams.gravity = Gravity.START;
                break;
            case CENTER:
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                break;
            case RIGHT:
                layoutParams.gravity = Gravity.END;
                break;
            default:
                Assert.fail("Unknown value");
                break;
        }
    }

    private void bindFullsizeButton(@NonNull View view) {
        view.setBackground(null);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
    }

    @NonNull
    private View buildMultipleButtons(@NonNull DivView divView, @NonNull List<DivButtonsBlock.Item> buttonItems, @NonNull Alignment alignment) {
        Context context = divView.getContext();

        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setId(R.id.div_buttons);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ButtonListItemDecorator(context.getResources()));

        ButtonsAdapter buttonsAdapter = new ButtonsAdapter(divView, buttonItems);
        recyclerView.setAdapter(buttonsAdapter);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        switch (alignment) {
            case LEFT:
                layoutParams.gravity = Gravity.START;
                break;
            case CENTER:
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                break;
            case RIGHT:
                layoutParams.gravity = Gravity.END;
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                break;
            default:
                Assert.fail("Unknown value");
                break;
        }
        layoutParams.gravity = layoutParams.gravity | Gravity.CENTER_VERTICAL;
        recyclerView.setLayoutParams(layoutParams);
        return recyclerView;
    }

    private class ButtonsAdapter extends RecyclerView.Adapter<ButtonViewHolder> {

        private static final int TEXT_TYPE = 0;
        private static final int IMAGE_TYPE = 1;

        @NonNull
        private final DivView mDivView;
        @NonNull
        private final List<DivButtonsBlock.Item> mButtons;

        ButtonsAdapter(@NonNull DivView divView,
                       @NonNull List<DivButtonsBlock.Item> buttons) {
            mDivView = divView;
            mButtons = buttons;
        }

        @NonNull
        @Override
        public ButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = viewType == TEXT_TYPE ? mViewPool.obtain(FACTORY_TAG_TEXT_BUTTON) : mViewPool.obtain(FACTORY_TAG_IMAGE_BUTTON);
            return new ButtonViewHolder(mDivView, view);
        }

        @Override
        public void onBindViewHolder(@NonNull ButtonViewHolder holder, int position) {
            holder.bind(mButtons.get(position));
        }

        @Override
        public int getItemViewType(int position) {
            return isImageButton(mButtons.get(position)) ? IMAGE_TYPE : TEXT_TYPE;
        }

        @Override
        public int getItemCount() {
            return mButtons.size();
        }
    }

    private class ButtonViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final DivView mDivView;

        ButtonViewHolder(@NonNull DivView divView, @NonNull View itemView) {
            super(itemView);
            mDivView = divView;
        }

        public void bind(@NonNull DivButtonsBlock.Item data) {
            bindButtonContent(mDivView, itemView, data);
            bindButtonAppearance(mDivView, itemView, data);
        }
    }

    private static class ButtonListItemDecorator extends RecyclerView.ItemDecoration {

        @Px
        private final int mHorizontalItemPadding;
        @Px
        private final int mVerticalItemPadding;

        ButtonListItemDecorator(@NonNull Resources resources) {
            mHorizontalItemPadding = resources.getDimensionPixelSize(R.dimen.div_horizontal_padding);
            mVerticalItemPadding = resources.getDimensionPixelSize(R.dimen.div_button_text_vertical_padding);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            final int position = parent.getLayoutManager().getPosition(view);
            outRect.set(position == 0 ? mHorizontalItemPadding : 0, mVerticalItemPadding,
                        mHorizontalItemPadding, mVerticalItemPadding);
        }
    }
}
