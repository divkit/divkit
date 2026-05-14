package com.yandex.div.compose.views.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.yandex.div.compose.utils.variables.mutableStateFromIntegerVariable
import kotlinx.coroutines.delay

@Composable
internal fun rememberVideoState(variableName: String?): DivVideoState {
    val variable = variableName?.let { mutableStateFromIntegerVariable(it) }
    return remember(variable) { DivVideoState(variable) }
}

internal class DivVideoState(
    private val elapsedTimeVariable: MutableState<Long>?
) {
    private val _isReady = mutableStateOf(false)
    private val _isPlaying = mutableStateOf(false)
    private var lastSyncedPositionMs: Long = 0L

    var isReady: Boolean
        get() = _isReady.value
        set(value) {
            _isReady.value = value
        }

    var isPlaying: Boolean
        get() = _isPlaying.value
        set(value) {
            _isPlaying.value = value
        }

    val variableValue: Long?
        get() = elapsedTimeVariable?.value

    fun setPosition(positionMs: Long): Boolean {
        if (lastSyncedPositionMs == positionMs)
            return false
        lastSyncedPositionMs = positionMs
        elapsedTimeVariable?.value = positionMs
        return true
    }
}

@Composable
internal fun DivVideoState.BindToPlayer(player: ExoPlayer) {
    DisposableEffect(this, player) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    isReady = true
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                isReady = false
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                setPosition(newPosition.positionMs)
            }
        }

        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    SyncWithPlayer(
        getCurrentPosition = { player.currentPosition },
        seekTo = { player.seekTo(it) },
    )
}

@Composable
internal fun DivVideoState.SyncWithPlayer(
    getCurrentPosition: () -> Long,
    seekTo: (Long) -> Unit,
    intervalMs: Long = POSITION_SYNC_INTERVAL_MS,
) {
    LaunchedEffect(this) {
        snapshotFlow { variableValue }
            .collect { value ->
                if (value != null && setPosition(value)) {
                    seekTo(value)
                }
            }
    }

    val playing = isPlaying
    LaunchedEffect(this, playing) {
        while (playing) {
            setPosition(getCurrentPosition())
            delay(intervalMs)
        }
    }
}

private const val POSITION_SYNC_INTERVAL_MS = 1000L
