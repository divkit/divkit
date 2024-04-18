package com.yandex.div.core.player

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.yandex.div.R
import com.yandex.div2.DivVideoScale

abstract class DivPlayerView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.divImageStyle
) : FrameLayout(context, attrs, defStyleAttr), DivVideoAttachable {
    @Deprecated("Will be removed in future releases")
    open fun isCompatibleWithNewParams(scale: DivVideoScale): Boolean = true
}
