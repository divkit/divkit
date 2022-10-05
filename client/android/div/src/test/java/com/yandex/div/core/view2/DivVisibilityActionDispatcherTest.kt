package com.yandex.div.core.view2

import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.asExpression
import com.yandex.div.core.view2.divs.DivActionBeaconSender
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivVisibilityAction
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivVisibilityActionDispatcherTest {

    private val logger = mock<Div2Logger>()
    private val beaconSender = mock<DivActionBeaconSender>()
    private val resolver = mock<ExpressionResolver>()

    private val contextActionHandler = mock<DivActionHandler> {
        on { handleAction(any<DivVisibilityAction>(), any<Div2View>()) } doReturn true
    }

    private val viewActionHandler = mock<DivActionHandler> {
        on { handleAction(any<DivVisibilityAction>(), any<Div2View>()) } doReturn true
    }

    private val divView = mock<Div2View> {
        on { logId } doReturn "card"
        on { dataTag } doReturn DivDataTag("tag")
        on { expressionResolver } doReturn resolver
    }

    private val dispatcher = DivVisibilityActionDispatcher(
        logger = logger,
        visibilityListener = mock(),
        divActionHandler = contextActionHandler,
        divActionBeaconSender = beaconSender
    )

    @Test
    fun `action dispatched to handler`() {
        val action = DivVisibilityAction(logId = "action")

        dispatcher.dispatchAction(divView, mock(), action)

        verify(contextActionHandler).handleAction(eq(action), eq(divView))
    }

    @Test
    fun `action dispatching is limited by action`() {
        val action = DivVisibilityAction(logId = "action", logLimit = 2.asExpression())

        repeat(4) {
            dispatcher.dispatchAction(divView, mock(), action)
        }

        verify(contextActionHandler, times(2)).handleAction(eq(action), eq(divView))
    }

    @Test
    fun `action dispatching restores after limit reset`() {
        val action = DivVisibilityAction(logId = "action", logLimit = 2.asExpression())

        repeat(4) {
            dispatcher.dispatchAction(divView, mock(), action)
        }
        dispatcher.reset()
        dispatcher.dispatchAction(divView, mock(), action)

        verify(contextActionHandler, times(3)).handleAction(eq(action), eq(divView))
    }

    @Test
    fun `action dispatching is unlimited by action`() {
        val action = DivVisibilityAction(logId = "action", logLimit = 0.asExpression())

        repeat(10) {
            dispatcher.dispatchAction(divView, mock(), action)
        }

        verify(contextActionHandler, times(10)).handleAction(eq(action), eq(divView))
    }

    @Test
    fun `action doesn't dispatched to context handler when view handler intercepts it`() {
        val action = DivVisibilityAction(logId = "action")
        whenever(divView.actionHandler) doReturn viewActionHandler

        dispatcher.dispatchAction(divView, mock(), action)

        verify(contextActionHandler, never()).handleAction(eq(action), eq(divView))
    }

    @Test
    fun `action dispatched to context handler when view handler passes it`() {
        val action = DivVisibilityAction(logId = "action")
        whenever(divView.actionHandler) doReturn viewActionHandler
        whenever(viewActionHandler.handleAction(eq(action), eq(divView))) doReturn false

        dispatcher.dispatchAction(divView, mock(), action)

        verify(contextActionHandler).handleAction(eq(action), eq(divView))
    }

    @Test
    fun `action doesn't dispatched to logger when handler intercepts it`() {
        val action = DivVisibilityAction(logId = "action")

        dispatcher.dispatchAction(divView, mock(), action)

        verify(logger, never()).logViewShown(eq(divView), any(), eq(action))
    }

    @Test
    fun `action doesn't dispatched to beacon sender when handler intercepts it`() {
        val action = DivVisibilityAction(logId = "action")

        dispatcher.dispatchAction(divView, mock(), action)

        verify(beaconSender, never()).sendVisibilityActionBeacon(any(), any())
    }

    @Test
    fun `action dispatched to logger when handler passes it`() {
        val action = DivVisibilityAction(logId = "action")
        whenever(contextActionHandler.handleAction(eq(action), eq(divView))) doReturn false

        dispatcher.dispatchAction(divView, mock(), action)

        verify(logger).logViewShown(eq(divView), any(), eq(action))
    }

    @Test
    fun `action dispatched to beacon sender when handler passes it`() {
        val action = DivVisibilityAction(logId = "action")
        whenever(contextActionHandler.handleAction(eq(action), eq(divView))) doReturn false

        dispatcher.dispatchAction(divView, mock(), action)

        verify(beaconSender).sendVisibilityActionBeacon(action, resolver)
    }

    @Test
    fun `dispatch actions runs as a bulk`() {
        val actionArray = arrayOf(DivVisibilityAction(logId = "action"), DivVisibilityAction(logId = "action2"))
        dispatcher.dispatchActions(divView, mock(), actionArray)
        verify(divView, times(1)).bulkActions(any())
    }
}
