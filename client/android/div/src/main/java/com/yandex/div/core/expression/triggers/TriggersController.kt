package com.yandex.div.core.expression.triggers

import com.yandex.div.core.Disposable
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.DivActionHandler.DivActionReason
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.downloader.PersistentDivDataObserver
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.DivActionBinder
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.runBindingAction
import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAction
import com.yandex.div2.DivTrigger
import java.util.WeakHashMap

@Mockable
internal class TriggersController(
    private val expressionResolver: ExpressionResolverImpl,
    private val errorCollector: ErrorCollector,
    private val logger: Div2Logger,
    private val divActionBinder: DivActionBinder
) {
    private val executors = mutableMapOf<List<DivTrigger>, MutableList<TriggerExecutor>>()
    private var currentView: Div2View? = null
    private var activeTriggers: List<DivTrigger>? = null

    fun ensureTriggersSynced(divTriggers: List<DivTrigger>) {
        if (activeTriggers === divTriggers) {
            return
        }

        activeTriggers = divTriggers

        val activeView = currentView

        val activeExecutors = executors.getOrPut(divTriggers) { mutableListOf() }
        activeView?.let { clearBinding(it) }

        divTriggers.forEach { trigger ->
            val expression = trigger.condition as? Expression.MutableExpression<*, Boolean> ?: run {
                errorCollector.logError(
                    IllegalStateException(
                        "Invalid condition: '${trigger.condition}'",
                        RuntimeException("Condition is not mutable!")
                    )
                )
                return@forEach
            }

            findErrors(expression.getVariablesName(expressionResolver))?.let {
                errorCollector.logError(
                    IllegalStateException("Invalid condition: '${trigger.condition}'", it)
                )
                return@forEach
            }

            activeExecutors.add(TriggerExecutor(
                expression,
                trigger.actions,
                trigger.mode,
                expressionResolver,
                errorCollector,
                logger,
                divActionBinder
            ))
        }

        activeView?.let { onAttachedToWindow(it) }
    }

    private fun findErrors(variables: List<String>): Throwable? {
        if (variables.isEmpty()) {
            return RuntimeException("No variables defined!")
        }
        return null
    }

    fun clearBinding(view: Div2View?) {
        currentView = null
        executors.forEach { (_, value) -> value.forEach { it.onDetach(view) } }
    }

    fun onAttachedToWindow(view: Div2View) {
        if (currentView == view) return
        currentView = view
        activeTriggers?.let {
            executors[it]?.forEach { executor -> executor.onAttach(view) }
        }
    }

    fun onDetachedFromWindow(view: Div2View) {
        if (currentView == view) {
            currentView = null
        }
        executors.forEach { (_, value) -> value.forEach { it.onDetach(view) } }
    }
}

private class TriggerExecutor(
    private val expression: Expression.MutableExpression<*, Boolean>,
    private val actions: List<DivAction>,
    private val mode: Expression<DivTrigger.Mode>,
    private val resolver: ExpressionResolverImpl,
    private val errorCollector: ErrorCollector,
    private val logger: Div2Logger,
    private val divActionBinder: DivActionBinder
) {
    private val changeTrigger = { _: Boolean -> tryTriggerActions() }
    private var modeObserver = mode.observeAndGet(resolver) { currentMode = it }
    private var currentMode = DivTrigger.Mode.ON_CONDITION
    private var wasConditionSatisfied = WeakHashMap<Div2View, Boolean>()
    private var observersDisposable = Disposable.NULL
    private var removingDisposable = Disposable.NULL
    private var bindCompletionDisposable = Disposable.NULL
    private val attachedViews = mutableSetOf<Div2View>()

    fun onAttach(view: Div2View) {
        attachedViews.add(view)
        invalidateObservation()
    }

    fun onDetach(view: Div2View?) {
        attachedViews.remove(view)
        invalidateObservation()
    }

    private fun invalidateObservation() {
        if (attachedViews.isEmpty()) {
            stopObserving()
        } else {
            startObserving()
        }
    }

    private fun stopObserving() {
        modeObserver.close()
        observersDisposable.close()
        removingDisposable.close()
        bindCompletionDisposable.close()
    }

    private fun startObserving() {
        modeObserver.close()
        observersDisposable = expression.observe(resolver, changeTrigger)

        removingDisposable = resolver.variableController
            .subscribeToVariablesUndeclared(expression.getVariablesName(resolver)) { stopObserving() }

        modeObserver = mode.observeAndGet(resolver) { currentMode = it }

        tryTriggerActions()
    }

    private fun tryTriggerActions() {
        attachedViews.forEach { divView ->
            (divView as? Div2View)?.runBindingAction {
                tryTriggerActions(divView)
            }
        }
    }

    private fun tryTriggerActions(view: Div2View) {
        if (view.inMiddleOfBind) {
            tryTriggerActionsAfterBind(view)
            return
        }

        if (!conditionSatisfied(view)) {
            return
        }

        actions.forEach {
            logger.logTrigger(view, it)
        }
        divActionBinder.handleActions(view, resolver, actions, DivActionReason.TRIGGER)
    }

    private fun tryTriggerActionsAfterBind(div2View: Div2View) {
        bindCompletionDisposable.close()

        val observer = object : PersistentDivDataObserver {
            override fun onAfterDivDataChanged() {
                div2View.removePersistentDivDataObserver(this)
                tryTriggerActions()
            }
        }
        bindCompletionDisposable = Disposable {
            div2View.removePersistentDivDataObserver(observer)
        }

        div2View.addPersistentDivDataObserver(observer)
    }

    private fun conditionSatisfied(view: Div2View): Boolean {
        val nowSatisfied: Boolean = try {
            expression.evaluate(resolver)
        } catch (e: Exception) {
            val exception = when (e) {
                is ClassCastException -> RuntimeException(
                    "Condition evaluated in non-boolean result! (expression: '${expression.rawValue}')", e)
                is EvaluableException -> RuntimeException(
                    "Condition evaluation failed! (expression: '${expression.rawValue}')", e)
                else -> throw e
            }

            errorCollector.logError(exception)
            return false
        }

        val wasSatisfied = wasConditionSatisfied[view] ?: false
        wasConditionSatisfied[view] = nowSatisfied

        if (!nowSatisfied) {
            return false
        }

        if (currentMode == DivTrigger.Mode.ON_CONDITION && wasSatisfied) {
            return false
        }

        return true
    }
}
