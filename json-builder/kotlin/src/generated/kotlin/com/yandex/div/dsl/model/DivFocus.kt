// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivFocus internal constructor(
    @JsonIgnore val background: Property<List<DivBackground>>?,
    @JsonIgnore val border: Property<DivBorder>?,
    @JsonIgnore val nextFocusIds: Property<NextFocusIds>?,
    @JsonIgnore val onBlur: Property<List<DivAction>>?,
    @JsonIgnore val onFocus: Property<List<DivAction>>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "background" to background,
            "border" to border,
            "next_focus_ids" to nextFocusIds,
            "on_blur" to onBlur,
            "on_focus" to onFocus,
        )
    }

    class NextFocusIds internal constructor(
        @JsonIgnore val down: Property<String>?,
        @JsonIgnore val forward: Property<String>?,
        @JsonIgnore val left: Property<String>?,
        @JsonIgnore val right: Property<String>?,
        @JsonIgnore val up: Property<String>?,
    ) {

        @JsonAnyGetter
        internal fun properties(): Map<String, Any> {
            return propertyMapOf(
                "down" to down,
                "forward" to forward,
                "left" to left,
                "right" to right,
                "up" to up,
            )
        }
    }
}

fun <T> TemplateContext<T>.divFocus(): LiteralProperty<DivFocus> {
    return value(DivFocus(
        background = null,
        border = null,
        nextFocusIds = null,
        onBlur = null,
        onFocus = null,
    ))
}

fun <T> TemplateContext<T>.divFocus(
    background: Property<List<DivBackground>>? = null,
    border: Property<DivBorder>? = null,
    nextFocusIds: Property<DivFocus.NextFocusIds>? = null,
    onBlur: Property<List<DivAction>>? = null,
    onFocus: Property<List<DivAction>>? = null,
): LiteralProperty<DivFocus> {
    return value(DivFocus(
        background = background,
        border = border,
        nextFocusIds = nextFocusIds,
        onBlur = onBlur,
        onFocus = onFocus,
    ))
}

fun <T> TemplateContext<T>.divFocus(
    background: List<DivBackground>? = null,
    border: DivBorder? = null,
    nextFocusIds: DivFocus.NextFocusIds? = null,
    onBlur: List<DivAction>? = null,
    onFocus: List<DivAction>? = null,
): LiteralProperty<DivFocus> {
    return value(DivFocus(
        background = optionalValue(background),
        border = optionalValue(border),
        nextFocusIds = optionalValue(nextFocusIds),
        onBlur = optionalValue(onBlur),
        onFocus = optionalValue(onFocus),
    ))
}

fun <T> TemplateContext<T>.nextFocusIds(): LiteralProperty<DivFocus.NextFocusIds> {
    return value(DivFocus.NextFocusIds(
        down = null,
        forward = null,
        left = null,
        right = null,
        up = null,
    ))
}

fun <T> TemplateContext<T>.nextFocusIds(
    down: Property<String>? = null,
    forward: Property<String>? = null,
    left: Property<String>? = null,
    right: Property<String>? = null,
    up: Property<String>? = null,
): LiteralProperty<DivFocus.NextFocusIds> {
    return value(DivFocus.NextFocusIds(
        down = down,
        forward = forward,
        left = left,
        right = right,
        up = up,
    ))
}

fun <T> TemplateContext<T>.nextFocusIds(
    down: String? = null,
    forward: String? = null,
    left: String? = null,
    right: String? = null,
    up: String? = null,
): LiteralProperty<DivFocus.NextFocusIds> {
    return value(DivFocus.NextFocusIds(
        down = optionalValue(down),
        forward = optionalValue(forward),
        left = optionalValue(left),
        right = optionalValue(right),
        up = optionalValue(up),
    ))
}

fun CardContext.divFocus(): DivFocus {
    return DivFocus(
        background = null,
        border = null,
        nextFocusIds = null,
        onBlur = null,
        onFocus = null,
    )
}

fun CardContext.divFocus(
    background: ValueProperty<List<DivBackground>>? = null,
    border: ValueProperty<DivBorder>? = null,
    nextFocusIds: ValueProperty<DivFocus.NextFocusIds>? = null,
    onBlur: ValueProperty<List<DivAction>>? = null,
    onFocus: ValueProperty<List<DivAction>>? = null,
): DivFocus {
    return DivFocus(
        background = background,
        border = border,
        nextFocusIds = nextFocusIds,
        onBlur = onBlur,
        onFocus = onFocus,
    )
}

fun CardContext.divFocus(
    background: List<DivBackground>? = null,
    border: DivBorder? = null,
    nextFocusIds: DivFocus.NextFocusIds? = null,
    onBlur: List<DivAction>? = null,
    onFocus: List<DivAction>? = null,
): DivFocus {
    return DivFocus(
        background = optionalValue(background),
        border = optionalValue(border),
        nextFocusIds = optionalValue(nextFocusIds),
        onBlur = optionalValue(onBlur),
        onFocus = optionalValue(onFocus),
    )
}

fun CardContext.nextFocusIds(): DivFocus.NextFocusIds {
    return DivFocus.NextFocusIds(
        down = null,
        forward = null,
        left = null,
        right = null,
        up = null,
    )
}

fun CardContext.nextFocusIds(
    down: ValueProperty<String>? = null,
    forward: ValueProperty<String>? = null,
    left: ValueProperty<String>? = null,
    right: ValueProperty<String>? = null,
    up: ValueProperty<String>? = null,
): DivFocus.NextFocusIds {
    return DivFocus.NextFocusIds(
        down = down,
        forward = forward,
        left = left,
        right = right,
        up = up,
    )
}

fun CardContext.nextFocusIds(
    down: String? = null,
    forward: String? = null,
    left: String? = null,
    right: String? = null,
    up: String? = null,
): DivFocus.NextFocusIds {
    return DivFocus.NextFocusIds(
        down = optionalValue(down),
        forward = optionalValue(forward),
        left = optionalValue(left),
        right = optionalValue(right),
        up = optionalValue(up),
    )
}
