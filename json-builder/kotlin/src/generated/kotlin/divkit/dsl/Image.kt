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
 * Image.
 * 
 * Can be created using the method [image].
 * 
 * Required parameters: `type, image_url`.
 */
@Generated
class Image internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Div {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "image")
    )

    operator fun plus(additive: Properties): Image = Image(
        Properties(
            imageUrl = additive.imageUrl ?: properties.imageUrl,
            accessibility = additive.accessibility ?: properties.accessibility,
            action = additive.action ?: properties.action,
            actionAnimation = additive.actionAnimation ?: properties.actionAnimation,
            actions = additive.actions ?: properties.actions,
            alignmentHorizontal = additive.alignmentHorizontal ?: properties.alignmentHorizontal,
            alignmentVertical = additive.alignmentVertical ?: properties.alignmentVertical,
            alpha = additive.alpha ?: properties.alpha,
            appearanceAnimation = additive.appearanceAnimation ?: properties.appearanceAnimation,
            aspect = additive.aspect ?: properties.aspect,
            background = additive.background ?: properties.background,
            border = additive.border ?: properties.border,
            columnSpan = additive.columnSpan ?: properties.columnSpan,
            contentAlignmentHorizontal = additive.contentAlignmentHorizontal ?: properties.contentAlignmentHorizontal,
            contentAlignmentVertical = additive.contentAlignmentVertical ?: properties.contentAlignmentVertical,
            disappearActions = additive.disappearActions ?: properties.disappearActions,
            doubletapActions = additive.doubletapActions ?: properties.doubletapActions,
            extensions = additive.extensions ?: properties.extensions,
            filters = additive.filters ?: properties.filters,
            focus = additive.focus ?: properties.focus,
            height = additive.height ?: properties.height,
            highPriorityPreviewShow = additive.highPriorityPreviewShow ?: properties.highPriorityPreviewShow,
            id = additive.id ?: properties.id,
            longtapActions = additive.longtapActions ?: properties.longtapActions,
            margins = additive.margins ?: properties.margins,
            paddings = additive.paddings ?: properties.paddings,
            placeholderColor = additive.placeholderColor ?: properties.placeholderColor,
            preloadRequired = additive.preloadRequired ?: properties.preloadRequired,
            preview = additive.preview ?: properties.preview,
            rowSpan = additive.rowSpan ?: properties.rowSpan,
            scale = additive.scale ?: properties.scale,
            selectedActions = additive.selectedActions ?: properties.selectedActions,
            tintColor = additive.tintColor ?: properties.tintColor,
            tintMode = additive.tintMode ?: properties.tintMode,
            tooltips = additive.tooltips ?: properties.tooltips,
            transform = additive.transform ?: properties.transform,
            transitionChange = additive.transitionChange ?: properties.transitionChange,
            transitionIn = additive.transitionIn ?: properties.transitionIn,
            transitionOut = additive.transitionOut ?: properties.transitionOut,
            transitionTriggers = additive.transitionTriggers ?: properties.transitionTriggers,
            visibility = additive.visibility ?: properties.visibility,
            visibilityAction = additive.visibilityAction ?: properties.visibilityAction,
            visibilityActions = additive.visibilityActions ?: properties.visibilityActions,
            width = additive.width ?: properties.width,
        )
    )

    class Properties internal constructor(
        /**
         * Direct URL to an image.
         */
        val imageUrl: Property<Url>?,
        /**
         * Accessibility settings.
         */
        val accessibility: Property<Accessibility>?,
        /**
         * One action when clicking on an element. Not used if the `actions` parameter is set.
         */
        val action: Property<Action>?,
        /**
         * Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
         * Default value: `{"name": "fade", "start_value": 1, "end_value": 0.6, "duration": 100 }`.
         */
        val actionAnimation: Property<Animation>?,
        /**
         * Multiple actions when clicking on an element.
         */
        val actions: Property<List<Action>>?,
        /**
         * Horizontal alignment of an element inside the parent element.
         */
        val alignmentHorizontal: Property<AlignmentHorizontal>?,
        /**
         * Vertical alignment of an element inside the parent element.
         */
        val alignmentVertical: Property<AlignmentVertical>?,
        /**
         * Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
         * Default value: `1.0`.
         */
        val alpha: Property<Double>?,
        /**
         * Transparency animation when loading an image.
         */
        val appearanceAnimation: Property<FadeTransition>?,
        /**
         * Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
         */
        val aspect: Property<Aspect>?,
        /**
         * Element background. It can contain multiple layers.
         */
        val background: Property<List<Background>>?,
        /**
         * Element stroke.
         */
        val border: Property<Border>?,
        /**
         * Merges cells in a column of the [grid](div-grid.md) element.
         */
        val columnSpan: Property<Int>?,
        /**
         * Horizontal image alignment.
         * Default value: `center`.
         */
        val contentAlignmentHorizontal: Property<AlignmentHorizontal>?,
        /**
         * Vertical image alignment.
         * Default value: `center`.
         */
        val contentAlignmentVertical: Property<AlignmentVertical>?,
        /**
         * Actions when an element disappears from the screen.
         */
        val disappearActions: Property<List<DisappearAction>>?,
        /**
         * Action when double-clicking on an element.
         */
        val doubletapActions: Property<List<Action>>?,
        /**
         * Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
         */
        val extensions: Property<List<Extension>>?,
        /**
         * Image filters.
         */
        val filters: Property<List<Filter>>?,
        /**
         * Parameters when focusing on an element or losing focus.
         */
        val focus: Property<Focus>?,
        /**
         * Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
         * Default value: `{"type": "wrap_content"}`.
         */
        val height: Property<Size>?,
        /**
         * It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
         * Default value: `false`.
         */
        val highPriorityPreviewShow: Property<Boolean>?,
        /**
         * Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
         */
        val id: Property<String>?,
        /**
         * Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
         */
        val longtapActions: Property<List<Action>>?,
        /**
         * External margins from the element stroke.
         */
        val margins: Property<EdgeInsets>?,
        /**
         * Internal margins from the element stroke.
         */
        val paddings: Property<EdgeInsets>?,
        /**
         * Placeholder background before the image is loaded.
         * Default value: `#14000000`.
         */
        val placeholderColor: Property<Color>?,
        /**
         * Background image must be loaded before the display.
         * Default value: `false`.
         */
        val preloadRequired: Property<Boolean>?,
        /**
         * Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
         */
        val preview: Property<String>?,
        /**
         * Merges cells in a string of the [grid](div-grid.md) element.
         */
        val rowSpan: Property<Int>?,
        /**
         * Image scaling:<li>`fit` places the entire image into the element (free space is filled with background);</li><li>`fill` scales the image to the element size and cuts off the excess.</li>
         * Default value: `fill`.
         */
        val scale: Property<ImageScale>?,
        /**
         * List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
         */
        val selectedActions: Property<List<Action>>?,
        /**
         * New color of a contour image.
         */
        val tintColor: Property<Color>?,
        /**
         * Blend mode of the color specified in `tint_color`.
         * Default value: `source_in`.
         */
        val tintMode: Property<BlendMode>?,
        /**
         * Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
         */
        val tooltips: Property<List<Tooltip>>?,
        /**
         * Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
         */
        val transform: Property<Transform>?,
        /**
         * Change animation. It is played when the position or size of an element changes in the new layout.
         */
        val transitionChange: Property<ChangeTransition>?,
        /**
         * Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
         */
        val transitionIn: Property<AppearanceTransition>?,
        /**
         * Disappearance animation. It is played when an element disappears in the new layout.
         */
        val transitionOut: Property<AppearanceTransition>?,
        /**
         * Animation starting triggers. Default value: `[state_change, visibility_change]`.
         */
        val transitionTriggers: Property<List<TransitionTrigger>>?,
        /**
         * Element visibility.
         * Default value: `visible`.
         */
        val visibility: Property<Visibility>?,
        /**
         * Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
         */
        val visibilityAction: Property<VisibilityAction>?,
        /**
         * Actions when an element appears on the screen.
         */
        val visibilityActions: Property<List<VisibilityAction>>?,
        /**
         * Element width.
         * Default value: `{"type": "match_parent"}`.
         */
        val width: Property<Size>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("image_url", imageUrl)
            result.tryPutProperty("accessibility", accessibility)
            result.tryPutProperty("action", action)
            result.tryPutProperty("action_animation", actionAnimation)
            result.tryPutProperty("actions", actions)
            result.tryPutProperty("alignment_horizontal", alignmentHorizontal)
            result.tryPutProperty("alignment_vertical", alignmentVertical)
            result.tryPutProperty("alpha", alpha)
            result.tryPutProperty("appearance_animation", appearanceAnimation)
            result.tryPutProperty("aspect", aspect)
            result.tryPutProperty("background", background)
            result.tryPutProperty("border", border)
            result.tryPutProperty("column_span", columnSpan)
            result.tryPutProperty("content_alignment_horizontal", contentAlignmentHorizontal)
            result.tryPutProperty("content_alignment_vertical", contentAlignmentVertical)
            result.tryPutProperty("disappear_actions", disappearActions)
            result.tryPutProperty("doubletap_actions", doubletapActions)
            result.tryPutProperty("extensions", extensions)
            result.tryPutProperty("filters", filters)
            result.tryPutProperty("focus", focus)
            result.tryPutProperty("height", height)
            result.tryPutProperty("high_priority_preview_show", highPriorityPreviewShow)
            result.tryPutProperty("id", id)
            result.tryPutProperty("longtap_actions", longtapActions)
            result.tryPutProperty("margins", margins)
            result.tryPutProperty("paddings", paddings)
            result.tryPutProperty("placeholder_color", placeholderColor)
            result.tryPutProperty("preload_required", preloadRequired)
            result.tryPutProperty("preview", preview)
            result.tryPutProperty("row_span", rowSpan)
            result.tryPutProperty("scale", scale)
            result.tryPutProperty("selected_actions", selectedActions)
            result.tryPutProperty("tint_color", tintColor)
            result.tryPutProperty("tint_mode", tintMode)
            result.tryPutProperty("tooltips", tooltips)
            result.tryPutProperty("transform", transform)
            result.tryPutProperty("transition_change", transitionChange)
            result.tryPutProperty("transition_in", transitionIn)
            result.tryPutProperty("transition_out", transitionOut)
            result.tryPutProperty("transition_triggers", transitionTriggers)
            result.tryPutProperty("visibility", visibility)
            result.tryPutProperty("visibility_action", visibilityAction)
            result.tryPutProperty("visibility_actions", visibilityActions)
            result.tryPutProperty("width", width)
            return result
        }
    }
}

/**
 * @param imageUrl Direct URL to an image.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param appearanceAnimation Transparency animation when loading an image.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param filters Image filters.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Image scaling:<li>`fit` places the entire image into the element (free space is filled with background);</li><li>`fill` scales the image to the element size and cuts off the excess.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun DivScope.image(
    imageUrl: Url? = null,
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    appearanceAnimation: FadeTransition? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: AlignmentHorizontal? = null,
    contentAlignmentVertical: AlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    filters: List<Filter>? = null,
    focus: Focus? = null,
    height: Size? = null,
    highPriorityPreviewShow: Boolean? = null,
    id: String? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    placeholderColor: Color? = null,
    preloadRequired: Boolean? = null,
    preview: String? = null,
    rowSpan: Int? = null,
    scale: ImageScale? = null,
    selectedActions: List<Action>? = null,
    tintColor: Color? = null,
    tintMode: BlendMode? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<TransitionTrigger>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Image = Image(
    Image.Properties(
        imageUrl = valueOrNull(imageUrl),
        accessibility = valueOrNull(accessibility),
        action = valueOrNull(action),
        actionAnimation = valueOrNull(actionAnimation),
        actions = valueOrNull(actions),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        appearanceAnimation = valueOrNull(appearanceAnimation),
        aspect = valueOrNull(aspect),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnSpan = valueOrNull(columnSpan),
        contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal),
        contentAlignmentVertical = valueOrNull(contentAlignmentVertical),
        disappearActions = valueOrNull(disappearActions),
        doubletapActions = valueOrNull(doubletapActions),
        extensions = valueOrNull(extensions),
        filters = valueOrNull(filters),
        focus = valueOrNull(focus),
        height = valueOrNull(height),
        highPriorityPreviewShow = valueOrNull(highPriorityPreviewShow),
        id = valueOrNull(id),
        longtapActions = valueOrNull(longtapActions),
        margins = valueOrNull(margins),
        paddings = valueOrNull(paddings),
        placeholderColor = valueOrNull(placeholderColor),
        preloadRequired = valueOrNull(preloadRequired),
        preview = valueOrNull(preview),
        rowSpan = valueOrNull(rowSpan),
        scale = valueOrNull(scale),
        selectedActions = valueOrNull(selectedActions),
        tintColor = valueOrNull(tintColor),
        tintMode = valueOrNull(tintMode),
        tooltips = valueOrNull(tooltips),
        transform = valueOrNull(transform),
        transitionChange = valueOrNull(transitionChange),
        transitionIn = valueOrNull(transitionIn),
        transitionOut = valueOrNull(transitionOut),
        transitionTriggers = valueOrNull(transitionTriggers),
        visibility = valueOrNull(visibility),
        visibilityAction = valueOrNull(visibilityAction),
        visibilityActions = valueOrNull(visibilityActions),
        width = valueOrNull(width),
    )
)

/**
 * @param imageUrl Direct URL to an image.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param appearanceAnimation Transparency animation when loading an image.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param filters Image filters.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Image scaling:<li>`fit` places the entire image into the element (free space is filled with background);</li><li>`fill` scales the image to the element size and cuts off the excess.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun DivScope.imageProps(
    `use named arguments`: Guard = Guard.instance,
    imageUrl: Url? = null,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    appearanceAnimation: FadeTransition? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: AlignmentHorizontal? = null,
    contentAlignmentVertical: AlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    filters: List<Filter>? = null,
    focus: Focus? = null,
    height: Size? = null,
    highPriorityPreviewShow: Boolean? = null,
    id: String? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    placeholderColor: Color? = null,
    preloadRequired: Boolean? = null,
    preview: String? = null,
    rowSpan: Int? = null,
    scale: ImageScale? = null,
    selectedActions: List<Action>? = null,
    tintColor: Color? = null,
    tintMode: BlendMode? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<TransitionTrigger>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
) = Image.Properties(
    imageUrl = valueOrNull(imageUrl),
    accessibility = valueOrNull(accessibility),
    action = valueOrNull(action),
    actionAnimation = valueOrNull(actionAnimation),
    actions = valueOrNull(actions),
    alignmentHorizontal = valueOrNull(alignmentHorizontal),
    alignmentVertical = valueOrNull(alignmentVertical),
    alpha = valueOrNull(alpha),
    appearanceAnimation = valueOrNull(appearanceAnimation),
    aspect = valueOrNull(aspect),
    background = valueOrNull(background),
    border = valueOrNull(border),
    columnSpan = valueOrNull(columnSpan),
    contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal),
    contentAlignmentVertical = valueOrNull(contentAlignmentVertical),
    disappearActions = valueOrNull(disappearActions),
    doubletapActions = valueOrNull(doubletapActions),
    extensions = valueOrNull(extensions),
    filters = valueOrNull(filters),
    focus = valueOrNull(focus),
    height = valueOrNull(height),
    highPriorityPreviewShow = valueOrNull(highPriorityPreviewShow),
    id = valueOrNull(id),
    longtapActions = valueOrNull(longtapActions),
    margins = valueOrNull(margins),
    paddings = valueOrNull(paddings),
    placeholderColor = valueOrNull(placeholderColor),
    preloadRequired = valueOrNull(preloadRequired),
    preview = valueOrNull(preview),
    rowSpan = valueOrNull(rowSpan),
    scale = valueOrNull(scale),
    selectedActions = valueOrNull(selectedActions),
    tintColor = valueOrNull(tintColor),
    tintMode = valueOrNull(tintMode),
    tooltips = valueOrNull(tooltips),
    transform = valueOrNull(transform),
    transitionChange = valueOrNull(transitionChange),
    transitionIn = valueOrNull(transitionIn),
    transitionOut = valueOrNull(transitionOut),
    transitionTriggers = valueOrNull(transitionTriggers),
    visibility = valueOrNull(visibility),
    visibilityAction = valueOrNull(visibilityAction),
    visibilityActions = valueOrNull(visibilityActions),
    width = valueOrNull(width),
)

/**
 * @param imageUrl Direct URL to an image.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param appearanceAnimation Transparency animation when loading an image.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param filters Image filters.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Image scaling:<li>`fit` places the entire image into the element (free space is filled with background);</li><li>`fill` scales the image to the element size and cuts off the excess.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun TemplateScope.imageRefs(
    `use named arguments`: Guard = Guard.instance,
    imageUrl: ReferenceProperty<Url>? = null,
    accessibility: ReferenceProperty<Accessibility>? = null,
    action: ReferenceProperty<Action>? = null,
    actionAnimation: ReferenceProperty<Animation>? = null,
    actions: ReferenceProperty<List<Action>>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    appearanceAnimation: ReferenceProperty<FadeTransition>? = null,
    aspect: ReferenceProperty<Aspect>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    contentAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    filters: ReferenceProperty<List<Filter>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    highPriorityPreviewShow: ReferenceProperty<Boolean>? = null,
    id: ReferenceProperty<String>? = null,
    longtapActions: ReferenceProperty<List<Action>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    placeholderColor: ReferenceProperty<Color>? = null,
    preloadRequired: ReferenceProperty<Boolean>? = null,
    preview: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    scale: ReferenceProperty<ImageScale>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    tintColor: ReferenceProperty<Color>? = null,
    tintMode: ReferenceProperty<BlendMode>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<TransitionTrigger>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
) = Image.Properties(
    imageUrl = imageUrl,
    accessibility = accessibility,
    action = action,
    actionAnimation = actionAnimation,
    actions = actions,
    alignmentHorizontal = alignmentHorizontal,
    alignmentVertical = alignmentVertical,
    alpha = alpha,
    appearanceAnimation = appearanceAnimation,
    aspect = aspect,
    background = background,
    border = border,
    columnSpan = columnSpan,
    contentAlignmentHorizontal = contentAlignmentHorizontal,
    contentAlignmentVertical = contentAlignmentVertical,
    disappearActions = disappearActions,
    doubletapActions = doubletapActions,
    extensions = extensions,
    filters = filters,
    focus = focus,
    height = height,
    highPriorityPreviewShow = highPriorityPreviewShow,
    id = id,
    longtapActions = longtapActions,
    margins = margins,
    paddings = paddings,
    placeholderColor = placeholderColor,
    preloadRequired = preloadRequired,
    preview = preview,
    rowSpan = rowSpan,
    scale = scale,
    selectedActions = selectedActions,
    tintColor = tintColor,
    tintMode = tintMode,
    tooltips = tooltips,
    transform = transform,
    transitionChange = transitionChange,
    transitionIn = transitionIn,
    transitionOut = transitionOut,
    transitionTriggers = transitionTriggers,
    visibility = visibility,
    visibilityAction = visibilityAction,
    visibilityActions = visibilityActions,
    width = width,
)

/**
 * @param imageUrl Direct URL to an image.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param appearanceAnimation Transparency animation when loading an image.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param filters Image filters.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Image scaling:<li>`fit` places the entire image into the element (free space is filled with background);</li><li>`fill` scales the image to the element size and cuts off the excess.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Image.override(
    `use named arguments`: Guard = Guard.instance,
    imageUrl: Url? = null,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    appearanceAnimation: FadeTransition? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: AlignmentHorizontal? = null,
    contentAlignmentVertical: AlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    filters: List<Filter>? = null,
    focus: Focus? = null,
    height: Size? = null,
    highPriorityPreviewShow: Boolean? = null,
    id: String? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    placeholderColor: Color? = null,
    preloadRequired: Boolean? = null,
    preview: String? = null,
    rowSpan: Int? = null,
    scale: ImageScale? = null,
    selectedActions: List<Action>? = null,
    tintColor: Color? = null,
    tintMode: BlendMode? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<TransitionTrigger>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Image = Image(
    Image.Properties(
        imageUrl = valueOrNull(imageUrl) ?: properties.imageUrl,
        accessibility = valueOrNull(accessibility) ?: properties.accessibility,
        action = valueOrNull(action) ?: properties.action,
        actionAnimation = valueOrNull(actionAnimation) ?: properties.actionAnimation,
        actions = valueOrNull(actions) ?: properties.actions,
        alignmentHorizontal = valueOrNull(alignmentHorizontal) ?: properties.alignmentHorizontal,
        alignmentVertical = valueOrNull(alignmentVertical) ?: properties.alignmentVertical,
        alpha = valueOrNull(alpha) ?: properties.alpha,
        appearanceAnimation = valueOrNull(appearanceAnimation) ?: properties.appearanceAnimation,
        aspect = valueOrNull(aspect) ?: properties.aspect,
        background = valueOrNull(background) ?: properties.background,
        border = valueOrNull(border) ?: properties.border,
        columnSpan = valueOrNull(columnSpan) ?: properties.columnSpan,
        contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal) ?: properties.contentAlignmentHorizontal,
        contentAlignmentVertical = valueOrNull(contentAlignmentVertical) ?: properties.contentAlignmentVertical,
        disappearActions = valueOrNull(disappearActions) ?: properties.disappearActions,
        doubletapActions = valueOrNull(doubletapActions) ?: properties.doubletapActions,
        extensions = valueOrNull(extensions) ?: properties.extensions,
        filters = valueOrNull(filters) ?: properties.filters,
        focus = valueOrNull(focus) ?: properties.focus,
        height = valueOrNull(height) ?: properties.height,
        highPriorityPreviewShow = valueOrNull(highPriorityPreviewShow) ?: properties.highPriorityPreviewShow,
        id = valueOrNull(id) ?: properties.id,
        longtapActions = valueOrNull(longtapActions) ?: properties.longtapActions,
        margins = valueOrNull(margins) ?: properties.margins,
        paddings = valueOrNull(paddings) ?: properties.paddings,
        placeholderColor = valueOrNull(placeholderColor) ?: properties.placeholderColor,
        preloadRequired = valueOrNull(preloadRequired) ?: properties.preloadRequired,
        preview = valueOrNull(preview) ?: properties.preview,
        rowSpan = valueOrNull(rowSpan) ?: properties.rowSpan,
        scale = valueOrNull(scale) ?: properties.scale,
        selectedActions = valueOrNull(selectedActions) ?: properties.selectedActions,
        tintColor = valueOrNull(tintColor) ?: properties.tintColor,
        tintMode = valueOrNull(tintMode) ?: properties.tintMode,
        tooltips = valueOrNull(tooltips) ?: properties.tooltips,
        transform = valueOrNull(transform) ?: properties.transform,
        transitionChange = valueOrNull(transitionChange) ?: properties.transitionChange,
        transitionIn = valueOrNull(transitionIn) ?: properties.transitionIn,
        transitionOut = valueOrNull(transitionOut) ?: properties.transitionOut,
        transitionTriggers = valueOrNull(transitionTriggers) ?: properties.transitionTriggers,
        visibility = valueOrNull(visibility) ?: properties.visibility,
        visibilityAction = valueOrNull(visibilityAction) ?: properties.visibilityAction,
        visibilityActions = valueOrNull(visibilityActions) ?: properties.visibilityActions,
        width = valueOrNull(width) ?: properties.width,
    )
)

/**
 * @param imageUrl Direct URL to an image.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param appearanceAnimation Transparency animation when loading an image.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param filters Image filters.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Image scaling:<li>`fit` places the entire image into the element (free space is filled with background);</li><li>`fill` scales the image to the element size and cuts off the excess.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Image.defer(
    `use named arguments`: Guard = Guard.instance,
    imageUrl: ReferenceProperty<Url>? = null,
    accessibility: ReferenceProperty<Accessibility>? = null,
    action: ReferenceProperty<Action>? = null,
    actionAnimation: ReferenceProperty<Animation>? = null,
    actions: ReferenceProperty<List<Action>>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    appearanceAnimation: ReferenceProperty<FadeTransition>? = null,
    aspect: ReferenceProperty<Aspect>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    contentAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    filters: ReferenceProperty<List<Filter>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    highPriorityPreviewShow: ReferenceProperty<Boolean>? = null,
    id: ReferenceProperty<String>? = null,
    longtapActions: ReferenceProperty<List<Action>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    placeholderColor: ReferenceProperty<Color>? = null,
    preloadRequired: ReferenceProperty<Boolean>? = null,
    preview: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    scale: ReferenceProperty<ImageScale>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    tintColor: ReferenceProperty<Color>? = null,
    tintMode: ReferenceProperty<BlendMode>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<TransitionTrigger>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
): Image = Image(
    Image.Properties(
        imageUrl = imageUrl ?: properties.imageUrl,
        accessibility = accessibility ?: properties.accessibility,
        action = action ?: properties.action,
        actionAnimation = actionAnimation ?: properties.actionAnimation,
        actions = actions ?: properties.actions,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        appearanceAnimation = appearanceAnimation ?: properties.appearanceAnimation,
        aspect = aspect ?: properties.aspect,
        background = background ?: properties.background,
        border = border ?: properties.border,
        columnSpan = columnSpan ?: properties.columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal ?: properties.contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical ?: properties.contentAlignmentVertical,
        disappearActions = disappearActions ?: properties.disappearActions,
        doubletapActions = doubletapActions ?: properties.doubletapActions,
        extensions = extensions ?: properties.extensions,
        filters = filters ?: properties.filters,
        focus = focus ?: properties.focus,
        height = height ?: properties.height,
        highPriorityPreviewShow = highPriorityPreviewShow ?: properties.highPriorityPreviewShow,
        id = id ?: properties.id,
        longtapActions = longtapActions ?: properties.longtapActions,
        margins = margins ?: properties.margins,
        paddings = paddings ?: properties.paddings,
        placeholderColor = placeholderColor ?: properties.placeholderColor,
        preloadRequired = preloadRequired ?: properties.preloadRequired,
        preview = preview ?: properties.preview,
        rowSpan = rowSpan ?: properties.rowSpan,
        scale = scale ?: properties.scale,
        selectedActions = selectedActions ?: properties.selectedActions,
        tintColor = tintColor ?: properties.tintColor,
        tintMode = tintMode ?: properties.tintMode,
        tooltips = tooltips ?: properties.tooltips,
        transform = transform ?: properties.transform,
        transitionChange = transitionChange ?: properties.transitionChange,
        transitionIn = transitionIn ?: properties.transitionIn,
        transitionOut = transitionOut ?: properties.transitionOut,
        transitionTriggers = transitionTriggers ?: properties.transitionTriggers,
        visibility = visibility ?: properties.visibility,
        visibilityAction = visibilityAction ?: properties.visibilityAction,
        visibilityActions = visibilityActions ?: properties.visibilityActions,
        width = width ?: properties.width,
    )
)

/**
 * @param imageUrl Direct URL to an image.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Image scaling:<li>`fit` places the entire image into the element (free space is filled with background);</li><li>`fill` scales the image to the element size and cuts off the excess.</li>
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param visibility Element visibility.
 */
@Generated
fun Image.evaluate(
    `use named arguments`: Guard = Guard.instance,
    imageUrl: ExpressionProperty<Url>? = null,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    contentAlignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    highPriorityPreviewShow: ExpressionProperty<Boolean>? = null,
    placeholderColor: ExpressionProperty<Color>? = null,
    preloadRequired: ExpressionProperty<Boolean>? = null,
    preview: ExpressionProperty<String>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    scale: ExpressionProperty<ImageScale>? = null,
    tintColor: ExpressionProperty<Color>? = null,
    tintMode: ExpressionProperty<BlendMode>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Image = Image(
    Image.Properties(
        imageUrl = imageUrl ?: properties.imageUrl,
        accessibility = properties.accessibility,
        action = properties.action,
        actionAnimation = properties.actionAnimation,
        actions = properties.actions,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        appearanceAnimation = properties.appearanceAnimation,
        aspect = properties.aspect,
        background = properties.background,
        border = properties.border,
        columnSpan = columnSpan ?: properties.columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal ?: properties.contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical ?: properties.contentAlignmentVertical,
        disappearActions = properties.disappearActions,
        doubletapActions = properties.doubletapActions,
        extensions = properties.extensions,
        filters = properties.filters,
        focus = properties.focus,
        height = properties.height,
        highPriorityPreviewShow = highPriorityPreviewShow ?: properties.highPriorityPreviewShow,
        id = properties.id,
        longtapActions = properties.longtapActions,
        margins = properties.margins,
        paddings = properties.paddings,
        placeholderColor = placeholderColor ?: properties.placeholderColor,
        preloadRequired = preloadRequired ?: properties.preloadRequired,
        preview = preview ?: properties.preview,
        rowSpan = rowSpan ?: properties.rowSpan,
        scale = scale ?: properties.scale,
        selectedActions = properties.selectedActions,
        tintColor = tintColor ?: properties.tintColor,
        tintMode = tintMode ?: properties.tintMode,
        tooltips = properties.tooltips,
        transform = properties.transform,
        transitionChange = properties.transitionChange,
        transitionIn = properties.transitionIn,
        transitionOut = properties.transitionOut,
        transitionTriggers = properties.transitionTriggers,
        visibility = visibility ?: properties.visibility,
        visibilityAction = properties.visibilityAction,
        visibilityActions = properties.visibilityActions,
        width = properties.width,
    )
)

/**
 * @param imageUrl Direct URL to an image.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param appearanceAnimation Transparency animation when loading an image.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param filters Image filters.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Image scaling:<li>`fit` places the entire image into the element (free space is filled with background);</li><li>`fill` scales the image to the element size and cuts off the excess.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Component<Image>.override(
    `use named arguments`: Guard = Guard.instance,
    imageUrl: Url? = null,
    accessibility: Accessibility? = null,
    action: Action? = null,
    actionAnimation: Animation? = null,
    actions: List<Action>? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    appearanceAnimation: FadeTransition? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: AlignmentHorizontal? = null,
    contentAlignmentVertical: AlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    filters: List<Filter>? = null,
    focus: Focus? = null,
    height: Size? = null,
    highPriorityPreviewShow: Boolean? = null,
    id: String? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    placeholderColor: Color? = null,
    preloadRequired: Boolean? = null,
    preview: String? = null,
    rowSpan: Int? = null,
    scale: ImageScale? = null,
    selectedActions: List<Action>? = null,
    tintColor: Color? = null,
    tintMode: BlendMode? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<TransitionTrigger>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Component<Image> = Component(
    template = template,
    properties = Image.Properties(
        imageUrl = valueOrNull(imageUrl),
        accessibility = valueOrNull(accessibility),
        action = valueOrNull(action),
        actionAnimation = valueOrNull(actionAnimation),
        actions = valueOrNull(actions),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        appearanceAnimation = valueOrNull(appearanceAnimation),
        aspect = valueOrNull(aspect),
        background = valueOrNull(background),
        border = valueOrNull(border),
        columnSpan = valueOrNull(columnSpan),
        contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal),
        contentAlignmentVertical = valueOrNull(contentAlignmentVertical),
        disappearActions = valueOrNull(disappearActions),
        doubletapActions = valueOrNull(doubletapActions),
        extensions = valueOrNull(extensions),
        filters = valueOrNull(filters),
        focus = valueOrNull(focus),
        height = valueOrNull(height),
        highPriorityPreviewShow = valueOrNull(highPriorityPreviewShow),
        id = valueOrNull(id),
        longtapActions = valueOrNull(longtapActions),
        margins = valueOrNull(margins),
        paddings = valueOrNull(paddings),
        placeholderColor = valueOrNull(placeholderColor),
        preloadRequired = valueOrNull(preloadRequired),
        preview = valueOrNull(preview),
        rowSpan = valueOrNull(rowSpan),
        scale = valueOrNull(scale),
        selectedActions = valueOrNull(selectedActions),
        tintColor = valueOrNull(tintColor),
        tintMode = valueOrNull(tintMode),
        tooltips = valueOrNull(tooltips),
        transform = valueOrNull(transform),
        transitionChange = valueOrNull(transitionChange),
        transitionIn = valueOrNull(transitionIn),
        transitionOut = valueOrNull(transitionOut),
        transitionTriggers = valueOrNull(transitionTriggers),
        visibility = valueOrNull(visibility),
        visibilityAction = valueOrNull(visibilityAction),
        visibilityActions = valueOrNull(visibilityActions),
        width = valueOrNull(width),
    ).mergeWith(properties)
)

/**
 * @param imageUrl Direct URL to an image.
 * @param accessibility Accessibility settings.
 * @param action One action when clicking on an element. Not used if the `actions` parameter is set.
 * @param actionAnimation Click animation. The web only supports the following values: `fade`, `scale`, `native`, `no_animation` and `set`.
 * @param actions Multiple actions when clicking on an element.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param appearanceAnimation Transparency animation when loading an image.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param filters Image filters.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Image scaling:<li>`fit` places the entire image into the element (free space is filled with background);</li><li>`fill` scales the image to the element size and cuts off the excess.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction.dita#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Component<Image>.defer(
    `use named arguments`: Guard = Guard.instance,
    imageUrl: ReferenceProperty<Url>? = null,
    accessibility: ReferenceProperty<Accessibility>? = null,
    action: ReferenceProperty<Action>? = null,
    actionAnimation: ReferenceProperty<Animation>? = null,
    actions: ReferenceProperty<List<Action>>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    appearanceAnimation: ReferenceProperty<FadeTransition>? = null,
    aspect: ReferenceProperty<Aspect>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    contentAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    filters: ReferenceProperty<List<Filter>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    highPriorityPreviewShow: ReferenceProperty<Boolean>? = null,
    id: ReferenceProperty<String>? = null,
    longtapActions: ReferenceProperty<List<Action>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    placeholderColor: ReferenceProperty<Color>? = null,
    preloadRequired: ReferenceProperty<Boolean>? = null,
    preview: ReferenceProperty<String>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    scale: ReferenceProperty<ImageScale>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    tintColor: ReferenceProperty<Color>? = null,
    tintMode: ReferenceProperty<BlendMode>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<TransitionTrigger>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
): Component<Image> = Component(
    template = template,
    properties = Image.Properties(
        imageUrl = imageUrl,
        accessibility = accessibility,
        action = action,
        actionAnimation = actionAnimation,
        actions = actions,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        appearanceAnimation = appearanceAnimation,
        aspect = aspect,
        background = background,
        border = border,
        columnSpan = columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical,
        disappearActions = disappearActions,
        doubletapActions = doubletapActions,
        extensions = extensions,
        filters = filters,
        focus = focus,
        height = height,
        highPriorityPreviewShow = highPriorityPreviewShow,
        id = id,
        longtapActions = longtapActions,
        margins = margins,
        paddings = paddings,
        placeholderColor = placeholderColor,
        preloadRequired = preloadRequired,
        preview = preview,
        rowSpan = rowSpan,
        scale = scale,
        selectedActions = selectedActions,
        tintColor = tintColor,
        tintMode = tintMode,
        tooltips = tooltips,
        transform = transform,
        transitionChange = transitionChange,
        transitionIn = transitionIn,
        transitionOut = transitionOut,
        transitionTriggers = transitionTriggers,
        visibility = visibility,
        visibilityAction = visibilityAction,
        visibilityActions = visibilityActions,
        width = width,
    ).mergeWith(properties)
)

/**
 * @param imageUrl Direct URL to an image.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Image scaling:<li>`fit` places the entire image into the element (free space is filled with background);</li><li>`fill` scales the image to the element size and cuts off the excess.</li>
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param visibility Element visibility.
 */
@Generated
fun Component<Image>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    imageUrl: ExpressionProperty<Url>? = null,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    contentAlignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    highPriorityPreviewShow: ExpressionProperty<Boolean>? = null,
    placeholderColor: ExpressionProperty<Color>? = null,
    preloadRequired: ExpressionProperty<Boolean>? = null,
    preview: ExpressionProperty<String>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    scale: ExpressionProperty<ImageScale>? = null,
    tintColor: ExpressionProperty<Color>? = null,
    tintMode: ExpressionProperty<BlendMode>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Component<Image> = Component(
    template = template,
    properties = Image.Properties(
        imageUrl = imageUrl,
        accessibility = null,
        action = null,
        actionAnimation = null,
        actions = null,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        appearanceAnimation = null,
        aspect = null,
        background = null,
        border = null,
        columnSpan = columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical,
        disappearActions = null,
        doubletapActions = null,
        extensions = null,
        filters = null,
        focus = null,
        height = null,
        highPriorityPreviewShow = highPriorityPreviewShow,
        id = null,
        longtapActions = null,
        margins = null,
        paddings = null,
        placeholderColor = placeholderColor,
        preloadRequired = preloadRequired,
        preview = preview,
        rowSpan = rowSpan,
        scale = scale,
        selectedActions = null,
        tintColor = tintColor,
        tintMode = tintMode,
        tooltips = null,
        transform = null,
        transitionChange = null,
        transitionIn = null,
        transitionOut = null,
        transitionTriggers = null,
        visibility = visibility,
        visibilityAction = null,
        visibilityActions = null,
        width = null,
    ).mergeWith(properties)
)

@Generated
operator fun Component<Image>.plus(additive: Image.Properties): Component<Image> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun Image.asList() = listOf(this)
