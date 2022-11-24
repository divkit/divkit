package com.yandex.div.internal.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.DimenRes;
import androidx.annotation.IdRes;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.yandex.div.core.util.Assert;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayDeque;

public class Views {

    public static final int VIEW_SIDE_BOTTOM = 1;
    public static final int VIEW_SIDE_LEFT = 1 << 1;
    public static final int VIEW_SIDE_RIGHT = 1 << 2;
    public static final int VIEW_SIDE_TOP = 1 << 3;

    private static final int[] HIT_TEST_ARRAY = new int[2];

    @NonNull
    public static <T extends View> T findViewAndCast(@NonNull Activity activity, @IdRes int viewId) {
        T viewById = activity.findViewById(viewId);
        if (viewById == null) {
            throw new IllegalStateException(
                    "View with id [" + activity.getResources().getResourceName(viewId) + "] doesn't exist");
        }
        return viewById;
    }

    @NonNull
    public static <T extends View> T findViewAndCast(@NonNull View parent, @IdRes int viewId) {
        T viewById = parent.findViewById(viewId);
        if (viewById == null) {
            throw new IllegalStateException(
                    "View with id [" + parent.getResources().getResourceName(viewId) + "] doesn't exist");
        }
        return viewById;
    }

    @Nullable
    public static <T extends View> T findOptionalViewAndCast(@NonNull View parent, @IdRes int viewId) {
        //noinspection unchecked
        return (T) parent.findViewById(viewId);
    }

    @NonNull
    public static <VIEW extends View> VIEW inflate(@NonNull ViewGroup viewGroup, @LayoutRes int layout) {
        //noinspection unchecked
        return (VIEW) LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
    }

    @NonNull
    public static <VIEW extends View> VIEW inflate(@NonNull Context context, @NonNull ViewGroup viewGroup, @LayoutRes int layout) {
        //noinspection unchecked
        return (VIEW) LayoutInflater.from(context).inflate(layout, viewGroup, false);
    }

    @NonNull
    public static <V extends View> V inflate(@NonNull Context context, @LayoutRes int layout) {
        //noinspection unchecked
        return (V) LayoutInflater.from(context).inflate(layout, null);
    }

    public static void setTextOrHide(@NonNull final TextView view,
                                     @Nullable final CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            view.setText(null);
            view.setVisibility(View.GONE);
        } else {
            view.setText(text);
            view.setVisibility(View.VISIBLE);
        }
    }

    public static boolean isVisible(@NonNull View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    /**
     * Checks, whether a point is inside the view
     *
     * @param view view to check
     * @param rawX point X coordinate (should screen-based)
     * @param rawY point Y coordinate (should screen-based)
     * @return true if the point is inside the view
     */
    @MainThread
    public static boolean hitTest(@NonNull View view, float rawX, float rawY) {
        view.getLocationInWindow(HIT_TEST_ARRAY);
        return rawX >= HIT_TEST_ARRAY[0] && rawX <= HIT_TEST_ARRAY[0] + view.getWidth() &&
                rawY >= HIT_TEST_ARRAY[1] && rawY <= HIT_TEST_ARRAY[1] + view.getHeight();
    }

    public static void setWidth(@NonNull View view,
                                @IntRange(from = ViewGroup.LayoutParams.WRAP_CONTENT) int width) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            params.width = width;
            view.setLayoutParams(params);
        }
    }

    public static void setHeight(@NonNull View view,
                                 @IntRange(from = ViewGroup.LayoutParams.WRAP_CONTENT) int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            params.height = height;
            view.setLayoutParams(params);
        }
    }

    public static void setPadding(@NonNull View view, @DimenRes int res, @ViewSideFlags int viewSideFlags) {
        int pixelSize = view.getResources().getDimensionPixelSize(res);
        setPaddingPixelSize(view, pixelSize, viewSideFlags);
    }

    public static void setPadding(@NonNull View view, @Px int px, @ViewSideFlags int viewSideFlags,
                                  @SuppressWarnings("UnusedParameters") Void unused) {
        setPaddingPixelSize(view, px, viewSideFlags);
    }

    public static void traverseViewTree(@NonNull final View view, @NonNull final ViewProcessor viewProcessor) {
        final ArrayDeque<ViewGroup> viewGroups = new ArrayDeque<>();
        viewProcessor.process(view);

        if (view instanceof ViewGroup) {
            viewGroups.add((ViewGroup) view);
        }

        while (!viewGroups.isEmpty()) {
            final ViewGroup viewGroup = viewGroups.remove();
            final int childCount = viewGroup.getChildCount();

            for (int index = 0; index < childCount; index++) {
                final View child = viewGroup.getChildAt(index);
                viewProcessor.process(child);

                if (child instanceof ViewGroup) {
                    viewGroups.add((ViewGroup) child);
                }
            }
        }
    }

    private static void setPaddingPixelSize(
            @NonNull View view, int paddingPixelSize, @ViewSideFlags int viewSideFlags) {
        view.setPadding(
                (viewSideFlags & VIEW_SIDE_LEFT) != 0 ? paddingPixelSize : view.getPaddingLeft(),
                (viewSideFlags & VIEW_SIDE_TOP) != 0 ? paddingPixelSize : view.getPaddingTop(),
                (viewSideFlags & VIEW_SIDE_RIGHT) != 0 ? paddingPixelSize : view.getPaddingRight(),
                (viewSideFlags & VIEW_SIDE_BOTTOM) != 0 ? paddingPixelSize : view.getPaddingBottom()
        );
    }

    public static int getBottomMargin(@NonNull View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        if (!(layoutParams instanceof ViewGroup.MarginLayoutParams)) {
            return 0;
        }
        return ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
    }

    public static int getVerticalMargins(@NonNull View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        if (!(layoutParams instanceof ViewGroup.MarginLayoutParams)) {
            return 0;
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
        return marginLayoutParams.bottomMargin + marginLayoutParams.topMargin;
    }

    /**
     * @return height including vertical margins if present
     */
    public static int getHeightWithMargins(@NonNull View view) {
        return view.getHeight() + getVerticalMargins(view);
    }

    /**
     * Setup alert dialog message font
     *
     * @param dialog   fully inflated dialog (at least one AlertDialog#show() was called)
     * @param typeface font, no op on null-values
     * @return styled dialog
     */
    @NonNull
    public static AlertDialog styleAlertDialog(@NonNull AlertDialog dialog, @Nullable Typeface typeface) {
        if (typeface == null) {
            return dialog;
        }

        TextView messageText = dialog.findViewById(android.R.id.message);
        Assert.assertNotNull("AlertDialog message textview not found", messageText);
        if (messageText != null) {
            messageText.setTypeface(typeface);
        }
        return dialog;
    }

    /**
     * Setup a toolbar as a support action bar
     *
     * @param activity to setup a toolbar
     * @param toolbar  to set
     */
    public static void setToolbar(@Nullable Activity activity, @Nullable Toolbar toolbar) {
        if (activity instanceof AppCompatActivity) {
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        }
    }

    @IntDef(flag = true, value = {
            VIEW_SIDE_BOTTOM, VIEW_SIDE_LEFT, VIEW_SIDE_RIGHT, VIEW_SIDE_TOP
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewSideFlags {
    }

    public interface ViewProcessor {
        void process(@NonNull View view);
    }
}
