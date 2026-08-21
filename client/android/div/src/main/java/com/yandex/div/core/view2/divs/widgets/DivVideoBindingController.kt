package com.yandex.div.core.view2.divs.widgets

import android.view.View
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div2.DivVideoScale

internal class DivVideoBindingController(
    private val hostView: View,
    private val resetVideoBinding: () -> Unit,
) {

    private var videoBindingGeneration = 0
    private var previewBindingGeneration = 0
    private var pendingPreviewBinding: PreviewBinding? = null
    private var appliedPreviewBinding: PreviewBinding? = null
    private var pendingPlayerInitialization: PendingPlayerInitialization? = null
    private val observedRecyclerViews = mutableSetOf<RecyclerView>()
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (recyclerView !in observedRecyclerViews) {
                recyclerView.removeOnScrollListener(this)
                return
            }
            if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                return
            }

            resumePendingPlayerInitialization()
        }
    }

    @MainThread
    fun release() {
        videoBindingGeneration++
        cancelPendingPreviewBinding()
        cancelPendingPlayerInitialization()
        resetVideoBinding()
    }

    @MainThread
    fun beginVideoBinding(): Int {
        videoBindingGeneration++
        cancelPendingPlayerInitialization()
        resetVideoBinding()
        return videoBindingGeneration
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

    @MainThread
    fun initializePlayerWhenIdle(
        generation: Int,
        initializer: () -> Unit,
    ) {
        if (!isVideoBindingCurrent(generation)) {
            return
        }

        pendingPlayerInitialization = PendingPlayerInitialization(generation, initializer)
        resumePendingPlayerInitialization()
    }

    @MainThread
    fun onAttachedToWindow() {
        resumePendingPlayerInitialization()
    }

    @MainThread
    fun onDetachedFromWindow() {
        stopObservingRecyclerViews()
    }

    private fun resumePendingPlayerInitialization() {
        if (pendingPlayerInitialization == null) {
            stopObservingRecyclerViews()
            return
        }
        if (!hostView.isAttachedToWindow) {
            stopObservingRecyclerViews()
            return
        }

        val scrollingParents = findParentRecyclerViews().filter {
            it.scrollState != RecyclerView.SCROLL_STATE_IDLE
        }
        updateObservedRecyclerViews(scrollingParents)
        if (scrollingParents.isEmpty()) {
            runPendingPlayerInitialization()
        }
    }

    private fun runPendingPlayerInitialization() {
        val pendingInitialization = pendingPlayerInitialization ?: return
        pendingPlayerInitialization = null
        if (isVideoBindingCurrent(pendingInitialization.generation)) {
            pendingInitialization.initializer()
        }
    }

    private fun isVideoBindingCurrent(generation: Int): Boolean {
        return generation == videoBindingGeneration
    }

    private fun cancelPendingPlayerInitialization() {
        stopObservingRecyclerViews()
        pendingPlayerInitialization = null
    }

    private fun cancelPendingPreviewBinding() {
        previewBindingGeneration++
        pendingPreviewBinding = null
    }

    private fun updateObservedRecyclerViews(recyclerViews: Collection<RecyclerView>) {
        val iterator = observedRecyclerViews.iterator()
        while (iterator.hasNext()) {
            val recyclerView = iterator.next()
            if (recyclerView !in recyclerViews) {
                recyclerView.removeOnScrollListener(scrollListener)
                iterator.remove()
            }
        }

        recyclerViews.forEach { recyclerView ->
            if (observedRecyclerViews.add(recyclerView)) {
                recyclerView.addOnScrollListener(scrollListener)
            }
        }
    }

    private fun stopObservingRecyclerViews() {
        observedRecyclerViews.forEach { it.removeOnScrollListener(scrollListener) }
        observedRecyclerViews.clear()
    }

    private fun findParentRecyclerViews(): List<RecyclerView> {
        val recyclerViews = mutableListOf<RecyclerView>()
        var ancestor = hostView.parent
        while (ancestor != null) {
            if (ancestor is RecyclerView) {
                recyclerViews += ancestor
            }
            ancestor = ancestor.parent
        }
        return recyclerViews
    }

    private class PendingPlayerInitialization(
        val generation: Int,
        val initializer: () -> Unit,
    )

    private data class PreviewBinding(
        val preview: String?,
        val scale: DivVideoScale,
    )
}
