package com.yandex.div.util;

import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

/**
 * Contains utility methods for making touch animations.
 */
@SuppressLint("ClickableViewAccessibility")
public final class AnimationUtils {

    public static final TimeInterpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();

    private static final int ANIMATION_DURATION_IN_MILLISECONDS = 100;
    private static final float ALPHA_PRESSED_RATE = 0.6f;

    private AnimationUtils() {
    }

    public static void attachTouchAnimation(@NonNull View view) {
        view.setOnTouchListener(createTouchAnimationListener(view));
    }

    public static void detachTouchAnimation(@NonNull View view) {
        view.setOnTouchListener(null);
    }

    public static void attachTouchAnimation(@NonNull View view, @FloatRange(from = 0f, to = 1f) float initialAlpha) {
        view.setOnTouchListener(createTouchAnimationListener(view, initialAlpha));
    }

    @NonNull
    public static OnTouchListener createTouchAnimationListener(@NonNull View view) {
        return createTouchAnimationListener(view, view.getAlpha());
    }

    @NonNull
    private static OnTouchListener createTouchAnimationListener(@NonNull View view, @FloatRange(from = 0f, to = 1f) float initialAlpha) {
        // We don't want to use lambda here because the frequency of this method is very high and lambda creation is more expensive than anonymous
        // class. As the result lambda creation could affect scroll fluency.
        //noinspection Convert2Lambda
        return new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!v.isEnabled() || !v.isClickable() || !v.hasOnClickListeners()) {
                    return false;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        animateAlpha(view, initialAlpha * ALPHA_PRESSED_RATE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        animateAlpha(view, initialAlpha);
                        break;
                    default:
                        break;
                }
                return false;
            }
        };
    }

    private static void animateAlpha(@NonNull final View view, final float value) {
        view.animate()
                .setDuration(ANIMATION_DURATION_IN_MILLISECONDS)
                .alpha(value)
                .start();
    }
}
