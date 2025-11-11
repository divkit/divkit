package com.yandex.div.core.downloader

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.view2.divs.gallery.DivGalleryAdapter
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.KLog
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivData
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGrid
import com.yandex.div2.DivPager
import com.yandex.div2.DivPatch
import com.yandex.div2.DivState
import com.yandex.div2.DivTabs

internal class DivPatchApply(
    private val patch: DivPatchMap,
    private val errorLogger: ParsingErrorLogger,
) {
    private val appliedPatches = mutableSetOf<String>()

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
            is Div.Custom -> applyPatch(value, resolver)
            else -> this
        })
    }

    private fun applyPatchForListOfDivs(divs: List<Div>?, resolver: ExpressionResolver): List<Div>? {
        return divs?.flatMap { it.applyPatch(resolver) }
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
        div.copy(items = applyPatchForListOfDivs(div.items, resolver))
    )

    private fun applyPatch(div: DivGrid, resolver: ExpressionResolver): Div.Grid  = Div.Grid(
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
                KLog.e(TAG) { "Unable to patch tab because there is more than 1 div in the patch" }
                newTabItems.add(tabItem)
            }
        }
        return Div.Tabs(
            div.copy(items = newTabItems)
        )
    }

    /**
     * Returns div with patched child if has such, otherwise null.
     */
    fun patchDivChild(
        parentView: View,
        parentDiv: Div,
        idToPatch: String,
        resolver: ExpressionResolver
    ): Div? {
        val pathToChild = pathToChildWithId(parentDiv, idToPatch, resolver)
        val iterator = pathToChild.iterator()
        if (pathToChild.isEmpty()) return null

        iterator.next()

        // Notify internal recycler about changes if needed.
        pathToChild.findLast { it is Div.Gallery || it is Div.Pager }?.let {
            findPatchedRecyclerViewAndNotifyChange(parentView, it, idToPatch)
        }

        return getPatchedTreeByPath(parentDiv, iterator, resolver)
    }

    private fun pathToChildWithId(
        currentDiv: Div,
        idToFind: String,
        resolver: ExpressionResolver,
        currentPath: MutableList<Div> = mutableListOf()
    ): List<Div> {
        currentPath.add(currentDiv)
        return when (val currentDivValue = currentDiv.value()) {
            is DivContainer ->
                currentDivValue.nonNullItems
                    .pathToChildWithId(idToFind, resolver, currentPath)

            is DivGrid ->
                currentDivValue.nonNullItems
                    .pathToChildWithId(idToFind, resolver, currentPath)

            is DivGallery ->
                currentDivValue.nonNullItems
                    .pathToChildWithId(idToFind, resolver, currentPath)

            is DivPager ->
                currentDivValue.nonNullItems
                    .pathToChildWithId(idToFind, resolver, currentPath)

            is DivTabs -> {
                if (currentDivValue.items.any { it.div.value().id == idToFind }) {
                    currentPath
                } else {
                    currentDivValue.items.forEach {
                        val newPath = pathToChildWithId(it.div, idToFind, resolver, currentPath)
                        if (newPath.isNotEmpty()) return newPath else currentPath.removeAt(currentPath.lastIndex)
                    }
                    emptyList()
                }
            }

            is DivState -> {
                if (currentDivValue.states.any { it.div?.value()?.id == idToFind }) {
                    currentPath
                } else {
                    currentDivValue.states.mapNotNull { it.div }.forEach {
                        val newPath = pathToChildWithId(it, idToFind, resolver, currentPath)
                        if (newPath.isNotEmpty()) return newPath else currentPath.removeAt(currentPath.lastIndex)
                    }
                    emptyList()
                }
            }

            else -> emptyList()
        }
    }

    private fun List<Div>.pathToChildWithId(
        idToFind: String,
        resolver: ExpressionResolver,
        currentPath: MutableList<Div> = mutableListOf(),
    ): List<Div> {
        if (any { it.value().id == idToFind }) return currentPath

        forEach {
            val newPath = pathToChildWithId(it, idToFind, resolver, currentPath)
            if (newPath.isNotEmpty()) return newPath else currentPath.removeAt(currentPath.lastIndex)
        }
        return emptyList()
    }

    private fun getPatchedTreeByPath(
        currentDiv: Div,
        pathIterator: Iterator<Div>,
        resolver: ExpressionResolver
    ): Div {
        return when (val currentDivValue = currentDiv.value()) {
            is DivContainer -> {
                getPatchedDivCollection(
                    currentDiv,
                    currentDivValue.nonNullItems,
                    pathIterator,
                    resolver,
                    { Div.Container(currentDivValue.copy(items = it)) },
                    { DivPatchApply(patch, errorLogger).applyPatch(currentDivValue, resolver) }
                )
            }

            is DivGrid -> {
                getPatchedDivCollection(
                    currentDiv,
                    currentDivValue.nonNullItems,
                    pathIterator,
                    resolver,
                    { Div.Grid(currentDivValue.copy(items = it)) },
                    { DivPatchApply(patch, errorLogger).applyPatch(currentDivValue, resolver) }
                )
            }

            is DivGallery -> {
                getPatchedDivCollection(
                    currentDiv,
                    currentDivValue.nonNullItems,
                    pathIterator,
                    resolver,
                    { Div.Gallery(currentDivValue.copy(items = it)) },
                    { DivPatchApply(patch, errorLogger).applyPatch(currentDivValue, resolver) }
                )
            }

            is DivPager -> {
                getPatchedDivCollection(
                    currentDiv,
                    currentDivValue.nonNullItems,
                    pathIterator,
                    resolver,
                    { Div.Pager(currentDivValue.copy(items = it)) },
                    { DivPatchApply(patch, errorLogger).applyPatch(currentDivValue, resolver) }
                )
            }

            is DivTabs -> {
                if (pathIterator.hasNext()) {
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
                    Div.Tabs(currentDivValue.copy(items = newItems))
                } else {
                    DivPatchApply(patch, errorLogger).applyPatch(currentDivValue, resolver)
                }
            }

            is DivState -> {
                if (pathIterator.hasNext()) {
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

                    Div.State(currentDivValue.copy(states = newItems))
                } else {
                    DivPatchApply(patch, errorLogger).applyPatch(currentDivValue, resolver)
                }
            }

            else -> currentDiv
        }
    }

    private fun getPatchedDivCollection(
        currentDiv: Div,
        items: List<Div>,
        pathIterator: Iterator<Div>,
        resolver: ExpressionResolver,
        createPatchedDiv: (List<Div>) -> Div,
        patchDiv: () -> Div
    ): Div {
        if (!pathIterator.hasNext()) return patchDiv()

        val find = pathIterator.next()
        val replaceIndex = items.indexOf(find)
        if (replaceIndex == -1) {
            KAssert.fail { PATH_FOLLOWING_ERROR }
            return currentDiv
        }

        val newItems = items.toMutableList()
        newItems[replaceIndex] = getPatchedTreeByPath(newItems[replaceIndex], pathIterator, resolver)
        return createPatchedDiv(newItems)
    }

    private fun findPatchedRecyclerViewAndNotifyChange(
        currentView: View,
        divWithPatchedChild: Div,
        patchedChildId: String
    ): View? {
        when(currentView) {
            is DivRecyclerView -> {
                if (currentView.div === divWithPatchedChild) {
                    val adapter = (currentView.adapter as? DivGalleryAdapter) ?: return currentView
                    currentView.div?.value?.items?.forEachIndexed { i, child ->
                        if (child.value().id == patchedChildId) {
                            adapter.notifyItemChanged(i)
                            return currentView
                        }
                    }
                    return currentView
                }
            }
            is DivPagerView -> {
                if (currentView.div === divWithPatchedChild) {
                    val adapter = (currentView.viewPager.getChildAt(0) as? RecyclerView)?.adapter ?: return currentView
                    currentView.div?.value?.items?.forEachIndexed { i, child ->
                        if (child.value().id == patchedChildId) {
                            adapter.notifyItemChanged(i)
                            return currentView
                        }
                    }
                    return currentView
                }
            }
        }
        if (currentView is ViewGroup) {
            currentView.children.forEach { child ->
                findPatchedRecyclerViewAndNotifyChange(child, divWithPatchedChild, patchedChildId)?.let {
                    return it
                }
            }
        }
        return null
    }

    companion object {
        const val TAG = "DivPatchApply"
        private const val PATH_FOLLOWING_ERROR = "Unable to find the next child to patch by following a precalculated path"
    }
}
