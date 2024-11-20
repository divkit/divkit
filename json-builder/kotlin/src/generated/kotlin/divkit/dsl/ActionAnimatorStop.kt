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
 * Stops the specified animator.
 * 
 * Can be created using the method [actionAnimatorStop].
 * 
 * Required parameters: `type, animator_id`.
 */
@Generated
data class ActionAnimatorStop internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "animator_stop")
    )

    operator fun plus(additive: Properties): ActionAnimatorStop = ActionAnimatorStop(
        Properties(
            animatorId = additive.animatorId ?: properties.animatorId,
        )
    )

    data class Properties internal constructor(
        /**
         * ID of the animator to be stopped.
         */
        val animatorId: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("animator_id", animatorId)
            return result
        }
    }
}

/**
 * @param animatorId ID of the animator to be stopped.
 */
@Generated
fun DivScope.actionAnimatorStop(
    `use named arguments`: Guard = Guard.instance,
    animatorId: String? = null,
): ActionAnimatorStop = ActionAnimatorStop(
    ActionAnimatorStop.Properties(
        animatorId = valueOrNull(animatorId),
    )
)

/**
 * @param animatorId ID of the animator to be stopped.
 */
@Generated
fun DivScope.actionAnimatorStopProps(
    `use named arguments`: Guard = Guard.instance,
    animatorId: String? = null,
) = ActionAnimatorStop.Properties(
    animatorId = valueOrNull(animatorId),
)

/**
 * @param animatorId ID of the animator to be stopped.
 */
@Generated
fun TemplateScope.actionAnimatorStopRefs(
    `use named arguments`: Guard = Guard.instance,
    animatorId: ReferenceProperty<String>? = null,
) = ActionAnimatorStop.Properties(
    animatorId = animatorId,
)

/**
 * @param animatorId ID of the animator to be stopped.
 */
@Generated
fun ActionAnimatorStop.override(
    `use named arguments`: Guard = Guard.instance,
    animatorId: String? = null,
): ActionAnimatorStop = ActionAnimatorStop(
    ActionAnimatorStop.Properties(
        animatorId = valueOrNull(animatorId) ?: properties.animatorId,
    )
)

/**
 * @param animatorId ID of the animator to be stopped.
 */
@Generated
fun ActionAnimatorStop.defer(
    `use named arguments`: Guard = Guard.instance,
    animatorId: ReferenceProperty<String>? = null,
): ActionAnimatorStop = ActionAnimatorStop(
    ActionAnimatorStop.Properties(
        animatorId = animatorId ?: properties.animatorId,
    )
)

@Generated
fun ActionAnimatorStop.asList() = listOf(this)
