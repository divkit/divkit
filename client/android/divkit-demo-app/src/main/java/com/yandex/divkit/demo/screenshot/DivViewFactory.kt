package com.yandex.divkit.demo.screenshot

import android.view.View
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivData
import org.json.JSONObject

internal sealed class DivViewFactory<T : View> {

    abstract fun createView(cardJson: JSONObject): T
}

internal class Div2ViewFactory(
    private val context: Div2Context,
    private val templatesJson: JSONObject? = null
) : DivViewFactory<Div2View>() {

    private val environment = DivParsingEnvironment(ParsingErrorLogger.ASSERT).apply {
        if (templatesJson != null) parseTemplates(templatesJson)
    }

    override fun createView(cardJson: JSONObject): Div2View {
        val divData = DivData(environment, cardJson)
        return Div2View(context).apply {
            setData(divData, DivDataTag("div2"))
        }
    }
}
