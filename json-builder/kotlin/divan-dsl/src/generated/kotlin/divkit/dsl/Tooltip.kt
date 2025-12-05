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
@ExposedCopyVisibility
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
            backgroundAccessibilityDescription = additive.backgroundAccessibilityDescription ?: properties.backgroundAccessibilityDescription,
            bringToTopId = additive.bringToTopId ?: properties.bringToTopId,
            closeByTapOutside = additive.closeByTapOutside ?: properties.closeByTapOutside,
            div = additive.div ?: properties.div,
            duration = additive.duration ?: properties.duration,
            id = additive.id ?: properties.id,
            mode = additive.mode ?: properties.mode,
            offset = additive.offset ?: properties.offset,
            position = additive.position ?: properties.position,
            substrateDiv = additive.substrateDiv ?: properties.substrateDiv,
            tapOutsideActions = additive.tapOutsideActions ?: properties.tapOutsideActions,
        )
    )

    @ExposedCopyVisibility
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
         * Description for accessibility of the tap action on the background of the tooltip.
         */
        val backgroundAccessibilityDescription: Property<String>?,
        /**
         * An element that will be brought to the top of the substrate.
         */
        val bringToTopId: Property<String>?,
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
         * An element that will be used as a substrate for the tooltip.
         */
        val substrateDiv: Property<Div>?,
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
            result.tryPutProperty("background_accessibility_description", backgroundAccessibilityDescription)
            result.tryPutProperty("bring_to_top_id", bringToTopId)
            result.tryPutProperty("close_by_tap_outside", closeByTapOutside)
            result.tryPutProperty("div", div)
            result.tryPutProperty("duration", duration)
            result.tryPutProperty("id", id)
            result.tryPutProperty("mode", mode)
            result.tryPutProperty("offset", offset)
            result.tryPutProperty("position", position)
            result.tryPutProperty("substrate_div", substrateDiv)
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
 * @param backgroundAccessibilityDescription Description for accessibility of the tap action on the background of the tooltip.
 * @param bringToTopId An element that will be brought to the top of the substrate.
 * @param closeByTapOutside Allows dismissing tooltip by tapping outside of it.
 * @param div An element that will be shown in a tooltip. If there are tooltips inside an element, they won't be shown.
 * @param duration Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
 * @param id Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips.
 * @param mode Tooltip modes.
 * @param offset Shift relative to an anchor point.
 * @param position The position of a tooltip relative to an element it belongs to.
 * @param substrateDiv An element that will be used as a substrate for the tooltip.
 * @param tapOutsideActions Specifies actions triggered by tapping outside the tooltip.
 */
@Generated
fun DivScope.tooltip(
    `use named arguments`: Guard = Guard.instance,
    animationIn: Animation? = null,
    animationOut: Animation? = null,
    backgroundAccessibilityDescription: String? = null,
    bringToTopId: String? = null,
    closeByTapOutside: Boolean? = null,
    div: Div? = null,
    duration: Int? = null,
    id: String? = null,
    mode: TooltipMode? = null,
    offset: Point? = null,
    position: Tooltip.Position? = null,
    substrateDiv: Div? = null,
    tapOutsideActions: List<Action>? = null,
): Tooltip = Tooltip(
    Tooltip.Properties(
        animationIn = valueOrNull(animationIn),
        animationOut = valueOrNull(animationOut),
        backgroundAccessibilityDescription = valueOrNull(backgroundAccessibilityDescription),
        bringToTopId = valueOrNull(bringToTopId),
        closeByTapOutside = valueOrNull(closeByTapOutside),
        div = valueOrNull(div),
        duration = valueOrNull(duration),
        id = valueOrNull(id),
        mode = valueOrNull(mode),
        offset = valueOrNull(offset),
        position = valueOrNull(position),
        substrateDiv = valueOrNull(substrateDiv),
        tapOutsideActions = valueOrNull(tapOutsideActions),
    )
)

/**
 * @param animationIn Tooltip appearance animation. By default, the tooltip will be appearing gradually with an offset from the anchor point by 10 dp.
 * @param animationOut Tooltip disappearance animation. By default, the tooltip will disappear gradually with an offset from the anchor point by 10 dp.
 * @param backgroundAccessibilityDescription Description for accessibility of the tap action on the background of the tooltip.
 * @param bringToTopId An element that will be brought to the top of the substrate.
 * @param closeByTapOutside Allows dismissing tooltip by tapping outside of it.
 * @param div An element that will be shown in a tooltip. If there are tooltips inside an element, they won't be shown.
 * @param duration Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
 * @param id Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips.
 * @param mode Tooltip modes.
 * @param offset Shift relative to an anchor point.
 * @param position The position of a tooltip relative to an element it belongs to.
 * @param substrateDiv An element that will be used as a substrate for the tooltip.
 * @param tapOutsideActions Specifies actions triggered by tapping outside the tooltip.
 */
@Generated
fun DivScope.tooltipProps(
    `use named arguments`: Guard = Guard.instance,
    animationIn: Animation? = null,
    animationOut: Animation? = null,
    backgroundAccessibilityDescription: String? = null,
    bringToTopId: String? = null,
    closeByTapOutside: Boolean? = null,
    div: Div? = null,
    duration: Int? = null,
    id: String? = null,
    mode: TooltipMode? = null,
    offset: Point? = null,
    position: Tooltip.Position? = null,
    substrateDiv: Div? = null,
    tapOutsideActions: List<Action>? = null,
) = Tooltip.Properties(
    animationIn = valueOrNull(animationIn),
    animationOut = valueOrNull(animationOut),
    backgroundAccessibilityDescription = valueOrNull(backgroundAccessibilityDescription),
    bringToTopId = valueOrNull(bringToTopId),
    closeByTapOutside = valueOrNull(closeByTapOutside),
    div = valueOrNull(div),
    duration = valueOrNull(duration),
    id = valueOrNull(id),
    mode = valueOrNull(mode),
    offset = valueOrNull(offset),
    position = valueOrNull(position),
    substrateDiv = valueOrNull(substrateDiv),
    tapOutsideActions = valueOrNull(tapOutsideActions),
)

/**
 * @param animationIn Tooltip appearance animation. By default, the tooltip will be appearing gradually with an offset from the anchor point by 10 dp.
 * @param animationOut Tooltip disappearance animation. By default, the tooltip will disappear gradually with an offset from the anchor point by 10 dp.
 * @param backgroundAccessibilityDescription Description for accessibility of the tap action on the background of the tooltip.
 * @param bringToTopId An element that will be brought to the top of the substrate.
 * @param closeByTapOutside Allows dismissing tooltip by tapping outside of it.
 * @param div An element that will be shown in a tooltip. If there are tooltips inside an element, they won't be shown.
 * @param duration Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
 * @param id Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips.
 * @param mode Tooltip modes.
 * @param offset Shift relative to an anchor point.
 * @param position The position of a tooltip relative to an element it belongs to.
 * @param substrateDiv An element that will be used as a substrate for the tooltip.
 * @param tapOutsideActions Specifies actions triggered by tapping outside the tooltip.
 */
@Generated
fun TemplateScope.tooltipRefs(
    `use named arguments`: Guard = Guard.instance,
    animationIn: ReferenceProperty<Animation>? = null,
    animationOut: ReferenceProperty<Animation>? = null,
    backgroundAccessibilityDescription: ReferenceProperty<String>? = null,
    bringToTopId: ReferenceProperty<String>? = null,
    closeByTapOutside: ReferenceProperty<Boolean>? = null,
    div: ReferenceProperty<Div>? = null,
    duration: ReferenceProperty<Int>? = null,
    id: ReferenceProperty<String>? = null,
    mode: ReferenceProperty<TooltipMode>? = null,
    offset: ReferenceProperty<Point>? = null,
    position: ReferenceProperty<Tooltip.Position>? = null,
    substrateDiv: ReferenceProperty<Div>? = null,
    tapOutsideActions: ReferenceProperty<List<Action>>? = null,
) = Tooltip.Properties(
    animationIn = animationIn,
    animationOut = animationOut,
    backgroundAccessibilityDescription = backgroundAccessibilityDescription,
    bringToTopId = bringToTopId,
    closeByTapOutside = closeByTapOutside,
    div = div,
    duration = duration,
    id = id,
    mode = mode,
    offset = offset,
    position = position,
    substrateDiv = substrateDiv,
    tapOutsideActions = tapOutsideActions,
)

/**
 * @param animationIn Tooltip appearance animation. By default, the tooltip will be appearing gradually with an offset from the anchor point by 10 dp.
 * @param animationOut Tooltip disappearance animation. By default, the tooltip will disappear gradually with an offset from the anchor point by 10 dp.
 * @param backgroundAccessibilityDescription Description for accessibility of the tap action on the background of the tooltip.
 * @param bringToTopId An element that will be brought to the top of the substrate.
 * @param closeByTapOutside Allows dismissing tooltip by tapping outside of it.
 * @param div An element that will be shown in a tooltip. If there are tooltips inside an element, they won't be shown.
 * @param duration Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
 * @param id Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips.
 * @param mode Tooltip modes.
 * @param offset Shift relative to an anchor point.
 * @param position The position of a tooltip relative to an element it belongs to.
 * @param substrateDiv An element that will be used as a substrate for the tooltip.
 * @param tapOutsideActions Specifies actions triggered by tapping outside the tooltip.
 */
@Generated
fun Tooltip.override(
    `use named arguments`: Guard = Guard.instance,
    animationIn: Animation? = null,
    animationOut: Animation? = null,
    backgroundAccessibilityDescription: String? = null,
    bringToTopId: String? = null,
    closeByTapOutside: Boolean? = null,
    div: Div? = null,
    duration: Int? = null,
    id: String? = null,
    mode: TooltipMode? = null,
    offset: Point? = null,
    position: Tooltip.Position? = null,
    substrateDiv: Div? = null,
    tapOutsideActions: List<Action>? = null,
): Tooltip = Tooltip(
    Tooltip.Properties(
        animationIn = valueOrNull(animationIn) ?: properties.animationIn,
        animationOut = valueOrNull(animationOut) ?: properties.animationOut,
        backgroundAccessibilityDescription = valueOrNull(backgroundAccessibilityDescription) ?: properties.backgroundAccessibilityDescription,
        bringToTopId = valueOrNull(bringToTopId) ?: properties.bringToTopId,
        closeByTapOutside = valueOrNull(closeByTapOutside) ?: properties.closeByTapOutside,
        div = valueOrNull(div) ?: properties.div,
        duration = valueOrNull(duration) ?: properties.duration,
        id = valueOrNull(id) ?: properties.id,
        mode = valueOrNull(mode) ?: properties.mode,
        offset = valueOrNull(offset) ?: properties.offset,
        position = valueOrNull(position) ?: properties.position,
        substrateDiv = valueOrNull(substrateDiv) ?: properties.substrateDiv,
        tapOutsideActions = valueOrNull(tapOutsideActions) ?: properties.tapOutsideActions,
    )
)

/**
 * @param animationIn Tooltip appearance animation. By default, the tooltip will be appearing gradually with an offset from the anchor point by 10 dp.
 * @param animationOut Tooltip disappearance animation. By default, the tooltip will disappear gradually with an offset from the anchor point by 10 dp.
 * @param backgroundAccessibilityDescription Description for accessibility of the tap action on the background of the tooltip.
 * @param bringToTopId An element that will be brought to the top of the substrate.
 * @param closeByTapOutside Allows dismissing tooltip by tapping outside of it.
 * @param div An element that will be shown in a tooltip. If there are tooltips inside an element, they won't be shown.
 * @param duration Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
 * @param id Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips.
 * @param mode Tooltip modes.
 * @param offset Shift relative to an anchor point.
 * @param position The position of a tooltip relative to an element it belongs to.
 * @param substrateDiv An element that will be used as a substrate for the tooltip.
 * @param tapOutsideActions Specifies actions triggered by tapping outside the tooltip.
 */
@Generated
fun Tooltip.defer(
    `use named arguments`: Guard = Guard.instance,
    animationIn: ReferenceProperty<Animation>? = null,
    animationOut: ReferenceProperty<Animation>? = null,
    backgroundAccessibilityDescription: ReferenceProperty<String>? = null,
    bringToTopId: ReferenceProperty<String>? = null,
    closeByTapOutside: ReferenceProperty<Boolean>? = null,
    div: ReferenceProperty<Div>? = null,
    duration: ReferenceProperty<Int>? = null,
    id: ReferenceProperty<String>? = null,
    mode: ReferenceProperty<TooltipMode>? = null,
    offset: ReferenceProperty<Point>? = null,
    position: ReferenceProperty<Tooltip.Position>? = null,
    substrateDiv: ReferenceProperty<Div>? = null,
    tapOutsideActions: ReferenceProperty<List<Action>>? = null,
): Tooltip = Tooltip(
    Tooltip.Properties(
        animationIn = animationIn ?: properties.animationIn,
        animationOut = animationOut ?: properties.animationOut,
        backgroundAccessibilityDescription = backgroundAccessibilityDescription ?: properties.backgroundAccessibilityDescription,
        bringToTopId = bringToTopId ?: properties.bringToTopId,
        closeByTapOutside = closeByTapOutside ?: properties.closeByTapOutside,
        div = div ?: properties.div,
        duration = duration ?: properties.duration,
        id = id ?: properties.id,
        mode = mode ?: properties.mode,
        offset = offset ?: properties.offset,
        position = position ?: properties.position,
        substrateDiv = substrateDiv ?: properties.substrateDiv,
        tapOutsideActions = tapOutsideActions ?: properties.tapOutsideActions,
    )
)

/**
 * @param animationIn Tooltip appearance animation. By default, the tooltip will be appearing gradually with an offset from the anchor point by 10 dp.
 * @param animationOut Tooltip disappearance animation. By default, the tooltip will disappear gradually with an offset from the anchor point by 10 dp.
 * @param backgroundAccessibilityDescription Description for accessibility of the tap action on the background of the tooltip.
 * @param bringToTopId An element that will be brought to the top of the substrate.
 * @param closeByTapOutside Allows dismissing tooltip by tapping outside of it.
 * @param div An element that will be shown in a tooltip. If there are tooltips inside an element, they won't be shown.
 * @param duration Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
 * @param id Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips.
 * @param mode Tooltip modes.
 * @param offset Shift relative to an anchor point.
 * @param position The position of a tooltip relative to an element it belongs to.
 * @param substrateDiv An element that will be used as a substrate for the tooltip.
 * @param tapOutsideActions Specifies actions triggered by tapping outside the tooltip.
 */
@Generated
fun Tooltip.modify(
    `use named arguments`: Guard = Guard.instance,
    animationIn: Property<Animation>? = null,
    animationOut: Property<Animation>? = null,
    backgroundAccessibilityDescription: Property<String>? = null,
    bringToTopId: Property<String>? = null,
    closeByTapOutside: Property<Boolean>? = null,
    div: Property<Div>? = null,
    duration: Property<Int>? = null,
    id: Property<String>? = null,
    mode: Property<TooltipMode>? = null,
    offset: Property<Point>? = null,
    position: Property<Tooltip.Position>? = null,
    substrateDiv: Property<Div>? = null,
    tapOutsideActions: Property<List<Action>>? = null,
): Tooltip = Tooltip(
    Tooltip.Properties(
        animationIn = animationIn ?: properties.animationIn,
        animationOut = animationOut ?: properties.animationOut,
        backgroundAccessibilityDescription = backgroundAccessibilityDescription ?: properties.backgroundAccessibilityDescription,
        bringToTopId = bringToTopId ?: properties.bringToTopId,
        closeByTapOutside = closeByTapOutside ?: properties.closeByTapOutside,
        div = div ?: properties.div,
        duration = duration ?: properties.duration,
        id = id ?: properties.id,
        mode = mode ?: properties.mode,
        offset = offset ?: properties.offset,
        position = position ?: properties.position,
        substrateDiv = substrateDiv ?: properties.substrateDiv,
        tapOutsideActions = tapOutsideActions ?: properties.tapOutsideActions,
    )
)

/**
 * @param backgroundAccessibilityDescription Description for accessibility of the tap action on the background of the tooltip.
 * @param closeByTapOutside Allows dismissing tooltip by tapping outside of it.
 * @param duration Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it.
 * @param position The position of a tooltip relative to an element it belongs to.
 */
@Generated
fun Tooltip.evaluate(
    `use named arguments`: Guard = Guard.instance,
    backgroundAccessibilityDescription: ExpressionProperty<String>? = null,
    closeByTapOutside: ExpressionProperty<Boolean>? = null,
    duration: ExpressionProperty<Int>? = null,
    position: ExpressionProperty<Tooltip.Position>? = null,
): Tooltip = Tooltip(
    Tooltip.Properties(
        animationIn = properties.animationIn,
        animationOut = properties.animationOut,
        backgroundAccessibilityDescription = backgroundAccessibilityDescription ?: properties.backgroundAccessibilityDescription,
        bringToTopId = properties.bringToTopId,
        closeByTapOutside = closeByTapOutside ?: properties.closeByTapOutside,
        div = properties.div,
        duration = duration ?: properties.duration,
        id = properties.id,
        mode = properties.mode,
        offset = properties.offset,
        position = position ?: properties.position,
        substrateDiv = properties.substrateDiv,
        tapOutsideActions = properties.tapOutsideActions,
    )
)

@Generated
fun Tooltip.asList() = listOf(this)

@Generated
fun Tooltip.Position.asList() = listOf(this)
