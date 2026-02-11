package com.yandex.div.core

import com.yandex.div.core.view2.Div2View

/**
 * Provides [Div2View.logId] when image loading fails.
 */
internal abstract class DivIdLoggingImageDownloadCallback(private val divId: String) : BaseImageDownloadCallback() {
    constructor(divView: Div2View) : this(divView.logId)

    override fun getAdditionalLogInfo() = divId
}
