package com.yandex.div.video

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.SurfaceView
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerView
import com.yandex.div2.DivVideoScale

abstract class ExoDivPlayerView(context: Context) : DivPlayerView(context) {
    protected abstract val playerView: StyledPlayerView

    private var attachedPlayer: DivPlayer? = null

    override fun attach(player: DivPlayer) {
        detach()
        playerView.player = (player as ExoDivPlayer).player
        attachedPlayer = player
        player.setTargetResolution(this.width, this.height)
    }

    override fun detach() {
        playerView.player?.release()
        playerView.player = null
        attachedPlayer = null
    }

    override fun getAttachedPlayer(): DivPlayer? = attachedPlayer

    override fun setScale(videoScale: DivVideoScale) {
        checkScale(videoScale)
        playerView.resizeMode = when (videoScale) {
            DivVideoScale.NO_SCALE -> AspectRatioFrameLayout.RESIZE_MODE_FIT // there is no "no scale" type
            DivVideoScale.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
            DivVideoScale.FILL -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        }
    }

    protected inline fun setupPlayerView(viewCreator: () -> StyledPlayerView): StyledPlayerView {
        return viewCreator().apply {
            useController = false
            setShutterBackgroundColor(Color.TRANSPARENT)
            (videoSurfaceView as? SurfaceView)?.apply {
                setZOrderOnTop(false)
                setBackgroundColor(Color.TRANSPARENT)
                holder.setFormat(PixelFormat.TRANSPARENT)
            }
        }.also {
            addView(it)
        }
    }

    /**
     * Checks is new scale compatible with view type. Should log warning if it's not.
     */
    protected abstract fun checkScale(videoScale: DivVideoScale)

    companion object {
        @JvmStatic
        protected val TAG = "ExoPlayerView"
    }
}
