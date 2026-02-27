package com.yandex.div.compose

import android.content.Context
import android.content.ContextWrapper
import androidx.annotation.MainThread
import com.yandex.div.json.expressions.ExpressionResolver

class DivContext @MainThread internal constructor(
    baseContext: Context,
    internal val expressionResolver: ExpressionResolver
) : ContextWrapper(baseContext)
