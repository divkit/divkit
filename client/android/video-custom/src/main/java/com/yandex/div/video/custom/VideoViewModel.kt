package com.yandex.div.video.custom

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

internal interface VideoViewModel {
    // TODO: Remove ExoPlayer instance from view model, see https://st.yandex-team.ru/DIVKIT-159
    val player: ExoPlayer
    val stubImage: Flow<Bitmap?>
    val stubVisible: Flow<Boolean>

    fun onReadyToPlay(ready: Boolean)

    fun onPlaybackError(exception: Exception)

    fun onVideoStartedShowing()

    fun onVideoRepeat()

    fun freezePlayback()

    fun unfreezePlayback()
}

internal class MutableVideoViewModel(
    private val videoConfig: VideoConfig,
    private val cache: VideoCache,
    private val actionNotifier: VideoCustomActionNotifier,
    context: Context,
) : VideoViewModel {
    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    override val player: ExoPlayer by lazy {
        SimpleExoPlayer.Builder(context).build()
    }

    private var isVideoConfigured = false

    private var isVideoShown = false

    private var isShowingStarted = false

    private var savedIsPlaying: Boolean? = null

    val videoId: String = videoConfig.id
        ?: throw IllegalArgumentException("VideoViewModel should receive VideoConfig with id specified")

    private val stubImageInternal = MutableStateFlow<Bitmap?>(null)

    init {
        viewModelScope.launch {
            val stubImage = videoConfig.stubImageUrl?.let { cache.getStubImage(it) }
            stubImageInternal.value = stubImage
        }
    }

    override val stubImage: Flow<Bitmap?>
        get() = stubImageInternal.asStateFlow()

    private val stubVisibleInternal = MutableStateFlow(false)

    override val stubVisible
        get() = stubVisibleInternal.debounce { stubVisible ->
            // Don't show stub image immediately, wait 500ms so that stub don't blink
            if (stubVisible) 500 else 0
        }

    override fun onReadyToPlay(ready: Boolean) {
        stubVisibleInternal.value = !ready
    }

    override fun onPlaybackError(exception: Exception) {
        val id = videoConfig.id ?: return
        actionNotifier.notifyPlaybackError(id, exception)
    }

    override fun onVideoStartedShowing() {
        val id = videoConfig.id ?: return
        if (!isShowingStarted) {
            actionNotifier.notifyVideoStartedShowing(id)
            isShowingStarted = true
        }
    }

    override fun onVideoRepeat() {
        val id = videoConfig.id ?: return
        if (!isVideoShown) {
            actionNotifier.notifyPlaybackFinished(id, player.duration)
            isVideoShown = true
        }
    }

    fun configureVideo() {
        if (isVideoConfigured) return
        player.playWhenReady = videoConfig.startMode == VideoStartMode.WHEN_READY
        player.repeatMode =
            if (videoConfig.repeat) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF

        val mediaSource = cache.createMediaSource(MediaItem.fromUri(Uri.parse(videoConfig.url)))
        player.setMediaSource(mediaSource)
        player.prepare()
        isVideoConfigured = true
    }

    override fun freezePlayback() {
        savedIsPlaying = player.isPlaying
        player.pause()
    }

    override fun unfreezePlayback() {
        if (savedIsPlaying == true) {
            player.play()
        }
        savedIsPlaying = null
    }

    fun release() {
        videoConfig.id?.let { id ->
            if (!isVideoShown) {
                actionNotifier.notifyPlaybackFinished(id, player.currentPosition)
            }
        }
        player.release()
        viewModelScope.cancel()
    }

    fun play() {
        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun reset() {
        player.seekTo(0)
    }
}
