// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivPager: DivBase {
  @frozen
  public enum Orientation: String, CaseIterable {
    case horizontal = "horizontal"
    case vertical = "vertical"
  }

  public static let type: String = "pager"
  public let accessibility: DivAccessibility
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let background: [DivBackground]? // at least 1 elements
  public let border: DivBorder
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let defaultItem: Expression<Int> // constraint: number >= 0; default value: 0
  public let disappearActions: [DivDisappearAction]? // at least 1 elements
  public let extensions: [DivExtension]? // at least 1 elements
  public let focus: DivFocus?
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: String? // at least 1 char
  public let infiniteScroll: Expression<Bool> // default value: false
  public let itemSpacing: DivFixedSize // default value: DivFixedSize(value: .value(0))
  public let items: [Div] // at least 1 elements
  public let layoutMode: DivPagerLayoutMode
  public let margins: DivEdgeInsets
  public let orientation: Expression<Orientation> // default value: horizontal
  public let paddings: DivEdgeInsets
  public let restrictParentScroll: Expression<Bool> // default value: false
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let selectedActions: [DivAction]? // at least 1 elements
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

  public func resolveDefaultItem(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: defaultItem) ?? 0
  }

  public func resolveInfiniteScroll(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumericValue(expression: infiniteScroll) ?? false
  }

  public func resolveOrientation(_ resolver: ExpressionResolver) -> Orientation {
    resolver.resolveStringBasedValue(expression: orientation, initializer: Orientation.init(rawValue:)) ?? Orientation.horizontal
  }

  public func resolveRestrictParentScroll(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumericValue(expression: restrictParentScroll) ?? false
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

  static let backgroundValidator: AnyArrayValueValidator<DivBackground> =
    makeArrayValidator(minItems: 1)

  static let borderValidator: AnyValueValidator<DivBorder> =
    makeNoOpValueValidator()

  static let columnSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let defaultItemValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let disappearActionsValidator: AnyArrayValueValidator<DivDisappearAction> =
    makeArrayValidator(minItems: 1)

  static let extensionsValidator: AnyArrayValueValidator<DivExtension> =
    makeArrayValidator(minItems: 1)

  static let focusValidator: AnyValueValidator<DivFocus> =
    makeNoOpValueValidator()

  static let heightValidator: AnyValueValidator<DivSize> =
    makeNoOpValueValidator()

  static let idValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let infiniteScrollValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let itemSpacingValidator: AnyValueValidator<DivFixedSize> =
    makeNoOpValueValidator()

  static let itemsValidator: AnyArrayValueValidator<Div> =
    makeArrayValidator(minItems: 1)

  static let marginsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let orientationValidator: AnyValueValidator<DivPager.Orientation> =
    makeNoOpValueValidator()

  static let paddingsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let restrictParentScrollValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

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
    accessibility: DivAccessibility?,
    alignmentHorizontal: Expression<DivAlignmentHorizontal>?,
    alignmentVertical: Expression<DivAlignmentVertical>?,
    alpha: Expression<Double>?,
    background: [DivBackground]?,
    border: DivBorder?,
    columnSpan: Expression<Int>?,
    defaultItem: Expression<Int>?,
    disappearActions: [DivDisappearAction]?,
    extensions: [DivExtension]?,
    focus: DivFocus?,
    height: DivSize?,
    id: String?,
    infiniteScroll: Expression<Bool>?,
    itemSpacing: DivFixedSize?,
    items: [Div],
    layoutMode: DivPagerLayoutMode,
    margins: DivEdgeInsets?,
    orientation: Expression<Orientation>?,
    paddings: DivEdgeInsets?,
    restrictParentScroll: Expression<Bool>?,
    rowSpan: Expression<Int>?,
    selectedActions: [DivAction]?,
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
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha ?? .value(1.0)
    self.background = background
    self.border = border ?? DivBorder()
    self.columnSpan = columnSpan
    self.defaultItem = defaultItem ?? .value(0)
    self.disappearActions = disappearActions
    self.extensions = extensions
    self.focus = focus
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.id = id
    self.infiniteScroll = infiniteScroll ?? .value(false)
    self.itemSpacing = itemSpacing ?? DivFixedSize(value: .value(0))
    self.items = items
    self.layoutMode = layoutMode
    self.margins = margins ?? DivEdgeInsets()
    self.orientation = orientation ?? .value(.horizontal)
    self.paddings = paddings ?? DivEdgeInsets()
    self.restrictParentScroll = restrictParentScroll ?? .value(false)
    self.rowSpan = rowSpan
    self.selectedActions = selectedActions
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
extension DivPager: Equatable {
  public static func ==(lhs: DivPager, rhs: DivPager) -> Bool {
    guard
      lhs.accessibility == rhs.accessibility,
      lhs.alignmentHorizontal == rhs.alignmentHorizontal,
      lhs.alignmentVertical == rhs.alignmentVertical
    else {
      return false
    }
    guard
      lhs.alpha == rhs.alpha,
      lhs.background == rhs.background,
      lhs.border == rhs.border
    else {
      return false
    }
    guard
      lhs.columnSpan == rhs.columnSpan,
      lhs.defaultItem == rhs.defaultItem,
      lhs.disappearActions == rhs.disappearActions
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
      lhs.infiniteScroll == rhs.infiniteScroll,
      lhs.itemSpacing == rhs.itemSpacing
    else {
      return false
    }
    guard
      lhs.items == rhs.items,
      lhs.layoutMode == rhs.layoutMode,
      lhs.margins == rhs.margins
    else {
      return false
    }
    guard
      lhs.orientation == rhs.orientation,
      lhs.paddings == rhs.paddings,
      lhs.restrictParentScroll == rhs.restrictParentScroll
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

extension DivPager: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["accessibility"] = accessibility.toDictionary()
    result["alignment_horizontal"] = alignmentHorizontal?.toValidSerializationValue()
    result["alignment_vertical"] = alignmentVertical?.toValidSerializationValue()
    result["alpha"] = alpha.toValidSerializationValue()
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border.toDictionary()
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["default_item"] = defaultItem.toValidSerializationValue()
    result["disappear_actions"] = disappearActions?.map { $0.toDictionary() }
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["height"] = height.toDictionary()
    result["id"] = id
    result["infinite_scroll"] = infiniteScroll.toValidSerializationValue()
    result["item_spacing"] = itemSpacing.toDictionary()
    result["items"] = items.map { $0.toDictionary() }
    result["layout_mode"] = layoutMode.toDictionary()
    result["margins"] = margins.toDictionary()
    result["orientation"] = orientation.toValidSerializationValue()
    result["paddings"] = paddings.toDictionary()
    result["restrict_parent_scroll"] = restrictParentScroll.toValidSerializationValue()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
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
