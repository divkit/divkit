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
 * Font family:<li>`text` — a standard text font;</li><li>`display` — a family of fonts with a large font size.</li>
 * 
 * Possible values: [text, display].
 */
@Generated
sealed interface FontFamily

fun FontFamily.asList() = listOf(this)
