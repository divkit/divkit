package com.yandex.div.core.actions

import com.yandex.div.core.expression.storedvalues.StoredValuesActionHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.StoredValue
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.Url
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionSetStoredValue
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivTypedValue
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
        val name = action.name.evaluate(resolver)
        val lifetime = action.lifetime.evaluate(resolver)
        val storedValue = createStoredValue(action.value, name, resolver)

        StoredValuesActionHandler.executeAction(storedValue, lifetime, view)
    }

    private fun createStoredValue(
        value: DivTypedValue,
        name: String,
        resolver: ExpressionResolver
    ): StoredValue = when (value) {
        is DivTypedValue.Str -> StoredValue.StringStoredValue(name, value.value.value.evaluate(resolver))
        is DivTypedValue.Integer -> StoredValue.IntegerStoredValue(name, value.value.value.evaluate(resolver))
        is DivTypedValue.Bool -> StoredValue.BooleanStoredValue(name, value.value.value.evaluate(resolver))
        is DivTypedValue.Number -> StoredValue.DoubleStoredValue(name, value.value.value.evaluate(resolver))
        is DivTypedValue.Color -> StoredValue.ColorStoredValue(name, Color(value.value.value.evaluate(resolver)))
        is DivTypedValue.Url -> StoredValue.UrlStoredValue(name, Url.from(value.value.value.evaluate(resolver).toString()))
        is DivTypedValue.Array -> StoredValue.ArrayStoredValue(name, value.value.value.evaluate(resolver))
        is DivTypedValue.Dict -> StoredValue.DictStoredValue(name, value.value.value)
    }

}
