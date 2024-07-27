package com.yandex.div.sizeprovider

import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.pxToDp
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivData

internal const val SIZE_PROVIDER_EXTENSION_ID = "size_provider"
internal const val SIZE_PROVIDER_PARAM_HEIGHT = "height_variable_name"
internal const val SIZE_PROVIDER_PARAM_WIDTH = "width_variable_name"

@Deprecated("Use div-base.layout-provider.")
class DivSizeProviderExtensionHandler(
    private val errorLogger: DivSizeProviderErrorLogger = DivSizeProviderErrorLogger.STUB
): DivExtensionHandler {

    private val sizes = mutableMapOf<String, Int>()

    private val variablesHolders = mutableMapOf<DivData, DivSizeProviderVariablesHolder>()
    private val divDataCounters = mutableMapOf<DivData, Int>()

    override fun matches(div: DivBase) = div.sizeProviderExtension != null

    private val DivBase.sizeProviderExtension get() = extensions?.find { it.id == SIZE_PROVIDER_EXTENSION_ID }

    override fun bindView(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase) {
        val params = div.sizeProviderExtension?.params ?: run {
            errorLogger.logError(Throwable("Failed to get extension params from extension $SIZE_PROVIDER_EXTENSION_ID"))
            return
        }
        val widthVariable = params.optString(SIZE_PROVIDER_PARAM_WIDTH)
        val heightVariable = params.optString(SIZE_PROVIDER_PARAM_HEIGHT)
        if (widthVariable.isEmpty() && heightVariable.isEmpty()) {
            errorLogger.logError(Throwable("Neither $SIZE_PROVIDER_PARAM_WIDTH nor $SIZE_PROVIDER_PARAM_HEIGHT found."))
            return
        }

        val data = divView.divData ?: return

        val variablesHolder = variablesHolders[data] ?: DivSizeProviderVariablesHolder()
            .apply { observeDivData(data, expressionResolver) }
            .also { variablesHolders[data] = it }
        divDataCounters[data] = (divDataCounters[data] ?: 0) + 1
        view.setTag(R.id.div_size_provider_data, data)

        val listener = View.OnLayoutChangeListener { _, left, top, right, bottom,
                                                     oldLeft, oldTop, oldRight, oldBottom ->
            val metrics = view.resources.displayMetrics
            updateVariable(metrics, widthVariable, variablesHolder, left, right, oldLeft, oldRight)
            updateVariable(metrics, heightVariable, variablesHolder, top, bottom, oldTop, oldBottom)
        }
        view.addOnLayoutChangeListener(listener)
        view.setTag(R.id.div_size_provider_listener, listener)

        if (divView.getTag(R.id.div_size_provider_clear_variables_listener) != null) return

        val clearVariablesListener = ViewTreeObserver.OnPreDrawListener {
            variablesHolder.clear()
            sizes.forEach { divView.setVariable(it.key, it.value.toString()) }
            sizes.clear()
            true
        }
        divView.setTag(R.id.div_size_provider_clear_variables_listener, clearVariablesListener)
        divView.viewTreeObserver.addOnPreDrawListener(clearVariablesListener)
    }

    private fun updateVariable(
        metrics: DisplayMetrics,
        variableName: String,
        variablesHolder: DivSizeProviderVariablesHolder,
        start: Int,
        end: Int,
        oldStart: Int,
        oldEnd: Int
    ) {
        if (variableName.isEmpty()) return

        val size = end - start
        if (size == oldEnd - oldStart) return

        if (variablesHolder.contains(variableName)) {
            errorLogger.logError(Throwable(
                "Size subscriber affects original view size. Relayout was prevented."))
            return
        }

        sizes[variableName] = size.pxToDp(metrics)
    }

    override fun unbindView(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase) {
        view.removeOnLayoutChangeListener(view.getTag(R.id.div_size_provider_listener) as? View.OnLayoutChangeListener)
        val data = view.getTag(R.id.div_size_provider_data) as? DivData ?: return
        val currentCount = divDataCounters[data] ?: return
        divDataCounters[data] = currentCount - 1
        if (currentCount == 1) {
            variablesHolders.remove(data)?.release()
        }
    }
}
