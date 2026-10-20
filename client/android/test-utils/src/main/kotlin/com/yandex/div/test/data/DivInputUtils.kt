package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivFocus
import com.yandex.div2.DivInput
import com.yandex.div2.DivInputFilter
import com.yandex.div2.DivInputMask
import com.yandex.div2.DivInputValidator
import com.yandex.div2.DivVisibility

fun input(
    autocapitalization: Expression<DivInput.Autocapitalization> = constant(DivInput.Autocapitalization.AUTO),
    enterKeyActions: List<DivAction>? = null,
    enterKeyType: Expression<DivInput.EnterKeyType> = constant(DivInput.EnterKeyType.DEFAULT),
    filters: List<DivInputFilter>? = null,
    focus: DivFocus? = null,
    hintText: Expression<String>? = null,
    id: String? = null,
    isEnabled: Expression<Boolean> = constant(true),
    keyboardType: Expression<DivInput.KeyboardType> = constant(DivInput.KeyboardType.MULTI_LINE_TEXT),
    mask: DivInputMask? = null,
    maxLength: Expression<Long>? = null,
    selectAllOnFocus: Expression<Boolean> = constant(false),
    textVariable: String,
    validators: List<DivInputValidator>? = null,
    visibility: Expression<DivVisibility> = constant(DivVisibility.VISIBLE),
): Div = Div.Input(
    DivInput(
        autocapitalization = autocapitalization,
        enterKeyActions = enterKeyActions,
        enterKeyType = enterKeyType,
        filters = filters,
        focus = focus,
        hintText = hintText,
        id = id,
        isEnabled = isEnabled,
        keyboardType = keyboardType,
        mask = mask,
        maxLength = maxLength,
        selectAllOnFocus = selectAllOnFocus,
        textVariable = textVariable,
        validators = validators,
        visibility = visibility,
    )
)
