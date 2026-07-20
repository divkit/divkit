package com.yandex.div.compose.preload

import com.yandex.div.compose.PreloadMode
import com.yandex.div.compose.context.DivViewContext
import com.yandex.div.compose.context.DivViewContextFactory
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.state.DivStateStorage
import com.yandex.div.compose.state.resolveActiveState
import com.yandex.div.compose.video.DivVideoPreloader
import com.yandex.div.core.state.DivStatePath
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
    suspend fun preload(
        data: DivData,
        mode: PreloadMode = PreloadMode.REQUIRED_ONLY,
    ) {
        if (mode == PreloadMode.DISABLED) return

        val activeStateOnly = mode == PreloadMode.ACTIVE_STATE_ONLY
        val downloadAll = mode == PreloadMode.ACTIVE_STATE_ONLY || mode == PreloadMode.ALL

        val viewContext = viewContextFactory.getOrCreate(data)
        val states = if (activeStateOnly) {
            listOfNotNull(data.states.firstOrNull())
        } else {
            data.states
        }
        states.forEach { state ->
            visitDiv(
                div = state.div,
                localComponent = viewContext.getLocalComponent(
                    data = state.div.value(),
                    parentComponent = viewContext.rootLocalComponent,
                ),
                viewContext = viewContext,
                parentPath = DivStatePath.fromState(state.stateId),
                activeStateOnly = activeStateOnly,
                downloadAll = downloadAll,
            )
        }
    }

    private suspend fun visitDiv(
        div: Div,
        localComponent: DivLocalComponent,
        viewContext: DivViewContext,
        parentPath: DivStatePath,
        activeStateOnly: Boolean,
        downloadAll: Boolean,
    ): Unit = coroutineScope {
        val resolver = localComponent.expressionResolver

        launch { imagePreloader.preloadImages(div, resolver, downloadAll) }
        launch { extensionPreloader.preloadExtensions(div, resolver) }

        when (div) {
            is Div.Video -> launch { preloadVideo(div, resolver, downloadAll) }
            is Div.Custom -> launch { preloadCustom(div, resolver) }
            else -> {}
        }

        childrenOf(
            div = div,
            activeStateOnly = activeStateOnly,
            resolver = resolver,
            stateStorage = viewContext.stateStorage,
            parentPath = parentPath,
        ).forEach { (child, childPath) ->
            visitDiv(
                div = child,
                localComponent = viewContext.getLocalComponent(
                    data = child.value(),
                    parentComponent = localComponent,
                ),
                viewContext = viewContext,
                parentPath = childPath,
                activeStateOnly = activeStateOnly,
                downloadAll = downloadAll,
            )
        }
    }

    private fun childrenOf(
        div: Div,
        activeStateOnly: Boolean,
        resolver: ExpressionResolver,
        stateStorage: DivStateStorage,
        parentPath: DivStatePath,
    ): List<Pair<Div, DivStatePath>> {
        return when (div) {
            is Div.Container -> div.value.items.orEmpty().map { it to parentPath }
            is Div.Grid -> div.value.items.orEmpty().map { it to parentPath }
            is Div.Gallery -> div.value.items.orEmpty().map { it to parentPath }
            is Div.Pager -> div.value.items.orEmpty().map { it to parentPath }
            is Div.Tabs -> div.value.items.map { it.div to parentPath }
            is Div.Custom -> div.value.items.orEmpty().map { it to parentPath }
            is Div.State -> {
                if (activeStateOnly) {
                    val activeState = div.value.resolveActiveState(resolver, stateStorage, parentPath)
                        ?: return emptyList()
                    val childDiv = activeState.div ?: return emptyList()
                    val divId = div.value.id.orEmpty()
                    val childPath = parentPath.append(divId, activeState.stateId, activeState.stateId)
                    listOf(childDiv to childPath)
                } else {
                    div.value.states.mapNotNull { it.div }.map { it to parentPath }
                }
            }
            else -> emptyList()
        }
    }

    private suspend fun preloadVideo(
        div: Div.Video,
        resolver: ExpressionResolver,
        downloadAll: Boolean,
    ) {
        if (!downloadAll && !div.value.preloadRequired.evaluate(resolver)) return
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
