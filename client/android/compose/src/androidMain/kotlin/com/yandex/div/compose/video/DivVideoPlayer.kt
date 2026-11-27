package com.yandex.div.compose.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.StateFlow

/**
 * Compose-native player abstraction for `div-video`.
 *
 * Inputs (sources, playback options, scale, ...) are pushed into the player by passing
 * a [DivVideoPlayerConfig] to [Content] on every composition. Outputs — playback state
 * and errors — are exposed as [StateFlow] so they can be consumed both from coroutines
 * and from `@Composable` functions via `collectAsState`.
 *
 * Implementations are free to render either via [androidx.compose.ui.viewinterop.AndroidView]
 * interop (for existing `View`-based players such as media3) or natively in Compose.
 */
public interface DivVideoPlayer {

    /**
     * `true` once the player is ready to render frames.
     */
    public val isReady: StateFlow<Boolean>

    /**
     * `true` while the player is actively playing.
     */
    public val isPlaying: StateFlow<Boolean>

    /**
     * `true` while the player is buffering.
     */
    public val isBuffering: StateFlow<Boolean>

    /**
     * `true` when playback has reached the end.
     */
    public val isEnded: StateFlow<Boolean>

    /**
     * Last fatal error, or `null` if no error has occurred.
     */
    public val error: StateFlow<Throwable?>

    /**
     * Current playback position in milliseconds; updated by the player as playback
     * progresses. Use [seek] to change the position.
     */
    public val currentTimeMs: StateFlow<Long>

    public fun play()

    public fun pause()

    /**
     * Jumps playback to the given position. Subsequent updates to [currentTimeMs] reflect
     * the new position.
     */
    public fun seek(timeMs: Long)

    public fun release()

    /**
     * Renders the visual representation of the player and applies [config].
     *
     * Changes to [config] between subsequent calls drive the underlying playback engine.
     */
    @Composable
    public fun Content(config: DivVideoPlayerConfig, modifier: Modifier)
}
