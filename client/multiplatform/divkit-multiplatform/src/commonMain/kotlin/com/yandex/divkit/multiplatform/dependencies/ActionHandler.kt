package com.yandex.divkit.multiplatform.dependencies

import com.yandex.divkit.multiplatform.DivKit
import com.yandex.divkit.multiplatform.NoOpDivKit

interface ActionHandler {

    @Deprecated("Use handle(DivKitScope, String) instead.")
    fun handle(url: String) = handle(NoOpDivKit, url)

    fun handle(divKit: DivKit, url: String)
}
