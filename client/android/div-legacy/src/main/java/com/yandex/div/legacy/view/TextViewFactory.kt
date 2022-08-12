package com.yandex.div.legacy.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

internal interface TextViewFactory {

    fun create(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0): AppCompatTextView
}