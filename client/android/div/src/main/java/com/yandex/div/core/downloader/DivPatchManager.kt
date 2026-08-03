package com.yandex.div.core.downloader

import androidx.collection.ArrayMap
import com.yandex.div.DivDataTag
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import javax.inject.Inject

@DivScope
internal class DivPatchManager @Inject constructor() {

    private val patches = ArrayMap<DivDataTag, DivPatchMap>()

    private fun putPatch(tag: DivDataTag, patch: DivPatch): DivPatchMap {
        val patchMap = DivPatchMap(patch)
        patches[tag] = patchMap
        return patchMap
    }

    fun createPatchedDivData(
        oldDivData: DivData, divDataTag: DivDataTag, patch: DivPatch,
        resolver: ExpressionResolver,
        divView: Div2View,
    ): DivData? {
        val patchMap = putPatch(divDataTag, patch)
        val states = DivPatchApply(patchMap) {
            divView.logError(it)
        }.applyPatch(oldDivData.states, resolver)
        if (states == null) {
            removePatch(divDataTag)
            return null
        }
        return DivData(
            logId = oldDivData.logId,
            states = states,
            variableTriggers = oldDivData.variableTriggers,
            variables = oldDivData.variables,
            timers = oldDivData.timers
        )
    }

    fun removePatch(tag: DivDataTag) {
        patches.remove(tag)
    }
}
