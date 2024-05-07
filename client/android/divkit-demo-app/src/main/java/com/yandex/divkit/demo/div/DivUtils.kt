package com.yandex.divkit.demo.div

import android.app.Activity
import android.net.Uri
import android.view.ContextThemeWrapper
import androidx.lifecycle.LifecycleOwner
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivKit
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.experiments.Experiment
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.font.YandexSansDisplayDivTypefaceProvider
import com.yandex.div.font.YandexSansDivTypefaceProvider
import com.yandex.div.internal.KLog
import com.yandex.div.internal.viewpool.ViewPoolProfiler
import com.yandex.div.internal.viewpool.ViewPreCreationProfile
import com.yandex.div.json.ParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.templates.CachingTemplateProvider
import com.yandex.div.json.templates.InMemoryTemplateProvider
import com.yandex.div.json.templates.TemplateProvider
import com.yandex.div.sizeprovider.DivSizeProviderExtensionHandler
import com.yandex.div.gesture.DivGestureExtensionHandler
import com.yandex.div.gesture.ParsingErrorLoggerFactory
import com.yandex.div.video.ExoDivPlayerFactory
import com.yandex.div.video.ExoPlayerVideoPreloader
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.utils.DivkitDemoUriHandler
import com.yandex.divkit.demo.utils.MetricaUtils
import com.yandex.divkit.demo.utils.lifecycleOwner
import com.yandex.divkit.regression.ScenarioLogDelegate
import org.json.JSONObject

fun divConfiguration(
    activity: Activity,
    logDelegate: ScenarioLogDelegate = ScenarioLogDelegate.Stub
): DivConfiguration.Builder {
    val reporter = MetricaUtils.getReporter(activity)
    val flagPreferenceProvider = Container.flagPreferenceProvider
    return DivConfiguration.Builder(Container.imageLoader)
        .actionHandler(DemoDivActionHandler(Container.uriHandler.apply { handlingActivity = activity }))
        .divCustomViewFactory(DemoDivCustomViewFactory())
        .divCustomContainerViewAdapter(DemoDivCustomViewAdapter(activity, Container.videoCustomViewController))
        .div2Logger(DemoDiv2Logger(logDelegate))
        .enableVisibilityBeacons()
        .enableAccessibility(true)
        .enableLongtapActionsPassingToChild()
        .enableViewPool(
            flagPreferenceProvider.getExperimentFlag(Experiment.VIEW_POOL_ENABLED)
        )
        .viewPreCreationProfile(ViewPreCreationProfile())
        .enableViewPoolProfiling(
            flagPreferenceProvider.getExperimentFlag(Experiment.VIEW_POOL_PROFILING_ENABLED)
        )
        .enableMultipleStateChange(
            flagPreferenceProvider.getExperimentFlag(Experiment.MULTIPLE_STATE_CHANGE_ENABLED)
        )
        .enableComplexRebind(
            flagPreferenceProvider.getExperimentFlag(Experiment.COMPLEX_REBIND_ENABLED)
        )
        .tooltipRestrictor { _, _ -> true }
        .divDownloader(DemoDivDownloader())
        .typefaceProvider(YandexSansDivTypefaceProvider(activity))
        .additionalTypefaceProviders(mapOf("display" to YandexSansDisplayDivTypefaceProvider(activity)))
        .viewPoolReporter(object : ViewPoolProfiler.Reporter {
            override fun reportEvent(message: String, result: Map<String, Any>) {
                reporter.reportEvent(message, result)
            }
        })
        .supportHyphenation(true)
        .visualErrorsEnabled(true)
        .extension(DivSizeProviderExtensionHandler())
        .extension(createDivSwipeGestureExtensionHandler())
        .divPlayerFactory(ExoDivPlayerFactory(activity))
        .divPlayerPreloader(ExoPlayerVideoPreloader(activity))
}

fun createDivSwipeGestureExtensionHandler(): DivGestureExtensionHandler {
    return DivGestureExtensionHandler(
        parsingErrorLoggerFactory = object : ParsingErrorLoggerFactory {
            override fun create(key: String) = ParsingErrorLogger.ASSERT
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

    return divContext(activity, configuration, activity.lifecycleOwner)
}

fun divContext(
    baseContext: ContextThemeWrapper,
    configuration: DivConfiguration,
    lifecycleOwner: LifecycleOwner?
): Div2Context {
    return Div2Context(baseContext, configuration, themeId = R.style.Div_Theme_Demo, lifecycleOwner)
}

open class DemoDivActionHandler(private val uriHandlerDivkit: DivkitDemoUriHandler) : DivActionHandler() {
    override fun handleUri(uri: Uri, view: DivViewFacade): Boolean {
        if (super.handleUri(uri, view)) {
            return true
        }

        KLog.d(TAG) { "layoutId=${view.divTag.id}" }
        return uriHandlerDivkit.handleUri(uri)
    }

    override fun handleAction(action: DivAction, view: DivViewFacade, resolver: ExpressionResolver): Boolean {
        return super.handleAction(action, view, resolver) || action.url != null &&
                handleUri(action.url!!.evaluate(resolver), view)
    }

    private companion object {
        private const val TAG = "DemoDivActionHandler"
    }
}

fun JSONObject.isDiv2() = optJSONArray("states")?.optJSONObject(0)?.optJSONObject("div") != null

fun JSONObject.parseToDiv2(errorLogger: ParsingErrorLogger? = null): DivData {
    return if (isDiv2Data()) {
        asDiv2DataWithTemplates(errorLogger)
    } else {
        asDiv2Data(errorLogger)
    }
}

fun JSONObject.parseToDiv2List(errorLogger: ParsingErrorLogger? = null, componentName: String? = null): List<DivData> {
    return when {
        isDiv2DataList() -> asDiv2DataListWithTemplates(errorLogger, componentName)
        isDiv2Data() -> listOf(asDiv2DataWithTemplates(errorLogger, componentName))
        else -> listOf(asDiv2Data(errorLogger, componentName))
    }
}

fun JSONObject.isDiv2Data() = optJSONObject("card") != null

fun JSONObject.isDiv2DataList() = optJSONArray("cards") != null

fun JSONObject.asDiv2Data(errorLogger: ParsingErrorLogger? = null, componentName: String? = null): DivData {
    val environment = DivParsingEnvironment(
        errorLogger ?: ParsingErrorLogger.LOG,
        CachingTemplateProvider(
            InMemoryTemplateProvider(),
            TemplateProvider.empty(),
        ),
    )
    return environment.createDivDataWithHistograms(this, componentName)
}

fun JSONObject.asDiv2DataWithTemplates(errorLogger: ParsingErrorLogger? = null, componentName: String? = null): DivData {
    val templates = optJSONObject("templates")
    val card = getJSONObject("card")
    val environment = createEnvironment(errorLogger, templates, componentName)
    return environment.createDivDataWithHistograms(card, componentName)
}

fun JSONObject.asDiv2DataListWithTemplates(errorLogger: ParsingErrorLogger? = null, componentName: String? = null): List<DivData> {
    val templates = optJSONObject("templates")
    val environment = createEnvironment(errorLogger, templates, componentName)

    val cards = getJSONArray("cards")
    val divDataList = mutableListOf<DivData>()
    for (i in 0 until cards.length()) {
        divDataList.add(environment.createDivDataWithHistograms(cards.getJSONObject(i), componentName))
    }
    return divDataList
}

fun JSONObject.asDivPatchWithTemplates(errorLogger: ParsingErrorLogger? = null): DivPatch {
    val templates = optJSONObject("templates")
    val card = getJSONObject("patch")
    val environment = createEnvironment(errorLogger, templates)
    return DivPatch(environment, card)
}

private fun createEnvironment(errorLogger: ParsingErrorLogger?, templates: JSONObject?, componentName: String? = null): DivParsingEnvironment {
    val environment = DivParsingEnvironment(errorLogger ?: ParsingErrorLogger.LOG)

    templates?.let {
        environment.parseTemplatesWithHistograms(templates, componentName)
    }

    return environment
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
