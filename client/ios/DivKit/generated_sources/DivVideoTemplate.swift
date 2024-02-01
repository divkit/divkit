// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivVideoTemplate: TemplateValue {
  public static let type: String = "video"
  public let parent: String?
  public let accessibility: Field<DivAccessibilityTemplate>?
  public let alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>?
  public let alignmentVertical: Field<Expression<DivAlignmentVertical>>?
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let aspect: Field<DivAspectTemplate>?
  public let autostart: Field<Expression<Bool>>? // default value: false
  public let background: Field<[DivBackgroundTemplate]>?
  public let border: Field<DivBorderTemplate>?
  public let bufferingActions: Field<[DivActionTemplate]>?
  public let columnSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let disappearActions: Field<[DivDisappearActionTemplate]>?
  public let elapsedTimeVariable: Field<String>?
  public let endActions: Field<[DivActionTemplate]>?
  public let extensions: Field<[DivExtensionTemplate]>?
  public let fatalActions: Field<[DivActionTemplate]>?
  public let focus: Field<DivFocusTemplate>?
  public let height: Field<DivSizeTemplate>? // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: Field<String>?
  public let margins: Field<DivEdgeInsetsTemplate>?
  public let muted: Field<Expression<Bool>>? // default value: false
  public let paddings: Field<DivEdgeInsetsTemplate>?
  public let pauseActions: Field<[DivActionTemplate]>?
  public let playerSettingsPayload: Field<[String: Any]>?
  public let preloadRequired: Field<Expression<Bool>>? // default value: false
  public let preview: Field<Expression<String>>?
  public let repeatable: Field<Expression<Bool>>? // default value: false
  public let resumeActions: Field<[DivActionTemplate]>?
  public let rowSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let scale: Field<Expression<DivVideoScale>>? // default value: fit
  public let selectedActions: Field<[DivActionTemplate]>?
  public let tooltips: Field<[DivTooltipTemplate]>?
  public let transform: Field<DivTransformTemplate>?
  public let transitionChange: Field<DivChangeTransitionTemplate>?
  public let transitionIn: Field<DivAppearanceTransitionTemplate>?
  public let transitionOut: Field<DivAppearanceTransitionTemplate>?
  public let transitionTriggers: Field<[DivTransitionTrigger]>? // at least 1 elements
  public let videoSources: Field<[DivVideoSourceTemplate]>? // at least 1 elements
  public let visibility: Field<Expression<DivVisibility>>? // default value: visible
  public let visibilityAction: Field<DivVisibilityActionTemplate>?
  public let visibilityActions: Field<[DivVisibilityActionTemplate]>?
  public let width: Field<DivSizeTemplate>? // default value: .divMatchParentSize(DivMatchParentSize())

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      accessibility: dictionary.getOptionalField("accessibility", templateToType: templateToType),
      alignmentHorizontal: dictionary.getOptionalExpressionField("alignment_horizontal"),
      alignmentVertical: dictionary.getOptionalExpressionField("alignment_vertical"),
      alpha: dictionary.getOptionalExpressionField("alpha"),
      aspect: dictionary.getOptionalField("aspect", templateToType: templateToType),
      autostart: dictionary.getOptionalExpressionField("autostart"),
      background: dictionary.getOptionalArray("background", templateToType: templateToType),
      border: dictionary.getOptionalField("border", templateToType: templateToType),
      bufferingActions: dictionary.getOptionalArray("buffering_actions", templateToType: templateToType),
      columnSpan: dictionary.getOptionalExpressionField("column_span"),
      disappearActions: dictionary.getOptionalArray("disappear_actions", templateToType: templateToType),
      elapsedTimeVariable: dictionary.getOptionalField("elapsed_time_variable"),
      endActions: dictionary.getOptionalArray("end_actions", templateToType: templateToType),
      extensions: dictionary.getOptionalArray("extensions", templateToType: templateToType),
      fatalActions: dictionary.getOptionalArray("fatal_actions", templateToType: templateToType),
      focus: dictionary.getOptionalField("focus", templateToType: templateToType),
      height: dictionary.getOptionalField("height", templateToType: templateToType),
      id: dictionary.getOptionalField("id"),
      margins: dictionary.getOptionalField("margins", templateToType: templateToType),
      muted: dictionary.getOptionalExpressionField("muted"),
      paddings: dictionary.getOptionalField("paddings", templateToType: templateToType),
      pauseActions: dictionary.getOptionalArray("pause_actions", templateToType: templateToType),
      playerSettingsPayload: dictionary.getOptionalField("player_settings_payload"),
      preloadRequired: dictionary.getOptionalExpressionField("preload_required"),
      preview: dictionary.getOptionalExpressionField("preview"),
      repeatable: dictionary.getOptionalExpressionField("repeatable"),
      resumeActions: dictionary.getOptionalArray("resume_actions", templateToType: templateToType),
      rowSpan: dictionary.getOptionalExpressionField("row_span"),
      scale: dictionary.getOptionalExpressionField("scale"),
      selectedActions: dictionary.getOptionalArray("selected_actions", templateToType: templateToType),
      tooltips: dictionary.getOptionalArray("tooltips", templateToType: templateToType),
      transform: dictionary.getOptionalField("transform", templateToType: templateToType),
      transitionChange: dictionary.getOptionalField("transition_change", templateToType: templateToType),
      transitionIn: dictionary.getOptionalField("transition_in", templateToType: templateToType),
      transitionOut: dictionary.getOptionalField("transition_out", templateToType: templateToType),
      transitionTriggers: dictionary.getOptionalArray("transition_triggers"),
      videoSources: dictionary.getOptionalArray("video_sources", templateToType: templateToType),
      visibility: dictionary.getOptionalExpressionField("visibility"),
      visibilityAction: dictionary.getOptionalField("visibility_action", templateToType: templateToType),
      visibilityActions: dictionary.getOptionalArray("visibility_actions", templateToType: templateToType),
      width: dictionary.getOptionalField("width", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    accessibility: Field<DivAccessibilityTemplate>? = nil,
    alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? = nil,
    alignmentVertical: Field<Expression<DivAlignmentVertical>>? = nil,
    alpha: Field<Expression<Double>>? = nil,
    aspect: Field<DivAspectTemplate>? = nil,
    autostart: Field<Expression<Bool>>? = nil,
    background: Field<[DivBackgroundTemplate]>? = nil,
    border: Field<DivBorderTemplate>? = nil,
    bufferingActions: Field<[DivActionTemplate]>? = nil,
    columnSpan: Field<Expression<Int>>? = nil,
    disappearActions: Field<[DivDisappearActionTemplate]>? = nil,
    elapsedTimeVariable: Field<String>? = nil,
    endActions: Field<[DivActionTemplate]>? = nil,
    extensions: Field<[DivExtensionTemplate]>? = nil,
    fatalActions: Field<[DivActionTemplate]>? = nil,
    focus: Field<DivFocusTemplate>? = nil,
    height: Field<DivSizeTemplate>? = nil,
    id: Field<String>? = nil,
    margins: Field<DivEdgeInsetsTemplate>? = nil,
    muted: Field<Expression<Bool>>? = nil,
    paddings: Field<DivEdgeInsetsTemplate>? = nil,
    pauseActions: Field<[DivActionTemplate]>? = nil,
    playerSettingsPayload: Field<[String: Any]>? = nil,
    preloadRequired: Field<Expression<Bool>>? = nil,
    preview: Field<Expression<String>>? = nil,
    repeatable: Field<Expression<Bool>>? = nil,
    resumeActions: Field<[DivActionTemplate]>? = nil,
    rowSpan: Field<Expression<Int>>? = nil,
    scale: Field<Expression<DivVideoScale>>? = nil,
    selectedActions: Field<[DivActionTemplate]>? = nil,
    tooltips: Field<[DivTooltipTemplate]>? = nil,
    transform: Field<DivTransformTemplate>? = nil,
    transitionChange: Field<DivChangeTransitionTemplate>? = nil,
    transitionIn: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionOut: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionTriggers: Field<[DivTransitionTrigger]>? = nil,
    videoSources: Field<[DivVideoSourceTemplate]>? = nil,
    visibility: Field<Expression<DivVisibility>>? = nil,
    visibilityAction: Field<DivVisibilityActionTemplate>? = nil,
    visibilityActions: Field<[DivVisibilityActionTemplate]>? = nil,
    width: Field<DivSizeTemplate>? = nil
  ) {
    self.parent = parent
    self.accessibility = accessibility
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha
    self.aspect = aspect
    self.autostart = autostart
    self.background = background
    self.border = border
    self.bufferingActions = bufferingActions
    self.columnSpan = columnSpan
    self.disappearActions = disappearActions
    self.elapsedTimeVariable = elapsedTimeVariable
    self.endActions = endActions
    self.extensions = extensions
    self.fatalActions = fatalActions
    self.focus = focus
    self.height = height
    self.id = id
    self.margins = margins
    self.muted = muted
    self.paddings = paddings
    self.pauseActions = pauseActions
    self.playerSettingsPayload = playerSettingsPayload
    self.preloadRequired = preloadRequired
    self.preview = preview
    self.repeatable = repeatable
    self.resumeActions = resumeActions
    self.rowSpan = rowSpan
    self.scale = scale
    self.selectedActions = selectedActions
    self.tooltips = tooltips
    self.transform = transform
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.videoSources = videoSources
    self.visibility = visibility
    self.visibilityAction = visibilityAction
    self.visibilityActions = visibilityActions
    self.width = width
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivVideoTemplate?) -> DeserializationResult<DivVideo> {
    let accessibilityValue = parent?.accessibility?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let alignmentHorizontalValue = parent?.alignmentHorizontal?.resolveOptionalValue(context: context) ?? .noValue
    let alignmentVerticalValue = parent?.alignmentVertical?.resolveOptionalValue(context: context) ?? .noValue
    let alphaValue = parent?.alpha?.resolveOptionalValue(context: context, validator: ResolvedValue.alphaValidator) ?? .noValue
    let aspectValue = parent?.aspect?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let autostartValue = parent?.autostart?.resolveOptionalValue(context: context) ?? .noValue
    let backgroundValue = parent?.background?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let borderValue = parent?.border?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let bufferingActionsValue = parent?.bufferingActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let columnSpanValue = parent?.columnSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.columnSpanValidator) ?? .noValue
    let disappearActionsValue = parent?.disappearActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let elapsedTimeVariableValue = parent?.elapsedTimeVariable?.resolveOptionalValue(context: context) ?? .noValue
    let endActionsValue = parent?.endActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let extensionsValue = parent?.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let fatalActionsValue = parent?.fatalActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let focusValue = parent?.focus?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let heightValue = parent?.height?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let idValue = parent?.id?.resolveOptionalValue(context: context) ?? .noValue
    let marginsValue = parent?.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let mutedValue = parent?.muted?.resolveOptionalValue(context: context) ?? .noValue
    let paddingsValue = parent?.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let pauseActionsValue = parent?.pauseActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let playerSettingsPayloadValue = parent?.playerSettingsPayload?.resolveOptionalValue(context: context) ?? .noValue
    let preloadRequiredValue = parent?.preloadRequired?.resolveOptionalValue(context: context) ?? .noValue
    let previewValue = parent?.preview?.resolveOptionalValue(context: context) ?? .noValue
    let repeatableValue = parent?.repeatable?.resolveOptionalValue(context: context) ?? .noValue
    let resumeActionsValue = parent?.resumeActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let rowSpanValue = parent?.rowSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.rowSpanValidator) ?? .noValue
    let scaleValue = parent?.scale?.resolveOptionalValue(context: context) ?? .noValue
    let selectedActionsValue = parent?.selectedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let tooltipsValue = parent?.tooltips?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transformValue = parent?.transform?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionChangeValue = parent?.transitionChange?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionInValue = parent?.transitionIn?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionOutValue = parent?.transitionOut?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionTriggersValue = parent?.transitionTriggers?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionTriggersValidator) ?? .noValue
    let videoSourcesValue = parent?.videoSources?.resolveValue(context: context, validator: ResolvedValue.videoSourcesValidator, useOnlyLinks: true) ?? .noValue
    let visibilityValue = parent?.visibility?.resolveOptionalValue(context: context) ?? .noValue
    let visibilityActionValue = parent?.visibilityAction?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let visibilityActionsValue = parent?.visibilityActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let widthValue = parent?.width?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      aspectValue.errorsOrWarnings?.map { .nestedObjectError(field: "aspect", error: $0) },
      autostartValue.errorsOrWarnings?.map { .nestedObjectError(field: "autostart", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      bufferingActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "buffering_actions", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      disappearActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_actions", error: $0) },
      elapsedTimeVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "elapsed_time_variable", error: $0) },
      endActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "end_actions", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      fatalActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "fatal_actions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      mutedValue.errorsOrWarnings?.map { .nestedObjectError(field: "muted", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      pauseActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "pause_actions", error: $0) },
      playerSettingsPayloadValue.errorsOrWarnings?.map { .nestedObjectError(field: "player_settings_payload", error: $0) },
      preloadRequiredValue.errorsOrWarnings?.map { .nestedObjectError(field: "preload_required", error: $0) },
      previewValue.errorsOrWarnings?.map { .nestedObjectError(field: "preview", error: $0) },
      repeatableValue.errorsOrWarnings?.map { .nestedObjectError(field: "repeatable", error: $0) },
      resumeActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "resume_actions", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      scaleValue.errorsOrWarnings?.map { .nestedObjectError(field: "scale", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      videoSourcesValue.errorsOrWarnings?.map { .nestedObjectError(field: "video_sources", error: $0) },
      visibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility", error: $0) },
      visibilityActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_action", error: $0) },
      visibilityActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_actions", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    if case .noValue = videoSourcesValue {
      errors.append(.requiredFieldIsMissing(field: "video_sources"))
    }
    guard
      let videoSourcesNonNil = videoSourcesValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivVideo(
      accessibility: accessibilityValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      aspect: aspectValue.value,
      autostart: autostartValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      bufferingActions: bufferingActionsValue.value,
      columnSpan: columnSpanValue.value,
      disappearActions: disappearActionsValue.value,
      elapsedTimeVariable: elapsedTimeVariableValue.value,
      endActions: endActionsValue.value,
      extensions: extensionsValue.value,
      fatalActions: fatalActionsValue.value,
      focus: focusValue.value,
      height: heightValue.value,
      id: idValue.value,
      margins: marginsValue.value,
      muted: mutedValue.value,
      paddings: paddingsValue.value,
      pauseActions: pauseActionsValue.value,
      playerSettingsPayload: playerSettingsPayloadValue.value,
      preloadRequired: preloadRequiredValue.value,
      preview: previewValue.value,
      repeatable: repeatableValue.value,
      resumeActions: resumeActionsValue.value,
      rowSpan: rowSpanValue.value,
      scale: scaleValue.value,
      selectedActions: selectedActionsValue.value,
      tooltips: tooltipsValue.value,
      transform: transformValue.value,
      transitionChange: transitionChangeValue.value,
      transitionIn: transitionInValue.value,
      transitionOut: transitionOutValue.value,
      transitionTriggers: transitionTriggersValue.value,
      videoSources: videoSourcesNonNil,
      visibility: visibilityValue.value,
      visibilityAction: visibilityActionValue.value,
      visibilityActions: visibilityActionsValue.value,
      width: widthValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivVideoTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivVideo> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var accessibilityValue: DeserializationResult<DivAccessibility> = .noValue
    var alignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> = parent?.alignmentHorizontal?.value() ?? .noValue
    var alignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = parent?.alignmentVertical?.value() ?? .noValue
    var alphaValue: DeserializationResult<Expression<Double>> = parent?.alpha?.value() ?? .noValue
    var aspectValue: DeserializationResult<DivAspect> = .noValue
    var autostartValue: DeserializationResult<Expression<Bool>> = parent?.autostart?.value() ?? .noValue
    var backgroundValue: DeserializationResult<[DivBackground]> = .noValue
    var borderValue: DeserializationResult<DivBorder> = .noValue
    var bufferingActionsValue: DeserializationResult<[DivAction]> = .noValue
    var columnSpanValue: DeserializationResult<Expression<Int>> = parent?.columnSpan?.value() ?? .noValue
    var disappearActionsValue: DeserializationResult<[DivDisappearAction]> = .noValue
    var elapsedTimeVariableValue: DeserializationResult<String> = parent?.elapsedTimeVariable?.value() ?? .noValue
    var endActionsValue: DeserializationResult<[DivAction]> = .noValue
    var extensionsValue: DeserializationResult<[DivExtension]> = .noValue
    var fatalActionsValue: DeserializationResult<[DivAction]> = .noValue
    var focusValue: DeserializationResult<DivFocus> = .noValue
    var heightValue: DeserializationResult<DivSize> = .noValue
    var idValue: DeserializationResult<String> = parent?.id?.value() ?? .noValue
    var marginsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var mutedValue: DeserializationResult<Expression<Bool>> = parent?.muted?.value() ?? .noValue
    var paddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var pauseActionsValue: DeserializationResult<[DivAction]> = .noValue
    var playerSettingsPayloadValue: DeserializationResult<[String: Any]> = parent?.playerSettingsPayload?.value() ?? .noValue
    var preloadRequiredValue: DeserializationResult<Expression<Bool>> = parent?.preloadRequired?.value() ?? .noValue
    var previewValue: DeserializationResult<Expression<String>> = parent?.preview?.value() ?? .noValue
    var repeatableValue: DeserializationResult<Expression<Bool>> = parent?.repeatable?.value() ?? .noValue
    var resumeActionsValue: DeserializationResult<[DivAction]> = .noValue
    var rowSpanValue: DeserializationResult<Expression<Int>> = parent?.rowSpan?.value() ?? .noValue
    var scaleValue: DeserializationResult<Expression<DivVideoScale>> = parent?.scale?.value() ?? .noValue
    var selectedActionsValue: DeserializationResult<[DivAction]> = .noValue
    var tooltipsValue: DeserializationResult<[DivTooltip]> = .noValue
    var transformValue: DeserializationResult<DivTransform> = .noValue
    var transitionChangeValue: DeserializationResult<DivChangeTransition> = .noValue
    var transitionInValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionOutValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionTriggersValue: DeserializationResult<[DivTransitionTrigger]> = parent?.transitionTriggers?.value(validatedBy: ResolvedValue.transitionTriggersValidator) ?? .noValue
    var videoSourcesValue: DeserializationResult<[DivVideoSource]> = .noValue
    var visibilityValue: DeserializationResult<Expression<DivVisibility>> = parent?.visibility?.value() ?? .noValue
    var visibilityActionValue: DeserializationResult<DivVisibilityAction> = .noValue
    var visibilityActionsValue: DeserializationResult<[DivVisibilityAction]> = .noValue
    var widthValue: DeserializationResult<DivSize> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "accessibility":
        accessibilityValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAccessibilityTemplate.self).merged(with: accessibilityValue)
      case "alignment_horizontal":
        alignmentHorizontalValue = deserialize(__dictValue).merged(with: alignmentHorizontalValue)
      case "alignment_vertical":
        alignmentVerticalValue = deserialize(__dictValue).merged(with: alignmentVerticalValue)
      case "alpha":
        alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator).merged(with: alphaValue)
      case "aspect":
        aspectValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAspectTemplate.self).merged(with: aspectValue)
      case "autostart":
        autostartValue = deserialize(__dictValue).merged(with: autostartValue)
      case "background":
        backgroundValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBackgroundTemplate.self).merged(with: backgroundValue)
      case "border":
        borderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBorderTemplate.self).merged(with: borderValue)
      case "buffering_actions":
        bufferingActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: bufferingActionsValue)
      case "column_span":
        columnSpanValue = deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator).merged(with: columnSpanValue)
      case "disappear_actions":
        disappearActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDisappearActionTemplate.self).merged(with: disappearActionsValue)
      case "elapsed_time_variable":
        elapsedTimeVariableValue = deserialize(__dictValue).merged(with: elapsedTimeVariableValue)
      case "end_actions":
        endActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: endActionsValue)
      case "extensions":
        extensionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivExtensionTemplate.self).merged(with: extensionsValue)
      case "fatal_actions":
        fatalActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: fatalActionsValue)
      case "focus":
        focusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFocusTemplate.self).merged(with: focusValue)
      case "height":
        heightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self).merged(with: heightValue)
      case "id":
        idValue = deserialize(__dictValue).merged(with: idValue)
      case "margins":
        marginsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).merged(with: marginsValue)
      case "muted":
        mutedValue = deserialize(__dictValue).merged(with: mutedValue)
      case "paddings":
        paddingsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).merged(with: paddingsValue)
      case "pause_actions":
        pauseActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: pauseActionsValue)
      case "player_settings_payload":
        playerSettingsPayloadValue = deserialize(__dictValue).merged(with: playerSettingsPayloadValue)
      case "preload_required":
        preloadRequiredValue = deserialize(__dictValue).merged(with: preloadRequiredValue)
      case "preview":
        previewValue = deserialize(__dictValue).merged(with: previewValue)
      case "repeatable":
        repeatableValue = deserialize(__dictValue).merged(with: repeatableValue)
      case "resume_actions":
        resumeActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: resumeActionsValue)
      case "row_span":
        rowSpanValue = deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator).merged(with: rowSpanValue)
      case "scale":
        scaleValue = deserialize(__dictValue).merged(with: scaleValue)
      case "selected_actions":
        selectedActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: selectedActionsValue)
      case "tooltips":
        tooltipsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTooltipTemplate.self).merged(with: tooltipsValue)
      case "transform":
        transformValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformTemplate.self).merged(with: transformValue)
      case "transition_change":
        transitionChangeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivChangeTransitionTemplate.self).merged(with: transitionChangeValue)
      case "transition_in":
        transitionInValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self).merged(with: transitionInValue)
      case "transition_out":
        transitionOutValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self).merged(with: transitionOutValue)
      case "transition_triggers":
        transitionTriggersValue = deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator).merged(with: transitionTriggersValue)
      case "video_sources":
        videoSourcesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.videoSourcesValidator, type: DivVideoSourceTemplate.self).merged(with: videoSourcesValue)
      case "visibility":
        visibilityValue = deserialize(__dictValue).merged(with: visibilityValue)
      case "visibility_action":
        visibilityActionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionValue)
      case "visibility_actions":
        visibilityActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionsValue)
      case "width":
        widthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self).merged(with: widthValue)
      case parent?.accessibility?.link:
        accessibilityValue = accessibilityValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAccessibilityTemplate.self) })
      case parent?.alignmentHorizontal?.link:
        alignmentHorizontalValue = alignmentHorizontalValue.merged(with: { deserialize(__dictValue) })
      case parent?.alignmentVertical?.link:
        alignmentVerticalValue = alignmentVerticalValue.merged(with: { deserialize(__dictValue) })
      case parent?.alpha?.link:
        alphaValue = alphaValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.alphaValidator) })
      case parent?.aspect?.link:
        aspectValue = aspectValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAspectTemplate.self) })
      case parent?.autostart?.link:
        autostartValue = autostartValue.merged(with: { deserialize(__dictValue) })
      case parent?.background?.link:
        backgroundValue = backgroundValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBackgroundTemplate.self) })
      case parent?.border?.link:
        borderValue = borderValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBorderTemplate.self) })
      case parent?.bufferingActions?.link:
        bufferingActionsValue = bufferingActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.columnSpan?.link:
        columnSpanValue = columnSpanValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator) })
      case parent?.disappearActions?.link:
        disappearActionsValue = disappearActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDisappearActionTemplate.self) })
      case parent?.elapsedTimeVariable?.link:
        elapsedTimeVariableValue = elapsedTimeVariableValue.merged(with: { deserialize(__dictValue) })
      case parent?.endActions?.link:
        endActionsValue = endActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.extensions?.link:
        extensionsValue = extensionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivExtensionTemplate.self) })
      case parent?.fatalActions?.link:
        fatalActionsValue = fatalActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.focus?.link:
        focusValue = focusValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFocusTemplate.self) })
      case parent?.height?.link:
        heightValue = heightValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self) })
      case parent?.id?.link:
        idValue = idValue.merged(with: { deserialize(__dictValue) })
      case parent?.margins?.link:
        marginsValue = marginsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self) })
      case parent?.muted?.link:
        mutedValue = mutedValue.merged(with: { deserialize(__dictValue) })
      case parent?.paddings?.link:
        paddingsValue = paddingsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self) })
      case parent?.pauseActions?.link:
        pauseActionsValue = pauseActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.playerSettingsPayload?.link:
        playerSettingsPayloadValue = playerSettingsPayloadValue.merged(with: { deserialize(__dictValue) })
      case parent?.preloadRequired?.link:
        preloadRequiredValue = preloadRequiredValue.merged(with: { deserialize(__dictValue) })
      case parent?.preview?.link:
        previewValue = previewValue.merged(with: { deserialize(__dictValue) })
      case parent?.repeatable?.link:
        repeatableValue = repeatableValue.merged(with: { deserialize(__dictValue) })
      case parent?.resumeActions?.link:
        resumeActionsValue = resumeActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.rowSpan?.link:
        rowSpanValue = rowSpanValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator) })
      case parent?.scale?.link:
        scaleValue = scaleValue.merged(with: { deserialize(__dictValue) })
      case parent?.selectedActions?.link:
        selectedActionsValue = selectedActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.tooltips?.link:
        tooltipsValue = tooltipsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTooltipTemplate.self) })
      case parent?.transform?.link:
        transformValue = transformValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformTemplate.self) })
      case parent?.transitionChange?.link:
        transitionChangeValue = transitionChangeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivChangeTransitionTemplate.self) })
      case parent?.transitionIn?.link:
        transitionInValue = transitionInValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self) })
      case parent?.transitionOut?.link:
        transitionOutValue = transitionOutValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self) })
      case parent?.transitionTriggers?.link:
        transitionTriggersValue = transitionTriggersValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator) })
      case parent?.videoSources?.link:
        videoSourcesValue = videoSourcesValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.videoSourcesValidator, type: DivVideoSourceTemplate.self) })
      case parent?.visibility?.link:
        visibilityValue = visibilityValue.merged(with: { deserialize(__dictValue) })
      case parent?.visibilityAction?.link:
        visibilityActionValue = visibilityActionValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self) })
      case parent?.visibilityActions?.link:
        visibilityActionsValue = visibilityActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self) })
      case parent?.width?.link:
        widthValue = widthValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self) })
      default: break
      }
    }
    if let parent = parent {
      accessibilityValue = accessibilityValue.merged(with: { parent.accessibility?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      aspectValue = aspectValue.merged(with: { parent.aspect?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      backgroundValue = backgroundValue.merged(with: { parent.background?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      borderValue = borderValue.merged(with: { parent.border?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      bufferingActionsValue = bufferingActionsValue.merged(with: { parent.bufferingActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      disappearActionsValue = disappearActionsValue.merged(with: { parent.disappearActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      endActionsValue = endActionsValue.merged(with: { parent.endActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      extensionsValue = extensionsValue.merged(with: { parent.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      fatalActionsValue = fatalActionsValue.merged(with: { parent.fatalActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      focusValue = focusValue.merged(with: { parent.focus?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      heightValue = heightValue.merged(with: { parent.height?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      marginsValue = marginsValue.merged(with: { parent.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      paddingsValue = paddingsValue.merged(with: { parent.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      pauseActionsValue = pauseActionsValue.merged(with: { parent.pauseActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      resumeActionsValue = resumeActionsValue.merged(with: { parent.resumeActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      selectedActionsValue = selectedActionsValue.merged(with: { parent.selectedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      tooltipsValue = tooltipsValue.merged(with: { parent.tooltips?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transformValue = transformValue.merged(with: { parent.transform?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transitionChangeValue = transitionChangeValue.merged(with: { parent.transitionChange?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transitionInValue = transitionInValue.merged(with: { parent.transitionIn?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transitionOutValue = transitionOutValue.merged(with: { parent.transitionOut?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      videoSourcesValue = videoSourcesValue.merged(with: { parent.videoSources?.resolveValue(context: context, validator: ResolvedValue.videoSourcesValidator, useOnlyLinks: true) })
      visibilityActionValue = visibilityActionValue.merged(with: { parent.visibilityAction?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      visibilityActionsValue = visibilityActionsValue.merged(with: { parent.visibilityActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      widthValue = widthValue.merged(with: { parent.width?.resolveOptionalValue(context: context, useOnlyLinks: true) })
    }
    var errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      aspectValue.errorsOrWarnings?.map { .nestedObjectError(field: "aspect", error: $0) },
      autostartValue.errorsOrWarnings?.map { .nestedObjectError(field: "autostart", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      bufferingActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "buffering_actions", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      disappearActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_actions", error: $0) },
      elapsedTimeVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "elapsed_time_variable", error: $0) },
      endActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "end_actions", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      fatalActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "fatal_actions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      mutedValue.errorsOrWarnings?.map { .nestedObjectError(field: "muted", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      pauseActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "pause_actions", error: $0) },
      playerSettingsPayloadValue.errorsOrWarnings?.map { .nestedObjectError(field: "player_settings_payload", error: $0) },
      preloadRequiredValue.errorsOrWarnings?.map { .nestedObjectError(field: "preload_required", error: $0) },
      previewValue.errorsOrWarnings?.map { .nestedObjectError(field: "preview", error: $0) },
      repeatableValue.errorsOrWarnings?.map { .nestedObjectError(field: "repeatable", error: $0) },
      resumeActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "resume_actions", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      scaleValue.errorsOrWarnings?.map { .nestedObjectError(field: "scale", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      videoSourcesValue.errorsOrWarnings?.map { .nestedObjectError(field: "video_sources", error: $0) },
      visibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility", error: $0) },
      visibilityActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_action", error: $0) },
      visibilityActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_actions", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    if case .noValue = videoSourcesValue {
      errors.append(.requiredFieldIsMissing(field: "video_sources"))
    }
    guard
      let videoSourcesNonNil = videoSourcesValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivVideo(
      accessibility: accessibilityValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      aspect: aspectValue.value,
      autostart: autostartValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      bufferingActions: bufferingActionsValue.value,
      columnSpan: columnSpanValue.value,
      disappearActions: disappearActionsValue.value,
      elapsedTimeVariable: elapsedTimeVariableValue.value,
      endActions: endActionsValue.value,
      extensions: extensionsValue.value,
      fatalActions: fatalActionsValue.value,
      focus: focusValue.value,
      height: heightValue.value,
      id: idValue.value,
      margins: marginsValue.value,
      muted: mutedValue.value,
      paddings: paddingsValue.value,
      pauseActions: pauseActionsValue.value,
      playerSettingsPayload: playerSettingsPayloadValue.value,
      preloadRequired: preloadRequiredValue.value,
      preview: previewValue.value,
      repeatable: repeatableValue.value,
      resumeActions: resumeActionsValue.value,
      rowSpan: rowSpanValue.value,
      scale: scaleValue.value,
      selectedActions: selectedActionsValue.value,
      tooltips: tooltipsValue.value,
      transform: transformValue.value,
      transitionChange: transitionChangeValue.value,
      transitionIn: transitionInValue.value,
      transitionOut: transitionOutValue.value,
      transitionTriggers: transitionTriggersValue.value,
      videoSources: videoSourcesNonNil,
      visibility: visibilityValue.value,
      visibilityAction: visibilityActionValue.value,
      visibilityActions: visibilityActionsValue.value,
      width: widthValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivVideoTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivVideoTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivVideoTemplate(
      parent: nil,
      accessibility: accessibility ?? mergedParent.accessibility,
      alignmentHorizontal: alignmentHorizontal ?? mergedParent.alignmentHorizontal,
      alignmentVertical: alignmentVertical ?? mergedParent.alignmentVertical,
      alpha: alpha ?? mergedParent.alpha,
      aspect: aspect ?? mergedParent.aspect,
      autostart: autostart ?? mergedParent.autostart,
      background: background ?? mergedParent.background,
      border: border ?? mergedParent.border,
      bufferingActions: bufferingActions ?? mergedParent.bufferingActions,
      columnSpan: columnSpan ?? mergedParent.columnSpan,
      disappearActions: disappearActions ?? mergedParent.disappearActions,
      elapsedTimeVariable: elapsedTimeVariable ?? mergedParent.elapsedTimeVariable,
      endActions: endActions ?? mergedParent.endActions,
      extensions: extensions ?? mergedParent.extensions,
      fatalActions: fatalActions ?? mergedParent.fatalActions,
      focus: focus ?? mergedParent.focus,
      height: height ?? mergedParent.height,
      id: id ?? mergedParent.id,
      margins: margins ?? mergedParent.margins,
      muted: muted ?? mergedParent.muted,
      paddings: paddings ?? mergedParent.paddings,
      pauseActions: pauseActions ?? mergedParent.pauseActions,
      playerSettingsPayload: playerSettingsPayload ?? mergedParent.playerSettingsPayload,
      preloadRequired: preloadRequired ?? mergedParent.preloadRequired,
      preview: preview ?? mergedParent.preview,
      repeatable: repeatable ?? mergedParent.repeatable,
      resumeActions: resumeActions ?? mergedParent.resumeActions,
      rowSpan: rowSpan ?? mergedParent.rowSpan,
      scale: scale ?? mergedParent.scale,
      selectedActions: selectedActions ?? mergedParent.selectedActions,
      tooltips: tooltips ?? mergedParent.tooltips,
      transform: transform ?? mergedParent.transform,
      transitionChange: transitionChange ?? mergedParent.transitionChange,
      transitionIn: transitionIn ?? mergedParent.transitionIn,
      transitionOut: transitionOut ?? mergedParent.transitionOut,
      transitionTriggers: transitionTriggers ?? mergedParent.transitionTriggers,
      videoSources: videoSources ?? mergedParent.videoSources,
      visibility: visibility ?? mergedParent.visibility,
      visibilityAction: visibilityAction ?? mergedParent.visibilityAction,
      visibilityActions: visibilityActions ?? mergedParent.visibilityActions,
      width: width ?? mergedParent.width
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivVideoTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivVideoTemplate(
      parent: nil,
      accessibility: merged.accessibility?.tryResolveParent(templates: templates),
      alignmentHorizontal: merged.alignmentHorizontal,
      alignmentVertical: merged.alignmentVertical,
      alpha: merged.alpha,
      aspect: merged.aspect?.tryResolveParent(templates: templates),
      autostart: merged.autostart,
      background: merged.background?.tryResolveParent(templates: templates),
      border: merged.border?.tryResolveParent(templates: templates),
      bufferingActions: merged.bufferingActions?.tryResolveParent(templates: templates),
      columnSpan: merged.columnSpan,
      disappearActions: merged.disappearActions?.tryResolveParent(templates: templates),
      elapsedTimeVariable: merged.elapsedTimeVariable,
      endActions: merged.endActions?.tryResolveParent(templates: templates),
      extensions: merged.extensions?.tryResolveParent(templates: templates),
      fatalActions: merged.fatalActions?.tryResolveParent(templates: templates),
      focus: merged.focus?.tryResolveParent(templates: templates),
      height: merged.height?.tryResolveParent(templates: templates),
      id: merged.id,
      margins: merged.margins?.tryResolveParent(templates: templates),
      muted: merged.muted,
      paddings: merged.paddings?.tryResolveParent(templates: templates),
      pauseActions: merged.pauseActions?.tryResolveParent(templates: templates),
      playerSettingsPayload: merged.playerSettingsPayload,
      preloadRequired: merged.preloadRequired,
      preview: merged.preview,
      repeatable: merged.repeatable,
      resumeActions: merged.resumeActions?.tryResolveParent(templates: templates),
      rowSpan: merged.rowSpan,
      scale: merged.scale,
      selectedActions: merged.selectedActions?.tryResolveParent(templates: templates),
      tooltips: merged.tooltips?.tryResolveParent(templates: templates),
      transform: merged.transform?.tryResolveParent(templates: templates),
      transitionChange: merged.transitionChange?.tryResolveParent(templates: templates),
      transitionIn: merged.transitionIn?.tryResolveParent(templates: templates),
      transitionOut: merged.transitionOut?.tryResolveParent(templates: templates),
      transitionTriggers: merged.transitionTriggers,
      videoSources: try merged.videoSources?.resolveParent(templates: templates),
      visibility: merged.visibility,
      visibilityAction: merged.visibilityAction?.tryResolveParent(templates: templates),
      visibilityActions: merged.visibilityActions?.tryResolveParent(templates: templates),
      width: merged.width?.tryResolveParent(templates: templates)
    )
  }
}
