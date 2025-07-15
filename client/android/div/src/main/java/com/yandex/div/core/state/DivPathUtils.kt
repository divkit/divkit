package com.yandex.div.core.state

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.local.ChildPathUnitCache
import com.yandex.div.core.expression.local.asImpl
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivState

internal object DivPathUtils {

    /**
     * Attempt to receive [DivStateLayout] within given hierarchy of views, searching by
     * [DivStatePath.pathToLastState], so [DivStatePath] equal to 0/first_state/second_div can return
     * [DivStateLayout] with path 0/first_state/first_div, as it very same layout but with different active states.
     */
    @Throws(StateConflictException::class)
    internal fun View.findStateLayout(path: DivStatePath): DivStateLayout? {
        if (this !is ViewGroup) {
            return null
        }
        if (this is DivStateLayout && this.path?.pathToLastState == path.pathToLastState) {
            return this
        }

        var candidate: DivStateLayout? = null

        for (child in children) {
            val childView: DivStateLayout? = child.findStateLayout(path)
            if (childView != null) {
                if (candidate?.path.toString() == childView.path.toString()) {
                    // At this point we have resolved at least 2 DivStateLayouts which
                    // may contain target state. We'll stop here to prevent unexpected
                    // behavior of switching to ambiguous state.
                    throw StateConflictException("Error resolving state for '$path'. " +
                            "Found multiple elements that respond to path '${childView.path}'!")
                }
                candidate = childView
            }
        }
        return candidate
    }

    /**
     * @return Finds corresponding to this path [Div] within given [Div] hierarchy, or null if failed. For example,
     * for [DivStatePath] equal to 0/first_state/first_div/second_state/second_div [Div] corresponding to
     * 0/first_state/first_div/second_state will be returned.
     */
    internal fun Div.findDivState(path: DivStatePath, resolver: ExpressionResolver): Div? {
        val states = path.getStates()
        if (states.isEmpty()) {
            return null
        }
        var foundDiv: Div? = this
        states.forEach { (divId, _) ->
            foundDiv = foundDiv?.findByPath(divId, resolver) ?: return null
        }
        return foundDiv
    }

    /**
     * Returns [DivStateLayout] and [Div.State] found inside the [receiver view][View] by provided
     * path.
     *
     * @receiver the root view of [Div2View][com.yandex.div.core.view2.Div2View]
     *
     * @return
     * - null - if state cannot be switched by passed [path] (if no bound view or div state
     * was found along the provided path)
     * - null and [Div.State] - if cannot find [DivStateLayout] and full
     * [root view][com.yandex.div.core.view2.Div2View] rebind needed
     */
    @Throws(StateConflictException::class)
    internal fun View.tryFindStateDivAndLayout(
        state: DivData.State,
        path: DivStatePath,
        resolver: ExpressionResolver,
    ): Pair<DivStateLayout?, Div.State>? {
        val viewByPath = findStateLayout(path)

        if (viewByPath == null) {
            val parentState = path.parentState()

            // Ignore if the view isn't bound yet
            val isParentUnchangedRoot = parentState.isRootPath() && state.stateId == path.topLevelStateId
            if (isParentUnchangedRoot || findStateLayout(parentState) == null) {
                return null
            }
        }

        val divByPath = viewByPath?.div
            ?: state.div.findDivState(path, resolver) as? Div.State
            ?: return null // Ignore if no such state

        return Pair(viewByPath, divByPath)
    }

    /**
     * If one path is an ancestor of another, keeps only ancestor
     */
    internal fun compactPathList(paths: List<DivStatePath>): List<DivStatePath> {
        if (paths.isEmpty()) {
            return paths
        }

        val sortedPaths = paths.sortedWith(DivStatePath.alphabeticalComparator())
        return sortedPaths.scan(sortedPaths.first()) { acc, path ->
            if (acc.isAncestorOf(path)) acc else path
        }.distinct()
    }

    private fun Div.findByPath(divId: String, resolver: ExpressionResolver): Div? {
        return when (this) {
            is Div.State -> {
                takeIf { value.getId() == divId }
                    ?: value.states.findRecursively(divId, resolver) { it.div }
            }
            is Div.Tabs -> value.items.findRecursively(divId, resolver) { it.div }
            is Div.Container -> value.buildItems(resolver).findRecursively(divId)
            is Div.Grid -> value.nonNullItems.findRecursively(divId, resolver)
            is Div.Gallery -> value.buildItems(resolver).findRecursively(divId)
            is Div.Pager -> value.buildItems(resolver).findRecursively(divId)
            is Div.Custom -> value.items?.findRecursively(divId, resolver)
            is Div.Text -> null
            is Div.Image -> null
            is Div.Slider -> null
            is Div.Input -> null
            is Div.GifImage -> null
            is Div.Indicator -> null
            is Div.Separator -> null
            is Div.Select -> null
            is Div.Video -> null
            is Div.Switch -> null
        }
    }

    private fun <T> Iterable<T>.findRecursively(
        divId: String,
        resolver: ExpressionResolver,
        getDiv: (T) -> Div? = { it as Div? },
    ): Div? = firstNotNullOfOrNull { getDiv(it)?.findByPath(divId, resolver) }

    private fun Iterable<DivItemBuilderResult>.findRecursively(divId: String): Div? =
        firstNotNullOfOrNull { (div, resolver) -> div.findByPath(divId, resolver) }

    internal fun DivState.getId(errorCallback: (() -> Unit)? = null): String = divId ?: id ?: run {
        errorCallback?.invoke()
        ""
    }

    fun List<Div>.getIds() = getIds({ this })

    fun List<DivItemBuilderResult>.getItemIds() = getIds({ div }, { expressionResolver.asImpl })

    fun <T> List<T>.getIds(div: T.() -> Div, resolver: T.() -> ExpressionResolverImpl? = { null }): List<String> {
        val idsCount = mutableMapOf<String, Int>()
        forEach {
            val id = it.div().getId() ?: return@forEach
            idsCount[id] = (idsCount[id] ?: 0) + 1
        }
        return mapIndexed { index, item ->
            item.div().getId()?.let { if ((idsCount[it] ?: 0) > 1) "$it#$index" else it }
                ?: item.resolver()?.itemBuilderData
                ?: ChildPathUnitCache.getValue(index)
        }
    }

    fun Div.getId() = value().run { if (this is DivState) getId() else id }
}

internal class StateConflictException(message: String, cause: Throwable? = null) : Exception(message, cause)
