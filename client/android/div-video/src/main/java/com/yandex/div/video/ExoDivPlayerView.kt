package com.yandex.div.video

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.SurfaceView
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerView

class ExoDivPlayerView(context: Context) : DivPlayerView(context) {
    private val playerView = PlayerView(context).apply {
        useController = false
        setShutterBackgroundColor(Color.TRANSPARENT)
        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        (videoSurfaceView as? SurfaceView)?.apply {
            setZOrderOnTop(false)
            setBackgroundColor(Color.TRANSPARENT)
            holder.setFormat(PixelFormat.TRANSPARENT)
        }
    }.also {
        this.addView(it)
    }

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
}
