package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.net.Uri
import android.util.AttributeSet
import com.yandex.div.core.widget.DivExtendableView
import com.yandex.div.core.widget.LoadableImageView
import com.yandex.div2.Div

internal open class DivGifImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LoadableImageView(context, attrs, defStyleAttr),
    DivHolderView<Div.GifImage> by DivHolderViewMixin(),
    DivExtendableView {

    internal var gifUrl: Uri? = null

    init {
        cropToPadding = true
    }

    override fun resetImageLoaded() {
        super.resetImageLoaded()
        gifUrl = null
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(it) }
    }

    override fun release() {
        super<LoadableImageView>.release()
        super<DivHolderView>.release()
        gifUrl = null
    }
}
