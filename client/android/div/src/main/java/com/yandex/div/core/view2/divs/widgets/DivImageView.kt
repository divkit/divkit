package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.net.Uri
import android.util.AttributeSet
import com.yandex.div.R
import com.yandex.div.core.extension.DivExtensionView
import com.yandex.div.core.widget.LoadableImageView
import com.yandex.div2.DivImage

internal open class DivImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.divImageStyle
): LoadableImageView(context, attrs, defStyleAttr),
    DivHolderView<DivImage> by DivHolderViewMixin(),
    DivExtensionView {

    internal var imageUrl: Uri? = null

    init {
        super.setAdjustViewBounds(true)
        super.setCropToPadding(true)
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) = Unit

    override fun setCropToPadding(cropToPadding: Boolean) = Unit

    override fun canResizeWidth(widthMeasureSpec: Int) = false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(it) }
    }

    override fun dispatchDraw(canvas: Canvas) {
        dispatchDrawBorderClipped(canvas) { super.dispatchDraw(it) }
    }
}
