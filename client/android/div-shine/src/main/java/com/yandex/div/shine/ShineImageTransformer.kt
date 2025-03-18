package com.yandex.div.shine

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.yandex.div.core.Disposable
import com.yandex.div.core.widget.LoadableImageView
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.json.expressions.ExpressionResolver

internal class ShineImageTransformer(
    data: ShineData,
    onCycleActionPerformer: () -> Unit,
    private val resolver: ExpressionResolver,
    private val logger: DivShineLogger,
) : LoadableImageView.ImageTransformer, ExpressionSubscriber {

    private val shineDrawable: ShineDrawable

    override val subscriptions: MutableList<Disposable> = mutableListOf()

    init {

        val config = data.createShine(onCycleActionPerformer = onCycleActionPerformer)

        shineDrawable = ShineDrawable(
            sourceBitmap = null,
            initialConfig = config
        )

        data.observeTo(shineDrawable)
    }

    override fun transform(drawable: Drawable?): Drawable? {
        return (drawable as? BitmapDrawable)?.let { bitmapDrawable ->
            shineDrawable.apply {
                updateSourceBitmap(bitmapDrawable.bitmap)
                start()
            }
        } ?: drawable
    }

    fun resumeAnimation() {
        shineDrawable.resume()
    }

    fun pauseAnimation() {
        shineDrawable.pause()
    }

    fun clear() {
        shineDrawable.stop()
        release()
    }

    private fun ShineData.observeTo(
        drawable: ShineDrawable
    ) {
        fun updateDrawable() {
            shineDrawable.config = createShine(drawable.config.onCycleActionPerformer)
        }

        listOf(
            isEnabled.observe(resolver) {
                val isEnabled = isEnabled.evaluate(resolver)
                if (isEnabled == drawable.config.enabled) return@observe
                drawable.config = createShine(
                    drawable.config.onCycleActionPerformer,
                    isEnabled
                )
            },
            colors.observe(resolver) { updateDrawable() },
            locations.observe(resolver) { updateDrawable() },
            angle.observe(resolver) { updateDrawable() },
            duration.observe(resolver) { updateDrawable() },
            repeatDelay.observe(resolver) { updateDrawable() },
        ).forEach {
            addSubscription(it)
        }

    }

    private fun ShineData.createShine(
        onCycleActionPerformer: () -> Unit,
        isEnabled: Boolean? = null,
    ): ShineDrawable.Config {
        var enabled = isEnabled ?: this.isEnabled.evaluate(resolver)
        val colors = colors.evaluate(resolver)
        val locations = locations.evaluate(resolver)
        val angle = angle.evaluate(resolver)
        val duration = duration.evaluate(resolver)
        val maxViews = maxViews.evaluate(resolver)
        val startDelay = startDelay.evaluate(resolver)
        val repeatDelay = repeatDelay.evaluate(resolver)

        if (locations.size != colors.size) {
            logger.logError(
                IllegalArgumentException(
                    "$EXTENSION_ID extension: location and color lists must have same size. " +
                            "Actual: ${locations.size} != ${colors.size}"
                )
            )
            enabled = false
        }

        return ShineDrawable.Config(
            enabled = enabled,
            colors = colors.toIntArray(),
            locations = locations.map { it.toFloat() }.toFloatArray(),
            angle = 90 - angle,
            duration = duration,
            startDelay = startDelay,
            repeatDelay = repeatDelay,
            repeatCount = maxViews.toInt() - 1,
            onCycleActionPerformer = onCycleActionPerformer,
        )
    }
}