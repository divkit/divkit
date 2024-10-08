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
 * Specifies container's end as scroll destination.
 * 
 * Can be created using the method [endDestination].
 * 
 * Required parameters: `type`.
 */
@Generated
data object EndDestination : ActionScrollDestination {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = mapOf("type" to "end")
}

@Generated
fun DivScope.endDestination(): EndDestination = EndDestination

@Generated
fun EndDestination.asList() = listOf(this)
