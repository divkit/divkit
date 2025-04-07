package com.yandex.div.core.view2.divs

import android.graphics.drawable.PictureDrawable
import android.widget.ImageView
import com.yandex.div.core.DivIdLoggingImageDownloadCallback
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.util.ImageRepresentation
import com.yandex.div.core.util.androidInterpolator
import com.yandex.div.core.util.equalsToConstant
import com.yandex.div.core.util.isConstant
import com.yandex.div.core.util.toCachedBitmap
import com.yandex.div.core.view2.BindingContext
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
import com.yandex.div2.Div
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivBlendMode
import com.yandex.div2.DivFilter
import com.yandex.div2.DivImage
import com.yandex.div2.DivImageScale
import javax.inject.Inject

@DivScope
internal class DivImageBinder @Inject constructor(
    baseBinder: DivBaseBinder,
    private val imageLoader: DivImageLoader,
    private val placeholderLoader: DivPlaceholderLoader,
    private val errorCollectors: ErrorCollectors,
) : DivViewBinder<Div.Image, DivImage, DivImageView>(baseBinder) {

    override fun DivImageView.bind(bindingContext: BindingContext, div: DivImage, oldDiv: DivImage?) {
        applyDivActions(
            bindingContext,
            div.action,
            div.actions,
            div.longtapActions,
            div.doubletapActions,
            div.hoverStartActions,
            div.hoverEndActions,
            div.pressStartActions,
            div.pressEndActions,
            div.actionAnimation,
            div.accessibility,
            div.captureFocusOnAction,
        )

        val divView = bindingContext.divView
        val expressionResolver = bindingContext.expressionResolver
        val errorCollector = errorCollectors.getOrCreate(divView.dataTag, divView.divData)

        bindAspectRatio(div.aspect, oldDiv?.aspect, expressionResolver)
        bindImageScale(div, oldDiv, expressionResolver)
        bindContentAlignment(div, oldDiv, expressionResolver)
        bindPreviewAndImage(bindingContext, div, oldDiv, errorCollector)
        bindTint(div, oldDiv, expressionResolver)
        bindFilters(bindingContext, div, oldDiv)
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
        bindingContext: BindingContext,
        newDiv: DivImage,
        oldDiv: DivImage?,
    ) {
        if (newDiv.filters?.size == oldDiv?.filters?.size) {
            val filtersAreTheSame = newDiv.filters?.foldIndexed(initial = true) { index, result, newFilter ->
                result && newFilter.equalsToConstant(oldDiv?.filters?.get(index))
            } ?: true
            if (filtersAreTheSame) {
                return
            }
        }

        applyFiltersAndSetBitmap(bindingContext, newDiv.filters)

        val allFiltersAreConstant = newDiv.filters?.all { filter -> filter.isConstant() }
        if (allFiltersAreConstant != false) {
            return
        }

        val callback = { _: Any -> applyFiltersAndSetBitmap(bindingContext, newDiv.filters) }
        newDiv.filters?.forEach { filter ->
            when (filter) {
                is DivFilter.Blur ->
                    addSubscription(filter.value.radius.observe(bindingContext.expressionResolver, callback))
                else -> Unit
            }
        }
    }

    private fun DivImageView.applyFiltersAndSetBitmap(
        bindingContext: BindingContext,
        filters: List<DivFilter>?
    ) {
        val bitmap = currentBitmapWithoutFilters
        if (bitmap == null) {
            setImageBitmap(null)
        } else {
            applyBitmapFilters(bindingContext, bitmap, filters) {
                setImageBitmap(it)
            }
        }
    }

    //endregion

    //region Preview

    private fun DivImageView.observePlaceholders(
        bindingContext: BindingContext,
        newDiv: DivImage,
        errorCollector: ErrorCollector
    ) {
        val resolver = bindingContext.expressionResolver

        val callback = callback@{ _: Any ->
            if (isImageLoaded) return@callback

            applyPlaceholders(
                bindingContext,
                newDiv,
                isHighPriorityShow(resolver, this, newDiv),
                errorCollector
            )
        }

        addSubscription(newDiv.preview?.observe(resolver, callback))
        addSubscription(newDiv.placeholderColor.observe(resolver, callback))
    }

    private fun DivImageView.applyPlaceholders(
        bindingContext: BindingContext,
        div: DivImage,
        synchronous: Boolean,
        errorCollector: ErrorCollector
    ) {
        val resolver = bindingContext.expressionResolver
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
                    when (it) {
                        is ImageRepresentation.Bitmap -> {
                            currentBitmapWithoutFilters = it.value
                            applyFiltersAndSetBitmap(bindingContext, div.filters)
                            previewLoaded()
                            applyTint(div.tintColor?.evaluate(resolver), div.tintMode.evaluate(resolver))
                        }
                        is ImageRepresentation.PictureDrawable -> {
                            previewLoaded()
                            setImageDrawable(it.value)
                        }
                    }
                }
            }
        )
    }

    //endregion

    //region Image
    private fun DivImageView.bindPreviewAndImage(
        context: BindingContext,
        newDiv: DivImage,
        oldDiv: DivImage?,
        errorCollector: ErrorCollector
    ) {
        val imageUrlChanged = !newDiv.imageUrl.equalsToConstant(oldDiv?.imageUrl)
        val placeholdersChanged = !(newDiv.preview.equalsToConstant(oldDiv?.preview)
                && newDiv.placeholderColor.equalsToConstant(oldDiv?.placeholderColor))
        val placeholdersAreConstant = newDiv.preview.isConstantOrNull() &&
                newDiv.placeholderColor.isConstant()

        val needPlaceholdersUpdate = !isImageLoaded && placeholdersChanged

        if (needPlaceholdersUpdate && !placeholdersAreConstant) {
            observePlaceholders(context, newDiv, errorCollector)
        }

        val needObserveImageUrl = imageUrlChanged && !newDiv.imageUrl.isConstantOrNull()
        if (needObserveImageUrl) {
            addSubscription(
                newDiv.imageUrl.observe(context.expressionResolver) {
                    applyImage(context, newDiv, errorCollector)
                }
            )
        }

        val applyImageWorkSkipped = !applyImage(context, newDiv, errorCollector)
        if (applyImageWorkSkipped && needPlaceholdersUpdate) {
            applyPlaceholders(
                context,
                newDiv,
                isHighPriorityShow(context.expressionResolver, this, newDiv),
                errorCollector,
            )
        }
    }

    private fun DivImageView.applyImage(
        bindingContext: BindingContext,
        div: DivImage,
        errorCollector: ErrorCollector
    ): Boolean {
        val resolver = bindingContext.expressionResolver
        val imageUrl = div.imageUrl.evaluate(resolver)
        if (imageUrl == this.imageUrl) {
            return false
        }

        // Called before resetImageLoaded() to ignore high priority preview if image was previously loaded.
        val isHighPriorityShowPreview = isHighPriorityShow(resolver, this, div)

        resetImageLoaded()
        clearTint()
        loadReference?.cancel()

        applyPlaceholders(bindingContext, div, isHighPriorityShowPreview, errorCollector)

        this.imageUrl = imageUrl
        val reference = imageLoader.loadImage(
            imageUrl.toString(),
            object : DivIdLoggingImageDownloadCallback(bindingContext.divView) {
                override fun onSuccess(cachedBitmap: CachedBitmap) {
                    super.onSuccess(cachedBitmap)
                    currentBitmapWithoutFilters = cachedBitmap.bitmap
                    applyFiltersAndSetBitmap(bindingContext, div.filters)
                    applyLoadingFade(div, resolver, cachedBitmap.from)
                    imageLoaded()
                    applyTint(div.tintColor?.evaluate(resolver), div.tintMode.evaluate(resolver))
                    invalidate()
                }

                override fun onSuccess(pictureDrawable: PictureDrawable) {
                    if (!div.isVectorCompatible()) {
                        val bitmap = pictureDrawable.toCachedBitmap(imageUrl)
                        onSuccess(bitmap)
                        return
                    }
                    super.onSuccess(pictureDrawable)

                    setImageDrawable(pictureDrawable)
                    applyLoadingFade(div, resolver, null)

                    imageLoaded()
                    invalidate()
                }

                override fun onError() {
                    super.onError()
                    this@applyImage.imageUrl = null
                }
            }
        )

        bindingContext.divView.addLoadReference(reference, this)
        loadReference = reference
        return true
    }

    /**
     * Vector format Image doesn't support color and filters.
     * If color or filters are specified for Image, it should be rasterized.
     */
    private fun DivImage.isVectorCompatible() : Boolean {
        return tintColor == null && filters.isNullOrEmpty()
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
