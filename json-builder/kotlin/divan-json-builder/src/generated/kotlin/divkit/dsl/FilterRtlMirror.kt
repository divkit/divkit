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
 * Mirrors an image if the system uses RTL (Right-to-Left) text direction.
 * 
 * Can be created using the method [filterRtlMirror].
 * 
 * Required parameters: `type`.
 */
@Generated
data object FilterRtlMirror : Filter {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = mapOf("type" to "rtl_mirror")
}

@Generated
fun DivScope.filterRtlMirror(): FilterRtlMirror = FilterRtlMirror

@Generated
fun FilterRtlMirror.asList() = listOf(this)
