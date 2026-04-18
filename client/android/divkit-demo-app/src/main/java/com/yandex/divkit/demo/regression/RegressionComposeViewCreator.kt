package com.yandex.divkit.demo.regression

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import com.yandex.div.compose.DivComposeConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView as ComposeDivView
import com.yandex.divkit.demo.div.DemoComposeCustomViewFactory
import com.yandex.divkit.demo.font.ComposeFontFamilyProvider
import com.yandex.divkit.demo.screenshot.DivAssetReader

class RegressionComposeViewCreator(context: Context) {
    private val assetReader = DivAssetReader(context)

    fun createView(
        activity: Activity,
        scenarioPath: String,
        onBound: (View) -> Unit,
    ) {
        val (templatesJson, cardJson) = assetReader.readScenarioJson(scenarioPath)
        val divData = mutableStateOf(parseDivData(templatesJson, cardJson))
        val composeDivContext = DivContext(
            baseContext = activity,
            configuration = DivComposeConfiguration(
                actionHandler = RegressionComposeActionHandler(assetReader, divData),
                fontFamilyProvider = ComposeFontFamilyProvider(activity),
                customViewFactory = DemoComposeCustomViewFactory()
            )
        )
        val composeView = ComposeView(composeDivContext).apply {
            setContent {
                ComposeDivView(data = divData.value)
            }
        }
        onBound(composeView)
    }
}
