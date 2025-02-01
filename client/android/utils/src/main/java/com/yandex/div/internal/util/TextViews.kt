package com.yandex.div.internal.util

import android.widget.TextView
import com.yandex.div.core.annotations.InternalApi

@InternalApi
public val TextView.textString: String
    get() = text.toString()
