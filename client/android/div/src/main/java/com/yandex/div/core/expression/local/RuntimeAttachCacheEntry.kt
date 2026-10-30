package com.yandex.div.core.expression.local

import com.yandex.div.core.state.DivStatePath
import com.yandex.div2.Div

/**
 * Identifies a completed traversal whose repeated window attachment can be skipped.
 */
internal class RuntimeAttachCacheEntry(
    private val root: Div,
    private val store: RuntimeStore,
    private val path: DivStatePath,
) {
    fun matches(root: Div, store: RuntimeStore, path: DivStatePath): Boolean =
        this.root === root && this.store === store && this.path == path
}
