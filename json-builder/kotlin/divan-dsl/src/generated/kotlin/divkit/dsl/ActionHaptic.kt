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
 * Plays one haptic feedback effect when the action is executed. Respects system settings. If the effect is unavailable, it is skipped without stopping other actions.
 * 
 * Can be created using the method [actionHaptic].
 * 
 * Required parameters: `type`.
 */
@Generated
@ExposedCopyVisibility
data class ActionHaptic internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "haptic")
    )

    operator fun plus(additive: Properties): ActionHaptic = ActionHaptic(
        Properties(
            feedback = additive.feedback ?: properties.feedback,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Feedback type: light, medium or heavy impact, or success or error notification. Defaults to light. The physical effect depends on the platform and device.
         * Default value: `light`.
         */
        val feedback: Property<Feedback>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("feedback", feedback)
            return result
        }
    }

    /**
     * Feedback type: light, medium or heavy impact, or success or error notification. Defaults to light. The physical effect depends on the platform and device.
     * 
     * Possible values: [light], [medium], [heavy], [success], [error].
     */
    @Generated
    sealed interface Feedback
}

/**
 * @param feedback Feedback type: light, medium or heavy impact, or success or error notification. Defaults to light. The physical effect depends on the platform and device.
 */
@Generated
fun DivScope.actionHaptic(
    `use named arguments`: Guard = Guard.instance,
    feedback: ActionHaptic.Feedback? = null,
): ActionHaptic = ActionHaptic(
    ActionHaptic.Properties(
        feedback = valueOrNull(feedback),
    )
)

/**
 * @param feedback Feedback type: light, medium or heavy impact, or success or error notification. Defaults to light. The physical effect depends on the platform and device.
 */
@Generated
fun DivScope.actionHapticProps(
    `use named arguments`: Guard = Guard.instance,
    feedback: ActionHaptic.Feedback? = null,
) = ActionHaptic.Properties(
    feedback = valueOrNull(feedback),
)

/**
 * @param feedback Feedback type: light, medium or heavy impact, or success or error notification. Defaults to light. The physical effect depends on the platform and device.
 */
@Generated
fun TemplateScope.actionHapticRefs(
    `use named arguments`: Guard = Guard.instance,
    feedback: ReferenceProperty<ActionHaptic.Feedback>? = null,
) = ActionHaptic.Properties(
    feedback = feedback,
)

/**
 * @param feedback Feedback type: light, medium or heavy impact, or success or error notification. Defaults to light. The physical effect depends on the platform and device.
 */
@Generated
fun ActionHaptic.override(
    `use named arguments`: Guard = Guard.instance,
    feedback: ActionHaptic.Feedback? = null,
): ActionHaptic = ActionHaptic(
    ActionHaptic.Properties(
        feedback = valueOrNull(feedback) ?: properties.feedback,
    )
)

/**
 * @param feedback Feedback type: light, medium or heavy impact, or success or error notification. Defaults to light. The physical effect depends on the platform and device.
 */
@Generated
fun ActionHaptic.defer(
    `use named arguments`: Guard = Guard.instance,
    feedback: ReferenceProperty<ActionHaptic.Feedback>? = null,
): ActionHaptic = ActionHaptic(
    ActionHaptic.Properties(
        feedback = feedback ?: properties.feedback,
    )
)

/**
 * @param feedback Feedback type: light, medium or heavy impact, or success or error notification. Defaults to light. The physical effect depends on the platform and device.
 */
@Generated
fun ActionHaptic.modify(
    `use named arguments`: Guard = Guard.instance,
    feedback: Property<ActionHaptic.Feedback>? = null,
): ActionHaptic = ActionHaptic(
    ActionHaptic.Properties(
        feedback = feedback ?: properties.feedback,
    )
)

/**
 * @param feedback Feedback type: light, medium or heavy impact, or success or error notification. Defaults to light. The physical effect depends on the platform and device.
 */
@Generated
fun ActionHaptic.evaluate(
    `use named arguments`: Guard = Guard.instance,
    feedback: ExpressionProperty<ActionHaptic.Feedback>? = null,
): ActionHaptic = ActionHaptic(
    ActionHaptic.Properties(
        feedback = feedback ?: properties.feedback,
    )
)

@Generated
fun ActionHaptic.asList() = listOf(this)

@Generated
fun ActionHaptic.Feedback.asList() = listOf(this)
