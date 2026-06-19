package com.yandex.div.compose.preload

import coil3.ImageLoader
import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div.compose.images.ImageRequestFactory
import com.yandex.div.compose.images.ImageRequestParams
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBackground
import javax.inject.Inject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@DivContextScope
internal class CoilImagePreloader @Inject constructor(
    private val imageLoader: ImageLoader,
    private val imageRequestFactory: ImageRequestFactory,
) : ImagePreloader {

    override suspend fun preloadImages(
        div: Div,
        resolver: ExpressionResolver,
    ): Unit = coroutineScope {
        div.value().background?.forEach { background ->
            if (background is DivBackground.Image && background.value.preloadRequired.evaluate(resolver)) {
                launch { loadImage(background.value.imageUrl.evaluate(resolver).toString()) }
            }
        }
        when (div) {
            is Div.Text -> {
                div.value.images?.forEach { image ->
                    if (image.preloadRequired.evaluate(resolver)) {
                        launch { loadImage(image.url.evaluate(resolver).toString()) }
                    }
                }
            }
            is Div.Image -> {
                if (div.value.preloadRequired.evaluate(resolver)) {
                    launch { loadImage(div.value.imageUrl.evaluate(resolver).toString()) }
                }
            }
            is Div.GifImage -> {
                if (div.value.preloadRequired.evaluate(resolver)) {
                    launch { loadImage(div.value.gifUrl.evaluate(resolver).toString()) }
                }
            }
            else -> Unit
        }
    }

    private suspend fun loadImage(url: String) {
        val params = ImageRequestParams(data = url, transformations = emptyList())
        val request = imageRequestFactory.build(params)
        imageLoader.execute(request)
    }
}
