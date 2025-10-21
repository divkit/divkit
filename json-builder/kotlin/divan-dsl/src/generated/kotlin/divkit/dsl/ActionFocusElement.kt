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
 * Requests focus for an element. May require a user action on the web.
 * 
 * Can be created using the method [actionFocusElement].
 * 
 * Required parameters: `type, element_id`.
 */
@Generated
@ExposedCopyVisibility
data class ActionFocusElement internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "focus_element")
    )

    operator fun plus(additive: Properties): ActionFocusElement = ActionFocusElement(
        Properties(
            elementId = additive.elementId ?: properties.elementId,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        val elementId: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("element_id", elementId)
            return result
        }
    }
}

@Generated
fun DivScope.actionFocusElement(
    `use named arguments`: Guard = Guard.instance,
    elementId: String? = null,
): ActionFocusElement = ActionFocusElement(
    ActionFocusElement.Properties(
        elementId = valueOrNull(elementId),
    )
)

@Generated
fun DivScope.actionFocusElementProps(
    `use named arguments`: Guard = Guard.instance,
    elementId: String? = null,
) = ActionFocusElement.Properties(
    elementId = valueOrNull(elementId),
)

@Generated
fun TemplateScope.actionFocusElementRefs(
    `use named arguments`: Guard = Guard.instance,
    elementId: ReferenceProperty<String>? = null,
) = ActionFocusElement.Properties(
    elementId = elementId,
)

@Generated
fun ActionFocusElement.override(
    `use named arguments`: Guard = Guard.instance,
    elementId: String? = null,
): ActionFocusElement = ActionFocusElement(
    ActionFocusElement.Properties(
        elementId = valueOrNull(elementId) ?: properties.elementId,
    )
)

@Generated
fun ActionFocusElement.defer(
    `use named arguments`: Guard = Guard.instance,
    elementId: ReferenceProperty<String>? = null,
): ActionFocusElement = ActionFocusElement(
    ActionFocusElement.Properties(
        elementId = elementId ?: properties.elementId,
    )
)

@Generated
fun ActionFocusElement.modify(
    `use named arguments`: Guard = Guard.instance,
    elementId: Property<String>? = null,
): ActionFocusElement = ActionFocusElement(
    ActionFocusElement.Properties(
        elementId = elementId ?: properties.elementId,
    )
)

@Generated
fun ActionFocusElement.evaluate(
    `use named arguments`: Guard = Guard.instance,
    elementId: ExpressionProperty<String>? = null,
): ActionFocusElement = ActionFocusElement(
    ActionFocusElement.Properties(
        elementId = elementId ?: properties.elementId,
    )
)

@Generated
fun ActionFocusElement.asList() = listOf(this)
