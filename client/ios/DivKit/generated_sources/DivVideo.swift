// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivVideo: DivBase, @unchecked Sendable {
  public static let type: String = "video"
  public let accessibility: DivAccessibility?
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let animators: [DivAnimator]?
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
  public let functions: [DivFunction]?
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
  public let transformations: [DivTransformation]?
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

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      accessibility: try dictionary.getOptionalField("accessibility", transform: { (dict: [String: Any]) in try DivAccessibility(dictionary: dict, context: context) }),
      alignmentHorizontal: try dictionary.getOptionalExpressionField("alignment_horizontal", context: context),
      alignmentVertical: try dictionary.getOptionalExpressionField("alignment_vertical", context: context),
      alpha: try dictionary.getOptionalExpressionField("alpha", validator: Self.alphaValidator, context: context),
      animators: try dictionary.getOptionalArray("animators", transform: { (dict: [String: Any]) in try? DivAnimator(dictionary: dict, context: context) }),
      aspect: try dictionary.getOptionalField("aspect", transform: { (dict: [String: Any]) in try DivAspect(dictionary: dict, context: context) }),
      autostart: try dictionary.getOptionalExpressionField("autostart", context: context),
      background: try dictionary.getOptionalArray("background", transform: { (dict: [String: Any]) in try? DivBackground(dictionary: dict, context: context) }),
      border: try dictionary.getOptionalField("border", transform: { (dict: [String: Any]) in try DivBorder(dictionary: dict, context: context) }),
      bufferingActions: try dictionary.getOptionalArray("buffering_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      columnSpan: try dictionary.getOptionalExpressionField("column_span", validator: Self.columnSpanValidator, context: context),
      disappearActions: try dictionary.getOptionalArray("disappear_actions", transform: { (dict: [String: Any]) in try? DivDisappearAction(dictionary: dict, context: context) }),
      elapsedTimeVariable: try dictionary.getOptionalField("elapsed_time_variable", context: context),
      endActions: try dictionary.getOptionalArray("end_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      extensions: try dictionary.getOptionalArray("extensions", transform: { (dict: [String: Any]) in try? DivExtension(dictionary: dict, context: context) }),
      fatalActions: try dictionary.getOptionalArray("fatal_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      focus: try dictionary.getOptionalField("focus", transform: { (dict: [String: Any]) in try DivFocus(dictionary: dict, context: context) }),
      functions: try dictionary.getOptionalArray("functions", transform: { (dict: [String: Any]) in try? DivFunction(dictionary: dict, context: context) }),
      height: try dictionary.getOptionalField("height", transform: { (dict: [String: Any]) in try DivSize(dictionary: dict, context: context) }),
      id: try dictionary.getOptionalField("id", context: context),
      layoutProvider: try dictionary.getOptionalField("layout_provider", transform: { (dict: [String: Any]) in try DivLayoutProvider(dictionary: dict, context: context) }),
      margins: try dictionary.getOptionalField("margins", transform: { (dict: [String: Any]) in try DivEdgeInsets(dictionary: dict, context: context) }),
      muted: try dictionary.getOptionalExpressionField("muted", context: context),
      paddings: try dictionary.getOptionalField("paddings", transform: { (dict: [String: Any]) in try DivEdgeInsets(dictionary: dict, context: context) }),
      pauseActions: try dictionary.getOptionalArray("pause_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      playerSettingsPayload: try dictionary.getOptionalField("player_settings_payload", context: context),
      preloadRequired: try dictionary.getOptionalExpressionField("preload_required", context: context),
      preview: try dictionary.getOptionalExpressionField("preview", context: context),
      repeatable: try dictionary.getOptionalExpressionField("repeatable", context: context),
      resumeActions: try dictionary.getOptionalArray("resume_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      reuseId: try dictionary.getOptionalExpressionField("reuse_id", context: context),
      rowSpan: try dictionary.getOptionalExpressionField("row_span", validator: Self.rowSpanValidator, context: context),
      scale: try dictionary.getOptionalExpressionField("scale", context: context),
      selectedActions: try dictionary.getOptionalArray("selected_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      tooltips: try dictionary.getOptionalArray("tooltips", transform: { (dict: [String: Any]) in try? DivTooltip(dictionary: dict, context: context) }),
      transform: try dictionary.getOptionalField("transform", transform: { (dict: [String: Any]) in try DivTransform(dictionary: dict, context: context) }),
      transformations: try dictionary.getOptionalArray("transformations", transform: { (dict: [String: Any]) in try? DivTransformation(dictionary: dict, context: context) }),
      transitionChange: try dictionary.getOptionalField("transition_change", transform: { (dict: [String: Any]) in try DivChangeTransition(dictionary: dict, context: context) }),
      transitionIn: try dictionary.getOptionalField("transition_in", transform: { (dict: [String: Any]) in try DivAppearanceTransition(dictionary: dict, context: context) }),
      transitionOut: try dictionary.getOptionalField("transition_out", transform: { (dict: [String: Any]) in try DivAppearanceTransition(dictionary: dict, context: context) }),
      transitionTriggers: try dictionary.getOptionalArray("transition_triggers", validator: Self.transitionTriggersValidator, context: context),
      variableTriggers: try dictionary.getOptionalArray("variable_triggers", transform: { (dict: [String: Any]) in try? DivTrigger(dictionary: dict, context: context) }),
      variables: try dictionary.getOptionalArray("variables", transform: { (dict: [String: Any]) in try? DivVariable(dictionary: dict, context: context) }),
      videoSources: try dictionary.getArray("video_sources", transform: { (dict: [String: Any]) in try? DivVideoSource(dictionary: dict, context: context) }, validator: Self.videoSourcesValidator),
      visibility: try dictionary.getOptionalExpressionField("visibility", context: context),
      visibilityAction: try dictionary.getOptionalField("visibility_action", transform: { (dict: [String: Any]) in try DivVisibilityAction(dictionary: dict, context: context) }),
      visibilityActions: try dictionary.getOptionalArray("visibility_actions", transform: { (dict: [String: Any]) in try? DivVisibilityAction(dictionary: dict, context: context) }),
      width: try dictionary.getOptionalField("width", transform: { (dict: [String: Any]) in try DivSize(dictionary: dict, context: context) })
    )
  }

  init(
    accessibility: DivAccessibility? = nil,
    alignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    alignmentVertical: Expression<DivAlignmentVertical>? = nil,
    alpha: Expression<Double>? = nil,
    animators: [DivAnimator]? = nil,
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
    functions: [DivFunction]? = nil,
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
    transformations: [DivTransformation]? = nil,
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
    self.animators = animators
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
    self.functions = functions
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
    self.transformations = transformations
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
      lhs.animators == rhs.animators,
      lhs.aspect == rhs.aspect
    else {
      return false
    }
    guard
      lhs.autostart == rhs.autostart,
      lhs.background == rhs.background,
      lhs.border == rhs.border
    else {
      return false
    }
    guard
      lhs.bufferingActions == rhs.bufferingActions,
      lhs.columnSpan == rhs.columnSpan,
      lhs.disappearActions == rhs.disappearActions
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
      lhs.fatalActions == rhs.fatalActions,
      lhs.focus == rhs.focus,
      lhs.functions == rhs.functions
    else {
      return false
    }
    guard
      lhs.height == rhs.height,
      lhs.id == rhs.id,
      lhs.layoutProvider == rhs.layoutProvider
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
      lhs.pauseActions == rhs.pauseActions,
      lhs.preloadRequired == rhs.preloadRequired,
      lhs.preview == rhs.preview
    else {
      return false
    }
    guard
      lhs.repeatable == rhs.repeatable,
      lhs.resumeActions == rhs.resumeActions,
      lhs.reuseId == rhs.reuseId
    else {
      return false
    }
    guard
      lhs.rowSpan == rhs.rowSpan,
      lhs.scale == rhs.scale,
      lhs.selectedActions == rhs.selectedActions
    else {
      return false
    }
    guard
      lhs.tooltips == rhs.tooltips,
      lhs.transform == rhs.transform,
      lhs.transformations == rhs.transformations
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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["accessibility"] = accessibility?.toDictionary()
    result["alignment_horizontal"] = alignmentHorizontal?.toValidSerializationValue()
    result["alignment_vertical"] = alignmentVertical?.toValidSerializationValue()
    result["alpha"] = alpha.toValidSerializationValue()
    result["animators"] = animators?.map { $0.toDictionary() }
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
    result["functions"] = functions?.map { $0.toDictionary() }
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
    result["transformations"] = transformations?.map { $0.toDictionary() }
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
