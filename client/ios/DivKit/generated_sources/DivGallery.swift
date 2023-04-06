// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivGallery: DivBase {
  @frozen
  public enum CrossContentAlignment: String, CaseIterable {
    case start = "start"
    case center = "center"
    case end = "end"
  }

  @frozen
  public enum Orientation: String, CaseIterable {
    case horizontal = "horizontal"
    case vertical = "vertical"
  }

  @frozen
  public enum ScrollMode: String, CaseIterable {
    case paging = "paging"
    case `default` = "default"
  }

  public static let type: String = "gallery"
  public let accessibility: DivAccessibility
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let background: [DivBackground]? // at least 1 elements
  public let border: DivBorder
  public let columnCount: Expression<Int>? // constraint: number > 0
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let crossContentAlignment: Expression<CrossContentAlignment> // default value: start
  public let crossSpacing: Expression<Int>? // constraint: number >= 0
  public let defaultItem: Expression<Int> // constraint: number >= 0; default value: 0
  public let extensions: [DivExtension]? // at least 1 elements
  public let focus: DivFocus?
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: String? // at least 1 char
  public let itemSpacing: Expression<Int> // constraint: number >= 0; default value: 8
  public let items: [Div] // at least 1 elements
  public let margins: DivEdgeInsets
  public let orientation: Expression<Orientation> // default value: horizontal
  public let paddings: DivEdgeInsets
  public let restrictParentScroll: Expression<Bool> // default value: false
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let scrollMode: Expression<ScrollMode> // default value: default
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

  public func resolveColumnCount(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: columnCount)
  }

  public func resolveColumnSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: columnSpan)
  }

  public func resolveCrossContentAlignment(_ resolver: ExpressionResolver) -> CrossContentAlignment {
    resolver.resolveStringBasedValue(expression: crossContentAlignment, initializer: CrossContentAlignment.init(rawValue:)) ?? CrossContentAlignment.start
  }

  public func resolveCrossSpacing(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: crossSpacing)
  }

  public func resolveDefaultItem(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: defaultItem) ?? 0
  }

  public func resolveItemSpacing(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: itemSpacing) ?? 8
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

  public func resolveScrollMode(_ resolver: ExpressionResolver) -> ScrollMode {
    resolver.resolveStringBasedValue(expression: scrollMode, initializer: ScrollMode.init(rawValue:)) ?? ScrollMode.default
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

  static let columnCountValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 > 0 })

  static let columnSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let crossContentAlignmentValidator: AnyValueValidator<DivGallery.CrossContentAlignment> =
    makeNoOpValueValidator()

  static let crossSpacingValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let defaultItemValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let extensionsValidator: AnyArrayValueValidator<DivExtension> =
    makeArrayValidator(minItems: 1)

  static let focusValidator: AnyValueValidator<DivFocus> =
    makeNoOpValueValidator()

  static let heightValidator: AnyValueValidator<DivSize> =
    makeNoOpValueValidator()

  static let idValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let itemSpacingValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let itemsValidator: AnyArrayValueValidator<Div> =
    makeArrayValidator(minItems: 1)

  static let marginsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let orientationValidator: AnyValueValidator<DivGallery.Orientation> =
    makeNoOpValueValidator()

  static let paddingsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let restrictParentScrollValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let rowSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let scrollModeValidator: AnyValueValidator<DivGallery.ScrollMode> =
    makeNoOpValueValidator()

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
    columnCount: Expression<Int>?,
    columnSpan: Expression<Int>?,
    crossContentAlignment: Expression<CrossContentAlignment>?,
    crossSpacing: Expression<Int>?,
    defaultItem: Expression<Int>?,
    extensions: [DivExtension]?,
    focus: DivFocus?,
    height: DivSize?,
    id: String?,
    itemSpacing: Expression<Int>?,
    items: [Div],
    margins: DivEdgeInsets?,
    orientation: Expression<Orientation>?,
    paddings: DivEdgeInsets?,
    restrictParentScroll: Expression<Bool>?,
    rowSpan: Expression<Int>?,
    scrollMode: Expression<ScrollMode>?,
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
    self.columnCount = columnCount
    self.columnSpan = columnSpan
    self.crossContentAlignment = crossContentAlignment ?? .value(.start)
    self.crossSpacing = crossSpacing
    self.defaultItem = defaultItem ?? .value(0)
    self.extensions = extensions
    self.focus = focus
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.id = id
    self.itemSpacing = itemSpacing ?? .value(8)
    self.items = items
    self.margins = margins ?? DivEdgeInsets()
    self.orientation = orientation ?? .value(.horizontal)
    self.paddings = paddings ?? DivEdgeInsets()
    self.restrictParentScroll = restrictParentScroll ?? .value(false)
    self.rowSpan = rowSpan
    self.scrollMode = scrollMode ?? .value(.default)
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
extension DivGallery: Equatable {
  public static func ==(lhs: DivGallery, rhs: DivGallery) -> Bool {
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
      lhs.columnCount == rhs.columnCount,
      lhs.columnSpan == rhs.columnSpan,
      lhs.crossContentAlignment == rhs.crossContentAlignment
    else {
      return false
    }
    guard
      lhs.crossSpacing == rhs.crossSpacing,
      lhs.defaultItem == rhs.defaultItem,
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
      lhs.itemSpacing == rhs.itemSpacing,
      lhs.items == rhs.items,
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
      lhs.scrollMode == rhs.scrollMode,
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

extension DivGallery: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["accessibility"] = accessibility.toDictionary()
    result["alignment_horizontal"] = alignmentHorizontal?.toValidSerializationValue()
    result["alignment_vertical"] = alignmentVertical?.toValidSerializationValue()
    result["alpha"] = alpha.toValidSerializationValue()
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border.toDictionary()
    result["column_count"] = columnCount?.toValidSerializationValue()
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["cross_content_alignment"] = crossContentAlignment.toValidSerializationValue()
    result["cross_spacing"] = crossSpacing?.toValidSerializationValue()
    result["default_item"] = defaultItem.toValidSerializationValue()
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["height"] = height.toDictionary()
    result["id"] = id
    result["item_spacing"] = itemSpacing.toValidSerializationValue()
    result["items"] = items.map { $0.toDictionary() }
    result["margins"] = margins.toDictionary()
    result["orientation"] = orientation.toValidSerializationValue()
    result["paddings"] = paddings.toDictionary()
    result["restrict_parent_scroll"] = restrictParentScroll.toValidSerializationValue()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["scroll_mode"] = scrollMode.toValidSerializationValue()
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
