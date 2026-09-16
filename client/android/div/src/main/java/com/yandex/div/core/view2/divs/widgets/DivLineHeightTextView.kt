package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.text.Spanned
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withTranslation
import com.yandex.div.R
import com.yandex.div.core.util.text.DivBackgroundSpan
import com.yandex.div.core.util.text.DivTextRangesBackgroundHelper
import com.yandex.div.core.view2.spannable.ParticlesTicker
import com.yandex.div.core.widget.AdaptiveMaxLines
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.util.UiThreadHandler
import com.yandex.div.internal.widget.TextViewWithAccessibleSpans
import com.yandex.div.json.expressions.ExpressionResolver

internal class DivLineHeightTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.divTextStyle
): TextViewWithAccessibleSpans(context, attrs, defStyleAttr),
    DivHolderView<DivBlock.Text> by DivHolderViewMixin(),
    DivAnimator {

    internal var adaptiveMaxLines: AdaptiveMaxLines? = null
    internal var textRoundedBgHelper: DivTextRangesBackgroundHelper? = null

    internal var animationStartDelay = 0L
    private var animationStarted = false
    private var particlesTicker: ParticlesTicker? = null
    private var animatedTextGradientController: AnimatedTextGradientController? = null

    internal fun getParticlesTicker(): ParticlesTicker {
        return particlesTicker ?: ParticlesTicker(this).also { particlesTicker = it }
    }

    internal fun setAnimatedTextGradient(data: AnimatedTextGradientData, animationsEnabled: Boolean) {
        val controller = animatedTextGradientController
            ?: AnimatedTextGradientController(this).also { animatedTextGradientController = it }
        controller.update(data, animationsEnabled)
    }

    internal fun setTextGradientAnimationsEnabled(enabled: Boolean) {
        animatedTextGradientController?.setAnimationsEnabled(enabled)
    }

    internal fun clearAnimatedTextGradient() {
        animatedTextGradientController?.release()
        animatedTextGradientController = null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animatedTextGradientController?.onAttachedToWindow()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        animatedTextGradientController?.onVisibilityChanged()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onBoundsChanged(w, h)
        animatedTextGradientController?.invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        animatedTextGradientController?.invalidate()
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

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility == View.VISIBLE) {
            particlesTicker?.resumeIfNeeded()
        } else {
            particlesTicker?.stop()
        }
        animatedTextGradientController?.onVisibilityChanged()
    }

    override fun onDetachedFromWindow() {
        particlesTicker?.stop()
        particlesTicker = null
        animatedTextGradientController?.onDetachedFromWindow()
        super.onDetachedFromWindow()
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
