package com.yandex.div.core.view2.divs

import android.widget.ImageView
import com.yandex.div.core.DivIdLoggingImageDownloadCallback
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.util.androidInterpolator
import com.yandex.div.core.util.equalsToConstant
import com.yandex.div.core.util.isConstant
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivPlaceholderLoader
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivImageView
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.core.widget.LoadableImageView
import com.yandex.div.internal.widget.AspectImageView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.equalsToConstant
import com.yandex.div.json.expressions.isConstant
import com.yandex.div.json.expressions.isConstantOrNull
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivBlendMode
import com.yandex.div2.DivFilter
import com.yandex.div2.DivImage
import com.yandex.div2.DivImageScale
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

        baseBinder.bindView(view, div, oldDiv, divView)
        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)

        val expressionResolver = divView.expressionResolver
        val errorCollector = errorCollectors.getOrCreate(divView.dataTag, divView.divData)

        view.bindAspectRatio(div.aspect, oldDiv?.aspect, expressionResolver)
        view.bindImageScale(div, oldDiv, expressionResolver)
        view.bindContentAlignment(div, oldDiv, expressionResolver)
        view.bindPreview(divView, div, oldDiv, expressionResolver, errorCollector)
        view.bindImage(divView, div, oldDiv, expressionResolver, errorCollector)
        view.bindTint(div, oldDiv, expressionResolver)
        view.bindFilters(divView, div, oldDiv, expressionResolver)
    }

    //region Image Scale

    private fun DivImageView.bindImageScale(
        newDiv: DivImage,
        oldDiv: DivImage?,
        resolver: ExpressionResolver,
    ) {
        if (newDiv.scale.equalsToConstant(oldDiv?.scale)) {
            return
        }

        applyImageScale(newDiv.scale.evaluate(resolver))

        if (newDiv.scale.isConstant()) {
            return
        }

        addSubscription(
            newDiv.scale.observe(resolver) { scale -> applyImageScale(scale) }
        )
    }

    private fun DivImageView.applyImageScale(scale: DivImageScale) {
        imageScale = scale.toImageScale()
    }

    //endregion

    //region Content Alignment

    private fun DivImageView.bindContentAlignment(
        newDiv: DivImage,
        oldDiv: DivImage?,
        resolver: ExpressionResolver
    ) {
        if (newDiv.contentAlignmentHorizontal.equalsToConstant(oldDiv?.contentAlignmentHorizontal)
            && newDiv.contentAlignmentVertical.equalsToConstant(oldDiv?.contentAlignmentVertical)) {
            return
        }

        applyContentAlignment(
            newDiv.contentAlignmentHorizontal.evaluate(resolver),
            newDiv.contentAlignmentVertical.evaluate(resolver)
        )

        if (newDiv.contentAlignmentHorizontal.isConstant() && newDiv.contentAlignmentVertical.isConstant()) {
            return
        }

        val callback = { _: Any ->
            applyContentAlignment(
                newDiv.contentAlignmentHorizontal.evaluate(resolver),
                newDiv.contentAlignmentVertical.evaluate(resolver)
            )
        }
        addSubscription(newDiv.contentAlignmentHorizontal.observe(resolver, callback))
        addSubscription(newDiv.contentAlignmentVertical.observe(resolver, callback))
    }

    private fun AspectImageView.applyContentAlignment(
        horizontalAlignment: DivAlignmentHorizontal,
        verticalAlignment: DivAlignmentVertical
    ) {
        gravity = evaluateGravity(horizontalAlignment, verticalAlignment)
    }

    //endregion

    //region Filters

    private fun DivImageView.bindFilters(
        divView: Div2View,
        newDiv: DivImage,
        oldDiv: DivImage?,
        resolver: ExpressionResolver,
    ) {
        if (newDiv.filters?.size == oldDiv?.filters?.size) {
            val filtersAreTheSame = newDiv.filters?.foldIndexed(initial = true) { index, result, newFilter ->
                result && newFilter.equalsToConstant(oldDiv?.filters?.get(index))
            } ?: true
            if (filtersAreTheSame) {
                return
            }
        }

        applyFiltersAndSetBitmap(divView, newDiv.filters)

        val allFiltersAreConstant = newDiv.filters?.all { filter -> filter.isConstant() }
        if (allFiltersAreConstant != false) {
            return
        }

        val callback = { _: Any -> applyFiltersAndSetBitmap(divView, newDiv.filters) }
        newDiv.filters?.forEach { filter ->
            when (filter) {
                is DivFilter.Blur -> { addSubscription(filter.value.radius.observe(resolver, callback)) }
                else -> Unit
            }
        }
    }

    private fun DivImageView.applyFiltersAndSetBitmap(
        divView: Div2View,
        filters: List<DivFilter>?
    ) {
        val bitmap = currentBitmapWithoutFilters
        if (bitmap == null) {
            setImageBitmap(null)
        } else {
            applyBitmapFilters(divView, bitmap, filters) {
                setImageBitmap(it)
            }
        }
    }

    //endregion

    //region Preview

    private fun DivImageView.bindPreview(
        divView: Div2View,
        newDiv: DivImage,
        oldDiv: DivImage?,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector
    ) {
        if (isImageLoaded) {
            return
        }

        if (newDiv.preview.equalsToConstant(oldDiv?.preview)
            && newDiv.placeholderColor.equalsToConstant(oldDiv?.placeholderColor)) {
            return
        }

        // Do not apply preview at this point. It will be done just before the start of image loading.

        if (newDiv.preview.isConstantOrNull() && newDiv.placeholderColor.isConstant()) {
            return
        }

        addSubscription(
            newDiv.preview?.observe(resolver) { newPreview ->
                if (isImageLoaded || newPreview == preview) {
                    return@observe
                }
                resetImageLoaded()
                applyPreview(
                    divView,
                    newDiv,
                    isHighPriorityShow(resolver, this, newDiv),
                    resolver,
                    errorCollector
                )
            }
        )
    }

    private fun DivImageView.applyPreview(
        divView: Div2View,
        div: DivImage,
        synchronous: Boolean,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector
    ) {
        placeholderLoader.applyPlaceholder(
            this,
            errorCollector,
            div.preview?.evaluate(resolver),
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
                    applyFiltersAndSetBitmap(divView, div.filters)
                    previewLoaded()
                    applyTint(div.tintColor?.evaluate(resolver), div.tintMode.evaluate(resolver))
                }
            }
        )
    }

    //endregion

    //region Image

    private fun DivImageView.bindImage(
        divView: Div2View,
        newDiv: DivImage,
        oldDiv: DivImage?,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector
    ) {
        if (newDiv.imageUrl.equalsToConstant(oldDiv?.imageUrl)) {
            return
        }

        applyImage(
            divView = divView,
            div = newDiv,
            resolver = resolver,
            errorCollector = errorCollector
        )

        if (newDiv.imageUrl.isConstantOrNull()) {
            return
        }

        addSubscription(
            newDiv.imageUrl.observe(resolver) {
                applyImage(
                    divView = divView,
                    div = newDiv,
                    resolver = resolver,
                    errorCollector = errorCollector
                )
            }
        )
    }

    private fun DivImageView.applyImage(
        divView: Div2View,
        div: DivImage,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector
    ) {
        val imageUrl = div.imageUrl.evaluate(resolver)
        if (imageUrl == this.imageUrl) {
            return
        }

        // Called before resetImageLoaded() to ignore high priority preview if image was previously loaded.
        val isHighPriorityShowPreview = isHighPriorityShow(resolver, this, div)

        resetImageLoaded()
        clearTint()
        loadReference?.cancel()

        applyPreview(divView, div, isHighPriorityShowPreview, resolver, errorCollector)

        this.imageUrl = imageUrl
        val reference = imageLoader.loadImage(
            imageUrl.toString(),
            object : DivIdLoggingImageDownloadCallback(divView) {
                override fun onSuccess(cachedBitmap: CachedBitmap) {
                    super.onSuccess(cachedBitmap)
                    currentBitmapWithoutFilters = cachedBitmap.bitmap
                    applyFiltersAndSetBitmap(divView, div.filters)
                    applyLoadingFade(div, resolver, cachedBitmap.from)
                    imageLoaded()
                    applyTint(div.tintColor?.evaluate(resolver), div.tintMode.evaluate(resolver))
                    invalidate()
                }

                override fun onError() {
                    super.onError()
                    this@applyImage.imageUrl = null
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

    //endregion

    //region Tint

    private fun DivImageView.bindTint(
        newDiv: DivImage,
        oldDiv: DivImage?,
        resolver: ExpressionResolver,
    ) {
        if (newDiv.tintColor.equalsToConstant(oldDiv?.tintColor)
            && newDiv.tintMode.equalsToConstant(oldDiv?.tintMode)) {
            return
        }

        applyTint(newDiv.tintColor?.evaluate(resolver), newDiv.tintMode.evaluate(resolver))

        if (newDiv.tintColor.isConstantOrNull() && newDiv.tintMode.isConstant()) {
            return
        }

        val callback = { _: Any ->
            applyTint(newDiv.tintColor?.evaluate(resolver), newDiv.tintMode.evaluate(resolver))
        }
        addSubscription(newDiv.tintColor?.observe(resolver, callback))
        addSubscription(newDiv.tintMode.observe(resolver, callback))
    }

    private fun LoadableImageView.applyTint(
        tintColor: Int?,
        tintMode: DivBlendMode
    ) {
        if ((isImageLoaded || isImagePreview) && tintColor != null) {
            setColorFilter(tintColor, tintMode.toPorterDuffMode())
        } else {
            clearTint()
        }
    }

    private fun ImageView.clearTint() {
        colorFilter = null
    }

    //endregion
}
