package com.yandex.divkit.demo.div

import android.view.View
import android.widget.Toast
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivCustom

class CheckBoundsExtensionHandler : DivExtensionHandler {
    override fun matches(div: DivBase): Boolean {
        return div is DivCustom && div.extensions?.any { it.id == EXTENSION_ID } ?: false
    }

    override fun bindView(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase) {
        Toast.makeText(view.context, "$EXTENSION_ID is bounded to ${div.id}!", Toast.LENGTH_SHORT).show()
    }

    override fun unbindView(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase) {
        Toast.makeText(view.context, "$EXTENSION_ID is unbounded from ${div.id}!", Toast.LENGTH_SHORT).show()
    }

    private companion object {
        private const val EXTENSION_ID = "check_binds"
    }
}