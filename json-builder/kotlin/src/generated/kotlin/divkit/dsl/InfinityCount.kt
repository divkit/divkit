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
 * Infinite number of repetitions.
 * 
 * Can be created using the method [infinityCount].
 * 
 * Required properties: `type`.
 */
@Generated
class InfinityCount internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Count {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "infinity")
    )

    operator fun plus(additive: Properties): InfinityCount = InfinityCount(
        Properties(
        )
    )

    class Properties internal constructor(
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            return result
        }
    }
}

@Generated
fun DivScope.infinityCount(

): InfinityCount = InfinityCount(
    InfinityCount.Properties(
    )
)

@Generated
fun DivScope.infinityCountProps(

) = InfinityCount.Properties(
)

@Generated
fun TemplateScope.infinityCountRefs(
    `use named arguments`: Guard = Guard.instance,
) = InfinityCount.Properties(
)

@Generated
fun InfinityCount.override(

): InfinityCount = InfinityCount(
    InfinityCount.Properties(
    )
)

@Generated
fun InfinityCount.defer(
    `use named arguments`: Guard = Guard.instance,
): InfinityCount = InfinityCount(
    InfinityCount.Properties(
    )
)

@Generated
fun InfinityCount.asList() = listOf(this)
