package com.yandex.div.core.view2

import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.DivBase
import com.yandex.div2.DivExtension
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.LooperMode
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.INSTRUMENTATION_TEST)
class DivDeferredExtensionBindingTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val mainThread = Looper.getMainLooper().thread
    private val preparationThreads = CopyOnWriteArrayList<Thread>()
    private val bindingThreads = mutableListOf<Thread>()
    private val boundTexts = mutableListOf<String>()
    private val secondItemPrepared = CountDownLatch(1)
    private lateinit var divView: Div2View

    @Before
    fun setUp() = instrumentation.runOnMainSync {
        val handler = object : DivExtensionHandler {
            override fun matches(div: DivBase) = div.extensions?.any { it.id == EXTENSION } == true

            override fun beforeBindView(
                divView: Div2View,
                expressionResolver: ExpressionResolver,
                view: View,
                div: DivBase,
            ) {
                preparationThreads += Thread.currentThread()
                if (div.id == "second") secondItemPrepared.countDown()
            }

            override fun bindView(
                divView: Div2View,
                expressionResolver: ExpressionResolver,
                view: View,
                div: DivBase,
            ) {
                bindingThreads += Thread.currentThread()
                boundTexts += (view as TextView).text.toString()
            }

            override fun unbindView(
                divView: Div2View,
                expressionResolver: ExpressionResolver,
                view: View,
                div: DivBase,
            ) = Unit
        }
        val configuration = DivConfiguration.Builder(mock())
            .enableBindOnAttach(false)
            .extension(handler)
            .build()
        divView = Div2View(Div2Context(testContextThemeWrapper(), configuration))
    }

    @After
    fun tearDown() = instrumentation.runOnMainSync { divView.cleanup() }

    @Test
    fun `async binding prepares the next item while main is busy and applies extensions before completion`() {
        val completed = CountDownLatch(1)
        var success = false
        var textsAtCompletion = emptyList<String>()
        var preparedWhileMainBusy = false

        instrumentation.runOnMainSync {
            divView.setDataAsync(content(), TAG) {
                success = it
                textsAtCompletion = boundTexts.toList()
                completed.countDown()
            }
            // A blocking handoff at the first extension prevents preparation of the second item.
            preparedWhileMainBusy = secondItemPrepared.await(5, TimeUnit.SECONDS)
        }

        // Release main and let binding finish even if the regression guard timed out.
        assertTrue(completed.await(5, TimeUnit.SECONDS), "Async binding did not complete")
        assertTrue(preparedWhileMainBusy, "The first extension blocked the binding worker on main")
        assertTrue(preparationThreads.size == 2 && preparationThreads.all { it !== mainThread })
        assertEquals(listOf(mainThread, mainThread), bindingThreads)
        assertTrue(success)
        assertEquals(listOf("First", "Second"), textsAtCompletion)
    }

    @Test
    fun `synchronous binding applies extensions on main before setData returns`() {
        instrumentation.runOnMainSync {
            assertTrue(divView.setData(content(), TAG))

            assertEquals(listOf(mainThread, mainThread), preparationThreads)
            assertEquals(listOf(mainThread, mainThread), bindingThreads)
            assertEquals(listOf("First", "Second"), boundTexts)
        }
    }

    private fun content() = data(container(items = listOf(
        text(id = "first", text = "First", extensions = listOf(DivExtension(id = EXTENSION))),
        text(id = "second", text = "Second", extensions = listOf(DivExtension(id = EXTENSION))),
    )))

    private companion object {
        const val EXTENSION = "test-extension"
        val TAG = DivDataTag("extension-binding")
    }
}
