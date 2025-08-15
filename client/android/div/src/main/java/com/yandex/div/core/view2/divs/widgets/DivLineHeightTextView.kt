package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.text.Spanned
import android.util.AttributeSet
import androidx.core.graphics.withTranslation
import com.yandex.div.R
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.util.text.DivBackgroundSpan
import com.yandex.div.core.util.text.DivTextRangesBackgroundHelper
import com.yandex.div.core.widget.AdaptiveMaxLines
import com.yandex.div.internal.util.UiThreadHandler
import com.yandex.div.internal.widget.TextViewWithAccessibleSpans
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div

@Mockable
internal class DivLineHeightTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.divTextStyle
): TextViewWithAccessibleSpans(context, attrs, defStyleAttr),
    DivHolderView<Div.Text> by DivHolderViewMixin(),
    DivAnimator {

    internal var adaptiveMaxLines: AdaptiveMaxLines? = null
    internal var textRoundedBgHelper: DivTextRangesBackgroundHelper? = null

    internal var animationStartDelay = 0L
    private var animationStarted = false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(it) }
    }

    override fun startDivAnimation() {
        super.startDivAnimation()
        animationStarted = true
        UiThreadHandler.get().postDelayed({ if (animationStarted) isSelected = true }, animationStartDelay)
    }

    override fun stopDivAnimation() {
        super.stopDivAnimation()
        animationStarted = false
        isSelected = false
    }

    override fun onDraw(canvas: Canvas) {
        // need to draw bg first so that text can be on top during super.onDraw()
        if (text is Spanned && layout != null && textRoundedBgHelper?.hasBackgroundSpan() == true ) {
            canvas.withTranslation(totalPaddingLeft.toFloat(), totalPaddingTop.toFloat()) {
                textRoundedBgHelper?.draw(canvas, text as Spanned, layout)
            }
        }
        super.onDraw(canvas)
    }
}

internal fun DivLineHeightTextView.hasBackgroundSpan(
    text: CharSequence,
    backgroundSpan: DivBackgroundSpan,
    start: Int,
    end: Int,
    resolver: ExpressionResolver
): Boolean {
    if (textRoundedBgHelper == null) {
        textRoundedBgHelper = DivTextRangesBackgroundHelper(this, resolver)
        return false
    }
    return textRoundedBgHelper!!.hasSameSpan(text, backgroundSpan, start, end)
}
