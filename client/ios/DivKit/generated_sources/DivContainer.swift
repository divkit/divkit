// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

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
    public let margins: DivEdgeInsets
    public let showAtEnd: Expression<Bool> // default value: false
    public let showAtStart: Expression<Bool> // default value: false
    public let showBetween: Expression<Bool> // default value: true
    public let style: DivDrawable

    public func resolveShowAtEnd(_ resolver: ExpressionResolver) -> Bool {
      resolver.resolveNumericValue(expression: showAtEnd) ?? false
    }

    public func resolveShowAtStart(_ resolver: ExpressionResolver) -> Bool {
      resolver.resolveNumericValue(expression: showAtStart) ?? false
    }

    public func resolveShowBetween(_ resolver: ExpressionResolver) -> Bool {
      resolver.resolveNumericValue(expression: showBetween) ?? true
    }

    static let marginsValidator: AnyValueValidator<DivEdgeInsets> =
      makeNoOpValueValidator()

    static let showAtEndValidator: AnyValueValidator<Bool> =
      makeNoOpValueValidator()

    static let showAtStartValidator: AnyValueValidator<Bool> =
      makeNoOpValueValidator()

    static let showBetweenValidator: AnyValueValidator<Bool> =
      makeNoOpValueValidator()

    init(
      margins: DivEdgeInsets? = nil,
      showAtEnd: Expression<Bool>? = nil,
      showAtStart: Expression<Bool>? = nil,
      showBetween: Expression<Bool>? = nil,
      style: DivDrawable
    ) {
      self.margins = margins ?? DivEdgeInsets()
      self.showAtEnd = showAtEnd ?? .value(false)
      self.showAtStart = showAtStart ?? .value(false)
      self.showBetween = showBetween ?? .value(true)
      self.style = style
    }
  }

  public static let type: String = "container"
  public let accessibility: DivAccessibility
  public let action: DivAction?
  public let actionAnimation: DivAnimation // default value: DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
  public let actions: [DivAction]? // at least 1 elements
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let aspect: DivAspect?
  public let background: [DivBackground]? // at least 1 elements
  public let border: DivBorder
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let contentAlignmentHorizontal: Expression<DivContentAlignmentHorizontal> // default value: start
  public let contentAlignmentVertical: Expression<DivContentAlignmentVertical> // default value: top
  public let disappearActions: [DivDisappearAction]? // at least 1 elements
  public let doubletapActions: [DivAction]? // at least 1 elements
  public let extensions: [DivExtension]? // at least 1 elements
  public let focus: DivFocus?
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: String? // at least 1 char
  public let items: [Div] // at least 1 elements
  public let layoutMode: Expression<LayoutMode> // default value: no_wrap
  public let lineSeparator: Separator?
  public let longtapActions: [DivAction]? // at least 1 elements
  public let margins: DivEdgeInsets
  public let orientation: Expression<Orientation> // default value: vertical
  public let paddings: DivEdgeInsets
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let selectedActions: [DivAction]? // at least 1 elements
  public let separator: Separator?
  public let tooltips: [DivTooltip]? // at least 1 elements
  public let transform: DivTransform
  public let transitionChange: DivChangeTransition?
  public let transitionIn: DivAppearanceTransition?
  public let transitionOut: DivAppearanceTransition?
  public let transitionTriggers: [DivTransitionTrigger]? // at least 1 elements
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

  public func resolveColumnSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: columnSpan)
  }

  public func resolveContentAlignmentHorizontal(_ resolver: ExpressionResolver) -> DivContentAlignmentHorizontal {
    resolver.resolveStringBasedValue(expression: contentAlignmentHorizontal, initializer: DivContentAlignmentHorizontal.init(rawValue:)) ?? DivContentAlignmentHorizontal.start
  }

  public func resolveContentAlignmentVertical(_ resolver: ExpressionResolver) -> DivContentAlignmentVertical {
    resolver.resolveStringBasedValue(expression: contentAlignmentVertical, initializer: DivContentAlignmentVertical.init(rawValue:)) ?? DivContentAlignmentVertical.top
  }

  public func resolveLayoutMode(_ resolver: ExpressionResolver) -> LayoutMode {
    resolver.resolveStringBasedValue(expression: layoutMode, initializer: LayoutMode.init(rawValue:)) ?? LayoutMode.noWrap
  }

  public func resolveOrientation(_ resolver: ExpressionResolver) -> Orientation {
    resolver.resolveStringBasedValue(expression: orientation, initializer: Orientation.init(rawValue:)) ?? Orientation.vertical
  }

  public func resolveRowSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: rowSpan)
  }

  public func resolveVisibility(_ resolver: ExpressionResolver) -> DivVisibility {
    resolver.resolveStringBasedValue(expression: visibility, initializer: DivVisibility.init(rawValue:)) ?? DivVisibility.visible
  }

  static let accessibilityValidator: AnyValueValidator<DivAccessibility> =
    makeNoOpValueValidator()

  static let actionValidator: AnyValueValidator<DivAction> =
    makeNoOpValueValidator()

  static let actionAnimationValidator: AnyValueValidator<DivAnimation> =
    makeNoOpValueValidator()

  static let actionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let alignmentHorizontalValidator: AnyValueValidator<DivAlignmentHorizontal> =
    makeNoOpValueValidator()

  static let alignmentVerticalValidator: AnyValueValidator<DivAlignmentVertical> =
    makeNoOpValueValidator()

  static let alphaValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 && $0 <= 1.0 })

  static let aspectValidator: AnyValueValidator<DivAspect> =
    makeNoOpValueValidator()

  static let backgroundValidator: AnyArrayValueValidator<DivBackground> =
    makeArrayValidator(minItems: 1)

  static let borderValidator: AnyValueValidator<DivBorder> =
    makeNoOpValueValidator()

  static let columnSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let contentAlignmentHorizontalValidator: AnyValueValidator<DivContentAlignmentHorizontal> =
    makeNoOpValueValidator()

  static let contentAlignmentVerticalValidator: AnyValueValidator<DivContentAlignmentVertical> =
    makeNoOpValueValidator()

  static let disappearActionsValidator: AnyArrayValueValidator<DivDisappearAction> =
    makeArrayValidator(minItems: 1)

  static let doubletapActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let extensionsValidator: AnyArrayValueValidator<DivExtension> =
    makeArrayValidator(minItems: 1)

  static let focusValidator: AnyValueValidator<DivFocus> =
    makeNoOpValueValidator()

  static let heightValidator: AnyValueValidator<DivSize> =
    makeNoOpValueValidator()

  static let idValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let itemsValidator: AnyArrayValueValidator<Div> =
    makeArrayValidator(minItems: 1)

  static let layoutModeValidator: AnyValueValidator<DivContainer.LayoutMode> =
    makeNoOpValueValidator()

  static let lineSeparatorValidator: AnyValueValidator<DivContainer.Separator> =
    makeNoOpValueValidator()

  static let longtapActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let marginsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let orientationValidator: AnyValueValidator<DivContainer.Orientation> =
    makeNoOpValueValidator()

  static let paddingsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let rowSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let selectedActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let separatorValidator: AnyValueValidator<DivContainer.Separator> =
    makeNoOpValueValidator()

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
    accessibility: DivAccessibility?,
    action: DivAction?,
    actionAnimation: DivAnimation?,
    actions: [DivAction]?,
    alignmentHorizontal: Expression<DivAlignmentHorizontal>?,
    alignmentVertical: Expression<DivAlignmentVertical>?,
    alpha: Expression<Double>?,
    aspect: DivAspect?,
    background: [DivBackground]?,
    border: DivBorder?,
    columnSpan: Expression<Int>?,
    contentAlignmentHorizontal: Expression<DivContentAlignmentHorizontal>?,
    contentAlignmentVertical: Expression<DivContentAlignmentVertical>?,
    disappearActions: [DivDisappearAction]?,
    doubletapActions: [DivAction]?,
    extensions: [DivExtension]?,
    focus: DivFocus?,
    height: DivSize?,
    id: String?,
    items: [Div],
    layoutMode: Expression<LayoutMode>?,
    lineSeparator: Separator?,
    longtapActions: [DivAction]?,
    margins: DivEdgeInsets?,
    orientation: Expression<Orientation>?,
    paddings: DivEdgeInsets?,
    rowSpan: Expression<Int>?,
    selectedActions: [DivAction]?,
    separator: Separator?,
    tooltips: [DivTooltip]?,
    transform: DivTransform?,
    transitionChange: DivChangeTransition?,
    transitionIn: DivAppearanceTransition?,
    transitionOut: DivAppearanceTransition?,
    transitionTriggers: [DivTransitionTrigger]?,
    visibility: Expression<DivVisibility>?,
    visibilityAction: DivVisibilityAction?,
    visibilityActions: [DivVisibilityAction]?,
    width: DivSize?
  ) {
    self.accessibility = accessibility ?? DivAccessibility()
    self.action = action
    self.actionAnimation = actionAnimation ?? DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
    self.actions = actions
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha ?? .value(1.0)
    self.aspect = aspect
    self.background = background
    self.border = border ?? DivBorder()
    self.columnSpan = columnSpan
    self.contentAlignmentHorizontal = contentAlignmentHorizontal ?? .value(.start)
    self.contentAlignmentVertical = contentAlignmentVertical ?? .value(.top)
    self.disappearActions = disappearActions
    self.doubletapActions = doubletapActions
    self.extensions = extensions
    self.focus = focus
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.id = id
    self.items = items
    self.layoutMode = layoutMode ?? .value(.noWrap)
    self.lineSeparator = lineSeparator
    self.longtapActions = longtapActions
    self.margins = margins ?? DivEdgeInsets()
    self.orientation = orientation ?? .value(.vertical)
    self.paddings = paddings ?? DivEdgeInsets()
    self.rowSpan = rowSpan
    self.selectedActions = selectedActions
    self.separator = separator
    self.tooltips = tooltips
    self.transform = transform ?? DivTransform()
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
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
      lhs.aspect == rhs.aspect,
      lhs.background == rhs.background
    else {
      return false
    }
    guard
      lhs.border == rhs.border,
      lhs.columnSpan == rhs.columnSpan,
      lhs.contentAlignmentHorizontal == rhs.contentAlignmentHorizontal
    else {
      return false
    }
    guard
      lhs.contentAlignmentVertical == rhs.contentAlignmentVertical,
      lhs.disappearActions == rhs.disappearActions,
      lhs.doubletapActions == rhs.doubletapActions
    else {
      return false
    }
    guard
      lhs.extensions == rhs.extensions,
      lhs.focus == rhs.focus,
      lhs.height == rhs.height
    else {
      return false
    }
    guard
      lhs.id == rhs.id,
      lhs.items == rhs.items,
      lhs.layoutMode == rhs.layoutMode
    else {
      return false
    }
    guard
      lhs.lineSeparator == rhs.lineSeparator,
      lhs.longtapActions == rhs.longtapActions,
      lhs.margins == rhs.margins
    else {
      return false
    }
    guard
      lhs.orientation == rhs.orientation,
      lhs.paddings == rhs.paddings,
      lhs.rowSpan == rhs.rowSpan
    else {
      return false
    }
    guard
      lhs.selectedActions == rhs.selectedActions,
      lhs.separator == rhs.separator,
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
    result["accessibility"] = accessibility.toDictionary()
    result["action"] = action?.toDictionary()
    result["action_animation"] = actionAnimation.toDictionary()
    result["actions"] = actions?.map { $0.toDictionary() }
    result["alignment_horizontal"] = alignmentHorizontal?.toValidSerializationValue()
    result["alignment_vertical"] = alignmentVertical?.toValidSerializationValue()
    result["alpha"] = alpha.toValidSerializationValue()
    result["aspect"] = aspect?.toDictionary()
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border.toDictionary()
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["content_alignment_horizontal"] = contentAlignmentHorizontal.toValidSerializationValue()
    result["content_alignment_vertical"] = contentAlignmentVertical.toValidSerializationValue()
    result["disappear_actions"] = disappearActions?.map { $0.toDictionary() }
    result["doubletap_actions"] = doubletapActions?.map { $0.toDictionary() }
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["height"] = height.toDictionary()
    result["id"] = id
    result["items"] = items.map { $0.toDictionary() }
    result["layout_mode"] = layoutMode.toValidSerializationValue()
    result["line_separator"] = lineSeparator?.toDictionary()
    result["longtap_actions"] = longtapActions?.map { $0.toDictionary() }
    result["margins"] = margins.toDictionary()
    result["orientation"] = orientation.toValidSerializationValue()
    result["paddings"] = paddings.toDictionary()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
    result["separator"] = separator?.toDictionary()
    result["tooltips"] = tooltips?.map { $0.toDictionary() }
    result["transform"] = transform.toDictionary()
    result["transition_change"] = transitionChange?.toDictionary()
    result["transition_in"] = transitionIn?.toDictionary()
    result["transition_out"] = transitionOut?.toDictionary()
    result["transition_triggers"] = transitionTriggers?.map { $0.rawValue }
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
    result["margins"] = margins.toDictionary()
    result["show_at_end"] = showAtEnd.toValidSerializationValue()
    result["show_at_start"] = showAtStart.toValidSerializationValue()
    result["show_between"] = showBetween.toValidSerializationValue()
    result["style"] = style.toDictionary()
    return result
  }
}
