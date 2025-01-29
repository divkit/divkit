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
data class Tooltip internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): Tooltip = Tooltip(
        Properties(
            animationIn = additive.animationIn ?: properties.animationIn,
            animationOut = additive.animationOut ?: properties.animationOut,
            closeByTapOutside = additive.closeByTapOutside ?: properties.closeByTapOutside,
            div = additive.div ?: properties.div,
            duration = additive.duration ?: properties.duration,
            id = additive.id ?: properties.id,
            mode = additive.mode ?: properties.mode,
            offset = additive.offset ?: properties.offset,
            position = additive.position ?: properties.position,
            tapOutsideActions = additive.tapOutsideActions ?: properties.tapOutsideActions,
        )
    )

    data class Properties internal constructor(
        /**
         * Tooltip appearance animation. By default, the tooltip will be appearing gradually with an offset from the anchor point by 10 dp.
         */
        val animationIn: Property<Animation>?,
        /**
         * Tooltip disappearance animation. By default, the tooltip will disappear gradually with an offset from the anchor point by 10 dp.
         */
        val animationOut: Property<Animation>?,
        /**
         * Allows dismissing tooltip by tapping outside of it.
         * Default value: `true`.
         */
        val closeByTapOutside: Property<Boolean>?,
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
         * Tooltip modes.
         * Default value: `{ "type": "modal" }`.
         */
        val mode: Property<TooltipMode>?,
        /**
         * Shift relative to an anchor point.
         */
        val offset: Property<Point>?,
        /**
         * The position of a tooltip relative to an element it belongs to.
         */
        val position: Property<Position>?,
        /**
         * Specifies actions triggered by tapping outside the tooltip.
         */
        val tapOutsideActions: Property<List<Action>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("animation_in", animationIn)
            result.tryPutProperty("animation_out", animationOut)
            result.tryPutProperty("close_by_tap_outside", closeByTapOutside)
            result.tryPutProperty("div", div)
            result.tryPutProperty("duration", duration)
            result.tryPutProperty("id", id)
            result.tryPutProperty("mode", mode)
            result.tryPutProperty("offset", offset)
            result.tryPutProperty("position", position)
            result.tryPutProperty("tap_outside_actions", tapOutsideActions)
            return result
        }
    }

    /**
     * The position of a tooltip relative to an element it belongs to.
     * 
     * Possible values: [left], [top_left], [top], [top_right], [right], [bottom_right], [bottom], [bottom_left], [center].
     */
    @Generated
    sealed interface Position
}

/**
 * @param animationIn Tooltip appearance animation. By default, the tooltip will be appearing gradually with an offset from the anchor point by 10 dp.
 * @param animationOut Tooltip disappearance animation. By default, the tooltip will disappear gradually with an offset from the anchor point by 10 dp.
 * @param closeByTapOutside Allows dismissing tooltip by tapping outside of it.
 * @param div An element that will be shown in a tooltip. If there are tooltips inside an element, they won't be shown.
 * @param duration Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
 * @param id Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips.
 * @param mode Tooltip modes.
 * @param offset Shift relative to an anchor point.
 * @param position The position of a tooltip relative to an element it belongs to.
 * @param tapOutsideActions Specifies actions triggered by tapping outside the tooltip.
 */
@Generated
fun DivScope.tooltip(
    `use named arguments`: Guard = Guard.instance,
    animationIn: Animation? = null,
    animationOut: Animation? = null,
    closeByTapOutside: Boolean? = null,
    div: Div? = null,
    duration: Int? = null,
    id: String? = null,
    mode: TooltipMode? = null,
    offset: Point? = null,
    position: Tooltip.Position? = null,
    tapOutsideActions: List<Action>? = null,
): Tooltip = Tooltip(
    Tooltip.Properties(
        animationIn = valueOrNull(animationIn),
        animationOut = valueOrNull(animationOut),
        closeByTapOutside = valueOrNull(closeByTapOutside),
        div = valueOrNull(div),
        duration = valueOrNull(duration),
        id = valueOrNull(id),
        mode = valueOrNull(mode),
        offset = valueOrNull(offset),
        position = valueOrNull(position),
        tapOutsideActions = valueOrNull(tapOutsideActions),
    )
)

/**
 * @param animationIn Tooltip appearance animation. By default, the tooltip will be appearing gradually with an offset from the anchor point by 10 dp.
 * @param animationOut Tooltip disappearance animation. By default, the tooltip will disappear gradually with an offset from the anchor point by 10 dp.
 * @param closeByTapOutside Allows dismissing tooltip by tapping outside of it.
 * @param div An element that will be shown in a tooltip. If there are tooltips inside an element, they won't be shown.
 * @param duration Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
 * @param id Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips.
 * @param mode Tooltip modes.
 * @param offset Shift relative to an anchor point.
 * @param position The position of a tooltip relative to an element it belongs to.
 * @param tapOutsideActions Specifies actions triggered by tapping outside the tooltip.
 */
@Generated
fun DivScope.tooltipProps(
    `use named arguments`: Guard = Guard.instance,
    animationIn: Animation? = null,
    animationOut: Animation? = null,
    closeByTapOutside: Boolean? = null,
    div: Div? = null,
    duration: Int? = null,
    id: String? = null,
    mode: TooltipMode? = null,
    offset: Point? = null,
    position: Tooltip.Position? = null,
    tapOutsideActions: List<Action>? = null,
) = Tooltip.Properties(
    animationIn = valueOrNull(animationIn),
    animationOut = valueOrNull(animationOut),
    closeByTapOutside = valueOrNull(closeByTapOutside),
    div = valueOrNull(div),
    duration = valueOrNull(duration),
    id = valueOrNull(id),
    mode = valueOrNull(mode),
    offset = valueOrNull(offset),
    position = valueOrNull(position),
    tapOutsideActions = valueOrNull(tapOutsideActions),
)

/**
 * @param animationIn Tooltip appearance animation. By default, the tooltip will be appearing gradually with an offset from the anchor point by 10 dp.
 * @param animationOut Tooltip disappearance animation. By default, the tooltip will disappear gradually with an offset from the anchor point by 10 dp.
 * @param closeByTapOutside Allows dismissing tooltip by tapping outside of it.
 * @param div An element that will be shown in a tooltip. If there are tooltips inside an element, they won't be shown.
 * @param duration Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
 * @param id Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips.
 * @param mode Tooltip modes.
 * @param offset Shift relative to an anchor point.
 * @param position The position of a tooltip relative to an element it belongs to.
 * @param tapOutsideActions Specifies actions triggered by tapping outside the tooltip.
 */
@Generated
fun TemplateScope.tooltipRefs(
    `use named arguments`: Guard = Guard.instance,
    animationIn: ReferenceProperty<Animation>? = null,
    animationOut: ReferenceProperty<Animation>? = null,
    closeByTapOutside: ReferenceProperty<Boolean>? = null,
    div: ReferenceProperty<Div>? = null,
    duration: ReferenceProperty<Int>? = null,
    id: ReferenceProperty<String>? = null,
    mode: ReferenceProperty<TooltipMode>? = null,
    offset: ReferenceProperty<Point>? = null,
    position: ReferenceProperty<Tooltip.Position>? = null,
    tapOutsideActions: ReferenceProperty<List<Action>>? = null,
) = Tooltip.Properties(
    animationIn = animationIn,
    animationOut = animationOut,
    closeByTapOutside = closeByTapOutside,
    div = div,
    duration = duration,
    id = id,
    mode = mode,
    offset = offset,
    position = position,
    tapOutsideActions = tapOutsideActions,
)

/**
 * @param animationIn Tooltip appearance animation. By default, the tooltip will be appearing gradually with an offset from the anchor point by 10 dp.
 * @param animationOut Tooltip disappearance animation. By default, the tooltip will disappear gradually with an offset from the anchor point by 10 dp.
 * @param closeByTapOutside Allows dismissing tooltip by tapping outside of it.
 * @param div An element that will be shown in a tooltip. If there are tooltips inside an element, they won't be shown.
 * @param duration Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
 * @param id Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips.
 * @param mode Tooltip modes.
 * @param offset Shift relative to an anchor point.
 * @param position The position of a tooltip relative to an element it belongs to.
 * @param tapOutsideActions Specifies actions triggered by tapping outside the tooltip.
 */
@Generated
fun Tooltip.override(
    `use named arguments`: Guard = Guard.instance,
    animationIn: Animation? = null,
    animationOut: Animation? = null,
    closeByTapOutside: Boolean? = null,
    div: Div? = null,
    duration: Int? = null,
    id: String? = null,
    mode: TooltipMode? = null,
    offset: Point? = null,
    position: Tooltip.Position? = null,
    tapOutsideActions: List<Action>? = null,
): Tooltip = Tooltip(
    Tooltip.Properties(
        animationIn = valueOrNull(animationIn) ?: properties.animationIn,
        animationOut = valueOrNull(animationOut) ?: properties.animationOut,
        closeByTapOutside = valueOrNull(closeByTapOutside) ?: properties.closeByTapOutside,
        div = valueOrNull(div) ?: properties.div,
        duration = valueOrNull(duration) ?: properties.duration,
        id = valueOrNull(id) ?: properties.id,
        mode = valueOrNull(mode) ?: properties.mode,
        offset = valueOrNull(offset) ?: properties.offset,
        position = valueOrNull(position) ?: properties.position,
        tapOutsideActions = valueOrNull(tapOutsideActions) ?: properties.tapOutsideActions,
    )
)

/**
 * @param animationIn Tooltip appearance animation. By default, the tooltip will be appearing gradually with an offset from the anchor point by 10 dp.
 * @param animationOut Tooltip disappearance animation. By default, the tooltip will disappear gradually with an offset from the anchor point by 10 dp.
 * @param closeByTapOutside Allows dismissing tooltip by tapping outside of it.
 * @param div An element that will be shown in a tooltip. If there are tooltips inside an element, they won't be shown.
 * @param duration Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
 * @param id Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips.
 * @param mode Tooltip modes.
 * @param offset Shift relative to an anchor point.
 * @param position The position of a tooltip relative to an element it belongs to.
 * @param tapOutsideActions Specifies actions triggered by tapping outside the tooltip.
 */
@Generated
fun Tooltip.defer(
    `use named arguments`: Guard = Guard.instance,
    animationIn: ReferenceProperty<Animation>? = null,
    animationOut: ReferenceProperty<Animation>? = null,
    closeByTapOutside: ReferenceProperty<Boolean>? = null,
    div: ReferenceProperty<Div>? = null,
    duration: ReferenceProperty<Int>? = null,
    id: ReferenceProperty<String>? = null,
    mode: ReferenceProperty<TooltipMode>? = null,
    offset: ReferenceProperty<Point>? = null,
    position: ReferenceProperty<Tooltip.Position>? = null,
    tapOutsideActions: ReferenceProperty<List<Action>>? = null,
): Tooltip = Tooltip(
    Tooltip.Properties(
        animationIn = animationIn ?: properties.animationIn,
        animationOut = animationOut ?: properties.animationOut,
        closeByTapOutside = closeByTapOutside ?: properties.closeByTapOutside,
        div = div ?: properties.div,
        duration = duration ?: properties.duration,
        id = id ?: properties.id,
        mode = mode ?: properties.mode,
        offset = offset ?: properties.offset,
        position = position ?: properties.position,
        tapOutsideActions = tapOutsideActions ?: properties.tapOutsideActions,
    )
)

/**
 * @param closeByTapOutside Allows dismissing tooltip by tapping outside of it.
 * @param duration Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
 * @param position The position of a tooltip relative to an element it belongs to.
 */
@Generated
fun Tooltip.evaluate(
    `use named arguments`: Guard = Guard.instance,
    closeByTapOutside: ExpressionProperty<Boolean>? = null,
    duration: ExpressionProperty<Int>? = null,
    position: ExpressionProperty<Tooltip.Position>? = null,
): Tooltip = Tooltip(
    Tooltip.Properties(
        animationIn = properties.animationIn,
        animationOut = properties.animationOut,
        closeByTapOutside = closeByTapOutside ?: properties.closeByTapOutside,
        div = properties.div,
        duration = duration ?: properties.duration,
        id = properties.id,
        mode = properties.mode,
        offset = properties.offset,
        position = position ?: properties.position,
        tapOutsideActions = properties.tapOutsideActions,
    )
)

@Generated
fun Tooltip.asList() = listOf(this)

@Generated
fun Tooltip.Position.asList() = listOf(this)
