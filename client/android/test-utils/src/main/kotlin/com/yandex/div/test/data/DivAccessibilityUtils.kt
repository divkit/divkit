package com.yandex.div.test.data

import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAccessibility.Mode
import com.yandex.div2.DivAccessibility.Type

fun accessibility(
    description: String? = null,
    hint: String? = null,
    isChecked: Boolean? = null,
    mode: Mode = Mode.DEFAULT,
    stateDescription: String? = null,
    type: Type = Type.AUTO
): DivAccessibility {
    return DivAccessibility(
        description = description?.let { constant(it) },
        hint = hint?.let { constant(it) },
        isChecked = isChecked?.let { constant(it) },
        mode = constant(mode),
        stateDescription = stateDescription?.let { constant(it) },
        type = type
    )
}
