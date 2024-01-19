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
 * Video.
 * 
 * Can be created using the method [video].
 * 
 * Required parameters: `video_sources, type`.
 */
@Generated
class Video internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Div {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "video")
    )

    operator fun plus(additive: Properties): Video = Video(
        Properties(
            accessibility = additive.accessibility ?: properties.accessibility,
            alignmentHorizontal = additive.alignmentHorizontal ?: properties.alignmentHorizontal,
            alignmentVertical = additive.alignmentVertical ?: properties.alignmentVertical,
            alpha = additive.alpha ?: properties.alpha,
            aspect = additive.aspect ?: properties.aspect,
            autostart = additive.autostart ?: properties.autostart,
            background = additive.background ?: properties.background,
            border = additive.border ?: properties.border,
            bufferingActions = additive.bufferingActions ?: properties.bufferingActions,
            columnSpan = additive.columnSpan ?: properties.columnSpan,
            disappearActions = additive.disappearActions ?: properties.disappearActions,
            elapsedTimeVariable = additive.elapsedTimeVariable ?: properties.elapsedTimeVariable,
            endActions = additive.endActions ?: properties.endActions,
            extensions = additive.extensions ?: properties.extensions,
            fatalActions = additive.fatalActions ?: properties.fatalActions,
            focus = additive.focus ?: properties.focus,
            height = additive.height ?: properties.height,
            id = additive.id ?: properties.id,
            margins = additive.margins ?: properties.margins,
            muted = additive.muted ?: properties.muted,
            paddings = additive.paddings ?: properties.paddings,
            pauseActions = additive.pauseActions ?: properties.pauseActions,
            playerSettingsPayload = additive.playerSettingsPayload ?: properties.playerSettingsPayload,
            preloadRequired = additive.preloadRequired ?: properties.preloadRequired,
            preview = additive.preview ?: properties.preview,
            repeatable = additive.repeatable ?: properties.repeatable,
            resumeActions = additive.resumeActions ?: properties.resumeActions,
            rowSpan = additive.rowSpan ?: properties.rowSpan,
            scale = additive.scale ?: properties.scale,
            selectedActions = additive.selectedActions ?: properties.selectedActions,
            tooltips = additive.tooltips ?: properties.tooltips,
            transform = additive.transform ?: properties.transform,
            transitionChange = additive.transitionChange ?: properties.transitionChange,
            transitionIn = additive.transitionIn ?: properties.transitionIn,
            transitionOut = additive.transitionOut ?: properties.transitionOut,
            transitionTriggers = additive.transitionTriggers ?: properties.transitionTriggers,
            videoSources = additive.videoSources ?: properties.videoSources,
            visibility = additive.visibility ?: properties.visibility,
            visibilityAction = additive.visibilityAction ?: properties.visibilityAction,
            visibilityActions = additive.visibilityActions ?: properties.visibilityActions,
            width = additive.width ?: properties.width,
        )
    )

    class Properties internal constructor(
        /**
         * Accessibility settings.
         */
        val accessibility: Property<Accessibility>?,
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
         * Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
         */
        val aspect: Property<Aspect>?,
        /**
         * This option turns on automatic video playback. On the web, the video starts if muted playback is turned on.
         * Default value: `false`.
         */
        val autostart: Property<Boolean>?,
        /**
         * Element background. It can contain multiple layers.
         */
        val background: Property<List<Background>>?,
        /**
         * Element stroke.
         */
        val border: Property<Border>?,
        /**
         * Actions performed during video loading.
         */
        val bufferingActions: Property<List<Action>>?,
        /**
         * Merges cells in a column of the [grid](div-grid.md) element.
         */
        val columnSpan: Property<Int>?,
        /**
         * Actions when an element disappears from the screen.
         */
        val disappearActions: Property<List<DisappearAction>>?,
        /**
         * Time interval from the video beginning to the current position in milliseconds.
         */
        val elapsedTimeVariable: Property<String>?,
        /**
         * Actions performed after the video ends.
         */
        val endActions: Property<List<Action>>?,
        /**
         * Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
         */
        val extensions: Property<List<Extension>>?,
        /**
         * Actions performed when playback can't be continued due to a player error.
         */
        val fatalActions: Property<List<Action>>?,
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
         * Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
         */
        val id: Property<String>?,
        /**
         * External margins from the element stroke.
         */
        val margins: Property<EdgeInsets>?,
        /**
         * This option mutes video.
         * Default value: `false`.
         */
        val muted: Property<Boolean>?,
        /**
         * Internal margins from the element stroke.
         */
        val paddings: Property<EdgeInsets>?,
        /**
         * Actions performed when playback is paused.
         */
        val pauseActions: Property<List<Action>>?,
        /**
         * Additional information that can be used in the player.
         */
        val playerSettingsPayload: Property<Map<String, Any>>?,
        /**
         * This option turns on preloading of video sources.
         * Default value: `false`.
         */
        val preloadRequired: Property<Boolean>?,
        /**
         * Video preview encoded in `base64`. Will be shown until the video is ready to play. `Data url` format: `data:[;base64],<data>`
         */
        val preview: Property<String>?,
        /**
         * This option turns on video repeat.
         * Default value: `false`.
         */
        val repeatable: Property<Boolean>?,
        /**
         * Actions performed when video playback resumes.
         */
        val resumeActions: Property<List<Action>>?,
        /**
         * Merges cells in a string of the [grid](div-grid.md) element.
         */
        val rowSpan: Property<Int>?,
        /**
         * Video scaling:<li>'fit' places the entire video into the element (free space is filled with background);</li><li>'fill` scales the video to the element size and cuts off anything that's extra.</li>
         * Default value: `fit`.
         */
        val scale: Property<VideoScale>?,
        /**
         * List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
         */
        val selectedActions: Property<List<Action>>?,
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
        val videoSources: Property<List<VideoSource>>?,
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
            result.tryPutProperty("accessibility", accessibility)
            result.tryPutProperty("alignment_horizontal", alignmentHorizontal)
            result.tryPutProperty("alignment_vertical", alignmentVertical)
            result.tryPutProperty("alpha", alpha)
            result.tryPutProperty("aspect", aspect)
            result.tryPutProperty("autostart", autostart)
            result.tryPutProperty("background", background)
            result.tryPutProperty("border", border)
            result.tryPutProperty("buffering_actions", bufferingActions)
            result.tryPutProperty("column_span", columnSpan)
            result.tryPutProperty("disappear_actions", disappearActions)
            result.tryPutProperty("elapsed_time_variable", elapsedTimeVariable)
            result.tryPutProperty("end_actions", endActions)
            result.tryPutProperty("extensions", extensions)
            result.tryPutProperty("fatal_actions", fatalActions)
            result.tryPutProperty("focus", focus)
            result.tryPutProperty("height", height)
            result.tryPutProperty("id", id)
            result.tryPutProperty("margins", margins)
            result.tryPutProperty("muted", muted)
            result.tryPutProperty("paddings", paddings)
            result.tryPutProperty("pause_actions", pauseActions)
            result.tryPutProperty("player_settings_payload", playerSettingsPayload)
            result.tryPutProperty("preload_required", preloadRequired)
            result.tryPutProperty("preview", preview)
            result.tryPutProperty("repeatable", repeatable)
            result.tryPutProperty("resume_actions", resumeActions)
            result.tryPutProperty("row_span", rowSpan)
            result.tryPutProperty("scale", scale)
            result.tryPutProperty("selected_actions", selectedActions)
            result.tryPutProperty("tooltips", tooltips)
            result.tryPutProperty("transform", transform)
            result.tryPutProperty("transition_change", transitionChange)
            result.tryPutProperty("transition_in", transitionIn)
            result.tryPutProperty("transition_out", transitionOut)
            result.tryPutProperty("transition_triggers", transitionTriggers)
            result.tryPutProperty("video_sources", videoSources)
            result.tryPutProperty("visibility", visibility)
            result.tryPutProperty("visibility_action", visibilityAction)
            result.tryPutProperty("visibility_actions", visibilityActions)
            result.tryPutProperty("width", width)
            return result
        }
    }
}

/**
 * @param accessibility Accessibility settings.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param autostart This option turns on automatic video playback. On the web, the video starts if muted playback is turned on.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param bufferingActions Actions performed during video loading.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param elapsedTimeVariable Time interval from the video beginning to the current position in milliseconds.
 * @param endActions Actions performed after the video ends.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param fatalActions Actions performed when playback can't be continued due to a player error.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param margins External margins from the element stroke.
 * @param muted This option mutes video.
 * @param paddings Internal margins from the element stroke.
 * @param pauseActions Actions performed when playback is paused.
 * @param playerSettingsPayload Additional information that can be used in the player.
 * @param preloadRequired This option turns on preloading of video sources.
 * @param preview Video preview encoded in `base64`. Will be shown until the video is ready to play. `Data url` format: `data:[;base64],<data>`
 * @param repeatable This option turns on video repeat.
 * @param resumeActions Actions performed when video playback resumes.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Video scaling:<li>'fit' places the entire video into the element (free space is filled with background);</li><li>'fill` scales the video to the element size and cuts off anything that's extra.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
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
fun DivScope.video(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    aspect: Aspect? = null,
    autostart: Boolean? = null,
    background: List<Background>? = null,
    border: Border? = null,
    bufferingActions: List<Action>? = null,
    columnSpan: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    elapsedTimeVariable: String? = null,
    endActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    fatalActions: List<Action>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    margins: EdgeInsets? = null,
    muted: Boolean? = null,
    paddings: EdgeInsets? = null,
    pauseActions: List<Action>? = null,
    playerSettingsPayload: Map<String, Any>? = null,
    preloadRequired: Boolean? = null,
    preview: String? = null,
    repeatable: Boolean? = null,
    resumeActions: List<Action>? = null,
    rowSpan: Int? = null,
    scale: VideoScale? = null,
    selectedActions: List<Action>? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<TransitionTrigger>? = null,
    videoSources: List<VideoSource>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Video = Video(
    Video.Properties(
        accessibility = valueOrNull(accessibility),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        aspect = valueOrNull(aspect),
        autostart = valueOrNull(autostart),
        background = valueOrNull(background),
        border = valueOrNull(border),
        bufferingActions = valueOrNull(bufferingActions),
        columnSpan = valueOrNull(columnSpan),
        disappearActions = valueOrNull(disappearActions),
        elapsedTimeVariable = valueOrNull(elapsedTimeVariable),
        endActions = valueOrNull(endActions),
        extensions = valueOrNull(extensions),
        fatalActions = valueOrNull(fatalActions),
        focus = valueOrNull(focus),
        height = valueOrNull(height),
        id = valueOrNull(id),
        margins = valueOrNull(margins),
        muted = valueOrNull(muted),
        paddings = valueOrNull(paddings),
        pauseActions = valueOrNull(pauseActions),
        playerSettingsPayload = valueOrNull(playerSettingsPayload),
        preloadRequired = valueOrNull(preloadRequired),
        preview = valueOrNull(preview),
        repeatable = valueOrNull(repeatable),
        resumeActions = valueOrNull(resumeActions),
        rowSpan = valueOrNull(rowSpan),
        scale = valueOrNull(scale),
        selectedActions = valueOrNull(selectedActions),
        tooltips = valueOrNull(tooltips),
        transform = valueOrNull(transform),
        transitionChange = valueOrNull(transitionChange),
        transitionIn = valueOrNull(transitionIn),
        transitionOut = valueOrNull(transitionOut),
        transitionTriggers = valueOrNull(transitionTriggers),
        videoSources = valueOrNull(videoSources),
        visibility = valueOrNull(visibility),
        visibilityAction = valueOrNull(visibilityAction),
        visibilityActions = valueOrNull(visibilityActions),
        width = valueOrNull(width),
    )
)

/**
 * @param accessibility Accessibility settings.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param autostart This option turns on automatic video playback. On the web, the video starts if muted playback is turned on.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param bufferingActions Actions performed during video loading.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param elapsedTimeVariable Time interval from the video beginning to the current position in milliseconds.
 * @param endActions Actions performed after the video ends.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param fatalActions Actions performed when playback can't be continued due to a player error.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param margins External margins from the element stroke.
 * @param muted This option mutes video.
 * @param paddings Internal margins from the element stroke.
 * @param pauseActions Actions performed when playback is paused.
 * @param playerSettingsPayload Additional information that can be used in the player.
 * @param preloadRequired This option turns on preloading of video sources.
 * @param preview Video preview encoded in `base64`. Will be shown until the video is ready to play. `Data url` format: `data:[;base64],<data>`
 * @param repeatable This option turns on video repeat.
 * @param resumeActions Actions performed when video playback resumes.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Video scaling:<li>'fit' places the entire video into the element (free space is filled with background);</li><li>'fill` scales the video to the element size and cuts off anything that's extra.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
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
fun DivScope.videoProps(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    aspect: Aspect? = null,
    autostart: Boolean? = null,
    background: List<Background>? = null,
    border: Border? = null,
    bufferingActions: List<Action>? = null,
    columnSpan: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    elapsedTimeVariable: String? = null,
    endActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    fatalActions: List<Action>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    margins: EdgeInsets? = null,
    muted: Boolean? = null,
    paddings: EdgeInsets? = null,
    pauseActions: List<Action>? = null,
    playerSettingsPayload: Map<String, Any>? = null,
    preloadRequired: Boolean? = null,
    preview: String? = null,
    repeatable: Boolean? = null,
    resumeActions: List<Action>? = null,
    rowSpan: Int? = null,
    scale: VideoScale? = null,
    selectedActions: List<Action>? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<TransitionTrigger>? = null,
    videoSources: List<VideoSource>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
) = Video.Properties(
    accessibility = valueOrNull(accessibility),
    alignmentHorizontal = valueOrNull(alignmentHorizontal),
    alignmentVertical = valueOrNull(alignmentVertical),
    alpha = valueOrNull(alpha),
    aspect = valueOrNull(aspect),
    autostart = valueOrNull(autostart),
    background = valueOrNull(background),
    border = valueOrNull(border),
    bufferingActions = valueOrNull(bufferingActions),
    columnSpan = valueOrNull(columnSpan),
    disappearActions = valueOrNull(disappearActions),
    elapsedTimeVariable = valueOrNull(elapsedTimeVariable),
    endActions = valueOrNull(endActions),
    extensions = valueOrNull(extensions),
    fatalActions = valueOrNull(fatalActions),
    focus = valueOrNull(focus),
    height = valueOrNull(height),
    id = valueOrNull(id),
    margins = valueOrNull(margins),
    muted = valueOrNull(muted),
    paddings = valueOrNull(paddings),
    pauseActions = valueOrNull(pauseActions),
    playerSettingsPayload = valueOrNull(playerSettingsPayload),
    preloadRequired = valueOrNull(preloadRequired),
    preview = valueOrNull(preview),
    repeatable = valueOrNull(repeatable),
    resumeActions = valueOrNull(resumeActions),
    rowSpan = valueOrNull(rowSpan),
    scale = valueOrNull(scale),
    selectedActions = valueOrNull(selectedActions),
    tooltips = valueOrNull(tooltips),
    transform = valueOrNull(transform),
    transitionChange = valueOrNull(transitionChange),
    transitionIn = valueOrNull(transitionIn),
    transitionOut = valueOrNull(transitionOut),
    transitionTriggers = valueOrNull(transitionTriggers),
    videoSources = valueOrNull(videoSources),
    visibility = valueOrNull(visibility),
    visibilityAction = valueOrNull(visibilityAction),
    visibilityActions = valueOrNull(visibilityActions),
    width = valueOrNull(width),
)

/**
 * @param accessibility Accessibility settings.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param autostart This option turns on automatic video playback. On the web, the video starts if muted playback is turned on.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param bufferingActions Actions performed during video loading.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param elapsedTimeVariable Time interval from the video beginning to the current position in milliseconds.
 * @param endActions Actions performed after the video ends.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param fatalActions Actions performed when playback can't be continued due to a player error.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param margins External margins from the element stroke.
 * @param muted This option mutes video.
 * @param paddings Internal margins from the element stroke.
 * @param pauseActions Actions performed when playback is paused.
 * @param playerSettingsPayload Additional information that can be used in the player.
 * @param preloadRequired This option turns on preloading of video sources.
 * @param preview Video preview encoded in `base64`. Will be shown until the video is ready to play. `Data url` format: `data:[;base64],<data>`
 * @param repeatable This option turns on video repeat.
 * @param resumeActions Actions performed when video playback resumes.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Video scaling:<li>'fit' places the entire video into the element (free space is filled with background);</li><li>'fill` scales the video to the element size and cuts off anything that's extra.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
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
fun TemplateScope.videoRefs(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    aspect: ReferenceProperty<Aspect>? = null,
    autostart: ReferenceProperty<Boolean>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    bufferingActions: ReferenceProperty<List<Action>>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    elapsedTimeVariable: ReferenceProperty<String>? = null,
    endActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    fatalActions: ReferenceProperty<List<Action>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    muted: ReferenceProperty<Boolean>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    pauseActions: ReferenceProperty<List<Action>>? = null,
    playerSettingsPayload: ReferenceProperty<Map<String, Any>>? = null,
    preloadRequired: ReferenceProperty<Boolean>? = null,
    preview: ReferenceProperty<String>? = null,
    repeatable: ReferenceProperty<Boolean>? = null,
    resumeActions: ReferenceProperty<List<Action>>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    scale: ReferenceProperty<VideoScale>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<TransitionTrigger>>? = null,
    videoSources: ReferenceProperty<List<VideoSource>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
) = Video.Properties(
    accessibility = accessibility,
    alignmentHorizontal = alignmentHorizontal,
    alignmentVertical = alignmentVertical,
    alpha = alpha,
    aspect = aspect,
    autostart = autostart,
    background = background,
    border = border,
    bufferingActions = bufferingActions,
    columnSpan = columnSpan,
    disappearActions = disappearActions,
    elapsedTimeVariable = elapsedTimeVariable,
    endActions = endActions,
    extensions = extensions,
    fatalActions = fatalActions,
    focus = focus,
    height = height,
    id = id,
    margins = margins,
    muted = muted,
    paddings = paddings,
    pauseActions = pauseActions,
    playerSettingsPayload = playerSettingsPayload,
    preloadRequired = preloadRequired,
    preview = preview,
    repeatable = repeatable,
    resumeActions = resumeActions,
    rowSpan = rowSpan,
    scale = scale,
    selectedActions = selectedActions,
    tooltips = tooltips,
    transform = transform,
    transitionChange = transitionChange,
    transitionIn = transitionIn,
    transitionOut = transitionOut,
    transitionTriggers = transitionTriggers,
    videoSources = videoSources,
    visibility = visibility,
    visibilityAction = visibilityAction,
    visibilityActions = visibilityActions,
    width = width,
)

/**
 * @param accessibility Accessibility settings.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param autostart This option turns on automatic video playback. On the web, the video starts if muted playback is turned on.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param bufferingActions Actions performed during video loading.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param elapsedTimeVariable Time interval from the video beginning to the current position in milliseconds.
 * @param endActions Actions performed after the video ends.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param fatalActions Actions performed when playback can't be continued due to a player error.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param margins External margins from the element stroke.
 * @param muted This option mutes video.
 * @param paddings Internal margins from the element stroke.
 * @param pauseActions Actions performed when playback is paused.
 * @param playerSettingsPayload Additional information that can be used in the player.
 * @param preloadRequired This option turns on preloading of video sources.
 * @param preview Video preview encoded in `base64`. Will be shown until the video is ready to play. `Data url` format: `data:[;base64],<data>`
 * @param repeatable This option turns on video repeat.
 * @param resumeActions Actions performed when video playback resumes.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Video scaling:<li>'fit' places the entire video into the element (free space is filled with background);</li><li>'fill` scales the video to the element size and cuts off anything that's extra.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
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
fun Video.override(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    aspect: Aspect? = null,
    autostart: Boolean? = null,
    background: List<Background>? = null,
    border: Border? = null,
    bufferingActions: List<Action>? = null,
    columnSpan: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    elapsedTimeVariable: String? = null,
    endActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    fatalActions: List<Action>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    margins: EdgeInsets? = null,
    muted: Boolean? = null,
    paddings: EdgeInsets? = null,
    pauseActions: List<Action>? = null,
    playerSettingsPayload: Map<String, Any>? = null,
    preloadRequired: Boolean? = null,
    preview: String? = null,
    repeatable: Boolean? = null,
    resumeActions: List<Action>? = null,
    rowSpan: Int? = null,
    scale: VideoScale? = null,
    selectedActions: List<Action>? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<TransitionTrigger>? = null,
    videoSources: List<VideoSource>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Video = Video(
    Video.Properties(
        accessibility = valueOrNull(accessibility) ?: properties.accessibility,
        alignmentHorizontal = valueOrNull(alignmentHorizontal) ?: properties.alignmentHorizontal,
        alignmentVertical = valueOrNull(alignmentVertical) ?: properties.alignmentVertical,
        alpha = valueOrNull(alpha) ?: properties.alpha,
        aspect = valueOrNull(aspect) ?: properties.aspect,
        autostart = valueOrNull(autostart) ?: properties.autostart,
        background = valueOrNull(background) ?: properties.background,
        border = valueOrNull(border) ?: properties.border,
        bufferingActions = valueOrNull(bufferingActions) ?: properties.bufferingActions,
        columnSpan = valueOrNull(columnSpan) ?: properties.columnSpan,
        disappearActions = valueOrNull(disappearActions) ?: properties.disappearActions,
        elapsedTimeVariable = valueOrNull(elapsedTimeVariable) ?: properties.elapsedTimeVariable,
        endActions = valueOrNull(endActions) ?: properties.endActions,
        extensions = valueOrNull(extensions) ?: properties.extensions,
        fatalActions = valueOrNull(fatalActions) ?: properties.fatalActions,
        focus = valueOrNull(focus) ?: properties.focus,
        height = valueOrNull(height) ?: properties.height,
        id = valueOrNull(id) ?: properties.id,
        margins = valueOrNull(margins) ?: properties.margins,
        muted = valueOrNull(muted) ?: properties.muted,
        paddings = valueOrNull(paddings) ?: properties.paddings,
        pauseActions = valueOrNull(pauseActions) ?: properties.pauseActions,
        playerSettingsPayload = valueOrNull(playerSettingsPayload) ?: properties.playerSettingsPayload,
        preloadRequired = valueOrNull(preloadRequired) ?: properties.preloadRequired,
        preview = valueOrNull(preview) ?: properties.preview,
        repeatable = valueOrNull(repeatable) ?: properties.repeatable,
        resumeActions = valueOrNull(resumeActions) ?: properties.resumeActions,
        rowSpan = valueOrNull(rowSpan) ?: properties.rowSpan,
        scale = valueOrNull(scale) ?: properties.scale,
        selectedActions = valueOrNull(selectedActions) ?: properties.selectedActions,
        tooltips = valueOrNull(tooltips) ?: properties.tooltips,
        transform = valueOrNull(transform) ?: properties.transform,
        transitionChange = valueOrNull(transitionChange) ?: properties.transitionChange,
        transitionIn = valueOrNull(transitionIn) ?: properties.transitionIn,
        transitionOut = valueOrNull(transitionOut) ?: properties.transitionOut,
        transitionTriggers = valueOrNull(transitionTriggers) ?: properties.transitionTriggers,
        videoSources = valueOrNull(videoSources) ?: properties.videoSources,
        visibility = valueOrNull(visibility) ?: properties.visibility,
        visibilityAction = valueOrNull(visibilityAction) ?: properties.visibilityAction,
        visibilityActions = valueOrNull(visibilityActions) ?: properties.visibilityActions,
        width = valueOrNull(width) ?: properties.width,
    )
)

/**
 * @param accessibility Accessibility settings.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param autostart This option turns on automatic video playback. On the web, the video starts if muted playback is turned on.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param bufferingActions Actions performed during video loading.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param elapsedTimeVariable Time interval from the video beginning to the current position in milliseconds.
 * @param endActions Actions performed after the video ends.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param fatalActions Actions performed when playback can't be continued due to a player error.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param margins External margins from the element stroke.
 * @param muted This option mutes video.
 * @param paddings Internal margins from the element stroke.
 * @param pauseActions Actions performed when playback is paused.
 * @param playerSettingsPayload Additional information that can be used in the player.
 * @param preloadRequired This option turns on preloading of video sources.
 * @param preview Video preview encoded in `base64`. Will be shown until the video is ready to play. `Data url` format: `data:[;base64],<data>`
 * @param repeatable This option turns on video repeat.
 * @param resumeActions Actions performed when video playback resumes.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Video scaling:<li>'fit' places the entire video into the element (free space is filled with background);</li><li>'fill` scales the video to the element size and cuts off anything that's extra.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
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
fun Video.defer(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    aspect: ReferenceProperty<Aspect>? = null,
    autostart: ReferenceProperty<Boolean>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    bufferingActions: ReferenceProperty<List<Action>>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    elapsedTimeVariable: ReferenceProperty<String>? = null,
    endActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    fatalActions: ReferenceProperty<List<Action>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    muted: ReferenceProperty<Boolean>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    pauseActions: ReferenceProperty<List<Action>>? = null,
    playerSettingsPayload: ReferenceProperty<Map<String, Any>>? = null,
    preloadRequired: ReferenceProperty<Boolean>? = null,
    preview: ReferenceProperty<String>? = null,
    repeatable: ReferenceProperty<Boolean>? = null,
    resumeActions: ReferenceProperty<List<Action>>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    scale: ReferenceProperty<VideoScale>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<TransitionTrigger>>? = null,
    videoSources: ReferenceProperty<List<VideoSource>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
): Video = Video(
    Video.Properties(
        accessibility = accessibility ?: properties.accessibility,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        aspect = aspect ?: properties.aspect,
        autostart = autostart ?: properties.autostart,
        background = background ?: properties.background,
        border = border ?: properties.border,
        bufferingActions = bufferingActions ?: properties.bufferingActions,
        columnSpan = columnSpan ?: properties.columnSpan,
        disappearActions = disappearActions ?: properties.disappearActions,
        elapsedTimeVariable = elapsedTimeVariable ?: properties.elapsedTimeVariable,
        endActions = endActions ?: properties.endActions,
        extensions = extensions ?: properties.extensions,
        fatalActions = fatalActions ?: properties.fatalActions,
        focus = focus ?: properties.focus,
        height = height ?: properties.height,
        id = id ?: properties.id,
        margins = margins ?: properties.margins,
        muted = muted ?: properties.muted,
        paddings = paddings ?: properties.paddings,
        pauseActions = pauseActions ?: properties.pauseActions,
        playerSettingsPayload = playerSettingsPayload ?: properties.playerSettingsPayload,
        preloadRequired = preloadRequired ?: properties.preloadRequired,
        preview = preview ?: properties.preview,
        repeatable = repeatable ?: properties.repeatable,
        resumeActions = resumeActions ?: properties.resumeActions,
        rowSpan = rowSpan ?: properties.rowSpan,
        scale = scale ?: properties.scale,
        selectedActions = selectedActions ?: properties.selectedActions,
        tooltips = tooltips ?: properties.tooltips,
        transform = transform ?: properties.transform,
        transitionChange = transitionChange ?: properties.transitionChange,
        transitionIn = transitionIn ?: properties.transitionIn,
        transitionOut = transitionOut ?: properties.transitionOut,
        transitionTriggers = transitionTriggers ?: properties.transitionTriggers,
        videoSources = videoSources ?: properties.videoSources,
        visibility = visibility ?: properties.visibility,
        visibilityAction = visibilityAction ?: properties.visibilityAction,
        visibilityActions = visibilityActions ?: properties.visibilityActions,
        width = width ?: properties.width,
    )
)

/**
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param autostart This option turns on automatic video playback. On the web, the video starts if muted playback is turned on.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param muted This option mutes video.
 * @param preloadRequired This option turns on preloading of video sources.
 * @param preview Video preview encoded in `base64`. Will be shown until the video is ready to play. `Data url` format: `data:[;base64],<data>`
 * @param repeatable This option turns on video repeat.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Video scaling:<li>'fit' places the entire video into the element (free space is filled with background);</li><li>'fill` scales the video to the element size and cuts off anything that's extra.</li>
 * @param visibility Element visibility.
 */
@Generated
fun Video.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    autostart: ExpressionProperty<Boolean>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    muted: ExpressionProperty<Boolean>? = null,
    preloadRequired: ExpressionProperty<Boolean>? = null,
    preview: ExpressionProperty<String>? = null,
    repeatable: ExpressionProperty<Boolean>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    scale: ExpressionProperty<VideoScale>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Video = Video(
    Video.Properties(
        accessibility = properties.accessibility,
        alignmentHorizontal = alignmentHorizontal ?: properties.alignmentHorizontal,
        alignmentVertical = alignmentVertical ?: properties.alignmentVertical,
        alpha = alpha ?: properties.alpha,
        aspect = properties.aspect,
        autostart = autostart ?: properties.autostart,
        background = properties.background,
        border = properties.border,
        bufferingActions = properties.bufferingActions,
        columnSpan = columnSpan ?: properties.columnSpan,
        disappearActions = properties.disappearActions,
        elapsedTimeVariable = properties.elapsedTimeVariable,
        endActions = properties.endActions,
        extensions = properties.extensions,
        fatalActions = properties.fatalActions,
        focus = properties.focus,
        height = properties.height,
        id = properties.id,
        margins = properties.margins,
        muted = muted ?: properties.muted,
        paddings = properties.paddings,
        pauseActions = properties.pauseActions,
        playerSettingsPayload = properties.playerSettingsPayload,
        preloadRequired = preloadRequired ?: properties.preloadRequired,
        preview = preview ?: properties.preview,
        repeatable = repeatable ?: properties.repeatable,
        resumeActions = properties.resumeActions,
        rowSpan = rowSpan ?: properties.rowSpan,
        scale = scale ?: properties.scale,
        selectedActions = properties.selectedActions,
        tooltips = properties.tooltips,
        transform = properties.transform,
        transitionChange = properties.transitionChange,
        transitionIn = properties.transitionIn,
        transitionOut = properties.transitionOut,
        transitionTriggers = properties.transitionTriggers,
        videoSources = properties.videoSources,
        visibility = visibility ?: properties.visibility,
        visibilityAction = properties.visibilityAction,
        visibilityActions = properties.visibilityActions,
        width = properties.width,
    )
)

/**
 * @param accessibility Accessibility settings.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param autostart This option turns on automatic video playback. On the web, the video starts if muted playback is turned on.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param bufferingActions Actions performed during video loading.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param elapsedTimeVariable Time interval from the video beginning to the current position in milliseconds.
 * @param endActions Actions performed after the video ends.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param fatalActions Actions performed when playback can't be continued due to a player error.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param margins External margins from the element stroke.
 * @param muted This option mutes video.
 * @param paddings Internal margins from the element stroke.
 * @param pauseActions Actions performed when playback is paused.
 * @param playerSettingsPayload Additional information that can be used in the player.
 * @param preloadRequired This option turns on preloading of video sources.
 * @param preview Video preview encoded in `base64`. Will be shown until the video is ready to play. `Data url` format: `data:[;base64],<data>`
 * @param repeatable This option turns on video repeat.
 * @param resumeActions Actions performed when video playback resumes.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Video scaling:<li>'fit' places the entire video into the element (free space is filled with background);</li><li>'fill` scales the video to the element size and cuts off anything that's extra.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
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
fun Component<Video>.override(
    `use named arguments`: Guard = Guard.instance,
    accessibility: Accessibility? = null,
    alignmentHorizontal: AlignmentHorizontal? = null,
    alignmentVertical: AlignmentVertical? = null,
    alpha: Double? = null,
    aspect: Aspect? = null,
    autostart: Boolean? = null,
    background: List<Background>? = null,
    border: Border? = null,
    bufferingActions: List<Action>? = null,
    columnSpan: Int? = null,
    disappearActions: List<DisappearAction>? = null,
    elapsedTimeVariable: String? = null,
    endActions: List<Action>? = null,
    extensions: List<Extension>? = null,
    fatalActions: List<Action>? = null,
    focus: Focus? = null,
    height: Size? = null,
    id: String? = null,
    margins: EdgeInsets? = null,
    muted: Boolean? = null,
    paddings: EdgeInsets? = null,
    pauseActions: List<Action>? = null,
    playerSettingsPayload: Map<String, Any>? = null,
    preloadRequired: Boolean? = null,
    preview: String? = null,
    repeatable: Boolean? = null,
    resumeActions: List<Action>? = null,
    rowSpan: Int? = null,
    scale: VideoScale? = null,
    selectedActions: List<Action>? = null,
    tooltips: List<Tooltip>? = null,
    transform: Transform? = null,
    transitionChange: ChangeTransition? = null,
    transitionIn: AppearanceTransition? = null,
    transitionOut: AppearanceTransition? = null,
    transitionTriggers: List<TransitionTrigger>? = null,
    videoSources: List<VideoSource>? = null,
    visibility: Visibility? = null,
    visibilityAction: VisibilityAction? = null,
    visibilityActions: List<VisibilityAction>? = null,
    width: Size? = null,
): Component<Video> = Component(
    template = template,
    properties = Video.Properties(
        accessibility = valueOrNull(accessibility),
        alignmentHorizontal = valueOrNull(alignmentHorizontal),
        alignmentVertical = valueOrNull(alignmentVertical),
        alpha = valueOrNull(alpha),
        aspect = valueOrNull(aspect),
        autostart = valueOrNull(autostart),
        background = valueOrNull(background),
        border = valueOrNull(border),
        bufferingActions = valueOrNull(bufferingActions),
        columnSpan = valueOrNull(columnSpan),
        disappearActions = valueOrNull(disappearActions),
        elapsedTimeVariable = valueOrNull(elapsedTimeVariable),
        endActions = valueOrNull(endActions),
        extensions = valueOrNull(extensions),
        fatalActions = valueOrNull(fatalActions),
        focus = valueOrNull(focus),
        height = valueOrNull(height),
        id = valueOrNull(id),
        margins = valueOrNull(margins),
        muted = valueOrNull(muted),
        paddings = valueOrNull(paddings),
        pauseActions = valueOrNull(pauseActions),
        playerSettingsPayload = valueOrNull(playerSettingsPayload),
        preloadRequired = valueOrNull(preloadRequired),
        preview = valueOrNull(preview),
        repeatable = valueOrNull(repeatable),
        resumeActions = valueOrNull(resumeActions),
        rowSpan = valueOrNull(rowSpan),
        scale = valueOrNull(scale),
        selectedActions = valueOrNull(selectedActions),
        tooltips = valueOrNull(tooltips),
        transform = valueOrNull(transform),
        transitionChange = valueOrNull(transitionChange),
        transitionIn = valueOrNull(transitionIn),
        transitionOut = valueOrNull(transitionOut),
        transitionTriggers = valueOrNull(transitionTriggers),
        videoSources = valueOrNull(videoSources),
        visibility = valueOrNull(visibility),
        visibilityAction = valueOrNull(visibilityAction),
        visibilityActions = valueOrNull(visibilityActions),
        width = valueOrNull(width),
    ).mergeWith(properties)
)

/**
 * @param accessibility Accessibility settings.
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param aspect Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * @param autostart This option turns on automatic video playback. On the web, the video starts if muted playback is turned on.
 * @param background Element background. It can contain multiple layers.
 * @param border Element stroke.
 * @param bufferingActions Actions performed during video loading.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param disappearActions Actions when an element disappears from the screen.
 * @param elapsedTimeVariable Time interval from the video beginning to the current position in milliseconds.
 * @param endActions Actions performed after the video ends.
 * @param extensions Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions.dita).
 * @param fatalActions Actions performed when playback can't be continued due to a player error.
 * @param focus Parameters when focusing on an element or losing focus.
 * @param height Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param id Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS.
 * @param margins External margins from the element stroke.
 * @param muted This option mutes video.
 * @param paddings Internal margins from the element stroke.
 * @param pauseActions Actions performed when playback is paused.
 * @param playerSettingsPayload Additional information that can be used in the player.
 * @param preloadRequired This option turns on preloading of video sources.
 * @param preview Video preview encoded in `base64`. Will be shown until the video is ready to play. `Data url` format: `data:[;base64],<data>`
 * @param repeatable This option turns on video repeat.
 * @param resumeActions Actions performed when video playback resumes.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Video scaling:<li>'fit' places the entire video into the element (free space is filled with background);</li><li>'fill` scales the video to the element size and cuts off anything that's extra.</li>
 * @param selectedActions List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md).
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
fun Component<Video>.defer(
    `use named arguments`: Guard = Guard.instance,
    accessibility: ReferenceProperty<Accessibility>? = null,
    alignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    alpha: ReferenceProperty<Double>? = null,
    aspect: ReferenceProperty<Aspect>? = null,
    autostart: ReferenceProperty<Boolean>? = null,
    background: ReferenceProperty<List<Background>>? = null,
    border: ReferenceProperty<Border>? = null,
    bufferingActions: ReferenceProperty<List<Action>>? = null,
    columnSpan: ReferenceProperty<Int>? = null,
    disappearActions: ReferenceProperty<List<DisappearAction>>? = null,
    elapsedTimeVariable: ReferenceProperty<String>? = null,
    endActions: ReferenceProperty<List<Action>>? = null,
    extensions: ReferenceProperty<List<Extension>>? = null,
    fatalActions: ReferenceProperty<List<Action>>? = null,
    focus: ReferenceProperty<Focus>? = null,
    height: ReferenceProperty<Size>? = null,
    id: ReferenceProperty<String>? = null,
    margins: ReferenceProperty<EdgeInsets>? = null,
    muted: ReferenceProperty<Boolean>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
    pauseActions: ReferenceProperty<List<Action>>? = null,
    playerSettingsPayload: ReferenceProperty<Map<String, Any>>? = null,
    preloadRequired: ReferenceProperty<Boolean>? = null,
    preview: ReferenceProperty<String>? = null,
    repeatable: ReferenceProperty<Boolean>? = null,
    resumeActions: ReferenceProperty<List<Action>>? = null,
    rowSpan: ReferenceProperty<Int>? = null,
    scale: ReferenceProperty<VideoScale>? = null,
    selectedActions: ReferenceProperty<List<Action>>? = null,
    tooltips: ReferenceProperty<List<Tooltip>>? = null,
    transform: ReferenceProperty<Transform>? = null,
    transitionChange: ReferenceProperty<ChangeTransition>? = null,
    transitionIn: ReferenceProperty<AppearanceTransition>? = null,
    transitionOut: ReferenceProperty<AppearanceTransition>? = null,
    transitionTriggers: ReferenceProperty<List<TransitionTrigger>>? = null,
    videoSources: ReferenceProperty<List<VideoSource>>? = null,
    visibility: ReferenceProperty<Visibility>? = null,
    visibilityAction: ReferenceProperty<VisibilityAction>? = null,
    visibilityActions: ReferenceProperty<List<VisibilityAction>>? = null,
    width: ReferenceProperty<Size>? = null,
): Component<Video> = Component(
    template = template,
    properties = Video.Properties(
        accessibility = accessibility,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        aspect = aspect,
        autostart = autostart,
        background = background,
        border = border,
        bufferingActions = bufferingActions,
        columnSpan = columnSpan,
        disappearActions = disappearActions,
        elapsedTimeVariable = elapsedTimeVariable,
        endActions = endActions,
        extensions = extensions,
        fatalActions = fatalActions,
        focus = focus,
        height = height,
        id = id,
        margins = margins,
        muted = muted,
        paddings = paddings,
        pauseActions = pauseActions,
        playerSettingsPayload = playerSettingsPayload,
        preloadRequired = preloadRequired,
        preview = preview,
        repeatable = repeatable,
        resumeActions = resumeActions,
        rowSpan = rowSpan,
        scale = scale,
        selectedActions = selectedActions,
        tooltips = tooltips,
        transform = transform,
        transitionChange = transitionChange,
        transitionIn = transitionIn,
        transitionOut = transitionOut,
        transitionTriggers = transitionTriggers,
        videoSources = videoSources,
        visibility = visibility,
        visibilityAction = visibilityAction,
        visibilityActions = visibilityActions,
        width = width,
    ).mergeWith(properties)
)

/**
 * @param alignmentHorizontal Horizontal alignment of an element inside the parent element.
 * @param alignmentVertical Vertical alignment of an element inside the parent element.
 * @param alpha Sets transparency of the entire element: `0` — completely transparent, `1` — opaque.
 * @param autostart This option turns on automatic video playback. On the web, the video starts if muted playback is turned on.
 * @param columnSpan Merges cells in a column of the [grid](div-grid.md) element.
 * @param muted This option mutes video.
 * @param preloadRequired This option turns on preloading of video sources.
 * @param preview Video preview encoded in `base64`. Will be shown until the video is ready to play. `Data url` format: `data:[;base64],<data>`
 * @param repeatable This option turns on video repeat.
 * @param rowSpan Merges cells in a string of the [grid](div-grid.md) element.
 * @param scale Video scaling:<li>'fit' places the entire video into the element (free space is filled with background);</li><li>'fill` scales the video to the element size and cuts off anything that's extra.</li>
 * @param visibility Element visibility.
 */
@Generated
fun Component<Video>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    alignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    alpha: ExpressionProperty<Double>? = null,
    autostart: ExpressionProperty<Boolean>? = null,
    columnSpan: ExpressionProperty<Int>? = null,
    muted: ExpressionProperty<Boolean>? = null,
    preloadRequired: ExpressionProperty<Boolean>? = null,
    preview: ExpressionProperty<String>? = null,
    repeatable: ExpressionProperty<Boolean>? = null,
    rowSpan: ExpressionProperty<Int>? = null,
    scale: ExpressionProperty<VideoScale>? = null,
    visibility: ExpressionProperty<Visibility>? = null,
): Component<Video> = Component(
    template = template,
    properties = Video.Properties(
        accessibility = null,
        alignmentHorizontal = alignmentHorizontal,
        alignmentVertical = alignmentVertical,
        alpha = alpha,
        aspect = null,
        autostart = autostart,
        background = null,
        border = null,
        bufferingActions = null,
        columnSpan = columnSpan,
        disappearActions = null,
        elapsedTimeVariable = null,
        endActions = null,
        extensions = null,
        fatalActions = null,
        focus = null,
        height = null,
        id = null,
        margins = null,
        muted = muted,
        paddings = null,
        pauseActions = null,
        playerSettingsPayload = null,
        preloadRequired = preloadRequired,
        preview = preview,
        repeatable = repeatable,
        resumeActions = null,
        rowSpan = rowSpan,
        scale = scale,
        selectedActions = null,
        tooltips = null,
        transform = null,
        transitionChange = null,
        transitionIn = null,
        transitionOut = null,
        transitionTriggers = null,
        videoSources = null,
        visibility = visibility,
        visibilityAction = null,
        visibilityActions = null,
        width = null,
    ).mergeWith(properties)
)

@Generated
operator fun Component<Video>.plus(additive: Video.Properties): Component<Video> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun Video.asList() = listOf(this)
