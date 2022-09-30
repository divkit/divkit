package com.yandex.div.core.downloader

import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivData
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGrid
import com.yandex.div2.DivPager
import com.yandex.div2.DivPatch
import com.yandex.div2.DivState
import com.yandex.div2.DivTabs

internal class DivPatchApply(private val patch: DivPatchMap) {
    private val appliedPatches = mutableSetOf<String>()

     fun applyPatch(states: List<DivData.State>, resolver: ExpressionResolver): List<DivData.State>? {
        val list: MutableList<DivData.State> = ArrayList(states.size)
        for (oldState in states) {
            val newState = DivData.State(oldState.div.applyPatch(resolver)[0], oldState.stateId)
            list.add(newState)
        }
        if (patch.mode.evaluate(resolver) == DivPatch.Mode.TRANSACTIONAL && appliedPatches.size != patch.patches.size) {
            return null
        }
        return list
    }

    fun applyPatchForDiv(div: Div, resolver: ExpressionResolver): List<Div> {
        return div.applyPatch(resolver)
    }

    private fun Div.applyPatch(resolver: ExpressionResolver): List<Div> {
        val divId = value().id
        if (divId != null && patch.patches.containsKey(divId)) return applyPatchForSingleDiv()

        return listOf(when (this) {
            is Div.Container -> applyPatch(value, resolver)
            is Div.Grid -> applyPatch(value, resolver)
            is Div.Gallery -> applyPatch(value, resolver)
            is Div.Pager -> applyPatch(value, resolver)
            is Div.State -> applyPatch(value, resolver)
            is Div.Tabs -> applyPatch(value, resolver)
            else -> this
        })
    }

    private fun applyPatchForListOfDivs(divs: List<Div>, resolver: ExpressionResolver): List<Div> {
        val divItems = mutableListOf<Div>()
        divs.forEach {
            divItems.addAll(it.applyPatch(resolver))
        }
        return divItems
    }

    private fun Div.applyPatchForSingleDiv(): List<Div> {
        val divId = value().id ?: return listOf(this)
        val patchList = patch.patches[divId]
        if (patchList != null) {
            appliedPatches += divId
            return patchList
        }
        return listOf(this)
    }

    private fun applyPatch(div: DivContainer, resolver: ExpressionResolver): Div.Container {
        return Div.Container(
            DivContainer(
                div.accessibility,
                div.action,
                div.actionAnimation,
                div.actions,
                div.alignmentHorizontal,
                div.alignmentVertical,
                div.alpha,
                div.background,
                div.border,
                div.columnSpan,
                div.contentAlignmentHorizontal,
                div.contentAlignmentVertical,
                div.doubletapActions,
                div.extensions,
                div.focus,
                div.height,
                div.id,
                applyPatchForListOfDivs(div.items, resolver),
                div.layoutMode,
                div.longtapActions,
                div.margins,
                div.orientation,
                div.paddings,
                div.rowSpan,
                div.selectedActions,
                div.tooltips,
                div.transform,
                div.transitionChange,
                div.transitionIn,
                div.transitionOut,
                div.transitionTriggers,
                div.visibility,
                div.visibilityAction,
                div.visibilityActions,
                div.width
            )
        )
    }

    private fun applyPatch(div: DivGrid, resolver: ExpressionResolver): Div.Grid {
        return Div.Grid(
            DivGrid(
                div.accessibility,
                div.action,
                div.actionAnimation,
                div.actions,
                div.alignmentHorizontal,
                div.alignmentVertical,
                div.alpha,
                div.background,
                div.border,
                div.columnCount,
                div.columnSpan,
                div.contentAlignmentHorizontal,
                div.contentAlignmentVertical,
                div.doubletapActions,
                div.extensions,
                div.focus,
                div.height,
                div.id,
                applyPatchForListOfDivs(div.items, resolver),
                div.longtapActions,
                div.margins,
                div.paddings,
                div.rowSpan,
                div.selectedActions,
                div.tooltips,
                div.transform,
                div.transitionChange,
                div.transitionIn,
                div.transitionOut,
                div.transitionTriggers,
                div.visibility,
                div.visibilityAction,
                div.visibilityActions,
                div.width
            )
        )
    }

    private fun applyPatch(div: DivGallery, resolver: ExpressionResolver): Div.Gallery {
        return Div.Gallery(
            DivGallery(
                div.accessibility,
                div.alignmentHorizontal,
                div.alignmentVertical,
                div.alpha,
                div.background,
                div.border,
                div.columnCount,
                div.columnSpan,
                div.crossContentAlignment,
                div.defaultItem,
                div.extensions,
                div.focus,
                div.height,
                div.id,
                div.itemSpacing,
                applyPatchForListOfDivs(div.items, resolver),
                div.margins,
                div.orientation,
                div.paddings,
                div.restrictParentScroll,
                div.rowSpan,
                div.scrollMode,
                div.selectedActions,
                div.tooltips,
                div.transform,
                div.transitionChange,
                div.transitionIn,
                div.transitionOut,
                div.transitionTriggers,
                div.visibility,
                div.visibilityAction,
                div.visibilityActions,
                div.width
            )
        )
    }

    private fun applyPatch(div: DivPager, resolver: ExpressionResolver): Div.Pager {
        return Div.Pager(
            DivPager(
                div.accessibility,
                div.alignmentHorizontal,
                div.alignmentVertical,
                div.alpha,
                div.background,
                div.border,
                div.columnSpan,
                div.defaultItem,
                div.extensions,
                div.focus,
                div.height,
                div.id,
                div.itemSpacing,
                applyPatchForListOfDivs(div.items, resolver),
                div.layoutMode,
                div.margins,
                div.orientation,
                div.paddings,
                div.restrictParentScroll,
                div.rowSpan,
                div.selectedActions,
                div.tooltips,
                div.transform,
                div.transitionChange,
                div.transitionIn,
                div.transitionOut,
                div.transitionTriggers,
                div.visibility,
                div.visibilityAction,
                div.visibilityActions,
                div.width
            )
        )
    }

    private fun applyPatch(div: DivState, resolver: ExpressionResolver): Div.State {
        return Div.State(
            DivState(
                div.accessibility,
                div.alignmentHorizontal,
                div.alignmentVertical,
                div.alpha,
                div.background,
                div.border,
                div.columnSpan,
                div.defaultStateId,
                div.divId,
                div.extensions,
                div.focus,
                div.height,
                div.id,
                div.margins,
                div.paddings,
                div.rowSpan,
                div.selectedActions,
                applyPatchForListStates(div.states, resolver),
                div.tooltips,
                div.transform,
                div.transitionAnimationSelector,
                div.transitionChange,
                div.transitionIn,
                div.transitionOut,
                div.transitionTriggers,
                div.visibility,
                div.visibilityAction,
                div.visibilityActions,
                div.width
            )
        )
    }

    private fun applyPatchForListStates(states: List<DivState.State>,
                                        resolver: ExpressionResolver): List<DivState.State> {
        val newStates = mutableListOf<DivState.State>()
        states.forEach { state ->
            val divId = state.div?.value()?.id
            if (divId != null) {
                val patchList = patch.patches[divId]
                if (patchList?.size == 1) {
                    val patchedState = DivState.State(
                        state.animationIn,
                        state.animationOut,
                        patchList[0],
                        state.stateId,
                        state.swipeOutActions,
                    )
                    newStates.add(patchedState)
                    appliedPatches += divId
                } else if (patchList != null && patchList.isEmpty()) {
                    // just delete this state
                    appliedPatches += divId
                } else {
                    newStates.add(state)
                }
            } else {
                val newDivs = state.div?.applyPatch(resolver)
                if (newDivs?.size == 1) {
                    newStates.add(
                        DivState.State(
                            state.animationIn,
                            state.animationOut,
                            newDivs[0],
                            state.stateId,
                            state.swipeOutActions
                        )
                    )
                } else {
                    newStates.add(state)
                }
            }
        }
        return newStates
    }

    private fun applyPatch(div: DivTabs, resolver: ExpressionResolver): Div.Tabs {
        val newTabItems = mutableListOf<DivTabs.Item>()
        div.items.forEach { tabItem ->
            val newDivs = tabItem.div.applyPatch(resolver)
            if (newDivs.size == 1) {
                newTabItems.add(
                    DivTabs.Item(
                        newDivs[0],
                        tabItem.title,
                        tabItem.titleClickAction
                    )
                )
            } else {
                newTabItems.add(tabItem)
            }
        }
        return Div.Tabs(
            DivTabs(
                div.accessibility,
                div.alignmentHorizontal,
                div.alignmentVertical,
                div.alpha,
                div.background,
                div.border,
                div.columnSpan,
                div.dynamicHeight,
                div.extensions,
                div.focus,
                div.hasSeparator,
                div.height,
                div.id,
                newTabItems,
                div.margins,
                div.paddings,
                div.restrictParentScroll,
                div.rowSpan,
                div.selectedActions,
                div.selectedTab,
                div.separatorColor,
                div.separatorPaddings,
                div.switchTabsByContentSwipeEnabled,
                div.tabTitleStyle,
                div.titlePaddings,
                div.tooltips,
                div.transform,
                div.transitionChange,
                div.transitionIn,
                div.transitionOut,
                div.transitionTriggers,
                div.visibility,
                div.visibilityAction,
                div.visibilityActions,
                div.width
            )
        )
    }

}
