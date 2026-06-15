package com.yandex.div.compose.views.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.dagger.LocalComponent
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
    LaunchedEffect(player, data) {
        val actionPerformer = VideoActionPerformer(
            player,
            data,
            localComponent.actionHandler,
            localComponent.actionHandlingContext,
            localComponent.reporter
        )
        actionPerformer.observe()
    }
}

private class VideoActionPerformer(
    private val player: DivVideoPlayer,
    private val data: DivVideo,
    private val actionHandler: DivActionHandler,
    private val actionHandlingContext: DivActionHandlingContext,
    private val reporter: DivReporter,
) {

    suspend fun observe() = coroutineScope {
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
                reporter.reportError(
                    "Playback in div with id '${data.id}' encountered an error: ${err.message}"
                )
                performActions(data.fatalActions)
            }
        }
    }

    private fun performActions(actions: List<DivAction>?) {
        if (actions.isNullOrEmpty()) return
        actionHandler.handle(actionHandlingContext, actions, source = DivActionSource.VIDEO)
    }
}
