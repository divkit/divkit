package com.yandex.div.compose.pager

/** Maps a finite set of pager items into a window with optional cyclic copies at both edges. */
internal data class PagerItemWindow(
    val realItemCount: Int,
    val edgeItemCount: Int = 0,
) {

    val itemCount: Int
        get() = if (realItemCount == 0) 0 else realItemCount + edgeItemCount * 2

    fun rawIndex(realIndex: Int): Int = realIndex.mod(realItemCount) + edgeItemCount

    fun realIndex(rawIndex: Int): Int = (rawIndex - edgeItemCount).mod(realItemCount)

    companion object {
        /**
         * Creates a practically unbounded cyclic window centered far from either physical edge.
         * The edge count is aligned to a full cycle so raw and real indices have stable modulo mapping.
         */
        fun virtuallyUnbounded(realItemCount: Int): PagerItemWindow {
            val maxEdgeItemCount = (Int.MAX_VALUE - realItemCount) / 2
            val edgeItemCount = maxEdgeItemCount - maxEdgeItemCount.mod(realItemCount)
            return PagerItemWindow(realItemCount, edgeItemCount)
        }
    }
}
