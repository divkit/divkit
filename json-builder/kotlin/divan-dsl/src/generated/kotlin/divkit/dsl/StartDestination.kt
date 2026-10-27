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
 * Specifies the start of the container as the scrolling end position.
 * 
 * Can be created using the method [startDestination].
 * 
 * Required parameters: `type`.
 */
@Generated
@JsonSerialize(using = StartDestinationSerializer::class)
data object StartDestination : ActionScrollDestination

@Generated
fun DivScope.startDestination(): StartDestination = StartDestination

@Generated
fun StartDestination.asList() = listOf(this)
