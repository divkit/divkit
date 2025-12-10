package com.yandex.divkit.demo.ui

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.yandex.div.core.Div2Context
import com.yandex.div.core.view2.Div2View
import com.yandex.div.font.YandexSansDivTypefaceProvider
import com.yandex.div.state.DivStateDatabase
import com.yandex.div.zoom.DivPinchToZoomConfiguration
import com.yandex.div.zoom.DivPinchToZoomExtensionHandler
import com.yandex.divkit.demo.div.Div2Activity
import com.yandex.divkit.demo.div.divConfiguration
import com.yandex.divkit.demo.div.divContext
import com.yandex.divkit.demo.screenshot.Div2ViewFactory
import com.yandex.divkit.demo.screenshot.DivAssetReader
import com.yandex.divkit.demo.utils.DivkitDemoUriHandler
import com.yandex.divkit.regression.Div2ViewCreator
import com.yandex.divkit.regression.ScenarioLogDelegate
import org.json.JSONObject
import java.util.concurrent.Executors

class UIDiv2ViewCreator(private val context: Context) : Div2ViewCreator {

    private val assetReader = DivAssetReader(context)
    private val uriHandler = DivkitDemoUriHandler(context)
    private val divStateStorage = DivStateDatabase(
        context,
        "regression-div-states",
        Executors.newSingleThreadExecutor()
    )

    override fun createDiv2ViewByConfig(
        activity: Activity,
        scenarioPath: String,
        parent: ViewGroup,
        logDelegate: ScenarioLogDelegate,
        onBound: (Div2View) -> Unit
    ) {
        val divContext = createContext(activity, parent, logDelegate)
        val divJson = assetReader.read(scenarioPath)
        val templateJson = parseTemplates(divJson)
        val cardJson = parseCard(divJson)

        Div2ViewFactory(divContext, templateJson).createViewByConfig(cardJson, onBound)
    }

    override fun createDiv2ViewSync(
        activity: Activity,
        scenarioPath: String,
        parent: ViewGroup,
        logDelegate: ScenarioLogDelegate
    ): Div2View {
        val divContext = createContext(activity, parent, logDelegate)
        val divJson = assetReader.read(scenarioPath)
        val templateJson = parseTemplates(divJson)
        val cardJson = parseCard(divJson)

        return Div2ViewFactory(divContext, templateJson).createViewSync(cardJson)
    }

    private fun createContext(
        activity: Activity,
        parent: ViewGroup,
        logDelegate: ScenarioLogDelegate
    ): Div2Context {
        val transitionScheduler = Div2Activity.DivParentTransitionScheduler(parent)
        val divConfiguration =
            divConfiguration(activity, logDelegate)
                .extension(
                    DivPinchToZoomExtensionHandler(
                        DivPinchToZoomConfiguration.Builder(activity).build()
                    )
                )
                .divStateChangeListener(transitionScheduler)
                .divDataChangeListener(transitionScheduler)
                .typefaceProvider(YandexSansDivTypefaceProvider(activity))
                .actionHandler(UIDiv2ActionHandler(uriHandler, context))
                .enableAccessibility(true)
                .build()
        return divContext(
            baseContext = activity,
            configuration = divConfiguration,
            lifecycleOwner = activity as? LifecycleOwner
        ).also {
            divStateStorage.preloadState("div2")
        }
    }

    private fun parseTemplates(
        divJson: JSONObject,
    ): JSONObject? {
        return divJson.optJSONObject("templates")
    }

    private fun parseCard(
        divJson: JSONObject,
    ): JSONObject {
        return divJson.getJSONObject("card")
    }
}
