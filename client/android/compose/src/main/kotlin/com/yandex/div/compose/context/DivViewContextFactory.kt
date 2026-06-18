package com.yandex.div.compose.context

import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div.compose.dagger.DivViewComponent
import com.yandex.div2.DivData
import javax.inject.Inject
import javax.inject.Provider

@DivContextScope
internal class DivViewContextFactory @Inject constructor(
    private val storage: DivViewContextStorage,
    private val viewComponentBuilderProvider: Provider<DivViewComponent.Builder>,
) {
    fun getOrCreate(data: DivData): DivViewContext {
        return storage.get(data) ?: DivViewContext(
            data = data,
            component = viewComponentBuilderProvider.get().build(cardId = data.logId),
        ).also { storage.put(data, it) }
    }
}
