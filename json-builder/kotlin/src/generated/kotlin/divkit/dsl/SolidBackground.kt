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
 * Solid background color.
 * 
 * Can be created using the method [solidBackground].
 * 
 * Required parameters: `type, color`.
 */
@Generated
class SolidBackground internal constructor(
    @JsonIgnore
    val properties: Properties,
) : TextRangeBackground, Background {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "solid")
    )

    operator fun plus(additive: Properties): SolidBackground = SolidBackground(
        Properties(
            color = additive.color ?: properties.color,
        )
    )

    class Properties internal constructor(
        /**
         * Color.
         */
        val color: Property<Color>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("color", color)
            return result
        }
    }
}

/**
 * @param color Color.
 */
@Generated
fun DivScope.solidBackground(
    color: Color? = null,
): SolidBackground = SolidBackground(
    SolidBackground.Properties(
        color = valueOrNull(color),
    )
)

/**
 * @param color Color.
 */
@Generated
fun DivScope.solidBackgroundProps(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
) = SolidBackground.Properties(
    color = valueOrNull(color),
)

/**
 * @param color Color.
 */
@Generated
fun TemplateScope.solidBackgroundRefs(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
) = SolidBackground.Properties(
    color = color,
)

/**
 * @param color Color.
 */
@Generated
fun SolidBackground.override(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
): SolidBackground = SolidBackground(
    SolidBackground.Properties(
        color = valueOrNull(color) ?: properties.color,
    )
)

/**
 * @param color Color.
 */
@Generated
fun SolidBackground.defer(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
): SolidBackground = SolidBackground(
    SolidBackground.Properties(
        color = color ?: properties.color,
    )
)

/**
 * @param color Color.
 */
@Generated
fun SolidBackground.evaluate(
    `use named arguments`: Guard = Guard.instance,
    color: ExpressionProperty<Color>? = null,
): SolidBackground = SolidBackground(
    SolidBackground.Properties(
        color = color ?: properties.color,
    )
)

@Generated
fun SolidBackground.asList() = listOf(this)
