package com.yandex.div.core.view2.divs

import android.view.View
import com.yandex.div.R
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.divs.widgets.DivBorderDrawer
import com.yandex.div.core.view2.divs.widgets.DivBorderSupports
import com.yandex.div.internal.util.allIsNullOrEmpty
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAction
import com.yandex.div2.DivBorder
import javax.inject.Inject

@DivScope
@Mockable
internal class DivFocusBinder @Inject constructor(private val actionBinder: DivActionBinder) {

    fun bindDivBorder(
        view: View,
        context: BindingContext,
        focusedBorder: DivBorder?,
        defaultBorder: DivBorder?
    ): Unit = view.run {
        val border = when {
            focusedBorder == null -> defaultBorder
            focusedBorder.isConstantlyEmpty() -> defaultBorder
            isFocused -> focusedBorder
            else -> defaultBorder
        }
        applyBorder(context, border)

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

        onFocusChangeListener = FocusChangeListener(context).apply {
            setBorders(focusedBorder, defaultBorder)
            focusListener?.let { setActions(it.focusActions, it.blurActions) }
        }
    }

    private fun View.applyBorder(bindingContext: BindingContext, border: DivBorder?) {
        if (this is DivBorderSupports) {
            setBorder(bindingContext, border, this)
            return
        }

        elevation = when {
            border == null -> DivBorderDrawer.NO_ELEVATION
            border.isConstantlyEmpty() -> DivBorderDrawer.NO_ELEVATION
            !border.hasShadow.evaluate(bindingContext.expressionResolver) -> DivBorderDrawer.NO_ELEVATION
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
        context: BindingContext,
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

        onFocusChangeListener = FocusChangeListener(context).apply {
            focusListener?.let { setBorders(it.focusedBorder, it.blurredBorder) }
            setActions(onFocusActions, onBlurActions)
        }
    }

    private inner class FocusChangeListener(
        private val context: BindingContext,
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
                focusActions?.handle(v, DivActionBinder.LogType.LOG_FOCUS)
            } else {
                if (focusedBorder != null) applyBorder(v, blurredBorder)
                blurActions?.handle(v, DivActionBinder.LogType.LOG_BLUR)
            }
        }

        private fun applyBorder(view: View, border: DivBorder?) = view.applyBorder(context, border)

        private fun List<DivAction>.handle(target: View, actionLogType: String) =
            actionBinder.handleBulkActions(context, target, this, actionLogType)
    }
}
