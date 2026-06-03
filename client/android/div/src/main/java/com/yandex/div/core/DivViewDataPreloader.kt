package com.yandex.div.core

import android.net.Uri
import com.yandex.div.core.DivPreloader.Callback
import com.yandex.div.core.DivPreloader.Companion.NO_CALLBACK
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.player.DivPlayerPreloader
import com.yandex.div.core.preload.CompositeResult
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.DivImagePreloader
import com.yandex.div.internal.core.DivTreeVisitor
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivData

@Mockable
internal class DivViewDataPreloader internal constructor(
    private val imagePreloader: DivImagePreloader?,
    private val customContainerViewAdapter: DivCustomContainerViewAdapter,
    private val extensionController: DivExtensionController,
    private val videoPreloader: DivPlayerPreloader,
    private val preloadFilter: DivPreloader.PreloadFilter
) {

    fun preload(
        data: DivData,
        resolver: ExpressionResolver,
        callback: Callback = NO_CALLBACK
    ): DivPreloader.Ticket {
        val downloadCallback = DivPreloader.DownloadCallback(callback)
        val ticket = PreloadVisitor(downloadCallback, callback, preloadFilter).preload(data, resolver)
        downloadCallback.onFullPreloadStarted()
        return ticket
    }

    private inner class PreloadVisitor(
        private val downloadCallback: DivPreloader.DownloadCallback,
        private val callback: Callback,
        private val preloadFilter: DivPreloader.PreloadFilter
    ) : DivTreeVisitor<Unit>() {
        private val ticket = DivPreloader.TicketImpl()

        fun preload(data: DivData, resolver: ExpressionResolver): DivPreloader.Ticket {
            visit(data, resolver)
            return ticket
        }

        override fun defaultVisit(data: Div, resolver: ExpressionResolver, path: DivStatePath) {
            imagePreloader?.preloadImage(data, resolver, preloadFilter, downloadCallback)
                ?.forEach { ticket.addImageReference(it) }
            extensionController.preprocessExtensions(data.value(), resolver, downloadCallback)
        }

        override fun visit(data: Div.Custom, resolver: ExpressionResolver, path: DivStatePath) {
            super.visit(data, resolver, path)
            customContainerViewAdapter.preload(data.value, callback).also { ticket.addReference(it) }
        }

        override fun visit(data: Div.Video, resolver: ExpressionResolver, path: DivStatePath) {
            defaultVisit(data, resolver, path)
            if (preloadFilter.shouldPreloadContent(data, resolver)) {
                val sources = mutableListOf<Uri>()
                data.value.videoSources?.forEach {
                    sources.add(it.url.evaluate(resolver))
                }
                val preloading = downloadCallback.registerPreloading("video")
                videoPreloader.preloadVideo(sources, callback = { results ->
                    preloading.onCompleted(CompositeResult(results))
                }).also { ticket.addReference(it) }
            }
        }
    }
}
