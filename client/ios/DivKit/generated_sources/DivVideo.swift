// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivVideo: DivBase {
  public static let type: String = "video"
  public let accessibility: DivAccessibility
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let autostart: Expression<Bool> // default value: false
  public let background: [DivBackground]? // at least 1 elements
  public let border: DivBorder
  public let bufferingActions: [DivAction]? // at least 1 elements
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let elapsedTimeVariable: String? // at least 1 char
  public let endActions: [DivAction]? // at least 1 elements
  public let extensions: [DivExtension]? // at least 1 elements
  public let focus: DivFocus?
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: String? // at least 1 char
  public let margins: DivEdgeInsets
  public let muted: Expression<Bool> // default value: false
  public let paddings: DivEdgeInsets
  public let playerSettingsPayload: [String: Any]?
  public let preview: Expression<String>? // at least 1 char
  public let repeatable: Expression<Bool> // default value: false
  public let resumeActions: [DivAction]? // at least 1 elements
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let selectedActions: [DivAction]? // at least 1 elements
  public let tooltips: [DivTooltip]? // at least 1 elements
  public let transform: DivTransform
  public let transitionChange: DivChangeTransition?
  public let transitionIn: DivAppearanceTransition?
  public let transitionOut: DivAppearanceTransition?
  public let transitionTriggers: [DivTransitionTrigger]? // at least 1 elements
  public let videoData: DivVideoData
  public let visibility: Expression<DivVisibility> // default value: visible
  public let visibilityAction: DivVisibilityAction?
  public let visibilityActions: [DivVisibilityAction]? // at least 1 elements
  public let width: DivSize // default value: .divMatchParentSize(DivMatchParentSize())

  public func resolveAlignmentHorizontal(_ resolver: ExpressionResolver) -> DivAlignmentHorizontal? {
    resolver.resolveStringBasedValue(expression: alignmentHorizontal, initializer: DivAlignmentHorizontal.init(rawValue:))
  }

  public func resolveAlignmentVertical(_ resolver: ExpressionResolver) -> DivAlignmentVertical? {
    resolver.resolveStringBasedValue(expression: alignmentVertical, initializer: DivAlignmentVertical.init(rawValue:))
  }

  public func resolveAlpha(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumericValue(expression: alpha) ?? 1.0
  }

  public func resolveAutostart(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumericValue(expression: autostart) ?? false
  }

  public func resolveColumnSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: columnSpan)
  }

  public func resolveMuted(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumericValue(expression: muted) ?? false
  }

  public func resolvePreview(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveStringBasedValue(expression: preview, initializer: { $0 })
  }

  public func resolveRepeatable(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumericValue(expression: repeatable) ?? false
  }

  public func resolveRowSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: rowSpan)
  }

  public func resolveVisibility(_ resolver: ExpressionResolver) -> DivVisibility {
    resolver.resolveStringBasedValue(expression: visibility, initializer: DivVisibility.init(rawValue:)) ?? DivVisibility.visible
  }

  static let accessibilityValidator: AnyValueValidator<DivAccessibility> =
    makeNoOpValueValidator()

  static let alignmentHorizontalValidator: AnyValueValidator<DivAlignmentHorizontal> =
    makeNoOpValueValidator()

  static let alignmentVerticalValidator: AnyValueValidator<DivAlignmentVertical> =
    makeNoOpValueValidator()

  static let alphaValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 && $0 <= 1.0 })

  static let autostartValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let backgroundValidator: AnyArrayValueValidator<DivBackground> =
    makeArrayValidator(minItems: 1)

  static let borderValidator: AnyValueValidator<DivBorder> =
    makeNoOpValueValidator()

  static let bufferingActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let columnSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let elapsedTimeVariableValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let endActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let extensionsValidator: AnyArrayValueValidator<DivExtension> =
    makeArrayValidator(minItems: 1)

  static let focusValidator: AnyValueValidator<DivFocus> =
    makeNoOpValueValidator()

  static let heightValidator: AnyValueValidator<DivSize> =
    makeNoOpValueValidator()

  static let idValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let marginsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let mutedValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let paddingsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let playerSettingsPayloadValidator: AnyValueValidator<[String: Any]> =
    makeNoOpValueValidator()

  static let previewValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let repeatableValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let resumeActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let rowSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let selectedActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let tooltipsValidator: AnyArrayValueValidator<DivTooltip> =
    makeArrayValidator(minItems: 1)

  static let transformValidator: AnyValueValidator<DivTransform> =
    makeNoOpValueValidator()

  static let transitionChangeValidator: AnyValueValidator<DivChangeTransition> =
    makeNoOpValueValidator()

  static let transitionInValidator: AnyValueValidator<DivAppearanceTransition> =
    makeNoOpValueValidator()

  static let transitionOutValidator: AnyValueValidator<DivAppearanceTransition> =
    makeNoOpValueValidator()

  static let transitionTriggersValidator: AnyArrayValueValidator<DivTransitionTrigger> =
    makeArrayValidator(minItems: 1)

  static let visibilityValidator: AnyValueValidator<DivVisibility> =
    makeNoOpValueValidator()

  static let visibilityActionValidator: AnyValueValidator<DivVisibilityAction> =
    makeNoOpValueValidator()

  static let visibilityActionsValidator: AnyArrayValueValidator<DivVisibilityAction> =
    makeArrayValidator(minItems: 1)

  static let widthValidator: AnyValueValidator<DivSize> =
    makeNoOpValueValidator()

  init(
    accessibility: DivAccessibility? = nil,
    alignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    alignmentVertical: Expression<DivAlignmentVertical>? = nil,
    alpha: Expression<Double>? = nil,
    autostart: Expression<Bool>? = nil,
    background: [DivBackground]? = nil,
    border: DivBorder? = nil,
    bufferingActions: [DivAction]? = nil,
    columnSpan: Expression<Int>? = nil,
    elapsedTimeVariable: String? = nil,
    endActions: [DivAction]? = nil,
    extensions: [DivExtension]? = nil,
    focus: DivFocus? = nil,
    height: DivSize? = nil,
    id: String? = nil,
    margins: DivEdgeInsets? = nil,
    muted: Expression<Bool>? = nil,
    paddings: DivEdgeInsets? = nil,
    playerSettingsPayload: [String: Any]? = nil,
    preview: Expression<String>? = nil,
    repeatable: Expression<Bool>? = nil,
    resumeActions: [DivAction]? = nil,
    rowSpan: Expression<Int>? = nil,
    selectedActions: [DivAction]? = nil,
    tooltips: [DivTooltip]? = nil,
    transform: DivTransform? = nil,
    transitionChange: DivChangeTransition? = nil,
    transitionIn: DivAppearanceTransition? = nil,
    transitionOut: DivAppearanceTransition? = nil,
    transitionTriggers: [DivTransitionTrigger]? = nil,
    videoData: DivVideoData,
    visibility: Expression<DivVisibility>? = nil,
    visibilityAction: DivVisibilityAction? = nil,
    visibilityActions: [DivVisibilityAction]? = nil,
    width: DivSize? = nil
  ) {
    self.accessibility = accessibility ?? DivAccessibility()
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha ?? .value(1.0)
    self.autostart = autostart ?? .value(false)
    self.background = background
    self.border = border ?? DivBorder()
    self.bufferingActions = bufferingActions
    self.columnSpan = columnSpan
    self.elapsedTimeVariable = elapsedTimeVariable
    self.endActions = endActions
    self.extensions = extensions
    self.focus = focus
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.id = id
    self.margins = margins ?? DivEdgeInsets()
    self.muted = muted ?? .value(false)
    self.paddings = paddings ?? DivEdgeInsets()
    self.playerSettingsPayload = playerSettingsPayload
    self.preview = preview
    self.repeatable = repeatable ?? .value(false)
    self.resumeActions = resumeActions
    self.rowSpan = rowSpan
    self.selectedActions = selectedActions
    self.tooltips = tooltips
    self.transform = transform ?? DivTransform()
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.videoData = videoData
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
      lhs.autostart == rhs.autostart,
      lhs.background == rhs.background
    else {
      return false
    }
    guard
      lhs.border == rhs.border,
      lhs.bufferingActions == rhs.bufferingActions,
      lhs.columnSpan == rhs.columnSpan
    else {
      return false
    }
    guard
      lhs.elapsedTimeVariable == rhs.elapsedTimeVariable,
      lhs.endActions == rhs.endActions,
      lhs.extensions == rhs.extensions
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
      lhs.margins == rhs.margins,
      lhs.muted == rhs.muted,
      lhs.paddings == rhs.paddings
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
      lhs.rowSpan == rhs.rowSpan,
      lhs.selectedActions == rhs.selectedActions,
      lhs.tooltips == rhs.tooltips
    else {
      return false
    }
    guard
      lhs.transform == rhs.transform,
      lhs.transitionChange == rhs.transitionChange,
      lhs.transitionIn == rhs.transitionIn
    else {
      return false
    }
    guard
      lhs.transitionOut == rhs.transitionOut,
      lhs.transitionTriggers == rhs.transitionTriggers,
      lhs.videoData == rhs.videoData
    else {
      return false
    }
    guard
      lhs.visibility == rhs.visibility,
      lhs.visibilityAction == rhs.visibilityAction,
      lhs.visibilityActions == rhs.visibilityActions
    else {
      return false
    }
    guard
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
    result["accessibility"] = accessibility.toDictionary()
    result["alignment_horizontal"] = alignmentHorizontal?.toValidSerializationValue()
    result["alignment_vertical"] = alignmentVertical?.toValidSerializationValue()
    result["alpha"] = alpha.toValidSerializationValue()
    result["autostart"] = autostart.toValidSerializationValue()
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border.toDictionary()
    result["buffering_actions"] = bufferingActions?.map { $0.toDictionary() }
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["elapsed_time_variable"] = elapsedTimeVariable
    result["end_actions"] = endActions?.map { $0.toDictionary() }
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["height"] = height.toDictionary()
    result["id"] = id
    result["margins"] = margins.toDictionary()
    result["muted"] = muted.toValidSerializationValue()
    result["paddings"] = paddings.toDictionary()
    result["player_settings_payload"] = playerSettingsPayload
    result["preview"] = preview?.toValidSerializationValue()
    result["repeatable"] = repeatable.toValidSerializationValue()
    result["resume_actions"] = resumeActions?.map { $0.toDictionary() }
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
    result["tooltips"] = tooltips?.map { $0.toDictionary() }
    result["transform"] = transform.toDictionary()
    result["transition_change"] = transitionChange?.toDictionary()
    result["transition_in"] = transitionIn?.toDictionary()
    result["transition_out"] = transitionOut?.toDictionary()
    result["transition_triggers"] = transitionTriggers?.map { $0.rawValue }
    result["video_data"] = videoData.toDictionary()
    result["visibility"] = visibility.toValidSerializationValue()
    result["visibility_action"] = visibilityAction?.toDictionary()
    result["visibility_actions"] = visibilityActions?.map { $0.toDictionary() }
    result["width"] = width.toDictionary()
    return result
  }
}
