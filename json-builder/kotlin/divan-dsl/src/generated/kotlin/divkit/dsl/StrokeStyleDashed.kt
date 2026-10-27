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
 * Dashed stroke style.
 * 
 * Can be created using the method [strokeStyleDashed].
 * 
 * Required parameters: `type`.
 */
@Generated
@JsonSerialize(using = StrokeStyleDashedSerializer::class)
data object StrokeStyleDashed : StrokeStyle

@Generated
fun DivScope.strokeStyleDashed(): StrokeStyleDashed = StrokeStyleDashed

@Generated
fun StrokeStyleDashed.asList() = listOf(this)
