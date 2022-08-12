package com.yandex.div.legacy.view;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import androidx.annotation.NonNull;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.div.DivNumericSize;
import com.yandex.div.DivSizeUnit;

public class DivViewLegacyUtils {

    public static int divSizeToLayoutParamsSize(@NonNull DivNumericSize numericSize, @NonNull DisplayMetrics metrics) {
        if (DivSizeUnit.DP.equals(numericSize.unit)) {
            return DivViewLegacyUtils.dpToPx(numericSize.value, metrics);
        } else if (DivSizeUnit.SP.equals(numericSize.unit)) {
            return DivViewLegacyUtils.spToPx(numericSize.value, metrics);
        }
        Assert.fail("No unit size defined");
        return -1;
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
}
