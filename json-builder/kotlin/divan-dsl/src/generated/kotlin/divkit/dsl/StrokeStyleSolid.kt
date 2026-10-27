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
 * Solid stroke style.
 * 
 * Can be created using the method [strokeStyleSolid].
 * 
 * Required parameters: `type`.
 */
@Generated
@JsonSerialize(using = StrokeStyleSolidSerializer::class)
data object StrokeStyleSolid : StrokeStyle

@Generated
fun DivScope.strokeStyleSolid(): StrokeStyleSolid = StrokeStyleSolid

@Generated
fun StrokeStyleSolid.asList() = listOf(this)
