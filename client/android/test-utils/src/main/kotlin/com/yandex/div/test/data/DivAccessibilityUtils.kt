package com.yandex.div.test.data

import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAccessibility.Mode
import com.yandex.div2.DivAccessibility.Type
import com.yandex.div2.DivAccessibilityMenuItem
import com.yandex.div2.DivAction

fun accessibility(
    description: String? = null,
    hint: String? = null,
    isChecked: Boolean? = null,
    mode: Mode = Mode.DEFAULT,
    stateDescription: String? = null,
    type: Type = Type.AUTO,
    menu: List<DivAccessibilityMenuItem>? = null,
): DivAccessibility {
    return DivAccessibility(
        description = description?.let { constant(it) },
        hint = hint?.let { constant(it) },
        isChecked = isChecked?.let { constant(it) },
        menu = menu,
        mode = constant(mode),
        stateDescription = stateDescription?.let { constant(it) },
        type = type
    )
}

fun accessibilityMenuItem(label: String, actions: List<DivAction>): DivAccessibilityMenuItem {
    return DivAccessibilityMenuItem(label = constant(label), actions = actions)
}

fun accessibilityModeExpression(expression: String): Expression<Mode> {
    return Expression.MutableExpression(
        expressionKey = "mode",
        rawExpression = expression,
        converter = Mode::fromString,
        validator = { true },
        logger = throwingErrorLogger,
        typeHelper = TypeHelper.from(default = Mode.DEFAULT) { it is Mode },
    )
}
