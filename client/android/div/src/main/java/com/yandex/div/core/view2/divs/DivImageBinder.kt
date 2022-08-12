package com.yandex.div.core.view2.divs

import android.graphics.PorterDuff
import android.widget.ImageView
import com.yandex.div.core.DivIdLoggingImageDownloadCallback
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.util.androidInterpolator
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivPlaceholderLoader
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivImageView
import com.yandex.div.core.widget.AspectImageView
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivAspect
import com.yandex.div2.DivImage
import javax.inject.Inject

@DivScope
internal class DivImageBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val imageLoader: DivImageLoader,
    private val placeholderLoader: DivPlaceholderLoader
) : DivViewBinder<DivImage, DivImageView> {

    override fun bindView(view: DivImageView, div: DivImage, divView: Div2View) {
        val oldDiv = view.div
        if (div == oldDiv) return

        val expressionResolver = divView.expressionResolver
        view.closeAllSubscription()

        view.div = div
        if (oldDiv != null) baseBinder.unbindExtensions(view, oldDiv, divView)
        baseBinder.bindView(view, div, oldDiv, divView)

        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)

        view.observeAspectRatio(expressionResolver, div.aspect)
        view.addSubscription(
            div.scale.observeAndGet(expressionResolver) { scale -> view.imageScale = scale.toImageScale() }
        )
        view.observeContentAlignment(expressionResolver, div.contentAlignmentHorizontal, div.contentAlignmentVertical)
        view.addSubscription(
            div.imageUrl.observeAndGet(expressionResolver) { view.applyImage(divView, expressionResolver, div) }
        )
    }

    private fun DivImageView.observeAspectRatio(resolver: ExpressionResolver, aspect: DivAspect?) {
        if (aspect?.ratio == null) {
            aspectRatio = AspectImageView.ASPECT_RATIO_OF_IMAGE
            return
        }

        addSubscription(
            aspect.ratio.observeAndGet(resolver) { ratio ->
                aspectRatio = ratio.toFloat()
            }
        )
    }

    private fun DivImageView.observeContentAlignment(
        resolver: ExpressionResolver,
        horizontalAlignment: Expression<DivAlignmentHorizontal>,
        verticalAlignment: Expression<DivAlignmentVertical>
    ) {
        applyContentAlignment(resolver, horizontalAlignment, verticalAlignment)

        val callback = { _: Any -> applyContentAlignment(resolver, horizontalAlignment, verticalAlignment) }
        addSubscription(horizontalAlignment.observe(resolver, callback))
        addSubscription(verticalAlignment.observe(resolver, callback))
    }

    private fun AspectImageView.applyContentAlignment(
        resolver: ExpressionResolver,
        horizontalAlignment: Expression<DivAlignmentHorizontal>,
        verticalAlignment: Expression<DivAlignmentVertical>
    ) {
        gravity = evaluateGravity(horizontalAlignment.evaluate(resolver), verticalAlignment.evaluate(resolver))
    }

    private fun DivImageView.applyImage(divView: Div2View, resolver: ExpressionResolver, div: DivImage) {
        val newImageUrl = div.imageUrl.evaluate(resolver)
        if (isImageLoaded && newImageUrl == imageUrl) {
            return
        }
        // Ignore high priority preview if image was previously loaded.
        val isHighPriorityShowPreview = isHighPriorityShow(resolver, this, div)
        newImageUrl.applyIfNotEquals(imageUrl) { resetImageLoaded() }
        // if bitmap was already loaded for the same imageUrl, we don't load placeholders.
        placeholderLoader.applyPlaceholder(
            this,
            div.preview?.evaluate(resolver),
            div.placeholderColor.evaluate(resolver),
            synchronous = isHighPriorityShowPreview
        ) { observeTintColor(resolver, div.tintColor) }

        val reference = imageLoader.loadImage(
            newImageUrl.toString(),
            object : DivIdLoggingImageDownloadCallback(divView) {
                override fun onSuccess(cachedBitmap: CachedBitmap) {
                    super.onSuccess(cachedBitmap)
                    imageUrl = newImageUrl
                    setImage(cachedBitmap.bitmap)
                    applyLoadingFade(div, resolver, cachedBitmap.from)
                    imageLoaded()
                    observeTintColor(resolver, div.tintColor)
                    invalidate()
                }
            }
        )
        divView.addLoadReference(reference, this)
    }

    private fun DivImageView.applyLoadingFade(
        div: DivImage,
        resolver: ExpressionResolver,
        bitmapSource: BitmapSource?,
    ) {

        this.animate().cancel()
        val animation = div.appearanceAnimation
        val maxAlpha = div.alpha.evaluate(resolver).toFloat()
        if (animation == null || bitmapSource == BitmapSource.MEMORY) {
            alpha = maxAlpha
            return
        }

        val duration = animation.duration.evaluate(resolver).toLong()
        val interpolator = animation.interpolator.evaluate(resolver).androidInterpolator
        alpha = animation.alpha.evaluate(resolver).toFloat()
        val delay = animation.startDelay.evaluate(resolver).toLong()
        this.animate()
            .alpha(maxAlpha)
            .setDuration(duration)
            .setInterpolator(interpolator)
            .setStartDelay(delay)
    }

    private fun isHighPriorityShow(resolver: ExpressionResolver, view: DivImageView, div: DivImage) : Boolean {
        if (!div.highPriorityPreviewShow.evaluate(resolver)) {
            return false
        }
        return !view.isImageLoaded
    }

    private fun DivImageView.observeTintColor(resolver: ExpressionResolver, tintColor: Expression<Int>?) {
        if (tintColor == null) {
            applyTintColor(null)
            return
        }

        addSubscription(tintColor.observeAndGet(resolver) { color -> applyTintColor(color) })
    }

    private fun ImageView.applyTintColor(color: Int?) {
        if (color != null) {
            setColorFilter(color, PorterDuff.Mode.SRC_IN)
        } else {
            colorFilter = null
        }
    }
}
