package com.yandex.div.backdrop

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ancestors
import com.yandex.div.backdrop.util.collectViewsAbove
import com.yandex.div.core.view2.Div2View

/**
 * Resolves the view whose rendered content is captured as the source of the backdrop effect.
 *
 * The effect draws whatever is rendered *behind* the decorated element; this abstraction decides
 * how far up the hierarchy that "behind" reaches. A provider is created per decorated view and
 * resolves [backdropView] lazily on first access, then caches the result — the lookup is not
 * thread-safe and is expected to happen on the main thread.
 *
 * @see ParentBackdropViewProvider
 * @see DivViewBackdropViewProvider
 * @see WindowBackdropViewProvider
 */
sealed class BackdropViewProvider {
    abstract val backdropView: View?

    /**
     * Views that have to be hidden while [backdropView] is captured because they are painted above
     * the decorated element and would otherwise bleed into its own backdrop. Empty unless the
     * backdrop reaches beyond the element's own branch of the hierarchy.
     *
     * Resolved on every capture rather than cached: unlike [backdropView], the set depends on the
     * current state of the hierarchy.
     */
    internal open fun collectOccludingViews(): List<View> = emptyList()
}

/**
 * Captures the nearest ancestor [ViewGroup] of [view], i.e. the element's immediate visual
 * container. The narrowest option: only siblings drawn below the decorated element and the
 * parent's own background end up in the backdrop.
 */
class ParentBackdropViewProvider internal constructor(
    view: View
) : BackdropViewProvider() {

    override val backdropView: ViewGroup? by lazy(LazyThreadSafetyMode.NONE) {
        view.ancestors
            .filterIsInstance<ViewGroup>()
            .firstOrNull()
    }
}

/**
 * Captures the nearest ancestor [Div2View] of [view], i.e. the whole DivKit card the element
 * belongs to. This is the default when the `backdrop_effect` extension omits `backdrop_id`.
 *
 * Returns `null` for views outside a [Div2View] hierarchy.
 */
class DivViewBackdropViewProvider internal constructor(
    view: View
) : BackdropViewProvider() {

    override val backdropView: Div2View? by lazy(LazyThreadSafetyMode.NONE) {
        view.ancestors
            .filterIsInstance<Div2View>()
            .firstOrNull()
    }
}

/**
 * Captures everything painted below [view] in the window it belongs to: the window root is used as
 * the backdrop, and the views drawn above the decorated element are hidden for the duration of the
 * capture. Backs the `"backdrop_scope": "window"` param of the `backdrop_effect` extension.
 *
 * Unlike the other providers this one is not limited by the enclosing [Div2View], so it sees
 * content rendered by sibling cards and by plain views of the host application. "Window" is the one
 * the element is attached to: an activity behind a dialog, a tooltip shown in a
 * [android.widget.PopupWindow] and the IME are neither captured nor hidden.
 *
 * **Side effects.** Hiding runs on every capture — up to once per frame — and
 * [View.setVisibility] dispatches [View.onVisibilityChanged] down each hidden subtree. Host views
 * are free to react to it: a `div-select` dropdown dismisses itself, Lottie and shine animations
 * pause and resume, [android.graphics.drawable.AnimationDrawable] restarts from its first frame.
 * The set is limited to the siblings of the decorated element's own branch, but those views belong
 * to the host, which is why the mode is opt-in per element rather than the default.
 *
 * @see collectViewsAbove for the content this cannot hide.
 */
class WindowBackdropViewProvider internal constructor(
    private val view: View
) : BackdropViewProvider() {

    override val backdropView: View? by lazy(LazyThreadSafetyMode.NONE) { view.rootView }

    override fun collectOccludingViews(): List<View> = view.collectViewsAbove()
}

/**
 * Captures the view marked with [tag], letting the layout choose an explicit backdrop. Backs the
 * `backdrop_id` param of the `backdrop_effect` extension.
 *
 * The lookup walks up from [view] to the first tagged ancestor. With [searchWholeWindow] disabled
 * it stops at the enclosing [Div2View] — a tag set above the card is out of scope — and falls back
 * to the first tagged view anywhere inside that [Div2View], which allows the backdrop to be a
 * sibling subtree rather than a container. Views outside a [Div2View] get no fallback, so an
 * unmatched tag resolves to `null`.
 *
 * With [searchWholeWindow] enabled the card boundary is ignored: the ancestor walk runs up to the
 * window root and the fallback searches the whole window. The tag then stops being scoped to a
 * single card — when several cards in the window use the same [tag], the fallback resolves to
 * whichever one comes first in the hierarchy.
 *
 * @param view the decorated view the effect is applied to.
 * @param tag the [View.getTag] value identifying the backdrop, compared with [Any.equals].
 * @param searchWholeWindow whether the lookup may leave the card the element belongs to.
 */
internal class TaggedBackdropViewProvider internal constructor(
    view: View,
    private val tag: String,
    private val searchWholeWindow: Boolean = false,
) : BackdropViewProvider() {

    override val backdropView: View? by lazy(LazyThreadSafetyMode.NONE) {
        findTaggedAncestor(view) ?: findSearchRoot(view)?.findViewWithTag<View>(tag)
    }

    private fun findTaggedAncestor(view: View): ViewGroup? {
        val ancestors = view.ancestors.filterIsInstance<ViewGroup>()
        val scopedAncestors = if (searchWholeWindow) ancestors else ancestors.takeWhile { it !is Div2View }

        return scopedAncestors.find { it.tag == tag }
    }

    private fun findSearchRoot(view: View): View? {
        return if (searchWholeWindow) view.rootView else findDivView(view)
    }

    private fun findDivView(view: View): Div2View? {
        return view.ancestors
            .filterIsInstance<Div2View>()
            .firstOrNull()
    }
}
