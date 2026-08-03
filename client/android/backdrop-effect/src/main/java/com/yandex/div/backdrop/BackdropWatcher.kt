package com.yandex.div.backdrop

import android.view.View

/**
 * Host-provided hook that lets the backdrop effect know its cached capture of the backdrop content
 * has gone stale.
 *
 * The effect re-captures the backdrop view whenever it detects a layout pass or a scroll on either
 * the decorated view or the backdrop view. It does *not* track the backdrop actually being redrawn,
 * so content that changes in place goes unnoticed — a view invalidated by its own state, a
 * `SurfaceView`/`TextureView` pushing new frames, an animation driven outside the view tree, or
 * content rendered by a third-party engine. Implement this interface to report such changes.
 *
 * Pass an implementation to [BackdropEffectExtensionHandler]; it is shared by every element that
 * uses the `backdrop_effect` extension, so the [backdropView] argument identifies which backdrop is
 * being asked about. When no watcher is supplied, the effect relies solely on its own detection.
 */
interface BackdropWatcher {

    /**
     * Returns `true` if the content of [backdropView] changed since the previous capture and the
     * backdrop must be re-captured on this frame.
     *
     * Called on the main thread from the backdrop view's pre-draw listener, at most once per draw
     * pass and once per element the effect is applied to — so potentially on every frame. Keep the
     * implementation cheap and free of side effects; in particular do not request layout,
     * invalidate views or allocate here.
     *
     * Returning `true` only forces an extra capture; returning `false` never suppresses one, since
     * the effect's own layout and scroll detection still applies.
     *
     * @param backdropView the view whose rendered content is used as the backdrop source, as
     *   resolved by [BackdropViewProvider].
     */
    fun isBackdropInvalidated(backdropView: View): Boolean
}

/**
 * [BackdropWatcher] that never reports an invalidation, leaving the effect to rely solely on its
 * own layout, scroll and draw detection. Used when no watcher is supplied to
 * [BackdropEffectExtensionHandler].
 */
internal class DefaultBackdropWatcher : BackdropWatcher {
    override fun isBackdropInvalidated(backdropView: View) = false
}
