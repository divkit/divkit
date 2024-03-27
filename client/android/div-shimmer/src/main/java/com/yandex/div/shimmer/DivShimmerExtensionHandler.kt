package com.yandex.div.shimmer

import android.os.SystemClock
import android.view.View
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.LoadableImage
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivGifImage
import com.yandex.div2.DivImage
import org.json.JSONObject
import kotlin.math.roundToLong

private const val EXTENSION_ID = "shimmer"
private const val MILLIS_IN_SECOND = 1000

/**
 * An extension handler for shimmer (skeleton, stub) while loading div image or gif.
 *
 * @param [animationStartTime] can be used to synchronize it with other animations.
 * It should be set to [SystemClock.uptimeMillis] of the start of the first animation.
 */
open class DivShimmerExtensionHandler(
        private var animationStartTime: Long = 0L
) : DivExtensionHandler {

    override fun matches(div: DivBase): Boolean {
        if (div !is DivGifImage && div !is DivImage) {
            return false
        }

        return div.extensions?.any { extension ->
            extension.id == EXTENSION_ID
        } ?: false
    }

    override fun bindView(divView: Div2View, view: View, div: DivBase) {
        val params: JSONObject? = div.extensions?.find { extension ->
            return@find extension.id == EXTENSION_ID
        }?.params
        val data = ShimmerData.fromJson(params)

        val imageView = view as? LoadableImage ?: return
        if (imageView.isImageLoaded || imageView.isImagePreview) {
            return
        }
        if (animationStartTime == 0L) {
            animationStartTime = SystemClock.uptimeMillis()
        }
        val resolver = divView.getExpressionResolver(div)
        val drawable = ShimmerDrawable(
                data.createShimmer(resolver),
                animationStartTime
        )
        data.observeTo(drawable, resolver)
        imageView.setImage(drawable)
        view.setTag(R.id.div_shimmer_drawable, drawable)
    }

    override fun unbindView(divView: Div2View, view: View, div: DivBase) {
        (view.getTag(R.id.div_shimmer_drawable) as? ShimmerDrawable)?.stop()
    }

    private fun ShimmerData.observeTo(drawable: ShimmerDrawable, resolver: ExpressionResolver) {
        fun updateDrawable() {
            drawable.config = createShimmer(resolver)
        }

        colors.observe(resolver) { updateDrawable() }
        locations.observe(resolver) { updateDrawable() }
        angle.observe(resolver) { updateDrawable() }
        duration.observe(resolver) { updateDrawable() }
    }

    private fun ShimmerData.createShimmer(resolver: ExpressionResolver): ShimmerDrawable.Config {
        val colors = colors.evaluate(resolver)
        val locations = locations.evaluate(resolver)
        val angle = angle.evaluate(resolver)
        val duration = duration.evaluate(resolver)
        return ShimmerDrawable.Config(
                colors = colors.toIntArray(),
                locations = locations.map { it.toFloat() }.toFloatArray(),
                angle = angle,
                duration = (duration * MILLIS_IN_SECOND).roundToLong()
        )
    }
}
