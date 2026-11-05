package com.yandex.div.internal.core

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.json.expressions.ExpressionResolver

/**
 * Supplies the current card tree and action-source metadata used to search it.
 */
@InternalApi
interface DivBlockSearchEnvironment {
    /**
     * Returns the root block of the card's current state, or null when no state is available.
     */
    fun rootBlock(): DivBlock?

    /**
     * Visits [root] and its descendants in search order.
     *
     * The result includes [root]. Every returned block contains the resolver and path that are
     * current for this search.
     */
    fun findDescendants(root: DivBlock): Iterable<DivBlock>

    /**
     * Returns the paths of the card's currently active nested states.
     */
    fun activeStatesPaths(): Set<DivStatePath>

    /**
     * Returns paths associated with [resolver].
     */
    fun findPathsFor(resolver: ExpressionResolver): Collection<DivStatePath>

    /**
     * Returns the item builder segment for [resolver], or null when it is unavailable.
     */
    fun itemBuilderSegment(resolver: ExpressionResolver): String?
}
