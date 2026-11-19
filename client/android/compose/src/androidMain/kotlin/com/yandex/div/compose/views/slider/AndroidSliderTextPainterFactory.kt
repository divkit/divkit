package com.yandex.div.compose.views.slider

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.resolveAsTypeface
import com.yandex.div.compose.di.DivContextScope
import dev.zacsweers.metro.Inject

@DivContextScope
@Inject
internal class AndroidSliderTextPainterFactory : SliderTextPainterFactory {

    @Composable
    override fun rememberPainter(style: SliderTextStyle): SliderTextPainter {
        val resolver = LocalFontFamilyResolver.current
        val typeface = remember(style.fontFamily, style.fontWeight, resolver) {
            resolver.resolveAsTypeface(style.fontFamily, style.fontWeight).value
        }
        return remember(style, typeface) { AndroidSliderTextPainter(style, typeface) }
    }
}

private class AndroidSliderTextPainter(
    private val textStyle: SliderTextStyle,
    private val typeface: Typeface,
) : SliderTextPainter {

    override fun draw(scope: DrawScope, text: String, centerX: Float) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = textStyle.fontSize
            letterSpacing = textStyle.letterSpacing
            color = textStyle.color
            typeface = this@AndroidSliderTextPainter.typeface
        }
        val bounds = Rect().also { paint.getTextBounds(text, 0, text.length, it) }
        scope.drawContext.canvas.nativeCanvas.drawText(
            text,
            centerX - paint.measureText(text) / 2f + textStyle.offsetX,
            scope.size.height / 2f + bounds.height() / 2f + textStyle.offsetY,
            paint,
        )
    }
}
