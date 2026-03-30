package com.yandex.div.zoom

import android.view.View
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.util.type
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.view.DivImageView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivImage

class DivPinchToZoomExtensionHandler(
    private val configuration: DivPinchToZoomConfiguration
) : DivExtensionHandler {

    private val viewController = ZoomViewController(configuration)

    override fun matches(div: DivBase): Boolean =
        div.extensions?.any { extension -> extension.id == EXTENSION_ID } ?: false

    override fun bindView(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase) {
        if (div !is DivImage || view !is DivImageView) {
            val id = div.id?.let { " with id='$it'" } ?: ""
            val message = "Extension $EXTENSION_ID is ignored for div$id with type '${div.type}'. " +
                "It can be applied for images only."
            divView.logError(Throwable(message))
            return
        }

        val controller = ZoomTouchController(configuration, viewController)
        val touchListenerObserver = { baseListener: View.OnTouchListener? ->
            val touchListener = ZoomTouchListener(controller, baseListener)
            view.overrideOnTouchListener(touchListener)
        }

        view.onTouchListenerChangeObserver = touchListenerObserver
        touchListenerObserver(view.baseTouchListener)
    }

    override fun unbindView(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase) {
        if (div !is DivImage || view !is DivImageView) return

        view.onTouchListenerChangeObserver = null
        view.setOnTouchListener(null)
    }

    private companion object {
        private const val EXTENSION_ID = "pinch-to-zoom"
    }
}
