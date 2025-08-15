package com.yandex.div.core.view2.logging.patch

import androidx.annotation.StringDef

@StringDef(value = [
    PatchResult.SUCCESS,
    PatchResult.FAIL_NO_STATE,
])
annotation class PatchResult {
    companion object {
        const val SUCCESS = "Div patched successfully"
        const val FAIL_NO_STATE = "Patch not performed. Cannot find state to bind"
    }
}
