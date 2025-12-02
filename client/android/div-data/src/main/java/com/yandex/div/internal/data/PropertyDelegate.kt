package com.yandex.div.internal.data

import com.yandex.div.core.Disposable
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.data.VariableMutationException
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.DateTime
import com.yandex.div.evaluable.types.Url
import com.yandex.div.internal.util.ParsingValueUtils.parseAsBoolean
import com.yandex.div.internal.util.ParsingValueUtils.parseAsColor
import com.yandex.div.internal.util.ParsingValueUtils.parseAsDouble
import com.yandex.div.internal.util.ParsingValueUtils.parseAsJsonArray
import com.yandex.div.internal.util.ParsingValueUtils.parseAsJsonObject
import com.yandex.div.internal.util.ParsingValueUtils.parseAsLong
import com.yandex.div.internal.util.ParsingValueUtils.parseAsUrl
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAction
import com.yandex.div2.DivEvaluableType
import com.yandex.div2.PropertyVariable
import org.json.JSONArray
import org.json.JSONObject

@InternalApi
class PropertyDelegate(
    private val variableName: String,
    private val valueType: DivEvaluableType,
    val getExpression: Expression<*>,
    private val setActions: List<DivAction>?,
    private val newValueVarName: String,
    private val executor: PropertyVariableExecutor,
) {

    private var subscription: Disposable? = null

    fun get() = executor.evaluate(getExpression)

    fun set(value: Any) {
        val actions = setActions
        if (actions.isNullOrEmpty()) {
            throw VariableMutationException("Cannot set property. No setters provided. Name: '$variableName'")
        }

        try {
            when (valueType) {
                DivEvaluableType.STRING -> value as String
                DivEvaluableType.INTEGER -> (value as Number).toLong()
                DivEvaluableType.NUMBER -> (value as Number).toDouble()
                DivEvaluableType.BOOLEAN -> value as Boolean
                DivEvaluableType.URL -> value as Url
                DivEvaluableType.COLOR -> value as Color
                DivEvaluableType.DICT -> value as JSONObject
                DivEvaluableType.ARRAY -> value as JSONArray
                DivEvaluableType.DATETIME -> value as DateTime
            }
        } catch (e: ClassCastException) {
            throw VariableMutationException(
                "Unable to set value with type ${value.javaClass} to variable '$variableName'",
                e
            )
        }

        executor.performSet(variableName, newValueVarName, actions, value)
    }

    fun set(value: String) {
        val newValue = when (valueType) {
            DivEvaluableType.STRING -> value
            DivEvaluableType.INTEGER -> value.parseAsLong()
            DivEvaluableType.NUMBER -> value.parseAsDouble()
            DivEvaluableType.BOOLEAN -> value.parseAsBoolean()
            DivEvaluableType.URL -> value.parseAsUrl()
            DivEvaluableType.COLOR -> value.parseAsColor()
            DivEvaluableType.DICT -> value.parseAsJsonObject()
            DivEvaluableType.ARRAY -> value.parseAsJsonArray()
            DivEvaluableType.DATETIME ->
                throw VariableMutationException("DateTime variables mutation from string is not supported.")
        }
        set(newValue)
    }

    fun observe(onChangeCallback: () -> Unit) {
        subscription = executor.observe(getExpression, onChangeCallback)
    }

    fun release() {
        subscription?.close()
        subscription = null
    }

    fun toDivVariable(): PropertyVariable {
        val get = Expression.constant(getExpression.rawValue as String)
        return PropertyVariable(get, variableName, newValueVarName, setActions, valueType)
    }

    fun copy(
        getExpression: Expression<*>,
        setActions: List<DivAction>?,
        newValueVarName: String,
    ) = PropertyDelegate(variableName, valueType, getExpression, setActions, newValueVarName, executor)
}
