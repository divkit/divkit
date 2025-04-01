// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivState: DivBase, Sendable {
  public final class State: Sendable {
    public let animationIn: DivAnimation?
    public let animationOut: DivAnimation?
    public let div: Div?
    public let stateId: String
    public let swipeOutActions: [DivAction]?

    init(
      animationIn: DivAnimation?,
      animationOut: DivAnimation?,
      div: Div?,
      stateId: String,
      swipeOutActions: [DivAction]?
    ) {
      self.animationIn = animationIn
      self.animationOut = animationOut
      self.div = div
      self.stateId = stateId
      self.swipeOutActions = swipeOutActions
    }
  }

  public static let type: String = "state"
  public let accessibility: DivAccessibility?
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let animators: [DivAnimator]?
  public let background: [DivBackground]?
  public let border: DivBorder?
  public let captureFocusOnAction: Expression<Bool> // default value: true
  public let clipToBounds: Expression<Bool> // default value: true
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let defaultStateId: Expression<String>?
  public let disappearActions: [DivDisappearAction]?
  public let divId: String?
  public let extensions: [DivExtension]?
  public let focus: DivFocus?
  public let functions: [DivFunction]?
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: String?
  public let layoutProvider: DivLayoutProvider?
  public let margins: DivEdgeInsets?
  public let paddings: DivEdgeInsets?
  public let reuseId: Expression<String>?
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let selectedActions: [DivAction]?
  public let stateIdVariable: String?
  public let states: [State] // at least 1 elements
  public let tooltips: [DivTooltip]?
  public let transform: DivTransform?
  public let transitionAnimationSelector: Expression<DivTransitionSelector> // default value: state_change
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

  public func resolveCaptureFocusOnAction(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(captureFocusOnAction) ?? true
  }

  public func resolveClipToBounds(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(clipToBounds) ?? true
  }

  public func resolveColumnSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(columnSpan)
  }

  public func resolveDefaultStateId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(defaultStateId)
  }

  public func resolveReuseId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(reuseId)
  }

  public func resolveRowSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(rowSpan)
  }

  public func resolveTransitionAnimationSelector(_ resolver: ExpressionResolver) -> DivTransitionSelector {
    resolver.resolveEnum(transitionAnimationSelector) ?? DivTransitionSelector.stateChange
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

  static let statesValidator: AnyArrayValueValidator<DivState.State> =
    makeArrayValidator(minItems: 1)

  static let transitionTriggersValidator: AnyArrayValueValidator<DivTransitionTrigger> =
    makeArrayValidator(minItems: 1)

  init(
    accessibility: DivAccessibility?,
    alignmentHorizontal: Expression<DivAlignmentHorizontal>?,
    alignmentVertical: Expression<DivAlignmentVertical>?,
    alpha: Expression<Double>?,
    animators: [DivAnimator]?,
    background: [DivBackground]?,
    border: DivBorder?,
    captureFocusOnAction: Expression<Bool>?,
    clipToBounds: Expression<Bool>?,
    columnSpan: Expression<Int>?,
    defaultStateId: Expression<String>?,
    disappearActions: [DivDisappearAction]?,
    divId: String?,
    extensions: [DivExtension]?,
    focus: DivFocus?,
    functions: [DivFunction]?,
    height: DivSize?,
    id: String?,
    layoutProvider: DivLayoutProvider?,
    margins: DivEdgeInsets?,
    paddings: DivEdgeInsets?,
    reuseId: Expression<String>?,
    rowSpan: Expression<Int>?,
    selectedActions: [DivAction]?,
    stateIdVariable: String?,
    states: [State],
    tooltips: [DivTooltip]?,
    transform: DivTransform?,
    transitionAnimationSelector: Expression<DivTransitionSelector>?,
    transitionChange: DivChangeTransition?,
    transitionIn: DivAppearanceTransition?,
    transitionOut: DivAppearanceTransition?,
    transitionTriggers: [DivTransitionTrigger]?,
    variableTriggers: [DivTrigger]?,
    variables: [DivVariable]?,
    visibility: Expression<DivVisibility>?,
    visibilityAction: DivVisibilityAction?,
    visibilityActions: [DivVisibilityAction]?,
    width: DivSize?
  ) {
    self.accessibility = accessibility
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha ?? .value(1.0)
    self.animators = animators
    self.background = background
    self.border = border
    self.captureFocusOnAction = captureFocusOnAction ?? .value(true)
    self.clipToBounds = clipToBounds ?? .value(true)
    self.columnSpan = columnSpan
    self.defaultStateId = defaultStateId
    self.disappearActions = disappearActions
    self.divId = divId
    self.extensions = extensions
    self.focus = focus
    self.functions = functions
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.id = id
    self.layoutProvider = layoutProvider
    self.margins = margins
    self.paddings = paddings
    self.reuseId = reuseId
    self.rowSpan = rowSpan
    self.selectedActions = selectedActions
    self.stateIdVariable = stateIdVariable
    self.states = states
    self.tooltips = tooltips
    self.transform = transform
    self.transitionAnimationSelector = transitionAnimationSelector ?? .value(.stateChange)
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
extension DivState: Equatable {
  public static func ==(lhs: DivState, rhs: DivState) -> Bool {
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
      lhs.background == rhs.background
    else {
      return false
    }
    guard
      lhs.border == rhs.border,
      lhs.captureFocusOnAction == rhs.captureFocusOnAction,
      lhs.clipToBounds == rhs.clipToBounds
    else {
      return false
    }
    guard
      lhs.columnSpan == rhs.columnSpan,
      lhs.defaultStateId == rhs.defaultStateId,
      lhs.disappearActions == rhs.disappearActions
    else {
      return false
    }
    guard
      lhs.divId == rhs.divId,
      lhs.extensions == rhs.extensions,
      lhs.focus == rhs.focus
    else {
      return false
    }
    guard
      lhs.functions == rhs.functions,
      lhs.height == rhs.height,
      lhs.id == rhs.id
    else {
      return false
    }
    guard
      lhs.layoutProvider == rhs.layoutProvider,
      lhs.margins == rhs.margins,
      lhs.paddings == rhs.paddings
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
      lhs.stateIdVariable == rhs.stateIdVariable,
      lhs.states == rhs.states,
      lhs.tooltips == rhs.tooltips
    else {
      return false
    }
    guard
      lhs.transform == rhs.transform,
      lhs.transitionAnimationSelector == rhs.transitionAnimationSelector,
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

extension DivState: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["accessibility"] = accessibility?.toDictionary()
    result["alignment_horizontal"] = alignmentHorizontal?.toValidSerializationValue()
    result["alignment_vertical"] = alignmentVertical?.toValidSerializationValue()
    result["alpha"] = alpha.toValidSerializationValue()
    result["animators"] = animators?.map { $0.toDictionary() }
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border?.toDictionary()
    result["capture_focus_on_action"] = captureFocusOnAction.toValidSerializationValue()
    result["clip_to_bounds"] = clipToBounds.toValidSerializationValue()
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["default_state_id"] = defaultStateId?.toValidSerializationValue()
    result["disappear_actions"] = disappearActions?.map { $0.toDictionary() }
    result["div_id"] = divId
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["functions"] = functions?.map { $0.toDictionary() }
    result["height"] = height.toDictionary()
    result["id"] = id
    result["layout_provider"] = layoutProvider?.toDictionary()
    result["margins"] = margins?.toDictionary()
    result["paddings"] = paddings?.toDictionary()
    result["reuse_id"] = reuseId?.toValidSerializationValue()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
    result["state_id_variable"] = stateIdVariable
    result["states"] = states.map { $0.toDictionary() }
    result["tooltips"] = tooltips?.map { $0.toDictionary() }
    result["transform"] = transform?.toDictionary()
    result["transition_animation_selector"] = transitionAnimationSelector.toValidSerializationValue()
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
extension DivState.State: Equatable {
  public static func ==(lhs: DivState.State, rhs: DivState.State) -> Bool {
    guard
      lhs.animationIn == rhs.animationIn,
      lhs.animationOut == rhs.animationOut,
      lhs.div == rhs.div
    else {
      return false
    }
    guard
      lhs.stateId == rhs.stateId,
      lhs.swipeOutActions == rhs.swipeOutActions
    else {
      return false
    }
    return true
  }
}
#endif

extension DivState.State: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["animation_in"] = animationIn?.toDictionary()
    result["animation_out"] = animationOut?.toDictionary()
    result["div"] = div?.toDictionary()
    result["state_id"] = stateId
    result["swipe_out_actions"] = swipeOutActions?.map { $0.toDictionary() }
    return result
  }
}
