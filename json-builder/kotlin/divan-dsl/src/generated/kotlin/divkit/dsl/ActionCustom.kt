@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import divkit.dsl.serialization.*
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
@JsonSerialize(using = ActionCustomSerializer::class)
data object ActionCustom : ActionTyped

@Generated
fun DivScope.actionCustom(): ActionCustom = ActionCustom

@Generated
fun ActionCustom.asList() = listOf(this)
