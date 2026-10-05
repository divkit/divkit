package com.yandex.div.core.util

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.state.DivStatePath

@InternalApi
fun interface ActiveStateProvider {
    fun activeStatesPaths(): Set<DivStatePath>
}
