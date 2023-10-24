package com.yandex.div.core.downloader

import android.view.View
import com.yandex.div.DivDataTag
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2Builder
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import javax.inject.Inject
import javax.inject.Provider

@DivScope
@Mockable
internal class DivPatchManager @Inject constructor(
    private val divPatchCache: DivPatchCache,
    private val divViewCreator: Provider<Div2Builder>
)  {

    private fun putPatch(tag: DivDataTag, patch: DivPatch): DivPatchMap {
        return divPatchCache.putPatch(tag, patch)
    }

    fun createViewsForId(rootView: Div2View, id: String): List<View>? {
        val patchDivs = divPatchCache.getPatchDivListById(rootView.dataTag, id) ?: return null
        val listViews = mutableListOf<View>()
        patchDivs.forEach {
            val view = divViewCreator.get().buildView(it, rootView, DivStatePath.fromState(rootView.currentStateId))
            listViews.add(view)
        }
        return listViews
    }

    fun createPatchedDivData(oldDivData: DivData, divDataTag: DivDataTag, patch: DivPatch,
                             resolver: ExpressionResolver): DivData? {
        val patchMap = putPatch(divDataTag, patch)
        val states = DivPatchApply(patchMap).applyPatch(oldDivData.states, resolver)
        if (states == null) {
            removePatch(divDataTag)
            return null
        }
        return DivData(
            logId = oldDivData.logId,
            states = states,
            variableTriggers = oldDivData.variableTriggers
        )
    }

    fun removePatch(tag: DivDataTag) {
        divPatchCache.removePatch(tag)
    }
}
