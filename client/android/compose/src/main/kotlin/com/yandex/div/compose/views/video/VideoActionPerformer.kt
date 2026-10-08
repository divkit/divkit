package com.yandex.div.compose.views.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.internal.VideoEventSource
import com.yandex.div.compose.internal.VideoEventSource.Event
import com.yandex.div.compose.video.DivVideoPlayer
import com.yandex.div2.DivAction
import com.yandex.div2.DivVideo
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@Composable
internal fun ObserveVideoActions(
    player: DivVideoPlayer,
    data: DivVideo
) {
    val localComponent = LocalComponent.current
    val actionPerformer = remember(player, data) {
        VideoActionPerformer(
            player,
            data,
            localComponent.actionHandler,
            localComponent.actionHandlingContext,
            localComponent.reporter
        )
    }
    if (player is VideoEventSource) {
        val currentActionPerformer by rememberUpdatedState(actionPerformer)
        LaunchedEffect(player) {
            player.videoEvents.collect { currentActionPerformer.performEvent(it) }
        }
    } else {
        LaunchedEffect(player, data) {
            actionPerformer.observeState()
        }
    }
}

private class VideoActionPerformer(
    private val player: DivVideoPlayer,
    private val data: DivVideo,
    private val actionHandler: DivActionHandler,
    private val actionHandlingContext: DivActionHandlingContext,
    private val reporter: DivReporter,
) {

    fun performEvent(event: Event) {
        when (event) {
            Event.Play -> performActions(data.resumeActions)
            Event.Pause -> performActions(data.pauseActions)
            Event.Buffering -> performActions(data.bufferingActions)
            Event.End -> performActions(data.endActions)
            is Event.Fatal -> performFatalActions(event.error)
        }
    }

    suspend fun observeState() = coroutineScope {
        launch { observePlayingChanges() }
        launch { observeBufferingChanges() }
        launch { observeEndedChanges() }
        launch { observeErrors() }
    }

    private suspend fun observePlayingChanges() {
        player.isPlaying.drop(1).collect { playing ->
            if (playing) {
                performActions(data.resumeActions)
            } else if (!player.isEnded.value) {
                performActions(data.pauseActions)
            }
        }
    }

    private suspend fun observeBufferingChanges() {
        player.isBuffering.drop(1).collect { buffering ->
            if (buffering) performActions(data.bufferingActions)
        }
    }

    private suspend fun observeEndedChanges() {
        player.isEnded.drop(1).collect { ended ->
            if (ended) performActions(data.endActions)
        }
    }

    private suspend fun observeErrors() {
        player.error.collect { err ->
            if (err != null) {
                performFatalActions(err)
            }
        }
    }

    private fun performFatalActions(error: Throwable?) {
        if (error != null) {
            reporter.reportError(
                "Playback in div with id '${data.id}' encountered an error: ${error.message}"
            )
        }
        performActions(data.fatalActions)
    }

    private fun performActions(actions: List<DivAction>?) {
        if (actions.isNullOrEmpty()) return
        actionHandler.handle(actionHandlingContext, actions, source = DivActionSource.VIDEO)
    }
}
