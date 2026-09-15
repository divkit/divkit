package com.yandex.div.core.view2.divs.widgets

import android.view.View
import android.view.ViewParent
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

// Two consecutive checks leave a full frame between observing idle and resuming binding.
private const val REQUIRED_STABLE_FRAMES = 2

@MainThread
internal suspend fun View.awaitParentRecyclersStableIdle() {
    suspendCancellableCoroutine { continuation ->
        ParentRecyclerViewAwaiter(this, continuation).start()
    }
}

private class ParentRecyclerViewAwaiter(
    private val view: View,
    private val continuation: CancellableContinuation<Unit>,
) {
    private var stableFrames = 0
    private var previousParent: ViewParent? = null
    private var previousRecyclers = emptyList<RecyclerView>()

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dx != 0 || dy != 0) {
                stableFrames = 0
            }
        }
    }

    private val frameCallback = Runnable { onFrame() }

    private val attachListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) = startAfterAttach()

        override fun onViewDetachedFromWindow(v: View) {
            view.removeCallbacks(frameCallback)
            stableFrames = 0
            previousParent = null
            updateRecyclers(emptyList())
        }
    }

    fun start() {
        view.addOnAttachStateChangeListener(attachListener)
        continuation.invokeOnCancellation { cleanup() }
        if (continuation.isActive && view.isAttachedToWindow) {
            startAfterAttach()
        }
    }

    private fun onFrame() {
        if (!continuation.isActive) {
            cleanup()
            return
        }
        if (!view.isAttachedToWindow) {
            stableFrames = 0
            return
        }

        val currentParent = view.parent
        val currentRecyclers = view.parentRecyclerViews()
        if (currentParent !== previousParent || !currentRecyclers.sameInstancesAs(previousRecyclers)) {
            stableFrames = 0
            updateRecyclers(currentRecyclers)
        }
        previousParent = currentParent

        if (currentRecyclers.isEmpty()) {
            complete()
            return
        }

        stableFrames = if (currentRecyclers.all { it.isStableIdle }) stableFrames + 1 else 0
        if (stableFrames == REQUIRED_STABLE_FRAMES) {
            complete()
        } else {
            postNextFrame()
        }
    }

    private fun startAfterAttach() {
        stableFrames = 0
        previousParent = view.parent
        updateRecyclers(view.parentRecyclerViews())
        if (previousRecyclers.isEmpty()) {
            complete()
        } else {
            postNextFrame()
        }
    }

    private fun postNextFrame() {
        view.postOnAnimation(frameCallback)
    }

    private fun updateRecyclers(recyclers: List<RecyclerView>) {
        previousRecyclers.forEach { it.removeOnScrollListener(scrollListener) }
        previousRecyclers = recyclers
        previousRecyclers.forEach { it.addOnScrollListener(scrollListener) }
    }

    private fun complete() {
        cleanup()
        if (continuation.isActive) {
            continuation.resume(Unit)
        }
    }

    private fun cleanup() {
        view.removeCallbacks(frameCallback)
        view.removeOnAttachStateChangeListener(attachListener)
        updateRecyclers(emptyList())
    }
}

private val RecyclerView.isStableIdle: Boolean
    get() = scrollState == RecyclerView.SCROLL_STATE_IDLE &&
        !isComputingLayout && !isLayoutRequested && !hasPendingAdapterUpdates()

private fun View.parentRecyclerViews(): List<RecyclerView> {
    val result = mutableListOf<RecyclerView>()
    var current = parent
    while (current != null) {
        if (current is RecyclerView) {
            result += current
        }
        current = current.parent
    }
    return result
}

private fun List<RecyclerView>.sameInstancesAs(other: List<RecyclerView>): Boolean {
    return size == other.size && indices.all { this[it] === other[it] }
}
