package com.yandex.div.compose.triggers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.actions.DivActionSource
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
        private val expressionResolver: ExpressionResolver,
        private val handleActions: (actions: List<DivAction>) -> Unit
    ) {
        private val mode = trigger.mode.evaluate(expressionResolver)

        private var lastCondition: Boolean = false
        private var conditionSubscription: Disposable? = null

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

        fun startObserving() {
            conditionSubscription = trigger.condition
                .observeAndGet(expressionResolver, this::onConditionChanged)
        }

        fun stopObserving() {
            conditionSubscription?.close()
            conditionSubscription = null
        }
    }

    private val items = mutableListOf<Item>()
    private var isActive = true

    fun add(trigger: DivTrigger) {
        val item = Item(trigger, expressionResolver, this::handle)
        items.add(item)

        if (isActive) {
            item.startObserving()
        }
    }

    fun startObserving() {
        if (!isActive) {
            items.forEach { it.startObserving() }
            isActive = true
        }
    }

    fun stopObserving() {
        if (isActive) {
            items.forEach { it.stopObserving() }
            isActive = false
        }
    }

    private fun handle(actions: List<DivAction>) {
        actionHandler.handle(actionHandlingContext, actions, source = DivActionSource.TRIGGER)
    }
}

@Composable
internal fun DivTriggerStorage.observe() {
    DisposableEffect(this) {
        startObserving()
        onDispose {
            stopObserving()
        }
    }
}
