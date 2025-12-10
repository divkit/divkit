package com.yandex.divkit.demo.utils

import com.yandex.div.DivDataTag
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import com.yandex.divkit.demo.Container

internal fun Div2View.applyPatchByConfig(patch: DivPatch, onComplete: ((Boolean) -> Unit)?) {
    if (Container.preferences.useBackgroundBinding) {
        applyPatchAsync(patch, onComplete)
    } else {
        val result = applyPatch(patch)
        onComplete?.invoke(result)
    }
}

internal fun Div2View.setDataByConfig(divData: DivData?, tag: DivDataTag, onComplete: ((Boolean) -> Unit)?) {
    if (Container.preferences.useBackgroundBinding) {
        setDataAsync(divData, tag, onComplete)
    } else {
        val result = setData(divData, tag)
        onComplete?.invoke(result)
    }
}
