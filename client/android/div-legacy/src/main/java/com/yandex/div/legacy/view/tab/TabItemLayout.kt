package com.yandex.div.legacy.view.tab

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import com.yandex.div.legacy.R

//todo make internal later
class TabItemLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    init {
        id = R.id.div_tabbed_tab_title_item
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        orientation = VERTICAL
        gravity = Gravity.TOP and Gravity.START
    }
}
