package com.yandex.divkit.demo.div

import android.view.View
import android.view.ViewGroup
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import com.yandex.divkit.demo.utils.applyPatchByConfig
import com.yandex.divkit.demo.utils.handleActionString

interface DemoRendererFacade {
    val view: View
    val currentData: DivData?
    fun setData(data: DivData, tag: DivDataTag)
    fun performAction(action: String)
    fun applyPatch(divPatch: DivPatch, errorCallback: () -> Unit): Boolean
    fun dismissTooltips()
    fun cleanup()

    fun deactivate() {
        cleanup()
        view.visibility = View.GONE
    }

    fun activate(data: DivData? = null, tag: DivDataTag? = null) {
        if (data != null && tag != null && currentData == null) {
            setData(data, tag)
        }
        view.visibility = if (currentData != null) View.VISIBLE else View.GONE
    }
}

class Div2RendererFacade(
    container: ViewGroup,
    private val divContext: Div2Context,
) : DemoRendererFacade {

    private val divView = Div2View(divContext).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    init {
        container.addView(divView)
    }

    override val view: View get() = divView

    override val currentData: DivData? get() = divView.divData

    override fun setData(data: DivData, tag: DivDataTag) {
        divView.setData(data, tag)
    }

    override fun performAction(action: String) {
        divView.handleActionString(action)
    }

    override fun applyPatch(divPatch: DivPatch, errorCallback: () -> Unit): Boolean {
        divView.applyPatchByConfig(divPatch) {
            if (!it) errorCallback()
        }
        return true
    }

    override fun dismissTooltips() {
        divContext.cancelTooltips()
    }

    override fun cleanup() {
        divView.cleanup()
    }
}
