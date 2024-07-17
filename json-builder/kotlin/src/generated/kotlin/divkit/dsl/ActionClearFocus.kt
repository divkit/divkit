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
 * Removes focus from an element.
 * 
 * Can be created using the method [actionClearFocus].
 * 
 * Required parameters: `type`.
 */
@Generated
object ActionClearFocus : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = mapOf("type" to "clear_focus")
}

@Generated
fun DivScope.actionClearFocus(): ActionClearFocus = ActionClearFocus

@Generated
fun ActionClearFocus.asList() = listOf(this)
