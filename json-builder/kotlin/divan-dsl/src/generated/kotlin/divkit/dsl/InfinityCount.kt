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
 * Infinite number of repetitions.
 * 
 * Can be created using the method [infinityCount].
 * 
 * Required parameters: `type`.
 */
@Generated
@JsonSerialize(using = InfinityCountSerializer::class)
data object InfinityCount : Count

@Generated
fun DivScope.infinityCount(): InfinityCount = InfinityCount

@Generated
fun InfinityCount.asList() = listOf(this)
