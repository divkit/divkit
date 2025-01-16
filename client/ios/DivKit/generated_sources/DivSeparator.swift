// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivSeparator: DivBase, Sendable {
  public final class DelimiterStyle: Sendable {
    @frozen
    public enum Orientation: String, CaseIterable, Sendable {
      case vertical = "vertical"
      case horizontal = "horizontal"
    }

    public let color: Expression<Color> // default value: #14000000
    public let orientation: Expression<Orientation> // default value: horizontal

    public func resolveColor(_ resolver: ExpressionResolver) -> Color {
      resolver.resolveColor(color) ?? Color.colorWithARGBHexCode(0x14000000)
    }

    public func resolveOrientation(_ resolver: ExpressionResolver) -> Orientation {
      resolver.resolveEnum(orientation) ?? Orientation.horizontal
    }

    init(
      color: Expression<Color>? = nil,
      orientation: Expression<Orientation>? = nil
    ) {
      self.color = color ?? .value(Color.colorWithARGBHexCode(0x14000000))
      self.orientation = orientation ?? .value(.horizontal)
    }
  }

  public static let type: String = "separator"
  public let accessibility: DivAccessibility?
  public let action: DivAction?
  public let actionAnimation: DivAnimation // default value: DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
  public let actions: [DivAction]?
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let animators: [DivAnimator]?
  public let background: [DivBackground]?
  public let border: DivBorder?
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let delimiterStyle: DelimiterStyle?
  public let disappearActions: [DivDisappearAction]?
  public let doubletapActions: [DivAction]?
  public let extensions: [DivExtension]?
  public let focus: DivFocus?
  public let functions: [DivFunction]?
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let hoverEndActions: [DivAction]?
  public let hoverStartActions: [DivAction]?
  public let id: String?
  public let layoutProvider: DivLayoutProvider?
  public let longtapActions: [DivAction]?
  public let margins: DivEdgeInsets?
  public let paddings: DivEdgeInsets?
  public let pressEndActions: [DivAction]?
  public let pressStartActions: [DivAction]?
  public let reuseId: Expression<String>?
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let selectedActions: [DivAction]?
  public let tooltips: [DivTooltip]?
  public let transform: DivTransform?
  public let transitionChange: DivChangeTransition?
  public let transitionIn: DivAppearanceTransition?
  public let transitionOut: DivAppearanceTransition?
  public let transitionTriggers: [DivTransitionTrigger]? // at least 1 elements
  public let variableTriggers: [DivTrigger]?
  public let variables: [DivVariable]?
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

  public func resolveColumnSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(columnSpan)
  }

  public func resolveReuseId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(reuseId)
  }

  public func resolveRowSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(rowSpan)
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

  init(
    accessibility: DivAccessibility? = nil,
    action: DivAction? = nil,
    actionAnimation: DivAnimation? = nil,
    actions: [DivAction]? = nil,
    alignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    alignmentVertical: Expression<DivAlignmentVertical>? = nil,
    alpha: Expression<Double>? = nil,
    animators: [DivAnimator]? = nil,
    background: [DivBackground]? = nil,
    border: DivBorder? = nil,
    columnSpan: Expression<Int>? = nil,
    delimiterStyle: DelimiterStyle? = nil,
    disappearActions: [DivDisappearAction]? = nil,
    doubletapActions: [DivAction]? = nil,
    extensions: [DivExtension]? = nil,
    focus: DivFocus? = nil,
    functions: [DivFunction]? = nil,
    height: DivSize? = nil,
    hoverEndActions: [DivAction]? = nil,
    hoverStartActions: [DivAction]? = nil,
    id: String? = nil,
    layoutProvider: DivLayoutProvider? = nil,
    longtapActions: [DivAction]? = nil,
    margins: DivEdgeInsets? = nil,
    paddings: DivEdgeInsets? = nil,
    pressEndActions: [DivAction]? = nil,
    pressStartActions: [DivAction]? = nil,
    reuseId: Expression<String>? = nil,
    rowSpan: Expression<Int>? = nil,
    selectedActions: [DivAction]? = nil,
    tooltips: [DivTooltip]? = nil,
    transform: DivTransform? = nil,
    transitionChange: DivChangeTransition? = nil,
    transitionIn: DivAppearanceTransition? = nil,
    transitionOut: DivAppearanceTransition? = nil,
    transitionTriggers: [DivTransitionTrigger]? = nil,
    variableTriggers: [DivTrigger]? = nil,
    variables: [DivVariable]? = nil,
    visibility: Expression<DivVisibility>? = nil,
    visibilityAction: DivVisibilityAction? = nil,
    visibilityActions: [DivVisibilityAction]? = nil,
    width: DivSize? = nil
  ) {
    self.accessibility = accessibility
    self.action = action
    self.actionAnimation = actionAnimation ?? DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
    self.actions = actions
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha ?? .value(1.0)
    self.animators = animators
    self.background = background
    self.border = border
    self.columnSpan = columnSpan
    self.delimiterStyle = delimiterStyle
    self.disappearActions = disappearActions
    self.doubletapActions = doubletapActions
    self.extensions = extensions
    self.focus = focus
    self.functions = functions
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.hoverEndActions = hoverEndActions
    self.hoverStartActions = hoverStartActions
    self.id = id
    self.layoutProvider = layoutProvider
    self.longtapActions = longtapActions
    self.margins = margins
    self.paddings = paddings
    self.pressEndActions = pressEndActions
    self.pressStartActions = pressStartActions
    self.reuseId = reuseId
    self.rowSpan = rowSpan
    self.selectedActions = selectedActions
    self.tooltips = tooltips
    self.transform = transform
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.variableTriggers = variableTriggers
    self.variables = variables
    self.visibility = visibility ?? .value(.visible)
    self.visibilityAction = visibilityAction
    self.visibilityActions = visibilityActions
    self.width = width ?? .divMatchParentSize(DivMatchParentSize())
  }
}

#if DEBUG
extension DivSeparator: Equatable {
  public static func ==(lhs: DivSeparator, rhs: DivSeparator) -> Bool {
    guard
      lhs.accessibility == rhs.accessibility,
      lhs.action == rhs.action,
      lhs.actionAnimation == rhs.actionAnimation
    else {
      return false
    }
    guard
      lhs.actions == rhs.actions,
      lhs.alignmentHorizontal == rhs.alignmentHorizontal,
      lhs.alignmentVertical == rhs.alignmentVertical
    else {
      return false
    }
    guard
      lhs.alpha == rhs.alpha,
      lhs.animators == rhs.animators,
      lhs.background == rhs.background
    else {
      return false
    }
    guard
      lhs.border == rhs.border,
      lhs.columnSpan == rhs.columnSpan,
      lhs.delimiterStyle == rhs.delimiterStyle
    else {
      return false
    }
    guard
      lhs.disappearActions == rhs.disappearActions,
      lhs.doubletapActions == rhs.doubletapActions,
      lhs.extensions == rhs.extensions
    else {
      return false
    }
    guard
      lhs.focus == rhs.focus,
      lhs.functions == rhs.functions,
      lhs.height == rhs.height
    else {
      return false
    }
    guard
      lhs.hoverEndActions == rhs.hoverEndActions,
      lhs.hoverStartActions == rhs.hoverStartActions,
      lhs.id == rhs.id
    else {
      return false
    }
    guard
      lhs.layoutProvider == rhs.layoutProvider,
      lhs.longtapActions == rhs.longtapActions,
      lhs.margins == rhs.margins
    else {
      return false
    }
    guard
      lhs.paddings == rhs.paddings,
      lhs.pressEndActions == rhs.pressEndActions,
      lhs.pressStartActions == rhs.pressStartActions
    else {
      return false
    }
    guard
      lhs.reuseId == rhs.reuseId,
      lhs.rowSpan == rhs.rowSpan,
      lhs.selectedActions == rhs.selectedActions
    else {
      return false
    }
    guard
      lhs.tooltips == rhs.tooltips,
      lhs.transform == rhs.transform,
      lhs.transitionChange == rhs.transitionChange
    else {
      return false
    }
    guard
      lhs.transitionIn == rhs.transitionIn,
      lhs.transitionOut == rhs.transitionOut,
      lhs.transitionTriggers == rhs.transitionTriggers
    else {
      return false
    }
    guard
      lhs.variableTriggers == rhs.variableTriggers,
      lhs.variables == rhs.variables,
      lhs.visibility == rhs.visibility
    else {
      return false
    }
    guard
      lhs.visibilityAction == rhs.visibilityAction,
      lhs.visibilityActions == rhs.visibilityActions,
      lhs.width == rhs.width
    else {
      return false
    }
    return true
  }
}
#endif

extension DivSeparator: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["accessibility"] = accessibility?.toDictionary()
    result["action"] = action?.toDictionary()
    result["action_animation"] = actionAnimation.toDictionary()
    result["actions"] = actions?.map { $0.toDictionary() }
    result["alignment_horizontal"] = alignmentHorizontal?.toValidSerializationValue()
    result["alignment_vertical"] = alignmentVertical?.toValidSerializationValue()
    result["alpha"] = alpha.toValidSerializationValue()
    result["animators"] = animators?.map { $0.toDictionary() }
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border?.toDictionary()
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["delimiter_style"] = delimiterStyle?.toDictionary()
    result["disappear_actions"] = disappearActions?.map { $0.toDictionary() }
    result["doubletap_actions"] = doubletapActions?.map { $0.toDictionary() }
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["functions"] = functions?.map { $0.toDictionary() }
    result["height"] = height.toDictionary()
    result["hover_end_actions"] = hoverEndActions?.map { $0.toDictionary() }
    result["hover_start_actions"] = hoverStartActions?.map { $0.toDictionary() }
    result["id"] = id
    result["layout_provider"] = layoutProvider?.toDictionary()
    result["longtap_actions"] = longtapActions?.map { $0.toDictionary() }
    result["margins"] = margins?.toDictionary()
    result["paddings"] = paddings?.toDictionary()
    result["press_end_actions"] = pressEndActions?.map { $0.toDictionary() }
    result["press_start_actions"] = pressStartActions?.map { $0.toDictionary() }
    result["reuse_id"] = reuseId?.toValidSerializationValue()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
    result["tooltips"] = tooltips?.map { $0.toDictionary() }
    result["transform"] = transform?.toDictionary()
    result["transition_change"] = transitionChange?.toDictionary()
    result["transition_in"] = transitionIn?.toDictionary()
    result["transition_out"] = transitionOut?.toDictionary()
    result["transition_triggers"] = transitionTriggers?.map { $0.rawValue }
    result["variable_triggers"] = variableTriggers?.map { $0.toDictionary() }
    result["variables"] = variables?.map { $0.toDictionary() }
    result["visibility"] = visibility.toValidSerializationValue()
    result["visibility_action"] = visibilityAction?.toDictionary()
    result["visibility_actions"] = visibilityActions?.map { $0.toDictionary() }
    result["width"] = width.toDictionary()
    return result
  }
}

#if DEBUG
extension DivSeparator.DelimiterStyle: Equatable {
  public static func ==(lhs: DivSeparator.DelimiterStyle, rhs: DivSeparator.DelimiterStyle) -> Bool {
    guard
      lhs.color == rhs.color,
      lhs.orientation == rhs.orientation
    else {
      return false
    }
    return true
  }
}
#endif

extension DivSeparator.DelimiterStyle: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["color"] = color.toValidSerializationValue()
    result["orientation"] = orientation.toValidSerializationValue()
    return result
  }
}
