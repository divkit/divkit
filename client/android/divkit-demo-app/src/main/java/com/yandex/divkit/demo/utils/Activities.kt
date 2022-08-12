
package com.yandex.divkit.demo.utils

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.yandex.div.core.util.KAssert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.EmptyCoroutineContext

val Activity.lifecycleOwner: LifecycleOwner?
    get() = this as? LifecycleOwner

val Activity.coroutineScope: CoroutineScope
    get() = lifecycleOwner?.lifecycleScope ?: CoroutineScope(EmptyCoroutineContext + Dispatchers.Main).also {
        KAssert.fail { "Using an Activity that is not LifecycleOwner: $this" }
    }
