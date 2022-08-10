package com.yandex.div.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView

val TextView.fontHeight: Int
    get() = paint.getFontMetricsInt(null)

fun TextView.onTextChanged(notifyInitialText: Boolean = false, doOnChange: (TextView, CharSequence?) -> Unit) {
    addTextChangedListener(TextChangeListener(this, doOnChange))
    if (notifyInitialText) doOnChange(this, text)
}

private class TextChangeListener(
    private val textView: TextView,
    private val doOnChange: (TextView, CharSequence?) -> Unit
) : TextWatcher {

    override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) = doOnChange(textView, text)

    override fun afterTextChanged(text: Editable?) = Unit
}
