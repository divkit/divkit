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
 * Runs an action for an extension declared on the target element. The action payload is passed to the extension handler.
 * 
 * Can be created using the method [actionExtensionAction].
 * 
 * Required parameters: `type, extension_id, div_id`.
 */
@Generated
@ExposedCopyVisibility
data class ActionExtensionAction internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "extension_action")
    )

    operator fun plus(additive: Properties): ActionExtensionAction = ActionExtensionAction(
        Properties(
            divId = additive.divId ?: properties.divId,
            extensionId = additive.extensionId ?: properties.extensionId,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * ID of the target element.
         */
        val divId: Property<String>?,
        /**
         * ID of the extension that handles the action.
         */
        val extensionId: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("div_id", divId)
            result.tryPutProperty("extension_id", extensionId)
            return result
        }
    }
}

/**
 * @param divId ID of the target element.
 * @param extensionId ID of the extension that handles the action.
 */
@Generated
fun DivScope.actionExtensionAction(
    `use named arguments`: Guard = Guard.instance,
    divId: String? = null,
    extensionId: String? = null,
): ActionExtensionAction = ActionExtensionAction(
    ActionExtensionAction.Properties(
        divId = valueOrNull(divId),
        extensionId = valueOrNull(extensionId),
    )
)

/**
 * @param divId ID of the target element.
 * @param extensionId ID of the extension that handles the action.
 */
@Generated
fun DivScope.actionExtensionActionProps(
    `use named arguments`: Guard = Guard.instance,
    divId: String? = null,
    extensionId: String? = null,
) = ActionExtensionAction.Properties(
    divId = valueOrNull(divId),
    extensionId = valueOrNull(extensionId),
)

/**
 * @param divId ID of the target element.
 * @param extensionId ID of the extension that handles the action.
 */
@Generated
fun TemplateScope.actionExtensionActionRefs(
    `use named arguments`: Guard = Guard.instance,
    divId: ReferenceProperty<String>? = null,
    extensionId: ReferenceProperty<String>? = null,
) = ActionExtensionAction.Properties(
    divId = divId,
    extensionId = extensionId,
)

/**
 * @param divId ID of the target element.
 * @param extensionId ID of the extension that handles the action.
 */
@Generated
fun ActionExtensionAction.override(
    `use named arguments`: Guard = Guard.instance,
    divId: String? = null,
    extensionId: String? = null,
): ActionExtensionAction = ActionExtensionAction(
    ActionExtensionAction.Properties(
        divId = valueOrNull(divId) ?: properties.divId,
        extensionId = valueOrNull(extensionId) ?: properties.extensionId,
    )
)

/**
 * @param divId ID of the target element.
 * @param extensionId ID of the extension that handles the action.
 */
@Generated
fun ActionExtensionAction.defer(
    `use named arguments`: Guard = Guard.instance,
    divId: ReferenceProperty<String>? = null,
    extensionId: ReferenceProperty<String>? = null,
): ActionExtensionAction = ActionExtensionAction(
    ActionExtensionAction.Properties(
        divId = divId ?: properties.divId,
        extensionId = extensionId ?: properties.extensionId,
    )
)

/**
 * @param divId ID of the target element.
 * @param extensionId ID of the extension that handles the action.
 */
@Generated
fun ActionExtensionAction.modify(
    `use named arguments`: Guard = Guard.instance,
    divId: Property<String>? = null,
    extensionId: Property<String>? = null,
): ActionExtensionAction = ActionExtensionAction(
    ActionExtensionAction.Properties(
        divId = divId ?: properties.divId,
        extensionId = extensionId ?: properties.extensionId,
    )
)

/**
 * @param divId ID of the target element.
 * @param extensionId ID of the extension that handles the action.
 */
@Generated
fun ActionExtensionAction.evaluate(
    `use named arguments`: Guard = Guard.instance,
    divId: ExpressionProperty<String>? = null,
    extensionId: ExpressionProperty<String>? = null,
): ActionExtensionAction = ActionExtensionAction(
    ActionExtensionAction.Properties(
        divId = divId ?: properties.divId,
        extensionId = extensionId ?: properties.extensionId,
    )
)

@Generated
fun ActionExtensionAction.asList() = listOf(this)
