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
 * Blend mode.
 * 
 * Possible values: [source_in], [source_atop], [darken], [lighten], [multiply], [screen].
 */
@Generated
sealed interface BlendMode

@Generated
fun BlendMode.asList() = listOf(this)
