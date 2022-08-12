package com.yandex.div.legacy;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.div.DivBackground;
import com.yandex.div.DivData;
import com.yandex.div.DivGradientBackground;
import com.yandex.div.DivImageBackground;
import com.yandex.div.DivImageElement;
import com.yandex.div.DivSolidBackground;
import com.yandex.div.DivTitleBlock;
import com.yandex.div.DivTrafficBlock;
import com.yandex.div.core.images.DivImageLoader;
import com.yandex.div.core.images.LoadReference;
import com.yandex.div.legacy.view.DivView;
import com.yandex.div.util.DivKitImageUtils;
import com.yandex.images.CachedBitmap;

/**
 * This class contains utils useful for working with Div data.
 */
public class LegacyDivDataUtils {
    public static final int INVALID_STATE_ID = -1;

    private LegacyDivDataUtils() {}

    public static int getInitialStateId(@NonNull DivData data) {
        return data.states.isEmpty() ? INVALID_STATE_ID : data.states.get(0).stateId;
    }

    /**
     * Method to get state by its id without NPE.
     *
     * @param divData div data
     * @param id      state id as it's set in stateId field
     * @return state or null if not found
     */
    @Nullable
    public static DivData.State getStateByIdSafely(@NonNull DivData divData, @IntRange(from = INVALID_STATE_ID) int id) {
        if (id == INVALID_STATE_ID) {
            return null;
        }

        for (DivData.State state : divData.states) {
            if (state.stateId == id) {
                return state;
            }
        }

        Assert.fail("Non existent state id got " + id);
        return null;
    }

    /**
     * Method to check if image element is valid
     *
     * @param element div image element
     * @return true if element is valid
     */
    public static boolean isDivImageValid(@Nullable DivImageElement element) {
        if (element == null) {
            return false;
        }

        String imageUrl = element.imageUrl.toString();
        if (TextUtils.isEmpty(imageUrl)) {
            return false;
        }

        return true;
    }

    /**
     * Method to check if text element is valid.
     *
     * @param text text from div text field
     * @return true if not empty
     */
    public static boolean isDivTextValid(@Nullable CharSequence text) {
        return !TextUtils.isEmpty(text);
    }

    public static boolean isTextOnlyDiv(@Nullable CharSequence text, @Nullable DivImageElement image) {
        return isDivTextValid(text) && !isDivImageValid(image);
    }

    public static boolean isImageOnlyDiv(@Nullable CharSequence text, @Nullable DivImageElement image) {
        return !isDivTextValid(text) && isDivImageValid(image);
    }

    public static boolean isTextAndImageDiv(@Nullable CharSequence text, @Nullable DivImageElement image) {
        return isDivTextValid(text) && isDivImageValid(image);
    }

    public static boolean isValidBlock(@NonNull DivTitleBlock divTitleBlock) {
        return !(TextUtils.isEmpty(divTitleBlock.text) && (divTitleBlock.menuItems == null || divTitleBlock.menuItems.isEmpty()));
    }

    public static boolean isValidBlock(@NonNull DivTrafficBlock divTrafficBlock) {
        for (DivTrafficBlock.Item item : divTrafficBlock.items) {
            if (!TextUtils.isEmpty(item.score)) {
                return true;
            }
        }
        return false;
    }

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
        return new GradientDrawable(orientation, new int[] {startColor, endColor});
    }

    @NonNull
    private static Drawable getImageDrawable(@NonNull String imageUrl,
                                             @NonNull DivImageLoader imageLoader,
                                             @NonNull DivView targetView) {
        final BackgroundImageDrawable backgroundImageDrawable = new BackgroundImageDrawable(targetView.getContext());

        LoadReference loadReference = imageLoader
                .loadImage(imageUrl, DivKitImageUtils.toDivKitCallback(new LegacyDivImageDownloadCallback(targetView) {
                    @UiThread
                    @Override
                    public void onSuccess(@NonNull CachedBitmap cachedBitmap) {
                        backgroundImageDrawable.setOriginalBitmap(cachedBitmap.getBitmap());
                    }
                }));
        targetView.addLoadReference(loadReference, targetView);

        return backgroundImageDrawable;
    }
}
