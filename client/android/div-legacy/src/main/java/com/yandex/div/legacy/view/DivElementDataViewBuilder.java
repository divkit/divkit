package com.yandex.div.legacy.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.AttrRes;
import androidx.annotation.DimenRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.AppCompatTextView;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.alicekit.core.views.EmptyDrawable;
import com.yandex.div.DivBaseBlock;
import com.yandex.div.DivImageElement;
import com.yandex.div.core.images.DivImageLoader;
import com.yandex.div.core.images.LoadReference;
import com.yandex.div.legacy.LegacyDivImageDownloadCallback;
import com.yandex.div.legacy.R;
import com.yandex.div.util.DivKitImageUtils;
import com.yandex.div.util.Position;
import com.yandex.images.CachedBitmap;
import com.yandex.images.utils.ScaleMode;
import com.yandex.images.utils.ThumbnailUtils;

abstract class DivElementDataViewBuilder<B extends DivBaseBlock> extends DivBaseViewBuilder<B> {

    private static final String TAG = "DivElementDataViewBuilder";

    static void setTextAndStyle(@NonNull AppCompatTextView textView, @Nullable CharSequence text, @NonNull TextStyle textStyle) {
        if (!TextUtils.isEmpty(text)) {
            textStyle.applyWithEllipsizig(textView);
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    @NonNull
    static AppCompatTextView createTextView(@NonNull TextViewFactory textViewFactory,
                                            @NonNull Context context,
                                            @AttrRes int styleAttr, @IdRes int id) {
        AppCompatTextView textView = textViewFactory.create(context, null, styleAttr);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setId(id);
        return textView;
    }

    @Px
    static int getDimensionOffset(@NonNull Context context, @DimenRes int dimen) {
        return context.getResources().getDimensionPixelOffset(dimen);
    }

    @Px
    static int getDimensionSize(@NonNull Context context, @DimenRes int dimen) {
        return context.getResources().getDimensionPixelSize(dimen);
    }

    static void bind(@NonNull DivView divView,
                     @NonNull DivImageLoader imageLoader,
                     @NonNull TextView textView,
                     @Nullable CharSequence divText,
                     @NonNull DivImageElement imageElement,
                     @DimenRes int paddingLeftDimenRes,
                     @DimenRes int paddingRightDimenRes,
                     @DimenRes int imageWidthDimenRes,
                     @DimenRes int imageHeightDimenRes) {

        final Resources resources = textView.getResources();
        final int imageWidth = resources.getDimensionPixelSize(imageWidthDimenRes);
        final int imageHeight = resources.getDimensionPixelSize(imageHeightDimenRes);
        final EmptyDrawable emptyDrawable = new EmptyDrawable(imageWidth, imageHeight);

        // no support for position in button divs yet
        setCompoundDrawable(textView, emptyDrawable, Position.LEFT);
        textView.setText(divText);

        textView.setCompoundDrawablePadding(resources.getDimensionPixelSize(R.dimen.div_compound_drawable_padding));
        final int paddingLeft = resources.getDimensionPixelOffset(paddingLeftDimenRes);
        final int paddingRight = resources.getDimensionPixelOffset(paddingRightDimenRes);
        final int vertical = resources.getDimensionPixelOffset(R.dimen.div_compound_drawable_vertical_padding);
        textView.setPadding(paddingLeft, vertical, paddingRight, vertical);

        LoadReference loadReference = imageLoader.loadImage(
                imageElement.imageUrl.toString(),
                DivKitImageUtils.toDivKitCallback(new LegacyDivImageDownloadCallback(divView) {
                    @UiThread
                    @Override
                    public void onSuccess(@NonNull CachedBitmap cachedBitmap) {
                        final Bitmap resized = ThumbnailUtils.extractThumbnail(cachedBitmap.getBitmap(), imageWidth, imageHeight, 0,
                                                                               ScaleMode.CENTER_CROP);
                        final BitmapDrawable drawable = new BitmapDrawable(resources, resized);
                        setCompoundDrawable(textView, drawable, Position.LEFT);
                    }
                }));
        divView.addLoadReference(loadReference, textView);
    }

    private static void setCompoundDrawable(@NonNull TextView textView, @NonNull Drawable drawable,
                                            @NonNull Position imagePosition) {
        final int h = drawable.getIntrinsicHeight();
        final int w = drawable.getIntrinsicWidth();
        drawable.setBounds(0, 0, w, h);
        if (imagePosition == Position.LEFT) {
            textView.setCompoundDrawables(drawable, null, null, null);
        } else {
            Assert.assertEquals(Position.RIGHT, imagePosition);
            textView.setCompoundDrawables(null, null, drawable, null);
        }
    }
}
