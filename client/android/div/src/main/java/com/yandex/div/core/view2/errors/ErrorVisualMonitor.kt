package com.yandex.div.core.view2.errors

import android.view.ViewGroup
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.dagger.ExperimentFlag
import com.yandex.div.core.experiments.Experiment.PERMANENT_DEBUG_PANEL_ENABLED
import com.yandex.div.core.experiments.Experiment.VISUAL_ERRORS_ENABLED
import com.yandex.div.core.font.DivTypefaceProvider
import com.yandex.div.core.util.hotreload.HotReloadController
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.ViewBindingProvider
import com.yandex.div.core.view2.debugview.DebugView
import com.yandex.div.core.view2.debugview.DebugViewModelProvider
import com.yandex.div.core.view2.runBindingAction
import javax.inject.Inject

@DivViewScope
internal class ErrorVisualMonitor @Inject constructor(
    errorCollectors: ErrorCollectors,
    private val divView: Div2View,
    @param:ExperimentFlag(VISUAL_ERRORS_ENABLED) private val visualErrorsEnabled: Boolean,
    @param:ExperimentFlag(PERMANENT_DEBUG_PANEL_ENABLED) private val showPermanently: Boolean,
    private val bindingProvider: ViewBindingProvider,
    private val typefaceProvider: DivTypefaceProvider,
) {
    private val hotReloadController = HotReloadController(divView)
    internal var enabled = visualErrorsEnabled
        set(value) {
            field = value
            connectOrDisconnect()
        }

    private val debugViewModelProvider = DebugViewModelProvider(
        errorCollectors = errorCollectors,
        div2View = divView,
        visualErrorsEnabled = visualErrorsEnabled,
        alwaysShowDebugView = showPermanently,
        hotReloadController = hotReloadController,
    )
    private var lastConnectionView: ViewGroup? = null
    private var debugView: DebugView? = null

    init {
        connectOrDisconnect()
    }

    private fun connectOrDisconnect() {
        divView.runBindingAction {
            if (canShowDebugView()) {
                bindingProvider.observeAndGet { debugViewModelProvider.bind(it) }
                lastConnectionView?.let {
                    connect(it)
                }
            } else {
                debugView?.close()
                debugView = null
            }
        }
    }

    private fun canShowDebugView(): Boolean {
        if (showPermanently) {
            return true
        }

        if (enabled) {
            return true
        }

        if (hotReloadController.active) {
            return true
        }

        return false
    }

    fun connect(root: ViewGroup) {
        divView.runBindingAction {
            lastConnectionView = root
            if (!canShowDebugView()) {
                return@runBindingAction
            }
            debugView?.close()
            debugView = DebugView(root, divView, debugViewModelProvider, typefaceProvider)
        }
    }
}
