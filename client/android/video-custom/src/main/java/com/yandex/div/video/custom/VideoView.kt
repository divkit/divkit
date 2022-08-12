package com.yandex.div.video.custom

import android.content.Context
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import androidx.core.view.isVisible
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.DISCONTINUITY_REASON_AUTO_TRANSITION
import com.google.android.exoplayer2.ui.PlayerView
import com.yandex.div.core.util.KAssert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

internal class VideoView(
    context: Context
) : PlayerView(context) {
    private val coroutineScope: CoroutineScope = MainScope()
    private var stubViewObservationJob: Job? = null

    var viewModel: VideoViewModel? = null
        private set

    private val stubImageView: ImageView = ImageView(context).apply {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        scaleType = ImageView.ScaleType.FIT_CENTER
        isVisible = false
    }

    init {
        useController = false
        addView(stubImageView)
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            assertBoundToViewModel()
            viewModel?.onReadyToPlay(state == Player.STATE_READY)
        }

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

        override fun onPlayerError(error: ExoPlaybackException) {
            assertBoundToViewModel()
            viewModel?.onPlaybackError(error)
        }
    }

    fun bindToViewModel(model: VideoViewModel) {
        viewModel?.let { pauseObservingViewModel(it) }
        stubImageView.setImageBitmap(null)

        viewModel = model
        player = model.player

        if (isAttachedToWindow) {
            resumeObservingViewModel(model)
        }
    }

    private fun assertBoundToViewModel() {
        if (viewModel == null) {
            KAssert.fail { "VideoView is required to be bound to view model, but it isn't bound" }
        }
    }

    private fun observeStubViewState(model: VideoViewModel): Job = coroutineScope.launch {
        launch {
            model.stubImage.collect { image -> stubImageView.setImageBitmap(image) }
        }
        launch {
            model.stubVisible.collect { visible -> stubImageView.isVisible = visible }
        }
    }

    private fun resumeObservingViewModel(model: VideoViewModel) {
        model.player.addListener(playerListener)
        model.unfreezePlayback()
        stubViewObservationJob?.cancel()
        stubViewObservationJob = observeStubViewState(model)
    }

    private fun pauseObservingViewModel(model: VideoViewModel) {
        model.player.removeListener(playerListener)
        model.freezePlayback()
        coroutineScope.coroutineContext.cancelChildren()
        stubViewObservationJob = null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        assertBoundToViewModel()
        viewModel?.let { resumeObservingViewModel(it) }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        assertBoundToViewModel()
        viewModel?.let { pauseObservingViewModel(it) }
    }

    fun release() {
        viewModel?.let { pauseObservingViewModel(it) }
        coroutineScope.cancel()
        player = null
        viewModel = null
    }
}
