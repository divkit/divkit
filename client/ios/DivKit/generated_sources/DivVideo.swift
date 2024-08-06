// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivVideo: DivBase {
  public static let type: String = "video"
  public let accessibility: DivAccessibility?
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let aspect: DivAspect?
  public let autostart: Expression<Bool> // default value: false
  public let background: [DivBackground]?
  public let border: DivBorder?
  public let bufferingActions: [DivAction]?
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let disappearActions: [DivDisappearAction]?
  public let elapsedTimeVariable: String?
  public let endActions: [DivAction]?
  public let extensions: [DivExtension]?
  public let fatalActions: [DivAction]?
  public let focus: DivFocus?
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: String?
  public let layoutProvider: DivLayoutProvider?
  public let margins: DivEdgeInsets?
  public let muted: Expression<Bool> // default value: false
  public let paddings: DivEdgeInsets?
  public let pauseActions: [DivAction]?
  public let playerSettingsPayload: [String: Any]?
  public let preloadRequired: Expression<Bool> // default value: false
  public let preview: Expression<String>?
  public let repeatable: Expression<Bool> // default value: false
  public let resumeActions: [DivAction]?
  public let reuseId: Expression<String>?
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let scale: Expression<DivVideoScale> // default value: fit
  public let selectedActions: [DivAction]?
  public let tooltips: [DivTooltip]?
  public let transform: DivTransform?
  public let transitionChange: DivChangeTransition?
  public let transitionIn: DivAppearanceTransition?
  public let transitionOut: DivAppearanceTransition?
  public let transitionTriggers: [DivTransitionTrigger]? // at least 1 elements
  public let variableTriggers: [DivTrigger]?
  public let variables: [DivVariable]?
  public let videoSources: [DivVideoSource] // at least 1 elements
  public let visibility: Expression<DivVisibility> // default value: visible
  public let visibilityAction: DivVisibilityAction?
  public let visibilityActions: [DivVisibilityAction]?
  public let width: DivSize // default value: .divMatchParentSize(DivMatchParentSize())

  public func resolveAlignmentHorizontal(_ resolver: ExpressionResolver) -> DivAlignmentHorizontal? {
    resolver.resolveEnum(alignmentHorizontal)
  }

  public func resolveAlignmentVertical(_ resolver: ExpressionResolver) -> DivAlignmentVertical? {
    resolver.resolveEnum(alignmentVertical)
  }

  public func resolveAlpha(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumeric(alpha) ?? 1.0
  }

  public func resolveAutostart(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(autostart) ?? false
  }

  public func resolveColumnSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(columnSpan)
  }

  public func resolveMuted(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(muted) ?? false
  }

  public func resolvePreloadRequired(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(preloadRequired) ?? false
  }

  public func resolvePreview(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(preview)
  }

  public func resolveRepeatable(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(repeatable) ?? false
  }

  public func resolveReuseId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(reuseId)
  }

  public func resolveRowSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(rowSpan)
  }

  public func resolveScale(_ resolver: ExpressionResolver) -> DivVideoScale {
    resolver.resolveEnum(scale) ?? DivVideoScale.fit
  }

  public func resolveVisibility(_ resolver: ExpressionResolver) -> DivVisibility {
    resolver.resolveEnum(visibility) ?? DivVisibility.visible
  }

  static let alphaValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 && $0 <= 1.0 })

  static let columnSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let rowSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let transitionTriggersValidator: AnyArrayValueValidator<DivTransitionTrigger> =
    makeArrayValidator(minItems: 1)

  static let videoSourcesValidator: AnyArrayValueValidator<DivVideoSource> =
    makeArrayValidator(minItems: 1)

  init(
    accessibility: DivAccessibility? = nil,
    alignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    alignmentVertical: Expression<DivAlignmentVertical>? = nil,
    alpha: Expression<Double>? = nil,
    aspect: DivAspect? = nil,
    autostart: Expression<Bool>? = nil,
    background: [DivBackground]? = nil,
    border: DivBorder? = nil,
    bufferingActions: [DivAction]? = nil,
    columnSpan: Expression<Int>? = nil,
    disappearActions: [DivDisappearAction]? = nil,
    elapsedTimeVariable: String? = nil,
    endActions: [DivAction]? = nil,
    extensions: [DivExtension]? = nil,
    fatalActions: [DivAction]? = nil,
    focus: DivFocus? = nil,
    height: DivSize? = nil,
    id: String? = nil,
    layoutProvider: DivLayoutProvider? = nil,
    margins: DivEdgeInsets? = nil,
    muted: Expression<Bool>? = nil,
    paddings: DivEdgeInsets? = nil,
    pauseActions: [DivAction]? = nil,
    playerSettingsPayload: [String: Any]? = nil,
    preloadRequired: Expression<Bool>? = nil,
    preview: Expression<String>? = nil,
    repeatable: Expression<Bool>? = nil,
    resumeActions: [DivAction]? = nil,
    reuseId: Expression<String>? = nil,
    rowSpan: Expression<Int>? = nil,
    scale: Expression<DivVideoScale>? = nil,
    selectedActions: [DivAction]? = nil,
    tooltips: [DivTooltip]? = nil,
    transform: DivTransform? = nil,
    transitionChange: DivChangeTransition? = nil,
    transitionIn: DivAppearanceTransition? = nil,
    transitionOut: DivAppearanceTransition? = nil,
    transitionTriggers: [DivTransitionTrigger]? = nil,
    variableTriggers: [DivTrigger]? = nil,
    variables: [DivVariable]? = nil,
    videoSources: [DivVideoSource],
    visibility: Expression<DivVisibility>? = nil,
    visibilityAction: DivVisibilityAction? = nil,
    visibilityActions: [DivVisibilityAction]? = nil,
    width: DivSize? = nil
  ) {
    self.accessibility = accessibility
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha ?? .value(1.0)
    self.aspect = aspect
    self.autostart = autostart ?? .value(false)
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
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.id = id
    self.layoutProvider = layoutProvider
    self.margins = margins
    self.muted = muted ?? .value(false)
    self.paddings = paddings
    self.pauseActions = pauseActions
    self.playerSettingsPayload = playerSettingsPayload
    self.preloadRequired = preloadRequired ?? .value(false)
    self.preview = preview
    self.repeatable = repeatable ?? .value(false)
    self.resumeActions = resumeActions
    self.reuseId = reuseId
    self.rowSpan = rowSpan
    self.scale = scale ?? .value(.fit)
    self.selectedActions = selectedActions
    self.tooltips = tooltips
    self.transform = transform
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.variableTriggers = variableTriggers
    self.variables = variables
    self.videoSources = videoSources
    self.visibility = visibility ?? .value(.visible)
    self.visibilityAction = visibilityAction
    self.visibilityActions = visibilityActions
    self.width = width ?? .divMatchParentSize(DivMatchParentSize())
  }
}

#if DEBUG
// WARNING: this == is incomplete because of [String: Any] in class fields
extension DivVideo: Equatable {
  public static func ==(lhs: DivVideo, rhs: DivVideo) -> Bool {
    guard
      lhs.accessibility == rhs.accessibility,
      lhs.alignmentHorizontal == rhs.alignmentHorizontal,
      lhs.alignmentVertical == rhs.alignmentVertical
    else {
      return false
    }
    guard
      lhs.alpha == rhs.alpha,
      lhs.aspect == rhs.aspect,
      lhs.autostart == rhs.autostart
    else {
      return false
    }
    guard
      lhs.background == rhs.background,
      lhs.border == rhs.border,
      lhs.bufferingActions == rhs.bufferingActions
    else {
      return false
    }
    guard
      lhs.columnSpan == rhs.columnSpan,
      lhs.disappearActions == rhs.disappearActions,
      lhs.elapsedTimeVariable == rhs.elapsedTimeVariable
    else {
      return false
    }
    guard
      lhs.endActions == rhs.endActions,
      lhs.extensions == rhs.extensions,
      lhs.fatalActions == rhs.fatalActions
    else {
      return false
    }
    guard
      lhs.focus == rhs.focus,
      lhs.height == rhs.height,
      lhs.id == rhs.id
    else {
      return false
    }
    guard
      lhs.layoutProvider == rhs.layoutProvider,
      lhs.margins == rhs.margins,
      lhs.muted == rhs.muted
    else {
      return false
    }
    guard
      lhs.paddings == rhs.paddings,
      lhs.pauseActions == rhs.pauseActions,
      lhs.preloadRequired == rhs.preloadRequired
    else {
      return false
    }
    guard
      lhs.preview == rhs.preview,
      lhs.repeatable == rhs.repeatable,
      lhs.resumeActions == rhs.resumeActions
    else {
      return false
    }
    guard
      lhs.reuseId == rhs.reuseId,
      lhs.rowSpan == rhs.rowSpan,
      lhs.scale == rhs.scale
    else {
      return false
    }
    guard
      lhs.selectedActions == rhs.selectedActions,
      lhs.tooltips == rhs.tooltips,
      lhs.transform == rhs.transform
    else {
      return false
    }
    guard
      lhs.transitionChange == rhs.transitionChange,
      lhs.transitionIn == rhs.transitionIn,
      lhs.transitionOut == rhs.transitionOut
    else {
      return false
    }
    guard
      lhs.transitionTriggers == rhs.transitionTriggers,
      lhs.variableTriggers == rhs.variableTriggers,
      lhs.variables == rhs.variables
    else {
      return false
    }
    guard
      lhs.videoSources == rhs.videoSources,
      lhs.visibility == rhs.visibility,
      lhs.visibilityAction == rhs.visibilityAction
    else {
      return false
    }
    guard
      lhs.visibilityActions == rhs.visibilityActions,
      lhs.width == rhs.width
    else {
      return false
    }
    return true
  }
}
#endif

extension DivVideo: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["accessibility"] = accessibility?.toDictionary()
    result["alignment_horizontal"] = alignmentHorizontal?.toValidSerializationValue()
    result["alignment_vertical"] = alignmentVertical?.toValidSerializationValue()
    result["alpha"] = alpha.toValidSerializationValue()
    result["aspect"] = aspect?.toDictionary()
    result["autostart"] = autostart.toValidSerializationValue()
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border?.toDictionary()
    result["buffering_actions"] = bufferingActions?.map { $0.toDictionary() }
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["disappear_actions"] = disappearActions?.map { $0.toDictionary() }
    result["elapsed_time_variable"] = elapsedTimeVariable
    result["end_actions"] = endActions?.map { $0.toDictionary() }
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["fatal_actions"] = fatalActions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["height"] = height.toDictionary()
    result["id"] = id
    result["layout_provider"] = layoutProvider?.toDictionary()
    result["margins"] = margins?.toDictionary()
    result["muted"] = muted.toValidSerializationValue()
    result["paddings"] = paddings?.toDictionary()
    result["pause_actions"] = pauseActions?.map { $0.toDictionary() }
    result["player_settings_payload"] = playerSettingsPayload
    result["preload_required"] = preloadRequired.toValidSerializationValue()
    result["preview"] = preview?.toValidSerializationValue()
    result["repeatable"] = repeatable.toValidSerializationValue()
    result["resume_actions"] = resumeActions?.map { $0.toDictionary() }
    result["reuse_id"] = reuseId?.toValidSerializationValue()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["scale"] = scale.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
    result["tooltips"] = tooltips?.map { $0.toDictionary() }
    result["transform"] = transform?.toDictionary()
    result["transition_change"] = transitionChange?.toDictionary()
    result["transition_in"] = transitionIn?.toDictionary()
    result["transition_out"] = transitionOut?.toDictionary()
    result["transition_triggers"] = transitionTriggers?.map { $0.rawValue }
    result["variable_triggers"] = variableTriggers?.map { $0.toDictionary() }
    result["variables"] = variables?.map { $0.toDictionary() }
    result["video_sources"] = videoSources.map { $0.toDictionary() }
    result["visibility"] = visibility.toValidSerializationValue()
    result["visibility_action"] = visibilityAction?.toDictionary()
    result["visibility_actions"] = visibilityActions?.map { $0.toDictionary() }
    result["width"] = width.toDictionary()
    return result
  }
}
