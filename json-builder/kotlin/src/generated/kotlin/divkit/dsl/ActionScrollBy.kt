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
 * Scrolls the container by `item_count` or `offset` starting from the current position. If both values are specified, the action will be combined. For scrolling back, use negative values.
 * 
 * Can be created using the method [actionScrollBy].
 * 
 * Required parameters: `type, id`.
 */
@Generated
data class ActionScrollBy internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "scroll_by")
    )

    operator fun plus(additive: Properties): ActionScrollBy = ActionScrollBy(
        Properties(
            animated = additive.animated ?: properties.animated,
            id = additive.id ?: properties.id,
            itemCount = additive.itemCount ?: properties.itemCount,
            offset = additive.offset ?: properties.offset,
            overflow = additive.overflow ?: properties.overflow,
        )
    )

    data class Properties internal constructor(
        /**
         * Enables scrolling animation.
         * Default value: `true`.
         */
        val animated: Property<Boolean>?,
        /**
         * ID of the element where the action should be performed.
         */
        val id: Property<String>?,
        /**
         * Number of container elements to scroll through. For scrolling back, use negative values.
         * Default value: `0`.
         */
        val itemCount: Property<Int>?,
        /**
         * Scrolling distance measured in `dp` from the current position. For scrolling back, use negative values. Only applies in `gallery`.
         * Default value: `0`.
         */
        val offset: Property<Int>?,
        /**
         * Defines navigation behavior at boundary elements:<li>`clamp`: Stop navigation at the boundary element (default)</li><li>`ring`: Navigate to the start or end, depending on the current element.</li>
         * Default value: `clamp`.
         */
        val overflow: Property<Overflow>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("animated", animated)
            result.tryPutProperty("id", id)
            result.tryPutProperty("item_count", itemCount)
            result.tryPutProperty("offset", offset)
            result.tryPutProperty("overflow", overflow)
            return result
        }
    }

    /**
     * Defines navigation behavior at boundary elements:<li>`clamp`: Stop navigation at the boundary element (default)</li><li>`ring`: Navigate to the start or end, depending on the current element.</li>
     * 
     * Possible values: [clamp], [ring].
     */
    @Generated
    sealed interface Overflow
}

/**
 * @param animated Enables scrolling animation.
 * @param id ID of the element where the action should be performed.
 * @param itemCount Number of container elements to scroll through. For scrolling back, use negative values.
 * @param offset Scrolling distance measured in `dp` from the current position. For scrolling back, use negative values. Only applies in `gallery`.
 * @param overflow Defines navigation behavior at boundary elements:<li>`clamp`: Stop navigation at the boundary element (default)</li><li>`ring`: Navigate to the start or end, depending on the current element.</li>
 */
@Generated
fun DivScope.actionScrollBy(
    `use named arguments`: Guard = Guard.instance,
    animated: Boolean? = null,
    id: String? = null,
    itemCount: Int? = null,
    offset: Int? = null,
    overflow: ActionScrollBy.Overflow? = null,
): ActionScrollBy = ActionScrollBy(
    ActionScrollBy.Properties(
        animated = valueOrNull(animated),
        id = valueOrNull(id),
        itemCount = valueOrNull(itemCount),
        offset = valueOrNull(offset),
        overflow = valueOrNull(overflow),
    )
)

/**
 * @param animated Enables scrolling animation.
 * @param id ID of the element where the action should be performed.
 * @param itemCount Number of container elements to scroll through. For scrolling back, use negative values.
 * @param offset Scrolling distance measured in `dp` from the current position. For scrolling back, use negative values. Only applies in `gallery`.
 * @param overflow Defines navigation behavior at boundary elements:<li>`clamp`: Stop navigation at the boundary element (default)</li><li>`ring`: Navigate to the start or end, depending on the current element.</li>
 */
@Generated
fun DivScope.actionScrollByProps(
    `use named arguments`: Guard = Guard.instance,
    animated: Boolean? = null,
    id: String? = null,
    itemCount: Int? = null,
    offset: Int? = null,
    overflow: ActionScrollBy.Overflow? = null,
) = ActionScrollBy.Properties(
    animated = valueOrNull(animated),
    id = valueOrNull(id),
    itemCount = valueOrNull(itemCount),
    offset = valueOrNull(offset),
    overflow = valueOrNull(overflow),
)

/**
 * @param animated Enables scrolling animation.
 * @param id ID of the element where the action should be performed.
 * @param itemCount Number of container elements to scroll through. For scrolling back, use negative values.
 * @param offset Scrolling distance measured in `dp` from the current position. For scrolling back, use negative values. Only applies in `gallery`.
 * @param overflow Defines navigation behavior at boundary elements:<li>`clamp`: Stop navigation at the boundary element (default)</li><li>`ring`: Navigate to the start or end, depending on the current element.</li>
 */
@Generated
fun TemplateScope.actionScrollByRefs(
    `use named arguments`: Guard = Guard.instance,
    animated: ReferenceProperty<Boolean>? = null,
    id: ReferenceProperty<String>? = null,
    itemCount: ReferenceProperty<Int>? = null,
    offset: ReferenceProperty<Int>? = null,
    overflow: ReferenceProperty<ActionScrollBy.Overflow>? = null,
) = ActionScrollBy.Properties(
    animated = animated,
    id = id,
    itemCount = itemCount,
    offset = offset,
    overflow = overflow,
)

/**
 * @param animated Enables scrolling animation.
 * @param id ID of the element where the action should be performed.
 * @param itemCount Number of container elements to scroll through. For scrolling back, use negative values.
 * @param offset Scrolling distance measured in `dp` from the current position. For scrolling back, use negative values. Only applies in `gallery`.
 * @param overflow Defines navigation behavior at boundary elements:<li>`clamp`: Stop navigation at the boundary element (default)</li><li>`ring`: Navigate to the start or end, depending on the current element.</li>
 */
@Generated
fun ActionScrollBy.override(
    `use named arguments`: Guard = Guard.instance,
    animated: Boolean? = null,
    id: String? = null,
    itemCount: Int? = null,
    offset: Int? = null,
    overflow: ActionScrollBy.Overflow? = null,
): ActionScrollBy = ActionScrollBy(
    ActionScrollBy.Properties(
        animated = valueOrNull(animated) ?: properties.animated,
        id = valueOrNull(id) ?: properties.id,
        itemCount = valueOrNull(itemCount) ?: properties.itemCount,
        offset = valueOrNull(offset) ?: properties.offset,
        overflow = valueOrNull(overflow) ?: properties.overflow,
    )
)

/**
 * @param animated Enables scrolling animation.
 * @param id ID of the element where the action should be performed.
 * @param itemCount Number of container elements to scroll through. For scrolling back, use negative values.
 * @param offset Scrolling distance measured in `dp` from the current position. For scrolling back, use negative values. Only applies in `gallery`.
 * @param overflow Defines navigation behavior at boundary elements:<li>`clamp`: Stop navigation at the boundary element (default)</li><li>`ring`: Navigate to the start or end, depending on the current element.</li>
 */
@Generated
fun ActionScrollBy.defer(
    `use named arguments`: Guard = Guard.instance,
    animated: ReferenceProperty<Boolean>? = null,
    id: ReferenceProperty<String>? = null,
    itemCount: ReferenceProperty<Int>? = null,
    offset: ReferenceProperty<Int>? = null,
    overflow: ReferenceProperty<ActionScrollBy.Overflow>? = null,
): ActionScrollBy = ActionScrollBy(
    ActionScrollBy.Properties(
        animated = animated ?: properties.animated,
        id = id ?: properties.id,
        itemCount = itemCount ?: properties.itemCount,
        offset = offset ?: properties.offset,
        overflow = overflow ?: properties.overflow,
    )
)

/**
 * @param animated Enables scrolling animation.
 * @param id ID of the element where the action should be performed.
 * @param itemCount Number of container elements to scroll through. For scrolling back, use negative values.
 * @param offset Scrolling distance measured in `dp` from the current position. For scrolling back, use negative values. Only applies in `gallery`.
 * @param overflow Defines navigation behavior at boundary elements:<li>`clamp`: Stop navigation at the boundary element (default)</li><li>`ring`: Navigate to the start or end, depending on the current element.</li>
 */
@Generated
fun ActionScrollBy.evaluate(
    `use named arguments`: Guard = Guard.instance,
    animated: ExpressionProperty<Boolean>? = null,
    id: ExpressionProperty<String>? = null,
    itemCount: ExpressionProperty<Int>? = null,
    offset: ExpressionProperty<Int>? = null,
    overflow: ExpressionProperty<ActionScrollBy.Overflow>? = null,
): ActionScrollBy = ActionScrollBy(
    ActionScrollBy.Properties(
        animated = animated ?: properties.animated,
        id = id ?: properties.id,
        itemCount = itemCount ?: properties.itemCount,
        offset = offset ?: properties.offset,
        overflow = overflow ?: properties.overflow,
    )
)

@Generated
fun ActionScrollBy.asList() = listOf(this)

@Generated
fun ActionScrollBy.Overflow.asList() = listOf(this)
