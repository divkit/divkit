package com.yandex.div.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

public class NonScrollImageView extends AppCompatImageView {

    public NonScrollImageView(Context context) {
        super(context);
    }

    public NonScrollImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean requestRectangleOnScreen(Rect rectangle, boolean immediate) {
        return false;
    }
}
