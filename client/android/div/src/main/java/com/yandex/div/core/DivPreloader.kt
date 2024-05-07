package com.yandex.div.core

import android.net.Uri
import android.graphics.drawable.PictureDrawable
import com.yandex.div.core.DivPreloader.Callback
import com.yandex.div.core.DivPreloader.PreloadReference
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.player.DivPlayerPreloader
import com.yandex.div.core.view2.DivImagePreloader
import com.yandex.div.internal.core.DivVisitor
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@PublicApi
@Mockable
class DivPreloader internal constructor(
    private val imagePreloader: DivImagePreloader?,
    private val customViewAdapter: DivCustomViewAdapter?,
    private val customContainerViewAdapter: DivCustomContainerViewAdapter,
    private val extensionController: DivExtensionController,
    private val videoPreloader: DivPlayerPreloader,
) {

    constructor(context: Div2Context) : this(
        imagePreloader = context.div2Component.imagePreloader,
        customViewAdapter = context.div2Component.divCustomViewAdapter,
        customContainerViewAdapter = context.div2Component.divCustomContainerViewAdapter,
        extensionController = context.div2Component.extensionController,
        videoPreloader = context.div2Component.divVideoPreloader
    )

    @Deprecated("Use DivPreloader(Div2Context) instead")
    constructor(
        imagePreloader: DivImagePreloader?,
        customViewAdapter: DivCustomViewAdapter?,
        extensionHandlers: List<DivExtensionHandler>
    ) : this(
        imagePreloader = imagePreloader,
        customViewAdapter = customViewAdapter,
        customContainerViewAdapter = DivCustomContainerViewAdapter.STUB,
        extensionController = DivExtensionController(extensionHandlers),
        videoPreloader = DivPlayerPreloader.STUB,
    )

    fun preload(div: Div, resolver: ExpressionResolver, callback: Callback = NO_CALLBACK): Ticket {
        val downloadCallback = DownloadCallback(callback)
        val ticket = PreloadVisitor(downloadCallback, callback, resolver).preload(div)
        downloadCallback.onFullPreloadStarted()
        return ticket
    }

    private companion object {
        private val NO_CALLBACK = Callback { }
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

        override fun defaultVisit(data: Div, resolver: ExpressionResolver) {
            imagePreloader?.preloadImage(data, resolver, downloadCallback)
                ?.forEach { ticket.addImageReference(it) }
            extensionController.preprocessExtensions(data.value(), resolver)
        }

        override fun visit(data: Div.Container, resolver: ExpressionResolver) {
            data.value.buildItems(resolver).forEach { (item, newResolver) -> visit(item, newResolver) }
            defaultVisit(data, resolver)
        }

        override fun visit(data: Div.Grid, resolver: ExpressionResolver) {
            data.value.nonNullItems.forEach { visit(it, resolver) }
            defaultVisit(data, resolver)
        }

        override fun visit(data: Div.Gallery, resolver: ExpressionResolver) {
            data.value.nonNullItems.forEach { visit(it, resolver) }
            defaultVisit(data, resolver)
        }

        override fun visit(data: Div.Pager, resolver: ExpressionResolver) {
            data.value.nonNullItems.forEach { visit(it, resolver) }
            defaultVisit(data, resolver)
        }

        override fun visit(data: Div.Tabs, resolver: ExpressionResolver) {
            data.value.items.forEach { visit(it.div, resolver) }
            defaultVisit(data, resolver)
        }

        override fun visit(data: Div.State, resolver: ExpressionResolver) {
            data.value.states.forEach { state -> state.div?.let { div -> visit(div, resolver) } }
            defaultVisit(data, resolver)
        }

        override fun visit(data: Div.Custom, resolver: ExpressionResolver) {
            data.value.items?.forEach { visit(it, resolver) }
            customViewAdapter?.preload(data.value, callback)?.let { ticket.addReference(it) }
            customContainerViewAdapter.preload(data.value, callback).also { ticket.addReference(it) }
            defaultVisit(data, resolver)
        }

        override fun visit(data: Div.Video, resolver: ExpressionResolver) {
            defaultVisit(data, resolver)
            if (data.value.preloadRequired.evaluate(resolver)) {
                val sources = mutableListOf<Uri>()
                data.value.videoSources.forEach {
                    sources.add(it.url.evaluate(resolver))
                }
                videoPreloader.preloadVideo(sources).also { ticket.addReference(it) }
            }
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

        override fun onSuccess(pictureDrawable: PictureDrawable) {
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
