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
 * Element size adjusts to a parent element.
 * 
 * Can be created using the method [matchParentSize].
 * 
 * Required properties: `type`.
 */
@Generated
class MatchParentSize internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Size {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "match_parent")
    )

    operator fun plus(additive: Properties): MatchParentSize = MatchParentSize(
        Properties(
            weight = additive.weight ?: properties.weight,
        )
    )

    class Properties internal constructor(
        /**
         * Weight when distributing free space between elements with the size type `match_parent` inside an element. If the weight isn't specified, the elements will divide the place equally.
         */
        val weight: Property<Double>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("weight", weight)
            return result
        }
    }
}

/**
 * @param weight Weight when distributing free space between elements with the size type `match_parent` inside an element. If the weight isn't specified, the elements will divide the place equally.
 */
@Generated
fun DivScope.matchParentSize(
    `use named arguments`: Guard = Guard.instance,
    weight: Double? = null,
): MatchParentSize = MatchParentSize(
    MatchParentSize.Properties(
        weight = valueOrNull(weight),
    )
)

/**
 * @param weight Weight when distributing free space between elements with the size type `match_parent` inside an element. If the weight isn't specified, the elements will divide the place equally.
 */
@Generated
fun DivScope.matchParentSizeProps(
    `use named arguments`: Guard = Guard.instance,
    weight: Double? = null,
) = MatchParentSize.Properties(
    weight = valueOrNull(weight),
)

/**
 * @param weight Weight when distributing free space between elements with the size type `match_parent` inside an element. If the weight isn't specified, the elements will divide the place equally.
 */
@Generated
fun TemplateScope.matchParentSizeRefs(
    `use named arguments`: Guard = Guard.instance,
    weight: ReferenceProperty<Double>? = null,
) = MatchParentSize.Properties(
    weight = weight,
)

/**
 * @param weight Weight when distributing free space between elements with the size type `match_parent` inside an element. If the weight isn't specified, the elements will divide the place equally.
 */
@Generated
fun MatchParentSize.override(
    `use named arguments`: Guard = Guard.instance,
    weight: Double? = null,
): MatchParentSize = MatchParentSize(
    MatchParentSize.Properties(
        weight = valueOrNull(weight) ?: properties.weight,
    )
)

/**
 * @param weight Weight when distributing free space between elements with the size type `match_parent` inside an element. If the weight isn't specified, the elements will divide the place equally.
 */
@Generated
fun MatchParentSize.defer(
    `use named arguments`: Guard = Guard.instance,
    weight: ReferenceProperty<Double>? = null,
): MatchParentSize = MatchParentSize(
    MatchParentSize.Properties(
        weight = weight ?: properties.weight,
    )
)

/**
 * @param weight Weight when distributing free space between elements with the size type `match_parent` inside an element. If the weight isn't specified, the elements will divide the place equally.
 */
@Generated
fun MatchParentSize.evaluate(
    `use named arguments`: Guard = Guard.instance,
    weight: ExpressionProperty<Double>? = null,
): MatchParentSize = MatchParentSize(
    MatchParentSize.Properties(
        weight = weight ?: properties.weight,
    )
)

@Generated
fun MatchParentSize.asList() = listOf(this)
