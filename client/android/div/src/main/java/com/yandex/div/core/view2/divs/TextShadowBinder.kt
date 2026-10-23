package com.yandex.div.core.view2.divs

import android.graphics.Color
import android.graphics.Paint
import android.util.DisplayMetrics
import android.widget.TextView
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.core.view2.spannable.ShadowData
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.equalsToConstant
import com.yandex.div.json.expressions.isConstantOrNull
import com.yandex.div2.DivShadow
import com.yandex.div2.DivText

internal object TextShadowBinder {

    fun bind(
        view: DivLineHeightTextView,
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver
    ) = with(view) {
        val sameAppearance = newDiv.textShadow?.alpha.equalsToConstant(oldDiv?.textShadow?.alpha)
            && newDiv.textShadow?.blur.equalsToConstant(oldDiv?.textShadow?.blur)
            && newDiv.textShadow?.color.equalsToConstant(oldDiv?.textShadow?.color)
        val sameOffsetX = newDiv.textShadow?.offset?.x?.value.equalsToConstant(oldDiv?.textShadow?.offset?.x?.value)
            && newDiv.textShadow?.offset?.x?.unit.equalsToConstant(oldDiv?.textShadow?.offset?.x?.unit)
        val sameOffsetY = newDiv.textShadow?.offset?.y?.value.equalsToConstant(oldDiv?.textShadow?.offset?.y?.value)
            && newDiv.textShadow?.offset?.y?.unit.equalsToConstant(oldDiv?.textShadow?.offset?.y?.unit)
        if (sameAppearance && sameOffsetX && sameOffsetY) {
            return
        }

        val shadow = newDiv.textShadow
        val displayMetrics = resources.displayMetrics
        applyTextShadow(shadow?.getShadowData(resolver, displayMetrics, newDiv.textColor.evaluate(resolver)))

        val constantAppearance = shadow?.alpha.isConstantOrNull()
            && shadow?.blur.isConstantOrNull() && shadow?.color.isConstantOrNull()
        val constantOffsetX = shadow?.offset?.x?.value.isConstantOrNull() && shadow?.offset?.x?.unit.isConstantOrNull()
        val constantOffsetY = shadow?.offset?.y?.value.isConstantOrNull() && shadow?.offset?.y?.unit.isConstantOrNull()
        if (constantAppearance && constantOffsetX && constantOffsetY) {
            return
        }

        val callback = { _: Any ->
            applyTextShadow(shadow?.getShadowData(resolver, displayMetrics, newDiv.textColor.evaluate(resolver)))
        }

        addSubscription(shadow?.alpha?.observe(resolver, callback))
        addSubscription(shadow?.color?.observe(resolver, callback))
        addSubscription(shadow?.blur?.observe(resolver, callback))
        addSubscription(shadow?.offset?.x?.value?.observe(resolver, callback))
        addSubscription(shadow?.offset?.x?.unit?.observe(resolver, callback))
        addSubscription(shadow?.offset?.y?.value?.observe(resolver, callback))
        addSubscription(shadow?.offset?.y?.unit?.observe(resolver, callback))
    }

    private fun TextView.applyTextShadow(shadowParams: ShadowData?) {
        if (shadowParams == null) {
            (parent as? DivViewWrapper)?.let {
                it.clipChildren = true
                it.clipToPadding = true
            }
            clipToOutline = true
            setShadowLayer(0.0f, 0.0f, 0.0f, Color.TRANSPARENT)
        } else {
            (parent as? DivViewWrapper)?.let {
                it.clipChildren = false
                it.clipToPadding = false
            }
            clipToOutline = false
            with(shadowParams) {
                setShadowLayer(radius, offsetX, offsetY, color)
            }
        }
    }

    private fun DivShadow.getShadowData(
        resolver: ExpressionResolver,
        displayMetrics: DisplayMetrics,
        fontColor: Int
    ): ShadowData {
        val fontAlpha = fontColor ushr 24

        val radius = blur.evaluate(resolver).dpToPxF(displayMetrics)
        val offsetX = offset.x.toPx(displayMetrics, resolver).toFloat()
        val offsetY = offset.y.toPx(displayMetrics, resolver).toFloat()
        val color = Paint().apply {
            color = this@getShadowData.color.evaluate(resolver)
            alpha = (this@getShadowData.alpha.evaluate(resolver) * fontAlpha).toInt()
        }.color

        return ShadowData(offsetX, offsetY, radius, color)
    }
}
