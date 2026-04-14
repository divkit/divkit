package com.yandex.divkit.demo.regression

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.compose.ui.platform.ComposeView
import com.yandex.div.compose.DivComposeConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView as ComposeDivView
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivData
import com.yandex.divkit.demo.font.ComposeFontFamilyProvider
import com.yandex.divkit.demo.screenshot.DivAssetReader
import org.json.JSONObject

class RegressionComposeViewCreator(context: Context) {

    private val assetReader = DivAssetReader(context)

    fun createView(
        activity: Activity,
        scenarioPath: String,
        onBound: (View) -> Unit,
    ) {
        val (templatesJson, cardJson) = assetReader.readScenarioJson(scenarioPath)
        val divData = parseDivData(templatesJson, cardJson)
        val composeDivContext = DivContext(
            baseContext = activity,
            configuration = DivComposeConfiguration(
                fontFamilyProvider = ComposeFontFamilyProvider(activity),
            )
        )
        val composeView = ComposeView(composeDivContext).apply {
            setContent { ComposeDivView(data = divData) }
        }
        onBound(composeView)
    }

    private fun parseDivData(templatesJson: JSONObject?, cardJson: JSONObject): DivData {
        val environment = DivParsingEnvironment(ParsingErrorLogger.LOG).apply {
            if (templatesJson != null) parseTemplates(templatesJson)
        }
        return DivData(environment, cardJson)
    }
}
