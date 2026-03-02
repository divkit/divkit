package com.yandex.div.compose

import android.content.Context
import com.yandex.div.json.expressions.ExpressionResolver

class DivComposeConfiguration {
    fun createContext(baseContext: Context): DivContext {
        return DivContext(
            baseContext = baseContext,
            expressionResolver = ExpressionResolver.EMPTY
        )
    }
}
