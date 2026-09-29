package com.yandex.div.internal.patch

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.internal.KLog
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivData
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGrid
import com.yandex.div2.DivPager
import com.yandex.div2.DivPatch
import com.yandex.div2.DivState
import com.yandex.div2.DivTabs

@InternalApi
class DivPatchApply(
    private val patch: DivPatchMap,
    private val transformItem: ((Div) -> Div)? = null,
    private val errorLogger: ParsingErrorLogger,
) {
    private val appliedPatches = mutableSetOf<String>()
    private val copies = mutableMapOf<DivBase, DivBase>()
    private var applicationCount = 0

    val copiedDivs: Map<DivBase, DivBase>
        get() = copies

    fun applyPatch(states: List<DivData.State>, resolver: ExpressionResolver): List<DivData.State>? {
        val list: MutableList<DivData.State> = ArrayList(states.size)
        for (oldState in states) {
            val div = oldState.div.applyPatch(resolver).getOrElse(0) {
                errorLogger.logError(RuntimeException(
                    "Patch contains empty or invalid div for state '${oldState.stateId}'!"))
                return null
            }
            val newState = DivData.State(div, oldState.stateId)
            list.add(newState)
        }
        if (patch.mode.evaluate(resolver) == DivPatch.Mode.TRANSACTIONAL && appliedPatches.size != patch.patches.size) {
            return null
        }
        return list
    }

    private fun Div.applyPatch(resolver: ExpressionResolver): List<Div> {
        val divId = value().id
        if (divId != null && patch.patches.containsKey(divId)) return applyPatchForSingleDiv()

        val previousApplicationCount = applicationCount
        val updated = when (this) {
            is Div.Container -> applyPatch(value, resolver)
            is Div.Grid -> applyPatch(value, resolver)
            is Div.Gallery -> applyPatch(value, resolver)
            is Div.Pager -> applyPatch(value, resolver)
            is Div.State -> applyPatch(value, resolver)
            is Div.Tabs -> applyPatch(value, resolver)
            is Div.Custom -> applyPatch(value, resolver)
            is Div.GifImage, is Div.Image, is Div.Indicator, is Div.Input,
            is Div.Select, is Div.Separator, is Div.Slider, is Div.Switch,
            is Div.Text, is Div.Video -> this
        }
        if (applicationCount == previousApplicationCount) return listOf(this)
        copies[value()] = updated.value()
        return listOf(updated)
    }

    private fun applyPatchForListOfDivs(divs: List<Div>?, resolver: ExpressionResolver): List<Div>? {
        return divs?.flatMap { it.applyPatch(resolver) }
    }

    private fun Div.applyPatchForSingleDiv(): List<Div> {
        val divId = value().id ?: return listOf(this)
        val patchList = patch.patches[divId]
        if (patchList != null) {
            appliedPatches += divId
            applicationCount++
            return transformItem?.let { patchList.map(it) } ?: patchList
        }
        return listOf(this)
    }

    private fun applyPatch(div: DivContainer, resolver: ExpressionResolver) = Div.Container(
        div.copy(items = applyPatchForListOfDivs(div.items, resolver))
    )

    private fun applyPatch(div: DivGrid, resolver: ExpressionResolver): Div.Grid = Div.Grid(
        div.copy(items = applyPatchForListOfDivs(div.items, resolver))
    )

    private fun applyPatch(div: DivGallery, resolver: ExpressionResolver) = Div.Gallery(
        div.copy(items = applyPatchForListOfDivs(div.items, resolver))
    )

    private fun applyPatch(div: DivPager, resolver: ExpressionResolver) = Div.Pager(
        div.copy(items = applyPatchForListOfDivs(div.items, resolver))
    )

    private fun applyPatch(div: DivState, resolver: ExpressionResolver) = Div.State(
        div.copy(states = applyPatchForListStates(div.states, resolver))
    )

    private fun applyPatch(div: DivCustom, resolver: ExpressionResolver) = Div.Custom(
        div.copy(items = applyPatchForListOfDivs(div.items, resolver))
    )

    private fun applyPatchForListStates(
        states: List<DivState.State>,
        resolver: ExpressionResolver
    ): List<DivState.State> {
        val newStates = mutableListOf<DivState.State>()
        states.forEach { state ->
            val divId = state.div?.value()?.id
            if (divId != null) {
                val patchList = patch.patches[divId]
                if (patchList?.size == 1) {
                    val patchedState = DivState.State(
                        state.animationIn,
                        state.animationOut,
                        transformItem?.invoke(patchList[0]) ?: patchList[0],
                        state.stateId,
                        state.swipeOutActions,
                    )
                    newStates.add(patchedState)
                    appliedPatches += divId
                    applicationCount++
                } else if (patchList != null && patchList.isEmpty()) {
                    appliedPatches += divId
                    applicationCount++
                } else {
                    val newState = state.tryApplyPatchToDiv(resolver)
                    newStates.add(newState)
                }
            } else {
                val newState = state.tryApplyPatchToDiv(resolver)
                newStates.add(newState)
            }
        }
        return newStates
    }

    private fun DivState.State.tryApplyPatchToDiv(
        resolver: ExpressionResolver
    ): DivState.State {
        val newDivs = div?.applyPatch(resolver)
        return if (newDivs?.size == 1) {
            DivState.State(
                animationIn,
                animationOut,
                newDivs[0],
                stateId,
                swipeOutActions
            )
        } else this
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
                KLog.e(TAG) { "Unable to patch tab because there is more than 1 div in the patch" }
                newTabItems.add(tabItem)
            }
        }
        return Div.Tabs(
            div.copy(items = newTabItems)
        )
    }

    companion object {
        const val TAG = "DivPatchApply"
    }
}
