package androidx.appcompat.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet

@Deprecated(
    message = "for backward compat only",
    replaceWith = ReplaceWith("com.yandex.div.core.widget.LinearContainerLayout")
)
open class LinearLayoutWithCenteredDividers @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    override fun drawHorizontalDivider(canvas: Canvas, top: Int) = drawDivider(
        canvas,
        paddingLeft + dividerPadding,
        top,
        width - paddingRight - dividerPadding,
        top + dividerDrawable.intrinsicHeight
    )

    override fun drawVerticalDivider(canvas: Canvas, left: Int) = drawDivider(
        canvas,
        left,
        paddingTop + dividerPadding,
        left + dividerDrawable.intrinsicWidth,
        height - paddingBottom - dividerPadding
    )

    private fun drawDivider(canvas: Canvas, left: Int, top: Int, right: Int, bottom: Int) =
        dividerDrawable.run {
            val centerHorizontal = (left + right) / 2f
            val centerVertical = (top + bottom) / 2f
            val halfWidth = intrinsicWidth / 2f
            val halfHeight = intrinsicHeight / 2f
            setBounds((centerHorizontal - halfWidth).toInt(), (centerVertical - halfHeight).toInt(),
                (centerHorizontal + halfWidth).toInt(), (centerVertical + halfHeight).toInt())
            draw(canvas)
        }
}
