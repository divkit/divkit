// Copyright 2024 Yandex LLC. All rights reserved.

package com.yandex.div.shine

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import com.yandex.div.core.Disposable
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.widget.DivViewDelegate
import com.yandex.div.core.widget.LoadableImageView
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivBase
import com.yandex.div2.DivImage
import org.json.JSONObject

private const val EXTENSION_ID = "shine"

class DivShineExtensionHandler(
    private val logger: DivShineLogger = DivShineLogger.STUB,
) : DivExtensionHandler {

    override fun matches(div: DivBase): Boolean {
        if (div !is DivImage) {
            return false
        }

        val res = div.extensions?.any { extension ->
            extension.id == EXTENSION_ID
        } ?: false

        return res
    }

    override fun bindView(
        divView: Div2View,
        expressionResolver: ExpressionResolver,
        view: View,
        div: DivBase
    ) {
        val imageView = view as? LoadableImageView ?: run {
            logger.logError(
                IllegalArgumentException(
                    "$EXTENSION_ID extension's view is not instance of LoadableImageView"
                )
            )
            return
        }

        val params: JSONObject? = div.extensions?.find { extension ->
            return@find extension.id == EXTENSION_ID
        }?.params

        val tintColorExpression = (div as? DivImage)?.tintColor

        val data = ShineData.fromJson(
            logger,
            params,
            tintColorExpression = tintColorExpression,
        )

        val actionPerformer = createActionPerformer(
            data.onCycleStartActions,
            divView,
            expressionResolver
        )

        val transformer = ShineImageTransformer(
            data,
            actionPerformer,
            expressionResolver,
            logger,
        )

        val delegate = object : DivViewDelegate {
            var isPaused = false

            private fun invalidateAnimationPause() {
                if (isPaused && view.isShown && view.isAttachedToWindow) {
                    transformer.resumeAnimation()
                    isPaused = false
                } else if (!isPaused && (!view.isShown || !view.isAttachedToWindow)) {
                    transformer.pauseAnimation()
                    isPaused = true
                }
            }

            override fun invalidateDrawable(dr: Drawable): Drawable = dr

            override fun onVisibilityChanged(changedView: View, visibility: Int): Boolean {
                invalidateAnimationPause()
                return true
            }

            override fun onAttachedToWindow() = invalidateAnimationPause()
            override fun onDetachedFromWindow() = invalidateAnimationPause()
            override fun buildDrawingCache(autoScale: Boolean) = Unit
            override fun unscheduleDrawable(who: Drawable?) = Unit
        }

        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        imageView.imageTransformer = transformer
        imageView.delegate = delegate
        view.setTag(R.id.div_shine_image_transformer, transformer)
    }

    override fun unbindView(
        divView: Div2View,
        expressionResolver: ExpressionResolver,
        view: View,
        div: DivBase
    ) {
        val imageView = view as? LoadableImageView
        imageView?.imageTransformer = null
        imageView?.delegate = null
        val transformer = (view.getTag(R.id.div_shine_image_transformer) as? ShineImageTransformer)
        transformer?.clear()
    }

    private fun createActionPerformer(
        divActions: List<DivAction>?,
        divView: Div2View,
        expressionResolver: ExpressionResolver,
    ): () -> Unit {
        return {
            divActions?.let { actions ->
                actions
                    .filter { it.isEnabled.evaluate(expressionResolver) }
                    .forEach { action -> divView.handleAction(action) }
            }
        }
    }
}

private class ShineImageTransformer(
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
