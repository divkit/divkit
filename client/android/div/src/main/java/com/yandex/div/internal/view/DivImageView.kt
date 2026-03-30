package com.yandex.div.internal.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import com.yandex.div.R
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.extension.DivExtensionView
import com.yandex.div.core.view2.divs.widgets.DivHolderView
import com.yandex.div.core.view2.divs.widgets.DivHolderViewMixin
import com.yandex.div.core.view2.divs.widgets.MediaReleasable
import com.yandex.div.core.view2.divs.widgets.drawBorderClipped
import com.yandex.div.core.widget.LoadableImageView
import com.yandex.div2.Div

private typealias OnTouchListenerChangeObserver = (View.OnTouchListener?) -> Unit

@InternalApi
open class DivImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.divImageStyle
) : LoadableImageView(context, attrs, defStyleAttr),
    DivHolderView<Div.Image> by DivHolderViewMixin(),
    DivExtensionView,
    MediaReleasable {

    internal var imageUrl: Uri? = null

    var baseTouchListener: OnTouchListener? = null
        private set

    var onTouchListenerChangeObserver: OnTouchListenerChangeObserver? = null

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

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnTouchListener(l: OnTouchListener?) {
        baseTouchListener = l
        onTouchListenerChangeObserver?.invoke(l)
        super.setOnTouchListener(l)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun overrideOnTouchListener(l: OnTouchListener) = super.setOnTouchListener(l)

    override fun release() {
        super<DivHolderView>.release()
        releaseMedia()
    }

    override fun releaseMedia() {
        super<LoadableImageView>.release()
        imageUrl = null
    }
}
