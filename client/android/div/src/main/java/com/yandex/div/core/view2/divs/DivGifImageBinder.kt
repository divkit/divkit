package com.yandex.div.core.view2.divs

import android.content.Context
import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Build
import androidx.annotation.RequiresApi
import com.yandex.div.core.DivIdLoggingImageDownloadCallback
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.util.ImageRepresentation
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivPlaceholderLoader
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivGifImageView
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.internal.KLog
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

private const val TEMP_FILE_NAME = "if_u_see_me_in_file_system_plz_report"
private const val GIF_SUFFIX = ".gif"

@DivScope
internal class DivGifImageBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val imageLoader: DivImageLoader,
    private val placeholderLoader: DivPlaceholderLoader,
    private val errorCollectors: ErrorCollectors,
) : DivViewBinder<DivGifImage, DivGifImageView> {

    override fun bindView(view: DivGifImageView, div: DivGifImage, divView: Div2View) {
        val oldDiv = view.div
        if (div === oldDiv) return

        val errorCollector = errorCollectors.getOrCreate(divView.dataTag, divView.divData)

        val expressionResolver = divView.expressionResolver

        baseBinder.bindView(view, div, oldDiv, divView)

        view.applyDivActions(divView, div.action, div.actions, div.longtapActions,
            div.doubletapActions, div.actionAnimation, div.accessibility)

        view.bindAspectRatio(div.aspect, oldDiv?.aspect, expressionResolver)

        view.addSubscription(
            div.scale.observeAndGet(expressionResolver) { scale -> view.imageScale = scale.toImageScale() }
        )
        view.observeContentAlignment(expressionResolver, div.contentAlignmentHorizontal, div.contentAlignmentVertical)
        view.addSubscription(
            div.gifUrl.observeAndGet(expressionResolver) { view.applyGifImage(divView, expressionResolver, div, errorCollector) }
        )
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

    private fun DivGifImageView.applyGifImage(divView: Div2View,
                                              resolver: ExpressionResolver,
                                              div: DivGifImage,
                                              errorCollector: ErrorCollector) {
        val newGifUrl = div.gifUrl.evaluate(resolver)
        if (newGifUrl == gifUrl) {
            return
        }

        resetImageLoaded()
        loadReference?.cancel()

        // if bitmap was already loaded for the same imageUrl, we don't load placeholders.
        placeholderLoader.applyPlaceholder(
            this,
            errorCollector,
            div.preview?.evaluate(resolver),
            div.placeholderColor.evaluate(resolver),
            synchronous = false,
            onSetPlaceholder = {
                if (!isImageLoaded && !isImagePreview) {
                    setPlaceholder(it)
                }
            },
            onSetPreview = {
                if (!isImageLoaded) {
                    when (it) {
                        is ImageRepresentation.Bitmap -> setPreview(it.value)
                        is ImageRepresentation.PictureDrawable -> setPreview(it.value)
                    }
                    previewLoaded()
                }
            }
        )

        gifUrl = newGifUrl

        // we don't reuse this because not all clients has bytes cache
        val reference = imageLoader.loadImageBytes(
            newGifUrl.toString(),
            object : DivIdLoggingImageDownloadCallback(divView) {
                override fun onSuccess(cachedBitmap: CachedBitmap) {
                    super.onSuccess(cachedBitmap)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        loadDrawable(cachedBitmap)
                    } else {
                        setImage(cachedBitmap.bitmap)
                        imageLoaded()
                    }
                }

                override fun onSuccess(drawable: Drawable) {
                    super.onSuccess(drawable)
                    setImage(drawable)
                    imageLoaded()
                }

                override fun onError() {
                    super.onError()
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
    }
}
