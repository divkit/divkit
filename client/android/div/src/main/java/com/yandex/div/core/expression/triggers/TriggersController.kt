package com.yandex.div.core.expression.triggers

import com.yandex.div.core.CompositeDisposable
import com.yandex.div.core.Disposable
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.expression.variables.VariableController
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
    divTriggers: List<DivTrigger>? = null,
    private val variableController: VariableController,
    private val expressionResolver: ExpressionResolver,
    private val divActionHandler: DivActionHandler,
    private val evaluator: Evaluator,
    private val errorCollector: ErrorCollector,
) {
    private val executors = mutableListOf<TriggerExecutor>()
    init {
        divTriggers?.forEach { trigger ->
            val rawExpression = trigger.condition.rawValue.toString()
            val evaluable = try {
                Evaluable.lazy(rawExpression)
            } catch (e: EvaluableException) {
                return@forEach
            }

            findErrors(evaluable.variables)?.let {
                Assert.fail("Invalid condition: '${trigger.condition}'", it)
                return@forEach
            }

            executors.add(TriggerExecutor(
                rawExpression,
                evaluable,
                evaluator,
                trigger.actions,
                trigger.mode,
                expressionResolver,
                divActionHandler,
                variableController,
                errorCollector,
            ))
        }
    }

    private fun findErrors(variables: List<String>): Throwable? {
        if (variables.isEmpty()) {
            return RuntimeException("No variables defined!")
        }
        return null
    }

    fun clearBinding() {
        executors.forEach { it.view = null }
    }

    fun onAttachedToWindow(view: DivViewFacade) {
        executors.forEach { it.view = view }
    }
}

private class TriggerExecutor(
    private val rawExpression: String,
    private val condition: Evaluable,
    private val evaluator: Evaluator,
    private val actions: List<DivAction>,
    private val mode: Expression<DivTrigger.Mode>,
    private val resolver: ExpressionResolver,
    private val divActionHandler: DivActionHandler,
    private val variableController: VariableController,
    private val errorCollector: ErrorCollector
) {
    private val changeTrigger = { _: Variable -> tryTriggerActions() }
    private var modeObserver = mode.observeAndGet(resolver) { currentMode = it }
    private var currentMode = DivTrigger.Mode.ON_CONDITION
    private var wasConditionSatisfied = false
    private var observersDisposable = Disposable.NULL

    var view: DivViewFacade? = null
        set(value) {
            field = value
            if (value == null) {
                stopObserving()
            } else {
                startObserving()
            }
        }

    private fun stopObserving() {
        modeObserver.close()
        observersDisposable.close()
    }

    private fun startObserving() {
        modeObserver.close()
        observersDisposable = variableController.subscribeToVariablesChange(
            condition.variables,
            invokeOnSubscription = false,
            changeTrigger
        )

        modeObserver = mode.observeAndGet(resolver) { currentMode = it }
        tryTriggerActions()
    }

    private fun tryTriggerActions() {
        Assert.assertMainThread()

        val viewFacade = view ?: return

        if (!conditionSatisfied()) {
            return
        }

        actions.forEach {
            divActionHandler.handleAction(it, viewFacade)
        }
    }

    private fun conditionSatisfied(): Boolean {
        val nowSatisfied: Boolean = try {
            evaluator.eval(condition)
        } catch (e: EvaluableException) {
            val exception = RuntimeException("Condition evaluation failed: '$rawExpression'!", e)
            Assert.fail(null, exception)
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
