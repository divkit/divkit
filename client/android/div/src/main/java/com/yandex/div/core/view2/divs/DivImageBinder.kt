package com.yandex.div.core.view2.divs

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import com.yandex.div.core.DivIdLoggingImageDownloadCallback
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.util.ImageRepresentation
import com.yandex.div.core.util.androidInterpolator
import com.yandex.div.core.util.bitmap.applyScaleAndFilters
import com.yandex.div.core.util.equalsToConstant
import com.yandex.div.core.util.evaluateGravity
import com.yandex.div.core.util.isConstant
import com.yandex.div.core.util.toFilters
import com.yandex.div.core.util.toImageScale
import com.yandex.div.core.util.toPorterDuffMode
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivPlaceholderLoader
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.animations.DivAnimationsEnabledController
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.core.view2.runMainThreadAction
import com.yandex.div.core.widget.LoadableImageView
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.view.DivImageView
import com.yandex.div.internal.widget.AspectImageView
import com.yandex.div.json.expressions.Expression
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
    baseBinder: DivBaseBinder,
    private val imageLoader: DivImageLoader,
    private val placeholderLoader: DivPlaceholderLoader,
    private val errorCollectors: ErrorCollectors,
    private val animationsEnabledController: DivAnimationsEnabledController,
) : DivViewBinder<DivBlock.Image, DivImageView>(baseBinder) {

    override fun DivImageView.bind(
        divBlock: DivBlock.Image,
        oldDivBlock: DivBlock.Image?,
        divView: Div2View,
    ) {
        val div = divBlock.divValue
        val oldDiv = oldDivBlock?.divValue
        val expressionResolver = divBlock.expressionResolver
        applyDivActions(
            div.action,
            div.actions,
            div.longtapActions,
            div.doubletapActions,
            div.hoverStartActions,
            div.hoverEndActions,
            div.pressStartActions,
            div.pressEndActions,
            div.actionAnimation,
            div.captureFocusOnAction,
            expressionResolver,
            divView,
        )

        val errorCollector = errorCollectors.getOrCreate(divView.dataTag, divView.divData)

        bindAspectRatio(div.aspect, oldDiv?.aspect, expressionResolver)
        bindImageScale(div, oldDiv, expressionResolver)
        bindContentAlignment(div, oldDiv, expressionResolver)
        bindPreviewAndImage(div, oldDiv, expressionResolver, divView, errorCollector)
        bindTint(div, oldDiv, expressionResolver)
        bindFilters(div, oldDiv, expressionResolver, divView)
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
        newDiv: DivImage,
        oldDiv: DivImage?,
        resolver: ExpressionResolver,
        divView: Div2View,
    ) {
        if (newDiv.filters?.size == oldDiv?.filters?.size) {
            val filtersAreTheSame = newDiv.filters?.foldIndexed(initial = true) { index, result, newFilter ->
                result && newFilter.equalsToConstant(oldDiv?.filters?.get(index))
            } ?: true
            if (filtersAreTheSame) {
                return
            }
        }

        applyFiltersAndSetBitmap(newDiv, oldDiv, resolver, divView)

        val filters = newDiv.filters
        if (filters.isNullOrEmpty()) return

        val allFiltersAreConstant = filters.all { filter -> filter.isConstant() }
        if (allFiltersAreConstant && newDiv.scale.isConstant()) {
            return
        }

        val callback = { _: Any ->
            currentBitmapWithoutFilters?.let {
                applyScaleAndFiltersAndSetBitmap(it, newDiv.scale, filters, resolver, divView)
            }
            Unit
        }
        filters.forEach { filter ->
            when (filter) {
                is DivFilter.Blur -> addSubscription(filter.value.radius.observe(resolver, callback))
                else -> Unit
            }
        }
        newDiv.scale.observe(resolver, callback)
    }

    private fun DivImageView.applyFiltersAndSetBitmap(
        newDiv: DivImage,
        oldDiv: DivImage?,
        resolver: ExpressionResolver,
        divView: Div2View,
    ) {
        if (!(isImageLoaded && newDiv.imageUrl.equalsToConstant(oldDiv?.imageUrl)) &&
            !(isImagePreview && newDiv.preview.equalsToConstant(oldDiv?.preview))) {
            return
        }

        if (newDiv.filters.isNullOrEmpty()) {
            currentBitmapWithoutFilters?.let {
                setImageBitmap(divView, it)
                currentBitmapWithoutFilters = null
            }
            return
        }

        currentBitmapWithoutFilters?.let {
            applyScaleAndFiltersAndSetBitmap(it, newDiv.scale, newDiv.filters, resolver, divView)
            return
        }

        val bitmap = when (val drawable = drawable) {
            null -> return
            is BitmapDrawable -> drawable.bitmap
            else -> drawable.run { toBitmap(intrinsicWidth, intrinsicHeight) }
        }
        applyScaleAndFiltersAndSetBitmap(bitmap, newDiv.scale, newDiv.filters, resolver, divView)
    }

    //endregion

    //region Preview

    private fun DivImageView.observePlaceholders(
        newDiv: DivImage,
        resolver: ExpressionResolver,
        divView: Div2View,
        errorCollector: ErrorCollector
    ) {
        val callback = callback@{ _: Any ->
            if (isImageLoaded) return@callback

            applyPlaceholders(
                newDiv,
                resolver,
                divView,
                isHighPriorityShow(resolver, this, newDiv),
                errorCollector
            )
        }

        addSubscription(newDiv.preview?.observe(resolver, callback))
        addSubscription(newDiv.placeholderColor.observe(resolver, callback))
    }

    private fun DivImageView.applyPlaceholders(
        div: DivImage,
        resolver: ExpressionResolver,
        divView: Div2View,
        synchronous: Boolean,
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
                    setPlaceholder(divView, drawable)
                }
            },
            onSetPreview = {
                if (!isImageLoaded) {
                    when (it) {
                        is ImageRepresentation.Bitmap -> setPreview(it.value, div, resolver, divView)

                        is ImageRepresentation.PictureDrawable -> {
                            if (div.isVectorCompatible()) {
                                previewLoaded()
                                setImageDrawable(divView, it.value)
                            } else {
                                setPreview(it.value.toBitmap(), div, resolver, divView)
                            }
                        }

                        is ImageRepresentation.Error -> Unit
                    }
                }
            }
        )
    }

    private fun DivImageView.setPreview(
        bitmap: Bitmap,
        div: DivImage,
        resolver: ExpressionResolver,
        divView: Div2View,
    ) {
        applyScaleAndFiltersAndSetBitmap(bitmap, div.scale, div.filters, resolver, divView)
        previewLoaded()
        applyTint(
            div.tintColor?.evaluate(resolver),
            div.tintMode.evaluate(resolver)
        )
    }

    //endregion

    //region Image
    private fun DivImageView.bindPreviewAndImage(
        newDiv: DivImage,
        oldDiv: DivImage?,
        resolver: ExpressionResolver,
        divView: Div2View,
        errorCollector: ErrorCollector
    ) {
        val newImageUrl = newDiv.imageUrl
        val imageUrlChanged = !newImageUrl.equalsToConstant(oldDiv?.imageUrl)
        val placeholdersChanged = !(newDiv.preview.equalsToConstant(oldDiv?.preview)
                && newDiv.placeholderColor.equalsToConstant(oldDiv?.placeholderColor))
        val placeholdersAreConstant = newDiv.preview.isConstantOrNull() &&
                newDiv.placeholderColor.isConstant()

        val needPlaceholdersUpdate = !isImageLoaded && placeholdersChanged
        if (needPlaceholdersUpdate && !placeholdersAreConstant) {
            observePlaceholders(newDiv, resolver, divView, errorCollector)
        }

        if (imageUrlChanged && newImageUrl != null && !newImageUrl.isConstant()) {
            addSubscription(
                newImageUrl.observe(resolver) {
                    applyImage(newDiv, resolver, divView, errorCollector)
                }
            )
        }

        val applyImageWorkSkipped = !applyImage(newDiv, resolver, divView, errorCollector)
        if (applyImageWorkSkipped && needPlaceholdersUpdate) {
            applyPlaceholders(
                newDiv,
                resolver,
                divView,
                isHighPriorityShow(resolver, this, newDiv),
                errorCollector,
            )
        }
    }

    private fun DivImageView.applyImage(
        div: DivImage,
        resolver: ExpressionResolver,
        divView: Div2View,
        errorCollector: ErrorCollector
    ): Boolean {
        val imageUrl = div.imageUrl?.evaluate(resolver)
        if (imageUrl == this.imageUrl) {
            return false
        }

        // Called before resetImageLoaded() to ignore high priority preview if image was previously loaded.
        val isHighPriorityShowPreview = isHighPriorityShow(resolver, this, div)

        resetImageLoaded()
        currentBitmapWithoutFilters = null
        clearTint()
        loadReference?.cancel()

        applyPlaceholders(div, resolver, divView, isHighPriorityShowPreview, errorCollector)

        this.imageUrl = imageUrl
        if (imageUrl == null) {
            return true
        }

        val reference = imageLoader.loadImage(
            imageUrl.toString(),
            object : DivIdLoggingImageDownloadCallback(divView) {

                override fun onSuccess(bitmap: Bitmap, source: BitmapSource) {
                    applyScaleAndFiltersAndSetBitmap(bitmap, div.scale, div.filters, resolver, divView)
                    applyLoadingFade(div, resolver, source, animationsEnabledController.isEnabled())
                    imageLoaded()
                    applyTint(div.tintColor?.evaluate(resolver), div.tintMode.evaluate(resolver))
                    invalidate()
                }

                override fun onSuccess(drawable: Drawable, source: BitmapSource) {
                    setImageDrawable(divView, drawable)
                    applyLoadingFade(div, resolver, source, animationsEnabledController.isEnabled())

                    imageLoaded()
                    invalidate()
                }

                override fun onSuccess(pictureDrawable: PictureDrawable, source: BitmapSource) {
                    if (div.isVectorCompatible()) {
                        return super.onSuccess(pictureDrawable, source)
                    }
                    onSuccess(pictureDrawable.toBitmap(), source)
                }

                override fun onError(e: Throwable?) {
                    super.onError(e)
                    this@applyImage.imageUrl = null
                }
            }
        )

        divView.addLoadReference(reference, this)
        loadReference = reference
        return true
    }

    private fun DivImageView.applyScaleAndFiltersAndSetBitmap(
        bitmap: Bitmap,
        divScale: Expression<DivImageScale>,
        divFilters: List<DivFilter>?,
        resolver: ExpressionResolver,
        divView: Div2View,
    ) {
        if (divFilters.isNullOrEmpty()) return setImageBitmap(divView, bitmap)

        val scale = divScale.evaluate(resolver)
        val filters = divFilters.toFilters(resolver)
        currentBitmapWithoutFilters = bitmap
        bitmap.applyScaleAndFilters(divView, this, scale, filters) {
            setImageBitmap(divView, it)
        }
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
        animationsEnabled: Boolean,
    ) {

        this.animate().cancel()
        val animation = div.appearanceAnimation
        val maxAlpha = div.alpha.evaluate(resolver).toFloat()
        if (animation == null || bitmapSource == BitmapSource.MEMORY || !animationsEnabled) {
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

    fun loadImage(
        view: DivImageView,
        divBlock: DivBlock.Image,
        divView: Div2View,
        errorCollector: ErrorCollector,
    ) = view.applyImage(divBlock.divValue, divBlock.expressionResolver, divView, errorCollector)

    private fun DivImageView.setImageDrawable(divView: Div2View, drawable: Drawable?) {
        divView.runMainThreadAction {
            setImageDrawable(drawable)
        }
    }

    private fun DivImageView.setImageBitmap(divView: Div2View, bitmap: Bitmap?) {
        divView.runMainThreadAction {
            setImageBitmap(bitmap)
        }
    }

    private fun DivImageView.setPlaceholder(divView: Div2View, drawable: Drawable?) {
        divView.runMainThreadAction {
            if (!isImageLoaded && !isImagePreview) {
                setPlaceholder(drawable)
            }
        }
    }
}
