/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yandex.div.internal.util;

import android.animation.TypeEvaluator;
import androidx.annotation.NonNull;

import com.yandex.div.core.annotations.InternalApi;
import com.yandex.div.core.internal.type.ArgbInterpolation;

/**
 * Copypaste of com.google.android.material:material ArgbEvaluatorCompat.
 *
 * This evaluator can be used to perform type interpolation between integer values that represent
 * ARGB colors.
 *
 * <p>This compat version matches {@link android.animation.ArgbEvaluator} with critical bug
 * fixes for older versions of Android; the arithmetic lives in
 * {@link ArgbInterpolation#interpolateArgb(float, int, int)} and is shared with the Compose renderer.
 */
@InternalApi
public class ArgbEvaluatorCompat implements TypeEvaluator<Integer> {
    private static final ArgbEvaluatorCompat instance = new ArgbEvaluatorCompat();

    /**
     * Returns an instance of <code>ArgbEvaluatorCompat</code> that may be used in {@link
     * android.animation.ValueAnimator#setEvaluator(TypeEvaluator)}. The same instance may be used in
     * multiple <code>Animator</code>s because it holds no state.
     *
     * @return An instance of <code>ArgbEvalutor</code>.
     */
    @NonNull
    public static ArgbEvaluatorCompat getInstance() {
        return instance;
    }

    /**
     * This function returns the calculated in-between value for a color given integers that represent
     * the start and end values in the four bytes of the 32-bit int. Each channel is separately
     * linearly interpolated and the resulting calculated values are recombined into the return value.
     *
     * @param fraction The fraction from the starting to the ending values
     * @param startValue A 32-bit int value representing colors in the separate bytes of the parameter
     * @param endValue A 32-bit int value representing colors in the separate bytes of the parameter
     * @return A value that is calculated to be the linearly interpolated result, derived by
     *     separating the start and end values into separate color channels and interpolating each one
     *     separately, recombining the resulting values in the same way.
     */
    @NonNull
    @Override
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        return ArgbInterpolation.interpolateArgb(fraction, startValue, endValue);
    }
}
