package com.yandex.div.legacy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.widget.LinearLayout;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

/**
 * A layout that clips it's contents with rounded rectangle of specified radius and stroke
 *
 */
public class RoundedCornersWithStrokeLayout extends LinearLayout {
    private static final float EXTRA_CONTENT_INSET = 1.0f;
    private float mCornerRadius;
    private int mStrokeWidth;
    @NonNull
    private final Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    @NonNull
    private final Path mPath = new Path();
    @ColorInt
    private int mStrokeColor = Color.RED;
    @NonNull
    private final RectF mRectBounds = new RectF();
    @NonNull
    private final RectF mRectBorder = new RectF();

    public RoundedCornersWithStrokeLayout(@NonNull Context context) {
        super(context);

        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setColor(mStrokeColor);
        mStrokePaint.setStrokeWidth(mStrokeWidth);
        mStrokePaint.setAntiAlias(true);
    }

    public void setStrokeColor(@ColorInt int strokeColor) {
        mStrokeColor = strokeColor;
        mStrokePaint.setColor(mStrokeColor);
        invalidate();
    }

    @ColorInt
    public int getStrokeColor() {
        return mStrokeColor;
    }

    public void setStrokeWidth(int strokeWidth) {
        mStrokeWidth = strokeWidth;
        mStrokePaint.setStrokeWidth(mStrokeWidth);
        invalidate();
    }

    public int getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setCornerRadius(float cornerRadius) {
        mCornerRadius = cornerRadius;
        invalidate();
    }

    public float getCornerRadius() {
        return mCornerRadius;
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        int saveCount = canvas.save();
        clipPath(canvas);
        super.dispatchDraw(canvas);

        canvas.restoreToCount(saveCount);
        drawStroke(canvas);
    }

    private void clipPath(@NonNull Canvas canvas) {
        if (!isRoundedRect()) {
            return;
        }
        int width = getWidth();
        int height = getHeight();
        mPath.reset();
        mRectBounds.set(0, 0, width, height);
        insetContentIfNeeded(mRectBounds);
        mPath.addRoundRect(mRectBounds, mCornerRadius, mCornerRadius, Path.Direction.CW);
        canvas.clipPath(mPath);
    }

    private void drawStroke(@NonNull Canvas canvas) {
        if (mStrokeWidth <= 0) {
            return;
        }

        int width = getWidth();
        int height = getHeight();
        mRectBorder.set(0, 0, width, height);

        float strokeHalfWidth = (float) Math.ceil(mStrokeWidth / 2.0f);
        mRectBorder.inset(strokeHalfWidth, strokeHalfWidth);
        if (isRoundedRect()) {
            canvas.drawRoundRect(mRectBorder, mCornerRadius, mCornerRadius, mStrokePaint);
        } else {
            canvas.drawRect(mRectBorder, mStrokePaint);
        }
    }

    private static boolean isRoundedRect() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    private void insetContentIfNeeded(@NonNull RectF contentRect) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N || (isRoundedRect() && mStrokeWidth > 0)) {
            contentRect.inset(EXTRA_CONTENT_INSET, EXTRA_CONTENT_INSET);
        }
    }

}
