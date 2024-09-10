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
 * Shows tooltip.
 * 
 * Can be created using the method [actionShowTooltip].
 * 
 * Required parameters: `type, id`.
 */
@Generated
class ActionShowTooltip internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "show_tooltip")
    )

    operator fun plus(additive: Properties): ActionShowTooltip = ActionShowTooltip(
        Properties(
            id = additive.id ?: properties.id,
            multiple = additive.multiple ?: properties.multiple,
        )
    )

    class Properties internal constructor(
        /**
         * Tooltip identifier.
         */
        val id: Property<String>?,
        /**
         * Defines whether tooltip can be shown again after being closed.
         */
        val multiple: Property<Boolean>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("id", id)
            result.tryPutProperty("multiple", multiple)
            return result
        }
    }
}

/**
 * @param id Tooltip identifier.
 * @param multiple Defines whether tooltip can be shown again after being closed.
 */
@Generated
fun DivScope.actionShowTooltip(
    `use named arguments`: Guard = Guard.instance,
    id: String? = null,
    multiple: Boolean? = null,
): ActionShowTooltip = ActionShowTooltip(
    ActionShowTooltip.Properties(
        id = valueOrNull(id),
        multiple = valueOrNull(multiple),
    )
)

/**
 * @param id Tooltip identifier.
 * @param multiple Defines whether tooltip can be shown again after being closed.
 */
@Generated
fun DivScope.actionShowTooltipProps(
    `use named arguments`: Guard = Guard.instance,
    id: String? = null,
    multiple: Boolean? = null,
) = ActionShowTooltip.Properties(
    id = valueOrNull(id),
    multiple = valueOrNull(multiple),
)

/**
 * @param id Tooltip identifier.
 * @param multiple Defines whether tooltip can be shown again after being closed.
 */
@Generated
fun TemplateScope.actionShowTooltipRefs(
    `use named arguments`: Guard = Guard.instance,
    id: ReferenceProperty<String>? = null,
    multiple: ReferenceProperty<Boolean>? = null,
) = ActionShowTooltip.Properties(
    id = id,
    multiple = multiple,
)

/**
 * @param id Tooltip identifier.
 * @param multiple Defines whether tooltip can be shown again after being closed.
 */
@Generated
fun ActionShowTooltip.override(
    `use named arguments`: Guard = Guard.instance,
    id: String? = null,
    multiple: Boolean? = null,
): ActionShowTooltip = ActionShowTooltip(
    ActionShowTooltip.Properties(
        id = valueOrNull(id) ?: properties.id,
        multiple = valueOrNull(multiple) ?: properties.multiple,
    )
)

/**
 * @param id Tooltip identifier.
 * @param multiple Defines whether tooltip can be shown again after being closed.
 */
@Generated
fun ActionShowTooltip.defer(
    `use named arguments`: Guard = Guard.instance,
    id: ReferenceProperty<String>? = null,
    multiple: ReferenceProperty<Boolean>? = null,
): ActionShowTooltip = ActionShowTooltip(
    ActionShowTooltip.Properties(
        id = id ?: properties.id,
        multiple = multiple ?: properties.multiple,
    )
)

/**
 * @param id Tooltip identifier.
 * @param multiple Defines whether tooltip can be shown again after being closed.
 */
@Generated
fun ActionShowTooltip.evaluate(
    `use named arguments`: Guard = Guard.instance,
    id: ExpressionProperty<String>? = null,
    multiple: ExpressionProperty<Boolean>? = null,
): ActionShowTooltip = ActionShowTooltip(
    ActionShowTooltip.Properties(
        id = id ?: properties.id,
        multiple = multiple ?: properties.multiple,
    )
)

@Generated
fun ActionShowTooltip.asList() = listOf(this)
