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
 * Stops specified animator
 * 
 * Can be created using the method [actionAnimatorStop].
 * 
 * Required parameters: `type, animator_id`.
 */
@Generated
class ActionAnimatorStop internal constructor(
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

    class Properties internal constructor(
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

@Generated
fun DivScope.actionAnimatorStop(
    `use named arguments`: Guard = Guard.instance,
    animatorId: String? = null,
): ActionAnimatorStop = ActionAnimatorStop(
    ActionAnimatorStop.Properties(
        animatorId = valueOrNull(animatorId),
    )
)

@Generated
fun DivScope.actionAnimatorStopProps(
    `use named arguments`: Guard = Guard.instance,
    animatorId: String? = null,
) = ActionAnimatorStop.Properties(
    animatorId = valueOrNull(animatorId),
)

@Generated
fun TemplateScope.actionAnimatorStopRefs(
    `use named arguments`: Guard = Guard.instance,
    animatorId: ReferenceProperty<String>? = null,
) = ActionAnimatorStop.Properties(
    animatorId = animatorId,
)

@Generated
fun ActionAnimatorStop.override(
    `use named arguments`: Guard = Guard.instance,
    animatorId: String? = null,
): ActionAnimatorStop = ActionAnimatorStop(
    ActionAnimatorStop.Properties(
        animatorId = valueOrNull(animatorId) ?: properties.animatorId,
    )
)

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
fun ActionAnimatorStop.evaluate(
    `use named arguments`: Guard = Guard.instance,
    animatorId: ExpressionProperty<String>? = null,
): ActionAnimatorStop = ActionAnimatorStop(
    ActionAnimatorStop.Properties(
        animatorId = animatorId ?: properties.animatorId,
    )
)

@Generated
fun ActionAnimatorStop.asList() = listOf(this)
