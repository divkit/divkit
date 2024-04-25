package com.yandex.div.core.view2.state

import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.state.DivPathUtils.compactPathList
import com.yandex.div.core.state.DivPathUtils.tryFindStateDivAndLayout
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData
import javax.inject.Inject

@DivViewScope
internal class DivMultipleStateSwitcher @Inject constructor(
    private val divView: Div2View,
    private val divBinder: DivBinder,
) : DivStateSwitcher {

    override fun switchStates(state: DivData.State, paths: List<DivStatePath>, resolver: ExpressionResolver) {
        val rootView = divView.getChildAt(0)
        val rootDiv = state.div

        val localPaths = compactPathList(paths).filter { path -> !path.isRootPath() }
        val boundLayouts = mutableSetOf<DivStateLayout>()
        localPaths.forEach { path ->
            val (viewByPath, divByPath) =
                rootView.tryFindStateDivAndLayout(state, path, resolver) ?: return

            if (viewByPath != null && viewByPath !in boundLayouts) {
                val bindingContext = viewByPath.bindingContext ?: divView.bindingContext
                divBinder.bind(bindingContext, viewByPath, divByPath, path.parentState())
                boundLayouts += viewByPath
            }
        }
        if (boundLayouts.isEmpty()) {
            divBinder.bind(divView.bindingContext, rootView, rootDiv, DivStatePath.fromState(state.stateId))
        }

        divBinder.attachIndicators()
    }
}
