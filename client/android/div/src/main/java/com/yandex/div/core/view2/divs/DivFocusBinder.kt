package com.yandex.div.core.view2.divs

import android.view.View
import com.yandex.div.R
import com.yandex.div.core.DivActionHandler.DivActionReason
import com.yandex.div.core.DivActionPerformer
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivBorderDrawer
import com.yandex.div.core.view2.divs.widgets.DivBorderSupports
import com.yandex.div.internal.util.allIsNullOrEmpty
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivBorder
import javax.inject.Inject

@DivScope
@Mockable
internal class DivFocusBinder @Inject constructor(private val actionPerformer: DivActionPerformer) {

    fun bindDivBorder(
        view: View,
        focusedBorder: DivBorder?,
        defaultBorder: DivBorder?,
        resolver: ExpressionResolver,
        divView: Div2View,
    ): Unit = view.run {
        val border = when {
            focusedBorder == null -> defaultBorder
            focusedBorder.isConstantlyEmpty() -> defaultBorder
            isFocused -> focusedBorder
            else -> defaultBorder
        }
        applyBorder(border, resolver, divView)

        val focusListener = onFocusChangeListener as? FocusChangeListener
        if (focusListener == null && focusedBorder.isConstantlyEmpty()) {
            return
        }

        val needNewListener = focusListener?.let {
            it.focusActions != null || it.blurActions != null || !focusedBorder.isConstantlyEmpty()
        } ?: true

        if (!needNewListener) {
            onFocusChangeListener = null
            return
        }

        onFocusChangeListener = FocusChangeListener(resolver, divView).apply {
            setBorders(focusedBorder, defaultBorder)
            focusListener?.let { setActions(it.focusActions, it.blurActions) }
        }
    }

    private fun View.applyBorder(border: DivBorder?, resolver: ExpressionResolver, divView: Div2View) {
        if (this is DivBorderSupports) {
            setBorder(border, this, resolver, divView)
            return
        }

        elevation = when {
            border == null -> DivBorderDrawer.NO_ELEVATION
            border.isConstantlyEmpty() -> DivBorderDrawer.NO_ELEVATION
            !border.hasShadow.evaluate(resolver) -> DivBorderDrawer.NO_ELEVATION
            border.shadow != null -> DivBorderDrawer.NO_ELEVATION
            else -> resources.getDimension(R.dimen.div_shadow_elevation)
        }
    }

    private fun DivBorder?.isConstantlyEmpty(): Boolean {
        this ?: return true
        if (cornerRadius != null) return false
        if (cornersRadius != null) return false
        if (hasShadow != Expression.constant(false)) return false
        if (shadow != null) return false
        return stroke == null
    }

    fun bindDivFocusActions(
        target: View,
        onFocusActions: List<DivAction>?,
        onBlurActions: List<DivAction>?,
        resolver: ExpressionResolver,
        divView: Div2View,
    ) = target.run {
        val focusListener = onFocusChangeListener as? FocusChangeListener
        if (focusListener == null && allIsNullOrEmpty(onFocusActions, onBlurActions)) {
            return
        }

        val needNewListener = focusListener?.let {
            it.focusedBorder != null || !allIsNullOrEmpty(onFocusActions, onBlurActions)
        } ?: true

        if (!needNewListener) {
            onFocusChangeListener = null
            return
        }

        onFocusChangeListener = FocusChangeListener(resolver, divView).apply {
            focusListener?.let { setBorders(it.focusedBorder, it.blurredBorder) }
            setActions(onFocusActions, onBlurActions)
        }
    }

    private inner class FocusChangeListener(
        private val resolver: ExpressionResolver,
        private val divView: Div2View,
    ): View.OnFocusChangeListener {

        var focusedBorder: DivBorder? = null
            private set
        var blurredBorder: DivBorder? = null
            private set

        var focusActions: List<DivAction>? = null
            private set
        var blurActions: List<DivAction>? = null
            private set

        fun setBorders(focused: DivBorder?, blurred: DivBorder?) {
            focusedBorder = focused
            blurredBorder = blurred
        }

        fun setActions(onFocus: List<DivAction>?, onBlur: List<DivAction>?) {
            focusActions = onFocus
            blurActions = onBlur
        }

        override fun onFocusChange(v: View, hasFocus: Boolean) {
            if (hasFocus) {
                applyBorder(v, focusedBorder)
                focusActions?.handle(v, DivActionReason.FOCUS)
            } else {
                if (focusedBorder != null) applyBorder(v, blurredBorder)
                blurActions?.handle(v, DivActionReason.BLUR)
            }
        }

        private fun applyBorder(view: View, border: DivBorder?) = view.applyBorder(border, resolver, divView)

        private fun List<DivAction>.handle(
            target: View,
            actionLogType: String
        ) = actionPerformer.performBulkActions(target, this, resolver, divView, actionLogType)
    }
}
