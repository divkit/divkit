package com.yandex.div.core.view2.divs.widgets

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.NinePatch
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.Px
import androidx.core.graphics.withSave
import androidx.core.graphics.withTranslation
import com.yandex.div.R
import com.yandex.div.core.Disposable
import com.yandex.div.core.util.equalsToConstant
import com.yandex.div.core.util.getCornerRadii
import com.yandex.div.core.util.isConstant
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.ShadowCache
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.core.view2.divs.dpToPxF
import com.yandex.div.core.view2.divs.spToPx
import com.yandex.div.core.view2.divs.toPx
import com.yandex.div.internal.KLog
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.widget.isInTransientHierarchy
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBorder
import com.yandex.div2.DivShadow
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivStroke
import kotlin.math.max
import kotlin.math.min

private const val STROKE_OFFSET_PERCENTAGE = 0.1f

internal class DivBorderDrawer(
    private val view: View
) : ExpressionSubscriber {

    var border: DivBorder? = null
        private set

    private val displayMetrics: DisplayMetrics
        get() = view.resources.displayMetrics

    private val clipParams = ClipParams()
    private val borderParams by lazy { BorderParams() }
    private val shadowParams by lazy { ShadowParams() }

    private var strokeWidth = DEFAULT_STROKE_WIDTH
    private var cornerRadii: FloatArray? = null
    private var hasDifferentCornerRadii = false
    private var hasBorder = false
    private var hasCustomShadow = false
    private var hasShadow = false
    var needClipping = true
        set(value) {
            if (field == value) return
            field = value
            invalidateOutline()
            view.invalidate()
        }

    override val subscriptions = mutableListOf<Disposable>()

    fun setBorder(border: DivBorder?, resolver: ExpressionResolver) {
        if (border.equalsToConstant(this.border)) {
            return
        }

        release()
        this.border = border
        bindBorder(border, resolver)
    }

    private fun bindBorder(border: DivBorder?, resolver: ExpressionResolver) {
        applyBorder(border, resolver)
        observeBorder(border, resolver)
    }

    private fun observeBorder(border: DivBorder?, resolver: ExpressionResolver) {
        if (border == null || border.isConstant()) {
            return
        }

        val callback = { _: Any ->
            applyBorder(border, resolver)
            view.invalidate()
        }

        addSubscription(border.cornerRadius?.observe(resolver, callback))
        addSubscription(border.cornersRadius?.topLeft?.observe(resolver, callback))
        addSubscription(border.cornersRadius?.topRight?.observe(resolver, callback))
        addSubscription(border.cornersRadius?.bottomRight?.observe(resolver, callback))
        addSubscription(border.cornersRadius?.bottomLeft?.observe(resolver, callback))
        addSubscription(border.hasShadow.observe(resolver, callback))
        addSubscription(border.stroke?.color?.observe(resolver, callback))
        addSubscription(border.stroke?.width?.observe(resolver, callback))
        addSubscription(border.stroke?.unit?.observe(resolver, callback))
        addSubscription(border.shadow?.alpha?.observe(resolver, callback))
        addSubscription(border.shadow?.blur?.observe(resolver, callback))
        addSubscription(border.shadow?.color?.observe(resolver, callback))
        addSubscription(border.shadow?.offset?.x?.unit?.observe(resolver, callback))
        addSubscription(border.shadow?.offset?.x?.value?.observe(resolver, callback))
        addSubscription(border.shadow?.offset?.y?.unit?.observe(resolver, callback))
        addSubscription(border.shadow?.offset?.y?.value?.observe(resolver, callback))
    }

    private fun applyBorder(border: DivBorder?, resolver: ExpressionResolver) {
        val metrics = displayMetrics

        strokeWidth = border?.stroke?.widthPx(resolver, metrics)?.toFloat() ?: DEFAULT_STROKE_WIDTH
        hasBorder = strokeWidth > 0
        if (hasBorder) {
            val borderColor = border?.stroke?.color?.evaluate(resolver)
                ?: Color.TRANSPARENT
            borderParams.setPaintParams(strokeWidth, borderColor)
        }

        cornerRadii = border?.getCornerRadii(view.width.dpToPx(metrics).toFloat(),
                view.height.dpToPx(metrics).toFloat(),
                metrics, resolver)
        hasDifferentCornerRadii = cornerRadii?.let { radii ->
            val firstCorner = radii.first()
            !radii.all { it.equals(firstCorner) }
        } ?: false

        val hadCustomShadow = hasCustomShadow
        hasShadow = border?.hasShadow?.evaluate(resolver) ?: false
        hasCustomShadow = hasShadow && (border?.shadow != null || view.parent is DivFrameLayout)

        view.elevation = when {
            !hasShadow -> NO_ELEVATION
            hasCustomShadow -> NO_ELEVATION
            else -> view.context.resources.getDimension(R.dimen.div_shadow_elevation)
        }

        if (hasCustomShadow) {
            shadowParams.set(border?.shadow, resolver)
        }

        invalidatePaths()
        invalidateOutline()

        // Since drawShadow is called by a parent, we have to invalidate it.
        if (hasCustomShadow || hadCustomShadow) {
            (view.parent as? View)?.invalidate()
        }
    }

    fun onBoundsChanged(width: Int, height: Int) {
        invalidatePaths()
        invalidateOutline()
    }

    private fun invalidatePaths() {
        val radii = cornerRadii?.clone() ?: return

        clipParams.invalidatePath(radii)

        // Because border rect shifted inside clipping rect we have to reduce corner radius
        // by half the stroke width to reach origin corner radius.
        // Moreover, we can't avoid this logic by clipping border path because on some devices it doesn't work properly.
        val halfWidth = strokeWidth / 2f
        for (i in radii.indices) {
            radii[i] = max(0f, radii[i] - halfWidth)
        }

        if (hasBorder) {
            borderParams.invalidatePath(radii)
        }

        if (hasCustomShadow) {
            shadowParams.invalidateShadow(radii)
        }
    }

    private fun invalidateOutline() {
        if (shouldUseCanvasClipping()) {
            view.clipToOutline = false
            view.outlineProvider = ViewOutlineProvider.BACKGROUND
            return
        }

        val cornerRadius = cornerRadii?.first() ?: DEFAULT_CORNER_RADIUS
        if (cornerRadius == DEFAULT_CORNER_RADIUS) {
            view.clipToOutline = false
            view.outlineProvider = ViewOutlineProvider.BACKGROUND
            return
        }

        view.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                if (view == null || outline == null) return
                outline.setRoundRect(
                    0, 0, view.width, view.height,
                    clampCornerRadius(cornerRadius, view.width.toFloat(), view.height.toFloat())
                )
            }
        }
        view.clipToOutline = needClipping
    }

    private fun shouldUseCanvasClipping(): Boolean {
        return needClipping && (hasCustomShadow ||
            (!hasShadow && (hasDifferentCornerRadii || hasBorder || view.isInTransientHierarchy())))
    }

    private fun clampCornerRadius(cornerRadius: Float, width: Float, height: Float) : Float {
        if (height <= 0 || width <= 0)
            return 0.0f

        val maxCornerRadius = min(height, width) / 2
        if (cornerRadius > maxCornerRadius ) {
            KLog.e("Div") { "Div corner radius is too big $cornerRadius > $maxCornerRadius" }
        }
        return min(cornerRadius, maxCornerRadius)
    }

    fun clipCorners(canvas: Canvas) {
        if (!shouldUseCanvasClipping()) {
            return
        }
        canvas.clipPath(clipParams.path)
    }

    fun drawBorder(canvas: Canvas) {
        if (!hasBorder) {
            return
        }
        canvas.drawPath(borderParams.path, borderParams.paint)
    }

    fun drawShadow(canvas: Canvas) {
        if (!hasCustomShadow) {
            return
        }

        canvas.withTranslation(shadowParams.offsetX, shadowParams.offsetY) {
            shadowParams.cachedShadow?.draw(this, shadowParams.rect, shadowParams.paint)
        }
    }

    private inner class ClipParams {
        val path = Path()
        private val rect = RectF()

        fun invalidatePath(radii: FloatArray?) {
            rect.set(0f, 0f, view.width.toFloat(), view.height.toFloat())

            path.reset()
            if (radii != null) {
                path.addRoundRect(rect, radii.clone(), Path.Direction.CW)
                path.close()
            }
        }
    }

    private inner class BorderParams {
        val paint = Paint()
        val path = Path()
        private val halfDp = 0.5.dpToPxF(displayMetrics)
        private val strokeOffset // magic formula to avoid border artifacts
            get() = min(halfDp, max(1f, strokeWidth * STROKE_OFFSET_PERCENTAGE))
        private val rect = RectF()

        init {
            paint.style = Paint.Style.STROKE
            paint.isAntiAlias = true
        }

        fun setPaintParams(strokeWidth: Float, borderColor: Int) {
            paint.strokeWidth = strokeWidth + strokeOffset
            paint.color = borderColor
        }

        fun invalidatePath(radii: FloatArray) {
            val halfWidth = (strokeWidth - strokeOffset) / 2f
            rect.set(halfWidth, halfWidth, view.width.toFloat() - halfWidth, view.height.toFloat() - halfWidth)

            path.reset()
            path.addRoundRect(rect, radii, Path.Direction.CW)
            path.close()
        }
    }

    private inner class ShadowParams {
        private val defaultRadius =
            view.context.resources.getDimension(R.dimen.div_shadow_elevation)
        private var radius = defaultRadius
        private var color = DEFAULT_SHADOW_COLOR
        private var alpha = DEFAULT_SHADOW_ALPHA

        val paint = Paint()
        val rect = Rect()

        var cachedShadow: NinePatch? = null

        var offsetX = DEFAULT_DX
        var offsetY = DEFAULT_DY

        fun set(shadow: DivShadow?, resolver: ExpressionResolver) {
            val metrics = displayMetrics

            radius = shadow?.blur?.evaluate(resolver)?.dpToPxF(metrics) ?: defaultRadius
            color = shadow?.color?.evaluate(resolver) ?: DEFAULT_SHADOW_COLOR
            alpha = shadow?.alpha?.evaluate(resolver)?.toFloat()
                ?: DEFAULT_SHADOW_ALPHA

            offsetX = (shadow?.offset?.x?.toPx(metrics, resolver)
                ?: DEFAULT_DX.dpToPx(metrics)).toFloat() - radius
            offsetY = (shadow?.offset?.y?.toPx(metrics, resolver)
                ?: DEFAULT_DY.dpToPx(metrics)).toFloat() - radius
        }

        fun invalidateShadow(radii: FloatArray) {
            rect.set(0, 0, (view.width + radius * 2).toInt(), (view.height + radius * 2).toInt())

            paint.color = color
            paint.alpha = (alpha * 255).toInt()

            cachedShadow = ShadowCache.getShadow(view.context, radii, radius)
        }
    }

    companion object {
        const val NO_ELEVATION = 0f
        private const val DEFAULT_STROKE_WIDTH = 0.0f
        private const val DEFAULT_CORNER_RADIUS = 0.0f
        private const val DEFAULT_DX = 0f
        private const val DEFAULT_DY = 0.5f
        private const val DEFAULT_SHADOW_COLOR = Color.BLACK
        private const val DEFAULT_SHADOW_ALPHA = 0.14f
    }
}

@Px
internal fun DivStroke?.widthPx(expressionResolver: ExpressionResolver, metrics: DisplayMetrics): Int {
    return when(this?.unit?.evaluate(expressionResolver)) {
        DivSizeUnit.DP -> width.evaluate(expressionResolver).dpToPx(metrics)
        DivSizeUnit.SP -> width.evaluate(expressionResolver).spToPx(metrics)
        DivSizeUnit.PX -> width.evaluate(expressionResolver).toIntSafely()
        else -> this?.width?.evaluate(expressionResolver)?.toIntSafely() ?: 0
    }
}

internal inline fun DivBorderDrawer?.drawClipped(canvas: Canvas, drawCallback: (Canvas) -> Unit) {
    if (this != null) {
        canvas.withSave {
            clipCorners(canvas)
            drawCallback(canvas)
            drawBorder(canvas)
        }
    } else {
        drawCallback(canvas)
    }
}

internal inline fun DivBorderDrawer?.drawClippedAndTranslated(
    canvas: Canvas,
    translationX: Int = 0,
    translationY: Int = 0,
    drawCallback: (Canvas) -> Unit
) {
    this ?: run {
        drawCallback(canvas)
        return
    }

    val x = translationX.toFloat()
    val y = translationY.toFloat()
    canvas.withSave {
        canvas.translate(x, y)
        clipCorners(canvas)
        canvas.translate(-x, -y)
        drawCallback(canvas)
        canvas.translate(x, y)
        drawBorder(canvas)
    }
}
