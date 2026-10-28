package com.yandex.div.core.view2

import android.app.Activity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivBindingPhase
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.TestComponent
import com.yandex.div.core.TestViewComponentBuilder
import com.yandex.div.core.dagger.Div2Component
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.EnableAssertsRule
import com.yandex.div.core.util.binding.BindingCriticalSection
import com.yandex.div.core.util.binding.BindingDispatcher
import com.yandex.div.core.util.binding.ControlledBindingExecutor
import com.yandex.div.histogram.reporter.HistogramReporter
import com.yandex.div.internal.util.Clock
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import com.yandex.div.test.testContextThemeWrapper
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.robolectric.Robolectric
import org.robolectric.annotation.LooperMode

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
internal class Div2ViewBindingHistogramsTest {
    @get:Rule
    val assertionsEnabled = EnableAssertsRule()

    private val binding = ControlledBindingExecutor()
    private val clock = TickingClock()
    private val histograms = RecordedHistograms()
    private val activity = lazy { Robolectric.buildActivity(Activity::class.java).setup() }
    private val tag = DivDataTag("binding-histograms-test")
    private lateinit var view: Div2View

    @BeforeTest
    fun setUp() {
        Clock.setForTests(clock)
        view = createView()
    }

    @AfterTest
    fun tearDown() {
        try {
            binding.settle()
            view.cleanup()
        } finally {
            binding.close()
            Clock.setForTests(null)
            if (activity.isInitialized()) {
                activity.value.pause().stop().destroy()
            }
        }
    }

    @Test
    fun `first binding reports prepare and binding parts once`() {
        view.setData(textData("First"), tag)

        assertEquals((BINDING_PARTS + PREPARE + DIV_BINDING).sorted(), histograms.names())
        assertEquals(histograms.duration(DIV_BINDING), BINDING_PARTS.sumOf(histograms::duration))
    }

    @Test
    fun `rebinding with new views reports prepare but not binding parts`() {
        view.setData(textData("First"), tag)
        histograms.clear()

        view.setData(data(container(id = "root", items = listOf(text(text = "Second")))), tag)

        assertEquals(listOf(PREPARE, DIV_REBINDING).sorted(), histograms.names())
    }

    @Test
    fun `binding deferred until attach does not report binding parts`() {
        view = createView(bindOnAttach = true)
        view.setData(textData("First"), tag)

        activity.value.get().setContentView(view)
        binding.settle()

        assertEquals(listOf(PREPARE), histograms.names().filter { it == PREPARE || it in BINDING_PARTS })
    }

    @Test
    fun `bind-on-attach binding of attached view does not report binding parts`() {
        view = createView(bindOnAttach = true)
        activity.value.get().setContentView(view)

        view.setData(textData("First"), tag)
        binding.settle()

        assertEquals(listOf(PREPARE), histograms.names().filter { it == PREPARE || it in BINDING_PARTS })
    }

    @Test
    fun `async binding reports its phases once`() {
        view.setDataAsync(textData("First"), tag, onComplete = null)
        binding.settle()

        assertEquals((ASYNC_PHASES + ASYNC_TOTAL + DIV_BINDING).sorted(), histograms.names())
        assertEquals(histograms.duration(ASYNC_TOTAL), ASYNC_PHASES.sumOf(histograms::duration))
    }

    @Test
    fun `async binding deferred until attach does not report async phases`() {
        view = createView(bindOnAttach = true)
        view.setDataAsync(textData("First"), tag, onComplete = null)
        binding.settle()

        activity.value.get().setContentView(view)
        binding.settle()

        assertEquals(emptyList(), histograms.names().filter { it in ASYNC_PHASES || it == ASYNC_TOTAL })
    }

    @Test
    fun `async bind-on-attach binding of attached view reports its phases once`() {
        view = createView(bindOnAttach = true)
        activity.value.get().setContentView(view)

        view.setDataAsync(textData("First"), tag, onComplete = null)
        binding.settle()

        assertEquals(
            (ASYNC_PHASES + ASYNC_TOTAL).sorted(),
            histograms.names().filter { it in ASYNC_PHASES || it == ASYNC_TOTAL },
        )
        assertEquals(histograms.duration(ASYNC_TOTAL), ASYNC_PHASES.sumOf(histograms::duration))
    }

    @Test
    fun `async binding with failed completion callback does not report async phases`() {
        val results = mutableListOf<Boolean>()

        view.setDataAsync(textData("First"), tag) { applied ->
            results += applied
            check(!applied) { "Completion callback failed" }
        }
        binding.settle()

        assertEquals(listOf(true, false), results)
        assertEquals(emptyList(), histograms.names().filter { it in ASYNC_PHASES || it == ASYNC_TOTAL })
    }

    @Test
    fun `async binding phases do not include completion callback`() {
        view.setDataAsync(textData("First"), tag) { clock.advance(CALLBACK_DURATION) }
        binding.settle()

        assertTrue(histograms.duration(ASYNC_TOTAL) < CALLBACK_DURATION)
    }

    @Test
    fun `async binding with states reports its phases once`() {
        view.setDataWithStatesAsync(
            textData("First"),
            tag,
            paths = listOf(DivStatePath.fromState(0)),
            temporary = true,
            onComplete = null,
        )
        binding.settle()

        assertEquals(
            (ASYNC_PHASES + ASYNC_TOTAL).sorted(),
            histograms.names().filter { it in ASYNC_PHASES || it == ASYNC_TOTAL },
        )
    }

    @Test
    fun `async binding with phase callback reports its phases once`() {
        val phases = mutableListOf<DivBindingPhase>()

        view.setDataAsync(textData("First"), tag, onBindingPhase = { phases += it })
        binding.settle()

        assertEquals(listOf(DivBindingPhase.APPLIED), phases)
        assertEquals((ASYNC_PHASES + ASYNC_TOTAL + DIV_BINDING).sorted(), histograms.names())
    }

    @Test
    fun `async binding of the same data does not report async phases`() {
        val data = textData("First")
        view.setDataAsync(data, tag, onComplete = null)
        view.setDataAsync(data, tag, onComplete = null)
        binding.settle()

        assertEquals((ASYNC_PHASES + ASYNC_TOTAL + DIV_BINDING).sorted(), histograms.names())
        histograms.clear()

        view.setDataAsync(data, tag, onComplete = null)
        binding.settle()

        assertEquals(emptyList(), histograms.names())
    }

    @Test
    fun `cancelled async binding does not report async phases`() {
        view.setDataAsync(textData("First"), tag, onComplete = null)
        binding.runNext()

        view.viewComponent.bindingDispatcher.cancelPendingTasks()
        binding.settle()

        assertEquals(listOf(DIV_BINDING), histograms.names())
    }

    private fun textData(text: String) = data(text(id = "text", text = text))

    private fun createView(bindOnAttach: Boolean = false): Div2View {
        val context = Div2Context(
            testContextThemeWrapper(),
            DivConfiguration.Builder(mock()).enableBindOnAttach(bindOnAttach).build(),
        )
        val component = TestComponent(
            HistogramComponent(context.div2Component, histograms.reporter),
            viewComponentBuilder = TestViewComponentBuilder(
                context.div2Component.viewComponent(),
                bindingDispatcher = { BindingDispatcher(it, BindingCriticalSection(), binding.executor) },
            ),
        )
        return Div2View(spy(context) { on { div2Component } doReturn component })
    }

    private class HistogramComponent(
        wrapped: Div2Component,
        private val histogramReporter: HistogramReporter,
    ) : Div2Component by wrapped {
        override fun getHistogramReporter(): HistogramReporter = histogramReporter
    }

    /**
     * Records histograms before [HistogramReporter] applies filters, so histograms that are off by
     * default are recorded too.
     */
    private class RecordedHistograms {
        private val durations = mutableListOf<Pair<String, Long>>()

        val reporter = mock<HistogramReporter> {
            on { reportDuration(any(), any(), anyOrNull(), anyOrNull(), any()) } doAnswer {
                record(it.getArgument(0), it.getArgument(1))
            }
        }

        private fun record(histogramName: String, duration: Long) {
            if (histogramName in IGNORED) return
            synchronized(this) { durations += histogramName to duration }
        }

        fun names(): List<String> = synchronized(this) { durations.map { it.first }.sorted() }

        fun duration(name: String): Long = synchronized(this) {
            durations.single { it.first == name }.second
        }

        fun clear() = synchronized(this) { durations.clear() }
    }

    private class TickingClock : Clock() {
        private var uptime = 0L

        @Synchronized
        override fun getUptimeMicros(): Long = ++uptime

        @Synchronized
        fun advance(micros: Long) {
            uptime += micros
        }
    }

    private companion object {
        const val PREPARE = "Div.Render.Prepare"
        const val DIV_BINDING = "Div.Binding"
        const val DIV_REBINDING = "Div.Rebinding"
        const val ASYNC_TOTAL = "Div.AsyncBinding.Total"
        const val CALLBACK_DURATION = 1_000_000L
        val BINDING_PARTS = setOf(
            "Div.Binding.Setup",
            "Div.Binding.Views",
            "Div.Binding.Finish",
        )
        val ASYNC_PHASES = setOf(
            "Div.AsyncBinding.BackgroundWait",
            "Div.AsyncBinding.Background",
            "Div.AsyncBinding.MainWait",
            "Div.AsyncBinding.Main",
        )
        val IGNORED = setOf("Div.View.Create", "Div.Context.Create")
    }
}
