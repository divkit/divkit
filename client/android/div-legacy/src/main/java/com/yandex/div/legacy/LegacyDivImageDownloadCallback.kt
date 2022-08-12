package com.yandex.div.legacy

import com.yandex.div.legacy.view.DivView
import com.yandex.images.ImageDownloadCallback

internal open class LegacyDivImageDownloadCallback(
    private val divId: String
    ) : ImageDownloadCallback() {

    constructor(divView: DivView) : this(divView.divTag.id)

    override fun getAdditionalLogInfo() = divId
}
