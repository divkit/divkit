package com.yandex.div.core.view2.divs

import android.view.View
import com.yandex.div.R
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.BindingContext
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
        applyBorder(border, context.expressionResolver)

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

    private fun View.applyBorder(border: DivBorder?, resolver: ExpressionResolver) {
        if (this is DivBorderSupports) {
            setBorder(border, this, resolver)
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
                focusedBorder?.applyToView(v)
                focusActions?.handle(v, DivActionBinder.LogType.LOG_FOCUS)
            } else {
                focusedBorder?.let { blurredBorder?.applyToView(v) }
                blurActions?.handle(v, DivActionBinder.LogType.LOG_BLUR)
            }
        }

        private fun DivBorder.applyToView(v: View) = v.applyBorder(this, context.expressionResolver)

        private fun List<DivAction>.handle(target: View, actionLogType: String) =
            actionBinder.handleBulkActions(context, target, this, actionLogType)
    }
}
