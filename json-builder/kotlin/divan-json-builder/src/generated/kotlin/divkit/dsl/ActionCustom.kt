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
 * Custom action. A handler is required on the host. The parameters can be passed via the action payload.
 * 
 * Can be created using the method [actionCustom].
 * 
 * Required parameters: `type`.
 */
@Generated
data object ActionCustom : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = mapOf("type" to "custom")
}

@Generated
fun DivScope.actionCustom(): ActionCustom = ActionCustom

@Generated
fun ActionCustom.asList() = listOf(this)
