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
 * Infinite number of repetitions.
 * 
 * Can be created using the method [infinityCount].
 * 
 * Required parameters: `type`.
 */
@Generated
data object InfinityCount : Count {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = mapOf("type" to "infinity")
}

@Generated
fun DivScope.infinityCount(): InfinityCount = InfinityCount

@Generated
fun InfinityCount.asList() = listOf(this)
