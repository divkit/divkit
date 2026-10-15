package com.yandex.div.core.view2

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.dagger.Div2Component
import com.yandex.div.core.dagger.Div2ViewComponent
import com.yandex.div.core.dagger.DivDataComponent
import com.yandex.div.core.state.DivStateManager
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.tooltip.DivTooltipController
import com.yandex.div.core.view2.state.DivStateSwitcher
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.text
import com.yandex.div2.DivData
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class BulkActionHandlerTest {

    private val stateManager = mock<DivStateManager>()
    private val dataComponent = mock<DivDataComponent> {
        on { stateManager } doReturn stateManager
    }
    private val tooltipController = mock<DivTooltipController>()
    private val div2Component = mock<Div2Component> {
        on { tooltipController } doReturn tooltipController
    }
    private val stateSwitcher = mock<DivStateSwitcher>()
    private val viewComponent = mock<Div2ViewComponent> {
        on { stateSwitcher } doReturn stateSwitcher
    }
    private val divView = mock<Div2View> {
        on { dataComponent } doReturn dataComponent
        on { div2Component } doReturn div2Component
        on { viewComponent } doReturn viewComponent
        on { currentStateId } doReturn 0
        on { childCount } doReturn 1
        on { expressionResolver } doReturn ExpressionResolver.EMPTY
    }

    private val state = DivData.State(text(text = "root"), 0)
    private val cardPath = DivStatePath.parse("0/state/second")
    private val tooltipPath = DivStatePath.fromState(0)
        .withNamespace("tooltip#1#t")
        .appendDiv("tooltip#t")
        .appendDiv("state")
        .append("state", "second", "second")

    private val underTest = BulkActionHandler(divView)

    @Test
    fun `tooltip paths are passed to tooltip controller and card paths to card state switcher`() {
        underTest.switchMultipleStates(state, listOf(cardPath, tooltipPath), temporary = true)

        verify(tooltipController).switchStates(divView, listOf(tooltipPath))
        verify(stateSwitcher).switchStates(state, ExpressionResolver.EMPTY, listOf(cardPath))
    }

    @Test
    fun `card state switcher is skipped when all paths belong to tooltips`() {
        underTest.switchState(state, tooltipPath, temporary = true)

        verify(tooltipController).switchStates(divView, listOf(tooltipPath))
        verify(stateSwitcher, never()).switchStates(any(), any(), any())
    }

    @Test
    fun `tooltip path does not switch card back to stale root state`() {
        whenever(divView.currentStateId).doReturn(1)

        underTest.switchState(state, tooltipPath, temporary = true)

        verify(tooltipController).switchStates(divView, listOf(tooltipPath))
        verify(divView, never()).switchToState(any<Long>(), any())
    }

    @Test
    fun `unclaimed tooltip paths are not passed to card state switcher`() {
        whenever(tooltipController.switchStates(any(), any())).doReturn(emptyList())

        underTest.switchMultipleStates(state, listOf(cardPath, tooltipPath), temporary = true)

        verify(stateSwitcher).switchStates(state, ExpressionResolver.EMPTY, listOf(cardPath))
    }

    @Test
    fun `tooltip paths are switched when bulk actions end`() {
        underTest.bulkActions {
            underTest.switchState(state, tooltipPath, temporary = true)
            verify(tooltipController, never()).switchStates(any(), any())
        }

        verify(stateManager).updateStates(tooltipPath, true)
        verify(tooltipController).switchStates(divView, listOf(tooltipPath))
    }
}
