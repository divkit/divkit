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
 * Hides the tooltip.
 * 
 * Can be created using the method [actionHideTooltip].
 * 
 * Required parameters: `type, id`.
 */
@Generated
@ExposedCopyVisibility
data class ActionHideTooltip internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "hide_tooltip")
    )

    operator fun plus(additive: Properties): ActionHideTooltip = ActionHideTooltip(
        Properties(
            id = additive.id ?: properties.id,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Tooltip ID.
         */
        val id: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("id", id)
            return result
        }
    }
}

/**
 * @param id Tooltip ID.
 */
@Generated
fun DivScope.actionHideTooltip(
    `use named arguments`: Guard = Guard.instance,
    id: String? = null,
): ActionHideTooltip = ActionHideTooltip(
    ActionHideTooltip.Properties(
        id = valueOrNull(id),
    )
)

/**
 * @param id Tooltip ID.
 */
@Generated
fun DivScope.actionHideTooltipProps(
    `use named arguments`: Guard = Guard.instance,
    id: String? = null,
) = ActionHideTooltip.Properties(
    id = valueOrNull(id),
)

/**
 * @param id Tooltip ID.
 */
@Generated
fun TemplateScope.actionHideTooltipRefs(
    `use named arguments`: Guard = Guard.instance,
    id: ReferenceProperty<String>? = null,
) = ActionHideTooltip.Properties(
    id = id,
)

/**
 * @param id Tooltip ID.
 */
@Generated
fun ActionHideTooltip.override(
    `use named arguments`: Guard = Guard.instance,
    id: String? = null,
): ActionHideTooltip = ActionHideTooltip(
    ActionHideTooltip.Properties(
        id = valueOrNull(id) ?: properties.id,
    )
)

/**
 * @param id Tooltip ID.
 */
@Generated
fun ActionHideTooltip.defer(
    `use named arguments`: Guard = Guard.instance,
    id: ReferenceProperty<String>? = null,
): ActionHideTooltip = ActionHideTooltip(
    ActionHideTooltip.Properties(
        id = id ?: properties.id,
    )
)

/**
 * @param id Tooltip ID.
 */
@Generated
fun ActionHideTooltip.modify(
    `use named arguments`: Guard = Guard.instance,
    id: Property<String>? = null,
): ActionHideTooltip = ActionHideTooltip(
    ActionHideTooltip.Properties(
        id = id ?: properties.id,
    )
)

/**
 * @param id Tooltip ID.
 */
@Generated
fun ActionHideTooltip.evaluate(
    `use named arguments`: Guard = Guard.instance,
    id: ExpressionProperty<String>? = null,
): ActionHideTooltip = ActionHideTooltip(
    ActionHideTooltip.Properties(
        id = id ?: properties.id,
    )
)

@Generated
fun ActionHideTooltip.asList() = listOf(this)
