package com.yandex.div.internal.data

import com.yandex.div.core.Disposable
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAction

/**
 * Executor for property variable get and set methods. It connects variable with entities of DivView scope.
 */
@InternalApi
interface PropertyVariableExecutor {

    /**
     * Evaluates property's value.
     *
     * @param getExpression expression from property's [com.yandex.div2.PropertyVariable.get] method
     * @return evaluated property value
     */
    fun evaluate(getExpression: Expression<*>): Any

    /**
     * Subscribes to property's [com.yandex.div2.PropertyVariable.get] expression
     * to notify about variable changes.
     *
     * @param getExpression expression from property's [com.yandex.div2.PropertyVariable.get] method
     * @param onChange callback notifying about variable changes
     * @return [Disposable] object to cancel the subscription
     */
    fun observe(getExpression: Expression<*>, onChange: () -> Unit): Disposable

    /**
     * Performs actions when setting new value to property variable.
     * Property doesn't keep its value, it's evaluated every time when [get] method is called.
     * So when [set] method is called, new value isn't assigned directly,
     * but actions from [com.yandex.div2.PropertyVariable.set] are performed with it.
     *
     * Example usage in JSON:
     * ```json
     * {
     *   "variables": [
     *     {
     *       "type": "integer",
     *       "name": "counter",
     *       "value": 0
     *     },
     *     {
     *       "type": "property",
     *       "name": "displayed_counter",
     *       "value_type": "integer",
     *       "get": "@{counter}",
     *       "new_value_variable_name": "new_val",
     *       "set": [
     *         {
     *           "log_id": "update_counter",
     *           "url": "div-action://set_variable?name=counter&value=@{new_val}"
     *         }
     *       ]
     *     }
     *   ]
     * }
     * ```
     *
     * How it works:
     * When `displayed_counter` property is set to new value (e.g., 5):
     *  - New value (5) is temporarily assigned to variable with name from `newValueVarName` field ("new_val")
     *  - Actions from `set` field are executed. They can access new value via "@{new_val}"
     *  - In this example, the action sets the new value to "counter" variable
     *
     * @param propertyName name of the property for which the value is being set
     * @param newValueVarName name of fake variable for using in [DivAction] to get the new value which is being set
     * @param actions list of [DivAction] to execute with new value
     * @param newValue new property variable value
     */
    fun performSet(
        propertyName: String,
        newValueVarName: String,
        actions: List<DivAction>,
        newValue: Any,
    )

    companion object {
        /**
         * Stub implementation of the [PropertyVariableExecutor] interface.
         *
         * Used when the real implementation is unavailable or not required.
         * All methods are no-op operations.
         */
        val STUB = object : PropertyVariableExecutor {

            override fun evaluate(getExpression: Expression<*>) = Any()

            override fun observe(getExpression: Expression<*>, onChange: () -> Unit) = Disposable.NULL

            override fun performSet(
                propertyName: String,
                newValueVarName: String,
                actions: List<DivAction>,
                newValue: Any
            ) = Unit
        }
    }
}
