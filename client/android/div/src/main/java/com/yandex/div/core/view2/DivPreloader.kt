package com.yandex.div.core.view2

import com.yandex.div.core.DivCustomViewAdapter
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.view2.DivPreloader.PreloadReference
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGifImage
import com.yandex.div2.DivGrid
import com.yandex.div2.DivImage
import com.yandex.div2.DivIndicator
import com.yandex.div2.DivInput
import com.yandex.div2.DivPager
import com.yandex.div2.DivSeparator
import com.yandex.div2.DivSlider
import com.yandex.div2.DivState
import com.yandex.div2.DivTabs
import com.yandex.div2.DivText
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

private val NO_CALLBACK = DivPreloader.Callback {}

@Mockable
@DivScope
class DivPreloader @Inject constructor(
    private val imagePreloader: DivImagePreloader?,
    private val divCustomViewAdapter: DivCustomViewAdapter?,
    extensionHandlers: List<DivExtensionHandler>
) {
    private val extensionController = DivExtensionController(extensionHandlers)

    fun preload(div: Div, resolver: ExpressionResolver, callback: Callback = NO_CALLBACK): Ticket {
        val downloadCallback = DownloadCallback(callback)
        val ticket = PreloadVisitor(downloadCallback, callback, resolver).preload(div)
        downloadCallback.onFullPreloadStarted()
        return ticket
    }

    private inner class PreloadVisitor(
        private val downloadCallback: DownloadCallback,
        private val callback: Callback,
        private val resolver: ExpressionResolver,
    ) : DivVisitor<Unit>() {
        private val ticket = TicketImpl()

        fun preload(div: Div): Ticket {
            visit(div, resolver)
            return ticket
        }

        override fun visit(data: DivText, resolver: ExpressionResolver) {
            imagePreloader?.preloadImage(data, resolver, downloadCallback)
                ?.forEach { ticket.addImageReference(it) }
            extensionController.preprocessExtensions(data, resolver)
        }

        override fun visit(data: DivImage, resolver: ExpressionResolver) {
            imagePreloader?.preloadImage(data, resolver, downloadCallback)
                ?.forEach { ticket.addImageReference(it) }
            extensionController.preprocessExtensions(data, resolver)
        }

        override fun visit(data: DivGifImage, resolver: ExpressionResolver) {
            imagePreloader?.preloadImage(data, resolver, downloadCallback)
                ?.forEach { ticket.addImageReference(it) }
            extensionController.preprocessExtensions(data, resolver)
        }

        override fun visit(data: DivSeparator, resolver: ExpressionResolver) {
            imagePreloader?.preloadImage(data, resolver, downloadCallback)
                ?.forEach { ticket.addImageReference(it) }
            extensionController.preprocessExtensions(data, resolver)
        }

        override fun visit(data: DivContainer, resolver: ExpressionResolver) {
            imagePreloader?.preloadImage(data, resolver, downloadCallback)
                ?.forEach { ticket.addImageReference(it) }
            data.items.forEach { visit(it, resolver) }
            extensionController.preprocessExtensions(data, resolver)
        }

        override fun visit(data: DivGrid, resolver: ExpressionResolver) {
            imagePreloader?.preloadImage(data, resolver, downloadCallback)
                ?.forEach { ticket.addImageReference(it) }
            data.items.forEach { visit(it, resolver) }
            extensionController.preprocessExtensions(data, resolver)
        }

        override fun visit(data: DivGallery, resolver: ExpressionResolver) {
            imagePreloader?.preloadImage(data, resolver, downloadCallback)
                ?.forEach { ticket.addImageReference(it) }
            data.items.forEach { visit(it, resolver) }
            extensionController.preprocessExtensions(data, resolver)
        }

        override fun visit(data: DivPager, resolver: ExpressionResolver) {
            imagePreloader?.preloadImage(data, resolver, downloadCallback)
                ?.forEach { ticket.addImageReference(it) }
            data.items.forEach { visit(it, resolver) }
            extensionController.preprocessExtensions(data, resolver)
        }

        override fun visit(data: DivTabs, resolver: ExpressionResolver) {
            imagePreloader?.preloadImage(data, resolver, downloadCallback)
                ?.forEach { ticket.addImageReference(it) }
            data.items.forEach { visit(it.div, resolver) }
            extensionController.preprocessExtensions(data, resolver)
        }

        override fun visit(data: DivState, resolver: ExpressionResolver) {
            imagePreloader?.preloadImage(data, resolver, downloadCallback)
                ?.forEach { ticket.addImageReference(it) }
            data.states.forEach { state -> state.div?.let { div -> visit(div, resolver) } }
            extensionController.preprocessExtensions(data, resolver)
        }

        override fun visit(data: DivCustom, resolver: ExpressionResolver) {
            imagePreloader?.preloadImage(data, resolver, downloadCallback)
                ?.forEach { ticket.addImageReference(it) }
            data.items?.forEach { visit(it, resolver) }
            divCustomViewAdapter?.preload(data, callback)?.let { ticket.addReference(it) }
            extensionController.preprocessExtensions(data, resolver)
        }

        override fun visit(data: DivIndicator, resolver: ExpressionResolver) {
            imagePreloader?.preloadImage(data, resolver, downloadCallback)
                ?.forEach { ticket.addImageReference(it) }
            extensionController.preprocessExtensions(data, resolver)
        }

        override fun visit(data: DivSlider, resolver: ExpressionResolver) {
            imagePreloader?.preloadImage(data, resolver, downloadCallback)
                ?.forEach { ticket.addImageReference(it) }
            extensionController.preprocessExtensions(data, resolver)
        }

        override fun visit(data: DivInput, resolver: ExpressionResolver) {
            imagePreloader?.preloadImage(data, resolver, downloadCallback)
                ?.forEach { ticket.addImageReference(it) }
            extensionController.preprocessExtensions(data, resolver)
        }
    }

    private class TicketImpl : Ticket {
        val refs = mutableListOf<PreloadReference>()
        fun addReference(reference: PreloadReference) {
            refs.add(reference)
        }

        fun addImageReference(reference: LoadReference) {
            refs.add(reference.toPreloadReference())
        }

        override fun cancel() {
            refs.forEach {
                it.cancel()
            }
        }

        private fun LoadReference.toPreloadReference(): PreloadReference {
            @Suppress("ObjectLiteralToLambda")  // for readability
            return object : PreloadReference {
                override fun cancel() {
                    this@toPreloadReference.cancel()
                }
            }
        }
    }

    class DownloadCallback(private val callback: Callback) : DivImageDownloadCallback() {
        private var downloadsLeftCount: AtomicInteger = AtomicInteger(0)
        private var failures: AtomicInteger = AtomicInteger(0)
        private var started: AtomicBoolean = AtomicBoolean(false)

        fun onSingleLoadingStarted() {
            downloadsLeftCount.incrementAndGet()
        }

        override fun onSuccess(cachedBitmap: CachedBitmap) {
            done()
        }

        override fun onError() {
            failures.incrementAndGet()
            done()
        }

        private fun done() {
            downloadsLeftCount.decrementAndGet()
            if (downloadsLeftCount.get() == 0 && started.get()) {
                callback.finish(failures.get() != 0)
            }
        }

        fun onFullPreloadStarted() {
            started.set(true)
            if (downloadsLeftCount.get() == 0) {
                callback.finish(failures.get() != 0)
            }
        }
    }

    fun interface Callback {
        fun finish(hasErrors: Boolean)
    }

    interface Ticket {
        fun cancel()
    }

    fun interface PreloadReference {
        fun cancel()

        companion object {
            val EMPTY = PreloadReference {}
        }
    }
}
