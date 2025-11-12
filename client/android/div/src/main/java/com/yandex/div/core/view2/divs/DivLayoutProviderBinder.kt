package com.yandex.div.core.view2.divs

import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver
import com.yandex.div.R
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.view.onPreDrawListener
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.internal.core.VariableMutationHandler
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData
import com.yandex.div2.DivLayoutProvider
import javax.inject.Inject

private typealias MeasuredSizes = MutableMap<String, Int>

@DivViewScope
internal class DivLayoutProviderBinder @Inject constructor(
    private val errorCollectors: ErrorCollectors,
    private val divView: Div2View,
) {

    private val measuredSizes = mutableMapOf<ExpressionResolver, MeasuredSizes>()
    private val variableHolders = mutableMapOf<DivData, DivLayoutProviderVariableHolder>()
    private val layoutProvidersForDivData = mutableMapOf<DivData, MutableSet<DivLayoutProvider>>()
    private var clearVariablesListener: ViewTreeObserver.OnPreDrawListener? = null

    fun bind(
        view: View,
        newLayoutProvider: DivLayoutProvider?,
        oldLayoutProvider: DivLayoutProvider?,
        resolver: ExpressionResolver,
    ) {
        newLayoutProvider ?: return view.clearLayoutProviderVariablesIfNeeded(oldLayoutProvider)

        if (newLayoutProvider.widthVariableName.equals(oldLayoutProvider?.widthVariableName)
            && newLayoutProvider.heightVariableName.equals(oldLayoutProvider?.heightVariableName)) {
            return
        }

        view.clearLayoutProviderVariablesIfNeeded(oldLayoutProvider)
        view.bindLayoutProvider(newLayoutProvider, resolver)
    }

    private fun View.clearLayoutProviderVariablesIfNeeded(oldLayoutProvider: DivLayoutProvider?) {
        oldLayoutProvider ?: return
        layoutProvidersForDivData[divView.divData]?.remove(oldLayoutProvider)
        removeOnLayoutChangeListener(getTag(R.id.div_layout_provider_listener_id) as? View.OnLayoutChangeListener)
    }

    private fun View.bindLayoutProvider(layoutProvider: DivLayoutProvider, resolver: ExpressionResolver) {
        val data = divView.divData ?: return

        val widthVariable = layoutProvider.widthVariableName
        val heightVariable = layoutProvider.heightVariableName
        val errorCollector = errorCollectors.getOrCreate(divView.dataTag, data)

        if (widthVariable.isNullOrEmpty() && heightVariable.isNullOrEmpty()) {
            errorCollector.logError(Throwable("Neither width_variable_name nor height_variable_name found."))
            return
        }

        layoutProvidersForDivData.getOrPut(data) { mutableSetOf() }
            .add(layoutProvider)

        val variableHolder = variableHolders.getOrPut(data) { DivLayoutProviderVariableHolder() }
        variableHolder.observeDivDataIfNeeded(data, divView.bindingContext)

        val listener =
            View.OnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                updateSizeVariable(
                    resources.displayMetrics,
                    widthVariable,
                    variableHolder,
                    left,
                    right,
                    oldLeft,
                    oldRight,
                    resolver,
                    errorCollector,
                )
                updateSizeVariable(
                    resources.displayMetrics,
                    heightVariable,
                    variableHolder,
                    top,
                    bottom,
                    oldTop,
                    oldBottom,
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
            addClearVariablesListener()
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

    private fun addClearVariablesListener() {
        if (clearVariablesListener != null) return

        val listener = onPreDrawListener {
            variableHolders[divView.divData]?.clear()
            measuredSizes.forEach { (resolver, sizes) ->
                sizes.forEach {
                    VariableMutationHandler.setVariable(divView, it.name, it.value.toString(), resolver)
                }
            }
            measuredSizes.clear()
            true
        }

        clearVariablesListener = listener
        divView.viewTreeObserver.addOnPreDrawListener(listener)
    }

    fun onAttach() {
        if (layoutProvidersForDivData[divView.divData].isNullOrEmpty()) return
        addClearVariablesListener()
    }

    fun onDetach() {
        divView.viewTreeObserver.removeOnPreDrawListener(clearVariablesListener)
        clearVariablesListener = null
    }

    fun release(divData: DivData?) {
        variableHolders[divData]?.release()
    }
}

private val Map.Entry<String, Int>.name get() = key
