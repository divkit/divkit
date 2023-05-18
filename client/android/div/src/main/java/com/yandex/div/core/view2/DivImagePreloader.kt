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

private val NO_CALLBACK = DivImagePreloader.Callback {
    //not interested
}

@Mockable
@DivScope
class DivImagePreloader @Inject constructor(
    private val imageLoader: DivImageLoader
) {

    @Deprecated("deprecated", replaceWith = ReplaceWith("DivPreloader.preloadImage"))
    fun preload(div: Div, resolver: ExpressionResolver, callback: Callback = NO_CALLBACK): Ticket {
        val downloadCallback = DivPreloader.DownloadCallback(callback.toPreloadCallback())
        val ref = PreloadVisitor(downloadCallback, resolver).preload(div)
        downloadCallback.onFullPreloadStarted()
        return ref.asTicket()
    }

    fun preloadImage(
        div: Div,
        resolver: ExpressionResolver,
        callback: DivPreloader.DownloadCallback
    ): List<LoadReference> {
        return PreloadVisitor(callback, resolver, visitContainers = false).preload(div)
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

        fun preload(div: Div): List<LoadReference> {
            visit(div, resolver)
            return references
        }

        override fun defaultVisit(data: Div, resolver: ExpressionResolver) {
            visitBackground(data, resolver)
        }

        override fun visit(data: Div.Text, resolver: ExpressionResolver) {
            defaultVisit(data, resolver)
            data.value.images?.forEach { preloadImage(it.url.evaluate(resolver).toString(), callback, references) }
        }

        override fun visit(data: Div.Image, resolver: ExpressionResolver) {
            defaultVisit(data, resolver)
            if (data.value.preloadRequired.evaluate(resolver)) {
                preloadImage(data.value.imageUrl.evaluate(resolver).toString(), callback, references)
            }
        }

        override fun visit(data: Div.GifImage, resolver: ExpressionResolver) {
            defaultVisit(data, resolver)
            if (data.value.preloadRequired.evaluate(resolver)) {
                preloadImageBytes(data.value.gifUrl.evaluate(resolver).toString(), callback, references)
            }
        }

        override fun visit(data: Div.Container, resolver: ExpressionResolver) {
            defaultVisit(data, resolver)
            if (visitContainers) {
                data.value.items.forEach { visit(it, resolver) }
            }
        }

        override fun visit(data: Div.Grid, resolver: ExpressionResolver) {
            defaultVisit(data, resolver)
            if (visitContainers) {
                data.value.items.forEach { visit(it, resolver) }
            }
        }

        override fun visit(data: Div.Gallery, resolver: ExpressionResolver) {
            defaultVisit(data, resolver)
            if (visitContainers) {
                data.value.items.forEach { visit(it, resolver) }
            }
        }

        override fun visit(data: Div.Pager, resolver: ExpressionResolver) {
            defaultVisit(data, resolver)
            if (visitContainers) {
                data.value.items.forEach { visit(it, resolver) }
            }
        }

        override fun visit(data: Div.Tabs, resolver: ExpressionResolver) {
            defaultVisit(data, resolver)
            if (visitContainers) {
                data.value.items.forEach { visit(it.div, resolver) }
            }
        }

        override fun visit(data: Div.State, resolver: ExpressionResolver) {
            defaultVisit(data, resolver)
            if (visitContainers) {
                data.value.states.forEach { state -> state.div?.let { div -> visit(div, resolver) } }
            }
        }

        private fun visitBackground(data: Div, resolver: ExpressionResolver) {
            data.value().background?.forEach { background ->
                if (background is DivBackground.Image && background.value.preloadRequired.evaluate(resolver)) {
                    preloadImage(background.value.imageUrl.evaluate(resolver).toString(), callback, references)
                }
            }
        }
    }

    interface Ticket {
        fun cancel()
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

    internal fun List<LoadReference>.asTicket(): Ticket {
        return TicketImpl().apply {
            forEach { addReference(it) }
        }
    }

    fun interface Callback {
        fun finish(hasErrors: Boolean)
    }

    fun Callback.toPreloadCallback(): DivPreloader.Callback {
        return DivPreloader.Callback { hasErrors -> this@toPreloadCallback.finish(hasErrors) }
    }
}
