package com.yandex.div.core.view2

import android.os.Looper
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.TestComponent
import com.yandex.div.core.TestViewComponentBuilder
import com.yandex.div.core.util.EnableAssertsRule
import com.yandex.div.core.util.binding.BindingCriticalSection
import com.yandex.div.core.util.binding.BindingDispatcher
import com.yandex.div.core.util.binding.BindingThreadExecutor
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import com.yandex.div.test.testContextThemeWrapper
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.robolectric.android.util.concurrent.PausedExecutorService
import org.robolectric.annotation.LooperMode
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.INSTRUMENTATION_TEST)
internal class Div2ViewAsyncBindingTest {
    @get:Rule
    val enableAssertsRule = EnableAssertsRule()

    private val worker = PausedExecutorService()
    private val tasks = ConcurrentLinkedQueue<Runnable>()
    private lateinit var view: Div2View
    private val firstData = data(text(id = "first", text = "First"))
    private val otherData = data(text(id = "other", text = "Other"))
    private val tag = DivDataTag("async-same-data-test")
    private val pendingTag = DivDataTag("pending-binding-test")

    @BeforeTest
    fun setUp() {
        val thread = worker.submit<Thread> { Thread.currentThread() }
        worker.runNext()
        view = onMain { createView(thread.get()) }
    }

    @AfterTest
    fun tearDown() {
        try {
            finishBinding()
            onMain { view.cleanup() }
            finishBinding()
        } finally {
            worker.shutdownNow()
        }
    }

    @Test
    fun `same data after pending cleanup restores rendered content`() {
        onMain {
            prepareNewBinding { view.setDataAsync(firstData, tag, onComplete = null) }
            view.cleanup()
            view.setDataAsync(firstData, tag, onComplete = null)
        }
        finishBinding()

        assertEquals("First", renderedText("first"))
    }

    @Test
    fun `same data completion cannot overtake pending cleanup`() {
        var completed = false
        onMain {
            prepareNewBinding { view.setDataAsync(firstData, tag, onComplete = null) }
            view.cleanup()
            view.setDataAsync(firstData, tag) { completed = true }

            assertFalse(completed)
        }
    }

    @Test
    fun `cleanup cancels the old completion but keeps the new one`() {
        val callbacks = mutableListOf<String>()
        onMain {
            prepareNewBinding { view.setDataAsync(firstData, tag) { callbacks += "old:$it" } }
            view.cleanup()
            view.setDataAsync(firstData, tag) { callbacks += "fresh:$it" }
        }
        finishBinding()

        assertEquals(listOf("fresh:true"), callbacks)
    }

    @Test
    fun `same data queued after replacement wins over replacement`() {
        onMain { view.setData(firstData, tag) }
        queueWorkerTask()

        onMain {
            view.setDataAsync(otherData, tag, onComplete = null)
            view.setDataAsync(firstData, tag, onComplete = null)
        }
        finishBinding()

        assertEquals("First", renderedText("first"))
    }

    @Test
    fun `replacement and same data callbacks preserve FIFO`() {
        val callbacks = mutableListOf<String>()
        onMain { view.setData(firstData, tag) }
        queueWorkerTask()

        onMain {
            view.setDataAsync(otherData, tag) { callbacks += "other" }
            view.setDataAsync(firstData, tag) { callbacks += "first" }
        }
        finishBinding()

        assertEquals(listOf("other", "first"), callbacks)
    }

    @Test
    fun `repeated request completes only after content is applied`() {
        var contentOnComplete: String? = null
        onMain {
            prepareNewBinding { view.setDataAsync(firstData, tag, onComplete = null) }
            view.setDataAsync(firstData, tag) { contentOnComplete = renderedText("first") }
        }
        finishBinding()

        assertEquals("First", contentOnComplete)
    }

    @Test
    fun `idle same data retains inline completion`() {
        onMain { view.setData(firstData, tag) }

        onMain {
            var completed = false
            view.setDataAsync(firstData, tag) { completed = it }

            assertTrue(completed)
        }
    }

    @Test
    fun `idle same data reuses the existing child`() {
        onMain { view.setData(firstData, tag) }
        val initialChild = onMain { requireNotNull(view.getChildAt(0)) }

        onMain { view.setDataAsync(firstData, tag, onComplete = null) }

        assertSame(initialChild, onMain { view.getChildAt(0) })
    }

    @Test
    fun `same data after pending cleanup restores rendered content with old data`() {
        onMain {
            prepareNewBinding { view.setDataAsync(firstData, firstData, tag, onComplete = null) }
            view.cleanup()
            view.setDataAsync(firstData, firstData, tag, onComplete = null)
        }
        finishBinding()

        assertEquals("First", renderedText("first"))
    }

    @Test
    fun `same data completion cannot overtake pending cleanup with old data`() {
        var completed = false
        onMain {
            prepareNewBinding { view.setDataAsync(firstData, firstData, tag, onComplete = null) }
            view.cleanup()
            view.setDataAsync(firstData, firstData, tag) { completed = true }

            assertFalse(completed)
        }
    }

    @Test
    fun `cleanup cancels the old completion but keeps the new one with old data`() {
        val callbacks = mutableListOf<String>()
        onMain {
            prepareNewBinding { view.setDataAsync(firstData, firstData, tag) { callbacks += "old:$it" } }
            view.cleanup()
            view.setDataAsync(firstData, firstData, tag) { callbacks += "fresh:$it" }
        }
        finishBinding()

        assertEquals(listOf("fresh:true"), callbacks)
    }

    @Test
    fun `same data queued after replacement wins over replacement with old data`() {
        onMain { view.setData(firstData, tag) }
        queueWorkerTask()

        onMain {
            view.setDataAsync(otherData, firstData, tag, onComplete = null)
            view.setDataAsync(firstData, firstData, tag, onComplete = null)
        }
        finishBinding()

        assertEquals("First", renderedText("first"))
    }

    @Test
    fun `replacement and same data callbacks preserve FIFO with old data`() {
        val callbacks = mutableListOf<String>()
        onMain { view.setData(firstData, tag) }
        queueWorkerTask()

        onMain {
            view.setDataAsync(otherData, firstData, tag) { callbacks += "other" }
            view.setDataAsync(firstData, firstData, tag) { callbacks += "first" }
        }
        finishBinding()

        assertEquals(listOf("other", "first"), callbacks)
    }

    @Test
    fun `repeated request completes only after content is applied with old data`() {
        var contentOnComplete: String? = null
        onMain {
            prepareNewBinding { view.setDataAsync(firstData, firstData, tag, onComplete = null) }
            view.setDataAsync(firstData, firstData, tag) { contentOnComplete = renderedText("first") }
        }
        finishBinding()

        assertEquals("First", contentOnComplete)
    }

    @Test
    fun `idle same data retains inline completion with old data`() {
        onMain { view.setData(firstData, tag) }

        onMain {
            var completed = false
            view.setDataAsync(firstData, firstData, tag) { completed = it }

            assertTrue(completed)
        }
    }

    @Test
    fun `idle same data reuses the existing child with old data`() {
        onMain { view.setData(firstData, tag) }
        val initialChild = onMain { requireNotNull(view.getChildAt(0)) }

        onMain { view.setDataAsync(firstData, firstData, tag, onComplete = null) }

        assertSame(initialChild, onMain { view.getChildAt(0) })
    }

    @Test
    fun `queued binding is reported as pending`() {
        queueWorkerTask()

        onMain {
            view.setDataAsync(firstData, pendingTag, onComplete = null)

            assertTrue(view.viewComponent.bindingDispatcher.hasPendingAsyncBindings)
        }
    }

    @Test
    fun `unfinished main continuation is reported as pending`() {
        onMain {
            prepareMainContinuation()

            assertTrue(view.viewComponent.bindingDispatcher.hasPendingAsyncBindings)
        }
    }

    @Test
    fun `completed binding is not reported as pending`() {
        onMain { view.setDataAsync(firstData, pendingTag, onComplete = null) }
        finishBinding()

        assertFalse(onMain { view.viewComponent.bindingDispatcher.hasPendingAsyncBindings })
    }

    private fun prepareNewBinding(request: () -> Unit) {
        request()
        runNextBinding()
    }

    private fun queueWorkerTask() = onMain {
        view.viewComponent.bindingDispatcher.runOnBindingThread { Unit }
    }

    private fun prepareMainContinuation() {
        view.viewComponent.bindingDispatcher.runOnBindingThread(onComplete = { _: Unit -> }) { Unit }
        runNextBinding()
    }

    private fun finishBinding() {
        do {
            onMain { Unit }
            val task = tasks.poll()
            if (task != null) {
                worker.execute(task)
                worker.runNext()
            }
        } while (task != null)
    }

    private fun renderedText(id: String): String? = onMain {
        view.findViewWithTag<TextView>(id)?.text?.toString()
    }

    private fun <T> onMain(block: () -> T): T {
        if (Thread.currentThread() === Looper.getMainLooper().thread) return block()
        val task = FutureTask<T> { block() }
        InstrumentationRegistry.getInstrumentation().runOnMainSync(task)
        try {
            return task.get()
        } catch (error: ExecutionException) {
            throw error.cause ?: error
        }
    }

    private fun runNextBinding() {
        worker.execute(requireNotNull(tasks.poll()))
        worker.runNext()
    }

    private fun createView(thread: Thread): Div2View {
        val executor = mock<BindingThreadExecutor> {
            on { ensureThreadCreated() } doReturn thread
            on { bindingThread } doReturn thread
            on { execute(any()) } doAnswer { tasks.add(it.getArgument<Runnable>(0)); Unit }
            on { remove(any()) } doAnswer { tasks.remove(it.getArgument<Runnable>(0)) }
        }
        val context = Div2Context(
            testContextThemeWrapper(),
            DivConfiguration.Builder(mock()).enableBindOnAttach(false).build(),
        )
        val component = TestComponent(
            context.div2Component,
            viewComponentBuilder = TestViewComponentBuilder(
                context.div2Component.viewComponent(),
                bindingDispatcher = { BindingDispatcher(it, BindingCriticalSection(), executor) },
            ),
        )
        val wrapped = spy(context) { on { div2Component } doReturn component }
        return Div2View(wrapped)
    }
}
