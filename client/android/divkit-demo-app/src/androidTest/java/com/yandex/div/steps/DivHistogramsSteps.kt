package com.yandex.div.steps

import android.content.Intent
import androidx.test.rule.ActivityTestRule
import com.yandex.divkit.demo.benchmark.EXTRA_ASSET_NAME
import com.yandex.divkit.demo.benchmark.EXTRA_REBIND_COUNT
import com.yandex.divkit.demo.div.histogram.HistogramDispatcher
import com.yandex.test.util.StepsDsl
import com.yandex.test.util.UiAutomator
import org.junit.Assert
import ru.tinkoff.allure.step
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

internal fun divHistograms(f: DivHistogramsSteps.() -> Unit) = DivHistogramsSteps().apply(f)

@StepsDsl
internal class DivHistogramsSteps {

    fun ActivityTestRule<*>.collectHistograms(
        dispatcher: TestHistogramDispatcher,
        histogramsType: HistogramsType,
    ): Unit =
        step("Collect $histogramsType histograms") {
            dispatcher.awaiter = HistogramsAwaiter(histogramsType.histograms)
            launchActivity(createIntent())
            UiAutomator.waitForText("Finished", TimeUnit.SECONDS.toMillis(10))
            finishActivity()
        }

    infix fun assert(f: DivHistogramsAssertions.() -> Unit) = f(DivHistogramsAssertions())
}

@StepsDsl
internal class DivHistogramsAssertions {

    fun checkCollectedHistograms(
        dispatcher: TestHistogramDispatcher,
        histogramsType: HistogramsType,
    ) = step("Check collected $histogramsType histograms") {
        dispatcher.awaitHistogramsCollected()
    }
}

internal enum class HistogramsType {
    COLD_WARM, COOL_WARM;

    val histograms: Set<String>
        get() = when (this) {
            COLD_WARM -> COLD_WARM_HISTOGRAMS
            COOL_WARM -> COOL_WARM_HISTOGRAMS
        }
}

internal class TestHistogramDispatcher : HistogramDispatcher {

    lateinit var awaiter: HistogramsAwaiter

    override fun dispatch(histogramName: String) {
        awaiter.dispatchHistogram(histogramName)
    }

    fun awaitHistogramsCollected() {
        awaiter.awaitHistograms()
    }
}

internal class HistogramsAwaiter(private val expectedHistograms: Set<String>) {

    private val histograms = ConcurrentHashMap<String, Int>()
    private val histogramsLatch = CountDownLatch(1)

    init {
        expectedHistograms.forEach { histograms[it] = 0 }
    }

    fun dispatchHistogram(histogramName: String) {
        Assert.assertTrue("$histogramName is not expected", histogramName in expectedHistograms)
        histograms.merge(histogramName, 1) { old: Int, new: Int -> old + new }
        if (histograms.values.all { it > 0 }) {
            histogramsLatch.countDown()
        }
    }

    fun awaitHistograms() {
        val result = histogramsLatch.await(10, TimeUnit.SECONDS)
        Assert.assertTrue(
            "There are not collected histograms: ${histograms.filter { it.value == 0 }}",
            result
        )
    }
}

private fun createIntent(): Intent {
    return Intent()
        .putExtra(EXTRA_ASSET_NAME, "div2-perf/benchmark-with-templates.json")
        .putExtra(EXTRA_REBIND_COUNT, 2)
}

private val COLD_WARM_HISTOGRAMS = setOf(
    "Div.Parsing.Data.Cold",
    "Div.Parsing.Data.Warm",
    "Div.Parsing.Data.Size",
    "Div.Parsing.Templates.Cold",
    "Div.Parsing.Templates.Warm",
    "Div.Parsing.Templates.Size",
    "Div.Context.Create.Cold",
    "Div.View.Create.Cold",
    "Div.View.Create.Warm",
    "Div.Binding.Cold",
    "Div.Binding.Warm",
    "Div.Rebinding.Cold",
    "Div.Rebinding.Warm",
    "Div.Render.Measure.Cold",
    "Div.Render.Measure.Warm",
    "Div.Render.Layout.Cold",
    "Div.Render.Layout.Warm",
    "Div.Render.Draw.Cold",
    "Div.Render.Draw.Warm",
    "Div.Render.Total.Cold",
    "Div.Render.Total.Warm",
    "Div.Parsing.JSON.Cold",
    "Div.Parsing.JSON.Warm",
)

private val COOL_WARM_HISTOGRAMS = setOf(
    "Div.Parsing.Data.Warm",
    "Div.Parsing.Data.Size",
    "Div.Parsing.Templates.Warm",
    "Div.Parsing.Templates.Size",
    "Div.Context.Create.Cool",
    "Div.View.Create.Cool",
    "Div.View.Create.Warm",
    "Div.Binding.Cool",
    "Div.Binding.Warm",
    "Div.Rebinding.Cool",
    "Div.Rebinding.Warm",
    "Div.Render.Measure.Cool",
    "Div.Render.Measure.Warm",
    "Div.Render.Layout.Cool",
    "Div.Render.Layout.Warm",
    "Div.Render.Draw.Cool",
    "Div.Render.Draw.Warm",
    "Div.Render.Total.Cool",
    "Div.Render.Total.Warm",
    "Div.Parsing.JSON.Warm",
)
