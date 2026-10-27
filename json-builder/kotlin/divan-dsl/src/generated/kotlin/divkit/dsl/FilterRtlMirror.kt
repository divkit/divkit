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
 * Mirrors an image if the system uses RTL (Right-to-Left) text direction.
 * 
 * Can be created using the method [filterRtlMirror].
 * 
 * Required parameters: `type`.
 */
@Generated
@JsonSerialize(using = FilterRtlMirrorSerializer::class)
data object FilterRtlMirror : Filter

@Generated
fun DivScope.filterRtlMirror(): FilterRtlMirror = FilterRtlMirror

@Generated
fun FilterRtlMirror.asList() = listOf(this)
