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
 * Solid stroke style.
 * 
 * Can be created using the method [strokeStyleSolid].
 * 
 * Required parameters: `type`.
 */
@Generated
data object StrokeStyleSolid : StrokeStyle {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = mapOf("type" to "solid")
}

@Generated
fun DivScope.strokeStyleSolid(): StrokeStyleSolid = StrokeStyleSolid

@Generated
fun StrokeStyleSolid.asList() = listOf(this)
