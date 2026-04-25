package com.yandex.divkit.demo.screenshot

import android.os.Build
import android.view.View

internal fun View.removeAutofocusForOldApis() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        isFocusableInTouchMode = true
    }
}
