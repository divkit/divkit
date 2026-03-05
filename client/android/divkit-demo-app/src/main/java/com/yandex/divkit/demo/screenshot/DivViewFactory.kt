package com.yandex.divkit.demo.screenshot

import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivData
import com.yandex.divkit.demo.utils.setDataByConfig
import org.json.JSONObject

internal class Div2ViewFactory(
    private val context: Div2Context,
    private val templatesJson: JSONObject? = null
) {

    private val environment = DivParsingEnvironment(ParsingErrorLogger.ASSERT).apply {
        if (templatesJson != null) parseTemplates(templatesJson)
    }

    fun createAndBindViewSync(cardJson: JSONObject): Div2View {
        val divData = DivData(environment, cardJson)
        val div2View = Div2View(context)
        div2View.setData(divData, DivDataTag("div2"))
        return div2View
    }

    fun createAndBindViewByConfig(cardJson: JSONObject, onBound: (Div2View) -> Unit) {
        val div2View = Div2View(context)
        bindViewByConfig(div2View, cardJson, onBound)
    }

    fun bindViewByConfig(divView: Div2View, cardJson: JSONObject, onBound: (Div2View) -> Unit) {
        val divData = DivData(environment, cardJson)
        divView.setDataByConfig(divData, DivDataTag("div2")) {
            onBound(divView)
        }
    }
}
