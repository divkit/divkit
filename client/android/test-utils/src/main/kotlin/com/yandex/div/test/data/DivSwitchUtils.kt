package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivExtension
import com.yandex.div2.DivSize
import com.yandex.div2.DivSwitch

fun switch(
    accessibility: DivAccessibility? = null,
    extensions: List<DivExtension>? = null,
    height: DivSize = wrapContent(),
    id: String? = null,
    isEnabled: Expression<Boolean> = constant(true),
    isOnVariable: String,
    paddings: DivEdgeInsets? = null,
    transitionOut: DivAppearanceTransition? = null,
    width: DivSize = matchParent(),
): Div = Div.Switch(
    DivSwitch(
        accessibility = accessibility,
        extensions = extensions,
        height = height,
        id = id,
        isEnabled = isEnabled,
        isOnVariable = isOnVariable,
        paddings = paddings,
        transitionOut = transitionOut,
        width = width,
    )
)
