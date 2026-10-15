package com.yandex.div.core.tooltip

import android.view.View
import com.yandex.div.core.state.DivStatePath
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
    val tooltipBlock: DivBlock = divTooltip.div.toBlock("tooltip")
    val substrateBlock: DivBlock? = divTooltip.substrateDiv?.toBlock("tooltip_substrate")
    val pendingStatePaths = mutableListOf<DivStatePath>()

    private fun Div.toBlock(kind: String): DivBlock {
        val pathPrefix = "$kind#${divTooltip.id}"
        val pathSegment = value().id?.let { "$pathPrefix#$it" } ?: pathPrefix
        val namespace = "$kind#${divTooltip.id.length}#${divTooltip.id}" +
            scopeId?.let { "#${it.length}#$it" }.orEmpty()
        val path = anchorBlock.path.withNamespace(namespace).appendDiv(pathSegment)
        val resolver = divView.runtimeStore.getOrCreateRuntime(path.fullPath, this, anchorResolver)
            .expressionResolver
        return DivBlock.create(this, resolver, path)
    }
}
