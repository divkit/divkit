package com.yandex.div.core.view2

import android.app.Activity
import android.view.View
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivKit
import com.yandex.div.core.TestComponent
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.json.missingVariable
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.text
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivDisappearAction
import com.yandex.div2.DivExtension
import com.yandex.div2.DivGrid
import com.yandex.div2.DivSightAction
import com.yandex.div2.DivVisibility
import com.yandex.div2.DivVisibilityAction
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doCallRealMethod
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowChoreographer
import org.robolectric.shadows.ShadowLooper
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class DivVisibilityBindingBatchTest {
    private val tag = DivDataTag("visibility-batch")
    private val appearAction = DivVisibilityAction(
        logId = constant("appear"), logLimit = constant(0L), visibilityDuration = constant(0L),
    )
    private val disappearAction = DivDisappearAction(
        logId = constant("disappear"), logLimit = constant(0L), disappearDuration = constant(0L),
    )
    private val appearActions = listOf(appearAction)
    private val disappearActions = listOf(disappearAction)
    private val trackedText = text(
        id = TRACKED_TEXT_ID, text = constant("Tracked item"), width = fixed(100), height = fixed(40),
        visibility = visibilityExpression("@{$VISIBILITY_VARIABLE}"),
        visibilityActions = appearActions, disappearActions = disappearActions,
    )
    private val tracker = spy(DivVisibilityActionTracker(ViewVisibilityCalculator()))
    private val actionHandler = mock<DivActionHandler>()
    private val extension = mock<DivExtensionHandler> {
        on { matches(any()) } doAnswer { it.getArgument<DivBase>(0).id == NESTED_CONTAINER_ID }
    }
    private val backingContext = Div2Context(
        testContextThemeWrapper(),
        DivConfiguration.Builder(mock())
            .enableBindOnAttach(false)
            .extension(extension)
            .actionHandler(actionHandler)
            .build(),
    )
    private val component = TestComponent(backingContext.div2Component, visibilityActionTracker = tracker)
    private val context = spy(backingContext) {
        on { div2Component } doReturn component
    }
    private val divView = Div2View(context)
    private var activity: ActivityController<Activity>? = null

    @Before
    fun setUp() {
        ShadowChoreographer.setPaused(true)
        divView.setData(containerData(DivVisibility.VISIBLE), tag)
        clearInvocations(tracker, extension)
    }

    @After
    fun tearDown() {
        try {
            reset(tracker, extension)
            divView.cleanup()
            activity?.pause()?.stop()?.destroy()
            ShadowLooper.idleMainLooper()
        } finally {
            DivKit.resetSingletonForTesting()
        }
    }

    @Test
    fun `initial binding makes all children visible`() {
        val visibility = listOf(TRACKED_TEXT_ID, GRID_TEXT_ID, NESTED_TEXT_ID).map {
            divView.findViewWithTag<View>(it).visibility
        }

        assertEquals(listOf(View.VISIBLE, View.VISIBLE, View.VISIBLE), visibility)
    }

    @Test
    fun `recursive container rebind tracks visibility once`() {
        divView.setData(containerData(DivVisibility.INVISIBLE), tag)

        verify(tracker).trackVisibilityActionsOf(
            eq(divView), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
        )
    }

    @Test
    fun `recursive container rebind tracks visibility after child extensions finish`() {
        divView.setData(containerData(DivVisibility.INVISIBLE), tag)

        inOrder(extension, tracker) {
            verify(extension).bindView(eq(divView), any(), any(), any())
            verify(tracker).trackVisibilityActionsOf(
                eq(divView), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
            )
        }
    }

    @Test
    fun `recursive container rebind updates all child visibility`() {
        divView.setData(containerData(DivVisibility.INVISIBLE), tag)

        val visibility = listOf(GRID_TEXT_ID, NESTED_TEXT_ID).map {
            divView.findViewWithTag<View>(it).visibility
        }
        assertEquals(listOf(View.INVISIBLE, View.INVISIBLE), visibility)
    }

    @Test
    fun `grid rebind tracks visibility once`() {
        divView.setData(
            data(grid(DivVisibility.VISIBLE), variables = listOf(variable(VISIBILITY_VARIABLE, VISIBLE))),
            tag,
        )
        clearInvocations(tracker)

        divView.setData(
            data(grid(DivVisibility.INVISIBLE), variables = listOf(variable(VISIBILITY_VARIABLE, VISIBLE))),
            tag,
        )

        verify(tracker).trackVisibilityActionsOf(
            eq(divView), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
        )
    }

    @Test
    fun `visibility variable hides the child outside binding`() {
        divView.setVariable(VISIBILITY_VARIABLE, GONE)

        assertEquals(View.GONE, divView.findViewWithTag<View>(TRACKED_TEXT_ID).visibility)
        verify(tracker).trackVisibilityActionsOf(
            eq(divView), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
        )
    }

    @Test
    fun `visibility variable shows the child outside binding`() {
        divView.setVariable(VISIBILITY_VARIABLE, GONE)
        clearInvocations(tracker)

        divView.setVariable(VISIBILITY_VARIABLE, VISIBLE)

        assertEquals(View.VISIBLE, divView.findViewWithTag<View>(TRACKED_TEXT_ID).visibility)
        verify(tracker).trackVisibilityActionsOf(
            eq(divView), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
        )
    }

    @Test
    fun `hiding an attached child dispatches its disappearance`() {
        attachView()
        clearInvocations(actionHandler)

        divView.setVariable(VISIBILITY_VARIABLE, GONE)
        ShadowLooper.idleMainLooper(1, TimeUnit.SECONDS)

        verify(actionHandler).handleAction(eq<DivSightAction>(disappearAction), eq(divView), any())
    }

    @Test
    fun `showing a hidden child dispatches its appearance`() {
        attachView()
        divView.setVariable(VISIBILITY_VARIABLE, GONE)
        ShadowLooper.idleMainLooper(1, TimeUnit.SECONDS)
        clearInvocations(actionHandler)

        divView.setVariable(VISIBILITY_VARIABLE, VISIBLE)
        ShadowLooper.idleMainLooper(1, TimeUnit.SECONDS)

        verify(actionHandler).handleAction(eq<DivSightAction>(appearAction), eq(divView), any())
    }

    @Test
    fun `a batch tracks only the final visibility`() {
        divView.withBatchedVisibilityTracking {
            divView.setVariable(VISIBILITY_VARIABLE, GONE)
            divView.setVariable(VISIBILITY_VARIABLE, VISIBLE)
            verify(tracker, never()).trackVisibilityActionsOf(
                eq(divView), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
            )
        }

        verify(tracker).trackVisibilityActionsOf(
            eq(divView), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
        )
    }

    @Test
    fun `discard cancels a pending scan`() {
        divView.withBatchedVisibilityTracking {
            divView.trackChildrenVisibility()
            divView.discardVisibilityTracking()
            clearInvocations(tracker)
        }
        ShadowLooper.idleMainLooper(1, TimeUnit.SECONDS)

        verify(tracker, never()).trackVisibilityActionsOf(
            eq(divView), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
        )
    }

    @Test
    fun `tracking still works after a discarded batch`() {
        divView.withBatchedVisibilityTracking {
            divView.trackChildrenVisibility()
            divView.discardVisibilityTracking()
        }
        clearInvocations(tracker)

        divView.trackChildrenVisibility()

        verify(tracker).trackVisibilityActionsOf(
            eq(divView), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
        )
    }

    @Test
    fun `discard queued behind background binding does not flush an extra scan`() {
        val started = CountDownLatch(1)
        val resume = CountDownLatch(1)
        val completed = CountDownLatch(1)
        val failure = AtomicReference<Throwable?>()
        val dispatcher = divView.viewComponent.bindingDispatcher
        dispatcher.runOnBindingThread<Unit>(onError = { failure.set(it) }) {
            started.countDown()
            check(resume.await(5, TimeUnit.SECONDS))
        }
        try {
            assertTrue(started.await(5, TimeUnit.SECONDS))
            divView.withBatchedVisibilityTracking {
                divView.trackChildrenVisibility()
                divView.discardVisibilityTracking()
            }
            dispatcher.runOnBindingThread<Unit>(
                onComplete = { completed.countDown() },
                onError = { failure.set(it); completed.countDown() },
            ) {}
        } finally {
            resume.countDown()
        }
        drainBinding(completed)

        assertEquals(0L, completed.count)
        assertNull(failure.get())
        verify(tracker).trackVisibilityActionsOf(
            eq(divView), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
        )
    }

    @Test
    fun `cleanup drops a pending scan`() {
        divView.withBatchedVisibilityTracking {
            divView.trackChildrenVisibility()
            divView.cleanup()
            clearInvocations(tracker)
        }

        verify(tracker, never()).trackVisibilityActionsOf(
            eq(divView), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
        )
    }

    @Test
    fun `cleanup dispatches a waiting disappearance only once`() {
        attachView()
        clearInvocations(actionHandler)

        divView.withBatchedVisibilityTracking {
            divView.setVariable(VISIBILITY_VARIABLE, GONE)
            divView.cleanup()
        }
        ShadowLooper.idleMainLooper(1, TimeUnit.SECONDS)
        divView.cleanup()

        verify(actionHandler).handleAction(eq<DivSightAction>(disappearAction), eq(divView), any())
    }

    @Test
    fun `replacing data drops the old pending scan`() {
        divView.withBatchedVisibilityTracking {
            divView.trackChildrenVisibility()
            divView.setData(containerData(DivVisibility.INVISIBLE), tag)
            clearInvocations(tracker)
        }

        verify(tracker, never()).trackVisibilityActionsOf(
            eq(divView), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
        )
    }

    @Test
    fun `a replaced card can request tracking before the old batch exits`() {
        divView.withBatchedVisibilityTracking {
            divView.trackChildrenVisibility()
            divView.setData(containerData(DivVisibility.INVISIBLE), tag)
            clearInvocations(tracker)

            divView.trackChildrenVisibility()

            verify(tracker).trackVisibilityActionsOf(
                eq(divView), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
            )
        }
    }

    @Test
    fun `restoring the same data after cleanup does not restore the old batch`() {
        val originalData = divView.divData
        divView.withBatchedVisibilityTracking {
            divView.trackChildrenVisibility()
            divView.cleanup()
            divView.setData(originalData, tag)
            clearInvocations(tracker)
            divView.trackChildrenVisibility()
        }

        verify(tracker).trackVisibilityActionsOf(
            eq(divView), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
        )
    }

    @Test
    fun `a failed batch propagates the exception without scanning partial visibility`() {
        val failure = IllegalStateException("Binding failed")

        val thrown = assertFailsWith<IllegalStateException> {
            divView.withBatchedVisibilityTracking {
                divView.trackChildrenVisibility()
                throw failure
            }
        }

        assertSame(failure, thrown)
        verify(tracker, never()).trackVisibilityActionsOf(
            eq(divView), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
        )
    }

    @Test
    fun `visibility tracking recovers after a failed batch`() {
        assertFailsWith<IllegalStateException> {
            divView.withBatchedVisibilityTracking {
                divView.trackChildrenVisibility()
                throw IllegalStateException("Binding failed")
            }
        }
        clearInvocations(tracker)

        divView.setData(containerData(DivVisibility.GONE), tag)

        verify(tracker).trackVisibilityActionsOf(
            eq(divView), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
        )
    }

    @Test
    fun `expression failure in the final scan does not leave tracking batched`() {
        doThrow(missingVariable("missing_at_visibility_flush")).whenever(tracker).trackVisibilityActionsOf(
            any(), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
        )
        divView.setData(containerData(DivVisibility.INVISIBLE), tag)
        verify(tracker).trackVisibilityActionsOf(
            eq(divView), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
        )
        doCallRealMethod().whenever(tracker).trackVisibilityActionsOf(
            any(), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
        )
        clearInvocations(tracker)

        divView.trackChildrenVisibility()

        verify(tracker).trackVisibilityActionsOf(
            eq(divView), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
        )
    }

    @Test
    fun `asynchronous initial binding applies child visibility`() {
        divView.cleanup()
        val completed = CountDownLatch(1)

        divView.setDataAsync(containerData(DivVisibility.GONE), tag) { completed.countDown() }
        drainBinding(completed)

        assertEquals(0L, completed.count)
        assertEquals(View.GONE, divView.findViewWithTag<View>(NESTED_TEXT_ID).visibility)
    }

    @Test
    fun `asynchronous rebind tracks visibility once`() {
        val completed = CountDownLatch(1)

        divView.setDataAsync(containerData(DivVisibility.INVISIBLE), tag) { completed.countDown() }
        drainBinding(completed)

        assertEquals(0L, completed.count)
        verify(tracker).trackVisibilityActionsOf(
            eq(divView), any(), anyOrNull(), eq(trackedText), eq(appearActions), eq(disappearActions),
        )
    }

    private fun containerData(visibility: DivVisibility) = data(
        container(
            id = "root", width = fixed(200), height = fixed(200),
            items = listOf(
                grid(visibility),
                container(
                    id = NESTED_CONTAINER_ID, width = fixed(200), height = fixed(80),
                    extensions = listOf(DivExtension(id = "batch-test")),
                    items = listOf(text(id = NESTED_TEXT_ID, text = "Nested", visibility = constant(visibility))),
                ),
            ),
        ),
        variables = listOf(variable(VISIBILITY_VARIABLE, VISIBLE)),
    )

    private fun grid(visibility: DivVisibility) = Div.Grid(DivGrid(
        id = "grid", width = fixed(200), height = fixed(80), columnCount = constant(2L),
        items = listOf(
            trackedText,
            text(id = GRID_TEXT_ID, text = "Grid child", visibility = constant(visibility)),
        ),
    ))

    private fun attachView() {
        activity = Robolectric.buildActivity(Activity::class.java).create().start().resume().also {
            it.get().window.setLayout(300, 300)
            it.get().setContentView(divView)
            it.visible()
        }
        ShadowLooper.idleMainLooper(1, TimeUnit.SECONDS)
    }

    private fun drainBinding(completed: CountDownLatch) {
        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
        while (completed.count != 0L && System.nanoTime() < deadline) {
            ShadowLooper.idleMainLooper()
            completed.await(10, TimeUnit.MILLISECONDS)
        }
    }

    private companion object {
        const val TRACKED_TEXT_ID = "tracked"
        const val GRID_TEXT_ID = "grid-text"
        const val NESTED_TEXT_ID = "nested-text"
        const val NESTED_CONTAINER_ID = "nested"
        const val VISIBILITY_VARIABLE = "visibility"
        const val VISIBLE = "visible"
        const val GONE = "gone"
    }
}
