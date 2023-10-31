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
 * Tooltip.
 * 
 * Can be created using the method [tooltip].
 * 
 * Required parameters: `position, id, div`.
 */
@Generated
class Tooltip internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): Tooltip = Tooltip(
        Properties(
            animationIn = additive.animationIn ?: properties.animationIn,
            animationOut = additive.animationOut ?: properties.animationOut,
            div = additive.div ?: properties.div,
            duration = additive.duration ?: properties.duration,
            id = additive.id ?: properties.id,
            offset = additive.offset ?: properties.offset,
            position = additive.position ?: properties.position,
        )
    )

    class Properties internal constructor(
        /**
         * Tooltip appearance animation. By default, the tooltip will be appearing gradually with an offset from the anchor point by 10 dp.
         */
        val animationIn: Property<Animation>?,
        /**
         * Tooltip disappearance animation. By default, the tooltip will disappear gradually with an offset from the anchor point by 10 dp.
         */
        val animationOut: Property<Animation>?,
        /**
         * An element that will be shown in a tooltip. If there are tooltips inside an element, they won't be shown.
         */
        val div: Property<Div>?,
        /**
         * Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
         * Default value: `5000`.
         */
        val duration: Property<Int>?,
        /**
         * Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips.
         */
        val id: Property<String>?,
        /**
         * Shift relative to an anchor point.
         */
        val offset: Property<Point>?,
        /**
         * The position of a tooltip relative to an element it belongs to.
         */
        val position: Property<Position>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("animation_in", animationIn)
            result.tryPutProperty("animation_out", animationOut)
            result.tryPutProperty("div", div)
            result.tryPutProperty("duration", duration)
            result.tryPutProperty("id", id)
            result.tryPutProperty("offset", offset)
            result.tryPutProperty("position", position)
            return result
        }
    }

    /**
     * The position of a tooltip relative to an element it belongs to.
     * 
     * Possible values: [left, top-left, top, top-right, right, bottom-right, bottom, bottom-left, center].
     */
    @Generated
    sealed interface Position
}

/**
 * @param animationIn Tooltip appearance animation. By default, the tooltip will be appearing gradually with an offset from the anchor point by 10 dp.
 * @param animationOut Tooltip disappearance animation. By default, the tooltip will disappear gradually with an offset from the anchor point by 10 dp.
 * @param div An element that will be shown in a tooltip. If there are tooltips inside an element, they won't be shown.
 * @param duration Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
 * @param id Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips.
 * @param offset Shift relative to an anchor point.
 * @param position The position of a tooltip relative to an element it belongs to.
 */
@Generated
fun DivScope.tooltip(
    `use named arguments`: Guard = Guard.instance,
    animationIn: Animation? = null,
    animationOut: Animation? = null,
    div: Div? = null,
    duration: Int? = null,
    id: String? = null,
    offset: Point? = null,
    position: Tooltip.Position? = null,
): Tooltip = Tooltip(
    Tooltip.Properties(
        animationIn = valueOrNull(animationIn),
        animationOut = valueOrNull(animationOut),
        div = valueOrNull(div),
        duration = valueOrNull(duration),
        id = valueOrNull(id),
        offset = valueOrNull(offset),
        position = valueOrNull(position),
    )
)

/**
 * @param animationIn Tooltip appearance animation. By default, the tooltip will be appearing gradually with an offset from the anchor point by 10 dp.
 * @param animationOut Tooltip disappearance animation. By default, the tooltip will disappear gradually with an offset from the anchor point by 10 dp.
 * @param div An element that will be shown in a tooltip. If there are tooltips inside an element, they won't be shown.
 * @param duration Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
 * @param id Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips.
 * @param offset Shift relative to an anchor point.
 * @param position The position of a tooltip relative to an element it belongs to.
 */
@Generated
fun DivScope.tooltipProps(
    `use named arguments`: Guard = Guard.instance,
    animationIn: Animation? = null,
    animationOut: Animation? = null,
    div: Div? = null,
    duration: Int? = null,
    id: String? = null,
    offset: Point? = null,
    position: Tooltip.Position? = null,
) = Tooltip.Properties(
    animationIn = valueOrNull(animationIn),
    animationOut = valueOrNull(animationOut),
    div = valueOrNull(div),
    duration = valueOrNull(duration),
    id = valueOrNull(id),
    offset = valueOrNull(offset),
    position = valueOrNull(position),
)

/**
 * @param animationIn Tooltip appearance animation. By default, the tooltip will be appearing gradually with an offset from the anchor point by 10 dp.
 * @param animationOut Tooltip disappearance animation. By default, the tooltip will disappear gradually with an offset from the anchor point by 10 dp.
 * @param div An element that will be shown in a tooltip. If there are tooltips inside an element, they won't be shown.
 * @param duration Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
 * @param id Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips.
 * @param offset Shift relative to an anchor point.
 * @param position The position of a tooltip relative to an element it belongs to.
 */
@Generated
fun TemplateScope.tooltipRefs(
    `use named arguments`: Guard = Guard.instance,
    animationIn: ReferenceProperty<Animation>? = null,
    animationOut: ReferenceProperty<Animation>? = null,
    div: ReferenceProperty<Div>? = null,
    duration: ReferenceProperty<Int>? = null,
    id: ReferenceProperty<String>? = null,
    offset: ReferenceProperty<Point>? = null,
    position: ReferenceProperty<Tooltip.Position>? = null,
) = Tooltip.Properties(
    animationIn = animationIn,
    animationOut = animationOut,
    div = div,
    duration = duration,
    id = id,
    offset = offset,
    position = position,
)

/**
 * @param animationIn Tooltip appearance animation. By default, the tooltip will be appearing gradually with an offset from the anchor point by 10 dp.
 * @param animationOut Tooltip disappearance animation. By default, the tooltip will disappear gradually with an offset from the anchor point by 10 dp.
 * @param div An element that will be shown in a tooltip. If there are tooltips inside an element, they won't be shown.
 * @param duration Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
 * @param id Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips.
 * @param offset Shift relative to an anchor point.
 * @param position The position of a tooltip relative to an element it belongs to.
 */
@Generated
fun Tooltip.override(
    `use named arguments`: Guard = Guard.instance,
    animationIn: Animation? = null,
    animationOut: Animation? = null,
    div: Div? = null,
    duration: Int? = null,
    id: String? = null,
    offset: Point? = null,
    position: Tooltip.Position? = null,
): Tooltip = Tooltip(
    Tooltip.Properties(
        animationIn = valueOrNull(animationIn) ?: properties.animationIn,
        animationOut = valueOrNull(animationOut) ?: properties.animationOut,
        div = valueOrNull(div) ?: properties.div,
        duration = valueOrNull(duration) ?: properties.duration,
        id = valueOrNull(id) ?: properties.id,
        offset = valueOrNull(offset) ?: properties.offset,
        position = valueOrNull(position) ?: properties.position,
    )
)

/**
 * @param animationIn Tooltip appearance animation. By default, the tooltip will be appearing gradually with an offset from the anchor point by 10 dp.
 * @param animationOut Tooltip disappearance animation. By default, the tooltip will disappear gradually with an offset from the anchor point by 10 dp.
 * @param div An element that will be shown in a tooltip. If there are tooltips inside an element, they won't be shown.
 * @param duration Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
 * @param id Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips.
 * @param offset Shift relative to an anchor point.
 * @param position The position of a tooltip relative to an element it belongs to.
 */
@Generated
fun Tooltip.defer(
    `use named arguments`: Guard = Guard.instance,
    animationIn: ReferenceProperty<Animation>? = null,
    animationOut: ReferenceProperty<Animation>? = null,
    div: ReferenceProperty<Div>? = null,
    duration: ReferenceProperty<Int>? = null,
    id: ReferenceProperty<String>? = null,
    offset: ReferenceProperty<Point>? = null,
    position: ReferenceProperty<Tooltip.Position>? = null,
): Tooltip = Tooltip(
    Tooltip.Properties(
        animationIn = animationIn ?: properties.animationIn,
        animationOut = animationOut ?: properties.animationOut,
        div = div ?: properties.div,
        duration = duration ?: properties.duration,
        id = id ?: properties.id,
        offset = offset ?: properties.offset,
        position = position ?: properties.position,
    )
)

/**
 * @param duration Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
 * @param position The position of a tooltip relative to an element it belongs to.
 */
@Generated
fun Tooltip.evaluate(
    `use named arguments`: Guard = Guard.instance,
    duration: ExpressionProperty<Int>? = null,
    position: ExpressionProperty<Tooltip.Position>? = null,
): Tooltip = Tooltip(
    Tooltip.Properties(
        animationIn = properties.animationIn,
        animationOut = properties.animationOut,
        div = properties.div,
        duration = duration ?: properties.duration,
        id = properties.id,
        offset = properties.offset,
        position = position ?: properties.position,
    )
)

@Generated
fun Tooltip.asList() = listOf(this)

@Generated
fun Tooltip.Position.asList() = listOf(this)
