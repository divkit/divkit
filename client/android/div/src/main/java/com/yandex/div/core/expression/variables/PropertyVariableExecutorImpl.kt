package com.yandex.div.core.expression.variables

import com.yandex.div.core.DivActionHandler.DivActionReason
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.data.PropertyVariableExecutor
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAction
import java.lang.ref.WeakReference

internal class PropertyVariableExecutorImpl(
    private val resolver: ExpressionResolverImpl,
) : PropertyVariableExecutor {

    private var viewRef: WeakReference<Div2View>? = null

    fun attachView(view: Div2View) {
        viewRef = WeakReference(view)
    }

    override fun evaluate(getExpression: Expression<*>) = getExpression.evaluate(resolver)

    override fun observe(getExpression: Expression<*>, onChange: () -> Unit) =
        getExpression.observe(resolver) { onChange() }

    override fun performSet(
        propertyName: String,
        newValueVarName: String,
        actions: List<DivAction>,
        newValue: Any,
    ) {
        val view = viewRef?.get() ?: run {
            throw IllegalStateException("Property '$propertyName' set skipped: target Div2View is gone")
        }

        val constants = mapOf(newValueVarName to newValue)
        val resolverWithValue = resolver.withConstants(
            pathSegment = "property:$propertyName",
            constants = ConstantsProvider(constants)
        )

        actions.forEach {
            view.handleAction(it, DivActionReason.PROPERTY_VARIABLE_SET, resolverWithValue)
        }
    }
}
