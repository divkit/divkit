package com.yandex.div.compose.triggers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.di.DivLocalScope
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivTrigger.Mode
import dev.zacsweers.metro.Inject

/**
 * The triggers of a scope. They are observed while at least one observer is attached: an element
 * observes its own scope while it is composed, and the collection owner that built it observes the
 * same scope while the owner is composed.
 */
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
        private var conditionSubscription: AutoCloseable? = null

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
    private var observerCount = 0

    fun add(trigger: DivTrigger) {
        val item = Item(trigger, expressionResolver, this::handle)
        items.add(item)

        if (observerCount > 0) {
            item.startObserving()
        }
    }

    fun startObserving() {
        if (observerCount++ == 0) {
            items.forEach { it.startObserving() }
        }
    }

    fun stopObserving() {
        if (observerCount > 0 && --observerCount == 0) {
            items.forEach { it.stopObserving() }
        }
    }

    /**
     * Stops observing whatever observers remain: the scope is released.
     */
    fun clearObservers() {
        if (observerCount > 0) {
            observerCount = 0
            items.forEach { it.stopObserving() }
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
