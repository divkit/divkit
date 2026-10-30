package com.yandex.div.core.view2

import android.app.Activity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.view2.divs.divBlock
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.text
import com.yandex.div.test.data.trigger
import com.yandex.div.test.data.variable
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.DivData
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.robolectric.Robolectric

@RunWith(AndroidJUnit4::class)
class Div2ViewRuntimeAttachTest {
    private val activity = Robolectric.buildActivity(Activity::class.java).setup()
    private val context = Div2Context(
        testContextThemeWrapper(),
        DivConfiguration.Builder(mock()).enableBindOnAttach(false).build(),
    )
    private val host = FrameLayout(activity.get()).also { activity.get().setContentView(it) }
    private val view = Div2View(context).also { host.addView(it) }
    private val tag = DivDataTag("card")
    private val title = Variable.StringVariable("title", "initial")

    @Test
    fun `cached window reattach preserves variable updates and child views`() {
        context.divVariableController.declare(title)
        val content = text(id = "title", text = expression("@{title}"))
        val card = data(container(items = listOf(content)))
        val store = bindAndTrack(card)
        val child = titleView()
        reattach()

        repeat(3) { index ->
            clearInvocations(store)
            reattach()
            title.set("updated-$index")

            verify(store, never()).getOrCreateRuntime(any(), any(), any())
            assertSame(child, titleView())
            assertEquals("updated-$index", titleView().text.toString())
        }
    }

    @Test
    fun `cached reattachment preserves nested and returning variable scopes`() {
        val first = text(id = "first", text = expression("@{label}"))
        val inner = container(
            items = listOf(text(id = "inner", text = expression("@{label}"))),
            variables = listOf(variable("label", "B")),
        )
        val last = text(id = "last", text = expression("@{label}"))
        val outer = container(
            items = listOf(first, inner, last),
            variables = listOf(variable("label", "A")),
        )
        val store = bindAndTrack(data(container(items = listOf(outer))))
        val firstView = view.findViewWithTag<TextView>("first")
        val innerView = view.findViewWithTag<TextView>("inner")
        val lastView = view.findViewWithTag<TextView>("last")
        val resolverA = requireNotNull(firstView.divBlock).expressionResolver
        val resolverB = requireNotNull(innerView.divBlock).expressionResolver
        val runtimeA = requireNotNull(store.getRuntimeWithOrNull(resolverA))
        val runtimeB = requireNotNull(store.getRuntimeWithOrNull(resolverB))
        assertSame(resolverA, requireNotNull(lastView.divBlock).expressionResolver)
        val valueA = runtimeA.expressionResolver.variableController
            .getMutableVariable("label") as Variable.StringVariable
        val valueB = runtimeB.expressionResolver.variableController
            .getMutableVariable("label") as Variable.StringVariable
        reattach()
        clearInvocations(store)

        reattach()
        valueA.set("updated-A")
        valueB.set("updated-B")

        verify(store, never()).getOrCreateRuntime(any(), any(), any())
        assertEquals("updated-A", firstView.text.toString())
        assertEquals("updated-B", innerView.text.toString())
        assertEquals("updated-A", lastView.text.toString())
    }

    @Test
    fun `same root and tag rebind preserves store and variable subscriptions`() {
        context.divVariableController.declare(title)
        val content = text(id = "title", text = expression("@{title}"))
        view.setData(data(content), tag)
        val store = view.runtimeStore
        reattach()

        view.setData(data(content, logId = "replacement"), tag)
        reattach()
        title.set("rebound")

        assertSame(store, view.runtimeStore)
        assertEquals("rebound", titleView().text.toString())
    }

    @Test
    fun `bind after cached attach traverses replacement data`() {
        val original = data(text(id = "title", text = "before"))
        bindAndTrack(original)
        reattach()
        reattach()

        val content = container(items = listOf(text(id = "title", text = "after")))
        val store = bindAndTrack(data(content))
        reattach()

        verify(store, atLeastOnce()).getOrCreateRuntime(any(), any(), any())
        assertEquals("after", titleView().text.toString())
        clearInvocations(store)
        reattach()
        verify(store, never()).getOrCreateRuntime(any(), any(), any())
    }

    @Test
    fun `context runtime reset invalidates cache even when live view keeps the same store`() {
        val store = bindAndTrack(data(text(id = "title", text = "retained")))
        reattach()

        listOf(listOf(tag), emptyList()).forEach { tags ->
            clearInvocations(store)
            reattach()
            verify(store, never()).getOrCreateRuntime(any(), any(), any())

            context.reset(Div2Context.RESET_EXPRESSION_RUNTIMES, tags)

            assertSame(store, view.runtimeStore)
            clearInvocations(store)
            reattach()
            verify(store, atLeastOnce()).getOrCreateRuntime(any(), any(), any())
            assertEquals("retained", titleView().text.toString())
        }
    }

    @Test
    fun `root trigger subscriptions resume after every window detach`() {
        checkTriggerReattach(local = false)
    }

    @Test
    fun `root state round trip can reuse the same static root and path`() {
        val content = text(id = "title", text = "shared")
        val store = bindAndTrack(data(states = listOf(
            DivData.State(stateId = 0, div = content),
            DivData.State(stateId = 1, div = content),
        )))
        reattach()

        view.switchToState(1)
        view.switchToState(0)
        clearInvocations(store)
        reattach()

        verify(store, never()).getOrCreateRuntime(any(), any(), any())
        assertEquals("shared", titleView().text.toString())
    }

    @Test
    fun `nested trigger subscriptions resume after every window detach`() {
        checkTriggerReattach(local = true)
    }

    private fun checkTriggerReattach(local: Boolean) {
        val counter = Variable.IntegerVariable("counter", 0)
        context.divVariableController.declare(counter)
        context.divVariableController.declare(title)
        val triggers = listOf(trigger(
            action = action(url = "div-action://set_variable?name=title&value=triggered"),
            condition = "@{counter > 0}",
        ))
        val card = data(
            container(items = listOf(text(
                id = "title",
                text = expression("@{title}"),
                triggers = triggers.takeIf { local },
            ))),
            triggers = triggers.takeUnless { local },
        )
        val store = bindAndTrack(card)

        repeat(3) {
            counter.set(0)
            host.removeView(view)
            title.set("detached")
            counter.set(1)
            assertEquals("detached", title.getValue())
            clearInvocations(store)

            host.addView(view)

            verify(store, atLeastOnce()).getOrCreateRuntime(any(), any(), any())
            assertEquals("triggered", titleView().text.toString())
        }
    }

    private fun bindAndTrack(card: DivData): RuntimeStore {
        view.setData(card, tag)
        return spy(view.runtimeStore).also { view.runtimeStore = it }
    }

    private fun reattach() {
        host.removeView(view)
        host.addView(view)
    }

    private fun titleView(): TextView = view.findViewWithTag("title")

    @AfterTest
    fun cleanup() {
        host.removeView(view)
        view.cleanup()
        activity.pause().stop().destroy()
    }
}
