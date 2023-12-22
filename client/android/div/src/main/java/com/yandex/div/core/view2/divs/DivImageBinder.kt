package com.yandex.div.core.view2.divs

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
import com.yandex.div.core.view2.divs.widgets.applyFilters
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.widget.AspectImageView
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivBlendMode
import com.yandex.div2.DivFilter
import com.yandex.div2.DivImage
import javax.inject.Inject

@DivScope
internal class DivImageBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val imageLoader: DivImageLoader,
    private val placeholderLoader: DivPlaceholderLoader,
    private val errorCollectors: ErrorCollectors,
) : DivViewBinder<DivImage, DivImageView> {

    override fun bindView(view: DivImageView, div: DivImage, divView: Div2View) {
        val oldDiv = view.div
        if (div === oldDiv) return

        val errorCollector = errorCollectors.getOrCreate(divView.dataTag, divView.divData)

        val expressionResolver = divView.expressionResolver

        baseBinder.bindView(view, div, oldDiv, divView)

        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)

        view.observeAspectRatio(expressionResolver, div.aspect)
        view.addSubscription(
            div.scale.observeAndGet(expressionResolver) { scale -> view.imageScale = scale.toImageScale() }
        )
        view.observeContentAlignment(expressionResolver, div.contentAlignmentHorizontal, div.contentAlignmentVertical)
        view.addSubscription(
            div.imageUrl.observeAndGet(expressionResolver) { view.applyImage(divView, expressionResolver, errorCollector, div) }
        )
        view.observePreview(divView, expressionResolver, errorCollector, div)
        view.observeTint(expressionResolver, div.tintColor, div.tintMode)
        view.observeFilters(div.filters, divView, view, expressionResolver)
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

    private fun DivImageView.observeFilters(
        filters: List<DivFilter>?,
        divView: Div2View,
        subscriber: ExpressionSubscriber,
        resolver: ExpressionResolver,
    ) {
        if (filters == null) return
        val callback =  { _: Any -> applyFiltersAndSetBitmap(filters, divView, resolver) }

        for (filter in filters) {
            when (filter) {
                is DivFilter.Blur -> {
                    subscriber.addSubscription(filter.value.radius.observe(resolver, callback))
                }
            }
        }
    }

    private fun DivImageView.applyFiltersAndSetBitmap(
        filters: List<DivFilter>?,
        divView: Div2View,
        resolver: ExpressionResolver,
    ) {
        val bitmap = currentBitmapWithoutFilters
        if (bitmap != null) {
            bitmap.applyFilters(this, filters, divView.div2Component, resolver) {
                setImageBitmap(it)
            }
        } else {
            setImageBitmap(null)
        }
    }

    private fun DivImageView.observePreview(divView: Div2View,
                                            resolver: ExpressionResolver,
                                            errorCollector: ErrorCollector,
                                            div: DivImage) {
        div.preview?.let {
            val callback = { newPreview: String ->
                if (!isImageLoaded && newPreview != preview) {
                    resetImageLoaded()
                    applyPreview(divView, resolver, div, errorCollector, synchronous = isHighPriorityShow(resolver, this, div))
                }
            }

            addSubscription(it.observeAndGet(resolver, callback))
        }
    }

    private fun DivImageView.applyPreview(divView: Div2View,
                                          resolver: ExpressionResolver,
                                          div: DivImage,
                                          errorCollector: ErrorCollector,
                                          synchronous: Boolean) {
        val newPreview = div.preview?.evaluate(resolver).also { preview = it }

        placeholderLoader.applyPlaceholder(
            this,
            errorCollector,
            newPreview,
            div.placeholderColor.evaluate(resolver),
            synchronous = synchronous,
            onSetPlaceholder = { drawable ->
                if (!isImageLoaded && !isImagePreview) {
                    setPlaceholder(drawable)
                }
            },
            onSetPreview = {
                if (!isImageLoaded) {
                    currentBitmapWithoutFilters = it
                    applyFiltersAndSetBitmap(div.filters, divView, resolver)
                    previewLoaded()
                    applyTint(resolver, div.tintColor, div.tintMode)
                }
            }
        )
    }

    private fun DivImageView.applyImage(divView: Div2View,
                                        resolver: ExpressionResolver,
                                        errorCollector: ErrorCollector,
                                        div: DivImage) {
        val newImageUrl = div.imageUrl.evaluate(resolver)

        if (newImageUrl == imageUrl) {
            applyTint(resolver, div.tintColor, div.tintMode)
            return
        }

        // Called before resetImageLoaded() to ignore high priority preview if image was previously loaded.
        val isHighPriorityShowPreview = isHighPriorityShow(resolver, this, div)

        resetImageLoaded()
        loadReference?.cancel()

        applyPreview(divView, resolver, div, errorCollector, synchronous = isHighPriorityShowPreview)

        imageUrl = newImageUrl

        val reference = imageLoader.loadImage(
            newImageUrl.toString(),
            object : DivIdLoggingImageDownloadCallback(divView) {
                override fun onSuccess(cachedBitmap: CachedBitmap) {
                    super.onSuccess(cachedBitmap)
                    currentBitmapWithoutFilters = cachedBitmap.bitmap
                    applyFiltersAndSetBitmap(div.filters, divView, resolver)
                    applyLoadingFade(div, resolver, cachedBitmap.from)
                    imageLoaded()
                    applyTint(resolver, div.tintColor, div.tintMode)
                    invalidate()
                }

                override fun onError() {
                    super.onError()
                    imageUrl = null
                }
            }
        )

        divView.addLoadReference(reference, this)
        loadReference = reference
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

        val duration = animation.duration.evaluate(resolver)
        val interpolator = animation.interpolator.evaluate(resolver).androidInterpolator
        alpha = animation.alpha.evaluate(resolver).toFloat()
        val delay = animation.startDelay.evaluate(resolver)
        this.animate()
            .alpha(maxAlpha)
            .setDuration(duration)
            .setInterpolator(interpolator)
            .setStartDelay(delay)
    }

    private fun isHighPriorityShow(resolver: ExpressionResolver, view: DivImageView, div: DivImage) : Boolean {
        return !view.isImageLoaded && div.highPriorityPreviewShow.evaluate(resolver)
    }

    private fun DivImageView.observeTint(
        resolver: ExpressionResolver,
        tintColor: Expression<Int>?,
        tintMode: Expression<DivBlendMode>
    ) {
        if (tintColor == null) {
            clearTint()
            return
        }

        val callback = { _: Any -> if (isImageLoaded || isImagePreview) applyTint(resolver, tintColor, tintMode) else clearTint() }
        addSubscription(tintColor.observeAndGet(resolver, callback))
        addSubscription(tintMode.observeAndGet(resolver, callback))
    }

    private fun ImageView.applyTint(
        resolver: ExpressionResolver,
        tintColor: Expression<Int>?,
        tintMode: Expression<DivBlendMode>
    ) {
        val color = tintColor?.evaluate(resolver)

        if (color != null) {
            setColorFilter(color, tintMode.evaluate(resolver).toPorterDuffMode())
        } else {
            clearTint()
        }
    }

    private fun ImageView.clearTint() {
        colorFilter = null
    }
}
