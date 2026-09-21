package com.yandex.div.compose.preload

import android.net.Uri
import coil3.ImageLoader
import coil3.request.ErrorResult
import coil3.request.SuccessResult
import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div.compose.images.ImageRequestFactory
import com.yandex.div.compose.images.ImageRequestParams
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBackground
import javax.inject.Inject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

@DivContextScope
internal class CoilImagePreloader @Inject constructor(
    private val imageLoader: ImageLoader,
    private val imageRequestFactory: ImageRequestFactory,
) : ImagePreloader {

    override suspend fun preloadImages(
        div: Div,
        resolver: ExpressionResolver,
        downloadAll: Boolean,
    ): PreloadResult = coroutineScope {
        val preloads = mutableListOf<Deferred<PreloadResult>>()

        div.value().background?.forEach { background ->
            if (background is DivBackground.Image &&
                (downloadAll || background.value.preloadRequired.evaluate(resolver))
            ) {
                preloads += async { loadImage(background.value.imageUrl.evaluate(resolver)) }
            }
        }
        when (div) {
            is Div.Text -> {
                div.value.images?.forEach { image ->
                    if (downloadAll || image.preloadRequired.evaluate(resolver)) {
                        preloads += async { loadImage(image.url.evaluate(resolver)) }
                    }
                }
            }

            is Div.Image -> {
                if (downloadAll || div.value.preloadRequired.evaluate(resolver)) {
                    preloads += async { loadImage(div.value.imageUrl?.evaluate(resolver)) }
                }
            }

            is Div.GifImage -> {
                if (downloadAll || div.value.preloadRequired.evaluate(resolver)) {
                    preloads += async { loadImage(div.value.gifUrl?.evaluate(resolver)) }
                }
            }

            else -> Unit
        }
        preloads.awaitAll().combineResults()
    }

    private suspend fun loadImage(url: Uri?): PreloadResult {
        if (url == null) return PreloadResult(true)

        val params = ImageRequestParams(data = url)
        val request = imageRequestFactory.build(params)
        return when (val result = imageLoader.execute(request)) {
            is SuccessResult -> PreloadResult(true)
            is ErrorResult -> PreloadResult(false)
        }
    }
}
