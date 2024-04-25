package com.yandex.div.core.view2.state

import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.state.DivPathUtils.tryFindStateDivAndLayout
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.divs.bindingContext
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData
import javax.inject.Inject

/**
 * State switcher that performs binding for joined path only using LCA search algorithm.
 */
@DivViewScope
internal class DivJoinedStateSwitcher @Inject constructor(
    private val divView: Div2View,
    private val divBinder: DivBinder,
) : DivStateSwitcher {

    override fun switchStates(state: DivData.State, paths: List<DivStatePath>, resolver: ExpressionResolver) {
        val rootView = divView.getChildAt(0)
        val rootDiv = state.div
        var view = rootView
        var div = rootDiv
        var path = DivStatePath.fromState(state.stateId)

        // trying to bind exact DivState, not whole view.
        val commonPath = findCommonPath(paths, path)
        if (!commonPath.isRootPath()) {
            val (viewByPath, divByPath) =
                rootView.tryFindStateDivAndLayout(state, commonPath, resolver) ?: return

            if (viewByPath != null) {
                view = viewByPath
                div = divByPath
                path = commonPath
            }
        }

        val bindingContext = view.bindingContext ?: divView.bindingContext
        divBinder.bind(bindingContext, view, div, path.parentState())

        divBinder.attachIndicators()
    }

    private fun findCommonPath(pathList: List<DivStatePath>, rootPath: DivStatePath): DivStatePath {
        when (pathList.size) {
            0 -> return rootPath
            1 -> return pathList.first()
        }
        return pathList.reduce { commonPath, path ->
            DivStatePath.lowestCommonAncestor(commonPath, path) ?: rootPath
        }
    }
}
