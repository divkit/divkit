package com.yandex.div.core.view2.divs.widgets

import androidx.annotation.MainThread
import com.yandex.div2.DivVideoScale

internal class DivVideoBindingController {

    private var previewBindingGeneration = 0
    private var pendingPreviewBinding: PreviewBinding? = null
    private var appliedPreviewBinding: PreviewBinding? = null

    @MainThread
    fun release() {
        cancelPendingPreviewBinding()
    }

    @MainThread
    fun invalidatePendingPreviewBindingIfChanged(preview: String?, scale: DivVideoScale) {
        val pendingBinding = pendingPreviewBinding ?: return
        if (pendingBinding != PreviewBinding(preview, scale)) {
            cancelPendingPreviewBinding()
        }
    }

    @MainThread
    fun beginPreviewBinding(preview: String?, scale: DivVideoScale): Int? {
        val binding = PreviewBinding(preview, scale)
        if (binding == pendingPreviewBinding ||
            (binding == appliedPreviewBinding && pendingPreviewBinding == null)
        ) {
            return null
        }

        previewBindingGeneration++
        pendingPreviewBinding = binding
        return previewBindingGeneration
    }

    @MainThread
    fun isPreviewBindingApplied(preview: String?, scale: DivVideoScale): Boolean {
        return pendingPreviewBinding == null && appliedPreviewBinding == PreviewBinding(preview, scale)
    }

    @MainThread
    fun isPreviewBindingPending(): Boolean = pendingPreviewBinding != null

    @MainThread
    fun completePreviewBinding(generation: Int): Boolean {
        if (generation != previewBindingGeneration) {
            return false
        }

        appliedPreviewBinding = pendingPreviewBinding
        pendingPreviewBinding = null
        return true
    }

    @MainThread
    fun discardPreviewBinding(generation: Int): Boolean {
        if (generation != previewBindingGeneration) {
            return false
        }

        pendingPreviewBinding = null
        return true
    }

    private fun cancelPendingPreviewBinding() {
        previewBindingGeneration++
        pendingPreviewBinding = null
    }

    private data class PreviewBinding(
        val preview: String?,
        val scale: DivVideoScale,
    )
}
