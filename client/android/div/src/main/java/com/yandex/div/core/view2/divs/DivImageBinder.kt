package com.yandex.div.core.view2.divs

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import com.yandex.div.core.DivIdLoggingImageDownloadCallback
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.ExperimentFlag
import com.yandex.div.core.experiments.Experiment.IMAGE_LAYOUT_OPTIMIZATION_ENABLED
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.util.ImageRepresentation
import com.yandex.div.core.util.androidInterpolator
import com.yandex.div.core.util.binding.BindingTaskType
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
import com.yandex.div.core.view2.divs.widgets.ViewImagePreviewLoader
import com.yandex.div.core.view2.drawable.ScaleDrawable
import com.yandex.div.core.view2.drawable.TintablePictureDrawable
import com.yandex.div.core.view2.errors.ErrorCollector
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
    private val animationsEnabledController: DivAnimationsEnabledController,
    @param:ExperimentFlag(IMAGE_LAYOUT_OPTIMIZATION_ENABLED)
    private val imageLayoutOptimizationEnabled: Boolean = IMAGE_LAYOUT_OPTIMIZATION_ENABLED.defaultValue,
) : DivViewBinder<DivBlock.Image, DivImageView>(baseBinder) {

    override fun bindView(view: DivImageView, divBlock: DivBlock.Image, divView: Div2View) {
        view.skipRedundantDrawableLayout = imageLayoutOptimizationEnabled
        super.bindView(view, divBlock, divView)
    }

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

        val errorCollector = divView.errorCollector

        bindAspectRatio(div.aspect, oldDiv?.aspect, expressionResolver)
        bindImageScale(div, oldDiv, expressionResolver)
        bindContentAlignment(div, oldDiv, expressionResolver)
        bindPreviewAndImage(divBlock, oldDiv, divView, errorCollector)
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
            is ScaleDrawable -> drawable.child.toBitmapWithoutTint()
            else -> drawable.toBitmapWithoutTint()
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
        val callback = { _: Any ->
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
        if (isImageLoaded) {
            return
        }

        val previewLoader = previewLoader ?: ViewImagePreviewLoader(imageLoader).also {
            this.previewLoader = it
        }
        previewLoader.cancel()
        getLoadingTask()?.cancel(true)
        cleanLoadingTask()
        val preview = div.preview?.evaluate(resolver)
        val previewUri = preview?.let(Uri::parse)
        val isUrlPreview = previewUri?.scheme != null && !previewUri.scheme.equals("data", ignoreCase = true)
        placeholderLoader.applyPlaceholder(
            this,
            errorCollector,
            preview.takeUnless { isUrlPreview },
            div.placeholderColor.evaluate(resolver),
            synchronous = synchronous,
            onSetPlaceholder = { drawable ->
                if (!isImageLoaded && !isImagePreview) {
                    setPlaceholder(divView, drawable)
                }
            },
            onSetPreview = {
                setDecodedPreview(it, div, resolver, divView)
            }
        )
        if (isUrlPreview && preview != null) {
            loadPreview(div, resolver, divView, preview, previewLoader)
        }
    }

    private fun DivImageView.setDecodedPreview(
        preview: ImageRepresentation,
        div: DivImage,
        resolver: ExpressionResolver,
        divView: Div2View,
    ) {
        if (isImageLoaded) {
            return
        }
        when (preview) {
            is ImageRepresentation.Bitmap -> setPreview(preview.value, div, resolver, divView)

            is ImageRepresentation.PictureDrawable -> {
                if (div.isVectorCompatible()) {
                    previewLoaded()
                    setImageDrawable(divView, preview.value.toVectorDrawable())
                    applyTint(div.tintColor?.evaluate(resolver), div.tintMode.evaluate(resolver))
                } else {
                    setPreview(preview.value.toBitmap(), div, resolver, divView)
                }
            }

            is ImageRepresentation.Error -> Unit
        }
    }

    private fun DivImageView.loadPreview(
        div: DivImage,
        resolver: ExpressionResolver,
        divView: Div2View,
        url: String,
        previewLoader: ViewImagePreviewLoader,
    ) {
        val request = previewLoader.load(
            url = url,
            onLoaded = { image, request ->
                divView.runMainThreadAction(BindingTaskType.DATA) {
                    if (!request.isCurrent() || isImageLoaded) {
                        return@runMainThreadAction
                    }
                    when (image) {
                        is DivCachedImage.Bitmap ->
                            setPreview(image.bitmap, div, resolver, divView, request)
                        is DivCachedImage.Drawable ->
                            setPreview(image.drawable, div, resolver, divView, request)
                    }
                }
            },
            onError = { error, _ -> error?.let(divView::logError) }
        )
        divView.addLoadReference(request, this)
    }

    private fun DivImageView.setPreview(
        drawable: Drawable,
        div: DivImage,
        resolver: ExpressionResolver,
        divView: Div2View,
        previewRequest: ViewImagePreviewLoader.Request,
    ) {
        when {
            drawable is BitmapDrawable ->
                setPreview(drawable.bitmap, div, resolver, divView, previewRequest)
            drawable is PictureDrawable && !div.isVectorCompatible() ->
                setPreview(drawable.toBitmap(), div, resolver, divView, previewRequest)
            else -> {
                previewLoaded()
                setImageDrawable((drawable as? PictureDrawable)?.toVectorDrawable() ?: drawable)
                applyTint(div.tintColor?.evaluate(resolver), div.tintMode.evaluate(resolver))
            }
        }
    }

    private fun DivImageView.setPreview(
        bitmap: Bitmap,
        div: DivImage,
        resolver: ExpressionResolver,
        divView: Div2View,
        previewRequest: ViewImagePreviewLoader.Request? = null,
    ) {
        applyScaleAndFiltersAndSetBitmap(
            bitmap, div.scale, div.filters, resolver, divView, previewRequest,
        )
        previewLoaded()
        applyTint(
            div.tintColor?.evaluate(resolver),
            div.tintMode.evaluate(resolver)
        )
    }

    //endregion

    //region Image
    private fun DivImageView.bindPreviewAndImage(
        block: DivBlock.Image,
        oldDiv: DivImage?,
        divView: Div2View,
        errorCollector: ErrorCollector
    ) {
        val newDiv = block.divValue
        val resolver = block.expressionResolver
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
                    applyImage(block, divView, errorCollector)
                }
            )
        }

        val applyImageWorkSkipped = !applyImage(block, divView, errorCollector)
        if (applyImageWorkSkipped && placeholdersChanged) {
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
        block: DivBlock.Image,
        divView: Div2View,
        errorCollector: ErrorCollector
    ): Boolean {
        val div = block.divValue
        val resolver = block.expressionResolver
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

                override fun onSuccess(bitmap: Bitmap, source: BitmapSource) =
                    onBitmapLoaded(bitmap, source, divBlock ?: block, divView)

                override fun onSuccess(drawable: Drawable, source: BitmapSource) =
                    onDrawableLoaded(drawable, source, divBlock ?: block, divView)

                override fun onSuccess(pictureDrawable: PictureDrawable, source: BitmapSource) {
                    val current = divBlock ?: block
                    if (current.divValue.isVectorCompatible()) {
                        onDrawableLoaded(pictureDrawable.toVectorDrawable(), source, current, divView)
                    } else {
                        onBitmapLoaded(pictureDrawable.toBitmap(), source, current, divView)
                    }
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

    private fun DivImageView.onBitmapLoaded(
        bitmap: Bitmap,
        source: BitmapSource,
        block: DivBlock.Image,
        divView: Div2View,
    ) {
        val div = block.divValue
        val resolver = block.expressionResolver
        applyScaleAndFiltersAndSetBitmap(bitmap, div.scale, div.filters, resolver, divView)
        onImageLoaded(block, source)
    }

    private fun DivImageView.onDrawableLoaded(
        drawable: Drawable,
        source: BitmapSource,
        block: DivBlock.Image,
        divView: Div2View,
    ) {
        setImageDrawable(divView, drawable)
        onImageLoaded(block, source)
    }

    private fun DivImageView.onImageLoaded(block: DivBlock.Image, source: BitmapSource) {
        val div = block.divValue
        val resolver = block.expressionResolver
        applyLoadingFade(div, resolver, source, animationsEnabledController.isEnabled())
        imageLoaded()
        applyTint(div.tintColor?.evaluate(resolver), div.tintMode.evaluate(resolver))
        invalidate()
    }

    private fun DivImageView.applyScaleAndFiltersAndSetBitmap(
        bitmap: Bitmap,
        divScale: Expression<DivImageScale>,
        divFilters: List<DivFilter>?,
        resolver: ExpressionResolver,
        divView: Div2View,
        previewRequest: ViewImagePreviewLoader.Request? = null,
    ) {
        if (divFilters.isNullOrEmpty()) {
            setImageBitmap(divView, bitmap, previewRequest)
            return
        }

        val scale = divScale.evaluate(resolver)
        val filters = divFilters.toFilters(resolver)
        currentBitmapWithoutFilters = bitmap
        bitmap.applyScaleAndFilters(divView, this, scale, filters) {
            setImageBitmap(divView, it, previewRequest)
        }
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

    fun loadImage(view: DivImageView, divBlock: DivBlock.Image, divView: Div2View) =
        view.applyImage(divBlock, divView, divView.errorCollector)

    private fun DivImageView.setImageDrawable(divView: Div2View, drawable: Drawable?) {
        divView.runMainThreadAction(BindingTaskType.DATA) {
            setImageDrawable(drawable)
        }
    }

    private fun DivImageView.setImageBitmap(
        divView: Div2View,
        bitmap: Bitmap?,
        previewRequest: ViewImagePreviewLoader.Request? = null,
    ) {
        divView.runMainThreadAction(BindingTaskType.DATA) {
            if (previewRequest == null || (previewRequest.isCurrent() && !isImageLoaded)) {
                setImageBitmap(bitmap)
            }
        }
    }

    private fun DivImageView.setPlaceholder(divView: Div2View, drawable: Drawable?) {
        divView.runMainThreadAction(BindingTaskType.DATA) {
            if (!isImageLoaded && !isImagePreview) {
                setPlaceholder(drawable)
            }
        }
    }
}

private fun Drawable.toBitmapWithoutTint(): Bitmap {
    val tint = colorFilter
    colorFilter = null
    return toBitmap(intrinsicWidth, intrinsicHeight).also { colorFilter = tint }
}

/**
 * Vector format Image doesn't support filters, and before Android P doesn't support tint.
 * If they are specified for Image, it should be rasterized.
 */
private fun DivImage.isVectorCompatible() : Boolean {
    return filters.isNullOrEmpty() && (tintColor == null || isVectorTintSupported)
}

private fun PictureDrawable.toVectorDrawable(): Drawable {
    return if (isVectorTintSupported) TintablePictureDrawable(picture) else this
}

// Before Android P a scaled saveLayer is drawn pixelated, so tinted vectors are rasterized
private val isVectorTintSupported get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
