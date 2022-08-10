package com.yandex.div.legacy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.alicekit.core.utils.Log;
import com.yandex.div.legacy.R;

/**
 * ImageView, which height is based on its width. Calculated dimension will be {@code mRatio} times
 * large than the dimension it is based on.
 * @see ImageView
 * @see R.styleable#RatioImageView_ratio
 */
public class RatioImageView extends AppCompatImageView {
    private static final String TAG = "[Y:RatioImageView]";

    public static final float MINIMAL_RATIO = 0.f;
    public static final int APPLY_ON_WIDTH = 0;
    public static final int APPLY_ON_HEIGHT = 1;
    private static final int SCALE_TOP_CROP = 0;

    @Nullable
    @FloatRange(from = MINIMAL_RATIO, fromInclusive = false)
    private Float mRatio;

    private int mScaleType = 1;
    private int mApplyOn = APPLY_ON_HEIGHT;

    private boolean mHasFrame = false;

    public RatioImageView(@NonNull Context context) {
        this(context, null);
    }

    public RatioImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.ratioImageViewStyle);
    }

    public RatioImageView(@NonNull Context context, @Nullable AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView, defStyleAttr, 0);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            final int attr = typedArray.getIndex(i);
            if (attr == R.styleable.RatioImageView_ratio) {
                mRatio = fixRatio(typedArray.getFloat(attr, MINIMAL_RATIO));
            } else if (attr == R.styleable.RatioImageView_customScaleType) {
                mScaleType = typedArray.getInt(attr, -1);
                if (mScaleType == SCALE_TOP_CROP) {
                    setScaleType(ImageView.ScaleType.MATRIX);
                }
            } else if (attr == R.styleable.RatioImageView_direction) { //noinspection ResourceType
                int applyOn = typedArray.getInteger(attr, APPLY_ON_HEIGHT);
                if (applyOn == APPLY_ON_HEIGHT || applyOn == APPLY_ON_WIDTH) {
                    mApplyOn = applyOn;
                }
            }
        }
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        int newWidthSpec = widthMeasureSpec;
        int newHeightSpec = heightMeasureSpec;
        if (mRatio != null) {
            int verticalPaddings = getPaddingTop() + getPaddingBottom();
            int horizontalPaddings = getPaddingLeft() + getPaddingRight();

            if (mApplyOn == APPLY_ON_HEIGHT) {
                newHeightSpec = calcHeightMeasureSpecByRatio(widthMeasureSpec, verticalPaddings, horizontalPaddings);
            } else {
                newWidthSpec = calcWidthMeasureSpecByRatio(heightMeasureSpec, verticalPaddings, horizontalPaddings);
            }
        }
        super.onMeasure(newWidthSpec, newHeightSpec);
    }

    private int calcHeightMeasureSpecByRatio(int widthMeasureSpec, int verticalPaddings, int horizontalPaddings) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int viewHeight = width;
        if (mRatio != null && width > horizontalPaddings) {
            viewHeight = Math.round((width - horizontalPaddings) / mRatio) + verticalPaddings;
        }

        return View.MeasureSpec.makeMeasureSpec(viewHeight, View.MeasureSpec.EXACTLY);
    }

    private int calcWidthMeasureSpecByRatio(int heightMeasureSpec, int verticalPaddings, int horizontalPaddings) {
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        int viewWidth = height;
        if (mRatio != null && height > verticalPaddings) {
            viewWidth = Math.round((height - verticalPaddings) * mRatio) + horizontalPaddings;
        }
        return View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY);
    }

    public void setRatio(@Nullable @FloatRange(from = MINIMAL_RATIO, fromInclusive = false) Float ratio) {
        mRatio = fixRatio(ratio);
        requestLayout();
    }

    public void setApplyOn(int applyOn) {
        mApplyOn = applyOn;
        requestLayout();
    }

    @Override
    protected boolean setFrame(int left, int top, int right, int bottom) {
        boolean result = super.setFrame(left, top, right, bottom);
        mHasFrame = true;
        topCropIfNeeded();
        return result;
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        topCropIfNeeded();
    }

    /**
     * Scale to fit width and place at the top. Bottom of image could be cut off, it's ok
     */
    private void topCropIfNeeded() {
        if (!mHasFrame || mScaleType != SCALE_TOP_CROP) {
            return;
        }

        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        Matrix matrix = new Matrix();
        matrix.set(getMatrix());

        float width = getWidth() - getPaddingLeft() - getPaddingRight();
        float drawableWidth = drawable.getIntrinsicWidth();
        if (width <= 0 || drawableWidth <= 0) {
            return;
        }

        float scale = width / drawableWidth;
        matrix.setScale(scale, scale);
        setImageMatrix(matrix);
    }

    public void setRoundedImage(@NonNull Bitmap bitmap, @Px int cornerRadius) {
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        if (mRatio != null && mRatio > MINIMAL_RATIO) {
            if (mApplyOn == APPLY_ON_HEIGHT) {
                imageHeight = Math.round(imageWidth / mRatio);
                imageHeight = imageHeight > bitmap.getHeight() ? bitmap.getHeight() : imageHeight;
            } else {
                imageWidth = Math.round(mRatio / imageHeight);
                imageWidth = imageWidth > bitmap.getWidth() ? bitmap.getWidth() : imageWidth;
            }
        }

        if (imageWidth <= 0 || imageHeight <= 0) {
            // Fallback to original size
            imageWidth = bitmap.getWidth();
            imageHeight = bitmap.getHeight();
        }

        try {
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),
                                                                                              Bitmap.createBitmap(bitmap, 0, 0, imageWidth,
                                                                                                                  imageHeight));
            roundedBitmapDrawable.setCornerRadius(cornerRadius);
            setImageDrawable(roundedBitmapDrawable);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "Not enough memory to scale image", e);
            setImageBitmap(bitmap);
        }
    }

    @Nullable
    private Float fixRatio(@Nullable Float ratio) {
        if (ratio != null && ratio <= MINIMAL_RATIO) {
            Assert.fail("Ratio must be greater than 0.0");
            return null;
        }
        return ratio;
    }
}
