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
import com.yandex.div.core.expression.ExpressionSubscriber
import com.yandex.div.core.util.KLog
import com.yandex.div.core.util.getCornerRadii
import com.yandex.div.core.view2.ShadowCache
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.core.view2.divs.dpToPxF
import com.yandex.div.core.view2.divs.spToPx
import com.yandex.div.core.view2.divs.toPx
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.util.dpToPx
import com.yandex.div2.DivBorder
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivStroke
import kotlin.math.max
import kotlin.math.min

class DivBorderDrawer(
    private val metrics: DisplayMetrics,
    private val view: View,
    private val expressionResolver: ExpressionResolver,
    val border: DivBorder
) : ExpressionSubscriber {

    private val paint = Paint()
    private val shadowPaint = Paint()
    private val shadowRect = Rect()
    private val clipRect = RectF()
    private val clipPath = Path()
    private val borderRect = RectF()
    private val borderPath = Path()

    private val defaultShadowRadius = view.context.resources.getDimension(R.dimen.div_shadow_elevation)
    private val defaultDX = 0f
    private val defaultDY = 0.5f
    private val defaultShadowColor = Color.BLACK
    private val defaultShadowAlpha = 0.23f

    private var strokeWidth = 0f
    private var cornerRadii: FloatArray? = null
    private var hasDifferentCornerRadii = false
    private var hasBorder = false
    private var hasCustomShadow = false
    private var hasShadow = false

    private var shadowRadius = defaultShadowRadius
    private var shadowOffsetX = defaultDX
    private var shadowOffsetY = defaultDY
    private var shadowColor = defaultShadowColor

    override val subscriptions = mutableListOf<Disposable>()

    private var cachedShadow: NinePatch? = null

    init {
        observeBorder(expressionResolver, border)

        val width = view.width
        val height = view.height
        if (width > 0 && height > 0) {
            onBoundsChanged(width, height)
        }
    }

    private fun observeBorder(resolver: ExpressionResolver, border: DivBorder) {
        applyBorder(border, resolver)

        val callback = { _: Any ->
            applyBorder(border, resolver)
            view.invalidate()
        }

        addSubscription(border.cornerRadius?.observe(resolver, callback) ?: Disposable.NULL)
        addSubscription(border.cornersRadius?.topLeft?.observe(resolver, callback) ?: Disposable.NULL)
        addSubscription(border.cornersRadius?.topRight?.observe(resolver, callback) ?: Disposable.NULL)
        addSubscription(border.cornersRadius?.bottomRight?.observe(resolver, callback) ?: Disposable.NULL)
        addSubscription(border.cornersRadius?.bottomLeft?.observe(resolver, callback) ?: Disposable.NULL)
        addSubscription(border.hasShadow.observe(resolver, callback))
        addSubscription(border.stroke?.color?.observe(resolver, callback) ?: Disposable.NULL)
        addSubscription(border.stroke?.width?.observe(resolver, callback) ?: Disposable.NULL)
        addSubscription(border.stroke?.unit?.observe(resolver, callback) ?: Disposable.NULL)
        addSubscription(border.hasShadow.observe(resolver, callback))
        addSubscription(border.shadow?.alpha?.observe(resolver, callback) ?: Disposable.NULL)
        addSubscription(border.shadow?.blur?.observe(resolver, callback) ?: Disposable.NULL)
        addSubscription(border.shadow?.color?.observe(resolver, callback) ?: Disposable.NULL)
        addSubscription(border.shadow?.offset?.x?.unit?.observe(resolver, callback) ?: Disposable.NULL)
        addSubscription(border.shadow?.offset?.x?.value?.observe(resolver, callback) ?: Disposable.NULL)
        addSubscription(border.shadow?.offset?.y?.unit?.observe(resolver, callback) ?: Disposable.NULL)
        addSubscription(border.shadow?.offset?.y?.value?.observe(resolver, callback) ?: Disposable.NULL)
    }

    private fun applyBorder(border: DivBorder, resolver: ExpressionResolver) {
        strokeWidth = border.stroke.widthPx().toFloat()
        hasBorder = strokeWidth > 0
        paint.strokeWidth = strokeWidth
        paint.color = border.stroke?.color?.evaluate(expressionResolver) ?: Color.TRANSPARENT
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true

        cornerRadii = border.getCornerRadii(metrics, resolver)
        hasDifferentCornerRadii = cornerRadii?.let { radii ->
            val firstCorner = radii.first()
            !radii.all { it.equals(firstCorner) }
        } ?: false

        hasShadow = border.hasShadow.evaluate(expressionResolver)
        hasCustomShadow = border.shadow != null && hasShadow

        invalidatePaths()
        invalidateOutline()
    }

    fun onBoundsChanged(width: Int, height: Int) {
        val halfWidth = border.stroke.widthPx() / 2f
        borderRect.set(halfWidth, halfWidth, width - halfWidth, height - halfWidth)
        clipRect.set(0f, 0f, width.toFloat(), height.toFloat())
        shadowRect.set(0, 0, (width + shadowRadius * 2).toInt(), (height + shadowRadius * 2).toInt())
        invalidatePaths()
        invalidateOutline()
    }

    private fun invalidatePaths() {
        borderPath.reset()
        clipPath.reset()

        val radii = cornerRadii?.clone() ?: return

        // clip corner radii
        for (i in radii.indices) {
            radii[i] = clampCornerRadius(radii[i], clipRect.width(), clipRect.height())
        }

        clipPath.addRoundRect(clipRect, radii.clone(), Path.Direction.CW)
        clipPath.close()

        // Because border rect shifted inside clipping rect we have to reduce corner radius
        // by half the stroke width to reach origin corner radius.
        // Moreover, we can't avoid this logic by clipping border path because on some devices it doesn't work properly.
        val halfWidth = border.stroke.widthPx() / 2f
        for (i in radii.indices) {
            radii[i] = max(0f, radii[i] - halfWidth)
        }

        borderPath.addRoundRect(borderRect, radii, Path.Direction.CW)
        borderPath.close()

        invalidateShadow(radii)
    }

    private fun invalidateShadow(radii: FloatArray) {
        if (!hasCustomShadow) return

        val shadow = border.shadow

        shadowRadius = shadow?.blur?.evaluate(expressionResolver)?.dpToPxF(metrics) ?: defaultShadowRadius
        shadowColor = shadow?.color?.evaluate(expressionResolver) ?: defaultShadowColor
        val shadowAlpha = shadow?.alpha?.evaluate(expressionResolver)?.toFloat() ?: defaultShadowAlpha

        shadowOffsetX = (shadow?.offset?.x?.toPx(metrics, expressionResolver) ?: defaultDX.dpToPx()).toFloat() - shadowRadius
        shadowOffsetY = (shadow?.offset?.y?.toPx(metrics, expressionResolver) ?: defaultDY.dpToPx()).toFloat() - shadowRadius

        shadowPaint.apply {
            color = shadowColor
            alpha = (shadowAlpha * 255).toInt()
        }

        cachedShadow = ShadowCache.getShadow(view.context, radii, shadowRadius)
    }

    private fun invalidateOutline() {
        if (isNeedUseCanvasClipping()) {
            view.clipToOutline = false
            return
        }
        view.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                if (view == null || outline == null) return
                outline.setRoundRect(
                    0, 0, view.width, view.height,
                    clampCornerRadius(
                        cornerRadii?.first() ?: 0f,
                        view.width.toFloat(), view.height.toFloat()
                    )
                )
            }
        }
        view.clipToOutline = true
    }

    private fun isNeedUseCanvasClipping(): Boolean {
        return hasCustomShadow || (!hasShadow && (hasDifferentCornerRadii || hasBorder || view.isInTransientHierarchy()))
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
        if (!isNeedUseCanvasClipping()) {
            return
        }
        canvas.clipPath(clipPath)
    }

    fun drawBorder(canvas: Canvas) {
        if (!hasBorder) {
            return
        }
        canvas.drawPath(borderPath, paint)
    }

    fun drawShadow(canvas: Canvas) {
        if (!hasCustomShadow) return

        canvas.withTranslation(shadowOffsetX, shadowOffsetY) {
            cachedShadow?.draw(this, shadowRect, shadowPaint)
        }
    }

    @Px
    private fun DivStroke?.widthPx(): Int {
        return when(this?.unit?.evaluate(expressionResolver)) {
            DivSizeUnit.DP -> width.evaluate(expressionResolver).dpToPx(metrics)
            DivSizeUnit.SP -> width.evaluate(expressionResolver).spToPx(metrics)
            DivSizeUnit.PX -> width.evaluate(expressionResolver)
            else -> this?.width?.evaluate(expressionResolver) ?: 0
        }
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
