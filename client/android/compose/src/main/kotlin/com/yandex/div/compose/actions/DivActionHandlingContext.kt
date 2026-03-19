package com.yandex.div.compose.actions

import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.json.expressions.ExpressionResolver
import javax.inject.Inject

@Mockable
@PublicApi
class DivActionHandlingContext @Inject constructor(
    val expressionResolver: ExpressionResolver
)
