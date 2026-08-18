package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.net.Uri
import android.util.AttributeSet
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.widget.DivExtendableView
import com.yandex.div.core.widget.LoadableImageView
import com.yandex.div.internal.core.DivBlock

internal open class DivGifImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LoadableImageView(context, attrs, defStyleAttr),
    DivHolderView<DivBlock.GifImage> by DivHolderViewMixin(),
    DivExtendableView,
    MediaReleasable {

    internal var gifUrl: Uri? = null
    internal var loadPreviewReference: LoadReference? = null
    internal var previewUrl: Uri? = null

    init {
        cropToPadding = true
    }

    override fun resetImageLoaded() {
        super.resetImageLoaded()
        gifUrl = null
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        onBoundsChanged(width, height)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(it) }
    }

    override fun release() {
        super<DivHolderView>.release()
        releaseMedia()
    }

    override fun releaseMedia() {
        super<LoadableImageView>.release()
        gifUrl = null
    }
}
