package com.yandex.div.internal.widget.tabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import androidx.appcompat.widget.TintTypedArray;
import com.yandex.div.R;

/**
 * TabItem is a special 'view' which allows you to declare tab items for a {@link BaseIndicatorTabLayout} within
 * a layout. This view is not actually added to TabLayout, it is just a dummy which allows setting
 * of a tab items's text, icon and custom layout. See TabLayout for more information on how to use
 * it.
 *
 * @attr ref com.google.android.material.R.styleable#TabItem_android_icon
 * @attr ref com.google.android.material.R.styleable#TabItem_android_text
 * @attr ref com.google.android.material.R.styleable#TabItem_android_layout
 * @see BaseIndicatorTabLayout
 */
@SuppressLint("RestrictedApi")
class TabItem extends View {
    public final CharSequence text;
    public final Drawable icon;
    public final int customLayout;

    public TabItem(Context context) {
        this(context, null);
    }

    public TabItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        final TintTypedArray a =
                TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.TabItem);
        text = a.getText(R.styleable.TabItem_android_text);
        icon = a.getDrawable(R.styleable.TabItem_android_icon);
        customLayout = a.getResourceId(R.styleable.TabItem_android_layout, 0);
        a.recycle();
    }
}
