package com.yandex.divkit.demo.div

import android.app.Activity
import android.net.Uri
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivStateChangeListener
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.experiments.Experiment
import com.yandex.div.core.util.KLog
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.font.YandexSansDisplayDivTypefaceProvider
import com.yandex.div.font.YandexSansDivTypefaceProvider
import com.yandex.div.json.ParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.templates.InMemoryTemplateProvider
import com.yandex.div.json.templates.MainTemplateProvider
import com.yandex.div.json.templates.TemplateProvider
import com.yandex.div.view.pooling.ViewPoolProfiler
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.utils.DivkitDemoUriHandler
import com.yandex.divkit.demo.utils.MetricaUtils
import com.yandex.divkit.regression.ScenarioLogDelegate
import org.json.JSONObject

object DivUtils {

    fun createDivConfiguration(
        activity: Activity,
        divStateChangeListener: DivStateChangeListener? = null,
        logDelegate: ScenarioLogDelegate = ScenarioLogDelegate.Stub
    ): DivConfiguration.Builder {
        val reporter = MetricaUtils.getReporter(activity)
        val flagPreferenceProvider = Container.flagPreferenceProvider
        return DivConfiguration.Builder(Container.imageLoader)
            .actionHandler(DemoDivActionHandler(Container.uriHandler.apply { handlingActivity = activity }))
            .divStateChangeListener(divStateChangeListener ?: DivStateChangeListener.STUB)
            .divCustomViewFactory(DemoDivCustomViewFactory())
            .divCustomViewAdapter(DemoDivCustomViewAdapter())
            .div2Logger(DemoDiv2Logger(logDelegate))
            .enableVisibilityBeacons()
            .enableLongtapActionsPassingToChild()
            .enableViewPool(
                flagPreferenceProvider.getExperimentFlag(Experiment.VIEW_POOL_ENABLED)
            )
            .enableViewPoolProfiling(
                flagPreferenceProvider.getExperimentFlag(Experiment.VIEW_POOL_PROFILING_ENABLED)
            )
            .enableMultipleStateChange(
                flagPreferenceProvider.getExperimentFlag(Experiment.MULTIPLE_STATE_CHANGE_ENABLED)
            )
            .tooltipRestrictor { _, _ -> true }
            .divDownloader(DemoDivDownloader())
            .typefaceProvider(YandexSansDivTypefaceProvider(activity))
            .displayTypefaceProvider(YandexSansDisplayDivTypefaceProvider(activity))
            .viewPoolReporter(object : ViewPoolProfiler.Reporter {
                override fun reportEvent(message: String, result: Map<String, Any>) {
                    reporter.reportEvent(message, result)
                }
            })
            .supportHyphenation(true)
            .visualErrorsEnabled(true)
    }

    fun createDivContext(
        activity: Activity, divStateChangeListener: DivStateChangeListener? = null,
        configBuilder: DivConfiguration.Builder.() -> DivConfiguration.Builder = { this }
    ): Div2Context {
        val configuration =
            createDivConfiguration(activity, divStateChangeListener).configBuilder().build()
        return Div2Context(activity, configuration)
    }
}

open class DemoDivActionHandler(private val uriHandlerDivkit: DivkitDemoUriHandler) : DivActionHandler() {
    override fun handleUri(uri: Uri, view: DivViewFacade): Boolean {
        if (super.handleUri(uri, view)) {
            return true
        }

        KLog.d(TAG) { "layoutId=${view.divTag.id}" }
        return uriHandlerDivkit.handleUri(uri)
    }

    override fun handleAction(action: DivAction, view: DivViewFacade): Boolean {
        return super.handleAction(action, view) || action.url != null &&
                handleUri(action.url!!.evaluate(view.expressionResolver), view)
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

fun JSONObject.parseToDiv2List(errorLogger: ParsingErrorLogger? = null): List<DivData> {
    return when {
        isDiv2DataList() -> asDiv2DataListWithTemplates(errorLogger)
        isDiv2Data() -> listOf(asDiv2DataWithTemplates(errorLogger))
        else -> listOf(asDiv2Data(errorLogger))
    }
}

fun JSONObject.isDiv2Data() = optJSONObject("card") != null

fun JSONObject.isDiv2DataList() = optJSONArray("cards") != null

fun JSONObject.asDiv2Data(errorLogger: ParsingErrorLogger? = null): DivData {
    val environment = DivParsingEnvironment(
        errorLogger ?: ParsingErrorLogger.LOG,
        MainTemplateProvider(
            InMemoryTemplateProvider(),
            TemplateProvider.empty(),
        ),
    )
    return environment.createDivDataWithHistograms(this)
}

fun JSONObject.asDiv2DataWithTemplates(errorLogger: ParsingErrorLogger? = null): DivData {
    val templates = optJSONObject("templates")
    val card = getJSONObject("card")
    val environment = createEnvironment(errorLogger, templates)
    return environment.createDivDataWithHistograms(card)
}

fun JSONObject.asDiv2DataListWithTemplates(errorLogger: ParsingErrorLogger? = null): List<DivData> {
    val templates = optJSONObject("templates")
    val environment = createEnvironment(errorLogger, templates)

    val cards = getJSONArray("cards")
    val divDataList = mutableListOf<DivData>()
    for (i in 0 until cards.length()) {
        divDataList.add(environment.createDivDataWithHistograms(cards.getJSONObject(i)))
    }
    return divDataList
}

fun JSONObject.asDivPatchWithTemplates(errorLogger: ParsingErrorLogger? = null): DivPatch {
    val templates = getJSONObject("templates")
    val card = getJSONObject("patch")
    val environment = createEnvironment(errorLogger, templates)
    return DivPatch(environment, card)
}

private fun createEnvironment(errorLogger: ParsingErrorLogger?, templates: JSONObject?): DivParsingEnvironment {
    val environment = DivParsingEnvironment(errorLogger ?: ParsingErrorLogger.LOG)

    templates?.let {
        environment.parseTemplatesWithHistograms(templates)
    }

    return environment
}

internal fun ParsingEnvironment.createDivDataWithHistograms(data: JSONObject): DivData {
    return Container.parsingHistogramReporter.measureDataParsing(data, componentName = null) {
        DivData(this, data)
    }
}

internal fun DivParsingEnvironment.parseTemplatesWithHistograms(templates: JSONObject) {
    Container.parsingHistogramReporter.measureTemplatesParsing(templates, componentName = null) {
        parseTemplates(templates)
    }
}

internal fun String.toJSONObjectWithHistograms(): JSONObject {
    return Container.parsingHistogramReporter.measureJsonParsing(componentName = null) {
        JSONObject(this)
    }
}
