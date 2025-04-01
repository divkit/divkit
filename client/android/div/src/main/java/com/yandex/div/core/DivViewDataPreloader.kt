package com.yandex.div.core

import android.net.Uri
import com.yandex.div.core.DivPreloader.Callback
import com.yandex.div.core.DivPreloader.Companion.NO_CALLBACK
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.player.DivPlayerPreloader
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivImagePreloader
import com.yandex.div.internal.core.DivTreeVisitor
import com.yandex.div2.Div

@PublicApi
@Mockable
internal class DivViewDataPreloader internal constructor(
    private val imagePreloader: DivImagePreloader?,
    private val customContainerViewAdapter: DivCustomContainerViewAdapter,
    private val extensionController: DivExtensionController,
    private val videoPreloader: DivPlayerPreloader,
    private val preloadFilter: DivPreloader.PreloadFilter
) {

    fun preload(
        div: Div,
        context: BindingContext,
        path: DivStatePath,
        callback: Callback = NO_CALLBACK
    ): DivPreloader.Ticket {
        val downloadCallback = DivPreloader.DownloadCallback(callback)
        val ticket = PreloadVisitor(downloadCallback, callback, preloadFilter).preload(div, context, path)
        downloadCallback.onFullPreloadStarted()
        return ticket
    }

    private inner class PreloadVisitor(
        private val downloadCallback: DivPreloader.DownloadCallback,
        private val callback: Callback,
        private val preloadFilter: DivPreloader.PreloadFilter
    ) : DivTreeVisitor<Unit>() {
        private val ticket = DivPreloader.TicketImpl()

        fun preload(div: Div, context: BindingContext, path: DivStatePath): DivPreloader.Ticket {
            visit(div, context, path)
            return ticket
        }

        override fun defaultVisit(data: Div, context: BindingContext, path: DivStatePath) {
            imagePreloader?.preloadImage(data, context.expressionResolver, preloadFilter, downloadCallback)
                ?.forEach { ticket.addImageReference(it) }
            extensionController.preprocessExtensions(data.value(), context.expressionResolver)
        }

        override fun visit(data: Div.Custom, context: BindingContext, path: DivStatePath) {
            super.visit(data, context, path)
            customContainerViewAdapter.preload(data.value, callback).also { ticket.addReference(it) }
        }

        override fun visit(data: Div.Video, context: BindingContext, path: DivStatePath) {
            defaultVisit(data, context, path)
            if (preloadFilter.shouldPreloadContent(data, context.expressionResolver)) {
                val sources = mutableListOf<Uri>()
                data.value.videoSources.forEach {
                    sources.add(it.url.evaluate(context.expressionResolver))
                }
                videoPreloader.preloadVideo(sources).also { ticket.addReference(it) }
            }
        }
    }
}
