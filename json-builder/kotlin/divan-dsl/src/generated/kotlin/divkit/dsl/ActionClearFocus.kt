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
 * Removes focus from an element.
 * 
 * Can be created using the method [actionClearFocus].
 * 
 * Required parameters: `type`.
 */
@Generated
@JsonSerialize(using = ActionClearFocusSerializer::class)
data object ActionClearFocus : ActionTyped

@Generated
fun DivScope.actionClearFocus(): ActionClearFocus = ActionClearFocus

@Generated
fun ActionClearFocus.asList() = listOf(this)
