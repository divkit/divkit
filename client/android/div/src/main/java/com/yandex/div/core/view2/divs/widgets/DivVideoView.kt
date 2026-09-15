package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.annotation.MainThread
import com.yandex.div.R
import com.yandex.div.core.Disposable
import com.yandex.div.core.extension.DivExtensionView
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerView
import com.yandex.div.core.view2.Releasable
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.widget.FrameContainerLayout
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

internal class DivVideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.divImageStyle
) : FrameContainerLayout(context, attrs, defStyleAttr),
    DivHolderView<DivBlock.Video> by DivHolderViewMixin(),
    DivExtensionView,
    Releasable,
    MediaReleasable {

    private val videoSubscriptions = mutableListOf<Disposable>()
    private val videoBindingScope = MainScope()
    private var videoBindingJob: Job? = null
    private var playerPendingRelease: DivPlayer? = null
    private var isReleasingPlayer = false
    internal val videoBindingController = DivVideoBindingController()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(it) }
    }

    override fun release() {
        super.release()
        releaseMedia()
    }

    @MainThread
    override fun releaseMedia() {
        cancelPendingVideoBinding()
        videoBindingController.release()
        resetVideoBinding()
    }

    @MainThread
    fun launchVideoBinding(block: suspend () -> Unit) {
        cancelPendingVideoBinding()
        resetVideoBinding()
        // Resume in the idle callback before another callback can scroll or detach the view.
        val bindingJob = videoBindingScope.launch(
            context = Dispatchers.Main.immediate,
            start = CoroutineStart.LAZY,
        ) {
            block()
        }
        // Publish before synchronous callbacks can start a newer binding.
        videoBindingJob = bindingJob
        bindingJob.start()
    }

    @MainThread
    fun cancelPendingVideoBinding() {
        val bindingJob = videoBindingJob
        videoBindingJob = null
        bindingJob?.cancel()
    }

    private fun resetVideoBinding() {
        closeVideoSubscriptions()
        releaseCurrentPlayer()
    }

    private fun releaseCurrentPlayer() {
        if (isReleasingPlayer) {
            return
        }
        isReleasingPlayer = true
        try {
            val pendingPlayer = playerPendingRelease
            if (pendingPlayer != null) {
                pendingPlayer.release()
                playerPendingRelease = null
            }

            val player = getPlayerView()?.let { playerView ->
                val attachedPlayer = playerView.getAttachedPlayer()
                playerView.detach()
                attachedPlayer
            } ?: return
            if (player === pendingPlayer) {
                return
            }

            // Detach removes the view's reference; retain ours until release succeeds.
            playerPendingRelease = player
            player.release()
            playerPendingRelease = null
        } finally {
            isReleasingPlayer = false
        }
    }

    private fun closeVideoSubscriptions() {
        videoSubscriptions.forEach { it.close() }
        videoSubscriptions.clear()
    }

    fun getPlayerView(): DivPlayerView? {
        if (this.childCount > 2) {
            KAssert.fail { "Too many children in DivVideo" }
        }
        this.getChildAt(0)?.let {
            if (it !is DivPlayerView) {
                KAssert.fail { "Wrong view type inside DivVideo" }
                return null
            }
            return it
        }
        return null
    }

    override fun getBaseline() = measuredHeight - paddingBottom

    fun addVideoSubscription(subscription: Disposable?) {
        if (subscription != null && subscription !== Disposable.NULL) {
            videoSubscriptions += subscription
        }
    }
}
