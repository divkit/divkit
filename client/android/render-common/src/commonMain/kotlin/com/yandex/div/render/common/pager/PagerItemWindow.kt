package com.yandex.div.render.common.pager

import com.yandex.div.core.annotations.InternalApi

/**
 * Maps a finite set of pager items into a window with optional cyclic copies at both edges.
 * Index conversion requires a positive [realItemCount]; an empty window has no indices.
 */
@InternalApi
public data class PagerItemWindow(
    public val realItemCount: Int,
    public val edgeItemCount: Int = 0,
) {

    public val itemCount: Int
        get() = if (realItemCount == 0) 0 else realItemCount + edgeItemCount * 2

    public fun rawIndex(realIndex: Int): Int =
        normalizePagerIndex(realIndex, realItemCount) + edgeItemCount

    public fun realIndex(rawIndex: Int): Int =
        normalizePagerIndex(rawIndex, realItemCount, edgeItemCount)

    public companion object {
        /**
         * Creates a practically unbounded cyclic window centered far from either physical edge.
         * The edge count is aligned to a full cycle so raw and real indices have stable modulo mapping.
         * [realItemCount] must be positive.
         */
        public fun virtuallyUnbounded(realItemCount: Int): PagerItemWindow {
            val maxEdgeItemCount = (Int.MAX_VALUE - realItemCount) / 2
            val edgeItemCount = maxEdgeItemCount - maxEdgeItemCount.mod(realItemCount)
            return PagerItemWindow(realItemCount, edgeItemCount)
        }
    }
}

/**
 * Maps a raw pager index to its real item in a cyclic window with [edgeItemCount] leading copies.
 * [realItemCount] must be positive. Empty-list fallback belongs to the caller.
 */
@InternalApi
public fun normalizePagerIndex(
    rawIndex: Int,
    realItemCount: Int,
    edgeItemCount: Int = 0,
): Int = (rawIndex - edgeItemCount).mod(realItemCount)
