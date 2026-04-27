package com.yandex.divkit.demo.regression

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import com.yandex.div.compose.DivComposeConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.divkit.demo.div.ChronometerViewFactory
import com.yandex.divkit.demo.div.CustomContainerViewFactory
import com.yandex.divkit.demo.div.CustomTextViewFactory
import com.yandex.divkit.demo.div.NestedScrollViewFactory
import com.yandex.div.compose.DivView as ComposeDivView
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
        val divContext = DivContext(
            baseContext = activity,
            configuration = DivComposeConfiguration(
                actionHandler = RegressionComposeActionHandler(assetReader, divData),
                customViewFactories = mapOf(
                    "old_custom_card_1" to CustomTextViewFactory(text = "Hi! I'm old card!"),
                    "old_custom_card_2" to CustomTextViewFactory(text = "Hi! I'm old as well!"),
                    "custom_card_with_min_size" to CustomTextViewFactory(text = "Text is placed in frame"),
                    "new_custom_card_1" to ChronometerViewFactory(),
                    "new_custom_card_2" to ChronometerViewFactory(),
                    "new_custom_container_1" to CustomContainerViewFactory(),
                    "nested_scroll_view" to NestedScrollViewFactory()
                ),
                fontFamilyProvider = ComposeFontFamilyProvider(activity),
            )
        )
        val view = ComposeView(divContext).apply {
            setContent {
                ComposeDivView(data = divData.value)
            }
        }
        onBound(view)
    }
}
