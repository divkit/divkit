package com.yandex.div.video

import android.content.Context
import android.view.SurfaceView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.yandex.div.internal.KLog
import com.yandex.div2.DivVideoScale

/**
 * [ExoDivPlayerView] that uses [SurfaceView] as a layout. Better for energy efficiency, but has a visual bug
 * when displaying multiple views side by side, with scale type [FILL][DivVideoScale.FILL].
 */
class StandardExoDivPlayerView(context: Context) : ExoDivPlayerView(context) {
    override val playerView = setupPlayerView { StyledPlayerView(context) }

    override fun isCompatibleWithNewParams(scale: DivVideoScale) = scale != DivVideoScale.FILL

    override fun checkScale(videoScale: DivVideoScale) {
        if (videoScale == DivVideoScale.FILL) {
            KLog.w(TAG) {
                """Setting scale to `fill` after DivVideo initialization with 
                    |different scale type may cause ExoPlayer visual bugs""".trimMargin()
            }
        }
    }
}
