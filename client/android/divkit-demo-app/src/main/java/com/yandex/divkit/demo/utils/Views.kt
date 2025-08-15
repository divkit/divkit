package com.yandex.divkit.demo.utils

import android.view.View
import android.widget.TextView

var View.padding: Int
    get() = noGetter()
    set(value) = setPadding(value, value, value, value)

var TextView.textColor: Int
    get() = noGetter()
    set(value) = setTextColor(value)

fun noGetter(): Nothing = throw UnsupportedOperationException("Property does not have a getter")
