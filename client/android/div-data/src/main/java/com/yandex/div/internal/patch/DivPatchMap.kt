package com.yandex.div.internal.patch

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div2.Div
import com.yandex.div2.DivPatch

@InternalApi
class DivPatchMap(divPatch: DivPatch) {

    val patches: Map<String, List<Div>> = divPatch.changes.associate {
        it.id to it.items.orEmpty()
    }
    val mode = divPatch.mode
}
