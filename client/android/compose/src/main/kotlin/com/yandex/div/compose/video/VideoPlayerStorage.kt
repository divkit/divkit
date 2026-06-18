package com.yandex.div.compose.video

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.yandex.div.compose.dagger.DivViewScope
import javax.inject.Inject

/**
 * Maps `div-video` ids to their [DivVideoPlayer] instances for a single
 * [com.yandex.div.compose.DivView], so that `video` actions (start / pause) can
 * control a player by its `id`.
 *
 * Players register themselves while they are in composition and unregister on
 * disposal; only the currently composed player for a given id is kept.
 */
@DivViewScope
internal class VideoPlayerStorage @Inject constructor() {

    private val players = mutableMapOf<String, DivVideoPlayer>()

    fun register(id: String, player: DivVideoPlayer) {
        players[id] = player
    }

    fun unregister(id: String, player: DivVideoPlayer) {
        if (players[id] === player) {
            players.remove(id)
        }
    }

    fun get(id: String): DivVideoPlayer? {
        return players[id]
    }
}

@SuppressLint("ComposableNaming")
@Composable
internal fun VideoPlayerStorage.registerPlayer(id: String, player: DivVideoPlayer) {
    DisposableEffect(this, id, player) {
        register(id, player)
        onDispose { unregister(id, player) }
    }
}
