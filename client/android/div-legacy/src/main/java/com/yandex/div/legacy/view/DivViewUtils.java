package com.yandex.div.legacy.view;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.alicekit.core.utils.Views;
import com.yandex.div.DivAlignment;
import com.yandex.div.DivAlignmentVertical;
import com.yandex.div.DivBackground;
import com.yandex.div.DivGradientBackground;
import com.yandex.div.DivImageBackground;
import com.yandex.div.DivImageElement;
import com.yandex.div.DivNumericSize;
import com.yandex.div.DivPaddingModifier;
import com.yandex.div.DivPosition;
import com.yandex.div.DivSize;
import com.yandex.div.DivSizeUnit;
import com.yandex.div.DivSolidBackground;
import com.yandex.div.core.images.DivImageLoader;
import com.yandex.div.core.images.LoadReference;
import com.yandex.div.legacy.Alignment;
import com.yandex.div.legacy.BackgroundImageDrawable;
import com.yandex.div.legacy.LegacyDivImageDownloadCallback;
import com.yandex.div.legacy.R;
import com.yandex.div.util.DivKitImageUtils;
import com.yandex.div.util.Position;
import com.yandex.images.CachedBitmap;

public class DivViewUtils {

    private static final float DEFAULT_RATIO = 1.0f;

    private DivViewUtils() {
    }

    @SuppressWarnings("squid:CallToDeprecatedMethod")
    @Nullable
    public static Drawable divBackgroundToDrawable(@Nullable DivBackground background,
                                                   @NonNull DivImageLoader imageLoader,
                                                   @NonNull DivView divView) {
        if (background == null) {
            return null;
        }
        DivSolidBackground solidBackground = background.asDivSolidBackground();
        if (solidBackground != null) {
            return getSolidDrawable(solidBackground.color);
        }

        DivGradientBackground gradientBackground = background.asDivGradientBackground();
        if (gradientBackground != null) {
            return getGradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, gradientBackground.startColor,
                                       gradientBackground.endColor);
        }

        DivImageBackground imageBackground = background.asDivImageBackground();
        if (imageBackground != null) {
            return getImageDrawable(imageBackground.imageUrl.toString(), imageLoader, divView);
        }

        return null;
    }

    @NonNull
    private static Drawable getSolidDrawable(@ColorInt int color) {
        return new ColorDrawable(color);
    }

    @NonNull
    private static Drawable getGradientDrawable(GradientDrawable.Orientation orientation,
                                                @ColorInt int startColor, @ColorInt int endColor) {
        return new GradientDrawable(orientation, new int[]{startColor, endColor});
    }

    @NonNull
    private static Drawable getImageDrawable(@NonNull String imageUrl,
                                             @NonNull DivImageLoader imageLoader,
                                             @NonNull DivView targetView) {
        final BackgroundImageDrawable backgroundImageDrawable = new BackgroundImageDrawable(targetView.getContext());

        LoadReference loadReference = imageLoader.loadImage(
                imageUrl,
                DivKitImageUtils.toDivKitCallback(new LegacyDivImageDownloadCallback(targetView) {
                    @UiThread
                    @Override
                    public void onSuccess(@NonNull CachedBitmap cachedBitmap) {
                        backgroundImageDrawable.setOriginalBitmap(cachedBitmap.getBitmap());
                    }
                }));
        targetView.addLoadReference(loadReference, targetView);

        return backgroundImageDrawable;
    }

    @FloatRange(from = 0.f, fromInclusive = false)
    public static float getImageRatio(@Nullable DivImageElement imageElement) {
        if (imageElement == null) {
            return DEFAULT_RATIO;
        }
        return (float) (imageElement.ratio > 0 ? imageElement.ratio : DEFAULT_RATIO);
    }

    public static void applyPadding(@Nullable DivPaddingModifier paddingModifier, @NonNull View view) {
        if (paddingModifier == null) {
            return;
        }

        @DimenRes
        int paddingDimenRes = getPaddingDimenResBySize(paddingModifier.size);
        Position position = divPositionToPosition(paddingModifier.position);

        Views.setPadding(view, paddingDimenRes,
                         position == Position.RIGHT ? Views.VIEW_SIDE_RIGHT : Views.VIEW_SIDE_LEFT);
    }

    @DimenRes
    static int getPaddingDimenResBySize(@NonNull @DivSize String paddingSize) {
        switch (paddingSize) {
            case DivSize.S:
                return R.dimen.div_horizontal_padding_s;
            case DivSize.M:
                return R.dimen.div_horizontal_padding_m;
            case DivSize.L:
                return R.dimen.div_horizontal_padding_l;
            default:
                return R.dimen.div_horizontal_padding;
        }
    }

    @Nullable
    static Uri getUri(@Nullable String uri) {
        return TextUtils.isEmpty(uri) ? null : Uri.parse(uri);
    }

    @NonNull
    public static Position divPositionToPosition(@Nullable @DivPosition String position) {
        if (position == null) {
            return Position.LEFT;
        }

        switch (position) {
            case DivPosition.LEFT:
                return Position.LEFT;
            case DivPosition.RIGHT:
                return Position.RIGHT;
            default:
                Assert.fail("Unknown position: " + position);
                return Position.LEFT;
        }
    }

    @NonNull
    public static Alignment divAlignmentToAlignment(@Nullable @DivAlignment String alignment) {
        if (alignment == null) {
            return Alignment.LEFT;
        }

        switch (alignment) {
            case DivAlignment.LEFT:
                return Alignment.LEFT;
            case DivAlignment.RIGHT:
                return Alignment.RIGHT;
            case DivAlignment.CENTER:
                return Alignment.CENTER;
            default:
                Assert.fail("Unknown alignment: " + alignment);
                return Alignment.LEFT;
        }
    }

    public static int horizontalAlignmentToGravity(@Nullable @DivAlignment String alignment) {
        if (alignment == null) {
            return Gravity.LEFT;
        }

        switch (alignment) {
            case DivAlignment.LEFT:
                return Gravity.LEFT;
            case DivAlignment.RIGHT:
                return Gravity.RIGHT;
            case DivAlignment.CENTER:
                return Gravity.CENTER_HORIZONTAL;
            default:
                Assert.fail("Unknown alignment: " + alignment);
                return Gravity.LEFT;
        }
    }

    public static int verticalAlignmentToGravity(@Nullable @DivAlignmentVertical String alignment) {
        if (alignment == null) {
            return Gravity.CENTER_VERTICAL;
        }

        switch (alignment) {
            case DivAlignmentVertical.TOP:
                return Gravity.TOP;
            case DivAlignmentVertical.BOTTOM:
                return Gravity.BOTTOM;
            case DivAlignmentVertical.CENTER:
                return Gravity.CENTER_VERTICAL;
            default:
                Assert.fail("Unknown vertical alignment: " + alignment);
                return Gravity.CENTER_VERTICAL;
        }
    }

    /**
     * converts dp to pixels
     *
     * @param dp
     * @param metrics
     * @return
     */
    public static int dpToPx(int dp, @NonNull DisplayMetrics metrics) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    /**
     * converts dp to pixels
     *
     * @param dp
     * @param metrics
     * @return
     */
    public static int spToPx(int dp, @NonNull DisplayMetrics metrics) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp, metrics);
    }

    public static int divSizeToLayoutParamsSize(@NonNull DivNumericSize numericSize, @NonNull DisplayMetrics metrics) {
        if (DivSizeUnit.DP.equals(numericSize.unit)) {
            return DivViewUtils.dpToPx(numericSize.value, metrics);
        } else if (DivSizeUnit.SP.equals(numericSize.unit)) {
            return DivViewUtils.spToPx(numericSize.value, metrics);
        }
        Assert.fail("No unit size defined");
        return -1;
    }
}
