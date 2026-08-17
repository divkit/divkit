package com.yandex.div.backdrop

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ancestors
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
 */
sealed class BackdropViewProvider {
    abstract val backdropView: View?
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
 * Captures the view marked with [tag], letting the layout choose an explicit backdrop. Backs the
 * `backdrop_id` param of the `backdrop_effect` extension.
 *
 * The lookup walks up from [view] to the first tagged ancestor, stopping at the enclosing
 * [Div2View] — a tag set above the card is out of scope. If no ancestor matches, the search falls
 * back to the first tagged view anywhere inside that [Div2View], which allows the backdrop to be a
 * sibling subtree rather than a container. Views outside a [Div2View] get no fallback, so an
 * unmatched tag resolves to `null`.
 *
 * @param view the decorated view the effect is applied to.
 * @param tag the [View.getTag] value identifying the backdrop, compared with [Any.equals].
 */
internal class TaggedBackdropViewProvider internal constructor(
    view: View,
    private val tag: String
) : BackdropViewProvider() {

    override val backdropView: View? by lazy(LazyThreadSafetyMode.NONE) {
        findTaggedAncestor(view) ?: findDivView(view)?.findViewWithTag<View>(tag)
    }

    private fun findTaggedAncestor(view: View): ViewGroup? {
        return view.ancestors
            .filterIsInstance<ViewGroup>()
            .takeWhile { it !is Div2View }
            .find { it.tag == tag }
    }

    private fun findDivView(view: View): Div2View? {
        return view.ancestors
            .filterIsInstance<Div2View>()
            .firstOrNull()
    }
}
