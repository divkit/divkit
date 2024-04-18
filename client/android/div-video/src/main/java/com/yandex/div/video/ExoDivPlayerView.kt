package com.yandex.div.video

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.util.Xml
import android.view.SurfaceView
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerView
import com.yandex.div.internal.KLog
import com.yandex.div2.DivVideoScale

open class ExoDivPlayerView(context: Context) : DivPlayerView(context) {
    private var _playerView = setupPlayerView { StyledPlayerView(context, getAttributeSet()) }
    protected open val playerView
        get() = _playerView

    private var attachedPlayer: DivPlayer? = null
    private var didFallbackToSurfaceView: Boolean = false

    override fun attach(player: DivPlayer) {
        detach()
        _playerView.player = (player as ExoDivPlayer).player
        attachedPlayer = player
        player.setTargetResolution(this.width, this.height)
    }

    override fun detach() {
        _playerView.player?.release()
        _playerView.player = null
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

        _playerView.resizeMode = when (videoScale) {
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

            val player = getAttachedPlayer() as? ExoDivPlayer ?: return
            _playerView.player = null
            _playerView = setupPlayerView { StyledPlayerView(context) }
            _playerView.player = player.player
        }

        super.onAttachedToWindow()
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
                if (parser.name == TYPE_STYLED_PLAYER_VIEW) {
                    attrs = Xml.asAttributeSet(parser)
                    break
                }
            } while (state != org.xmlpull.v1.XmlPullParser.END_DOCUMENT)

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

    @Deprecated("Will be removed in future releases")
    protected open fun checkScale(videoScale: DivVideoScale) = Unit

    @Deprecated("Will be removed in future releases")
    override fun isCompatibleWithNewParams(scale: DivVideoScale): Boolean = true

    companion object {
        private const val TYPE_STYLED_PLAYER_VIEW = "com.google.android.exoplayer2.ui.StyledPlayerView"
        @JvmStatic
        protected val TAG = "ExoPlayerView"

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
