package com.yandex.div.video.m3

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.util.Xml
import android.view.SurfaceView
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerView
import com.yandex.div.internal.KLog
import com.yandex.div2.DivVideoScale
import org.xmlpull.v1.XmlPullParser

@OptIn(UnstableApi::class)
internal class ExoDivPlayerView(context: Context) : DivPlayerView(context) {
    private var playerView = setupPlayerView { PlayerView(context, getAttributeSet()) }

    private var attachedPlayer: DivPlayer? = null
    private var didFallbackToSurfaceView: Boolean = false

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
        if (didFallbackToSurfaceView && videoScale == DivVideoScale.FILL) {
            KLog.w(TAG) {
                """This DivVideo instance was initialized with SurfaceView. 
                    |SurfaceView with `fill` scale may cause ExoPlayer visual bugs""".trimMargin()
            }
        }

        playerView.resizeMode = when (videoScale) {
            DivVideoScale.NO_SCALE -> AspectRatioFrameLayout.RESIZE_MODE_FIT // there is no "no scale" type
            DivVideoScale.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
            DivVideoScale.FILL -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        }
    }

    override fun onAttachedToWindow() {
        // TextureView doesn't work without hardware acceleration. But it's really rare situation,
        // so we can swap views in runtime and remember that there is no point to create TextureViews
        if (!didFallbackToSurfaceView && !isHardwareAccelerated) {
            definitelyNoHardwareAcceleration = true
            didFallbackToSurfaceView = true

            KLog.w(TAG) {
                "Device doesn't support hardware acceleration. Fallback to SurfaceView"
            }

            val currentPlayer = (getAttachedPlayer() as? ExoDivPlayer)?.player ?: return

            playerView.player = null
            removeView(playerView)

            playerView = setupPlayerView { PlayerView(context) }.apply {
                player = currentPlayer
            }
        }

        super.onAttachedToWindow()
    }

    private inline fun setupPlayerView(viewCreator: () -> PlayerView): PlayerView {
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

    private fun getAttributeSet(): AttributeSet? {
        if (definitelyNoHardwareAcceleration) {
            didFallbackToSurfaceView = true
            return null
        }
        attributeSet?.let {
            return it
        }

        var attrs: AttributeSet? = null
        synchronized(lock) {
            val parser = resources.getLayout(R.layout.zoom_player_view)
            var state: Int
            do {
                state = parser.next()
                if (parser.name == TYPE_PLAYER_VIEW) {
                    attrs = Xml.asAttributeSet(parser)
                    break
                }
            } while (state != XmlPullParser.END_DOCUMENT)

            if (attrs == null) {
                KLog.e(TAG) {
                    """Can't create TextureView for DivPlayerView. Can't find `surface_type` attribute. 
                        |Fallback to SurfaceView. Visual bugs may occur""".trimMargin()
                }
                didFallbackToSurfaceView = true
            }

            attributeSet = attrs
        }

        return attrs
    }

    companion object {
        private const val TYPE_PLAYER_VIEW = "androidx.media3.ui.PlayerView"
        private const val TAG = "ExoPlayerView"

        private val lock = Any()
        private var definitelyNoHardwareAcceleration: Boolean = false
        private var attributeSet: AttributeSet? = null
            get() {
                if (field == null) {
                    synchronized(lock) {
                        return field
                    }
                } else {
                    return field
                }
            }
    }
}
