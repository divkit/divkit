package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.net.Uri
import android.util.AttributeSet
import com.yandex.div.R
import com.yandex.div.core.Disposable
import com.yandex.div.core.extension.DivExtensionView
import com.yandex.div.core.widget.LoadableImageView
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.widget.TransientView
import com.yandex.div.internal.widget.TransientViewMixin
import com.yandex.div2.DivImage

internal open class DivImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.divImageStyle
): LoadableImageView(context, attrs, defStyleAttr), DivBorderSupports by DivBorderSupportsMixin(),
    TransientView by TransientViewMixin(), DivExtensionView, ExpressionSubscriber {

    internal var div: DivImage? = null
    internal var imageUrl: Uri? = null
    internal var preview: String? = null

    override val subscriptions = mutableListOf<Disposable>()

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

    override fun release() {
        super.release()
        releaseBorderDrawer()
    }
}
