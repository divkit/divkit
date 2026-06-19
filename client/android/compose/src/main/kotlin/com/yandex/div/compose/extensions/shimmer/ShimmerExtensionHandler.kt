package com.yandex.div.compose.extensions.shimmer

import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.internal.extensions.ShimmerExtensionParams
import com.yandex.div.json.expressions.Expression
import org.json.JSONObject
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.math.tan

/**
 * A [DivExtensionHandler] that draws an animated placeholder for an `image` or `gif` elements
 * until its content is loaded.
 */
@ExperimentalApi
class ShimmerExtensionHandler : DivExtensionHandler {
    // used to synchronize animations across multiple elements
    private var animationStartTime: Long = 0L

    @Composable
    override fun Content(
        modifier: Modifier,
        environment: DivExtensionEnvironment,
        content: @Composable (modifier: Modifier) -> Unit
    ) {
        val isImageLoaded = LocalDivViewContext.current.component.imageStateStorage
            .isLoaded(environment.data.value())
        val params = environment.extension.params
        val modifier = if (isImageLoaded || params == null) {
            modifier
        } else {
            modifier.shimmer(params)
        }
        content(modifier)
    }

    @Composable
    private fun Modifier.shimmer(paramsJson: JSONObject): Modifier {
        if (animationStartTime == 0L) {
            animationStartTime = SystemClock.uptimeMillis()
        }

        val params = remember(paramsJson) { ShimmerExtensionParams.fromJson(paramsJson) }
        val config = params.observedConfig()
        val phaseState = remember { mutableFloatStateOf(0f) }

        LaunchedEffect(config.durationMillis) {
            if (coroutineContext[MotionDurationScale]?.scaleFactor == 0f) {
                return@LaunchedEffect
            }
            val durationMillis = config.durationMillis.coerceAtLeast(1L)
            while (true) {
                withFrameMillis { now ->
                    val elapsed = (now - animationStartTime).coerceAtLeast(0L)
                    phaseState.floatValue =
                        (elapsed % durationMillis).toFloat() / durationMillis.toFloat()
                }
            }
        }

        return drawBehind {
            drawShimmer(config, phaseState.floatValue)
        }
    }
}

private fun DrawScope.drawShimmer(config: ShimmerConfig, phase: Float) {
    if (config.colorStops.size < 2) {
        return
    }

    val width = size.width
    val height = size.height
    if (width <= 0f || height <= 0f) {
        return
    }

    val normalizedAngle = (config.angle.toInt() % 360 + 360) % 360
    val isVerticalGradient = normalizedAngle in 45 until 135 || normalizedAngle in 225 until 315
    val baseEndX = if (isVerticalGradient) 0f else width
    val baseEndY = if (isVerticalGradient) height else 0f

    val tanAngle = abs(tan(Math.toRadians(config.angle)).toFloat())
    val translateHeight = height + tanAngle * width
    val translateWidth = width + tanAngle * height

    var rotate = config.angle
    val dx: Float
    val dy: Float
    when (normalizedAngle) {
        in 45 until 135 -> {
            rotate -= 90
            dx = 0f
            dy = offsetSymmetric(-translateHeight, phase)
        }

        in 225 until 315 -> {
            rotate -= 270
            dx = 0f
            dy = offsetSymmetric(translateHeight, phase)
        }

        in 135 until 225 -> {
            rotate -= 180
            dx = offsetSymmetric(translateWidth, phase)
            dy = 0f
        }

        else -> {
            dx = offsetSymmetric(-translateWidth, phase)
            dy = 0f
        }
    }

    val rotateRad = Math.toRadians(rotate).toFloat()
    val cosA = cos(rotateRad)
    val sinA = sin(rotateRad)
    val centerX = width / 2f
    val centerY = height / 2f

    fun transform(x: Float, y: Float): Offset {
        val tx = x + dx
        val ty = y + dy
        val rx = (tx - centerX) * cosA - (ty - centerY) * sinA + centerX
        val ry = (tx - centerX) * sinA + (ty - centerY) * cosA + centerY
        return Offset(rx, ry)
    }

    val brush = Brush.linearGradient(
        colorStops = config.colorStops.toTypedArray(),
        start = transform(0f, 0f),
        end = transform(baseEndX, baseEndY),
        tileMode = TileMode.Clamp,
    )

    val cornerRadius = config.cornerRadius
    if (cornerRadius == null) {
        drawRect(brush)
        return
    }

    val path = Path().apply {
        addRoundRect(
            RoundRect(
                left = 0f,
                top = 0f,
                right = width,
                bottom = height,
                topLeftCornerRadius = cornerRadius.topLeft,
                topRightCornerRadius = cornerRadius.topRight,
                bottomRightCornerRadius = cornerRadius.bottomRight,
                bottomLeftCornerRadius = cornerRadius.bottomLeft,
            )
        )
    }
    drawPath(path, brush)
}

private class ShimmerConfig(
    val colorStops: List<Pair<Float, Color>>,
    val angle: Double,
    val durationMillis: Long,
    val cornerRadius: ShimmerCornerRadius?,
)

private data class ShimmerCornerRadius(
    val topLeft: CornerRadius,
    val topRight: CornerRadius,
    val bottomLeft: CornerRadius,
    val bottomRight: CornerRadius,
)

@Composable
private fun Expression<Long>?.observedCornerRadius(): CornerRadius {
    return this?.observedValue(
        transform = { value ->
            value.toFloat().let { CornerRadius(x = it, y = it) }
        }
    ) ?: CornerRadius.Zero
}

@Composable
private fun ShimmerExtensionParams.observedConfig(): ShimmerConfig {
    val colors = colors.observedValue()
    val locations = locations.observedValue()
    val angle = angle.observedValue()
    val duration = duration.observedValue()

    val cornerRadius = if (cornerRadius == null) {
        null
    } else {
        val topLeft = cornerRadius?.topLeft.observedCornerRadius()
        val topRight = cornerRadius?.topRight.observedCornerRadius()
        val bottomLeft = cornerRadius?.bottomLeft.observedCornerRadius()
        val bottomRight = cornerRadius?.bottomRight.observedCornerRadius()
        ShimmerCornerRadius(topLeft, topRight, bottomLeft, bottomRight)
    }

    return remember(colors, locations, angle, duration, cornerRadius) {
        val stopsCount = minOf(colors.size, locations.size)
        val colorStops = List(stopsCount) { i ->
            locations[i].toFloat().coerceIn(0f, 1f) to Color(colors[i])
        }
        ShimmerConfig(
            colorStops = colorStops,
            angle = angle,
            durationMillis = (duration * 1000).roundToLong(),
            cornerRadius = cornerRadius,
        )
    }
}

private fun offsetSymmetric(start: Float, percent: Float): Float {
    val end = -start
    return start + (end - start) * percent
}
