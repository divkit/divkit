package com.yandex.div.backdrop

import android.os.Build
import android.view.View
import com.yandex.div.backdrop.model.AmbientRimHighlight
import com.yandex.div.backdrop.model.BackdropEffect
import com.yandex.div.backdrop.model.BackdropScope
import com.yandex.div.backdrop.model.SpecularRimHighlight
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.backgroundUnderlay
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivBorder
import com.yandex.div2.DivExtension
import com.yandex.divkit.backdrop.R
import org.json.JSONException

/**
 * [DivExtensionHandler] that applies a backdrop effect (blur, refraction, rim highlight and color
 * adjustment) to a view, using the content rendered behind it as the source.
 *
 * Register it in `DivConfiguration` via `extension(BackdropEffectExtensionHandler())` and enable it
 * on an element with the `backdrop_effect` extension:
 * ```json
 * "extensions": [
 *   {
 *     "id": "backdrop_effect",
 *     "params": {
 *       "backdrop_id": "content",
 *       "blur": {
 *         "radius": 8
 *       },
 *       "refraction": {
 *         "height": 12,
 *         "strength": 24
 *       },
 *       "rim_highlight": {
 *         "type": "specular",
 *         "angle": 45
 *       },
 *       "color_adjustment": {
 *         "saturation": 1.5
 *       }
 *     }
 *   }
 * ]
 * ```
 * All `params` are optional:
 * - `backdrop_id` — tag of the ancestor [ViewGroup][android.view.ViewGroup] to capture as the backdrop; when omitted
 *   the nearest ancestor [Div2View] is used. The tag is looked up on the ancestors of the element up to
 *   the enclosing [Div2View], then anywhere inside that [Div2View]; a tag set above the card is out
 *   of scope unless `backdrop_scope` says otherwise.
 * - `backdrop_scope` — how far the lookup reaches, `"card"` (default) or `"window"`. Combined with
 *   `backdrop_id` it lifts the card boundary, so the tag is searched in the whole window; this
 *   works on every supported version. Without `backdrop_id` it captures everything painted below
 *   the element in the window — including sibling [Div2View]s and plain host views — by using the
 *   window root as the backdrop and hiding the views drawn above the element while the capture
 *   runs; that mode requires Android 13 (API 33) and degrades to `"card"` on older versions. An
 *   unrecognized value is a parsing error and drops the whole effect, as with `rim_highlight.type`.
 * - `blur` — `radius` (`dp`, default `0`).
 * - `refraction` — `height` and `strength` (`dp`, default `0`) and `chromatic_aberration`
 *   (default `false`).
 * - `rim_highlight` — a rim highlight discriminated by `type`:
 *     - `"specular"` — `width` (`dp`, default `0.5`), `alpha` (`0..1`, default `1`),
 *       `color` (default `#80FFFFFF`), `angle` (degrees, default `45`), `falloff` (default `1`).
 *     - `"ambient"` — `width` (`dp`, default `0.5`), `alpha` (`0..1`, default `1`),
 *       `intensity` (`0..1`, default `0.4`), `angle` (degrees, default `45`).
 * - `color_adjustment` — `brightness` (default `0`), `contrast` (default `1`) and `saturation`
 *   (default `1`) applied to the backdrop.
 *
 * Values documented as `dp` (`blur.radius`, `refraction.height`, `refraction.strength`,
 * `rim_highlight.width`) are scaled by the display density. Corner radii are taken from the
 * element's own `border` and kept in sync with its expressions.
 *
 * The effect is drawn beneath the view's own background: the handler installs a
 * [Drawable][android.graphics.drawable.Drawable] as the view's background *underlay* in
 * [beforeBindView] (before `DivBackgroundBinder` builds the background).
 *
 * **Android version restrictions.** Refraction, color adjustment and the directional rim highlight
 * shading rely on [RenderEffect][android.graphics.RenderEffect] /
 * [RuntimeShader][android.graphics.RuntimeShader] and are only available on Android 13 (API 33,
 * [TIRAMISU][android.os.Build.VERSION_CODES.TIRAMISU]) and above. On older versions `blur` is
 * applied via a bitmap fallback and `highlight` degrades to a plain uniform rim (its `angle`,
 * `falloff` and `color`/`intensity` shading is ignored), while `refraction` and `color_adjustment`
 * are not applied at all.
 *
 * **Performance.** This extension is expensive. On every draw pass of the backdrop it re-renders the
 * captured ancestor subtree off-screen, runs GPU blur/refraction/highlight passes and reads the
 * result back into a bitmap. Cost scales with the captured view's size and complexity and is paid
 * again whenever that content invalidates. Apply it sparingly — prefer small, mostly static
 * elements and avoid attaching it to frequently redrawing or scrolling content, or to many elements
 * at once. `"backdrop_scope": "window"` does not change how often the capture is taken — the
 * observers already fire for anything happening in the window — but it makes each one record the
 * whole window's display list and re-record every branch it hides.
 *
 * **Invalidation.** The captured content is reused until a layout pass or a scroll on the decorated
 * view or the backdrop marks it stale. Redraws of the backdrop are not tracked, so content that
 * changes in place — a view invalidated by its own state, a `SurfaceView`, an externally driven
 * animation — keeps showing a stale capture; supply a [BackdropWatcher] to report those changes.
 *
 * @param backdropWatcher reports backdrop changes the handler cannot detect on its own. Shared by
 *   every element handled by this instance and consulted on each frame, so it must be cheap.
 *   Defaults to never invalidating.
 */
class BackdropEffectExtensionHandler(
    private val backdropWatcher: BackdropWatcher = DefaultBackdropWatcher()
) : DivExtensionHandler {

    override fun matches(div: DivBase): Boolean {
        return div.backdropEffectExtension != null
    }

    override fun beforeBindView(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase) {
        unbindView(divView, expressionResolver, view, div)

        val params = div.backdropEffectExtension?.params ?: return
        val backdropEffect = try {
            BackdropEffect.deserialize(params)
        } catch (e: JSONException) {
            divView.logError(e)
            return
        }

        val backdropViewProvider = createBackdropViewProvider(view, backdropEffect)
        val backdropEffectDrawable = BackdropEffectDrawable(
            view,
            backdropViewProvider,
            backdropWatcher
        )
        val density = view.resources.displayMetrics.density

        backdropEffectDrawable.setBlurEffect(
            radius = (backdropEffect.blur?.radius?.toFloat() ?: 0.0f) * density
        )

        backdropEffectDrawable.setRefractionEffect(
            height = (backdropEffect.refraction?.height?.toFloat() ?: 0.0f) * density,
            strength = (backdropEffect.refraction?.strength?.toFloat() ?: 0.0f) * density,
            chromaticAberration = backdropEffect.refraction?.chromaticAberration ?: false
        )

        val rimHighlight = backdropEffect.rimHighlight
        if (rimHighlight is SpecularRimHighlight) {
            backdropEffectDrawable.setRimHighlightEffect(
                width = rimHighlight.width.toFloat() * density,
                alpha = rimHighlight.alpha.toFloat(),
                color = rimHighlight.color,
                angle = rimHighlight.angle.toFloat(),
                falloff = rimHighlight.falloff.toFloat(),
            )
        } else if (rimHighlight is AmbientRimHighlight) {
            backdropEffectDrawable.setRimHighlightEffect(
                width = rimHighlight.width.toFloat() * density,
                alpha = rimHighlight.alpha.toFloat(),
                intensity = rimHighlight.intensity.toFloat(),
                angle = rimHighlight.angle.toFloat(),
            )
        } else {
            backdropEffectDrawable.setRimHighlightEffect(width = 0.0f, alpha = 1.0f, intensity = 0.0f, angle = 0.0f)
        }

        backdropEffectDrawable.setColorAdjustment(
            brightness = backdropEffect.colorAdjustment?.brightness?.toFloat() ?: 0.0f,
            contrast = backdropEffect.colorAdjustment?.contrast?.toFloat() ?: 1.0f,
            saturation = backdropEffect.colorAdjustment?.saturation?.toFloat() ?: 1.0f,
        )

        view.backdropEffectDrawable = backdropEffectDrawable
    }

    override fun bindView(
        divView: Div2View,
        expressionResolver: ExpressionResolver,
        view: View,
        div: DivBase
    ) {
        val backdropEffectDrawable = view.backdropEffectDrawable ?: return

        setCornerRadii(backdropEffectDrawable, div.border, expressionResolver)
        val subscriber = view as? ExpressionSubscriber
        observeCornerRadii(subscriber, div.border, expressionResolver) {
            setCornerRadii(backdropEffectDrawable, div.border, expressionResolver)
        }

        if (view.isAttachedToWindow) {
            backdropEffectDrawable.attachToViews()
        }
        view.backdropFilterOnAttachStateChangeListener = object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                backdropEffectDrawable.attachToViews()
            }

            override fun onViewDetachedFromWindow(v: View) {
                backdropEffectDrawable.detachFromViews()
            }
        }
    }

    private fun createBackdropViewProvider(view: View, backdropEffect: BackdropEffect): BackdropViewProvider {
        val backdropId = backdropEffect.backdropId
        val isWindowScope = backdropEffect.scope == BackdropScope.WINDOW

        return when {
            backdropId != null -> TaggedBackdropViewProvider(view, backdropId, searchWholeWindow = isWindowScope)
            isWindowScope && isWholeWindowCaptureSupported -> WindowBackdropViewProvider(view)
            else -> DivViewBackdropViewProvider(view)
        }
    }

    private fun setCornerRadii(
        drawable: BackdropEffectDrawable,
        border: DivBorder?,
        resolver: ExpressionResolver
    ) {
        if (border == null) {
            drawable.setCornerRadius(0.0f)
            return
        }

        val density = drawable.density

        val cornerRadii = border.cornersRadius
        if (cornerRadii == null) {
            val cornerRadius = border.cornerRadius
            drawable.setCornerRadius(cornerRadius?.evaluate(resolver)?.toFloat()?.times(density) ?: 0.0f)
        } else {
            drawable.setCornerRadii(
                topLeft = cornerRadii.topLeft?.evaluate(resolver)?.toFloat()?.times(density) ?: 0.0f,
                topRight = cornerRadii.topRight?.evaluate(resolver)?.toFloat()?.times(density) ?: 0.0f,
                bottomRight = cornerRadii.bottomRight?.evaluate(resolver)?.toFloat()?.times(density) ?: 0.0f,
                bottomLeft = cornerRadii.bottomLeft?.evaluate(resolver)?.toFloat()?.times(density) ?: 0.0f,
            )
        }
    }

    private fun observeCornerRadii(
        subscriber: ExpressionSubscriber?,
        border: DivBorder?,
        resolver: ExpressionResolver,
        callback: (Any) -> Unit
    ) {
        if (subscriber == null || border == null) {
            return
        }

        subscriber.addSubscription(border.cornerRadius?.observe(resolver, callback))
        subscriber.addSubscription(border.cornersRadius?.topLeft?.observe(resolver, callback))
        subscriber.addSubscription(border.cornersRadius?.topRight?.observe(resolver, callback))
        subscriber.addSubscription(border.cornersRadius?.bottomRight?.observe(resolver, callback))
        subscriber.addSubscription(border.cornersRadius?.bottomLeft?.observe(resolver, callback))
    }

    override fun unbindView(
        divView: Div2View,
        expressionResolver: ExpressionResolver,
        view: View,
        div: DivBase
    ) {
        view.backdropFilterOnAttachStateChangeListener = null
        view.backdropEffectDrawable?.close()
        view.backdropEffectDrawable = null
    }
}

private const val EXTENSION_ID = "backdrop_effect"

/**
 * Capturing the whole window redraws it off-screen on every invalidation. Below
 * [TIRAMISU][Build.VERSION_CODES.TIRAMISU] that redraw is a software pass into a bitmap, which is
 * too expensive to run against a whole window, so the scope degrades to the enclosing card.
 */
private val isWholeWindowCaptureSupported: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

private val DivBase.backdropEffectExtension: DivExtension?
    get() = extensions?.find { it.id == EXTENSION_ID }

private var View.backdropEffectDrawable: BackdropEffectDrawable?
    get() = backgroundUnderlay as? BackdropEffectDrawable
    set(value) { backgroundUnderlay = value }

private var View.backdropFilterOnAttachStateChangeListener: View.OnAttachStateChangeListener?
    get() = getTag(R.id.backdrop_filter_on_attach_state_change_listener) as? View.OnAttachStateChangeListener
    set(value) {
        val oldValue = getTag(R.id.backdrop_filter_on_attach_state_change_listener) as? View.OnAttachStateChangeListener
        if (oldValue != null) {
            removeOnAttachStateChangeListener(oldValue)
        }
        setTag(R.id.backdrop_filter_on_attach_state_change_listener, value)
        if (value != null) {
            addOnAttachStateChangeListener(value)
        }
    }
