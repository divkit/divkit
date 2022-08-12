package com.yandex.div.legacy.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.yandex.alicekit.core.utils.dpF
import com.yandex.div.legacy.R


internal class ContainerShadowLayout
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : androidx.cardview.widget.CardView(context, attrs) {

    init {
        layoutParams = createLayoutParams()
        radius = resources.getDimension(R.dimen.div_gallery_item_corners_radius)
        cardElevation = dpF(2)
        maxCardElevation = cardElevation
        setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
    }

    private fun createLayoutParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER_VERTICAL
        }
    }
}
