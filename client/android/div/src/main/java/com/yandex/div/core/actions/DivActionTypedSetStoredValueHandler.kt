package com.yandex.div.core.actions

import com.yandex.div.core.expression.storedvalues.StoredValuesActionHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.storedvalues.toStoredValue
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionSetStoredValue
import com.yandex.div2.DivActionTyped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedSetStoredValueHandler @Inject constructor() : DivActionTypedHandler {

    override fun handleAction(
        scopeId: String?,
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver
    ): Boolean = when(action) {
        is DivActionTyped.SetStoredValue -> {
            handleAction(action.value, view, resolver)
            true
        }
        else -> false
    }

    private fun handleAction(
        action: DivActionSetStoredValue,
        view: Div2View,
        resolver: ExpressionResolver
    ) {
        StoredValuesActionHandler.executeAction(
            value = action.value.toStoredValue(
                name = action.name.evaluate(resolver),
                expressionResolver = resolver
            ),
            scope = action.scope?.evaluate(resolver),
            lifetime = action.lifetime.evaluate(resolver),
            view = view
        )
    }
}
