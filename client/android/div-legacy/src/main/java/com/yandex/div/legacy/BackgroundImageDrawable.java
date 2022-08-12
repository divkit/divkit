package com.yandex.div.legacy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.images.utils.ThumbnailUtils;

public class BackgroundImageDrawable extends Drawable {
    private final Rect mDrawableBounds = new Rect();
    @NonNull
    private final Context mContext;
    @NonNull
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    @Nullable
    private Bitmap mOriginalBitmap;

    @Nullable
    private Matrix mThumbTransformMatrix;

    public BackgroundImageDrawable(@NonNull Context context) {
        mContext = context;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mDrawableBounds.set(bounds);

        onChanged();
    }

    public void setOriginalBitmap(@NonNull Bitmap bitmap) {
        mOriginalBitmap = bitmap;

        onChanged();
    }

    private void onChanged() {
        if (mOriginalBitmap == null) {
            return;
        }

        if (mDrawableBounds.width() == 0 || mDrawableBounds.height() == 0) {
            return;
        }

        mThumbTransformMatrix = ThumbnailUtils.makeThumbnailTransformMatrix(mOriginalBitmap, mDrawableBounds.width(), mDrawableBounds.height(), 0);
        invalidateSelf();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mOriginalBitmap == null || mThumbTransformMatrix == null) {
            return;
        }
        canvas.save();
        canvas.clipRect(mDrawableBounds);
        canvas.drawBitmap(mOriginalBitmap, mThumbTransformMatrix, mPaint);
        canvas.restore();
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
