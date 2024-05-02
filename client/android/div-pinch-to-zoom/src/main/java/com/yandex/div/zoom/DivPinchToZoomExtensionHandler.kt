package com.yandex.div.zoom

import android.view.View
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivImage

class DivPinchToZoomExtensionHandler(
    private val configuration: DivPinchToZoomConfiguration
) : DivExtensionHandler {

    private val viewController = ZoomViewController(configuration)

    override fun matches(div: DivBase): Boolean {
        if (div !is DivImage) {
            return false
        }

        val extension = div.extensions?.find { extension ->
            extension.id == EXTENSION_ID
        }
        return extension != null
    }

    override fun bindView(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase) {
        val touchListener = ZoomTouchListener(
            ZoomTouchController(configuration, viewController)
        )
        view.setOnTouchListener(touchListener)
    }

    override fun unbindView(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase) {
        view.setOnTouchListener(null)
    }

    private companion object {
        private const val EXTENSION_ID = "pinch-to-zoom"
    }
}
