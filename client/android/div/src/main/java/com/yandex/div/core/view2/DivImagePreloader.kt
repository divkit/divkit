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
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGifImage
import com.yandex.div2.DivGrid
import com.yandex.div2.DivImage
import com.yandex.div2.DivIndicator
import com.yandex.div2.DivInput
import com.yandex.div2.DivPager
import com.yandex.div2.DivSelect
import com.yandex.div2.DivSeparator
import com.yandex.div2.DivSlider
import com.yandex.div2.DivState
import com.yandex.div2.DivTabs
import com.yandex.div2.DivText
import com.yandex.div2.DivVideo
import javax.inject.Inject

private val NO_CALLBACK = DivImagePreloader.Callback {
    //not interested
}

@Mockable
@DivScope
class DivImagePreloader @Inject constructor(
    private val imageLoader: DivImageLoader
) {

    @Deprecated("deprecated", replaceWith = ReplaceWith("DivPreloader.preload"))
    fun preload(div: Div, resolver: ExpressionResolver, callback: Callback = NO_CALLBACK): Ticket {
        val downloadCallback = DivPreloader.DownloadCallback(callback.toPreloadCallback())
        val ref = PreloadVisitor(downloadCallback, resolver).preload(div)
        downloadCallback.onFullPreloadStarted()
        return ref
    }

    fun preloadImage(
        div: DivBase,
        resolver: ExpressionResolver,
        callback: DivPreloader.DownloadCallback
    ): List<LoadReference> {
        val ref = PreloadVisitor(callback, resolver, visitContainers = false).preload(div)
        return ref
    }

    private fun preloadImage(url: String, callback: DivPreloader.DownloadCallback, references: ArrayList<LoadReference>) {
        references.add(imageLoader.loadImage(url, callback, DivImagePriority.IMAGES_PRIORITY_PRELOAD))
        callback.onSingleLoadingStarted()
    }

    private fun preloadImageBytes(url: String, callback: DivPreloader.DownloadCallback, references: ArrayList<LoadReference>) {
        references.add(imageLoader.loadImageBytes(url, callback, DivImagePriority.IMAGES_PRIORITY_PRELOAD))
        callback.onSingleLoadingStarted()
    }

    private inner class PreloadVisitor(
        private val callback: DivPreloader.DownloadCallback,
        private val resolver: ExpressionResolver,
        private val visitContainers: Boolean = true,
    ) : DivVisitor<Unit>() {
        private val references = ArrayList<LoadReference>()
        private val ticket = TicketImpl()
        fun preload(div: Div): Ticket {
            visit(div, resolver)
            references.forEach { ticket.addReference(it) }
            return ticket
        }

        fun preload(div: DivBase): List<LoadReference> {
            visit(div, resolver)
            return references
        }

        override fun visit(data: DivText, resolver: ExpressionResolver) {
            visitBackground(data, resolver)
            data.images?.forEach { preloadImage(it.url.evaluate(resolver).toString(), callback, references) }
        }

        override fun visit(data: DivImage, resolver: ExpressionResolver) {
            visitBackground(data, resolver)
            if (data.preloadRequired.evaluate(resolver)) {
                preloadImage(data.imageUrl.evaluate(resolver).toString(), callback, references)
            }
        }

        override fun visit(data: DivGifImage, resolver: ExpressionResolver) {
            visitBackground(data, resolver)
            if (data.preloadRequired.evaluate(resolver)) {
                preloadImageBytes(data.gifUrl.evaluate(resolver).toString(), callback, references)
            }
        }

        override fun visit(data: DivSeparator, resolver: ExpressionResolver) {
            visitBackground(data, resolver)
        }

        override fun visit(data: DivContainer, resolver: ExpressionResolver) {
            visitBackground(data, resolver)
            if (visitContainers) {
                data.items.forEach { visit(it, resolver) }
            }
        }

        override fun visit(data: DivGrid, resolver: ExpressionResolver) {
            visitBackground(data, resolver)
            if (visitContainers) {
                data.items.forEach { visit(it, resolver) }
            }
        }

        override fun visit(data: DivGallery, resolver: ExpressionResolver) {
            visitBackground(data, resolver)
            if (visitContainers) {
                data.items.forEach { visit(it, resolver) }
            }
        }

        override fun visit(data: DivPager, resolver: ExpressionResolver) {
            visitBackground(data, resolver)
            if (visitContainers) {
                data.items.forEach { visit(it, resolver) }
            }
        }

        override fun visit(data: DivTabs, resolver: ExpressionResolver) {
            visitBackground(data, resolver)
            if (visitContainers) {
                data.items.forEach { visit(it.div, resolver) }
            }
        }

        override fun visit(data: DivState, resolver: ExpressionResolver) {
            visitBackground(data, resolver)
            if (visitContainers) {
                data.states.forEach { state -> state.div?.let { div -> visit(div, resolver) } }
            }
        }

        override fun visit(data: DivCustom, resolver: ExpressionResolver) {
            visitBackground(data, resolver)
        }

        override fun visit(data: DivIndicator, resolver: ExpressionResolver) {
            visitBackground(data, resolver)
        }

        override fun visit(data: DivSlider, resolver: ExpressionResolver) {
            visitBackground(data, resolver)
        }

        override fun visit(data: DivInput, resolver: ExpressionResolver) {
            visitBackground(data, resolver)
        }

        override fun visit(data: DivSelect, resolver: ExpressionResolver) {
            visitBackground(data, resolver)
        }

        override fun visit(data: DivVideo, resolver: ExpressionResolver) {
            visitBackground(data, resolver)
        }

        private fun visitBackground(data: DivBase, resolver: ExpressionResolver) {
            data.background?.forEach { background ->
                if (background is DivBackground.Image && background.value.preloadRequired.evaluate(resolver)) {
                    preloadImage(background.value.imageUrl.evaluate(resolver).toString(), callback, references)
                }
            }
        }
    }

    private class TicketImpl : Ticket {
        val refs = mutableListOf<LoadReference>()
        fun addReference(reference: LoadReference) {
            refs.add(reference)
        }

        override fun cancel() {
            refs.forEach {
                it.cancel()
            }
        }
    }

    fun interface Callback {
        fun finish(hasErrors: Boolean)
    }

    fun Callback.toPreloadCallback(): DivPreloader.Callback {
        return DivPreloader.Callback { hasErrors -> this@toPreloadCallback.finish(hasErrors) }
    }

    interface Ticket {
        fun cancel()
    }
}
