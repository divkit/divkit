package com.yandex.divkit.demo.div

import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.core.net.toUri
import com.yandex.div.DivDataTag
import com.yandex.div.compose.DivComposeConfiguration
import com.yandex.div.compose.video.viewbased.ViewBasedDivVideoPlayerFactory
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.video.m3.ExoDivPlayerFactory
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import com.yandex.divkit.demo.font.ComposeFontSourceProvider
import org.json.JSONException
import org.json.JSONObject
import com.yandex.div.compose.DivContext as ComposeDivContext
import com.yandex.div.compose.DivView as ComposeDivView

@OptIn(InternalApi::class)
class ComposeRendererFacade(
    container: ViewGroup,
    variableController: DivVariableController
) : DemoRendererFacade {

    private val divContext = ComposeDivContext(
        baseContext = container.context,
        configuration = DivComposeConfiguration(
            fontSourceProvider = ComposeFontSourceProvider(),
            playerFactory = ViewBasedDivVideoPlayerFactory(ExoDivPlayerFactory(container.context)),
            variableController = variableController,
        )
    )

    private val composeView = ComposeView(divContext).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        visibility = View.GONE
    }

    private val divData = mutableStateOf<DivData?>(null)

    init {
        container.addView(composeView, 0)
        composeView.setContent {
            divData.value?.let { data ->
                ComposeDivView(data = data)
            }
        }
    }

    override val view: View = composeView

    override val currentData: DivData? get() = divData.value

    override fun setData(data: DivData, tag: DivDataTag) {
        divData.value = data
    }

    override fun performAction(action: String) {
        val data = divData.value ?: return
        try {
            val divAction = DivAction(parsingEnvironment, JSONObject(action))
            divContext.debugFeatures.performAction(data, divAction)
        } catch (_: JSONException) {
            val jsonAction = JSONObject().apply {
                put("log_id", "menu_action")
                put("url", action.toUri().toString())
            }
            val divAction = DivAction(parsingEnvironment, jsonAction)
            divContext.debugFeatures.performAction(data, divAction)
        }
    }

    override fun applyPatch(divPatch: DivPatch, errorCallback: () -> Unit): Boolean {
        // Not supported yet
        return false
    }

    override fun dismissTooltips() {
        // Not supported yet
    }

    override fun cleanup() {
        divData.value = null
    }
}

private val parsingEnvironment = DivParsingEnvironment(ParsingErrorLogger.LOG)
