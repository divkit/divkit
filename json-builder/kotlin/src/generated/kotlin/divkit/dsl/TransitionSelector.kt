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
 * Variants of events that will trigger animations in child elements. The parent element can be [data](div-data.md) or [state](div-state.md).
 * 
 * Possible values: [none, data_change, state_change, any_change].
 */
@Generated
sealed interface TransitionSelector

fun TransitionSelector.asList() = listOf(this)
