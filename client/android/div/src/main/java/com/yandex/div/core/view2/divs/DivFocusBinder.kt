package com.yandex.div.core.view2.divs

import android.view.View
import com.yandex.div.R
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivBorderDrawer
import com.yandex.div.core.view2.divs.widgets.DivBorderSupports
import com.yandex.div.internal.util.allIsNullOrEmpty
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivBorder
import javax.inject.Inject

@DivScope
@Mockable
internal class DivFocusBinder @Inject constructor(private val actionBinder: DivActionBinder) {

    fun bindDivBorder(
        view: View,
        divView: Div2View,
        resolver: ExpressionResolver,
        focusedBorder: DivBorder?,
        blurredBorder: DivBorder
    ): Unit = view.run {
        val border = when {
            focusedBorder == null -> blurredBorder
            focusedBorder.isConstantlyEmpty() -> blurredBorder
            isFocused -> focusedBorder
            else -> blurredBorder
        }
        applyBorder(border, resolver)

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

        onFocusChangeListener = FocusChangeListener(divView, resolver).apply {
            setBorders(focusedBorder, blurredBorder)
            focusListener?.let { setActions(it.focusActions, it.blurActions) }
        }
    }

    private fun View.applyBorder(border: DivBorder, resolver: ExpressionResolver) {
        if (this is DivBorderSupports) {
            setBorder(border, resolver)
            return
        }

        elevation = when {
            border.isConstantlyEmpty() -> DivBorderDrawer.NO_ELEVATION
            !border.hasShadow.evaluate(resolver) -> DivBorderDrawer.NO_ELEVATION
            border.shadow != null -> DivBorderDrawer.NO_ELEVATION
            else -> resources.getDimension(R.dimen.div_shadow_elevation)
        }
    }

    fun bindDivFocusActions(
        target: View,
        divView: Div2View,
        resolver: ExpressionResolver,
        onFocusActions: List<DivAction>?,
        onBlurActions: List<DivAction>?
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

        onFocusChangeListener = FocusChangeListener(divView, resolver).apply {
            focusListener?.let { setBorders(it.focusedBorder, it.blurredBorder) }
            setActions(onFocusActions, onBlurActions)
        }
    }

    private inner class FocusChangeListener(
        private val divView: Div2View,
        private val resolver: ExpressionResolver
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
                focusedBorder?.applyToView(v)
                focusActions?.handle(v, DivActionBinder.LogType.LOG_FOCUS)
            } else {
                focusedBorder?.let { blurredBorder?.applyToView(v) }
                blurActions?.handle(v, DivActionBinder.LogType.LOG_BLUR)
            }
        }

        private fun DivBorder.applyToView(v: View) = v.applyBorder(this, resolver)

        private fun List<DivAction>.handle(target: View, actionLogType: String) =
            actionBinder.handleBulkActions(divView, target, this, actionLogType)
    }
}
