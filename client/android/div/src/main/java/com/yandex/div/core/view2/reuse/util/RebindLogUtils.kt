package com.yandex.div.core.view2.reuse.util

import com.yandex.div.core.view2.reuse.ExistingToken
import com.yandex.div.core.view2.reuse.NewToken
import com.yandex.div.core.view2.reuse.RebindTask
import com.yandex.div.core.view2.reuse.ReusableTokenList
import com.yandex.div.internal.Log
import com.yandex.div2.Div

internal fun logRebindDiff(
    reusableList: ReusableTokenList,
    bindingPoints: Set<ExistingToken>,
    aloneExisting: List<ExistingToken>,
    aloneNew: List<NewToken>,
) {
    val log = StringBuilder()
    log.appendLine(
        """
        -----------
        Calculated:
        
        bindingPoints [${bindingPoints.size}]: 
        """.trimIndent()
    )
    bindingPoints.forEach {
        it.item.div.printSelf(reusableList, bindingPoints, log)
    }

    log.appendLine("\nreuse [${reusableList.count()}]: ")
    reusableList.asList().forEach {
        it.item.div.printSelf(reusableList, bindingPoints, log)
    }

    log.appendLine("\nremoved [${aloneExisting.count()}]: ")
    aloneExisting.forEach {
        it.item.div.printSelf(reusableList, bindingPoints, log)
    }

    log.appendLine("\nnew [${aloneNew.count()}]: ")
    aloneNew.forEach {
        it.item.div.printSelf(reusableList, bindingPoints, log)
    }

    log.appendLine("-----------")
    Log.d(RebindTask.TAG, log.toString())
}

private fun Div.printSelf(
    reusableList: ReusableTokenList,
    bindingPoints: Set<ExistingToken>,
    builder: StringBuilder,
    step: Int = 1,
) {
    var logString = "|  ".repeat(step - 1) + "|--${value()::class}"
    if (reusableList.contains(this)) {
        logString += " (reusable)"
    }

    bindingPoints.find { it.divHash == this.propertiesHash() }?.let {
        logString += " (binding)"
    }

    logString += when (this) {
        is Div.Text -> ": ${this.value.text.rawValue.toString().replace("\n", " \\n ")}"
        is Div.Image -> ": ${this.value.imageUrl.rawValue}"
        is Div.GifImage -> ": ${this.value.gifUrl.rawValue}"
        is Div.Video -> ": ${this.value.videoSources.firstOrNull()?.url?.rawValue}"
        else -> ""
    }
    builder.appendLine(logString)
    when (this) {
        is Div.Container -> this.value.items?.forEach { it.printSelf(reusableList, bindingPoints, builder, step + 1) }
        is Div.Grid -> this.value.items?.forEach { it.printSelf(reusableList, bindingPoints, builder, step + 1) }
        is Div.Gallery -> this.value.items?.forEach { it.printSelf(reusableList, bindingPoints, builder, step + 1) }
        is Div.Pager -> this.value.items?.forEach { it.printSelf(reusableList, bindingPoints, builder, step + 1) }
        is Div.Tabs -> this.value.items.forEach { it.div.printSelf(reusableList, bindingPoints, builder, step + 1) }
        is Div.State -> this.value.states.forEach { it.div?.printSelf(reusableList, bindingPoints, builder, step + 1) }
        else -> Unit
    }
}
