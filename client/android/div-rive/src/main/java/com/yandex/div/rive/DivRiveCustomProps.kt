package com.yandex.div.rive

import app.rive.runtime.kotlin.core.Alignment
import app.rive.runtime.kotlin.core.Fit
import app.rive.runtime.kotlin.core.Loop
import com.yandex.div.internal.util.getStringOrNull
import com.yandex.div2.DivCustom
import org.json.JSONObject

private const val RIVE_URL_KEY = "url"
private const val RIVE_FIT_KEY = "fit"
private const val RIVE_ALIGNMENT_KEY = "alignment"
private const val RIVE_LOOP_KEY = "loop"

internal data class DivRiveCustomProps(
    val url: String,
    val fit: Fit,
    val alignment: Alignment,
    val loop: Loop
) {
    companion object {
        val DEFAULT = DivRiveCustomProps(
            url = "",
            fit = Fit.CONTAIN,
            alignment = Alignment.CENTER,
            loop = Loop.AUTO
        )
        const val FIT_COVER = "Cover"
        const val FIT_CONTAIN = "Contain"
        const val FIT_FILL = "Fill"
        const val FIT_FIT_WIDTH = "FitWidth"
        const val FIT_FIT_HEIGHT = "FitHeight"
        const val FIT_NONE = "None"
        const val FIT_SCALE_DOWN = "ScaleDown"
        const val ALIGNMENT_CENTER = "Center"
        const val ALIGNMENT_TOP_LEFT = "TopLeft"
        const val ALIGNMENT_TOP_CENTER = "TopCenter"
        const val ALIGNMENT_TOP_RIGHT = "TopRight"
        const val ALIGNMENT_CENTER_LEFT = "CenterLeft"
        const val ALIGNMENT_CENTER_RIGHT = "CenterRight"
        const val ALIGNMENT_BOTTOM_LEFT = "BottomLeft"
        const val ALIGNMENT_BOTTOM_CENTER = "BottomCenter"
        const val ALIGNMENT_BOTTOM_RIGHT = "BottomRight"
        const val LOOP_LOOP = "Loop"
        const val LOOP_ONE_SHOT = "OneShot"
        const val LOOP_PING_PONG = "PingPong"
        const val LOOP_AUTO = "Auto"
    }
}

internal val DivCustom.riveAnimationProps: DivRiveCustomProps
    get() = customProps?.riveAnimationProps() ?: DivRiveCustomProps.DEFAULT

internal fun JSONObject.riveAnimationProps(): DivRiveCustomProps = DivRiveCustomProps(
    url = getStringOrNull(RIVE_URL_KEY) ?: DivRiveCustomProps.DEFAULT.url,
    fit = getStringOrNull(RIVE_FIT_KEY)
        ?.let { key ->
            when (key) {
                DivRiveCustomProps.FIT_COVER -> Fit.COVER
                DivRiveCustomProps.FIT_CONTAIN -> Fit.CONTAIN
                DivRiveCustomProps.FIT_FILL -> Fit.FILL
                DivRiveCustomProps.FIT_FIT_WIDTH -> Fit.FIT_WIDTH
                DivRiveCustomProps.FIT_FIT_HEIGHT -> Fit.FIT_HEIGHT
                DivRiveCustomProps.FIT_NONE -> Fit.NONE
                DivRiveCustomProps.FIT_SCALE_DOWN -> Fit.SCALE_DOWN
                else -> null
            }
        } ?: DivRiveCustomProps.DEFAULT.fit,
    alignment = getStringOrNull(RIVE_ALIGNMENT_KEY)
        ?.let { key ->
            when (key) {
                DivRiveCustomProps.ALIGNMENT_CENTER -> Alignment.CENTER
                DivRiveCustomProps.ALIGNMENT_TOP_LEFT -> Alignment.TOP_LEFT
                DivRiveCustomProps.ALIGNMENT_TOP_CENTER -> Alignment.TOP_CENTER
                DivRiveCustomProps.ALIGNMENT_TOP_RIGHT -> Alignment.TOP_RIGHT
                DivRiveCustomProps.ALIGNMENT_CENTER_LEFT -> Alignment.CENTER_LEFT
                DivRiveCustomProps.ALIGNMENT_CENTER_RIGHT -> Alignment.CENTER_RIGHT
                DivRiveCustomProps.ALIGNMENT_BOTTOM_LEFT -> Alignment.BOTTOM_LEFT
                DivRiveCustomProps.ALIGNMENT_BOTTOM_CENTER -> Alignment.BOTTOM_CENTER
                DivRiveCustomProps.ALIGNMENT_BOTTOM_RIGHT -> Alignment.BOTTOM_RIGHT
                else -> null
            }
        }
        ?: DivRiveCustomProps.DEFAULT.alignment,
    loop = getStringOrNull(RIVE_LOOP_KEY)
        ?.let { key ->
            when (key) {
                DivRiveCustomProps.LOOP_LOOP -> Loop.LOOP
                DivRiveCustomProps.LOOP_ONE_SHOT -> Loop.ONESHOT
                DivRiveCustomProps.LOOP_PING_PONG -> Loop.PINGPONG
                DivRiveCustomProps.LOOP_AUTO -> Loop.AUTO
                else -> null
            }
        } ?: DivRiveCustomProps.DEFAULT.loop
)