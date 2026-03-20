package com.yandex.div.compose.triggers

import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.dagger.DivLocalScope
import com.yandex.div.core.Disposable
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivTrigger.Mode
import javax.inject.Inject

@DivLocalScope
internal class DivTriggerStorage @Inject constructor(
    private val actionHandler: DivActionHandler,
    private val actionHandlingContext: DivActionHandlingContext,
    private val expressionResolver: ExpressionResolver
) {

    private class Item(
        private val trigger: DivTrigger,
        expressionResolver: ExpressionResolver,
        private val handleActions: (actions: List<DivAction>) -> Unit
    ) {
        private val mode = trigger.mode.evaluate(expressionResolver)
        private val conditionSubscription: Disposable

        private var lastCondition: Boolean = false

        init {
            conditionSubscription = trigger.condition
                .observeAndGet(expressionResolver, this::onConditionChanged)
        }

        private fun onConditionChanged(condition: Boolean) {
            when (mode) {
                Mode.ON_CONDITION -> {
                    if (!lastCondition && condition) {
                        handleActions(trigger.actions)
                    }
                    lastCondition = condition
                }

                Mode.ON_VARIABLE ->
                    if (condition) {
                        handleActions(trigger.actions)
                    }
            }
        }
    }

    private val items = mutableListOf<Item>()

    fun add(trigger: DivTrigger) {
        items.add(Item(trigger, expressionResolver, this::handle))
    }

    private fun handle(actions: List<DivAction>) {
        actions.forEach {
            actionHandler.handle(context = actionHandlingContext, action = it)
        }
    }
}
