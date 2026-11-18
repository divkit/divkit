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
 * Animation speed nature:<li>`linear` — linear animation speed;</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1);</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1);</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1);</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1).</li>
 * 
 * Possible values: [linear], [ease], [ease_in], [ease_out], [ease_in_out], [spring].
 */
@Generated
sealed interface AnimationInterpolator

@Generated
fun AnimationInterpolator.asList() = listOf(this)
