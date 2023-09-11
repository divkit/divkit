package com.yandex.div.core.downloader

import com.yandex.div.internal.KAssert
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

    private fun applyPatch(div: DivContainer, resolver: ExpressionResolver) = Div.Container(
        div.copyWithNewArray(applyPatchForListOfDivs(div.items, resolver))
    )

    private fun applyPatch(div: DivGrid, resolver: ExpressionResolver): Div.Grid  = Div.Grid(
        div.copyWithNewArray(applyPatchForListOfDivs(div.items, resolver))
    )

    private fun applyPatch(div: DivGallery, resolver: ExpressionResolver) = Div.Gallery(
        div.copyWithNewArray(applyPatchForListOfDivs(div.items, resolver))
    )

    private fun applyPatch(div: DivPager, resolver: ExpressionResolver) = Div.Pager(
        div.copyWithNewArray(applyPatchForListOfDivs(div.items, resolver))
    )

    private fun applyPatch(div: DivState, resolver: ExpressionResolver) = Div.State(
        div.copyWithNewArray(applyPatchForListStates(div.states, resolver))
    )

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
                newTabItems.add(tabItem)
            }
        }
        return Div.Tabs(
            div.copyWithNewArray(newTabItems)
        )
    }

    /**
     * Returns div with patched child if has such, otherwise null.
     */
    fun patchDivChild(
        parentDiv: Div,
        idToPatch: String,
        resolver: ExpressionResolver
    ): Div? {
        val pathToChild = pathToChildWithId(parentDiv, idToPatch)
        val iterator = pathToChild.iterator()
        return if (pathToChild.isNotEmpty()) {
            iterator.next()
            return getPatchedTreeByPath(parentDiv, iterator, resolver)
        } else {
            null
        }
    }

    private fun pathToChildWithId(
        currentDiv: Div,
        idToFind: String,
        currentPath: MutableList<Div> = mutableListOf()
    ): List<Div> {
        currentPath.add(currentDiv)
        when (val currentDivValue = currentDiv.value()) {
            is DivContainer -> {
                if (currentDivValue.items.any { it.value().id == idToFind }) {
                    return currentPath
                } else {
                    currentDivValue.items.forEach {
                        val newPath = pathToChildWithId(it, idToFind, currentPath)
                        if (newPath.isNotEmpty()) return newPath
                    }
                    return emptyList()
                }
            }

            is DivGrid -> {
                if (currentDivValue.items.any { it.value().id == idToFind }) {
                    return currentPath
                } else {
                    currentDivValue.items.forEach {
                        val newPath = pathToChildWithId(it, idToFind, currentPath)
                        if (newPath.isNotEmpty()) return newPath
                    }
                    return emptyList()
                }
            }

            is DivGallery -> {
                if (currentDivValue.items.any { it.value().id == idToFind }) {
                    return currentPath
                } else {
                    currentDivValue.items.forEach {
                        val newPath = pathToChildWithId(it, idToFind, currentPath)
                        if (newPath.isNotEmpty()) return newPath
                    }
                    return emptyList()
                }
            }

            is DivPager -> {
                if (currentDivValue.items.any { it.value().id == idToFind }) {
                    return currentPath
                } else {
                    currentDivValue.items.forEach {
                        val newPath = pathToChildWithId(it, idToFind, currentPath)
                        if (newPath.isNotEmpty()) return newPath
                    }
                    return emptyList()
                }
            }

            is DivTabs -> {
                if (currentDivValue.items.any { it.div.value().id == idToFind }) {
                    return currentPath
                } else {
                    currentDivValue.items.forEach {
                        val newPath = pathToChildWithId(it.div, idToFind, currentPath)
                        if (newPath.isNotEmpty()) return newPath
                    }
                    return emptyList()
                }
            }

            is DivState -> {
                if (currentDivValue.states.any { it.div?.value()?.id == idToFind }) {
                    return currentPath
                } else {
                    currentDivValue.states.mapNotNull { it.div }.forEach {
                        val newPath = pathToChildWithId(it, idToFind, currentPath)
                        if (newPath.isNotEmpty()) return newPath
                    }
                    return emptyList()
                }
            }

            else -> {
                return emptyList()
            }
        }
    }

    private fun getPatchedTreeByPath(
        currentDiv: Div,
        pathIterator: Iterator<Div>,
        resolver: ExpressionResolver
    ): Div {
        when (val currentDivValue = currentDiv.value()) {
            is DivContainer -> {
                return if (pathIterator.hasNext()) {
                    val newItems = currentDivValue.items.toMutableList()
                    val find = pathIterator.next()
                    val replaceIndex = newItems.indexOf(find)
                    if (replaceIndex == -1) {
                        KAssert.fail { PATH_FOLLOWING_ERROR }
                        return currentDiv
                    }

                    newItems[replaceIndex] = getPatchedTreeByPath(newItems[replaceIndex], pathIterator, resolver)
                    Div.Container(currentDivValue.copyWithNewArray(newItems))
                } else {
                    DivPatchApply(patch).applyPatch(currentDivValue, resolver)
                }
            }

            is DivGrid -> {
                return if (pathIterator.hasNext()) {
                    val newItems = currentDivValue.items.toMutableList()
                    val replaceIndex = newItems.indexOf(pathIterator.next())
                    if (replaceIndex == -1) {
                        KAssert.fail { PATH_FOLLOWING_ERROR }
                        return currentDiv
                    }

                    newItems[replaceIndex] = getPatchedTreeByPath(newItems[replaceIndex], pathIterator, resolver)
                    Div.Grid(currentDivValue.copyWithNewArray(newItems))
                } else {
                    DivPatchApply(patch).applyPatch(currentDivValue, resolver)
                }
            }

            is DivGallery -> {
                return if (pathIterator.hasNext()) {
                    val newItems = currentDivValue.items.toMutableList()
                    val replaceIndex = newItems.indexOf(pathIterator.next())
                    if (replaceIndex == -1) {
                        KAssert.fail { PATH_FOLLOWING_ERROR }
                        return currentDiv
                    }

                    newItems[replaceIndex] = getPatchedTreeByPath(newItems[replaceIndex], pathIterator, resolver)
                    Div.Gallery(currentDivValue.copyWithNewArray(newItems))
                } else {
                    DivPatchApply(patch).applyPatch(currentDivValue, resolver)
                }
            }

            is DivPager -> {
                return if (pathIterator.hasNext()) {
                    val newItems = currentDivValue.items.toMutableList()
                    val replaceIndex = newItems.indexOf(pathIterator.next())
                    if (replaceIndex == -1) {
                        KAssert.fail { PATH_FOLLOWING_ERROR }
                        return currentDiv
                    }

                    newItems[replaceIndex] = getPatchedTreeByPath(newItems[replaceIndex], pathIterator, resolver)
                    Div.Pager(currentDivValue.copyWithNewArray(newItems))
                } else {
                    DivPatchApply(patch).applyPatch(currentDivValue, resolver)
                }
            }

            is DivTabs -> {
                return if (pathIterator.hasNext()) {
                    val newItems = currentDivValue.items.toMutableList()
                    val replaceIndex = newItems.map { it.div }.indexOf(pathIterator.next())
                    if (replaceIndex == -1) {
                        KAssert.fail { PATH_FOLLOWING_ERROR }
                        return currentDiv
                    }

                    val itemToReplace = newItems[replaceIndex]
                    newItems[replaceIndex] = DivTabs.Item(
                        getPatchedTreeByPath(itemToReplace.div, pathIterator, resolver),
                        itemToReplace.title,
                        itemToReplace.titleClickAction
                    )
                    Div.Tabs(currentDivValue.copyWithNewArray(newItems))
                } else {
                    DivPatchApply(patch).applyPatch(currentDivValue, resolver)
                }
            }

            is DivState -> {
                return if (pathIterator.hasNext()) {
                    val newItems = currentDivValue.states.toMutableList()
                    val replaceIndex = newItems.map { it.div }.indexOf(pathIterator.next())
                    if (replaceIndex == -1) {
                        KAssert.fail { PATH_FOLLOWING_ERROR }
                        return currentDiv
                    }

                    val itemToReplace = newItems[replaceIndex]
                    val itemToReplaceDiv = itemToReplace.div ?: return currentDiv

                    newItems[replaceIndex] = DivState.State(
                        itemToReplace.animationIn,
                        itemToReplace.animationOut,
                        getPatchedTreeByPath(itemToReplaceDiv, pathIterator, resolver),
                        itemToReplace.stateId,
                        itemToReplace.swipeOutActions
                    )

                    Div.State(currentDivValue.copyWithNewArray(newItems))
                } else {
                    DivPatchApply(patch).applyPatch(currentDivValue, resolver)
                }
            }

            else -> {
                return currentDiv
            }
        }
    }

    companion object {
        private const val PATH_FOLLOWING_ERROR = "Unable to find the next child to patch by following a precalculated path"
    }
}
