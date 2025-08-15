package com.yandex.div.core.expression.triggers

import com.yandex.div.core.Disposable
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.ObserverList
import com.yandex.div.core.downloader.PersistentDivDataObserver
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.DivActionBinder
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAction
import com.yandex.div2.DivTrigger
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

/**
 * Tests for [TriggersController].
 */
@RunWith(RobolectricTestRunner::class)
class TriggersControllerTest {
    private val variableController = mock<VariableController> {
        on { subscribeToVariablesUndeclared(any(), any()) } doReturn Disposable.NULL
    }
    private val expressionResolver = mock<ExpressionResolverImpl> {
        on { variableController } doReturn variableController
    }
    private val divActionBinder = mock<DivActionBinder>()
    private val errorCollector = mock<ErrorCollector>()
    private val logger = mock<Div2Logger>()
    private val persistentDivDataObservers = ObserverList<PersistentDivDataObserver>()
    private val view = mock<Div2View> {
        on { addPersistentDivDataObserver(any()) } doAnswer {
            persistentDivDataObservers.addObserver(it.getArgument(0))
            Unit
        }
        on { removePersistentDivDataObserver(any()) } doAnswer {
            persistentDivDataObservers.removeObserver(it.getArgument(0))
            Unit
        }
    }

    private val condition = mock<Expression.MutableExpression<*, Boolean>> {
        on { getVariablesName(any()) } doReturn listOf("varA")
        on { evaluate(any()) } doReturn true
        on { observe(any(), any()) } doReturn Disposable.NULL
    }
    private val actionsA = listOf(mock<DivAction>())
    private val divTriggerA = DivTrigger(actionsA, condition)
    private val triggersA = listOf(divTriggerA)

    private val actionsB = listOf(mock<DivAction>())
    private val divTriggerB = DivTrigger(actionsB, condition)
    private val triggersB = listOf(divTriggerB)

    private val underTest = TriggersController(
        expressionResolver,
        errorCollector,
        logger,
        divActionBinder
    )

    @Test
    fun `switch between active group of triggers`() {
        underTest.onAttachedToWindow(view)
        underTest.ensureTriggersSynced(triggersA)
        underTest.ensureTriggersSynced(triggersB)

        verifyActionTriggered(actionsA)
        verifyActionTriggered(actionsB)
    }

    @Test
    fun `actions triggered only once after multiple ensureTriggersSynced`() {
        underTest.onAttachedToWindow(view)
        underTest.ensureTriggersSynced(triggersA)
        underTest.ensureTriggersSynced(triggersA)

        verifyActionTriggered(actionsA)
    }

    @Test
    fun `actions not triggered during bind`() {
        notifyBindStarted()

        underTest.onAttachedToWindow(view)
        underTest.ensureTriggersSynced(triggersA)

        verifyNoInteractions(divActionBinder)
    }

    @Test
    fun `actions will be triggered after bind`() {
        notifyBindStarted()

        underTest.onAttachedToWindow(view)
        underTest.ensureTriggersSynced(triggersA)
        notifyBindEnded()

        verifyActionTriggered(actionsA, times = 1)
    }

    @Test
    fun `actions will be triggered after bind only once`() {
        notifyBindStarted()

        underTest.onAttachedToWindow(view)
        underTest.ensureTriggersSynced(triggersA)
        notifyBindEnded()

        notifyBindStarted()
        notifyBindEnded()

        verifyActionTriggered(actionsA, times = 1)
    }

    @Test
    fun `actions not triggered after clearBindings`() {
        underTest.onAttachedToWindow(view)
        underTest.ensureTriggersSynced(triggersA)
        underTest.clearBinding(view)
        underTest.ensureTriggersSynced(triggersB)

        verifyActionTriggered(actionsA, times = 1)
        verifyActionTriggered(actionsB, times = 0)
    }

    @Test
    fun `when condition is not changing actions triggered only once after attach-detach-attach`() {
        underTest.ensureTriggersSynced(triggersB)

        underTest.onAttachedToWindow(view)
        underTest.clearBinding(view)
        underTest.onAttachedToWindow(view)

        verifyActionTriggered(actionsB, times = 1)
    }

    private fun verifyActionTriggered(actions: List<DivAction>, times: Int = 1) {
        verify(divActionBinder, times(times)).handleActions(any(), any(), eq(actions), any(), anyOrNull())
    }

    private fun notifyBindStarted() {
        whenever(view.inMiddleOfBind).doReturn(true)
        persistentDivDataObservers.forEach {
            it.onBeforeDivDataChanged()
        }
    }

    private fun notifyBindEnded() {
        whenever(view.inMiddleOfBind).doReturn(false)
        persistentDivDataObservers.forEach {
            it.onAfterDivDataChanged()
        }
    }
}
