package com.yandex.div.legacy.view

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.yandex.alicekit.core.utils.dp
import com.yandex.div.legacy.R

internal class ContainerBorderLayout constructor(context: Context) : RoundedCornersWithStrokeLayout(context) {

    init {
        layoutParams = createLayoutParams()
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER_VERTICAL
        cornerRadius = resources.getDimension(R.dimen.div_gallery_item_corners_radius)
        strokeColor = ContextCompat.getColor(context, android.R.color.white)
        strokeWidth = dp(1)
    }

    private fun createLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER_VERTICAL
        }
    }
}
