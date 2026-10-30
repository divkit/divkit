package com.yandex.div.core.expression.local

import com.yandex.div.core.dagger.DivDataScope
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.Div
import javax.inject.Inject

/**
 * Caches completed no-op reattachments and owns their invalidation for one data component.
 */
@DivDataScope
internal class RuntimeAttachCacheManager @Inject constructor(
    private val visitor: DivRuntimeVisitor,
) {
    @Volatile
    private var entry: RuntimeAttachCacheEntry? = null

    fun invalidate() {
        entry = null
    }

    /**
     * Only window reattachment can reuse an entry. Binding must always traverse the model.
     */
    fun reattachRuntimes(rootDiv: Div, rootPath: DivStatePath, divView: Div2View) {
        val store = divView.runtimeStore
        if (entry?.matches(rootDiv, store, rootPath) == true) return
        entry = null
        if (visitor.createAndAttachRuntimes(rootDiv, rootPath, divView)) {
            entry = RuntimeAttachCacheEntry(rootDiv, store, rootPath)
        }
    }
}
