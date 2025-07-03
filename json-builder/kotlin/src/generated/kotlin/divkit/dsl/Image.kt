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
data class Image internal constructor(
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
            animators = additive.animators ?: properties.animators,
            appearanceAnimation = additive.appearanceAnimation ?: properties.appearanceAnimation,
            aspect = additive.aspect ?: properties.aspect,
            background = additive.background ?: properties.background,
            border = additive.border ?: properties.border,
            captureFocusOnAction = additive.captureFocusOnAction ?: properties.captureFocusOnAction,
            columnSpan = additive.columnSpan ?: properties.columnSpan,
            contentAlignmentHorizontal = additive.contentAlignmentHorizontal ?: properties.contentAlignmentHorizontal,
            contentAlignmentVertical = additive.contentAlignmentVertical ?: properties.contentAlignmentVertical,
            disappearActions = additive.disappearActions ?: properties.disappearActions,
            doubletapActions = additive.doubletapActions ?: properties.doubletapActions,
            extensions = additive.extensions ?: properties.extensions,
            filters = additive.filters ?: properties.filters,
            focus = additive.focus ?: properties.focus,
            functions = additive.functions ?: properties.functions,
            height = additive.height ?: properties.height,
            highPriorityPreviewShow = additive.highPriorityPreviewShow ?: properties.highPriorityPreviewShow,
            hoverEndActions = additive.hoverEndActions ?: properties.hoverEndActions,
            hoverStartActions = additive.hoverStartActions ?: properties.hoverStartActions,
            id = additive.id ?: properties.id,
            layoutProvider = additive.layoutProvider ?: properties.layoutProvider,
            longtapActions = additive.longtapActions ?: properties.longtapActions,
            margins = additive.margins ?: properties.margins,
            paddings = additive.paddings ?: properties.paddings,
            placeholderColor = additive.placeholderColor ?: properties.placeholderColor,
            preloadRequired = additive.preloadRequired ?: properties.preloadRequired,
            pressEndActions = additive.pressEndActions ?: properties.pressEndActions,
            pressStartActions = additive.pressStartActions ?: properties.pressStartActions,
            preview = additive.preview ?: properties.preview,
            reuseId = additive.reuseId ?: properties.reuseId,
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
            variableTriggers = additive.variableTriggers ?: properties.variableTriggers,
            variables = additive.variables ?: properties.variables,
            visibility = additive.visibility ?: properties.visibility,
            visibilityAction = additive.visibilityAction ?: properties.visibilityAction,
            visibilityActions = additive.visibilityActions ?: properties.visibilityActions,
            width = additive.width ?: properties.width,
        )
    )

    data class Properties internal constructor(
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
         * Declaration of animators that change variable values over time.
         */
        val animators: Property<List<Animator>>?,
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
         * If the value is:<li>`true` - when the element action is activated, the focus will be moved to that element. That means that the accessibility focus will be moved and the virtual keyboard will be hidden, unless the target element implies its presence (e.g. `input`).</li><li>`false` - when you click on an element, the focus will remain on the currently focused element.</li>
         * Default value: `true`.
         */
        val captureFocusOnAction: Property<Boolean>?,
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
         * Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
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
         * User functions.
         */
        val functions: Property<List<Function>>?,
        /**
         * Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
         * Default value: `{"type": "wrap_content"}`.
         */
        val height: Property<Size>?,
        /**
         * It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
         * Default value: `false`.
         */
        val highPriorityPreviewShow: Property<Boolean>?,
        /**
         * Actions performed after hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
         */
        val hoverEndActions: Property<List<Action>>?,
        /**
         * Actions performed when hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
         */
        val hoverStartActions: Property<List<Action>>?,
        /**
         * Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
         */
        val id: Property<String>?,
        /**
         * Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
         */
        val layoutProvider: Property<LayoutProvider>?,
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
         * Actions performed after clicking/tapping an element.
         */
        val pressEndActions: Property<List<Action>>?,
        /**
         * Actions performed at the start of a click/tap on an element.
         */
        val pressStartActions: Property<List<Action>>?,
        /**
         * Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
         */
        val preview: Property<String>?,
        /**
         * ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
         */
        val reuseId: Property<String>?,
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
         * Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
         */
        val transitionIn: Property<AppearanceTransition>?,
        /**
         * Disappearance animation. It is played when an element disappears in the new layout.
         */
        val transitionOut: Property<AppearanceTransition>?,
        /**
         * Animation starting triggers. Default value: `[state_change, visibility_change]`.
         */
        val transitionTriggers: Property<List<ArrayElement<TransitionTrigger>>>?,
        /**
         * Triggers for changing variables within an element.
         */
        val variableTriggers: Property<List<Trigger>>?,
        /**
         * Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
         */
        val variables: Property<List<Variable>>?,
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
            result.tryPutProperty("animators", animators)
            result.tryPutProperty("appearance_animation", appearanceAnimation)
            result.tryPutProperty("aspect", aspect)
            result.tryPutProperty("background", background)
            result.tryPutProperty("border", border)
            result.tryPutProperty("capture_focus_on_action", captureFocusOnAction)
            result.tryPutProperty("column_span", columnSpan)
            result.tryPutProperty("content_alignment_horizontal", contentAlignmentHorizontal)
            result.tryPutProperty("content_alignment_vertical", contentAlignmentVertical)
            result.tryPutProperty("disappear_actions", disappearActions)
            result.tryPutProperty("doubletap_actions", doubletapActions)
            result.tryPutProperty("extensions", extensions)
            result.tryPutProperty("filters", filters)
            result.tryPutProperty("focus", focus)
            result.tryPutProperty("functions", functions)
            result.tryPutProperty("height", height)
            result.tryPutProperty("high_priority_preview_show", highPriorityPreviewShow)
            result.tryPutProperty("hover_end_actions", hoverEndActions)
            result.tryPutProperty("hover_start_actions", hoverStartActions)
            result.tryPutProperty("id", id)
            result.tryPutProperty("layout_provider", layoutProvider)
            result.tryPutProperty("longtap_actions", longtapActions)
            result.tryPutProperty("margins", margins)
            result.tryPutProperty("paddings", paddings)
            result.tryPutProperty("placeholder_color", placeholderColor)
            result.tryPutProperty("preload_required", preloadRequired)
            result.tryPutProperty("press_end_actions", pressEndActions)
            result.tryPutProperty("press_start_actions", pressStartActions)
            result.tryPutProperty("preview", preview)
            result.tryPutProperty("reuse_id", reuseId)
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
            result.tryPutProperty("variable_triggers", variableTriggers)
            result.tryPutProperty("variables", variables)
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
 * @param animators Declaration of animators that change variable values over time.
 * @param appearanceAnimation Transparency animation when loading an image.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param captureFocusOnAction If the value is:<li>`true` - when the element action is activated, the focus will be moved to that element. That means that the accessibility focus will be moved and the virtual keyboard will be hidden, unless the target element implies its presence (e.g. `input`).</li><li>`false` - when you click on an element, the focus will remain on the currently focused element.</li>
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param filters Image filters.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param hoverEndActions Actions performed after hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
 * @param hoverStartActions Actions performed when hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param pressEndActions Actions performed after clicking/tapping an element.
 * @param pressStartActions Actions performed at the start of a click/tap on an element.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Image scaling:<li>`fit` places the entire image into the element (free space is filled with background);</li><li>`fill` scales the image to the element size and cuts off the excess.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
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
    animators: List<Animator>? = null,
    appearanceAnimation: FadeTransition? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    captureFocusOnAction: Boolean? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: AlignmentHorizontal? = null,
    contentAlignmentVertical: AlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    filters: List<Filter>? = null,
    focus: Focus? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    highPriorityPreviewShow: Boolean? = null,
    hoverEndActions: List<Action>? = null,
    hoverStartActions: List<Action>? = null,
    id: String? = null,
    layoutProvider: LayoutProvider? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    placeholderColor: Color? = null,
    preloadRequired: Boolean? = null,
    pressEndActions: List<Action>? = null,
    pressStartActions: List<Action>? = null,
    preview: String? = null,
    reuseId: String? = null,
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
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
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
        animators = valueOrNull(animators),
        appearanceAnimation = valueOrNull(appearanceAnimation),
        aspect = valueOrNull(aspect),
        background = valueOrNull(background),
        border = valueOrNull(border),
        captureFocusOnAction = valueOrNull(captureFocusOnAction),
        columnSpan = valueOrNull(columnSpan),
        contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal),
        contentAlignmentVertical = valueOrNull(contentAlignmentVertical),
        disappearActions = valueOrNull(disappearActions),
        doubletapActions = valueOrNull(doubletapActions),
        extensions = valueOrNull(extensions),
        filters = valueOrNull(filters),
        focus = valueOrNull(focus),
        functions = valueOrNull(functions),
        height = valueOrNull(height),
        highPriorityPreviewShow = valueOrNull(highPriorityPreviewShow),
        hoverEndActions = valueOrNull(hoverEndActions),
        hoverStartActions = valueOrNull(hoverStartActions),
        id = valueOrNull(id),
        layoutProvider = valueOrNull(layoutProvider),
        longtapActions = valueOrNull(longtapActions),
        margins = valueOrNull(margins),
        paddings = valueOrNull(paddings),
        placeholderColor = valueOrNull(placeholderColor),
        preloadRequired = valueOrNull(preloadRequired),
        pressEndActions = valueOrNull(pressEndActions),
        pressStartActions = valueOrNull(pressStartActions),
        preview = valueOrNull(preview),
        reuseId = valueOrNull(reuseId),
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
        variableTriggers = valueOrNull(variableTriggers),
        variables = valueOrNull(variables),
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
 * @param animators Declaration of animators that change variable values over time.
 * @param appearanceAnimation Transparency animation when loading an image.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param captureFocusOnAction If the value is:<li>`true` - when the element action is activated, the focus will be moved to that element. That means that the accessibility focus will be moved and the virtual keyboard will be hidden, unless the target element implies its presence (e.g. `input`).</li><li>`false` - when you click on an element, the focus will remain on the currently focused element.</li>
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param filters Image filters.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param hoverEndActions Actions performed after hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
 * @param hoverStartActions Actions performed when hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param pressEndActions Actions performed after clicking/tapping an element.
 * @param pressStartActions Actions performed at the start of a click/tap on an element.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Image scaling:<li>`fit` places the entire image into the element (free space is filled with background);</li><li>`fill` scales the image to the element size and cuts off the excess.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
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
    animators: List<Animator>? = null,
    appearanceAnimation: FadeTransition? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    captureFocusOnAction: Boolean? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: AlignmentHorizontal? = null,
    contentAlignmentVertical: AlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    filters: List<Filter>? = null,
    focus: Focus? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    highPriorityPreviewShow: Boolean? = null,
    hoverEndActions: List<Action>? = null,
    hoverStartActions: List<Action>? = null,
    id: String? = null,
    layoutProvider: LayoutProvider? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    placeholderColor: Color? = null,
    preloadRequired: Boolean? = null,
    pressEndActions: List<Action>? = null,
    pressStartActions: List<Action>? = null,
    preview: String? = null,
    reuseId: String? = null,
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
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
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
    animators = valueOrNull(animators),
    appearanceAnimation = valueOrNull(appearanceAnimation),
    aspect = valueOrNull(aspect),
    background = valueOrNull(background),
    border = valueOrNull(border),
    captureFocusOnAction = valueOrNull(captureFocusOnAction),
    columnSpan = valueOrNull(columnSpan),
    contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal),
    contentAlignmentVertical = valueOrNull(contentAlignmentVertical),
    disappearActions = valueOrNull(disappearActions),
    doubletapActions = valueOrNull(doubletapActions),
    extensions = valueOrNull(extensions),
    filters = valueOrNull(filters),
    focus = valueOrNull(focus),
    functions = valueOrNull(functions),
    height = valueOrNull(height),
    highPriorityPreviewShow = valueOrNull(highPriorityPreviewShow),
    hoverEndActions = valueOrNull(hoverEndActions),
    hoverStartActions = valueOrNull(hoverStartActions),
    id = valueOrNull(id),
    layoutProvider = valueOrNull(layoutProvider),
    longtapActions = valueOrNull(longtapActions),
    margins = valueOrNull(margins),
    paddings = valueOrNull(paddings),
    placeholderColor = valueOrNull(placeholderColor),
    preloadRequired = valueOrNull(preloadRequired),
    pressEndActions = valueOrNull(pressEndActions),
    pressStartActions = valueOrNull(pressStartActions),
    preview = valueOrNull(preview),
    reuseId = valueOrNull(reuseId),
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
    variableTriggers = valueOrNull(variableTriggers),
    variables = valueOrNull(variables),
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
 * @param animators Declaration of animators that change variable values over time.
 * @param appearanceAnimation Transparency animation when loading an image.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param captureFocusOnAction If the value is:<li>`true` - when the element action is activated, the focus will be moved to that element. That means that the accessibility focus will be moved and the virtual keyboard will be hidden, unless the target element implies its presence (e.g. `input`).</li><li>`false` - when you click on an element, the focus will remain on the currently focused element.</li>
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param filters Image filters.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param hoverEndActions Actions performed after hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
 * @param hoverStartActions Actions performed when hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param pressEndActions Actions performed after clicking/tapping an element.
 * @param pressStartActions Actions performed at the start of a click/tap on an element.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Image scaling:<li>`fit` places the entire image into the element (free space is filled with background);</li><li>`fill` scales the image to the element size and cuts off the excess.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
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
    animators: ReferenceProperty<List<Animator>>? = null,
    appearanceAnimation: ReferenceProperty<FadeTransition>? = null,
    aspect: ReferenceProperty<Aspect>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    captureFocusOnAction: ReferenceProperty<Boolean>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    contentAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    filters: ReferenceProperty<List<Filter>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    functions: ReferenceProperty<List<Function>>? = null,
    height: ReferenceProperty<Size>? = null,
    highPriorityPreviewShow: ReferenceProperty<Boolean>? = null,
    hoverEndActions: ReferenceProperty<List<Action>>? = null,
    hoverStartActions: ReferenceProperty<List<Action>>? = null,
    id: ReferenceProperty<String>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    longtapActions: ReferenceProperty<List<Action>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    placeholderColor: ReferenceProperty<Color>? = null,
    preloadRequired: ReferenceProperty<Boolean>? = null,
    pressEndActions: ReferenceProperty<List<Action>>? = null,
    pressStartActions: ReferenceProperty<List<Action>>? = null,
    preview: ReferenceProperty<String>? = null,
    reuseId: ReferenceProperty<String>? = null,
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
    transitionTriggers: ReferenceProperty<List<ArrayElement<TransitionTrigger>>>? = null,
    variableTriggers: ReferenceProperty<List<Trigger>>? = null,
    variables: ReferenceProperty<List<Variable>>? = null,
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
    animators = animators,
    appearanceAnimation = appearanceAnimation,
    aspect = aspect,
    background = background,
    border = border,
    captureFocusOnAction = captureFocusOnAction,
    columnSpan = columnSpan,
    contentAlignmentHorizontal = contentAlignmentHorizontal,
    contentAlignmentVertical = contentAlignmentVertical,
    disappearActions = disappearActions,
    doubletapActions = doubletapActions,
    extensions = extensions,
    filters = filters,
    focus = focus,
    functions = functions,
    height = height,
    highPriorityPreviewShow = highPriorityPreviewShow,
    hoverEndActions = hoverEndActions,
    hoverStartActions = hoverStartActions,
    id = id,
    layoutProvider = layoutProvider,
    longtapActions = longtapActions,
    margins = margins,
    paddings = paddings,
    placeholderColor = placeholderColor,
    preloadRequired = preloadRequired,
    pressEndActions = pressEndActions,
    pressStartActions = pressStartActions,
    preview = preview,
    reuseId = reuseId,
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
    variableTriggers = variableTriggers,
    variables = variables,
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
 * @param animators Declaration of animators that change variable values over time.
 * @param appearanceAnimation Transparency animation when loading an image.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param captureFocusOnAction If the value is:<li>`true` - when the element action is activated, the focus will be moved to that element. That means that the accessibility focus will be moved and the virtual keyboard will be hidden, unless the target element implies its presence (e.g. `input`).</li><li>`false` - when you click on an element, the focus will remain on the currently focused element.</li>
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param filters Image filters.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param hoverEndActions Actions performed after hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
 * @param hoverStartActions Actions performed when hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param pressEndActions Actions performed after clicking/tapping an element.
 * @param pressStartActions Actions performed at the start of a click/tap on an element.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Image scaling:<li>`fit` places the entire image into the element (free space is filled with background);</li><li>`fill` scales the image to the element size and cuts off the excess.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
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
    animators: List<Animator>? = null,
    appearanceAnimation: FadeTransition? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    captureFocusOnAction: Boolean? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: AlignmentHorizontal? = null,
    contentAlignmentVertical: AlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    filters: List<Filter>? = null,
    focus: Focus? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    highPriorityPreviewShow: Boolean? = null,
    hoverEndActions: List<Action>? = null,
    hoverStartActions: List<Action>? = null,
    id: String? = null,
    layoutProvider: LayoutProvider? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    placeholderColor: Color? = null,
    preloadRequired: Boolean? = null,
    pressEndActions: List<Action>? = null,
    pressStartActions: List<Action>? = null,
    preview: String? = null,
    reuseId: String? = null,
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
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
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
        animators = valueOrNull(animators) ?: properties.animators,
        appearanceAnimation = valueOrNull(appearanceAnimation) ?: properties.appearanceAnimation,
        aspect = valueOrNull(aspect) ?: properties.aspect,
        background = valueOrNull(background) ?: properties.background,
        border = valueOrNull(border) ?: properties.border,
        captureFocusOnAction = valueOrNull(captureFocusOnAction) ?: properties.captureFocusOnAction,
        columnSpan = valueOrNull(columnSpan) ?: properties.columnSpan,
        contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal) ?: properties.contentAlignmentHorizontal,
        contentAlignmentVertical = valueOrNull(contentAlignmentVertical) ?: properties.contentAlignmentVertical,
        disappearActions = valueOrNull(disappearActions) ?: properties.disappearActions,
        doubletapActions = valueOrNull(doubletapActions) ?: properties.doubletapActions,
        extensions = valueOrNull(extensions) ?: properties.extensions,
        filters = valueOrNull(filters) ?: properties.filters,
        focus = valueOrNull(focus) ?: properties.focus,
        functions = valueOrNull(functions) ?: properties.functions,
        height = valueOrNull(height) ?: properties.height,
        highPriorityPreviewShow = valueOrNull(highPriorityPreviewShow) ?: properties.highPriorityPreviewShow,
        hoverEndActions = valueOrNull(hoverEndActions) ?: properties.hoverEndActions,
        hoverStartActions = valueOrNull(hoverStartActions) ?: properties.hoverStartActions,
        id = valueOrNull(id) ?: properties.id,
        layoutProvider = valueOrNull(layoutProvider) ?: properties.layoutProvider,
        longtapActions = valueOrNull(longtapActions) ?: properties.longtapActions,
        margins = valueOrNull(margins) ?: properties.margins,
        paddings = valueOrNull(paddings) ?: properties.paddings,
        placeholderColor = valueOrNull(placeholderColor) ?: properties.placeholderColor,
        preloadRequired = valueOrNull(preloadRequired) ?: properties.preloadRequired,
        pressEndActions = valueOrNull(pressEndActions) ?: properties.pressEndActions,
        pressStartActions = valueOrNull(pressStartActions) ?: properties.pressStartActions,
        preview = valueOrNull(preview) ?: properties.preview,
        reuseId = valueOrNull(reuseId) ?: properties.reuseId,
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
        variableTriggers = valueOrNull(variableTriggers) ?: properties.variableTriggers,
        variables = valueOrNull(variables) ?: properties.variables,
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
 * @param animators Declaration of animators that change variable values over time.
 * @param appearanceAnimation Transparency animation when loading an image.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param captureFocusOnAction If the value is:<li>`true` - when the element action is activated, the focus will be moved to that element. That means that the accessibility focus will be moved and the virtual keyboard will be hidden, unless the target element implies its presence (e.g. `input`).</li><li>`false` - when you click on an element, the focus will remain on the currently focused element.</li>
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param filters Image filters.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param hoverEndActions Actions performed after hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
 * @param hoverStartActions Actions performed when hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param pressEndActions Actions performed after clicking/tapping an element.
 * @param pressStartActions Actions performed at the start of a click/tap on an element.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Image scaling:<li>`fit` places the entire image into the element (free space is filled with background);</li><li>`fill` scales the image to the element size and cuts off the excess.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
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
    animators: ReferenceProperty<List<Animator>>? = null,
    appearanceAnimation: ReferenceProperty<FadeTransition>? = null,
    aspect: ReferenceProperty<Aspect>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    captureFocusOnAction: ReferenceProperty<Boolean>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    contentAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    filters: ReferenceProperty<List<Filter>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    functions: ReferenceProperty<List<Function>>? = null,
    height: ReferenceProperty<Size>? = null,
    highPriorityPreviewShow: ReferenceProperty<Boolean>? = null,
    hoverEndActions: ReferenceProperty<List<Action>>? = null,
    hoverStartActions: ReferenceProperty<List<Action>>? = null,
    id: ReferenceProperty<String>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    longtapActions: ReferenceProperty<List<Action>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    placeholderColor: ReferenceProperty<Color>? = null,
    preloadRequired: ReferenceProperty<Boolean>? = null,
    pressEndActions: ReferenceProperty<List<Action>>? = null,
    pressStartActions: ReferenceProperty<List<Action>>? = null,
    preview: ReferenceProperty<String>? = null,
    reuseId: ReferenceProperty<String>? = null,
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
    transitionTriggers: ReferenceProperty<List<ArrayElement<TransitionTrigger>>>? = null,
    variableTriggers: ReferenceProperty<List<Trigger>>? = null,
    variables: ReferenceProperty<List<Variable>>? = null,
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
        animators = animators ?: properties.animators,
        appearanceAnimation = appearanceAnimation ?: properties.appearanceAnimation,
        aspect = aspect ?: properties.aspect,
        background = background ?: properties.background,
        border = border ?: properties.border,
        captureFocusOnAction = captureFocusOnAction ?: properties.captureFocusOnAction,
        columnSpan = columnSpan ?: properties.columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal ?: properties.contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical ?: properties.contentAlignmentVertical,
        disappearActions = disappearActions ?: properties.disappearActions,
        doubletapActions = doubletapActions ?: properties.doubletapActions,
        extensions = extensions ?: properties.extensions,
        filters = filters ?: properties.filters,
        focus = focus ?: properties.focus,
        functions = functions ?: properties.functions,
        height = height ?: properties.height,
        highPriorityPreviewShow = highPriorityPreviewShow ?: properties.highPriorityPreviewShow,
        hoverEndActions = hoverEndActions ?: properties.hoverEndActions,
        hoverStartActions = hoverStartActions ?: properties.hoverStartActions,
        id = id ?: properties.id,
        layoutProvider = layoutProvider ?: properties.layoutProvider,
        longtapActions = longtapActions ?: properties.longtapActions,
        margins = margins ?: properties.margins,
        paddings = paddings ?: properties.paddings,
        placeholderColor = placeholderColor ?: properties.placeholderColor,
        preloadRequired = preloadRequired ?: properties.preloadRequired,
        pressEndActions = pressEndActions ?: properties.pressEndActions,
        pressStartActions = pressStartActions ?: properties.pressStartActions,
        preview = preview ?: properties.preview,
        reuseId = reuseId ?: properties.reuseId,
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
        variableTriggers = variableTriggers ?: properties.variableTriggers,
        variables = variables ?: properties.variables,
        visibility = visibility ?: properties.visibility,
        visibilityAction = visibilityAction ?: properties.visibilityAction,
        visibilityActions = visibilityActions ?: properties.visibilityActions,
        width = width ?: properties.width,
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
 * @param animators Declaration of animators that change variable values over time.
 * @param appearanceAnimation Transparency animation when loading an image.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param captureFocusOnAction If the value is:<li>`true` - when the element action is activated, the focus will be moved to that element. That means that the accessibility focus will be moved and the virtual keyboard will be hidden, unless the target element implies its presence (e.g. `input`).</li><li>`false` - when you click on an element, the focus will remain on the currently focused element.</li>
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param filters Image filters.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param hoverEndActions Actions performed after hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
 * @param hoverStartActions Actions performed when hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param pressEndActions Actions performed after clicking/tapping an element.
 * @param pressStartActions Actions performed at the start of a click/tap on an element.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Image scaling:<li>`fit` places the entire image into the element (free space is filled with background);</li><li>`fill` scales the image to the element size and cuts off the excess.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Image.modify(
    `use named arguments`: Guard = Guard.instance,
    imageUrl: Property<Url>? = null,
    accessibility: Property<Accessibility>? = null,
    action: Property<Action>? = null,
    actionAnimation: Property<Animation>? = null,
    actions: Property<List<Action>>? = null,
    alignmentHorizontal: Property<AlignmentHorizontal>? = null,
    alignmentVertical: Property<AlignmentVertical>? = null,
    alpha: Property<Double>? = null,
    animators: Property<List<Animator>>? = null,
    appearanceAnimation: Property<FadeTransition>? = null,
    aspect: Property<Aspect>? = null,
    background: Property<List<Background>>? = null,
    border: Property<Border>? = null,
    captureFocusOnAction: Property<Boolean>? = null,
    columnSpan: Property<Int>? = null,
    contentAlignmentHorizontal: Property<AlignmentHorizontal>? = null,
    contentAlignmentVertical: Property<AlignmentVertical>? = null,
    disappearActions: Property<List<DisappearAction>>? = null,
    doubletapActions: Property<List<Action>>? = null,
    extensions: Property<List<Extension>>? = null,
    filters: Property<List<Filter>>? = null,
    focus: Property<Focus>? = null,
    functions: Property<List<Function>>? = null,
    height: Property<Size>? = null,
    highPriorityPreviewShow: Property<Boolean>? = null,
    hoverEndActions: Property<List<Action>>? = null,
    hoverStartActions: Property<List<Action>>? = null,
    id: Property<String>? = null,
    layoutProvider: Property<LayoutProvider>? = null,
    longtapActions: Property<List<Action>>? = null,
    margins: Property<EdgeInsets>? = null,
    paddings: Property<EdgeInsets>? = null,
    placeholderColor: Property<Color>? = null,
    preloadRequired: Property<Boolean>? = null,
    pressEndActions: Property<List<Action>>? = null,
    pressStartActions: Property<List<Action>>? = null,
    preview: Property<String>? = null,
    reuseId: Property<String>? = null,
    rowSpan: Property<Int>? = null,
    scale: Property<ImageScale>? = null,
    selectedActions: Property<List<Action>>? = null,
    tintColor: Property<Color>? = null,
    tintMode: Property<BlendMode>? = null,
    tooltips: Property<List<Tooltip>>? = null,
    transform: Property<Transform>? = null,
    transitionChange: Property<ChangeTransition>? = null,
    transitionIn: Property<AppearanceTransition>? = null,
    transitionOut: Property<AppearanceTransition>? = null,
    transitionTriggers: Property<List<ArrayElement<TransitionTrigger>>>? = null,
    variableTriggers: Property<List<Trigger>>? = null,
    variables: Property<List<Variable>>? = null,
    visibility: Property<Visibility>? = null,
    visibilityAction: Property<VisibilityAction>? = null,
    visibilityActions: Property<List<VisibilityAction>>? = null,
    width: Property<Size>? = null,
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
        animators = animators ?: properties.animators,
        appearanceAnimation = appearanceAnimation ?: properties.appearanceAnimation,
        aspect = aspect ?: properties.aspect,
        background = background ?: properties.background,
        border = border ?: properties.border,
        captureFocusOnAction = captureFocusOnAction ?: properties.captureFocusOnAction,
        columnSpan = columnSpan ?: properties.columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal ?: properties.contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical ?: properties.contentAlignmentVertical,
        disappearActions = disappearActions ?: properties.disappearActions,
        doubletapActions = doubletapActions ?: properties.doubletapActions,
        extensions = extensions ?: properties.extensions,
        filters = filters ?: properties.filters,
        focus = focus ?: properties.focus,
        functions = functions ?: properties.functions,
        height = height ?: properties.height,
        highPriorityPreviewShow = highPriorityPreviewShow ?: properties.highPriorityPreviewShow,
        hoverEndActions = hoverEndActions ?: properties.hoverEndActions,
        hoverStartActions = hoverStartActions ?: properties.hoverStartActions,
        id = id ?: properties.id,
        layoutProvider = layoutProvider ?: properties.layoutProvider,
        longtapActions = longtapActions ?: properties.longtapActions,
        margins = margins ?: properties.margins,
        paddings = paddings ?: properties.paddings,
        placeholderColor = placeholderColor ?: properties.placeholderColor,
        preloadRequired = preloadRequired ?: properties.preloadRequired,
        pressEndActions = pressEndActions ?: properties.pressEndActions,
        pressStartActions = pressStartActions ?: properties.pressStartActions,
        preview = preview ?: properties.preview,
        reuseId = reuseId ?: properties.reuseId,
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
        variableTriggers = variableTriggers ?: properties.variableTriggers,
        variables = variables ?: properties.variables,
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
 * @param captureFocusOnAction If the value is:<li>`true` - when the element action is activated, the focus will be moved to that element. That means that the accessibility focus will be moved and the virtual keyboard will be hidden, unless the target element implies its presence (e.g. `input`).</li><li>`false` - when you click on an element, the focus will remain on the currently focused element.</li>
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
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
    captureFocusOnAction: ExpressionProperty<Boolean>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    contentAlignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    highPriorityPreviewShow: ExpressionProperty<Boolean>? = null,
    placeholderColor: ExpressionProperty<Color>? = null,
    preloadRequired: ExpressionProperty<Boolean>? = null,
    preview: ExpressionProperty<String>? = null,
    reuseId: ExpressionProperty<String>? = null,
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
        animators = properties.animators,
        appearanceAnimation = properties.appearanceAnimation,
        aspect = properties.aspect,
        background = properties.background,
        border = properties.border,
        captureFocusOnAction = captureFocusOnAction ?: properties.captureFocusOnAction,
        columnSpan = columnSpan ?: properties.columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal ?: properties.contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical ?: properties.contentAlignmentVertical,
        disappearActions = properties.disappearActions,
        doubletapActions = properties.doubletapActions,
        extensions = properties.extensions,
        filters = properties.filters,
        focus = properties.focus,
        functions = properties.functions,
        height = properties.height,
        highPriorityPreviewShow = highPriorityPreviewShow ?: properties.highPriorityPreviewShow,
        hoverEndActions = properties.hoverEndActions,
        hoverStartActions = properties.hoverStartActions,
        id = properties.id,
        layoutProvider = properties.layoutProvider,
        longtapActions = properties.longtapActions,
        margins = properties.margins,
        paddings = properties.paddings,
        placeholderColor = placeholderColor ?: properties.placeholderColor,
        preloadRequired = preloadRequired ?: properties.preloadRequired,
        pressEndActions = properties.pressEndActions,
        pressStartActions = properties.pressStartActions,
        preview = preview ?: properties.preview,
        reuseId = reuseId ?: properties.reuseId,
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
        variableTriggers = properties.variableTriggers,
        variables = properties.variables,
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
 * @param animators Declaration of animators that change variable values over time.
 * @param appearanceAnimation Transparency animation when loading an image.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param captureFocusOnAction If the value is:<li>`true` - when the element action is activated, the focus will be moved to that element. That means that the accessibility focus will be moved and the virtual keyboard will be hidden, unless the target element implies its presence (e.g. `input`).</li><li>`false` - when you click on an element, the focus will remain on the currently focused element.</li>
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param filters Image filters.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param hoverEndActions Actions performed after hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
 * @param hoverStartActions Actions performed when hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param pressEndActions Actions performed after clicking/tapping an element.
 * @param pressStartActions Actions performed at the start of a click/tap on an element.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Image scaling:<li>`fit` places the entire image into the element (free space is filled with background);</li><li>`fill` scales the image to the element size and cuts off the excess.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
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
    animators: List<Animator>? = null,
    appearanceAnimation: FadeTransition? = null,
    aspect: Aspect? = null,
    background: List<Background>? = null,
    border: Border? = null,
    captureFocusOnAction: Boolean? = null,
    columnSpan: Int? = null,
    contentAlignmentHorizontal: AlignmentHorizontal? = null,
    contentAlignmentVertical: AlignmentVertical? = null,
    disappearActions: List<DisappearAction>? = null,
    doubletapActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    filters: List<Filter>? = null,
    focus: Focus? = null,
    functions: List<Function>? = null,
    height: Size? = null,
    highPriorityPreviewShow: Boolean? = null,
    hoverEndActions: List<Action>? = null,
    hoverStartActions: List<Action>? = null,
    id: String? = null,
    layoutProvider: LayoutProvider? = null,
    longtapActions: List<Action>? = null,
    margins: EdgeInsets? = null,
    paddings: EdgeInsets? = null,
    placeholderColor: Color? = null,
    preloadRequired: Boolean? = null,
    pressEndActions: List<Action>? = null,
    pressStartActions: List<Action>? = null,
    preview: String? = null,
    reuseId: String? = null,
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
    transitionTriggers: List<ArrayElement<TransitionTrigger>>? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
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
        animators = valueOrNull(animators),
        appearanceAnimation = valueOrNull(appearanceAnimation),
        aspect = valueOrNull(aspect),
        background = valueOrNull(background),
        border = valueOrNull(border),
        captureFocusOnAction = valueOrNull(captureFocusOnAction),
        columnSpan = valueOrNull(columnSpan),
        contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal),
        contentAlignmentVertical = valueOrNull(contentAlignmentVertical),
        disappearActions = valueOrNull(disappearActions),
        doubletapActions = valueOrNull(doubletapActions),
        extensions = valueOrNull(extensions),
        filters = valueOrNull(filters),
        focus = valueOrNull(focus),
        functions = valueOrNull(functions),
        height = valueOrNull(height),
        highPriorityPreviewShow = valueOrNull(highPriorityPreviewShow),
        hoverEndActions = valueOrNull(hoverEndActions),
        hoverStartActions = valueOrNull(hoverStartActions),
        id = valueOrNull(id),
        layoutProvider = valueOrNull(layoutProvider),
        longtapActions = valueOrNull(longtapActions),
        margins = valueOrNull(margins),
        paddings = valueOrNull(paddings),
        placeholderColor = valueOrNull(placeholderColor),
        preloadRequired = valueOrNull(preloadRequired),
        pressEndActions = valueOrNull(pressEndActions),
        pressStartActions = valueOrNull(pressStartActions),
        preview = valueOrNull(preview),
        reuseId = valueOrNull(reuseId),
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
        variableTriggers = valueOrNull(variableTriggers),
        variables = valueOrNull(variables),
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
 * @param animators Declaration of animators that change variable values over time.
 * @param appearanceAnimation Transparency animation when loading an image.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param captureFocusOnAction If the value is:<li>`true` - when the element action is activated, the focus will be moved to that element. That means that the accessibility focus will be moved and the virtual keyboard will be hidden, unless the target element implies its presence (e.g. `input`).</li><li>`false` - when you click on an element, the focus will remain on the currently focused element.</li>
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param filters Image filters.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param hoverEndActions Actions performed after hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
 * @param hoverStartActions Actions performed when hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param pressEndActions Actions performed after clicking/tapping an element.
 * @param pressStartActions Actions performed at the start of a click/tap on an element.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Image scaling:<li>`fit` places the entire image into the element (free space is filled with background);</li><li>`fill` scales the image to the element size and cuts off the excess.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
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
    animators: ReferenceProperty<List<Animator>>? = null,
    appearanceAnimation: ReferenceProperty<FadeTransition>? = null,
    aspect: ReferenceProperty<Aspect>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    captureFocusOnAction: ReferenceProperty<Boolean>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    contentAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    doubletapActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    filters: ReferenceProperty<List<Filter>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    functions: ReferenceProperty<List<Function>>? = null,
    height: ReferenceProperty<Size>? = null,
    highPriorityPreviewShow: ReferenceProperty<Boolean>? = null,
    hoverEndActions: ReferenceProperty<List<Action>>? = null,
    hoverStartActions: ReferenceProperty<List<Action>>? = null,
    id: ReferenceProperty<String>? = null,
    layoutProvider: ReferenceProperty<LayoutProvider>? = null,
    longtapActions: ReferenceProperty<List<Action>>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    placeholderColor: ReferenceProperty<Color>? = null,
    preloadRequired: ReferenceProperty<Boolean>? = null,
    pressEndActions: ReferenceProperty<List<Action>>? = null,
    pressStartActions: ReferenceProperty<List<Action>>? = null,
    preview: ReferenceProperty<String>? = null,
    reuseId: ReferenceProperty<String>? = null,
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
    transitionTriggers: ReferenceProperty<List<ArrayElement<TransitionTrigger>>>? = null,
    variableTriggers: ReferenceProperty<List<Trigger>>? = null,
    variables: ReferenceProperty<List<Variable>>? = null,
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
        animators = animators,
        appearanceAnimation = appearanceAnimation,
        aspect = aspect,
        background = background,
        border = border,
        captureFocusOnAction = captureFocusOnAction,
        columnSpan = columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical,
        disappearActions = disappearActions,
        doubletapActions = doubletapActions,
        extensions = extensions,
        filters = filters,
        focus = focus,
        functions = functions,
        height = height,
        highPriorityPreviewShow = highPriorityPreviewShow,
        hoverEndActions = hoverEndActions,
        hoverStartActions = hoverStartActions,
        id = id,
        layoutProvider = layoutProvider,
        longtapActions = longtapActions,
        margins = margins,
        paddings = paddings,
        placeholderColor = placeholderColor,
        preloadRequired = preloadRequired,
        pressEndActions = pressEndActions,
        pressStartActions = pressStartActions,
        preview = preview,
        reuseId = reuseId,
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
        variableTriggers = variableTriggers,
        variables = variables,
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
 * @param captureFocusOnAction If the value is:<li>`true` - when the element action is activated, the focus will be moved to that element. That means that the accessibility focus will be moved and the virtual keyboard will be hidden, unless the target element implies its presence (e.g. `input`).</li><li>`false` - when you click on an element, the focus will remain on the currently focused element.</li>
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
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
    captureFocusOnAction: ExpressionProperty<Boolean>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    contentAlignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    highPriorityPreviewShow: ExpressionProperty<Boolean>? = null,
    placeholderColor: ExpressionProperty<Color>? = null,
    preloadRequired: ExpressionProperty<Boolean>? = null,
    preview: ExpressionProperty<String>? = null,
    reuseId: ExpressionProperty<String>? = null,
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
        animators = null,
        appearanceAnimation = null,
        aspect = null,
        background = null,
        border = null,
        captureFocusOnAction = captureFocusOnAction,
        columnSpan = columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical,
        disappearActions = null,
        doubletapActions = null,
        extensions = null,
        filters = null,
        focus = null,
        functions = null,
        height = null,
        highPriorityPreviewShow = highPriorityPreviewShow,
        hoverEndActions = null,
        hoverStartActions = null,
        id = null,
        layoutProvider = null,
        longtapActions = null,
        margins = null,
        paddings = null,
        placeholderColor = placeholderColor,
        preloadRequired = preloadRequired,
        pressEndActions = null,
        pressStartActions = null,
        preview = preview,
        reuseId = reuseId,
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
        variableTriggers = null,
        variables = null,
        visibility = visibility,
        visibilityAction = null,
        visibilityActions = null,
        width = null,
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
 * @param animators Declaration of animators that change variable values over time.
 * @param appearanceAnimation Transparency animation when loading an image.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param captureFocusOnAction If the value is:<li>`true` - when the element action is activated, the focus will be moved to that element. That means that the accessibility focus will be moved and the virtual keyboard will be hidden, unless the target element implies its presence (e.g. `input`).</li><li>`false` - when you click on an element, the focus will remain on the currently focused element.</li>
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param doubletapActions Action when double-clicking on an element.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions).
 * @param filters Image filters.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param functions User functions.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param highPriorityPreviewShow It sets the priority of displaying the preview — the preview is decoded in the main stream and displayed as the first frame. Use the parameter carefully — it will worsen the preview display time and can worsen the application launch time.
 * @param hoverEndActions Actions performed after hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
 * @param hoverStartActions Actions performed when hovering over an element. Available on platforms that support pointing devices (such as a mouse or stylus).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param layoutProvider Provides data on the actual size of the element. The size is calculated without taking into account the margins of the element itself.
 * @param longtapActions Action when long-clicking an element. Doesn't work on devices that don't support touch gestures.
 * @param margins External margins from the element stroke.
 * @param paddings Internal margins from the element stroke.
 * @param placeholderColor Placeholder background before the image is loaded.
 * @param preloadRequired Background image must be loaded before the display.
 * @param pressEndActions Actions performed after clicking/tapping an element.
 * @param pressStartActions Actions performed at the start of a click/tap on an element.
 * @param preview Image preview encoded in `base64`. It will be shown instead of `placeholder_color` before the image is loaded. Format `data url`: `data:[;base64],<data>`
 * @param reuseId ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md).
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Image scaling:<li>`fit` places the entire image into the element (free space is filled with background);</li><li>`fill` scales the image to the element size and cuts off the excess.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
 * @param tintColor New color of a contour image.
 * @param tintMode Blend mode of the color specified in `tint_color`.
 * @param tooltips Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id.
 * @param transform Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off.
 * @param transitionChange Change animation. It is played when the position or size of an element changes in the new layout.
 * @param transitionIn Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation).
 * @param transitionOut Disappearance animation. It is played when an element disappears in the new layout.
 * @param transitionTriggers Animation starting triggers. Default value: `[state_change, visibility_change]`.
 * @param variableTriggers Triggers for changing variables within an element.
 * @param variables Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements.
 * @param visibility Element visibility.
 * @param visibilityAction Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set.
 * @param visibilityActions Actions when an element appears on the screen.
 * @param width Element width.
 */
@Generated
fun Component<Image>.modify(
    `use named arguments`: Guard = Guard.instance,
    imageUrl: Property<Url>? = null,
    accessibility: Property<Accessibility>? = null,
    action: Property<Action>? = null,
    actionAnimation: Property<Animation>? = null,
    actions: Property<List<Action>>? = null,
    alignmentHorizontal: Property<AlignmentHorizontal>? = null,
    alignmentVertical: Property<AlignmentVertical>? = null,
    alpha: Property<Double>? = null,
    animators: Property<List<Animator>>? = null,
    appearanceAnimation: Property<FadeTransition>? = null,
    aspect: Property<Aspect>? = null,
    background: Property<List<Background>>? = null,
    border: Property<Border>? = null,
    captureFocusOnAction: Property<Boolean>? = null,
    columnSpan: Property<Int>? = null,
    contentAlignmentHorizontal: Property<AlignmentHorizontal>? = null,
    contentAlignmentVertical: Property<AlignmentVertical>? = null,
    disappearActions: Property<List<DisappearAction>>? = null,
    doubletapActions: Property<List<Action>>? = null,
    extensions: Property<List<Extension>>? = null,
    filters: Property<List<Filter>>? = null,
    focus: Property<Focus>? = null,
    functions: Property<List<Function>>? = null,
    height: Property<Size>? = null,
    highPriorityPreviewShow: Property<Boolean>? = null,
    hoverEndActions: Property<List<Action>>? = null,
    hoverStartActions: Property<List<Action>>? = null,
    id: Property<String>? = null,
    layoutProvider: Property<LayoutProvider>? = null,
    longtapActions: Property<List<Action>>? = null,
    margins: Property<EdgeInsets>? = null,
    paddings: Property<EdgeInsets>? = null,
    placeholderColor: Property<Color>? = null,
    preloadRequired: Property<Boolean>? = null,
    pressEndActions: Property<List<Action>>? = null,
    pressStartActions: Property<List<Action>>? = null,
    preview: Property<String>? = null,
    reuseId: Property<String>? = null,
    rowSpan: Property<Int>? = null,
    scale: Property<ImageScale>? = null,
    selectedActions: Property<List<Action>>? = null,
    tintColor: Property<Color>? = null,
    tintMode: Property<BlendMode>? = null,
    tooltips: Property<List<Tooltip>>? = null,
    transform: Property<Transform>? = null,
    transitionChange: Property<ChangeTransition>? = null,
    transitionIn: Property<AppearanceTransition>? = null,
    transitionOut: Property<AppearanceTransition>? = null,
    transitionTriggers: Property<List<ArrayElement<TransitionTrigger>>>? = null,
    variableTriggers: Property<List<Trigger>>? = null,
    variables: Property<List<Variable>>? = null,
    visibility: Property<Visibility>? = null,
    visibilityAction: Property<VisibilityAction>? = null,
    visibilityActions: Property<List<VisibilityAction>>? = null,
    width: Property<Size>? = null,
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
        animators = animators,
        appearanceAnimation = appearanceAnimation,
        aspect = aspect,
        background = background,
        border = border,
        captureFocusOnAction = captureFocusOnAction,
        columnSpan = columnSpan,
        contentAlignmentHorizontal = contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical,
        disappearActions = disappearActions,
        doubletapActions = doubletapActions,
        extensions = extensions,
        filters = filters,
        focus = focus,
        functions = functions,
        height = height,
        highPriorityPreviewShow = highPriorityPreviewShow,
        hoverEndActions = hoverEndActions,
        hoverStartActions = hoverStartActions,
        id = id,
        layoutProvider = layoutProvider,
        longtapActions = longtapActions,
        margins = margins,
        paddings = paddings,
        placeholderColor = placeholderColor,
        preloadRequired = preloadRequired,
        pressEndActions = pressEndActions,
        pressStartActions = pressStartActions,
        preview = preview,
        reuseId = reuseId,
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
        variableTriggers = variableTriggers,
        variables = variables,
        visibility = visibility,
        visibilityAction = visibilityAction,
        visibilityActions = visibilityActions,
        width = width,
    ).mergeWith(properties)
)

@Generated
operator fun Component<Image>.plus(additive: Image.Properties): Component<Image> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun Image.asList() = listOf(this)
