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
 * Dashed stroke style.
 * 
 * Can be created using the method [strokeStyleDashed].
 * 
 * Required parameters: `type`.
 */
@Generated
data object StrokeStyleDashed : StrokeStyle {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = mapOf("type" to "dashed")
}

@Generated
fun DivScope.strokeStyleDashed(): StrokeStyleDashed = StrokeStyleDashed

@Generated
fun StrokeStyleDashed.asList() = listOf(this)
