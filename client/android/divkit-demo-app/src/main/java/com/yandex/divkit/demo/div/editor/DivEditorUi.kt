package com.yandex.divkit.demo.div.editor

import android.graphics.Bitmap
import android.view.View
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
import com.yandex.div.data.Variable
import com.yandex.div.internal.Assert
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import com.yandex.div2.DivSize
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.div.DemoRendererFacade
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
    private val scrollView: View,
    private val composeContainer: View,
    private val divContext: Div2Context,
    private val viewRenderer: DemoRendererFacade,
    private val composeRenderer: DemoRendererFacade,
    private val div2Recycler: RecyclerView,
    private val div2Adapter: DivEditorAdapter,
    private val metadataHost: Div2MetadataBottomSheet.MetadataHost,
    private val showRenderTime: Boolean,
) {

    var onDiv2ViewDrawnListener: ((bitmap: Bitmap) -> Unit)? = null
    var hasTemplates: Boolean = false

    val activeRenderer: DemoRendererFacade
        get() = if (useComposeRenderer) composeRenderer else viewRenderer

    private val inactiveRenderer: DemoRendererFacade
        get() = if (useComposeRenderer) viewRenderer else composeRenderer

    var useComposeRenderer: Boolean = false
        set(value) {
            field = value
            applyRendererVisibility()
        }

    private var isSingleCard: Boolean = false
    private var lastDivData: DivData? = null
    private var lastDivDataTag: DivDataTag? = null

    private val debounceOnViewDrawObserver = DebounceOnViewDrawObserver {
        updateRenderTime()
        activity.takeScreenshot { screenshot: Bitmap ->
            onDiv2ViewDrawnListener?.invoke(screenshot)
        }
    }

    init {
        viewRenderer.view.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            val divData = viewRenderer.currentData ?: return@addOnLayoutChangeListener
            divContainer.doOnPreDraw { adjustContainerHeight(divData) }
        }
    }

    fun updateState(newState: DivEditorState) {
        when (newState) {
            is DivEditorState.InitialState -> showInitialState()
            is DivEditorState.LoadingState -> showLoadingState()
            is DivEditorState.DivReceivedState -> showDivReceivedState(newState)
            is DivEditorState.FailedState -> showFailedState(newState)
        }
    }

    fun setDivData(divData: DivData, tag: DivDataTag = DivDataTag(divData.logId)) {
        isSingleCard = true
        setRendererData(divData, tag)
        applyRendererVisibility()
    }

    fun applyPatch(divPatch: DivPatch, errorCallback: () -> Unit) {
        if (isSingleCard) {
            activeRenderer.applyPatch(divPatch, errorCallback)
        } else {
            div2Adapter.applyPatch(divPatch, errorCallback)
        }
    }

    private fun setRendererData(data: DivData, tag: DivDataTag) {
        lastDivData = data
        lastDivDataTag = tag
        activeRenderer.setData(data, tag)
    }

    private fun applyRendererVisibility() {
        if (!isSingleCard) return
        inactiveRenderer.deactivate()
        activeRenderer.activate(lastDivData, lastDivDataTag)
        activeRenderer.currentData?.let { adjustContainerHeight(it) }
        scrollView.visibility = if (useComposeRenderer) GONE else VISIBLE
        composeContainer.visibility = if (useComposeRenderer) VISIBLE else GONE
    }

    private fun adjustContainerHeight(divData: DivData) {
        val rootHeight = divData.states.firstOrNull()?.div?.value()?.height
        val containerHeight = when (rootHeight) {
            is DivSize.MatchParent -> ViewGroup.LayoutParams.MATCH_PARENT
            else -> ViewGroup.LayoutParams.WRAP_CONTENT
        }
        if (divContainer.layoutParams.height != containerHeight) {
            divContainer.layoutParams.height = containerHeight
            divContainer.requestLayout()
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
        isSingleCard = state.isSingleCard
        if (state.isSingleCard) {
            val card = state.divDataList.first()
            setRendererData(card, DivDataTag(card.logId))
            div2Recycler.visibility = GONE
            div2Adapter.clearList()
            applyRendererVisibility()
        } else {
            div2Recycler.visibility = VISIBLE
            viewRenderer.cleanup()
            composeRenderer.cleanup()
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
        divContext.divVariableController.declare(*result.toTypedArray())
    }

    private fun hideAll() {
        metadataButton.visibility = GONE
        failedTextMessage.visibility = GONE
        viewRenderer.view.visibility = GONE
        composeRenderer.view.visibility = GONE
        divContainer.viewTreeObserver.removeOnDrawListener(debounceOnViewDrawObserver)
    }
}
