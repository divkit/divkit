package com.yandex.div.core

import androidx.annotation.CallSuper
import com.yandex.div.core.view2.Div2View
import java.lang.ref.WeakReference

/**
 * Provides [Div2View.logId] when image loading fails.
 */
internal abstract class DivIdLoggingImageDownloadCallback(divView: Div2View) : BaseImageDownloadCallback() {

    private val divId = divView.logId
    private val divView = WeakReference(divView)

    override fun getAdditionalLogInfo() = divId

    @CallSuper
    override fun onError(e: Throwable?) {
        e?.let { divView.get()?.logError(it) }
    }
}
