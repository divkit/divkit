package com.yandex.div.core.expression.triggers

import com.yandex.div.core.Disposable
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.ObserverList
import com.yandex.div.core.downloader.PersistentDivDataObserver
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.DivActionBinder
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.json.JsonTemplate
import com.yandex.div.json.ParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.templates.TemplateProvider
import com.yandex.div2.DivAction
import com.yandex.div2.DivTrigger
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
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
        on { subscribeToVariablesChange(any(), any(), any()) } doReturn Disposable.NULL
        on { subscribeToVariablesUndeclared(any(), any()) } doReturn Disposable.NULL
    }
    private val expressionResolver = mock<ExpressionResolver>()
    private val divActionBinder = mock<DivActionBinder>()
    private val evaluator = mock<Evaluator> {
        on { eval<Boolean>(any()) } doReturn true
    }
    private val errorCollector = mock<ErrorCollector>()
    private val logger = mock<Div2Logger>()
    private val persistentDivDataObservers = ObserverList<PersistentDivDataObserver>()
    private val view = mock<Div2View> {
        on { expressionResolver } doReturn mock()
        on { addPersistentDivDataObserver(any()) } doAnswer {
            persistentDivDataObservers.addObserver(it.getArgument(0))
            Unit
        }
        on { removePersistentDivDataObserver(any()) } doAnswer {
            persistentDivDataObservers.removeObserver(it.getArgument(0))
            Unit
        }
    }

    private val env = object : ParsingEnvironment {
        override val templates: TemplateProvider<JsonTemplate<*>> = TemplateProvider.empty()
        override val logger = ParsingErrorLogger { throw it }
    }

    private val logIdA = "log_id_A"
    private val divTriggerA = createDivTrigger(logIdA)
    private val triggersA = listOf(divTriggerA)

    private val logIdB = "log_id_B"
    private val divTriggerB = createDivTrigger(logIdB)
    private val triggersB = listOf(divTriggerB)

    private val underTest = TriggersController(
        variableController,
        expressionResolver,
        evaluator,
        errorCollector,
        logger,
        divActionBinder
    )

    @Test
    fun `switch between active group of triggers`() {
        underTest.onAttachedToWindow(view)
        underTest.ensureTriggersSynced(triggersA)
        underTest.ensureTriggersSynced(triggersB)

        verifyActionTriggered(logIdA)
        verifyActionTriggered(logIdB)
    }

    @Test
    fun `actions triggered only once after multiple ensureTriggersSynced`() {
        underTest.onAttachedToWindow(view)
        underTest.ensureTriggersSynced(triggersA)
        underTest.ensureTriggersSynced(triggersA)

        verifyActionTriggered(logIdA)
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

        verifyActionTriggered(logIdA, times = 1)
    }

    @Test
    fun `actions will be triggered after bind only once`() {
        notifyBindStarted()

        underTest.onAttachedToWindow(view)
        underTest.ensureTriggersSynced(triggersA)
        notifyBindEnded()

        notifyBindStarted()
        notifyBindEnded()

        verifyActionTriggered(logIdA, times = 1)
    }

    @Test
    fun `actions not triggered after clearBindings`() {
        underTest.onAttachedToWindow(view)
        underTest.ensureTriggersSynced(triggersA)
        underTest.clearBinding()
        underTest.ensureTriggersSynced(triggersB)

        verifyActionTriggered(logIdA, times = 1)
        verifyActionTriggered(logIdB, times = 0)
    }

    @Test
    fun `when condition is not changing actions triggered only once after attach-detach-attach`() {
        underTest.ensureTriggersSynced(triggersB)

        underTest.onAttachedToWindow(view)
        underTest.clearBinding()
        underTest.onAttachedToWindow(view)

        verifyActionTriggered(logIdB, times = 1)
    }

    private fun verifyActionTriggered(logId: String, times: Int = 1) {
        val actions = argumentCaptor<List<DivAction>>()
        verify(divActionBinder, atLeastOnce()).handleActions(any(), any(), actions.capture(), any(), anyOrNull())

        val actionsWithLogId = actions.allValues.flatten().filter { it.logId.rawValue == logId }
        Assert.assertEquals(times, actionsWithLogId.size)
    }

    private fun createDivTrigger(actionLogId: String): DivTrigger {
        val rawJson = """
            {
                "condition": "@{varA > 0}",
                "actions": [
                    {
                        "url": "div-action://set_variable?name=var_b&value=true",
                        "log_id": "$actionLogId"
                    }
                ]
            }
        """.trimIndent()
        return DivTrigger(env, JSONObject(rawJson))
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
