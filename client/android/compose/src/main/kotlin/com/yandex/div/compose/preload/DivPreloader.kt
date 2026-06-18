package com.yandex.div.compose.preload

import com.yandex.div.compose.context.DivViewContext
import com.yandex.div.compose.context.DivViewContextFactory
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.video.DivVideoPreloader
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivData
import javax.inject.Inject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@DivContextScope
internal class DivPreloader @Inject constructor(
    private val imagePreloader: ImagePreloader,
    private val customPreloader: CustomResourcePreloader,
    private val extensionPreloader: ExtensionPreloader,
    private val videoPreloader: DivVideoPreloader,
    private val viewContextFactory: DivViewContextFactory,
) {
    suspend fun preload(data: DivData) {
        val viewContext = viewContextFactory.getOrCreate(data)
        data.states.forEach { state ->
            val stateComponent = viewContext.getLocalComponent(
                data = state.div.value(),
                parentComponent = viewContext.rootLocalComponent,
            )
            visitDiv(state.div, stateComponent, viewContext)
        }
    }

    private suspend fun visitDiv(
        div: Div,
        localComponent: DivLocalComponent,
        viewContext: DivViewContext,
    ): Unit = coroutineScope {
        val resolver = localComponent.expressionResolver

        launch { imagePreloader.preloadImages(div, resolver) }
        launch { extensionPreloader.preloadExtensions(div, resolver) }

        when (div) {
            is Div.Video -> launch { preloadVideo(div, resolver) }
            is Div.Custom -> launch { preloadCustom(div, resolver) }
            else -> {}
        }

        childrenOf(div).forEach { child ->
            val childComponent = viewContext.getLocalComponent(
                data = child.value(),
                parentComponent = localComponent,
            )
            visitDiv(child, childComponent, viewContext)
        }
    }

    private fun childrenOf(div: Div): List<Div> = when (div) {
        is Div.Container -> div.value.items.orEmpty()
        is Div.Grid -> div.value.items.orEmpty()
        is Div.Gallery -> div.value.items.orEmpty()
        is Div.Pager -> div.value.items.orEmpty()
        is Div.Tabs -> div.value.items.map { it.div }
        is Div.State -> div.value.states.mapNotNull { it.div }
        is Div.Custom -> div.value.items.orEmpty()
        else -> emptyList()
    }

    private suspend fun preloadVideo(div: Div.Video, resolver: ExpressionResolver) {
        if (!div.value.preloadRequired.evaluate(resolver)) return
        val sources = div.value.videoSources?.map { it.url.evaluate(resolver) } ?: return
        videoPreloader.preloadVideo(sources)
    }

    private suspend fun preloadCustom(div: Div.Custom, resolver: ExpressionResolver) {
        val environment = DivCustomEnvironment(
            data = div.value,
            expressionResolver = resolver,
            items = {},
            item = { _, _ -> },
        )
        customPreloader.preload(environment)
    }
}
