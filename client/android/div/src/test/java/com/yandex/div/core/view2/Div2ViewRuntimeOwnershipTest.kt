package com.yandex.div.core.view2

import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.div.DivDataTag
import com.yandex.div.core.Disposable
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.TestComponent
import com.yandex.div.core.TestViewComponentBuilder
import com.yandex.div.core.util.binding.BindingCriticalSection
import com.yandex.div.core.util.binding.BindingDispatcher
import com.yandex.div.core.util.binding.BindingThreadExecutor
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.data.Variable
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.text
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.DivData
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask
import kotlin.test.assertEquals
import kotlin.test.AfterTest
import kotlin.test.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.robolectric.android.util.concurrent.PausedExecutorService
import org.robolectric.annotation.LooperMode

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.INSTRUMENTATION_TEST)
class Div2ViewRuntimeOwnershipTest {
    private val cardTitle = Variable.StringVariable("card_title", "Initial title")
    private val card = createCardWithVariableTitle()
    private val cardTag = DivDataTag("card")
    private val bindingTasks = PausedExecutorService()
    private val queuedBindings = ConcurrentLinkedQueue<Runnable>()
    private val bindingThread = bindingTasks.submit<Thread> { Thread.currentThread() }.run {
        bindingTasks.runNext()
        get()
    }
    private val bindingExecutor = mock<BindingThreadExecutor> {
        on { ensureThreadCreated() } doReturn bindingThread
        on { this.bindingThread } doReturn bindingThread
        on { execute(any()) } doAnswer { queuedBindings.add(it.getArgument(0)); Unit }
        on { remove(any()) } doAnswer { queuedBindings.remove(it.getArgument<Runnable>(0)) }
    }
    private val createdViews = mutableListOf<Div2View>()
    private val subscriptions = mutableListOf<Disposable>()
    private val context = onMain { createContext() }

    @Test
    fun `cached card restores variable subscriptions with its new view`() {
        val previousView = onMain { createView() }
        val cachedRuntime = previousView.bindCardAndCacheRuntime()
        val subscriptionViews = observeViewsReceivingSubscriptions(cachedRuntime)
        val newView = onMain { createView() }

        onMain { newView.setData(card, cardTag) }

        assertEquals(listOf<Div2View?>(newView), subscriptionViews)
    }

    @Test
    fun `async card does not restore variable subscriptions before binding reaches the main thread`() {
        val previousView = onMain { createView() }
        val cachedRuntime = previousView.bindCardAndCacheRuntime()
        val subscriptionViews = observeViewsReceivingSubscriptions(cachedRuntime)
        val newView = onMain { createView() }

        onMain {
            newView.setDataAsync(card, cardTag, null)
            prepareBinding()

            assertEquals(emptyList(), subscriptionViews.toList())
        }
    }

    @Test
    fun `cleaning the previous card view does not cancel subscriptions for the new view`() {
        val previousView = onMain { createView() }
        val cachedRuntime = previousView.bindCardAndCacheRuntime()
        val subscriptionViews = observeViewsReceivingSubscriptions(cachedRuntime)
        val newView = onMain { createView() }
        onMain {
            newView.setDataAsync(card, cardTag, null)
            prepareBinding()

            previousView.cleanup()
        }
        finishBinding()

        assertEquals(listOf<Div2View?>(newView), subscriptionViews)
    }

    @Test
    fun `cleaning the card being bound cancels restoration of its variable subscriptions`() {
        val previousView = onMain { createView() }
        val cachedRuntime = previousView.bindCardAndCacheRuntime()
        val subscriptionViews = observeViewsReceivingSubscriptions(cachedRuntime)
        val newView = onMain { createView() }
        onMain {
            newView.setDataAsync(card, cardTag, null)
            prepareBinding()

            newView.cleanup()
        }
        finishBinding()

        assertEquals(emptyList(), subscriptionViews)
    }

    @Test
    fun `card can show its title after an earlier async binding was cancelled`() {
        val previousView = onMain { createView() }
        previousView.bindCardAndCacheRuntime()
        val newView = onMain { createView() }
        onMain {
            newView.setDataAsync(card, cardTag, null)
            prepareBinding()
            newView.cleanup()
        }
        finishBinding()

        newView.bindAsync(card)

        assertEquals("Initial title", newView.titleText())
    }

    @Test
    fun `updating a bound card with the same runtime does not restore subscriptions again`() {
        val view = onMain { createView() }
        val runtime = onMain {
            view.setData(card, cardTag)
            view.runtimeStore
        }
        val subscriptionViews = observeViewsReceivingSubscriptions(runtime)
        val updatedCard = createCardWithVariableTitle()

        view.bindAsync(updatedCard)

        assertEquals(emptyList(), subscriptionViews)
    }

    @Test
    fun `card title receives variable updates after its cached runtime is replaced`() {
        val view = onMain { createView() }
        onMain {
            view.setData(card, cardTag)
            view.dataComponent.runtimeStoreProvider.reset()
        }
        val updatedCard = createCardWithVariableTitle()
        view.bindAsync(updatedCard)

        onMain { cardTitle.set("Updated title") }

        assertEquals("Updated title", view.titleText())
    }

    @Test
    fun `cleaning one view of a shared card keeps the other views title subscribed`() {
        val previousView = onMain { createView() }
        onMain { previousView.setData(card, cardTag) }
        val newView = onMain { createView() }
        newView.bindAsync(card)

        onMain {
            previousView.cleanup()
            cardTitle.set("Updated title")
        }

        assertEquals("Updated title", newView.titleText())
    }

    @AfterTest
    fun cleanup() {
        try {
            finishBinding()
            onMain {
                subscriptions.forEach { it.close() }
                createdViews.forEach { it.cleanup() }
            }
            finishBinding()
        } finally {
            bindingTasks.shutdownNow()
        }
    }

    private fun createContext(): Div2Context {
        val context = Div2Context(
            testContextThemeWrapper(),
            DivConfiguration.Builder(mock()).enableBindOnAttach(false).build(),
        )
        context.divVariableController.declare(cardTitle)
        val component = TestComponent(
            context.div2Component,
            viewComponentBuilder = TestViewComponentBuilder(
                context.div2Component.viewComponent(),
                bindingDispatcher = { BindingDispatcher(it, BindingCriticalSection(), bindingExecutor) },
            ),
        )
        return spy(context) { on { div2Component } doReturn component }
    }

    private fun createView() = Div2View(context).also { createdViews += it }

    private fun createCardWithVariableTitle(): DivData =
        data(text(id = "title", text = expression("@{card_title}")))

    private fun Div2View.bindCardAndCacheRuntime(): RuntimeStore = onMain {
        setData(card, cardTag)
        val cachedRuntime = runtimeStore
        cleanup()
        cachedRuntime
    }

    private fun observeViewsReceivingSubscriptions(runtime: RuntimeStore): List<Div2View?> = onMain {
        val views = CopyOnWriteArrayList<Div2View?>()
        subscriptions += runtime.rootRuntime.expressionResolver.variableController.subscribeToVariablesChange(
            names = listOf(cardTitle.name),
            invokeOnSubscription = false,
            observer = { views += runtime.viewProvider.get() },
        )
        views
    }

    private fun Div2View.bindAsync(data: DivData) {
        onMain { setDataAsync(data, cardTag, null) }
        finishBinding()
    }

    private fun Div2View.titleText(): String? = onMain {
        findViewWithTag<TextView>("title")?.text?.toString()
    }

    private fun prepareBinding() {
        bindingTasks.execute(queuedBindings.remove())
        bindingTasks.runNext()
    }

    private fun finishBinding() {
        do {
            queuedBindings.poll()?.let {
                bindingTasks.execute(it)
                bindingTasks.runNext()
            }
            onMain { Unit }
        } while (queuedBindings.isNotEmpty())
    }

    private fun <T> onMain(block: () -> T): T {
        val task = FutureTask(block)
        InstrumentationRegistry.getInstrumentation().runOnMainSync(task)
        return try {
            task.get()
        } catch (error: ExecutionException) {
            throw error.cause ?: error
        }
    }
}
