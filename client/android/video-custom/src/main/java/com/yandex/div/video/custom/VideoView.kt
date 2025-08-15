package com.yandex.div.video.custom

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.SurfaceView
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.isVisible
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.DISCONTINUITY_REASON_AUTO_TRANSITION
import com.google.android.exoplayer2.Player.STATE_ENDED
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.SeekParameters
import com.google.android.exoplayer2.ui.PlayerView
import com.yandex.div.internal.KAssert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlin.math.max

internal class VideoView(
    context: Context,
    zOrderMode: ZOrderMode = ZOrderMode.ON_TOP,
    private val videoCustomViewController: VideoCustomViewController,
) : FrameLayout(context) {
    private val coroutineScope: CoroutineScope = MainScope()
    private var videoConfig: VideoConfig? = null
    private var stubViewObservationJob: Job? = null

    var viewModel: VideoViewModel? = null
        private set

    private val playerView = PlayerView(context).apply {
        useController = false
        setShutterBackgroundColor(Color.TRANSPARENT)
        (videoSurfaceView as? SurfaceView)?.apply {
            when (zOrderMode) {
                ZOrderMode.ON_TOP -> setZOrderOnTop(true)
                ZOrderMode.MEDIA_OVERLAY -> setZOrderMediaOverlay(true)
            }
            setBackgroundColor(Color.TRANSPARENT)
            holder.setFormat(PixelFormat.TRANSPARENT)
        }
    }

    private val stubImageView: ImageView = ImageView(context).apply {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        scaleType = ImageView.ScaleType.FIT_CENTER
        isVisible = false
        setBackgroundColor(Color.TRANSPARENT)
    }

    private val playerActivityCallback = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityStarted(activity: Activity) = Unit
        override fun onActivityDestroyed(activity: Activity) =
            (context.applicationContext as Application).unregisterActivityLifecycleCallbacks(this)
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
        override fun onActivityStopped(activity: Activity) = Unit
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit

        override fun onActivityPaused(activity: Activity) {
            viewModel?.freezePlayback()
        }

        override fun onActivityResumed(activity: Activity) {
            //This code used to avoid black player after return from background
            val player = viewModel?.player ?: return
            val state = player.playbackState

            viewModel?.unfreezePlayback()
            if (state == STATE_ENDED) {
                // play video, because we moved not on the last frame.
                player.setSeekParameters(SeekParameters.DEFAULT)
                player.seekTo(max(player.currentPosition - 1, 0L))
                player.setSeekParameters(SeekParameters.EXACT)
                viewModel?.player?.play()
            }
        }
    }

    init {
        addView(stubImageView)
    }

    private val playerListener = object : Player.Listener {
        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            if (reason == DISCONTINUITY_REASON_AUTO_TRANSITION) {
                // Video has ended and now is repeating
                assertBoundToViewModel()
                viewModel?.onVideoRepeat()
            }
        }

        override fun onRenderedFirstFrame() {
            assertBoundToViewModel()
            viewModel?.onVideoStartedShowing()
        }

        override fun onPlayerError(error: PlaybackException) {
            assertBoundToViewModel()
            viewModel?.onPlaybackError(error)
        }

        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)
            when (state)  {
                STATE_READY -> if (playerView.parent == null) {
                    stubImageView.isVisible = false
                    addView(playerView)
                }
                else -> return
            }

        }
    }

    fun bindToViewModel(model: VideoViewModel) {
        viewModel?.let { stopObservingViewModel(it) }
        viewModel = model
        videoConfig = (model as MutableVideoViewModel).videoConfig

        if (isAttachedToWindow) {
            resumeObservingViewModel(model)
        }
    }

    private fun assertBoundToViewModel() {
        if (viewModel == null) {
            KAssert.fail { "VideoView is required to be bound to view model, but it isn't bound" }
        }
    }

    private fun observeStubViewState(model: VideoViewModel): Job =
        coroutineScope.launch(Dispatchers.Main.immediate) {
            model.stubImageIfVisible.collect { image ->
                stubImageView.setImageBitmap(image)
                stubImageView.isVisible = image != null
            }
        }

    private fun resumeObservingViewModel(model: VideoViewModel) {
        playerView.player = model.player
        model.player.addListener(playerListener)
        model.unfreezePlayback()
        stubViewObservationJob?.cancel()
        stubViewObservationJob = observeStubViewState(model)
    }

    private fun stopObservingViewModel(model: VideoViewModel) {
        model.player.removeListener(playerListener)
        playerView.player = null
        model.onDetached()
        coroutineScope.coroutineContext.cancelChildren()
        stubViewObservationJob = null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (viewModel != null) {
            viewModel?.let { resumeObservingViewModel(it) }
        } else {
            videoConfig?.let { videoCustomViewController.bind(this, it) }
        }
        (context.applicationContext as Application)
            .registerActivityLifecycleCallbacks(playerActivityCallback)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewModel?.let { stopObservingViewModel(it) }
        viewModel = null
        stubImageView.isVisible = true
        (context.applicationContext as Application)
            .unregisterActivityLifecycleCallbacks(playerActivityCallback)
    }

    fun release() {
        viewModel?.let { stopObservingViewModel(it) }
        coroutineScope.cancel()
        playerView.player = null
        viewModel = null
        (context.applicationContext as Application)
            .unregisterActivityLifecycleCallbacks(playerActivityCallback)
    }
}
