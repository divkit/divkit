package com.yandex.div.core.view2.divs

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.annotation.RequiresApi
import com.yandex.div.core.DivIdLoggingImageDownloadCallback
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.util.ImageRepresentation
import com.yandex.div.core.util.binding.BindingTaskType
import com.yandex.div.core.util.evaluateGravity
import com.yandex.div.core.util.toImageScale
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivPlaceholderLoader
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.animations.DivAnimationsEnabledController
import com.yandex.div.core.view2.divs.widgets.DivGifImageView
import com.yandex.div.core.view2.divs.widgets.ViewImagePreviewLoader
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.runMainThreadAction
import com.yandex.div.internal.KLog
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.widget.AspectImageView
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivGifImage
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference
import javax.inject.Inject

@DivScope
internal class DivGifImageBinder @Inject constructor(
    baseBinder: DivBaseBinder,
    private val imageLoader: DivImageLoader,
    private val placeholderLoader: DivPlaceholderLoader,
    private val animationsEnabledController: DivAnimationsEnabledController,
) : DivViewBinder<DivBlock.GifImage, DivGifImageView>(baseBinder) {

    override fun DivGifImageView.bind(
        divBlock: DivBlock.GifImage,
        oldDivBlock: DivBlock.GifImage?,
        divView: Div2View,
    ) {
        val div = divBlock.divValue
        val expressionResolver = divBlock.expressionResolver
        val errorCollector = divView.errorCollector

        setAnimationsEnabled(animationsEnabledController.isEnabled())
        addSubscription(
            animationsEnabledController.observe(divView) {
                setAnimationsEnabled(animationsEnabledController.isEnabled())
            }
        )

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

        bindAspectRatio(div.aspect, oldDivBlock?.divValue?.aspect, expressionResolver)

        addSubscription(
            div.scale.observeAndGet(expressionResolver) { scale -> imageScale = scale.toImageScale() }
        )
        observeContentAlignment(
            expressionResolver,
            div.contentAlignmentHorizontal,
            div.contentAlignmentVertical
        )

        bindPreviewAndImage(divView, expressionResolver, div, errorCollector)
    }

    private fun DivGifImageView.bindPreviewAndImage(
        divView: Div2View,
        expressionResolver: ExpressionResolver,
        div: DivGifImage,
        errorCollector: ErrorCollector,
    ) {
        val url = div.gifUrl
        val onPlaceholderChanged = { _: Any ->
            applyPlaceholders(divView, expressionResolver, div, errorCollector)
        }
        addSubscription(div.preview?.observe(expressionResolver, onPlaceholderChanged))
        addSubscription(div.previewUrl?.observe(expressionResolver, onPlaceholderChanged))
        addSubscription(div.placeholderColor.observe(expressionResolver, onPlaceholderChanged))
        if (url == null) {
            applyPlaceholders(divView, expressionResolver, div, errorCollector)
        } else {
            addSubscription(
                url.observeAndGet(expressionResolver) {
                    applyGifImage(divView, expressionResolver, div, errorCollector)
                }
            )
        }
    }

    private fun DivGifImageView.observeContentAlignment(
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

    private fun DivGifImageView.applyGifImage(
        divView: Div2View,
        resolver: ExpressionResolver,
        div: DivGifImage,
        errorCollector: ErrorCollector
    ) {
        val newGifUrl = div.gifUrl?.evaluate(resolver)
        if (newGifUrl == gifUrl) {
            applyPlaceholders(divView, resolver, div, errorCollector)
            return
        }

        resetImageLoaded()
        loadReference?.cancel()

        applyPlaceholders(divView, resolver, div, errorCollector)

        gifUrl = newGifUrl
        if (newGifUrl == null) {
            return
        }

        loadAnimatedImage(divView, newGifUrl)
    }

    private fun DivGifImageView.loadAnimatedImage(divView: Div2View, url: Uri) {
        // we don't reuse this because not all clients has bytes cache
        val reference = imageLoader.loadAnimatedImage(
            url.toString(),
            object : DivIdLoggingImageDownloadCallback(divView) {

                override fun onSuccess(cachedBitmap: CachedBitmap) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        loadDrawable(cachedBitmap)
                    } else {
                        onSuccess(cachedBitmap.bitmap, cachedBitmap.from)
                    }
                }

                override fun onSuccess(bitmap: Bitmap, source: BitmapSource) {
                    setImage(divView, bitmap)
                    imageLoaded()
                }

                override fun onSuccess(drawable: Drawable, source: BitmapSource) {
                    setImage(divView, drawable)
                    imageLoaded()
                }

                override fun onError(e: Throwable?) {
                    super.onError(e)
                    gifUrl = null
                }
            }
        )

        divView.addLoadReference(reference, this)
        loadReference = reference
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun DivGifImageView.loadDrawable(cachedBitmap: CachedBitmap) {
        LoadDrawableOnPostPTask(WeakReference(this), cachedBitmap)
            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    private fun DivGifImageView.applyPlaceholders(
        divView: Div2View,
        resolver: ExpressionResolver,
        div: DivGifImage,
        errorCollector: ErrorCollector,
    ) {
        if (isImageLoaded) {
            return
        }

        val preview = div.preview?.evaluate(resolver)
        val previewUri = preview?.let(Uri::parse)
        val isUrlPreview = previewUri?.scheme != null && !previewUri.scheme.equals("data", ignoreCase = true)
        val newPreviewUrl = if (isUrlPreview) {
            previewUri
        } else if (preview == null) {
            div.previewUrl?.evaluate(resolver)
        } else {
            null
        }
        if (newPreviewUrl != null && isImagePreview && previewUrl == newPreviewUrl) {
            return
        }
        val previewLoader = previewLoader ?: ViewImagePreviewLoader(imageLoader).also {
            this.previewLoader = it
        }
        previewLoader.cancel()
        previewUrl = newPreviewUrl
        getLoadingTask()?.cancel(true)
        cleanLoadingTask()

        applyBase64PreviewAndPlaceholder(
            divView, resolver, div, errorCollector, preview.takeUnless { isUrlPreview }
        )

        if (newPreviewUrl == null || isImageLoaded) {
            return
        }

        applyPreviewUrl(divView, newPreviewUrl.toString(), previewLoader)
    }

    private fun DivGifImageView.applyBase64PreviewAndPlaceholder(
        divView: Div2View,
        resolver: ExpressionResolver,
        div: DivGifImage,
        errorCollector: ErrorCollector,
        preview: String?,
    ) {
        // if bitmap was already loaded for the same imageUrl, we don't load placeholders.
        placeholderLoader.applyPlaceholder(
            imageView = this,
            errorCollector = errorCollector,
            currentPreview = preview,
            currentPlaceholderColor = div.placeholderColor.evaluate(resolver),
            synchronous = false,
            onSetPlaceholder = {
                if (!isImageLoaded && !isImagePreview) {
                    setPlaceholder(divView, it)
                }
            },
            onSetPreview = {
                if (!isImageLoaded) {
                    when (it) {
                        is ImageRepresentation.Bitmap -> setPreview(divView, it.value)
                        is ImageRepresentation.PictureDrawable -> setPreview(divView, it.value)
                        is ImageRepresentation.Error -> Unit
                    }
                }
            }
        )
    }

    private fun DivGifImageView.applyPreviewUrl(
        divView: Div2View,
        newPreviewUrl: String,
        previewLoader: ViewImagePreviewLoader,
    ) {
        val request = previewLoader.load(
            url = newPreviewUrl,
            onLoaded = { image, request ->
                divView.runMainThreadAction(BindingTaskType.DATA) {
                    if (!request.isCurrent() || isImageLoaded) {
                        return@runMainThreadAction
                    }
                    when (image) {
                        is DivCachedImage.Bitmap -> setPreview(image.bitmap)
                        is DivCachedImage.Drawable -> setPreview(image.drawable)
                    }
                    previewLoaded()
                }
            },
            onError = { error, request ->
                error?.let(divView::logError)
                if (request.isCurrent()) {
                    previewUrl = null
                }
            },
        )
        divView.addLoadReference(request, this)
    }

    fun loadGifImage(view: DivGifImageView, divBlock: DivBlock.GifImage, divView: Div2View) =
        view.applyGifImage(divView, divBlock.expressionResolver, divBlock.divValue, divView.errorCollector)

    @RequiresApi(Build.VERSION_CODES.P)
    class LoadDrawableOnPostPTask(
        val view: WeakReference<DivGifImageView>,
        val cachedBitmap: CachedBitmap
    ) : AsyncTask<Void, Void, Drawable?>() {

        override fun doInBackground(vararg params: Void?): Drawable? {
            try {
                return createDrawableFromBytes()
            } catch (e: IOException) {
                KLog.e(TAG) { "Failed writing bytes to temp file, exception: ${e.message}" }
            } catch (e: IllegalStateException) {
                KLog.e(TAG) { "Failed create drawable from bytes, exception: ${e.message}" }
                // don't return null just yet, try to decode from uri source
            }

            val fromUriSource = createSourceFromUri()
            if (fromUriSource != null) {
                try {
                    return ImageDecoder.decodeDrawable(fromUriSource)
                } catch (e: IOException) {
                    KLog.e(TAG) { "Decode drawable from uri exception ${e.message}" }
                }
            }

            return null
        }

        override fun onPostExecute(result: Drawable?) {
            super.onPostExecute(result)
            if (result != null && result is AnimatedImageDrawable) {
                view.get()?.setImage(result)
            } else {
                view.get()?.setImage(cachedBitmap.bitmap)
            }
            view.get()?.imageLoaded()
        }

        /**
         * @return drawable from [cachedBitmap] bytes.
         * @throws IllegalStateException if conditions for creating temp files are not met.
         * @throws IOException if image decoding or writing to temp file goes wrong.
         */
        @Throws(IOException::class, IllegalStateException::class)
        private fun createDrawableFromBytes(): Drawable {
            val bytes: ByteArray = cachedBitmap.bytes ?: throw IllegalStateException("no bytes stored in cached bitmap")
            val context: Context = view.get()?.context ?: throw IllegalStateException("failed retrieve context")
            // We use file here, instead of ByteBuffer, for creating source, because of android sdk issue:
            // https://issuetracker.google.com/issues/139371066?pli=1
            val tempFile = File.createTempFile(TEMP_FILE_NAME, GIF_SUFFIX, context.cacheDir)
            return try {
                tempFile.writeBytes(bytes)
                val source: ImageDecoder.Source = ImageDecoder.createSource(tempFile)
                ImageDecoder.decodeDrawable(source)
            } finally {
                tempFile.delete()
            }
        }

        private fun createSourceFromUri(): ImageDecoder.Source? {
            val path = cachedBitmap.cacheUri?.path
            return if (path != null) {
                try {
                    ImageDecoder.createSource(File(path))
                } catch (e: IOException) {
                    KLog.e(TAG, e)
                    null
                }
            } else {
                KLog.e(TAG) { "No bytes or file in cache to decode gif drawable" }
                null
            }
        }
    }

    private companion object {
        const val TAG = "DivGifImageBinder"
        const val TEMP_FILE_NAME = "if_u_see_me_in_file_system_plz_report"
        const val GIF_SUFFIX = ".gif"

        private fun DivGifImageView.setPlaceholder(divView: Div2View, drawable: Drawable?) {
            divView.runMainThreadAction(BindingTaskType.DATA) {
                if (!isImageLoaded && !isImagePreview) {
                    setPlaceholder(drawable)
                }
            }
        }

        private fun DivGifImageView.setPreview(divView: Div2View, drawable: Drawable?) {
            divView.runMainThreadAction(BindingTaskType.DATA) {
                if (!isImageLoaded) {
                    setPreview(drawable)
                    previewLoaded()
                }
            }
        }

        private fun DivGifImageView.setPreview(divView: Div2View, bitmap: Bitmap?) {
            divView.runMainThreadAction(BindingTaskType.DATA) {
                if (!isImageLoaded) {
                    setPreview(bitmap)
                    previewLoaded()
                }
            }
        }

        private fun DivGifImageView.setImage(divView: Div2View, bitmap: Bitmap?) {
            divView.runMainThreadAction(BindingTaskType.DATA) {
                setImage(bitmap)
            }
        }

        private fun DivGifImageView.setImage(divView: Div2View, drawable: Drawable?) {
            divView.runMainThreadAction(BindingTaskType.DATA) {
                setImage(drawable)
            }
        }
    }
}
