package com.yandex.div.core.actions

import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionTyped

internal interface DivActionTypedHandler {

    fun handleAction(action: DivActionTyped, view: Div2View, resolver: ExpressionResolver): Boolean
}
