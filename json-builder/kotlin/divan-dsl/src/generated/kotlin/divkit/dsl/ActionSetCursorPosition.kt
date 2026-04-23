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
 * Sets the cursor position in the specified input field.
 * 
 * Can be created using the method [actionSetCursorPosition].
 * 
 * Required parameters: `type, position, id`.
 */
@Generated
@ExposedCopyVisibility
data class ActionSetCursorPosition internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "set_cursor_position")
    )

    operator fun plus(additive: Properties): ActionSetCursorPosition = ActionSetCursorPosition(
        Properties(
            id = additive.id ?: properties.id,
            position = additive.position ?: properties.position,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * ID of the input field.
         */
        val id: Property<String>?,
        /**
         * Defines the cursor position. If `end` is not set, or if `end` is equal to `start`, it will just be the cursor position, if both are set and the values are different, part of the text will be selected. Use 0 to indicate the beginning of the text, -1 for the end.
         */
        val position: Property<Position>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("id", id)
            result.tryPutProperty("position", position)
            return result
        }
    }

    /**
     * Defines the cursor position. If `end` is not set, or if `end` is equal to `start`, it will just be the cursor position, if both are set and the values are different, part of the text will be selected. Use 0 to indicate the beginning of the text, -1 for the end.
     * 
     * Can be created using the method [actionSetCursorPositionPosition].
     * 
     * Required parameters: `type, start`.
     */
    @Generated
    @ExposedCopyVisibility
    data class Position internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
            mapOf("type" to "absolute")
        )

        operator fun plus(additive: Properties): Position = Position(
            Properties(
                end = additive.end ?: properties.end,
                start = additive.start ?: properties.start,
            )
        )

        @ExposedCopyVisibility
        data class Properties internal constructor(
            val end: Property<Int>?,
            val start: Property<Int>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("end", end)
                result.tryPutProperty("start", start)
                return result
            }
        }
    }

}

/**
 * @param id ID of the input field.
 * @param position Defines the cursor position. If `end` is not set, or if `end` is equal to `start`, it will just be the cursor position, if both are set and the values are different, part of the text will be selected. Use 0 to indicate the beginning of the text, -1 for the end.
 */
@Generated
fun DivScope.actionSetCursorPosition(
    `use named arguments`: Guard = Guard.instance,
    id: String? = null,
    position: ActionSetCursorPosition.Position? = null,
): ActionSetCursorPosition = ActionSetCursorPosition(
    ActionSetCursorPosition.Properties(
        id = valueOrNull(id),
        position = valueOrNull(position),
    )
)

/**
 * @param id ID of the input field.
 * @param position Defines the cursor position. If `end` is not set, or if `end` is equal to `start`, it will just be the cursor position, if both are set and the values are different, part of the text will be selected. Use 0 to indicate the beginning of the text, -1 for the end.
 */
@Generated
fun DivScope.actionSetCursorPositionProps(
    `use named arguments`: Guard = Guard.instance,
    id: String? = null,
    position: ActionSetCursorPosition.Position? = null,
) = ActionSetCursorPosition.Properties(
    id = valueOrNull(id),
    position = valueOrNull(position),
)

/**
 * @param id ID of the input field.
 * @param position Defines the cursor position. If `end` is not set, or if `end` is equal to `start`, it will just be the cursor position, if both are set and the values are different, part of the text will be selected. Use 0 to indicate the beginning of the text, -1 for the end.
 */
@Generated
fun TemplateScope.actionSetCursorPositionRefs(
    `use named arguments`: Guard = Guard.instance,
    id: ReferenceProperty<String>? = null,
    position: ReferenceProperty<ActionSetCursorPosition.Position>? = null,
) = ActionSetCursorPosition.Properties(
    id = id,
    position = position,
)

/**
 * @param id ID of the input field.
 * @param position Defines the cursor position. If `end` is not set, or if `end` is equal to `start`, it will just be the cursor position, if both are set and the values are different, part of the text will be selected. Use 0 to indicate the beginning of the text, -1 for the end.
 */
@Generated
fun ActionSetCursorPosition.override(
    `use named arguments`: Guard = Guard.instance,
    id: String? = null,
    position: ActionSetCursorPosition.Position? = null,
): ActionSetCursorPosition = ActionSetCursorPosition(
    ActionSetCursorPosition.Properties(
        id = valueOrNull(id) ?: properties.id,
        position = valueOrNull(position) ?: properties.position,
    )
)

/**
 * @param id ID of the input field.
 * @param position Defines the cursor position. If `end` is not set, or if `end` is equal to `start`, it will just be the cursor position, if both are set and the values are different, part of the text will be selected. Use 0 to indicate the beginning of the text, -1 for the end.
 */
@Generated
fun ActionSetCursorPosition.defer(
    `use named arguments`: Guard = Guard.instance,
    id: ReferenceProperty<String>? = null,
    position: ReferenceProperty<ActionSetCursorPosition.Position>? = null,
): ActionSetCursorPosition = ActionSetCursorPosition(
    ActionSetCursorPosition.Properties(
        id = id ?: properties.id,
        position = position ?: properties.position,
    )
)

/**
 * @param id ID of the input field.
 * @param position Defines the cursor position. If `end` is not set, or if `end` is equal to `start`, it will just be the cursor position, if both are set and the values are different, part of the text will be selected. Use 0 to indicate the beginning of the text, -1 for the end.
 */
@Generated
fun ActionSetCursorPosition.modify(
    `use named arguments`: Guard = Guard.instance,
    id: Property<String>? = null,
    position: Property<ActionSetCursorPosition.Position>? = null,
): ActionSetCursorPosition = ActionSetCursorPosition(
    ActionSetCursorPosition.Properties(
        id = id ?: properties.id,
        position = position ?: properties.position,
    )
)

/**
 * @param id ID of the input field.
 */
@Generated
fun ActionSetCursorPosition.evaluate(
    `use named arguments`: Guard = Guard.instance,
    id: ExpressionProperty<String>? = null,
): ActionSetCursorPosition = ActionSetCursorPosition(
    ActionSetCursorPosition.Properties(
        id = id ?: properties.id,
        position = properties.position,
    )
)

@Generated
fun ActionSetCursorPosition.asList() = listOf(this)

@Generated
fun DivScope.actionSetCursorPositionPosition(
    `use named arguments`: Guard = Guard.instance,
    end: Int? = null,
    start: Int? = null,
): ActionSetCursorPosition.Position = ActionSetCursorPosition.Position(
    ActionSetCursorPosition.Position.Properties(
        end = valueOrNull(end),
        start = valueOrNull(start),
    )
)

@Generated
fun DivScope.actionSetCursorPositionPositionProps(
    `use named arguments`: Guard = Guard.instance,
    end: Int? = null,
    start: Int? = null,
) = ActionSetCursorPosition.Position.Properties(
    end = valueOrNull(end),
    start = valueOrNull(start),
)

@Generated
fun TemplateScope.actionSetCursorPositionPositionRefs(
    `use named arguments`: Guard = Guard.instance,
    end: ReferenceProperty<Int>? = null,
    start: ReferenceProperty<Int>? = null,
) = ActionSetCursorPosition.Position.Properties(
    end = end,
    start = start,
)

@Generated
fun ActionSetCursorPosition.Position.override(
    `use named arguments`: Guard = Guard.instance,
    end: Int? = null,
    start: Int? = null,
): ActionSetCursorPosition.Position = ActionSetCursorPosition.Position(
    ActionSetCursorPosition.Position.Properties(
        end = valueOrNull(end) ?: properties.end,
        start = valueOrNull(start) ?: properties.start,
    )
)

@Generated
fun ActionSetCursorPosition.Position.defer(
    `use named arguments`: Guard = Guard.instance,
    end: ReferenceProperty<Int>? = null,
    start: ReferenceProperty<Int>? = null,
): ActionSetCursorPosition.Position = ActionSetCursorPosition.Position(
    ActionSetCursorPosition.Position.Properties(
        end = end ?: properties.end,
        start = start ?: properties.start,
    )
)

@Generated
fun ActionSetCursorPosition.Position.modify(
    `use named arguments`: Guard = Guard.instance,
    end: Property<Int>? = null,
    start: Property<Int>? = null,
): ActionSetCursorPosition.Position = ActionSetCursorPosition.Position(
    ActionSetCursorPosition.Position.Properties(
        end = end ?: properties.end,
        start = start ?: properties.start,
    )
)

@Generated
fun ActionSetCursorPosition.Position.evaluate(
    `use named arguments`: Guard = Guard.instance,
    end: ExpressionProperty<Int>? = null,
    start: ExpressionProperty<Int>? = null,
): ActionSetCursorPosition.Position = ActionSetCursorPosition.Position(
    ActionSetCursorPosition.Position.Properties(
        end = end ?: properties.end,
        start = start ?: properties.start,
    )
)

@Generated
fun ActionSetCursorPosition.Position.asList() = listOf(this)
