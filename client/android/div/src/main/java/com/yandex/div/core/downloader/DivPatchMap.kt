package com.yandex.div.core.downloader

import com.yandex.div.util.arrayMap
import com.yandex.div2.Div
import com.yandex.div2.DivPatch

internal class DivPatchMap(divPatch: DivPatch) {

    val patches = arrayMap<String, List<Div>>()
    val mode = divPatch.mode

    init {
        divPatch.changes.forEach { patches[it.id] = it.items ?: emptyList() }
    }

}