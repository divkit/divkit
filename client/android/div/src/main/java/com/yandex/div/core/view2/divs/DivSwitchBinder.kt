package com.yandex.div.core.view2.divs

import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.variables.TwoWayBooleanVariableBinder
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivSwitchView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.equalsToConstant
import com.yandex.div.json.expressions.isConstant
import com.yandex.div.json.expressions.isConstantOrNull
import com.yandex.div2.DivSwitch
import javax.inject.Inject

@DivScope
internal class DivSwitchBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val variableBinder: TwoWayBooleanVariableBinder
) : DivViewBinder<DivSwitch, DivSwitchView> {

    override fun bindView(context: BindingContext, view: DivSwitchView, div: DivSwitch, path: DivStatePath) {
        val oldDiv = view.div
        if (div === oldDiv) return

        baseBinder.bindView(context, view, div, oldDiv)

        view.apply {
            bindIsEnabled(div, oldDiv, context.expressionResolver)
            bindOnColor(div, oldDiv, context.expressionResolver)

            observeVariable(div, context, path)
        }
    }

    private fun DivSwitchView.bindIsEnabled(div: DivSwitch, oldDiv: DivSwitch?, resolver: ExpressionResolver) {
        if (div.isEnabled.equalsToConstant(oldDiv?.isEnabled)) {
            return
        }
        applyIsEnabled(div, resolver)
        if (div.isEnabled.isConstant()) {
            return
        }
        val callback = { _: Any -> applyIsEnabled(div, resolver) }
        addSubscription(div.isEnabled.observe(resolver, callback))
    }

    private fun DivSwitchView.applyIsEnabled(div: DivSwitch, resolver: ExpressionResolver) {
        val isEnabledValue = div.isEnabled.evaluate(resolver)
        isEnabled = isEnabledValue
    }

    private fun DivSwitchView.bindOnColor(div: DivSwitch, oldDiv: DivSwitch?, resolver: ExpressionResolver) {
        if (div.onColor.equalsToConstant(oldDiv?.onColor)) {
            return
        }
        applyOnColor(div, resolver)
        if (div.onColor.isConstantOrNull()) {
            return
        }
        val callback = { _: Any -> applyOnColor(div, resolver) }
        addSubscription(div.onColor?.observe(resolver, callback))
    }

    private fun DivSwitchView.applyOnColor(div: DivSwitch, resolver: ExpressionResolver) {
        val checkedColor = div.onColor?.evaluate(resolver)
        colorOn = checkedColor
    }

    private fun DivSwitchView.observeVariable(
        div: DivSwitch,
        bindingContext: BindingContext,
        path: DivStatePath,
    ) {
        val callbacks = object : TwoWayBooleanVariableBinder.Callbacks {
            override fun onVariableChanged(value: Boolean?) {
                value?.let {
                    isChecked = it
                }
            }

            override fun setViewStateChangeListener(valueUpdater: (Boolean) -> Unit) {
                setOnCheckedChangeListener(valueUpdater)
            }
        }

        val subscription = variableBinder.bindVariable(bindingContext, div.isOnVariable, callbacks, path)
        addSubscription(subscription)
    }
}
