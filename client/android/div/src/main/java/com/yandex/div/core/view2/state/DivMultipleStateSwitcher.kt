package com.yandex.div.core.view2.state

import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.state.DivPathUtils.compactPathList
import com.yandex.div.core.state.DivPathUtils.fromState
import com.yandex.div.core.state.DivPathUtils.tryFindStateDivAndLayout
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData
import javax.inject.Inject

@DivViewScope
internal class DivMultipleStateSwitcher @Inject constructor(
    private val divView: Div2View,
    private val divBinder: DivBinder,
) : DivStateSwitcher {

    override fun switchStates(state: DivData.State, resolver: ExpressionResolver, paths: List<DivStatePath>) {
        val rootView = divView.getChildAt(0)
        val rootDiv = state.div

        val localPaths = compactPathList(paths).filter { path -> !path.isRootPath() }
        val boundLayouts = mutableSetOf<DivStateLayout>()
        localPaths.forEach { path ->
            val (viewByPath, divByPath) =
                rootView.tryFindStateDivAndLayout(state, path, resolver) ?: return

            if (viewByPath != null && viewByPath !in boundLayouts) {
                val viewPath = viewByPath.currentStatePath ?: path
                val divBlock = viewByPath.divBlock ?: DivBlock.create(divByPath, resolver, viewPath.parentState())
                divBinder.bind(viewByPath, divBlock, divView)
                boundLayouts += viewByPath
            }
        }
        if (boundLayouts.isEmpty()) {
            divBinder.bind(rootView, DivBlock.create(rootDiv, resolver, DivStatePath.fromState(state)), divView)
        }

        divBinder.attachIndicators(divView)
    }
}
