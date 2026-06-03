package com.yandex.div.core.view2

import com.yandex.div.core.DivPreloader
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.DivImagePriority
import com.yandex.div.core.images.LoadReference
import com.yandex.div.internal.core.DivVisitor
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBackground
import javax.inject.Inject

@Mockable
@DivScope
class DivImagePreloader @Inject constructor(
    private val imageLoader: DivImageLoader
) {

    fun preloadImage(
        div: Div,
        resolver: ExpressionResolver,
        preloadFilter: DivPreloader.PreloadFilter = DivPreloader.PreloadFilter.ONLY_PRELOAD_REQUIRED_FILTER,
        callback: DivPreloader.DownloadCallback,
    ): List<LoadReference> = PreloadVisitor(callback, resolver, preloadFilter).preload(div)

    private fun preloadImage(
        url: String,
        callback: DivPreloader.DownloadCallback,
        references: ArrayList<LoadReference>
    ) {
        callback.onSingleLoadingStarted()
        references.add(imageLoader.loadImage(url, callback, DivImagePriority.IMAGES_PRIORITY_PRELOAD))
    }

    private fun preloadImageBytes(
        url: String,
        callback: DivPreloader.DownloadCallback,
        references: ArrayList<LoadReference>
    ) {
        callback.onSingleLoadingStarted()
        references.add(imageLoader.loadImageBytes(url, callback, DivImagePriority.IMAGES_PRIORITY_PRELOAD))
    }

    private inner class PreloadVisitor(
        private val callback: DivPreloader.DownloadCallback,
        private val resolver: ExpressionResolver,
        private val preloadFilter: DivPreloader.PreloadFilter,
    ) : DivVisitor<Unit>() {
        private val references = ArrayList<LoadReference>()

        fun preload(div: Div): List<LoadReference> {
            visit(div, resolver)
            return references
        }

        override fun defaultVisit(data: Div, resolver: ExpressionResolver) {
            visitBackground(data, resolver)
        }

        override fun visit(data: Div.Text, resolver: ExpressionResolver) {
            defaultVisit(data, resolver)
            if (preloadFilter.shouldPreloadContent(data, resolver)) {
                data.value.images?.forEach { preloadImage(it.url.evaluate(resolver).toString(), callback, references) }
            }
        }

        override fun visit(data: Div.Image, resolver: ExpressionResolver) {
            defaultVisit(data, resolver)
            if (preloadFilter.shouldPreloadContent(data, resolver)) {
                preloadImage(data.value.imageUrl.evaluate(resolver).toString(), callback, references)
            }
        }

        override fun visit(data: Div.GifImage, resolver: ExpressionResolver) {
            defaultVisit(data, resolver)
            if (preloadFilter.shouldPreloadContent(data, resolver)) {
                preloadImageBytes(data.value.gifUrl.evaluate(resolver).toString(), callback, references)
            }
        }

        private fun visitBackground(data: Div, resolver: ExpressionResolver) {
            data.value().background?.forEach { background ->
                if (background is DivBackground.Image && preloadFilter.shouldPreloadBackground(background, resolver)) {
                    preloadImage(background.value.imageUrl.evaluate(resolver).toString(), callback, references)
                }
            }
        }
    }

    @Deprecated("Not used in DivKit")
    interface Ticket {
        fun cancel()
    }

    @Deprecated("Not used in DivKit")
    fun interface Callback {
        fun finish(hasErrors: Boolean)
    }

    @Deprecated("Not used in DivKit")
    fun Callback.toPreloadCallback(): DivPreloader.Callback {
        return DivPreloader.Callback { hasErrors -> this@toPreloadCallback.finish(hasErrors) }
    }
}
