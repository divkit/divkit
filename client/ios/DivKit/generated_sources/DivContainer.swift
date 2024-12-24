// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivContainer: DivBase {
  @frozen
  public enum LayoutMode: String, CaseIterable {
    case noWrap = "no_wrap"
    case wrap = "wrap"
  }

  @frozen
  public enum Orientation: String, CaseIterable {
    case vertical = "vertical"
    case horizontal = "horizontal"
    case overlap = "overlap"
  }

  public final class Separator {
    public let margins: DivEdgeInsets?
    public let showAtEnd: Expression<Bool> // default value: false
    public let showAtStart: Expression<Bool> // default value: false
    public let showBetween: Expression<Bool> // default value: true
    public let style: DivDrawable

    public func resolveShowAtEnd(_ resolver: ExpressionResolver) -> Bool {
      resolver.resolveNumeric(showAtEnd) ?? false
    }

    public func resolveShowAtStart(_ resolver: ExpressionResolver) -> Bool {
      resolver.resolveNumeric(showAtStart) ?? false
    }

    public func resolveShowBetween(_ resolver: ExpressionResolver) -> Bool {
      resolver.resolveNumeric(showBetween) ?? true
    }

    init(
      margins: DivEdgeInsets? = nil,
      showAtEnd: Expression<Bool>? = nil,
      showAtStart: Expression<Bool>? = nil,
      showBetween: Expression<Bool>? = nil,
      style: DivDrawable
    ) {
      self.margins = margins
      self.showAtEnd = showAtEnd ?? .value(false)
      self.showAtStart = showAtStart ?? .value(false)
      self.showBetween = showBetween ?? .value(true)
      self.style = style
    }
  }

  public static let type: String = "container"
  public let accessibility: DivAccessibility?
  public let action: DivAction?
  public let actionAnimation: DivAnimation // default value: DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
  public let actions: [DivAction]?
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let animators: [DivAnimator]?
  public let aspect: DivAspect?
  public let background: [DivBackground]?
  public let border: DivBorder?
  public let clipToBounds: Expression<Bool> // default value: true
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let contentAlignmentHorizontal: Expression<DivContentAlignmentHorizontal> // default value: start
  public let contentAlignmentVertical: Expression<DivContentAlignmentVertical> // default value: top
  public let disappearActions: [DivDisappearAction]?
  public let doubletapActions: [DivAction]?
  public let extensions: [DivExtension]?
  public let focus: DivFocus?
  public let functions: [DivFunction]?
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let hoverEndActions: [DivAction]?
  public let hoverStartActions: [DivAction]?
  public let id: String?
  public let itemBuilder: DivCollectionItemBuilder?
  public let items: [Div]?
  public let layoutMode: Expression<LayoutMode> // default value: no_wrap
  public let layoutProvider: DivLayoutProvider?
  public let lineSeparator: Separator?
  public let longtapActions: [DivAction]?
  public let margins: DivEdgeInsets?
  public let orientation: Expression<Orientation> // default value: vertical
  public let paddings: DivEdgeInsets?
  public let pressEndActions: [DivAction]?
  public let pressStartActions: [DivAction]?
  public let reuseId: Expression<String>?
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let selectedActions: [DivAction]?
  public let separator: Separator?
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

  public func resolveClipToBounds(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(clipToBounds) ?? true
  }

  public func resolveColumnSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(columnSpan)
  }

  public func resolveContentAlignmentHorizontal(_ resolver: ExpressionResolver) -> DivContentAlignmentHorizontal {
    resolver.resolveEnum(contentAlignmentHorizontal) ?? DivContentAlignmentHorizontal.start
  }

  public func resolveContentAlignmentVertical(_ resolver: ExpressionResolver) -> DivContentAlignmentVertical {
    resolver.resolveEnum(contentAlignmentVertical) ?? DivContentAlignmentVertical.top
  }

  public func resolveLayoutMode(_ resolver: ExpressionResolver) -> LayoutMode {
    resolver.resolveEnum(layoutMode) ?? LayoutMode.noWrap
  }

  public func resolveOrientation(_ resolver: ExpressionResolver) -> Orientation {
    resolver.resolveEnum(orientation) ?? Orientation.vertical
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
    accessibility: DivAccessibility?,
    action: DivAction?,
    actionAnimation: DivAnimation?,
    actions: [DivAction]?,
    alignmentHorizontal: Expression<DivAlignmentHorizontal>?,
    alignmentVertical: Expression<DivAlignmentVertical>?,
    alpha: Expression<Double>?,
    animators: [DivAnimator]?,
    aspect: DivAspect?,
    background: [DivBackground]?,
    border: DivBorder?,
    clipToBounds: Expression<Bool>?,
    columnSpan: Expression<Int>?,
    contentAlignmentHorizontal: Expression<DivContentAlignmentHorizontal>?,
    contentAlignmentVertical: Expression<DivContentAlignmentVertical>?,
    disappearActions: [DivDisappearAction]?,
    doubletapActions: [DivAction]?,
    extensions: [DivExtension]?,
    focus: DivFocus?,
    functions: [DivFunction]?,
    height: DivSize?,
    hoverEndActions: [DivAction]?,
    hoverStartActions: [DivAction]?,
    id: String?,
    itemBuilder: DivCollectionItemBuilder?,
    items: [Div]?,
    layoutMode: Expression<LayoutMode>?,
    layoutProvider: DivLayoutProvider?,
    lineSeparator: Separator?,
    longtapActions: [DivAction]?,
    margins: DivEdgeInsets?,
    orientation: Expression<Orientation>?,
    paddings: DivEdgeInsets?,
    pressEndActions: [DivAction]?,
    pressStartActions: [DivAction]?,
    reuseId: Expression<String>?,
    rowSpan: Expression<Int>?,
    selectedActions: [DivAction]?,
    separator: Separator?,
    tooltips: [DivTooltip]?,
    transform: DivTransform?,
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
    self.action = action
    self.actionAnimation = actionAnimation ?? DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
    self.actions = actions
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha ?? .value(1.0)
    self.animators = animators
    self.aspect = aspect
    self.background = background
    self.border = border
    self.clipToBounds = clipToBounds ?? .value(true)
    self.columnSpan = columnSpan
    self.contentAlignmentHorizontal = contentAlignmentHorizontal ?? .value(.start)
    self.contentAlignmentVertical = contentAlignmentVertical ?? .value(.top)
    self.disappearActions = disappearActions
    self.doubletapActions = doubletapActions
    self.extensions = extensions
    self.focus = focus
    self.functions = functions
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.hoverEndActions = hoverEndActions
    self.hoverStartActions = hoverStartActions
    self.id = id
    self.itemBuilder = itemBuilder
    self.items = items
    self.layoutMode = layoutMode ?? .value(.noWrap)
    self.layoutProvider = layoutProvider
    self.lineSeparator = lineSeparator
    self.longtapActions = longtapActions
    self.margins = margins
    self.orientation = orientation ?? .value(.vertical)
    self.paddings = paddings
    self.pressEndActions = pressEndActions
    self.pressStartActions = pressStartActions
    self.reuseId = reuseId
    self.rowSpan = rowSpan
    self.selectedActions = selectedActions
    self.separator = separator
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
extension DivContainer: Equatable {
  public static func ==(lhs: DivContainer, rhs: DivContainer) -> Bool {
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
      lhs.aspect == rhs.aspect
    else {
      return false
    }
    guard
      lhs.background == rhs.background,
      lhs.border == rhs.border,
      lhs.clipToBounds == rhs.clipToBounds
    else {
      return false
    }
    guard
      lhs.columnSpan == rhs.columnSpan,
      lhs.contentAlignmentHorizontal == rhs.contentAlignmentHorizontal,
      lhs.contentAlignmentVertical == rhs.contentAlignmentVertical
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
      lhs.itemBuilder == rhs.itemBuilder,
      lhs.items == rhs.items,
      lhs.layoutMode == rhs.layoutMode
    else {
      return false
    }
    guard
      lhs.layoutProvider == rhs.layoutProvider,
      lhs.lineSeparator == rhs.lineSeparator,
      lhs.longtapActions == rhs.longtapActions
    else {
      return false
    }
    guard
      lhs.margins == rhs.margins,
      lhs.orientation == rhs.orientation,
      lhs.paddings == rhs.paddings
    else {
      return false
    }
    guard
      lhs.pressEndActions == rhs.pressEndActions,
      lhs.pressStartActions == rhs.pressStartActions,
      lhs.reuseId == rhs.reuseId
    else {
      return false
    }
    guard
      lhs.rowSpan == rhs.rowSpan,
      lhs.selectedActions == rhs.selectedActions,
      lhs.separator == rhs.separator
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

extension DivContainer: Serializable {
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
    result["aspect"] = aspect?.toDictionary()
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border?.toDictionary()
    result["clip_to_bounds"] = clipToBounds.toValidSerializationValue()
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["content_alignment_horizontal"] = contentAlignmentHorizontal.toValidSerializationValue()
    result["content_alignment_vertical"] = contentAlignmentVertical.toValidSerializationValue()
    result["disappear_actions"] = disappearActions?.map { $0.toDictionary() }
    result["doubletap_actions"] = doubletapActions?.map { $0.toDictionary() }
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["functions"] = functions?.map { $0.toDictionary() }
    result["height"] = height.toDictionary()
    result["hover_end_actions"] = hoverEndActions?.map { $0.toDictionary() }
    result["hover_start_actions"] = hoverStartActions?.map { $0.toDictionary() }
    result["id"] = id
    result["item_builder"] = itemBuilder?.toDictionary()
    result["items"] = items?.map { $0.toDictionary() }
    result["layout_mode"] = layoutMode.toValidSerializationValue()
    result["layout_provider"] = layoutProvider?.toDictionary()
    result["line_separator"] = lineSeparator?.toDictionary()
    result["longtap_actions"] = longtapActions?.map { $0.toDictionary() }
    result["margins"] = margins?.toDictionary()
    result["orientation"] = orientation.toValidSerializationValue()
    result["paddings"] = paddings?.toDictionary()
    result["press_end_actions"] = pressEndActions?.map { $0.toDictionary() }
    result["press_start_actions"] = pressStartActions?.map { $0.toDictionary() }
    result["reuse_id"] = reuseId?.toValidSerializationValue()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
    result["separator"] = separator?.toDictionary()
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
extension DivContainer.Separator: Equatable {
  public static func ==(lhs: DivContainer.Separator, rhs: DivContainer.Separator) -> Bool {
    guard
      lhs.margins == rhs.margins,
      lhs.showAtEnd == rhs.showAtEnd,
      lhs.showAtStart == rhs.showAtStart
    else {
      return false
    }
    guard
      lhs.showBetween == rhs.showBetween,
      lhs.style == rhs.style
    else {
      return false
    }
    return true
  }
}
#endif

extension DivContainer.Separator: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["margins"] = margins?.toDictionary()
    result["show_at_end"] = showAtEnd.toValidSerializationValue()
    result["show_at_start"] = showAtStart.toValidSerializationValue()
    result["show_between"] = showBetween.toValidSerializationValue()
    result["style"] = style.toDictionary()
    return result
  }
}
