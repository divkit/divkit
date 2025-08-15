package com.yandex.divkit.demo.div.editor

import android.graphics.Bitmap
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.expression.variables.DivVariablesParser
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.Variable
import com.yandex.div.internal.Assert
import com.yandex.div2.DivSize
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.div.Div2MetadataBottomSheet
import com.yandex.divkit.demo.div.editor.DivEditorScreenshot.takeScreenshot
import com.yandex.divkit.demo.div.editor.list.DivEditorAdapter
import com.yandex.divkit.demo.div.histogram.LoggingHistogramBridge
import org.json.JSONObject

class DivEditorUi(
    private val activity: AppCompatActivity,
    private val metadataButton: FloatingActionButton,
    private val failedTextMessage: TextView,
    private val divContainer: ViewGroup,
    private val div2View: Div2View,
    private val div2Recycler: RecyclerView,
    private val div2Adapter: DivEditorAdapter,
    private val metadataHost: Div2MetadataBottomSheet.MetadataHost,
    private val showRenderTime: Boolean,
) {

    var onDiv2ViewDrawnListener: ((bitmap: Bitmap) -> Unit)? = null
    var hasTemplates: Boolean = false

    private val debounceOnViewDrawObserver = DebounceOnViewDrawObserver {
        updateRenderTime()
        activity.takeScreenshot { screenshot: Bitmap ->
            onDiv2ViewDrawnListener?.invoke(screenshot)
        }
    }

    init {
        div2View.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            div2View.divData?.states?.firstOrNull { it.stateId == div2View.currentStateId }?.let {
                val height = when (it.div.value().height) {
                    is DivSize.MatchParent -> ViewGroup.LayoutParams.MATCH_PARENT
                    else -> ViewGroup.LayoutParams.WRAP_CONTENT
                }
                if (divContainer.layoutParams.height == height) return@addOnLayoutChangeListener
                divContainer.doOnPreDraw {
                    divContainer.layoutParams.height = height
                    divContainer.requestLayout()
                }
            }
        }
    }

    fun updateState(newState: DivEditorState) {
        when (newState) {
            is DivEditorState.InitialState -> {
                showInitialState()
            }
            is DivEditorState.LoadingState -> {
                showLoadingState()
            }
            is DivEditorState.DivReceivedState -> {
                showDivReceivedState(newState)
            }
            is DivEditorState.FailedState -> {
                showFailedState(newState)
            }
        }
    }

    private fun showInitialState() {
        hideAll()
    }

    private fun updateRenderTime() {
        metadataHost.renderingTimeMessages.clear()
        val histogramBridge = Container.histogramConfiguration.histogramBridge.get()
            as LoggingHistogramBridge
        histogramBridge.getLastSavedHistogram("$DEMO_ACTIVITY_COMPONENT_NAME.$DIV_PARSING_DATA")?.let {
            metadataHost.renderingTimeMessages.put(DIV_PARSING_DATA, it)
        }
        if (hasTemplates) {
            histogramBridge.getLastSavedHistogram("$DEMO_ACTIVITY_COMPONENT_NAME.$DIV_PARSING_TEMPLATES")?.let {
                metadataHost.renderingTimeMessages.put(DIV_PARSING_TEMPLATES, it)
            }
        }
        histogramBridge.getLastSavedHistogram("$DEMO_ACTIVITY_COMPONENT_NAME.$DIV_RENDER_TOTAL")?.let {
            metadataHost.renderingTimeMessages.put(DIV_RENDER_TOTAL, it)
        }

        if (showRenderTime) {
            metadataButton.visibility = VISIBLE
        } else {
            metadataButton.visibility = GONE
            return
        }
    }

    private fun showLoadingState() {
        hideAll()
        failedTextMessage.visibility = VISIBLE
        metadataHost.renderingTimeMessages.clear()
        metadataButton.visibility = GONE
        failedTextMessage.text = "Loading..."
    }

    private fun showFailedState(failedState: DivEditorState.FailedState) {
        hideAll()
        failedTextMessage.visibility = VISIBLE
        failedTextMessage.text = failedState.message
        debounceOnViewDrawObserver.onDraw()
    }

    private fun showDivReceivedState(state: DivEditorState.DivReceivedState) {
        hideAll()

        tryDeclareContextVariables(state.rawDiv)
        if (state.isSingleCard) {
            div2View.visibility = VISIBLE
            div2Recycler.visibility = GONE
            div2Adapter.clearList()
            val card = state.divDataList.first()
            div2View.setData(card, DivDataTag(card.logId))
        } else {
            div2Recycler.visibility = VISIBLE
            div2View.visibility = GONE
            div2View.cleanup()
            div2Adapter.setList(state.divDataList)
            div2Recycler.viewTreeObserver.addOnDrawListener(debounceOnViewDrawObserver)
        }

        // Make sure onDiv2ViewDrawnListener called, even if div2View not going to be drawn.
        debounceOnViewDrawObserver.onDraw()
    }

    private fun tryDeclareContextVariables(rawDiv: JSONObject) {
        val variables = rawDiv.optJSONArray("variables") ?: return
        val result: List<Variable> = DivVariablesParser.parse(variables) { e ->
            Assert.fail("Error during parsing of variable", e)
        }

        (div2View.context as Div2Context).divVariableController.declare(*result.toTypedArray())
    }

    private fun hideAll() {
        metadataButton.visibility = GONE
        failedTextMessage.visibility = GONE
        divContainer.viewTreeObserver.removeOnDrawListener(debounceOnViewDrawObserver)
    }
}
