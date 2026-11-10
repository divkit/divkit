package com.yandex.div.compose.histogram

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.core.histogram.HistogramBridge
import com.yandex.div.core.histogram.NoOpHistogramBridge
import com.yandex.div.histogram.HistogramFilter
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mockito.CALLS_REAL_METHODS
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.TestTimeSource
import kotlin.time.toDuration
import com.yandex.div.histogram.HistogramBridge as LegacyHistogramBridge

@RunWith(AndroidJUnit4::class)
class DivViewHistogramReporterTest {

    @get:Rule
    val rule = createComposeRule()

    private val timeSource = TestTimeSource()
    private val histogramBridge = RecordingHistogramBridge()
    private lateinit var view: View

    @Test
    fun `apply changes and effects are reported separately`() {
        val reporter = createReporter()

        rule.setContent {
            view = LocalView.current
            reporter.measure(prepareDuration = 1.milliseconds) {
                timeSource += 2.milliseconds
                Box(modifier = Modifier.then(OnAttachElement { timeSource += 4.milliseconds }))
                remember { OnRememberedObserver { timeSource += 8.milliseconds } }
                DisposableEffect(Unit) {
                    timeSource += 16.milliseconds
                    onDispose {}
                }
                SideEffect { timeSource += 32.milliseconds }
            }
        }
        drawFrame()

        assertEquals(
            listOf(
                "DivCompose.Render.Prepare.Cold" to 1_000L,
                "DivCompose.Render.Composition.Cold" to 2_000L,
                "DivCompose.Render.ApplyChanges.Cold" to 4_000L,
                "DivCompose.Render.Effects.Cold" to 24_000L,
                "DivCompose.Render.Total.Cold" to 62_000L,
            ),
            histogramBridge.histograms
        )
    }

    @Test
    fun `disposal of replaced content is reported in apply changes on recomposition`() {
        val reporter = createReporter()

        rule.setContent {
            view = LocalView.current
            reporter.measure(prepareDuration = 1.milliseconds) {
                DisposableEffect(Unit) {
                    timeSource += 8.milliseconds
                    onDispose { timeSource += 4.milliseconds }
                }
            }
        }
        drawFrame()

        reporter.forceRecomposition()
        drawFrame()

        assertEquals(
            listOf(
                "DivCompose.Render.Prepare.Warm" to 1_000L,
                "DivCompose.Render.Composition.Warm" to 0L,
                "DivCompose.Render.ApplyChanges.Warm" to 4_000L,
                "DivCompose.Render.Effects.Warm" to 8_000L,
                "DivCompose.Render.Total.Warm" to 12_000L,
            ),
            histogramBridge.histograms.filter { it.first.endsWith(".Warm") }
        )
    }

    @Test
    fun `prepare duration is reported for cold and warm renders`() {
        val reporter = createReporter()
        var prepareDuration = 3.milliseconds

        rule.setContent {
            view = LocalView.current
            reporter.measure(prepareDuration) {
                Box(modifier = Modifier.size(10.dp))
            }
        }
        drawFrame()

        prepareDuration = 5.microseconds
        reporter.forceRecomposition()
        drawFrame()

        assertEquals(
            listOf(
                "DivCompose.Render.Prepare.Cold" to 3_000L,
                "DivCompose.Render.Prepare.Warm" to 5L,
            ),
            histogramBridge.histograms.filter { it.first.startsWith("DivCompose.Render.Prepare.") }
        )
    }

    @Test
    fun `prepare is not reported by default`() {
        val reporter = DivViewHistogramReporter(
            configuration = object : DivHistogramConfiguration {
                override val isEnabled = true
                override val componentName = ""
                override val histogramBridge = this@DivViewHistogramReporterTest.histogramBridge
            },
            timeSource = timeSource
        )

        rule.setContent {
            view = LocalView.current
            reporter.measure(prepareDuration = 1.milliseconds) {
                Box(modifier = Modifier.size(10.dp))
            }
        }
        drawFrame()

        assertEquals(
            listOf("Composition", "ApplyChanges", "Effects", "Total").map {
                "DivCompose.Render.$it.Cold"
            },
            histogramBridge.histograms.map { it.first }
        )
    }

    @Test
    fun `prepare filter gets the component name`() {
        val reporter = createReporter(
            componentName = "Card",
            prepareFilter = { it == "Card" }
        )

        rule.setContent {
            view = LocalView.current
            reporter.measure(prepareDuration = 1.milliseconds) {
                Box(modifier = Modifier.size(10.dp))
            }
        }
        drawFrame()

        assertEquals(
            listOf("Card.DivCompose.Render.Prepare.Cold" to 1_000L),
            histogramBridge.histograms.filter { it.first.contains(".Prepare.") }
        )
    }

    @Test
    fun `prepare filter gets null when component name is empty`() {
        val reporter = createReporter(
            componentName = "",
            prepareFilter = { it == null }
        )

        rule.setContent {
            view = LocalView.current
            reporter.measure(prepareDuration = 1.milliseconds) {
                Box(modifier = Modifier.size(10.dp))
            }
        }
        drawFrame()

        assertEquals(
            listOf("DivCompose.Render.Prepare.Cold" to 1_000L),
            histogramBridge.histograms.filter { it.first.contains(".Prepare.") }
        )
    }

    @Test
    fun `DivView reports prepare before other render histograms`() {
        val divContext = DivContext(
            baseContext = ApplicationProvider.getApplicationContext(),
            configuration = DivConfiguration(
                histogramConfiguration = createConfiguration(isEnabled = true),
                reporter = TestReporter()
            )
        )
        val data = data(text(text = "Text"))

        rule.setContent {
            view = LocalView.current
            CompositionLocalProvider(LocalContext provides divContext) {
                DivView(data = data)
            }
        }
        drawFrame()

        divContext.debugFeatures.forceRecomposition(data)
        drawFrame()

        assertEquals(
            listOf("Cold", "Warm").flatMap { state ->
                listOf("Prepare", "Composition", "ApplyChanges", "Effects", "Total").map {
                    "DivCompose.Render.$it.$state"
                }
            },
            histogramBridge.histograms.map { it.first }
        )
    }

    @Test
    fun `content is composed without histograms when reporting is disabled`() {
        val reporter = createReporter(isEnabled = false)

        rule.setContent {
            view = LocalView.current
            reporter.measure(prepareDuration = 1.milliseconds) {
                Box(modifier = Modifier.size(10.dp).testTag("content"))
            }
        }
        drawFrame()

        reporter.forceRecomposition()
        drawFrame()

        rule.onNodeWithTag("content").assertExists()
        assertEquals(emptyList(), histogramBridge.histograms)
    }

    @Test
    fun `core bridge receives histogram bucket settings in microseconds`() {
        val bridge = mock<HistogramBridge>()
        val reporter = DivViewHistogramReporter(
            configuration = createConfiguration(isEnabled = true, histogramBridge = bridge),
            timeSource = timeSource
        )

        rule.setContent {
            view = LocalView.current
            reporter.measure(prepareDuration = 1234.microseconds) {}
        }
        drawFrame()

        verify(bridge).recordTimeHistogram(
            name = "DivCompose.Render.Prepare.Cold",
            duration = 1234,
            min = 1000,
            max = 10_000_000,
            unit = DurationUnit.MICROSECONDS,
            bucketCount = 50
        )
    }

    @Test
    fun `legacy bridge receives render histograms through its TimeUnit callback`() {
        val bridge = mock<LegacyHistogramBridge>(defaultAnswer = CALLS_REAL_METHODS)
        val reporter = DivViewHistogramReporter(
            configuration = createConfiguration(isEnabled = true, histogramBridge = bridge),
            timeSource = timeSource
        )

        rule.setContent {
            view = LocalView.current
            reporter.measure(prepareDuration = 1234.microseconds) {}
        }
        drawFrame()

        verify(bridge).recordTimeHistogram(
            name = "DivCompose.Render.Prepare.Cold",
            duration = 1234,
            min = 1000,
            max = 10_000_000,
            unit = TimeUnit.MICROSECONDS,
            bucketCount = 50
        )
    }

    private fun drawFrame() {
        rule.runOnIdle { view.viewTreeObserver.dispatchOnDraw() }
        rule.waitForIdle()
    }

    private fun createReporter(
        isEnabled: Boolean = true,
        componentName: String = "",
        prepareFilter: HistogramFilter = HistogramFilter.ON,
    ): DivViewHistogramReporter {
        return DivViewHistogramReporter(
            configuration = createConfiguration(isEnabled, componentName, prepareFilter),
            timeSource = timeSource
        )
    }

    private fun createConfiguration(
        isEnabled: Boolean,
        componentName: String = "",
        prepareFilter: HistogramFilter = HistogramFilter.ON,
        histogramBridge: HistogramBridge = this.histogramBridge,
    ): DivHistogramConfiguration {
        return object : DivHistogramConfiguration {
            override val isEnabled = isEnabled
            override val componentName = componentName
            override val histogramBridge = histogramBridge
            override val prepareFilter = prepareFilter
        }
    }

    private class RecordingHistogramBridge : HistogramBridge by NoOpHistogramBridge() {
        val histograms = mutableListOf<Pair<String, Long>>()

        override fun recordTimeHistogram(
            name: String,
            duration: Long,
            min: Long,
            max: Long,
            unit: DurationUnit,
            bucketCount: Int
        ) {
            histograms += name to duration.toDuration(unit).inWholeMicroseconds
        }
    }

    private class OnRememberedObserver(private val onRemembered: () -> Unit) : RememberObserver {
        override fun onRemembered() = onRemembered.invoke()
        override fun onForgotten() = Unit
        override fun onAbandoned() = Unit
    }

    private class OnAttachElement(
        private val onAttach: () -> Unit
    ) : ModifierNodeElement<OnAttachNode>() {
        override fun create() = OnAttachNode(onAttach)
        override fun update(node: OnAttachNode) = Unit
        override fun equals(other: Any?) = other === this
        override fun hashCode() = System.identityHashCode(this)
    }

    private class OnAttachNode(private val onAttach: () -> Unit) : Modifier.Node() {
        override fun onAttach() = onAttach.invoke()
    }
}
