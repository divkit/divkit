package com.yandex.div.core.view2.divs.pager

import android.util.DisplayMetrics
import android.view.View
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.core.view2.divs.dpToPxF
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivEdgeInsets

internal class DivPagerPaddingsHolder(
    paddings: DivEdgeInsets?,
    private val resolver: ExpressionResolver,
    parent: View,
    private val metrics: DisplayMetrics,
    isHorizontal: Boolean,
) {

    private val hasRelativePaddings = paddings?.start != null || paddings?.end != null

    val left = when {
        !hasRelativePaddings -> paddings?.left
        parent.isLayoutRtl() -> paddings?.end
        else -> paddings?.start
    }.toPadding()

    val top = paddings?.top.toPadding()

    val right = when {
        !hasRelativePaddings -> paddings?.right
        parent.isLayoutRtl() -> paddings?.start
        else -> paddings?.end
    }.toPadding()

    val bottom = paddings?.bottom.toPadding()

    private fun Expression<Long>?.toPadding() = this?.evaluate(resolver)?.dpToPxF(metrics) ?: 0f

    val start = when {
        !isHorizontal -> top
        parent.isLayoutRtl() -> right
        else -> left
    }

    val end = when {
        !isHorizontal -> bottom
        parent.isLayoutRtl() -> left
        else -> right
    }
}
