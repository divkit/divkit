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
 * Possible values: [Image], [GifImage], [Text], [Separator], [Container], [Grid], [Gallery], [Pager], [Tabs], [State], [Custom], [Indicator], [Slider], [Switch], [Input], [Select], [Video].
 */
@Generated
sealed interface Div

@Generated
fun Div.asList() = listOf(this)
