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
 * Possible values: [StringVariable], [NumberVariable], [IntegerVariable], [BooleanVariable], [ColorVariable], [UrlVariable], [DictVariable], [ArrayVariable].
 */
@Generated
sealed interface Variable

@Generated
fun Variable.asList() = listOf(this)
