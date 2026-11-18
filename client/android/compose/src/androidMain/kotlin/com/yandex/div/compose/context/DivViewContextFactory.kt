package com.yandex.div.compose.context

import com.yandex.div.compose.di.DivContextScope
import com.yandex.div.compose.di.DivViewComponent
import com.yandex.div2.DivData
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider

@DivContextScope
internal class DivViewContextFactory @Inject constructor(
    private val storage: DivViewContextStorage,
    private val viewComponentBuilderProvider: Provider<DivViewComponent.Builder>,
) {
    fun getOrCreate(data: DivData): DivViewContext {
        return storage.get(data) ?: DivViewContext(
            component = viewComponentBuilderProvider().build(data = data),
        ).also { storage.put(data, it) }
    }
}
