@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * Element behavior when focusing or losing focus.
 * 
 * Can be created using the method [focus].
 */
@Generated
@ExposedCopyVisibility
data class Focus internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): Focus = Focus(
        Properties(
            background = additive.background ?: properties.background,
            border = additive.border ?: properties.border,
            nextFocusIds = additive.nextFocusIds ?: properties.nextFocusIds,
            onBlur = additive.onBlur ?: properties.onBlur,
            onFocus = additive.onFocus ?: properties.onFocus,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Background of an element when it is in focus. It can contain multiple layers.
         */
        val background: Property<List<Background>>?,
        /**
         * Border of an element when it's in focus.
         */
        val border: Property<Border>?,
        /**
         * IDs of elements that will be next to get focus.
         */
        val nextFocusIds: Property<NextFocusIds>?,
        /**
         * Actions when an element loses focus.
         */
        val onBlur: Property<List<Action>>?,
        /**
         * Actions when an element gets focus.
         */
        val onFocus: Property<List<Action>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("background", background)
            result.tryPutProperty("border", border)
            result.tryPutProperty("next_focus_ids", nextFocusIds)
            result.tryPutProperty("on_blur", onBlur)
            result.tryPutProperty("on_focus", onFocus)
            return result
        }
    }

    /**
     * IDs of elements that will be next to get focus.
     * 
     * Can be created using the method [focusNextFocusIds].
     */
    @Generated
    @ExposedCopyVisibility
    data class NextFocusIds internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): NextFocusIds = NextFocusIds(
            Properties(
                down = additive.down ?: properties.down,
                forward = additive.forward ?: properties.forward,
                left = additive.left ?: properties.left,
                right = additive.right ?: properties.right,
                up = additive.up ?: properties.up,
            )
        )

        @ExposedCopyVisibility
        data class Properties internal constructor(
            val down: Property<String>?,
            val forward: Property<String>?,
            val left: Property<String>?,
            val right: Property<String>?,
            val up: Property<String>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("down", down)
                result.tryPutProperty("forward", forward)
                result.tryPutProperty("left", left)
                result.tryPutProperty("right", right)
                result.tryPutProperty("up", up)
                return result
            }
        }
    }

}

/**
 * @param background Background of an element when it is in focus. It can contain multiple layers.
 * @param border Border of an element when it's in focus.
 * @param nextFocusIds IDs of elements that will be next to get focus.
 * @param onBlur Actions when an element loses focus.
 * @param onFocus Actions when an element gets focus.
 */
@Generated
fun DivScope.focus(
    `use named arguments`: Guard = Guard.instance,
    background: List<Background>? = null,
    border: Border? = null,
    nextFocusIds: Focus.NextFocusIds? = null,
    onBlur: List<Action>? = null,
    onFocus: List<Action>? = null,
): Focus = Focus(
    Focus.Properties(
        background = valueOrNull(background),
        border = valueOrNull(border),
        nextFocusIds = valueOrNull(nextFocusIds),
        onBlur = valueOrNull(onBlur),
        onFocus = valueOrNull(onFocus),
    )
)

/**
 * @param background Background of an element when it is in focus. It can contain multiple layers.
 * @param border Border of an element when it's in focus.
 * @param nextFocusIds IDs of elements that will be next to get focus.
 * @param onBlur Actions when an element loses focus.
 * @param onFocus Actions when an element gets focus.
 */
@Generated
fun DivScope.focusProps(
    `use named arguments`: Guard = Guard.instance,
    background: List<Background>? = null,
    border: Border? = null,
    nextFocusIds: Focus.NextFocusIds? = null,
    onBlur: List<Action>? = null,
    onFocus: List<Action>? = null,
) = Focus.Properties(
    background = valueOrNull(background),
    border = valueOrNull(border),
    nextFocusIds = valueOrNull(nextFocusIds),
    onBlur = valueOrNull(onBlur),
    onFocus = valueOrNull(onFocus),
)

/**
 * @param background Background of an element when it is in focus. It can contain multiple layers.
 * @param border Border of an element when it's in focus.
 * @param nextFocusIds IDs of elements that will be next to get focus.
 * @param onBlur Actions when an element loses focus.
 * @param onFocus Actions when an element gets focus.
 */
@Generated
fun TemplateScope.focusRefs(
    `use named arguments`: Guard = Guard.instance,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    nextFocusIds: ReferenceProperty<Focus.NextFocusIds>? = null,
    onBlur: ReferenceProperty<List<Action>>? = null,
    onFocus: ReferenceProperty<List<Action>>? = null,
) = Focus.Properties(
    background = background,
    border = border,
    nextFocusIds = nextFocusIds,
    onBlur = onBlur,
    onFocus = onFocus,
)

/**
 * @param background Background of an element when it is in focus. It can contain multiple layers.
 * @param border Border of an element when it's in focus.
 * @param nextFocusIds IDs of elements that will be next to get focus.
 * @param onBlur Actions when an element loses focus.
 * @param onFocus Actions when an element gets focus.
 */
@Generated
fun Focus.override(
    `use named arguments`: Guard = Guard.instance,
    background: List<Background>? = null,
    border: Border? = null,
    nextFocusIds: Focus.NextFocusIds? = null,
    onBlur: List<Action>? = null,
    onFocus: List<Action>? = null,
): Focus = Focus(
    Focus.Properties(
        background = valueOrNull(background) ?: properties.background,
        border = valueOrNull(border) ?: properties.border,
        nextFocusIds = valueOrNull(nextFocusIds) ?: properties.nextFocusIds,
        onBlur = valueOrNull(onBlur) ?: properties.onBlur,
        onFocus = valueOrNull(onFocus) ?: properties.onFocus,
    )
)

/**
 * @param background Background of an element when it is in focus. It can contain multiple layers.
 * @param border Border of an element when it's in focus.
 * @param nextFocusIds IDs of elements that will be next to get focus.
 * @param onBlur Actions when an element loses focus.
 * @param onFocus Actions when an element gets focus.
 */
@Generated
fun Focus.defer(
    `use named arguments`: Guard = Guard.instance,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    nextFocusIds: ReferenceProperty<Focus.NextFocusIds>? = null,
    onBlur: ReferenceProperty<List<Action>>? = null,
    onFocus: ReferenceProperty<List<Action>>? = null,
): Focus = Focus(
    Focus.Properties(
        background = background ?: properties.background,
        border = border ?: properties.border,
        nextFocusIds = nextFocusIds ?: properties.nextFocusIds,
        onBlur = onBlur ?: properties.onBlur,
        onFocus = onFocus ?: properties.onFocus,
    )
)

/**
 * @param background Background of an element when it is in focus. It can contain multiple layers.
 * @param border Border of an element when it's in focus.
 * @param nextFocusIds IDs of elements that will be next to get focus.
 * @param onBlur Actions when an element loses focus.
 * @param onFocus Actions when an element gets focus.
 */
@Generated
fun Focus.modify(
    `use named arguments`: Guard = Guard.instance,
    background: Property<List<Background>>? = null,
    border: Property<Border>? = null,
    nextFocusIds: Property<Focus.NextFocusIds>? = null,
    onBlur: Property<List<Action>>? = null,
    onFocus: Property<List<Action>>? = null,
): Focus = Focus(
    Focus.Properties(
        background = background ?: properties.background,
        border = border ?: properties.border,
        nextFocusIds = nextFocusIds ?: properties.nextFocusIds,
        onBlur = onBlur ?: properties.onBlur,
        onFocus = onFocus ?: properties.onFocus,
    )
)

@Generated
fun Focus.asList() = listOf(this)

@Generated
fun DivScope.focusNextFocusIds(
    `use named arguments`: Guard = Guard.instance,
    down: String? = null,
    forward: String? = null,
    left: String? = null,
    right: String? = null,
    up: String? = null,
): Focus.NextFocusIds = Focus.NextFocusIds(
    Focus.NextFocusIds.Properties(
        down = valueOrNull(down),
        forward = valueOrNull(forward),
        left = valueOrNull(left),
        right = valueOrNull(right),
        up = valueOrNull(up),
    )
)

@Generated
fun DivScope.focusNextFocusIdsProps(
    `use named arguments`: Guard = Guard.instance,
    down: String? = null,
    forward: String? = null,
    left: String? = null,
    right: String? = null,
    up: String? = null,
) = Focus.NextFocusIds.Properties(
    down = valueOrNull(down),
    forward = valueOrNull(forward),
    left = valueOrNull(left),
    right = valueOrNull(right),
    up = valueOrNull(up),
)

@Generated
fun TemplateScope.focusNextFocusIdsRefs(
    `use named arguments`: Guard = Guard.instance,
    down: ReferenceProperty<String>? = null,
    forward: ReferenceProperty<String>? = null,
    left: ReferenceProperty<String>? = null,
    right: ReferenceProperty<String>? = null,
    up: ReferenceProperty<String>? = null,
) = Focus.NextFocusIds.Properties(
    down = down,
    forward = forward,
    left = left,
    right = right,
    up = up,
)

@Generated
fun Focus.NextFocusIds.override(
    `use named arguments`: Guard = Guard.instance,
    down: String? = null,
    forward: String? = null,
    left: String? = null,
    right: String? = null,
    up: String? = null,
): Focus.NextFocusIds = Focus.NextFocusIds(
    Focus.NextFocusIds.Properties(
        down = valueOrNull(down) ?: properties.down,
        forward = valueOrNull(forward) ?: properties.forward,
        left = valueOrNull(left) ?: properties.left,
        right = valueOrNull(right) ?: properties.right,
        up = valueOrNull(up) ?: properties.up,
    )
)

@Generated
fun Focus.NextFocusIds.defer(
    `use named arguments`: Guard = Guard.instance,
    down: ReferenceProperty<String>? = null,
    forward: ReferenceProperty<String>? = null,
    left: ReferenceProperty<String>? = null,
    right: ReferenceProperty<String>? = null,
    up: ReferenceProperty<String>? = null,
): Focus.NextFocusIds = Focus.NextFocusIds(
    Focus.NextFocusIds.Properties(
        down = down ?: properties.down,
        forward = forward ?: properties.forward,
        left = left ?: properties.left,
        right = right ?: properties.right,
        up = up ?: properties.up,
    )
)

@Generated
fun Focus.NextFocusIds.modify(
    `use named arguments`: Guard = Guard.instance,
    down: Property<String>? = null,
    forward: Property<String>? = null,
    left: Property<String>? = null,
    right: Property<String>? = null,
    up: Property<String>? = null,
): Focus.NextFocusIds = Focus.NextFocusIds(
    Focus.NextFocusIds.Properties(
        down = down ?: properties.down,
        forward = forward ?: properties.forward,
        left = left ?: properties.left,
        right = right ?: properties.right,
        up = up ?: properties.up,
    )
)

@Generated
fun Focus.NextFocusIds.evaluate(
    `use named arguments`: Guard = Guard.instance,
    down: ExpressionProperty<String>? = null,
    forward: ExpressionProperty<String>? = null,
    left: ExpressionProperty<String>? = null,
    right: ExpressionProperty<String>? = null,
    up: ExpressionProperty<String>? = null,
): Focus.NextFocusIds = Focus.NextFocusIds(
    Focus.NextFocusIds.Properties(
        down = down ?: properties.down,
        forward = forward ?: properties.forward,
        left = left ?: properties.left,
        right = right ?: properties.right,
        up = up ?: properties.up,
    )
)

@Generated
fun Focus.NextFocusIds.asList() = listOf(this)
