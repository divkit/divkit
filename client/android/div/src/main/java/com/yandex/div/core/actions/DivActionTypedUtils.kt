package com.yandex.div.core.actions

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivInputView
import com.yandex.div.core.view2.errors.ErrorObserver
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivTypedValue

internal fun Div2View.logError(throwable: Throwable) {
    viewComponent
        .errorCollectors
        .getOrCreate(dataTag, divData)
        .logError(throwable)
}

internal fun Div2View.logWarning(throwable: Throwable) {
    viewComponent
        .errorCollectors
        .getOrCreate(dataTag, divData)
        .logWarning(throwable)
}

internal fun Div2View.observeErrors(observer: ErrorObserver) {
    viewComponent
        .errorCollectors
        .getOrCreate(dataTag, divData)
        .observeAndGet(observer)
}

internal fun DivTypedValue.evaluate(expressionResolver: ExpressionResolver): Any {
    val newValue: Any = when (this) {
        is DivTypedValue.Integer -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Str -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Bool -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Color -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Number -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Url -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Array -> value.value.evaluate(expressionResolver)
        is DivTypedValue.Dict -> value.value.evaluate(expressionResolver)
    }
    return newValue
}

internal fun DivTypedValue.longValue(expressionResolver: ExpressionResolver): Long? {
    return when (this) {
        is DivTypedValue.Integer -> value.value.evaluate(expressionResolver)
        else -> null
    }
}

internal fun DivTypedValue.doubleValue(expressionResolver: ExpressionResolver): Double? {
    return when (this) {
        is DivTypedValue.Integer -> value.value.evaluate(expressionResolver).toDouble()
        is DivTypedValue.Number -> value.value.evaluate(expressionResolver)
        else -> null
    }
}

internal fun DivTypedValue.colorIntValue(expressionResolver: ExpressionResolver): Int? {
    return when (this) {
        is DivTypedValue.Color -> value.value.evaluate(expressionResolver)
        else -> null
    }
}

internal fun DivInputView.openKeyboard() {
    val imm = ContextCompat.getSystemService(context, InputMethodManager::class.java)
    imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

internal fun View.closeKeyboard() {
    val imm = ContextCompat.getSystemService(context, InputMethodManager::class.java)
    imm?.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}
