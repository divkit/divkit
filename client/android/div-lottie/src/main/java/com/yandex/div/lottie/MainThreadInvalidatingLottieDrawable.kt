package com.yandex.div.lottie

import android.os.Handler
import android.os.Looper
import com.airbnb.lottie.LottieDrawable
import java.util.concurrent.atomic.AtomicBoolean

/**
 * [LottieDrawable] that never notifies its [android.graphics.drawable.Drawable.Callback]
 * outside the main thread.
 *
 * With async updates enabled Lottie evaluates keyframes on a background executor, and
 * `CompositionLayer.setProgress` notifies keyframe listeners that end up calling
 * `invalidateSelf()` from that thread. Lottie re-posts such invalidations to the main thread
 * only up to API 25; on newer versions it calls the callback directly from the executor.
 * The invalidation then reaches `ViewRootImpl.onDescendantInvalidated()`, which verifies the
 * calling thread only behind a platform flag, so with the flag off `scheduleTraversals()` races
 * with the main thread over the traversal sync barrier. The losing outcome is either
 * `IllegalStateException` from `MessageQueue.removeSyncBarrier()` or a barrier that is never
 * removed and freezes traversals; with the flag on it is a `CalledFromWrongThreadException`.
 * See https://github.com/airbnb/lottie-android/issues/2511.
 *
 * Invalidations are coalesced: a single `setProgress` pass notifies many keyframe listeners,
 * but only the first one before the drawable is drawn again has any effect.
 */
internal class MainThreadInvalidatingLottieDrawable : LottieDrawable() {

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val invalidatePending = AtomicBoolean(false)
    private val invalidateOnMainThread = Runnable {
        invalidatePending.set(false)
        invalidateSelfOnMainThread()
    }

    override fun invalidateSelf() {
        if (Looper.myLooper() === Looper.getMainLooper()) {
            // An invalidation made here supersedes a pending background one: the next draw
            // renders whatever `setProgress` has written by then, no matter which call asked
            // for it. The post is dropped before the flag is cleared, never after: a background
            // thread that posts in between then leaves a redundant invalidation, while clearing
            // the flag first could strand it as pending with an empty queue, silently swallowing
            // every later background invalidation.
            if (invalidatePending.get()) {
                mainThreadHandler.removeCallbacks(invalidateOnMainThread)
                invalidatePending.set(false)
            }
            super.invalidateSelf()
            return
        }
        if (invalidatePending.compareAndSet(false, true)) {
            mainThreadHandler.post(invalidateOnMainThread)
        }
    }

    private fun invalidateSelfOnMainThread() = super.invalidateSelf()
}
