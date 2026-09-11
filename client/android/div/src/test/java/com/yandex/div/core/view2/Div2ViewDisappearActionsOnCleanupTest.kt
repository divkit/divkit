package com.yandex.div.core.view2

import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivCustomContainerViewAdapter
import com.yandex.div.core.TestComponent
import com.yandex.div.core.TestViewComponentBuilder
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.animations.DIV_STATE_DIR
import com.yandex.div.core.view2.divs.UnitTestData
import com.yandex.div.core.view2.divs.widgets.DivHolderView
import com.yandex.div.core.view2.divs.widgets.ReleaseViewVisitor
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.test.testContextThemeWrapper
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

/**
 * Verifies that disappear actions are dispatched (not just discarded) specifically when a
 * [Div2View] is permanently torn down via [Div2View.cleanup], while other visibility-discarding
 * paths (rebind of replaceable data, state switch) keep their old cancel-only behavior.
 */
@RunWith(RobolectricTestRunner::class)
class Div2ViewDisappearActionsOnCleanupTest {
    private val testData = UnitTestData(DIV_STATE_DIR, "state_tree.json")
    private val testOtherData = UnitTestData(DIV_STATE_DIR, "state_list.json")
    private val tag = DivDataTag("tag")

    private val backingContext = Div2Context(
        baseContext = testContextThemeWrapper(),
        configuration = DivConfiguration.Builder(mock()).build()
    )
    private val viewBinder = mock<DivBinder> {
        on { bind(any(), any(), any()) } doAnswer {
            @Suppress("UNCHECKED_CAST")
            (it.arguments[0] as DivHolderView<DivBlock>).divBlock = it.arguments[1] as DivBlock
        }
    }
    private val divExtensionController = DivExtensionController(emptyList())
    private val releaseViewVisitor = spy(ReleaseViewVisitor(mock(), DivCustomContainerViewAdapter.STUB, divExtensionController))
    private val visibilityActionTracker = mock<DivVisibilityActionTracker>()
    private val component: TestComponent = TestComponent(
        wrapped = backingContext.div2Component,
        divBinder = viewBinder,
        viewComponentBuilder = TestViewComponentBuilder(
            wrapped = backingContext.div2Component.viewComponent(),
            releaseViewVisitor = releaseViewVisitor
        ),
        visibilityActionTracker = visibilityActionTracker
    )
    private val div2Context = spy(backingContext) {
        on { div2Component } doReturn component
    }
    private val divView = Div2View(div2Context)

    init {
        divView.setData(testData.data, tag)
    }

    @Test
    fun `cleanup dispatches waiting disappear actions instead of just discarding them`() {
        divView.cleanup()

        verify(visibilityActionTracker).dispatchWaitingDisappearActions(eq(divView), any(), eq(divView))
    }

    @Test
    fun `state switch does not dispatch disappear actions, only discards tracking`() {
        val state = DivStatePath.parse("0/state_container/first/container_item_one/two")

        divView.switchToState(state, false)

        verify(visibilityActionTracker, never()).dispatchWaitingDisappearActions(any(), any(), any())
    }

    @Test
    fun `rebind with non-replaceable data does not dispatch disappear actions`() {
        divView.setData(testOtherData.data, tag)

        verify(visibilityActionTracker, never()).dispatchWaitingDisappearActions(any(), any(), any())
    }

    @Test
    fun `plain detach from window (discardVisibilityTracking without dispatch flag) does not dispatch disappear actions`() {
        // Mirrors what Div2View#onDetachedFromWindow does on regular, possibly transient, detach.
        divView.discardVisibilityTracking()

        verify(visibilityActionTracker, never()).dispatchWaitingDisappearActions(any(), any(), any())
    }
}
