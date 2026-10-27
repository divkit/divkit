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
 * Specifies the end of the container as the scrolling end position.
 * 
 * Can be created using the method [endDestination].
 * 
 * Required parameters: `type`.
 */
@Generated
@JsonSerialize(using = EndDestinationSerializer::class)
data object EndDestination : ActionScrollDestination

@Generated
fun DivScope.endDestination(): EndDestination = EndDestination

@Generated
fun EndDestination.asList() = listOf(this)
