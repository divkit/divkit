package com.yandex.div.core

import android.graphics.drawable.PictureDrawable
import android.net.Uri
import com.yandex.div.core.DivPreloader.Callback
import com.yandex.div.core.DivPreloader.PreloadReference
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.player.DivPlayerPreloader
import com.yandex.div.core.view2.DivImagePreloader
import com.yandex.div.internal.core.DivVisitor
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.internal.util.UiThreadHandler
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBackground

@PublicApi
@Mockable
class DivPreloader internal constructor(
    private val imagePreloader: DivImagePreloader?,
    private val customContainerViewAdapter: DivCustomContainerViewAdapter,
    private val extensionController: DivExtensionController,
    private val videoPreloader: DivPlayerPreloader,
    private val preloadFilter: PreloadFilter
) {

    @JvmOverloads
    constructor(
        configuration: DivConfiguration,
        preloadFilter: PreloadFilter = PreloadFilter.ONLY_PRELOAD_REQUIRED_FILTER
    ) : this(
        imagePreloader = DivImagePreloader(configuration.imageLoader),
        customContainerViewAdapter = configuration.divCustomContainerViewAdapter,
        extensionController = DivExtensionController(configuration.extensionHandlers),
        videoPreloader = configuration.divPlayerPreloader,
        preloadFilter = preloadFilter
    )

    @JvmOverloads
    constructor(
        context: Div2Context,
        preloadFilter: PreloadFilter = PreloadFilter.ONLY_PRELOAD_REQUIRED_FILTER
    ) : this(
        imagePreloader = context.div2Component.imagePreloader,
        customContainerViewAdapter = context.div2Component.divCustomContainerViewAdapter,
        extensionController = context.div2Component.extensionController,
        videoPreloader = context.div2Component.divVideoPreloader,
        preloadFilter = preloadFilter
    )

    fun preload(
        div: Div,
        resolver: ExpressionResolver,
        callback: Callback = NO_CALLBACK
    ): Ticket {
        val downloadCallback = DownloadCallback(callback)
        val ticket = PreloadVisitor(downloadCallback, callback, resolver, preloadFilter).preload(div)
        downloadCallback.onFullPreloadStarted()
        return ticket
    }

    companion object {
        internal val NO_CALLBACK = Callback { }
    }

    private inner class PreloadVisitor(
        private val downloadCallback: DownloadCallback,
        private val callback: Callback,
        private val resolver: ExpressionResolver,
        private val preloadFilter: PreloadFilter
    ) : DivVisitor<Unit>() {
        private val ticket = TicketImpl()

        fun preload(div: Div): Ticket {
            visit(div, resolver)
            return ticket
        }

        override fun defaultVisit(data: Div, resolver: ExpressionResolver) {
            imagePreloader?.preloadImage(data, resolver, preloadFilter, downloadCallback)
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
            data.value.buildItems(resolver).forEach { (item, newResolver) -> visit(item, newResolver) }
            defaultVisit(data, resolver)
        }

        override fun visit(data: Div.Pager, resolver: ExpressionResolver) {
            data.value.buildItems(resolver).forEach { (item, newResolver) -> visit(item, newResolver) }
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
            customContainerViewAdapter.preload(data.value, callback).also { ticket.addReference(it) }
            defaultVisit(data, resolver)
        }

        override fun visit(data: Div.Video, resolver: ExpressionResolver) {
            defaultVisit(data, resolver)
            if (preloadFilter.shouldPreloadContent(data, resolver)) {
                val sources = mutableListOf<Uri>()
                data.value.videoSources.forEach {
                    sources.add(it.url.evaluate(resolver))
                }
                videoPreloader.preloadVideo(sources).also { ticket.addReference(it) }
            }
        }
    }

    internal class TicketImpl : Ticket {

        private val refs = mutableListOf<PreloadReference>()

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
        private var downloadsLeftCount = 0
        private var failures = 0
        private var started = false

        fun onSingleLoadingStarted() = runOnUiThread {
            downloadsLeftCount++
        }

        override fun onSuccess(cachedBitmap: CachedBitmap) {
            done()
        }

        override fun onSuccess(pictureDrawable: PictureDrawable) {
            done()
        }

        override fun onError() = runOnUiThread {
            failures++
            done()
        }

        private fun done() = runOnUiThread {
            downloadsLeftCount--
            if (downloadsLeftCount == 0 && started) {
                callback.finish(failures != 0)
            }
        }

        fun onFullPreloadStarted() = runOnUiThread {
            started = true
            if (downloadsLeftCount == 0) {
                callback.finish(failures != 0)
            }
        }

        private inline fun runOnUiThread(crossinline action: () -> Unit) {
            UiThreadHandler.executeOnMainThread(action)
        }
    }

    fun interface Callback {
        fun finish(hasErrors: Boolean)
    }

    interface Ticket {
        fun cancel()
    }

    interface PreloadFilter {
        fun shouldPreloadContent(div: Div, resolver: ExpressionResolver): Boolean
        fun shouldPreloadBackground(background: DivBackground, resolver: ExpressionResolver): Boolean

        companion object {
            @JvmField
            val ONLY_PRELOAD_REQUIRED_FILTER = object : PreloadFilter {
                override fun shouldPreloadContent(div: Div, resolver: ExpressionResolver) = when(div) {
                    is Div.Video -> div.value.preloadRequired.evaluate(resolver)
                    is Div.Image -> div.value.preloadRequired.evaluate(resolver)
                    is Div.GifImage -> div.value.preloadRequired.evaluate(resolver)
                    else -> false
                }

                override fun shouldPreloadBackground(
                    background: DivBackground,
                    resolver: ExpressionResolver
                ) = when(background) {
                    is DivBackground.Image -> background.value.preloadRequired.evaluate(resolver)
                    else -> false
                }
            }

            @JvmField
            val PRELOAD_ALL_FILTER = object : PreloadFilter {
                override fun shouldPreloadContent(div: Div, resolver: ExpressionResolver) = true
                override fun shouldPreloadBackground(background: DivBackground, resolver: ExpressionResolver) = true
            }
        }
    }

    fun interface PreloadReference {
        fun cancel()

        companion object {
            val EMPTY = PreloadReference {}
        }
    }
}
