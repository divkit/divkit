package com.yandex.div.legacy.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.DimenRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.AppCompatTextView;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.div.DivImageElement;
import com.yandex.div.DivSize;
import com.yandex.div.DivUniversalBlock;
import com.yandex.div.core.images.DivImageLoader;
import com.yandex.div.core.images.LoadReference;
import com.yandex.div.legacy.DivTextStyleProvider;
import com.yandex.div.legacy.LegacyDivDataUtils;
import com.yandex.div.legacy.LegacyDivImageDownloadCallback;
import com.yandex.div.legacy.R;
import com.yandex.div.legacy.dagger.DivLegacyScope;
import com.yandex.div.legacy.dagger.LegacyNames;
import com.yandex.div.util.DivKitImageUtils;
import com.yandex.div.util.Position;
import com.yandex.div.view.pooling.ViewPool;
import com.yandex.images.CachedBitmap;
import javax.inject.Inject;
import javax.inject.Named;

@DivLegacyScope
public class UniversalDivViewBuilder extends DivElementDataViewBuilder<DivUniversalBlock> {

    private static final int NO_CORNERS = -1;
    private static final int CORNERS_FOR_S = 10;
    private static final int CORNERS_FOR_M_L = 15;

    private static final String FACTORY_TAG_TITLE_AND_TEXT = "UniversalDivViewBuilder.TITLE_AND_TEXT";
    private static final String FACTORY_TAG_TITLE = "UniversalDivViewBuilder.TITLE";
    private static final String FACTORY_TAG_TEXT = "UniversalDivViewBuilder.TEXT";
    private static final String FACTORY_TAG_IMAGE = "UniversalDivViewBuilder.IMAGE";

    @NonNull
    private final Context mContext;
    @NonNull
    private final Context mThemedContext;
    @NonNull
    private final ViewPool mViewPool;
    @NonNull
    private final DivImageLoader mImageLoader;
    @NonNull
    private final DivTextStyleProvider mTextStyleProvider;

    @Inject
    UniversalDivViewBuilder(@NonNull @Named(LegacyNames.CONTEXT) Context context,
                            @NonNull @Named(LegacyNames.THEMED_CONTEXT) Context themedContext,
                            @NonNull ViewPool viewPool,
                            @NonNull DivImageLoader imageLoader,
                            @NonNull DivTextStyleProvider textStyleProvider,
                            @NonNull TextViewFactory textViewFactory) {
        super();

        mContext = context;
        mThemedContext = themedContext;
        mViewPool = viewPool;
        mImageLoader = imageLoader;
        mTextStyleProvider = textStyleProvider;

        mViewPool.register(FACTORY_TAG_TITLE_AND_TEXT, () -> new LinearLayout(mThemedContext), 2);
        mViewPool.register(FACTORY_TAG_TITLE, () -> createTextView(textViewFactory, mThemedContext,
                                                                   R.attr.legacyUniversalTitleStyle,
                                                                   R.id.div_universal_title), 10);
        mViewPool.register(FACTORY_TAG_TEXT, () -> createTextView(textViewFactory, mThemedContext,
                                                                  R.attr.legacyUniversalTextStyle,
                                                                  R.id.div_universal_text), 10);
        mViewPool.register(FACTORY_TAG_IMAGE, () -> new RatioImageView(mThemedContext, null,
                                                                       R.attr.legacyUniversalImageStyle), 4);
    }

    @Override
    @Nullable
    protected View build(@NonNull DivView divView, @NonNull DivUniversalBlock divData) {
        if (!isValidBlock(divData)) {
            return null;
        }

        TextStyle titleStyle = mTextStyleProvider.getTextStyle(divData.titleStyle, divData.titleMaxLines);
        TextStyle textStyle = mTextStyleProvider.getTextStyle(divData.textStyle, divData.textMaxLines);

        DivUniversalBlock.SideElement sideElement = divData.sideElement;
        if (sideElement == null) {
            return buildTextOnlyDiv(divData.title, titleStyle, divData.text, textStyle);
        }

        @DivSize String sideElementSize = sideElement.size;
        Position sideElementPosition = DivViewUtils.divPositionToPosition(sideElement.position);

        DivImageElement imageElement = sideElement.element.asDivImageElement();
        if (imageElement != null) {
            return buildDivWithImage(divView, imageElement, sideElementSize, sideElementPosition,
                                     divData.title, titleStyle, divData.text, textStyle);
        }

        DivUniversalBlock.SideElement.DateElement dateElement = sideElement.element.asDateElement();

        if (dateElement != null) {
            return buildDivWithDate(dateElement, sideElementSize, sideElementPosition,
                                    divData.title, titleStyle, divData.text, textStyle);
        }

        Assert.fail("Invalid universal div with side : " + sideElement.element.type);

        return buildTextOnlyDiv(divData.title, titleStyle, divData.text, textStyle);
    }

    /**
     * Method to check if we can show something from this div.
     *
     * @param divData div universal block
     */
    public static boolean isValidBlock(@NonNull DivUniversalBlock divData) {
        if (LegacyDivDataUtils.isDivTextValid(divData.text) || LegacyDivDataUtils.isDivTextValid(divData.title)) {
            return true;
        }
        if (divData.sideElement != null) {
            DivImageElement imageElement = divData.sideElement.element.asDivImageElement();

            if (imageElement != null && LegacyDivDataUtils.isDivImageValid(imageElement)) {
                return true;
            }

            DivUniversalBlock.SideElement.DateElement dateElement = divData.sideElement.element.asDateElement();
            if (dateElement != null && LegacyDivDataUtils.isDivTextValid(dateElement.dateDay)) {
                return true;
            }
        }

        return false;
    }

    @NonNull
    private View buildTextOnlyDiv(@Nullable CharSequence title, @NonNull TextStyle titleTextStyle,
                                  @Nullable CharSequence text, @NonNull TextStyle textTextStyle) {
        int defaultPaddingBottom = getDimensionOffset(mContext, R.dimen.div_universal_padding_bottom);
        int defaultPaddingHorizontal = getDimensionOffset(mContext, R.dimen.div_horizontal_padding);

        if (title != null && text != null) {
            View view = createTitleAndTextView(title, titleTextStyle, text, textTextStyle);
            view.setPadding(defaultPaddingHorizontal, getDimensionOffset(mContext, R.dimen.div_universal_title_and_text_padding_top),
                            defaultPaddingHorizontal, defaultPaddingBottom);
            return view;
        }

        // title only
        if (title != null) {
            View view = createTitleView(title, titleTextStyle);
            int paddingVertical = getDimensionOffset(mContext, R.dimen.div_universal_title_padding_vertical);
            view.setPadding(defaultPaddingHorizontal, paddingVertical, defaultPaddingHorizontal, paddingVertical);
            return view;
        }

        // text only
        View view = createTextView(text, textTextStyle);
        view.setPadding(defaultPaddingHorizontal, getDimensionOffset(mContext, R.dimen.div_padding_zero),
                        defaultPaddingHorizontal, defaultPaddingBottom);
        return view;
    }

    @NonNull
    private View createTitleAndTextView(@NonNull CharSequence title, @NonNull TextStyle titleTextStyle,
                                        @NonNull CharSequence text, @NonNull TextStyle textTextStyle) {
        LinearLayout linearLayout = mViewPool.obtain(FACTORY_TAG_TITLE_AND_TEXT);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.addView(createTitleView(title, titleTextStyle));
        linearLayout.addView(createTextView(text, textTextStyle));

        return linearLayout;
    }

    @NonNull
    private TextView createTextView(@Nullable CharSequence text, @NonNull TextStyle textTextStyle) {
        AppCompatTextView textView = mViewPool.obtain(FACTORY_TAG_TEXT);
        setTextAndStyle(textView, text, textTextStyle);
        return textView;
    }

    @NonNull
    private TextView createTitleView(@Nullable CharSequence title, @NonNull TextStyle titleTextStyle) {
        AppCompatTextView textView = mViewPool.obtain(FACTORY_TAG_TITLE);
        setTextAndStyle(textView, title, titleTextStyle);
        return textView;
    }

    @NonNull
    private View buildDivWithImage(@NonNull DivView divView,
                                   @NonNull DivImageElement image,
                                   @NonNull @DivSize String sideElementSize,
                                   @NonNull Position sideElementPosition,
                                   @Nullable CharSequence title,
                                   @NonNull TextStyle titleTextStyle,
                                   @Nullable CharSequence text,
                                   @NonNull TextStyle textTextStyle) {
        RelativeLayout relativeLayout = createLayoutForDivWithSideElement();
        relativeLayout.addView(createImageView(divView, image, sideElementSize, sideElementPosition));
        addTitleAndTextToViewWithSideElement(relativeLayout, sideElementPosition, R.id.div_universal_image,
                                             title, titleTextStyle, text, textTextStyle);

        return relativeLayout;
    }

    private void addTitleAndTextToViewWithSideElement(@NonNull RelativeLayout relativeLayout, @NonNull Position sideElementPosition,
                                                      @IdRes int sideViewId,
                                                      @Nullable CharSequence title, @NonNull TextStyle titleTextStyle,
                                                      @Nullable CharSequence text, @NonNull TextStyle textTextStyle) {
        View titleView = createTitleView(title, titleTextStyle);
        RelativeLayout.LayoutParams titleLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                                        ViewGroup.LayoutParams.WRAP_CONTENT);

        View textView = createTextView(text, textTextStyle);
        RelativeLayout.LayoutParams textLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                                       ViewGroup.LayoutParams.WRAP_CONTENT);

        int textPosition = sideElementPosition == Position.RIGHT ? RelativeLayout.LEFT_OF : RelativeLayout.RIGHT_OF;

        titleLayoutParams.addRule(textPosition, sideViewId);
        textLayoutParams.addRule(textPosition, sideViewId);
        textLayoutParams.addRule(RelativeLayout.BELOW, R.id.div_universal_title);

        relativeLayout.addView(titleView, titleLayoutParams);
        relativeLayout.addView(textView, textLayoutParams);

    }

    @NonNull
    private RelativeLayout createLayoutForDivWithSideElement() {
        int defaultPaddingTop = getDimensionOffset(mContext, R.dimen.div_universal_padding_top);
        int defaultPaddingBottom = getDimensionOffset(mContext, R.dimen.div_universal_padding_bottom);
        int defaultPaddingHorizontal = getDimensionOffset(mContext, R.dimen.div_horizontal_padding);

        RelativeLayout relativeLayout = new RelativeLayout(mContext);
        relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        relativeLayout.setPadding(defaultPaddingHorizontal, defaultPaddingTop, defaultPaddingHorizontal, defaultPaddingBottom);
        return relativeLayout;
    }

    @NonNull
    private RatioImageView createImageView(@NonNull DivView divView,
                                           @NonNull DivImageElement image,
                                           @NonNull @DivSize String sideElementSize,
                                           @NonNull Position sideElementPosition) {
        RatioImageView imageView = mViewPool.obtain(FACTORY_TAG_IMAGE);
        imageView.setId(R.id.div_universal_image);

        @Px int imageWidth = getSideElementPixelSize(sideElementSize);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setMaxWidth(imageWidth);
        imageView.setRatio(DivViewUtils.getImageRatio(image));

        LoadReference loadReference = mImageLoader.loadImage(
                image.imageUrl.toString(),
                DivKitImageUtils.toDivKitCallback(new LegacyDivImageDownloadCallback(divView) {
                    @UiThread
                    @Override
                    public void onSuccess(@NonNull CachedBitmap cachedBitmap) {
                        Bitmap bitmap = cachedBitmap.getBitmap();
                        int cornerPixelSize = getCornerPixelSize(sideElementSize, bitmap.getWidth());
                        if (cornerPixelSize == NO_CORNERS) {
                            imageView.setImageBitmap(bitmap);
                        } else {
                            imageView.setRoundedImage(bitmap, cornerPixelSize);
                        }
                    }
                }));
        divView.addLoadReference(loadReference, imageView);

        alignSideElementView(layoutParams, sideElementPosition);
        imageView.setLayoutParams(layoutParams);
        return imageView;
    }

    private void alignSideElementView(@NonNull RelativeLayout.LayoutParams layoutParams,
                                      @NonNull Position sideElementPosition) {
        int sideMargin = getDimensionOffset(mContext, R.dimen.div_universal_image_horizontal_margin);
        if (sideElementPosition == Position.RIGHT) {
            layoutParams.leftMargin = sideMargin;
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        } else {
            layoutParams.rightMargin = sideMargin;
        }
    }

    @NonNull
    private View buildDivWithDate(@NonNull DivUniversalBlock.SideElement.DateElement dateElement,
                                  @NonNull @DivSize String sideElementSize,
                                  @NonNull Position sideElementPosition,
                                  @Nullable CharSequence title,
                                  @NonNull TextStyle titleTextStyle,
                                  @Nullable CharSequence text,
                                  @NonNull TextStyle textTextStyle) {
        RelativeLayout relativeLayout = createLayoutForDivWithSideElement();
        addDateViews(relativeLayout, dateElement, sideElementSize, sideElementPosition);
        addTitleAndTextToViewWithSideElement(relativeLayout, sideElementPosition, R.id.div_universal_date_day,
                                             title, titleTextStyle, text, textTextStyle);
        return relativeLayout;
    }

    private void addDateViews(@NonNull RelativeLayout relativeLayout,
                              @NonNull DivUniversalBlock.SideElement.DateElement dateElement,
                              @NonNull @DivSize String sideElementSize,
                              @NonNull Position sideElementPosition) {
        @Px int dateWidth = getSideElementPixelSize(sideElementSize);
        RelativeLayout.LayoutParams dayLayoutParams = new RelativeLayout.LayoutParams(dateWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        alignSideElementView(dayLayoutParams, sideElementPosition);

        TextView dayView = createDayView(dateElement.dateDay, sideElementSize);
        relativeLayout.addView(dayView, dayLayoutParams);

        if (dateElement.dateMonth != null) {
            TextView monthView = createMonthView(dateElement.dateMonth);
            RelativeLayout.LayoutParams monthLayoutParams = new RelativeLayout.LayoutParams(dateWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            alignSideElementView(monthLayoutParams, sideElementPosition);
            monthLayoutParams.addRule(RelativeLayout.BELOW, R.id.div_universal_date_day);

            relativeLayout.addView(monthView, monthLayoutParams);
        }
    }

    @NonNull
    private TextView createDayView(@NonNull CharSequence dayText, @NonNull @DivSize String sideElementSize) {
        TextView dayView = new TextView(mThemedContext, null, R.attr.legacyUniversalDayStyle);
        dayView.setId(R.id.div_universal_date_day);
        dayView.setText(dayText);

        @DimenRes int dayTextSizeId = DivSize.S.equals(sideElementSize)
                ? R.dimen.div_universal_day_text_size_s
                : R.dimen.div_universal_day_text_size;
        dayView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimensionSize(mContext, dayTextSizeId));

        return dayView;
    }

    @NonNull
    private TextView createMonthView(@NonNull CharSequence monthText) {
        TextView monthView = new TextView(mThemedContext, null, R.attr.legacyUniversalMonthStyle);
        monthView.setId(R.id.div_universal_date_month);
        monthView.setText(monthText);
        return monthView;
    }

    @Px
    private int getSideElementPixelSize(@NonNull @DivSize String size) {
        @DimenRes int id;
        switch (size) {
            case DivSize.S:
                id = R.dimen.div_universal_image_size_s;
                break;
            case DivSize.M:
                id = R.dimen.div_universal_image_size_m;
                break;
            case DivSize.L:
                id = R.dimen.div_universal_image_size_l;
                break;
            default:
                Assert.fail("Unknown size");
                id = R.dimen.div_universal_image_size_s;
                break;
        }
        return getDimensionSize(mContext, id);
    }

    @Px
    private int getCornerPixelSize(@NonNull @DivSize String sideElementSize, @Px int bitmapWidth) {
        switch (sideElementSize) {
            case DivSize.S:
                return bitmapWidth / CORNERS_FOR_S;
            case DivSize.M:
            case DivSize.L:
                return bitmapWidth / CORNERS_FOR_M_L;
            default:
                return NO_CORNERS;
        }
    }
}
