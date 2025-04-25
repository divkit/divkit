package com.yandex.div.core.expression.triggers

import com.yandex.div.core.Disposable
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.DivActionHandler.DivActionReason
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.downloader.PersistentDivDataObserver
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.DivActionBinder
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.Evaluable
import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.internal.Assert
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivTrigger

@Mockable
internal class TriggersController(
    private val variableController: VariableController,
    private val expressionResolver: ExpressionResolver,
    private val evaluator: Evaluator,
    private val errorCollector: ErrorCollector,
    private val logger: Div2Logger,
    private val divActionBinder: DivActionBinder
) {
    private val executors = mutableMapOf<List<DivTrigger>, MutableList<TriggerExecutor>>()
    private var currentView: DivViewFacade? = null
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
            val rawExpression = trigger.condition.rawValue.toString()
            val evaluable = try {
                Evaluable.lazy(rawExpression)
            } catch (e: EvaluableException) {
                return@forEach
            }

            findErrors(evaluable.variables)?.let {
                errorCollector.logError(
                    IllegalStateException("Invalid condition: '${trigger.condition}'", it)
                )
                return@forEach
            }

            activeExecutors.add(TriggerExecutor(
                rawExpression,
                evaluable,
                evaluator,
                trigger.actions,
                trigger.mode,
                expressionResolver,
                variableController,
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

    fun clearBinding(view: DivViewFacade?) {
        currentView = null
        executors.forEach { (_, value) -> value.forEach { it.onDetach(view) } }
    }

    fun onAttachedToWindow(view: DivViewFacade) {
        if (currentView == view) return
        currentView = view
        activeTriggers?.let {
            executors[it]?.forEach { executor -> executor.onAttach(view) }
        }
    }
}

private class TriggerExecutor(
    private val rawExpression: String,
    private val condition: Evaluable,
    private val evaluator: Evaluator,
    private val actions: List<DivAction>,
    private val mode: Expression<DivTrigger.Mode>,
    private val resolver: ExpressionResolver,
    private val variableController: VariableController,
    private val errorCollector: ErrorCollector,
    private val logger: Div2Logger,
    private val divActionBinder: DivActionBinder
) {
    private val changeTrigger = { _: Variable -> tryTriggerActions() }
    private var modeObserver = mode.observeAndGet(resolver) { currentMode = it }
    private var currentMode = DivTrigger.Mode.ON_CONDITION
    private var wasConditionSatisfied = false
    private var observersDisposable = Disposable.NULL
    private var removingDisposable = Disposable.NULL
    private var bindCompletionDisposable = Disposable.NULL
    private val attachedViews = mutableSetOf<DivViewFacade>()

    fun onAttach(view: DivViewFacade) {
        attachedViews.add(view)
        invalidateObservation()
    }

    fun onDetach(view: DivViewFacade?) {
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
        observersDisposable = variableController.subscribeToVariablesChange(
            condition.variables,
            invokeOnSubscription = false,
            changeTrigger
        )

        removingDisposable = variableController.subscribeToVariablesUndeclared(condition.variables) {
            stopObserving()
        }

        modeObserver = mode.observeAndGet(resolver) { currentMode = it }

        tryTriggerActions()
    }

    private fun tryTriggerActions() {
        Assert.assertMainThread()
        attachedViews.forEach { tryTriggerActions(it) }
    }

    private fun tryTriggerActions(viewFacade: DivViewFacade) {
        (viewFacade as? Div2View)?.takeIf { it.inMiddleOfBind }?.let { div2View ->
            tryTriggerActionsAfterBind(div2View)
            return
        }

        if (!conditionSatisfied()) {
            return
        }

        actions.forEach {
            (viewFacade as? Div2View)?.let { div2View ->
                logger.logTrigger(div2View, it)
            }
        }
        divActionBinder.handleActions(viewFacade, resolver, actions, DivActionReason.TRIGGER)
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

    private fun conditionSatisfied(): Boolean {
        val nowSatisfied: Boolean = try {
            evaluator.eval(condition)
        } catch (e: Exception) {
            val exception = when (e) {
                is ClassCastException -> RuntimeException(
                    "Condition evaluated in non-boolean result! (expression: '$rawExpression')", e)
                is EvaluableException -> RuntimeException(
                    "Condition evaluation failed! (expression: '$rawExpression')", e)
                else -> throw e
            }

            errorCollector.logError(exception)
            return false
        }

        val wasSatisfied = wasConditionSatisfied
        wasConditionSatisfied = nowSatisfied

        if (!nowSatisfied) {
            return false
        }

        if (currentMode == DivTrigger.Mode.ON_CONDITION && wasSatisfied && nowSatisfied) {
            return false
        }

        return true
    }
}
