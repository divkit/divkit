package com.yandex.div.compose.histogram

import android.graphics.Canvas
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.core.histogram.HistogramBridge
import com.yandex.div.core.histogram.NoOpHistogramBridge
import com.yandex.div.histogram.HistogramFilter
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.TestTimeSource
import kotlin.time.toDuration

@RunWith(AndroidJUnit4::class)
class DivViewHistogramReporterTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val histogramBridge = RecordingHistogramBridge()
    private val timeSource = TestTimeSource()

    private val configuration = divConfiguration {
        histogramConfiguration = createConfiguration(isEnabled = true)
        reporter = TestReporter()
    }

    @Test
    fun `all cold metrics are reported`() {
        val reporter = createReporter()

        rule.setContent {
            reporter.measure(prepareDuration = 1.milliseconds) { modifier ->
                timeSource += 2.milliseconds
                Box(
                    modifier = modifier
                        .size(10.dp)
                        .then(OnAttachElement { timeSource += 8.milliseconds })
                        .drawWithContent {
                            drawContent()
                            timeSource += 128.milliseconds
                        }
                )
                remember {
                    timeSource += 4.milliseconds
                    Any()
                }
                SideEffect {
                    timeSource += 16.milliseconds
                }
                DisposableEffect(Unit) {
                    timeSource += 32.milliseconds
                    onDispose {}
                }
                LaunchedEffect(Unit) {
                    timeSource += 64.milliseconds
                }
            }
        }
        drawFrame()

        assertEquals(
            listOf(
                "DivCompose.Render.Prepare.Cold" to 1_000L,
                "DivCompose.Render.Composition.Cold" to 6_000L,
                "DivCompose.Render.ApplyChanges.Cold" to 8_000L,
                "DivCompose.Render.Effects.Cold" to 32_000L,
                "DivCompose.Render.Total.Cold" to 254_000L,
            ),
            histogramBridge.histograms
        )
    }

    @Test
    fun `total is not reported again when content is redrawn`() {
        val reporter = createReporter()
        var drawDuration by mutableIntStateOf(8)

        rule.setContent {
            reporter.measure(prepareDuration = 1.milliseconds) { modifier ->
                Box(
                    modifier = modifier
                        .size(10.dp)
                        .drawWithContent {
                            drawContent()
                            timeSource += drawDuration.milliseconds
                        }
                )
            }
        }
        drawFrame()

        rule.runOnIdle { drawDuration = 16 }
        drawFrame()

        assertEquals(
            listOf("DivCompose.Render.Total.Cold" to 8_000L),
            histogramsWith("DivCompose.Render.Total.")
        )
    }

    @Test
    fun `all warm metrics are reported when modifier changes with the same data`() {
        val data = data(text(text = "Text"))
        var modifier by mutableStateOf(Modifier.size(10.dp))

        rule.setContentWithDivContext(configuration) {
            DivView(data = data, modifier = modifier)
        }
        drawFrame()

        rule.runOnIdle {
            modifier = Modifier.size(20.dp)
        }
        drawFrame()

        assertEquals(
            listOf(
                "DivCompose.Render.Prepare.Warm",
                "DivCompose.Render.Composition.Warm",
                "DivCompose.Render.ApplyChanges.Warm",
                "DivCompose.Render.Effects.Warm",
                "DivCompose.Render.Total.Warm",
            ),
            histogramsWith(".Warm").map { it.first }
        )
    }

    @Test
    fun `disposal of replaced content is reported in apply changes on recomposition`() {
        val reporter = createReporter()

        rule.setContent {
            reporter.measure(prepareDuration = 1.milliseconds) { modifier ->
                Box(modifier = modifier.size(10.dp))
                DisposableEffect(Unit) {
                    timeSource += 8.milliseconds
                    onDispose { timeSource += 4.milliseconds }
                }
            }
        }
        drawFrame()

        histogramBridge.histograms.clear()

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
            histogramBridge.histograms
        )
    }

    @Test
    fun `prepare duration is reported for cold and warm renders`() {
        val reporter = createReporter()
        var prepareDuration = 3.milliseconds

        rule.setContent {
            reporter.measure(prepareDuration) { modifier ->
                Box(modifier = modifier.size(10.dp))
            }
        }
        drawFrame()

        prepareDuration = 5.milliseconds

        reporter.forceRecomposition()
        drawFrame()

        assertEquals(
            listOf(
                "DivCompose.Render.Prepare.Cold" to 3_000L,
                "DivCompose.Render.Prepare.Warm" to 5_000L,
            ),
            histogramsWith("DivCompose.Render.Prepare.")
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
            reporter.measure(prepareDuration = 1.milliseconds) { modifier ->
                Box(modifier = modifier.size(10.dp))
            }
        }
        drawFrame()

        assertEquals(emptyList(), histogramsWith("DivCompose.Render.Prepare."))
    }

    @Test
    fun `prepare filter gets the component name`() {
        val reporter = createReporter(
            componentName = "Card",
            prepareFilter = { it == "Card" }
        )

        rule.setContent {
            reporter.measure(prepareDuration = 1.milliseconds) { modifier ->
                Box(modifier = modifier.size(10.dp))
            }
        }
        drawFrame()

        assertEquals(
            listOf("Card.DivCompose.Render.Prepare.Cold" to 1_000L),
            histogramsWith("DivCompose.Render.Prepare.")
        )
    }

    @Test
    fun `prepare filter gets null when component name is empty`() {
        val reporter = createReporter(
            componentName = "",
            prepareFilter = { it == null }
        )

        rule.setContent {
            reporter.measure(prepareDuration = 1.milliseconds) { modifier ->
                Box(modifier = modifier.size(10.dp))
            }
        }
        drawFrame()

        assertEquals(
            listOf("DivCompose.Render.Prepare.Cold" to 1_000L),
            histogramsWith("DivCompose.Render.Prepare.")
        )
    }

    @Test
    fun `content is composed without histograms when reporting is disabled`() {
        val reporter = createReporter(isEnabled = false)

        rule.setContent {
            reporter.measure(prepareDuration = 1.milliseconds) { modifier ->
                Box(
                    modifier = modifier
                        .size(10.dp)
                        .testTag("content")
                )
            }
        }
        drawFrame()

        reporter.forceRecomposition()
        drawFrame()

        rule.onNodeWithTag("content").assertExists()
        assertEquals(emptyList(), histogramBridge.histograms)
    }

    @Test
    fun `HistogramBridge receives histogram bucket settings in microseconds`() {
        val histogramBridge = mock<HistogramBridge>()
        val reporter = DivViewHistogramReporter(
            configuration = createConfiguration(
                isEnabled = true,
                histogramBridge = histogramBridge,
            ),
            timeSource = timeSource
        )

        rule.setContent {
            reporter.measure(prepareDuration = 1234.microseconds) { modifier ->
                Box(modifier = modifier.size(10.dp))
            }
        }
        drawFrame()

        verify(histogramBridge).recordTimeHistogram(
            name = "DivCompose.Render.Prepare.Cold",
            duration = 1234,
            min = 1000,
            max = 10_000_000,
            unit = DurationUnit.MICROSECONDS,
            bucketCount = 50
        )
    }

    private fun histogramsWith(name: String): List<Pair<String, Long>> {
        return histogramBridge.histograms.filter { it.first.contains(name) }
    }

    private fun drawFrame() {
        rule.runOnIdle {
            val view = rule.activity.window.decorView
            val bitmap = createBitmap(view.width, view.height)
            try {
                view.draw(Canvas(bitmap))
            } finally {
                bitmap.recycle()
            }
        }
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
