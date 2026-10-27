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
 * Type used for function arguments and return values.
 * 
 * Possible values: [string], [integer], [number], [boolean], [datetime], [color], [url], [dict], [array].
 */
@Generated
sealed interface EvaluableType

@Generated
fun EvaluableType.asList() = listOf(this)
