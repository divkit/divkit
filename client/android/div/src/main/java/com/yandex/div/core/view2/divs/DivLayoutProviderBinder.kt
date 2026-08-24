package com.yandex.div.core.view2.divs

import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver
import com.yandex.div.R
import com.yandex.div.core.dagger.DivDataScope
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.internal.core.VariableMutationHandler
import com.yandex.div.internal.view.onPreDrawListener
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivLayoutProvider
import javax.inject.Inject

private typealias MeasuredSizes = MutableMap<String, Int>

@DivDataScope
internal class DivLayoutProviderBinder @Inject constructor(
    private val variableMutationHandler: VariableMutationHandler,
) {

    private val measuredSizes = mutableMapOf<ExpressionResolver, MeasuredSizes>()
    private var variableHolder: DivLayoutProviderVariableHolder? = null
    private val layoutProviders = mutableSetOf<DivLayoutProvider>()
    private var clearVariablesListener: ViewTreeObserver.OnPreDrawListener? = null

    fun bind(
        view: View,
        newLayoutProvider: DivLayoutProvider?,
        oldLayoutProvider: DivLayoutProvider?,
        resolver: ExpressionResolver,
        divView: Div2View,
    ) {
        newLayoutProvider ?: return view.clearLayoutProviderVariablesIfNeeded(oldLayoutProvider)

        if (newLayoutProvider.widthVariableName.equals(oldLayoutProvider?.widthVariableName)
            && newLayoutProvider.heightVariableName.equals(oldLayoutProvider?.heightVariableName)) {
            return
        }

        view.clearLayoutProviderVariablesIfNeeded(oldLayoutProvider)
        view.bindLayoutProvider(newLayoutProvider, resolver, divView)
    }

    private fun View.clearLayoutProviderVariablesIfNeeded(oldLayoutProvider: DivLayoutProvider?) {
        oldLayoutProvider ?: return
        layoutProviders.remove(oldLayoutProvider)
        removeOnLayoutChangeListener(getTag(R.id.div_layout_provider_listener_id) as? View.OnLayoutChangeListener)
    }

    private fun View.bindLayoutProvider(
        layoutProvider: DivLayoutProvider,
        resolver: ExpressionResolver,
        divView: Div2View
    ) {
        val data = divView.divData ?: return

        val widthVariable = layoutProvider.widthVariableName
        val heightVariable = layoutProvider.heightVariableName
        val errorCollector = divView.errorCollector

        if (widthVariable.isNullOrEmpty() && heightVariable.isNullOrEmpty()) {
            errorCollector.logError(Throwable("Neither width_variable_name nor height_variable_name found."))
            return
        }

        layoutProviders.add(layoutProvider)

        val variableHolder = variableHolder ?: DivLayoutProviderVariableHolder().also { variableHolder = it }
        variableHolder.observeDivDataIfNeeded(data, divView.expressionResolver)

        val listener =
            View.OnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                updateSizeVariable(
                    resources.displayMetrics,
                    widthVariable,
                    variableHolder,
                    left, right, oldLeft, oldRight,
                    resolver,
                    errorCollector,
                )
                updateSizeVariable(
                    resources.displayMetrics,
                    heightVariable,
                    variableHolder,
                    top, bottom, oldTop, oldBottom,
                    resolver,
                    errorCollector,
                )
            }

        if (width > 0 || height > 0) {
            listener.onLayoutChange(this, left, top, right, bottom, 0, 0, 0, 0)
        }
        addOnLayoutChangeListener(listener)
        setTag(R.id.div_layout_provider_listener_id, listener)

        if (divView.isAttachedToWindow) {
            addClearVariablesListener(divView)
        }
    }

    private fun updateSizeVariable(
        metrics: DisplayMetrics,
        variableName: String?,
        variableHolder: DivLayoutProviderVariableHolder,
        start: Int,
        end: Int,
        oldStart: Int,
        oldEnd: Int,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector,
    ) {
        if (variableName.isNullOrEmpty()) return

        val size = end - start
        if (size == oldEnd - oldStart) return

        if (variableHolder.contains(variableName)) {
            errorCollector.logError(
                Throwable(
                    "Size subscriber for variable '$variableName' affects original view size. Relayout was prevented."
                )
            )
            return
        }

        val sizes = measuredSizes.getOrPut(resolver) { mutableMapOf() }
        sizes[variableName] = size.pxToDp(metrics)
    }

    private fun addClearVariablesListener(divView: Div2View) {
        if (clearVariablesListener != null) return

        val listener = onPreDrawListener {
            variableHolder = null
            measuredSizes.forEach { (resolver, sizes) ->
                sizes.forEach {
                    variableMutationHandler.setVariable(it.name, it.value.toString(), resolver, divView.errorCollector)
                }
            }
            measuredSizes.clear()
        }

        clearVariablesListener = listener
        divView.viewTreeObserver.addOnPreDrawListener(listener)
    }

    fun onAttach(divView: Div2View) {
        if (layoutProviders.isEmpty()) return
        addClearVariablesListener(divView)
    }

    fun onDetach(divView: Div2View) {
        divView.viewTreeObserver.removeOnPreDrawListener(clearVariablesListener)
        clearVariablesListener = null
    }

    fun release() {
        variableHolder?.release()
    }
}

private val Map.Entry<String, Int>.name get() = key
