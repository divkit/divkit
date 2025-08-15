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
 * Can be created using the method [sizeUnitValue].
 * 
 * Required parameters: `value`.
 */
@Generated
data class SizeUnitValue internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): SizeUnitValue = SizeUnitValue(
        Properties(
            unit = additive.unit ?: properties.unit,
            value = additive.value ?: properties.value,
        )
    )

    data class Properties internal constructor(
        /**
         * Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
         * Default value: `dp`.
         */
        val unit: Property<SizeUnit>?,
        /**
         * Element size.
         */
        val value: Property<Int>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("unit", unit)
            result.tryPutProperty("value", value)
            return result
        }
    }
}

/**
 * @param unit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param value Element size.
 */
@Generated
fun DivScope.sizeUnitValue(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Int? = null,
): SizeUnitValue = SizeUnitValue(
    SizeUnitValue.Properties(
        unit = valueOrNull(unit),
        value = valueOrNull(value),
    )
)

/**
 * @param unit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param value Element size.
 */
@Generated
fun DivScope.sizeUnitValueProps(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Int? = null,
) = SizeUnitValue.Properties(
    unit = valueOrNull(unit),
    value = valueOrNull(value),
)

/**
 * @param unit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param value Element size.
 */
@Generated
fun TemplateScope.sizeUnitValueRefs(
    `use named arguments`: Guard = Guard.instance,
    unit: ReferenceProperty<SizeUnit>? = null,
    value: ReferenceProperty<Int>? = null,
) = SizeUnitValue.Properties(
    unit = unit,
    value = value,
)

/**
 * @param unit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param value Element size.
 */
@Generated
fun SizeUnitValue.override(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Int? = null,
): SizeUnitValue = SizeUnitValue(
    SizeUnitValue.Properties(
        unit = valueOrNull(unit) ?: properties.unit,
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param unit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param value Element size.
 */
@Generated
fun SizeUnitValue.defer(
    `use named arguments`: Guard = Guard.instance,
    unit: ReferenceProperty<SizeUnit>? = null,
    value: ReferenceProperty<Int>? = null,
): SizeUnitValue = SizeUnitValue(
    SizeUnitValue.Properties(
        unit = unit ?: properties.unit,
        value = value ?: properties.value,
    )
)

/**
 * @param unit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param value Element size.
 */
@Generated
fun SizeUnitValue.modify(
    `use named arguments`: Guard = Guard.instance,
    unit: Property<SizeUnit>? = null,
    value: Property<Int>? = null,
): SizeUnitValue = SizeUnitValue(
    SizeUnitValue.Properties(
        unit = unit ?: properties.unit,
        value = value ?: properties.value,
    )
)

/**
 * @param unit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 * @param value Element size.
 */
@Generated
fun SizeUnitValue.evaluate(
    `use named arguments`: Guard = Guard.instance,
    unit: ExpressionProperty<SizeUnit>? = null,
    value: ExpressionProperty<Int>? = null,
): SizeUnitValue = SizeUnitValue(
    SizeUnitValue.Properties(
        unit = unit ?: properties.unit,
        value = value ?: properties.value,
    )
)

@Generated
fun SizeUnitValue.asList() = listOf(this)
