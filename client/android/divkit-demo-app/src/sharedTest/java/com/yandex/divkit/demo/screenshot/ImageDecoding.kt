package com.yandex.divkit.demo.screenshot

import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.core.view.children
import com.yandex.div.core.view2.divs.widgets.LoadableImage

@MainThread
internal fun View.hasPendingImageDecoding(): Boolean {
    // A completed Future can still be waiting for its result to be applied on the main thread.
    if (this is LoadableImage && getLoadingTask() != null) {
        return true
    }
    return this is ViewGroup && children.any { it.hasPendingImageDecoding() }
}
