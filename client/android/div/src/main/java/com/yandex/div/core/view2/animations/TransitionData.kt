package com.yandex.div.core.view2.animations

import com.yandex.div.json.expressions.ExpressionResolver

internal class TransitionData(
    val viewId: String,
    val transitions: List<DivTransition>,
    val resolver: ExpressionResolver,
)
