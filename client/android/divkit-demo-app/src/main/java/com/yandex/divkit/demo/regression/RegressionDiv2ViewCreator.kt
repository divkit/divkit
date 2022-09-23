package com.yandex.divkit.demo.regression

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import com.yandex.div.core.Div2Context
import com.yandex.div.core.view2.Div2View
import com.yandex.div.font.YandexSansDivTypefaceProvider
import com.yandex.div.lottie.DivLottieExtensionHandler
import com.yandex.div.state.DivStateDatabase
import com.yandex.div.video.custom.VideoCustomAdapter
import com.yandex.div.zoom.DivPinchToZoomConfiguration
import com.yandex.div.zoom.DivPinchToZoomExtensionHandler
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.div.DemoDivLottieRawResProvider
import com.yandex.divkit.demo.div.Div2Activity
import com.yandex.divkit.demo.div.DivUtils
import com.yandex.divkit.demo.screenshot.Div2ViewFactory
import com.yandex.divkit.demo.screenshot.DivAssetReader
import com.yandex.divkit.demo.utils.DivkitDemoUriHandler
import com.yandex.divkit.regression.Div2ViewCreator
import com.yandex.divkit.regression.ScenarioLogDelegate
import java.util.concurrent.Executors

class RegressionDiv2ViewCreator(context: Context) : Div2ViewCreator {

    private val assetReader = DivAssetReader(context)
    private val uriHandler = DivkitDemoUriHandler(context)
    private val divStateStorage = DivStateDatabase(
        context,
        "regression-div-states",
        Executors.newSingleThreadExecutor()
    )

    override fun createDiv2View(
        activity: Activity,
        scenarioPath: String,
        parent: ViewGroup,
        logDelegate: ScenarioLogDelegate
    ): Div2View {
        val divJson = assetReader.read(scenarioPath)
        val transitionScheduler = Div2Activity.DivParentTransitionScheduler(parent)
        val divConfiguration =
            DivUtils.createDivConfiguration(activity, transitionScheduler, logDelegate)
                .extension(
                    DivPinchToZoomExtensionHandler(
                        DivPinchToZoomConfiguration.Builder(activity).build()
                    )
                )
                .divCustomViewAdapter(VideoCustomAdapter(Container.videoCustomViewController))
                .extension(DivLottieExtensionHandler(DemoDivLottieRawResProvider))
                .divDataChangeListener(transitionScheduler)
                .divStateCache(divStateStorage.cache)
                .typefaceProvider(YandexSansDivTypefaceProvider(activity))
                .actionHandler(RegressionDivActionHandler(uriHandler, assetReader, activity))
                .enableAccessibility(true)
                .build()
        val divContext = Div2Context(baseContext = activity, configuration = divConfiguration)
        divStateStorage.preloadState("div2")
        return when {
            divJson.has("card") -> {
                val templateJson = divJson.optJSONObject("templates")
                val cardJson = divJson.getJSONObject("card")
                Div2ViewFactory(divContext, templateJson).createView(cardJson)
            }
            else -> Div2ViewFactory(divContext).createView(divJson)
        }
    }
}
