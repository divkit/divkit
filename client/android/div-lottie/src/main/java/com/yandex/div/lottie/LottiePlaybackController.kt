package com.yandex.div.lottie

import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieDrawable
import com.yandex.div.core.Disposable
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.animations.DivAnimationsEnabledController
import com.yandex.div.internal.extensions.lottie.LottieExtensionParams
import com.yandex.div.internal.extensions.lottie.LottieRepeat
import com.yandex.div.internal.extensions.lottie.LottieRepeatMode
import com.yandex.div.json.expressions.ExpressionResolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/** Coordinates playback state with one bound Lottie view. */
internal class LottiePlaybackController(
    private val viewController: LottieController,
    private val divView: Div2View,
    private val params: LottieExtensionParams,
    private val resolver: ExpressionResolver,
    private val animationsEnabledController: DivAnimationsEnabledController,
) : Disposable {

    private var playbackBinding: PlaybackBinding? = null

    fun onBind(
        stateController: LottiePlaybackStateController,
        path: DivStatePath,
    ) {
        onUnbind()
        playbackBinding = PlaybackBinding(stateController, path)
    }

    fun onUnbind() {
        val binding = playbackBinding ?: return
        playbackBinding = null
        binding.close()
    }

    fun onCompositionReady(composition: LottieComposition) {
        playbackBinding?.onCompositionReady(composition)
    }

    override fun close() {
        onUnbind()
    }

    private fun applyConfiguredPlayback() {
        if (params.isPlaying.evaluate(resolver) && animationsEnabledController.isEnabled()) {
            viewController.playAnimation()
        } else {
            pauseAtEnd()
        }
    }

    private fun playOrPauseAction() {
        if (!animationsEnabledController.isEnabled()) {
            if (viewController.isAnimating()) {
                viewController.pauseAnimation()
            }
            return
        }

        if (!viewController.isAnimating()) {
            viewController.resumeAnimation()
        }
    }

    private fun playOrPauseConfigured() {
        val shouldPlay = params.isPlaying.evaluate(resolver) && animationsEnabledController.isEnabled()
        if (!shouldPlay) {
            if (viewController.isAnimating()) {
                viewController.pauseAnimation()
            }
            return
        }

        val canResume =
            viewController.getProgress() < 1f || viewController.getRepeatCount() == LottieDrawable.INFINITE
        if (!viewController.isAnimating() && canResume) {
            viewController.resumeAnimation()
        }
    }

    private fun pauseAtEnd() {
        val progress = if (viewController.getRepeatMode() == LottieDrawable.REVERSE) {
            0f
        } else {
            1f
        }
        viewController.pauseAnimationAt(progress)
    }

    private inner class PlaybackBinding(
        private val stateController: LottiePlaybackStateController,
        private val path: DivStatePath,
    ) : Disposable {

        private var composition: LottieComposition? = null
        private var repeatIndex = 0
        private var appliedPlaybackState: PlaybackState = PlaybackState.Idle

        private val playbackSubscription: Job = CoroutineScope(Dispatchers.Main).launch {
            stateController.states
                .map { states -> states[path] ?: PlaybackState.Idle }
                .distinctUntilChanged()
                .collect(::applyPlaybackState)
        }
        private val isPlayingSubscription: Disposable = params.isPlaying.observe(resolver) {
            updatePlaybackAvailability()
        }
        private val animationsEnabledSubscription: Disposable = animationsEnabledController.observe(divView) {
            updatePlaybackAvailability()
        }

        fun onCompositionReady(composition: LottieComposition) {
            this.composition = composition
            viewController.setComposition(composition)
            restartRepeats()
            viewController.setSafeMode(params.safeMode)

            viewController.clearPlaybackEndListeners()
            viewController.addEndListener(::onAnimationFinished)

            val state = currentPlaybackState()
            if (state is PlaybackState.Completed) {
                // A newly bound view has no terminal frame to retain.
                pauseAtEnd()
            }
            applyPlaybackState(state)
        }

        override fun close() {
            playbackSubscription.cancel()
            isPlayingSubscription.close()
            animationsEnabledSubscription.close()
            viewController.clearPlaybackEndListeners()
        }

        private fun currentPlaybackState(): PlaybackState =
            stateController.states.value[path] ?: PlaybackState.Idle

        private fun applyPlaybackState(state: PlaybackState) {
            if (composition == null) {
                return
            }

            appliedPlaybackState = state
            when (state) {
                is PlaybackState.Started -> restartActionPlayback()
                is PlaybackState.Stopped -> resetActionPlayback()
                is PlaybackState.Completed -> viewController.pauseAnimation()
                PlaybackState.Idle -> applyConfiguredPlayback()
            }
        }

        private fun restartActionPlayback() {
            resetActionPlayback()
            if (animationsEnabledController.isEnabled()) {
                viewController.playAnimation()
            }
        }

        private fun resetActionPlayback() {
            viewController.clearPlaybackEndListeners()
            viewController.pauseAnimation()
            // REVERSE can leave Lottie's animator running backwards after a repeat.
            viewController.setRepeatMode(LottieDrawable.RESTART)
            restartRepeats()
            viewController.pauseAnimationAt(0f)
            viewController.addEndListener(::onAnimationFinished)
        }

        private fun updatePlaybackAvailability() {
            if (composition == null) {
                return
            }

            when (currentPlaybackState()) {
                is PlaybackState.Started -> playOrPauseAction()
                is PlaybackState.Stopped -> Unit
                is PlaybackState.Completed -> Unit
                PlaybackState.Idle -> playOrPauseConfigured()
            }
        }

        private fun onAnimationFinished() {
            val nextRepeat = params.repeats.getOrNull(repeatIndex + 1)
            if (nextRepeat != null) {
                repeatIndex++
                applyRepeat(nextRepeat)
                viewController.resumeAnimation()
                return
            }

            when (val state = appliedPlaybackState) {
                is PlaybackState.Started -> stateController.onPlaybackFinished(path, state.sequence)
                PlaybackState.Idle -> stateController.onAutoplayed(path)
                is PlaybackState.Stopped -> Unit
                is PlaybackState.Completed -> Unit
            }
        }

        private fun restartRepeats() {
            repeatIndex = 0
            val firstRepeat = params.repeats.firstOrNull()
            if (firstRepeat == null) {
                viewController.setRepeatCount(params.repeatCount)
                viewController.setRepeatMode(params.repeatMode.toLottieDrawableRepeatMode())
            } else {
                applyRepeat(firstRepeat)
            }
        }

        private fun applyRepeat(repeat: LottieRepeat) {
            viewController.setRepeatCount(repeat.count)
            viewController.setRepeatMode(repeat.mode.toLottieDrawableRepeatMode())

            val minFrame = repeat.minFrame ?: composition?.startFrame?.toInt()
            val maxFrame = repeat.maxFrame ?: composition?.endFrame?.toInt()
            if (minFrame != null && maxFrame != null) {
                viewController.setFrameRange(minFrame, maxFrame)
            }
        }
    }
}

@LottieDrawable.RepeatMode
private fun LottieRepeatMode.toLottieDrawableRepeatMode(): Int {
    return when (this) {
        LottieRepeatMode.RESTART -> LottieDrawable.RESTART
        LottieRepeatMode.REVERSE -> LottieDrawable.REVERSE
    }
}
