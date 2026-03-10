package com.yandex.div.compose

import android.content.Context
import android.content.ContextWrapper
import androidx.annotation.MainThread
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.json.expressions.ExpressionResolver
import javax.inject.Inject

@PublicApi
class DivContext @Inject @MainThread internal constructor(
    baseContext: Context,
    internal val expressionResolver: ExpressionResolver,
    internal val reporter: DivReporter
) : ContextWrapper(baseContext)
