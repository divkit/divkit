package com.yandex.div.core.view2.spannable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.annotation.Px
import com.yandex.div.internal.spannable.PositionAwareReplacementSpan
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal class ImageSpan (
    image: Drawable?,
    @Px private val width: Int,
    @Px private val height: Int,
    @Px private val lineHeight: Int = 0,
    private val alignment: TextVerticalAlignment,
    internal val accessibility: Accessibility?
) : PositionAwareReplacementSpan() {

    var image: Drawable? = image
        set(value) {
            if (field != value) {
                field = value
                field?.setBounds(0, 0, width, height)
                boundsInText.setEmpty()
            }
        }

    private val boundsInText = RectF()

    override fun adjustSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        if (fm == null || lineHeight > 0) {
            return width
        }

        val ascent = paint.ascent().roundToInt()
        val descent = paint.descent().roundToInt()

        val imageHeight = image?.bounds?.height() ?: height
        val imageBaseline = when (alignment) {
            TextVerticalAlignment.TOP -> ascent + imageHeight
            TextVerticalAlignment.CENTER -> (ascent + descent + imageHeight) / 2
            TextVerticalAlignment.BASELINE -> 0
            TextVerticalAlignment.BOTTOM -> descent
        }
        val imageAscent = imageBaseline - imageHeight

        val topAscent = fm.top - fm.ascent
        val bottomDescent = fm.bottom - fm.descent

        fm.ascent = min(imageAscent, fm.ascent)
        fm.descent = max(imageBaseline, fm.descent)
        fm.top = fm.ascent + topAscent
        fm.bottom = fm.descent + bottomDescent

        return image?.bounds?.width() ?: width
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val image = this.image ?: return

        canvas.save()

        val imageHeight = image.bounds.height()
        val imageBaseline = when (alignment) {
            TextVerticalAlignment.TOP -> top + imageHeight
            TextVerticalAlignment.CENTER -> (top + bottom + imageHeight) / 2
            TextVerticalAlignment.BASELINE -> y
            TextVerticalAlignment.BOTTOM -> bottom
        }

        val imageAscent = imageBaseline.toFloat() - imageHeight

        boundsInText.set(image.bounds)
        boundsInText.offset(x, imageAscent)

        canvas.translate(x, imageAscent)
        image.draw(canvas)
        canvas.restore()
    }

    fun getBoundsInText(rect: Rect): Rect {
        rect.set(
            /* left = */ boundsInText.left.roundToInt(),
            /* top = */ boundsInText.top.roundToInt(),
            /* right = */ boundsInText.right.roundToInt(),
            /* bottom = */ boundsInText.bottom.roundToInt()
        )
        return rect
    }

    fun getBoundsInText(rect: RectF): RectF {
        rect.set(boundsInText)
        return rect
    }

    class Accessibility(
        val accessibilityType: String?,
        val contentDescription: String?,
        val onClickAction: OnAccessibilityClickAction?
    )

    fun interface OnAccessibilityClickAction {
        fun perform()
    }
}
