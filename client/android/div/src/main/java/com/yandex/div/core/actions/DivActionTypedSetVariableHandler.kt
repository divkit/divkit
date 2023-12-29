package com.yandex.div.core.actions

import android.net.Uri
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.types.Color
import com.yandex.div2.DivActionTyped
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedSetVariableHandler @Inject constructor()
    : DivActionTypedHandler {

    override fun handleAction(
        action: DivActionTyped,
        view: Div2View
    ): Boolean = when (action) {

        is DivActionTyped.SetVariable -> {
            handleSetVariable(action, view)
            true
        }

        else -> false
    }

    private fun handleSetVariable(
        action: DivActionTyped.SetVariable,
        view: Div2View
    ) {
        val variableName = action.value.variableName.evaluate(view.expressionResolver)
        val newValue = action.value.value.evaluate(view.expressionResolver)
        view.setVariable(variableName) { variable: Variable ->
            variable.apply {
                when (this) {
                    is Variable.ArrayVariable ->
                        checkValueAndCast<JSONArray>(newValue, view, variableName)?.let(::set)

                    is Variable.BooleanVariable ->
                        checkValueAndCast<Boolean>(newValue, view, variableName)?.let(::set)

                    is Variable.ColorVariable ->
                        checkValueAndCast<Int>(newValue, view, variableName)?.let(::Color)?.let(::set)

                    is Variable.DictVariable ->
                        checkValueAndCast<JSONObject>(newValue, view, variableName)?.let(::set)

                    is Variable.DoubleVariable ->
                        checkValueAndCast<Double>(newValue, view, variableName)?.let(::set)

                    is Variable.IntegerVariable ->
                        checkValueAndCast<Long>(newValue, view, variableName)?.let(::set)

                    is Variable.StringVariable ->
                        checkValueAndCast<String>(newValue, view, variableName)?.let(::set)

                    is Variable.UrlVariable ->
                        checkValueAndCast<Uri>(newValue, view, variableName)?.let(::set)
                }
            }
        }
    }

    private inline fun <reified T : Any> checkValueAndCast(
        newValue: Any,
        view: Div2View,
        variableName: String
    ): T? =
        (newValue as? T).also { value ->
            if (value == null) {
                val valueType = when (newValue) {
                    is Int, is Double -> "number"
                    is JSONObject -> "dict"
                    is JSONArray -> "array"
                    else -> newValue.javaClass.simpleName.lowercase()
                }
                view.logError(IllegalArgumentException(
                    "Trying to set value with invalid type ($valueType) to variable $variableName"
                ))
            }
        }
}
