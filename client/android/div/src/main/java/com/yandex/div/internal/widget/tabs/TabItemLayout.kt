package com.yandex.div.internal.widget.tabs

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import com.yandex.div.R

internal class TabItemLayout
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    init {
        id = R.id.div_tabbed_tab_title_item
        layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.TOP and Gravity.START
    }
}
