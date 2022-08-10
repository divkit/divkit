package com.yandex.div.core.downloader

import androidx.collection.ArrayMap
import com.yandex.div.DivDataTag
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div2.Div
import com.yandex.div2.DivPatch
import javax.inject.Inject

@DivScope
@Mockable
internal class DivPatchCache @Inject internal constructor() {

    private val patches = ArrayMap<DivDataTag, DivPatchMap>()

    fun getPatchDivListById(tag: DivDataTag, id: String): List<Div>? {
        val patch = patches[tag] ?: return null
        return patch.patches[id]
    }

    fun getPatch(tag: DivDataTag): DivPatchMap? {
        return patches[tag]
    }

    fun putPatch(tag: DivDataTag, patch: DivPatch): DivPatchMap {
        val patchMap = DivPatchMap(patch)
        patches[tag] = patchMap
        return patchMap
    }

    fun removePatch(tag: DivDataTag) {
        patches.remove(tag)
    }

}
