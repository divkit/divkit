package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.text.Spanned
import android.util.AttributeSet
import androidx.core.graphics.withTranslation
import com.yandex.div.R
import com.yandex.div.core.Disposable
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.util.text.DivTextRangesBackgroundHelper
import com.yandex.div.core.widget.AdaptiveMaxLines
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.util.UiThreadHandler
import com.yandex.div.internal.widget.SuperLineHeightTextView
import com.yandex.div.internal.widget.TransientView
import com.yandex.div.internal.widget.TransientViewMixin
import com.yandex.div2.DivText

@Mockable
internal class DivLineHeightTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    private val defStyleAttr: Int = R.attr.divTextStyle
) : SuperLineHeightTextView(context, attrs, defStyleAttr), DivAnimator,
    DivBorderSupports by DivBorderSupportsMixin(), TransientView by TransientViewMixin(),
    ExpressionSubscriber {

    internal var div: DivText? = null
    internal var adaptiveMaxLines: AdaptiveMaxLines? = null
    internal var textRoundedBgHelper: DivTextRangesBackgroundHelper? = null

    internal var animationStartDelay = 0L
    private var animationStarted = false

    override val subscriptions = mutableListOf<Disposable>()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(it) }
    }

    override fun dispatchDraw(canvas: Canvas) {
        dispatchDrawBorderClipped(canvas) { super.dispatchDraw(it) }
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

    override fun release() {
        super.release()
        releaseBorderDrawer()
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
