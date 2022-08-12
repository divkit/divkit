package com.yandex.div.legacy.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import com.yandex.alicekit.core.views.EllipsizingTextView
import com.yandex.div.legacy.R

internal class GalleryTailLayout
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    init {
        layoutParams = createLayoutParams()
        orientation = VERTICAL
        gravity = Gravity.CENTER
        minimumWidth = resources.getDimensionPixelSize(R.dimen.div_gallery_tail_width)

        val imageView = ImageView(context).apply {
            id = R.id.div_gallery_tail_icon
            layoutParams = createImageLayoutParams()
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }

        val textView = EllipsizingTextView(context).apply {
            id = R.id.div_gallery_tail_text
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            maxWidth = resources.getDimensionPixelSize(R.dimen.div_gallery_tail_width)
            gravity = Gravity.CENTER
        }

        addView(imageView)
        addView(textView)
    }

    private fun createLayoutParams(): LayoutParams {
        return LayoutParams(resources.getDimensionPixelSize(R.dimen.div_gallery_tail_width), LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER
        }
    }

    private fun createImageLayoutParams(): LayoutParams {
        return LayoutParams(
                resources.getDimensionPixelSize(R.dimen.div_gallery_tail_image_size),
                resources.getDimensionPixelSize(R.dimen.div_gallery_tail_image_size)).apply {
            bottomMargin = resources.getDimensionPixelSize(R.dimen.div_gallery_tail_image_bottom_margin)
        }
    }
}
