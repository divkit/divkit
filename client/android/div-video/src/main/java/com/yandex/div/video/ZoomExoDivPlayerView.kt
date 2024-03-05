package com.yandex.div.video

import android.content.Context
import android.util.AttributeSet
import android.util.Xml
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.yandex.div.internal.KLog
import com.yandex.div2.DivVideoScale

/**
 * [ExoDivPlayerView] that uses [TextureView][android.view.TextureView] as a layout. Has energy efficiency
 * problems, but is a workaround for ExoPlayer `ZOOM`-scale visual bug.
 */
class ZoomExoDivPlayerView(context: Context): ExoDivPlayerView(context) {
    override val playerView = setupPlayerView {
        val parser = resources.getLayout(R.layout.zoom_player_view)

        var attrs: AttributeSet? = null
        var state: Int
        do {
            state = parser.next()
            if (parser.name == TYPE_STYLED_PLAYER_VIEW) {
                attrs = Xml.asAttributeSet(parser)
                break
            }
        } while (state != org.xmlpull.v1.XmlPullParser.END_DOCUMENT)

        attrs ?: KLog.e(TAG) {
            """Can't create `ZoomExoDivPlayerView`. Can't find `surface_type` attribute. 
                |Fallback to SurfaceView. Visual errors may occur""".trimMargin()
        }

        StyledPlayerView(context, attrs)
    }

    override fun isCompatibleWithNewParams(scale: DivVideoScale) = scale == DivVideoScale.FILL

    override fun checkScale(videoScale: DivVideoScale) = Unit

    companion object {
        private const val TYPE_STYLED_PLAYER_VIEW = "com.google.android.exoplayer2.ui.StyledPlayerView"
    }
}
