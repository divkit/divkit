package com.yandex.div.core.tooltip

import android.view.View
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.core.DivBlock
import com.yandex.div2.Div
import com.yandex.div2.DivTooltip

internal class TooltipData(
    val id: String,
    val scopeId: String?,
    val divTooltip: DivTooltip,
    val anchor: View,
    private val anchorBlock: DivBlock,
    val divView: Div2View,
) {
    val anchorResolver = anchorBlock.expressionResolver
    val tooltipBlock: DivBlock = divTooltip.div.toBlock("tooltip#${divTooltip.id}")
    val substrateBlock: DivBlock? = divTooltip.substrateDiv?.toBlock("tooltip_substrate#${divTooltip.id}")

    private fun Div.toBlock(pathPrefix: String): DivBlock {
        val pathSegment = value().id?.let { "$pathPrefix#$it" } ?: pathPrefix
        val path = anchorBlock.path.appendDiv(pathSegment)
        val resolver = divView.runtimeStore.getOrCreateRuntime(path.fullPath, this, anchorResolver)
            .expressionResolver
        return DivBlock.create(this, resolver, path)
    }
}
