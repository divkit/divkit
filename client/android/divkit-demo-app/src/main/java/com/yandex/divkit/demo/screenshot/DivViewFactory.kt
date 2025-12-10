package com.yandex.divkit.demo.screenshot

import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivData
import com.yandex.divkit.demo.utils.setDataByConfig
import org.json.JSONObject

internal sealed class DivViewFactory {

    abstract fun createViewSync(cardJson: JSONObject): Div2View

    abstract fun createViewByConfig(cardJson: JSONObject, onBound: (Div2View) -> Unit)
}

internal class Div2ViewFactory(
    private val context: Div2Context,
    private val templatesJson: JSONObject? = null
) : DivViewFactory() {

    private val environment = DivParsingEnvironment(ParsingErrorLogger.ASSERT).apply {
        if (templatesJson != null) parseTemplates(templatesJson)
    }

    override fun createViewSync(cardJson: JSONObject): Div2View {
        val divData = DivData(environment, cardJson)
        val div2View = Div2View(context)
        div2View.setData(divData, DivDataTag("div2"))
        return div2View
    }

    override fun createViewByConfig(cardJson: JSONObject, onBound: (Div2View) -> Unit) {
        val divData = DivData(environment, cardJson)
        val div2View = Div2View(context)
        div2View.setDataByConfig(divData, DivDataTag("div2")) {
            onBound(div2View)
        }
    }
}
