package com.yandex.div.core.base.compat

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi

/**
 * Utility class to use new APIs that were added in [Build.VERSION_CODES#M].
 * These need to exist in a separate class so that Android framework can successfully verify
 * classes without encountering the new APIs.
 */
@RequiresApi(Build.VERSION_CODES.M)
object ApiHelperForM {

    fun removeForeground(view: View) {
        view.foreground = null
    }
}
