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
 * Scrolls scrollable container from current position by `item_count` or by `offset`, if both provided scroll action will be combined, negative numbers associated with backward scroll.
 * 
 * Can be created using the method [actionScrollBy].
 * 
 * Required parameters: `type, id`.
 */
@Generated
class ActionScrollBy internal constructor(
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

    class Properties internal constructor(
        /**
         * If `true` (default value) scroll will be animated, else not.
         * Default value: `true`.
         */
        val animated: Property<Boolean>?,
        /**
         * Identifier of the view that is going to be manipulated.
         */
        val id: Property<String>?,
        /**
         * Count of container items to scroll, negative value is associated with backward scroll.
         * Default value: `0`.
         */
        val itemCount: Property<Int>?,
        /**
         * Distance to scroll measured in `dp` from current position, negative value is associated with backward scroll. Applicable only in `gallery`.
         * Default value: `0`.
         */
        val offset: Property<Int>?,
        /**
         * Specifies how navigation will occur when the boundary elements are reached:<li>`clamp` — Transition will stop at the boundary element (default value);</li><li>`ring` — Transition will be to the beginning or the end depending on the current element.</li>
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
     * Specifies how navigation will occur when the boundary elements are reached:<li>`clamp` — Transition will stop at the boundary element (default value);</li><li>`ring` — Transition will be to the beginning or the end depending on the current element.</li>
     * 
     * Possible values: [clamp], [ring].
     */
    @Generated
    sealed interface Overflow
}

/**
 * @param animated If `true` (default value) scroll will be animated, else not.
 * @param id Identifier of the view that is going to be manipulated.
 * @param itemCount Count of container items to scroll, negative value is associated with backward scroll.
 * @param offset Distance to scroll measured in `dp` from current position, negative value is associated with backward scroll. Applicable only in `gallery`.
 * @param overflow Specifies how navigation will occur when the boundary elements are reached:<li>`clamp` — Transition will stop at the boundary element (default value);</li><li>`ring` — Transition will be to the beginning or the end depending on the current element.</li>
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
 * @param animated If `true` (default value) scroll will be animated, else not.
 * @param id Identifier of the view that is going to be manipulated.
 * @param itemCount Count of container items to scroll, negative value is associated with backward scroll.
 * @param offset Distance to scroll measured in `dp` from current position, negative value is associated with backward scroll. Applicable only in `gallery`.
 * @param overflow Specifies how navigation will occur when the boundary elements are reached:<li>`clamp` — Transition will stop at the boundary element (default value);</li><li>`ring` — Transition will be to the beginning or the end depending on the current element.</li>
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
 * @param animated If `true` (default value) scroll will be animated, else not.
 * @param id Identifier of the view that is going to be manipulated.
 * @param itemCount Count of container items to scroll, negative value is associated with backward scroll.
 * @param offset Distance to scroll measured in `dp` from current position, negative value is associated with backward scroll. Applicable only in `gallery`.
 * @param overflow Specifies how navigation will occur when the boundary elements are reached:<li>`clamp` — Transition will stop at the boundary element (default value);</li><li>`ring` — Transition will be to the beginning or the end depending on the current element.</li>
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
 * @param animated If `true` (default value) scroll will be animated, else not.
 * @param id Identifier of the view that is going to be manipulated.
 * @param itemCount Count of container items to scroll, negative value is associated with backward scroll.
 * @param offset Distance to scroll measured in `dp` from current position, negative value is associated with backward scroll. Applicable only in `gallery`.
 * @param overflow Specifies how navigation will occur when the boundary elements are reached:<li>`clamp` — Transition will stop at the boundary element (default value);</li><li>`ring` — Transition will be to the beginning or the end depending on the current element.</li>
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
 * @param animated If `true` (default value) scroll will be animated, else not.
 * @param id Identifier of the view that is going to be manipulated.
 * @param itemCount Count of container items to scroll, negative value is associated with backward scroll.
 * @param offset Distance to scroll measured in `dp` from current position, negative value is associated with backward scroll. Applicable only in `gallery`.
 * @param overflow Specifies how navigation will occur when the boundary elements are reached:<li>`clamp` — Transition will stop at the boundary element (default value);</li><li>`ring` — Transition will be to the beginning or the end depending on the current element.</li>
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
 * @param animated If `true` (default value) scroll will be animated, else not.
 * @param id Identifier of the view that is going to be manipulated.
 * @param itemCount Count of container items to scroll, negative value is associated with backward scroll.
 * @param offset Distance to scroll measured in `dp` from current position, negative value is associated with backward scroll. Applicable only in `gallery`.
 * @param overflow Specifies how navigation will occur when the boundary elements are reached:<li>`clamp` — Transition will stop at the boundary element (default value);</li><li>`ring` — Transition will be to the beginning or the end depending on the current element.</li>
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
