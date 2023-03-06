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
 * Element visibility:<li>`visible` — the element is visible;</li><li>`invisible` — the element is invisible, but a place is reserved for it;</li><li>`gone` — the element is invisible, a place isn't reserved.</li>
 * 
 * Possible values: [visible, invisible, gone].
 */
@Generated
sealed interface Visibility

@Generated
fun Visibility.asList() = listOf(this)
