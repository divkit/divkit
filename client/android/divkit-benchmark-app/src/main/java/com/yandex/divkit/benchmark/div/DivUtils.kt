package com.yandex.divkit.benchmark.div

import android.app.Activity
import android.view.ContextThemeWrapper
import androidx.lifecycle.LifecycleOwner
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivKit
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.evaluable.types.Color
import com.yandex.div.font.YandexSansDisplayDivTypefaceProvider
import com.yandex.div.font.YandexSansDivTypefaceProvider
import com.yandex.div.gesture.DivGestureExtensionHandler
import com.yandex.div.gesture.ParsingErrorLoggerFactory
import com.yandex.div.internal.Log
import com.yandex.div.internal.viewpool.ViewPoolProfiler
import com.yandex.div.internal.viewpool.ViewPreCreationProfile
import com.yandex.div.json.ParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.markdown.DivMarkdownExtensionHandler
import com.yandex.div.network.DefaultDivDownloader
import com.yandex.div.shimmer.DivShimmerExtensionHandler
import com.yandex.div.shine.DivShineExtensionHandler
import com.yandex.div.shine.DivShineLogger
import com.yandex.div.sizeprovider.DivSizeProviderExtensionHandler
import com.yandex.div.video.m3.ExoDivPlayerFactory
import com.yandex.div2.DivData
import com.yandex.divkit.benchmark.Container
import com.yandex.divkit.benchmark.R
import com.yandex.divkit.regression.ScenarioLogDelegate
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.core.MarkwonTheme
import org.json.JSONObject

fun divConfiguration(
    activity: Activity,
    logDelegate: ScenarioLogDelegate = ScenarioLogDelegate.Stub
): DivConfiguration.Builder {
    return DivConfiguration.Builder(Container.imageLoader)
        .divCustomContainerViewAdapter(DemoDivCustomViewAdapter(activity, Container.videoCustomViewController))
        .div2Logger(DemoDiv2Logger(logDelegate))
        .enableVisibilityBeacons()
        .enableAccessibility(true)
        .enableLongtapActionsPassingToChild()
        .viewPreCreationProfile(ViewPreCreationProfile())
        .tooltipRestrictor { _, _, _, _ -> true }
        .divDownloader(DefaultDivDownloader(Container.httpClient))
        .typefaceProvider(YandexSansDivTypefaceProvider(activity))
        .additionalTypefaceProviders(
            mapOf(
                "display" to YandexSansDisplayDivTypefaceProvider(activity),
            )
        )
        .viewPoolReporter(object : ViewPoolProfiler.Reporter {
            override fun reportEvent(message: String, result: Map<String, Any>) {
                Log.i("DivReporter", "$message params: $result")
            }
        })
        .supportHyphenation(true)
        .visualErrorsEnabled(true)
        .extension(DivSizeProviderExtensionHandler())
        .extension(createDivSwipeGestureExtensionHandler())
        .extension(createMarkdownExtension(activity))
        .extension(DivShimmerExtensionHandler())
        .extension(createDivShineExtensionHandler())
        .divPlayerFactory(ExoDivPlayerFactory(activity))
}

fun createDivSwipeGestureExtensionHandler(): DivGestureExtensionHandler {
    return DivGestureExtensionHandler(
        parsingErrorLoggerFactory = object : ParsingErrorLoggerFactory {
            override fun create(key: String) = ParsingErrorLogger.ASSERT
        }
    )
}

fun createDivShineExtensionHandler(): DivShineExtensionHandler {
    return DivShineExtensionHandler(
        logger = object : DivShineLogger {
            override fun logError(e: Exception) = ParsingErrorLogger.ASSERT.logError(e)
        }
    )
}

fun divContext(
    activity: Activity,
    forceDisableLogs: Boolean = false,
    configBuilder: DivConfiguration.Builder.() -> DivConfiguration.Builder = { this },
): Div2Context {
    if (forceDisableLogs) DivKit.enableLogging(false)

    val configuration = divConfiguration(activity)
        .configBuilder()
        .build()

    return divContext(activity, configuration, activity as? LifecycleOwner)
}

fun divContext(
    baseContext: ContextThemeWrapper,
    configuration: DivConfiguration,
    lifecycleOwner: LifecycleOwner?
): Div2Context {
    return Div2Context(baseContext, configuration, themeId = R.style.Div_Theme_Demo, lifecycleOwner)
}

private fun createMarkdownExtension(context: Activity): DivMarkdownExtensionHandler {
    val plugin = object : AbstractMarkwonPlugin() {
        override fun configureTheme(builder: MarkwonTheme.Builder) {
            builder.linkColor(Color.parse("#0000EE").value)
            super.configureTheme(builder)
        }
    }
    return DivMarkdownExtensionHandler(context, listOf(plugin))
}

internal fun ParsingEnvironment.createDivDataWithHistograms(data: JSONObject, componentName: String? = null): DivData {
    return Container.parsingHistogramReporter.measureDataParsing(data, componentName) {
        DivData(this, data)
    }
}

internal fun DivParsingEnvironment.parseTemplatesWithHistograms(templates: JSONObject, componentName: String? = null) {
    Container.parsingHistogramReporter.measureTemplatesParsing(templates, componentName) {
        parseTemplates(templates)
    }
}

internal fun String.toJSONObjectWithHistograms(componentName: String? = null): JSONObject {
    return Container.parsingHistogramReporter.measureJsonParsing(componentName) {
        JSONObject(this)
    }
}
