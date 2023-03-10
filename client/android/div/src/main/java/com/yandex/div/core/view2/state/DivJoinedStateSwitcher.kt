package com.yandex.div.core.view2.state

import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.state.DivPathUtils.findDivState
import com.yandex.div.core.state.DivPathUtils.findStateLayout
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div2.Div
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

    override fun switchStates(state: DivData.State, paths: List<DivStatePath>) {
        val rootView = divView.getChildAt(0)
        val rootDiv = state.div
        var view = rootView
        var div = rootDiv
        var path = DivStatePath.fromState(state.stateId)

        // trying to bind exact DivState, not whole view.
        val commonPath = findCommonPath(paths, path)
        if (!commonPath.isRootPath()) {
            val viewByPath = rootView.findStateLayout(commonPath)
            val divByPath = rootDiv.findDivState(commonPath) as? Div.State
            if (viewByPath != null && divByPath != null) {
                view = viewByPath
                div = divByPath
                path = commonPath
            }
        }
        divBinder.bind(view, div, divView, path.parentState())

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
