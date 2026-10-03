package com.yandex.div.internal.util

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivBorder

@InternalApi
fun DivBorder?.isConstantlyEmpty(): Boolean {
    this ?: return true
    return cornerRadius == null && cornersRadius == null && stroke == null && shadow == null
        && hasShadow == Expression.constant(false)
}
