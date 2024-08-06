// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivPager: DivBase {
  @frozen
  public enum Orientation: String, CaseIterable {
    case horizontal = "horizontal"
    case vertical = "vertical"
  }

  public static let type: String = "pager"
  public let accessibility: DivAccessibility?
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let background: [DivBackground]?
  public let border: DivBorder?
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let defaultItem: Expression<Int> // constraint: number >= 0; default value: 0
  public let disappearActions: [DivDisappearAction]?
  public let extensions: [DivExtension]?
  public let focus: DivFocus?
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: String?
  public let infiniteScroll: Expression<Bool> // default value: false
  public let itemBuilder: DivCollectionItemBuilder?
  public let itemSpacing: DivFixedSize // default value: DivFixedSize(value: .value(0))
  public let items: [Div]?
  public let layoutMode: DivPagerLayoutMode
  public let layoutProvider: DivLayoutProvider?
  public let margins: DivEdgeInsets?
  public let orientation: Expression<Orientation> // default value: horizontal
  public let paddings: DivEdgeInsets?
  public let pageTransformation: DivPageTransformation?
  public let restrictParentScroll: Expression<Bool> // default value: false
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

  public func resolveDefaultItem(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(defaultItem) ?? 0
  }

  public func resolveInfiniteScroll(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(infiniteScroll) ?? false
  }

  public func resolveOrientation(_ resolver: ExpressionResolver) -> Orientation {
    resolver.resolveEnum(orientation) ?? Orientation.horizontal
  }

  public func resolveRestrictParentScroll(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(restrictParentScroll) ?? false
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

  static let defaultItemValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let rowSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let transitionTriggersValidator: AnyArrayValueValidator<DivTransitionTrigger> =
    makeArrayValidator(minItems: 1)

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
    itemBuilder: DivCollectionItemBuilder?,
    itemSpacing: DivFixedSize?,
    items: [Div]?,
    layoutMode: DivPagerLayoutMode,
    layoutProvider: DivLayoutProvider?,
    margins: DivEdgeInsets?,
    orientation: Expression<Orientation>?,
    paddings: DivEdgeInsets?,
    pageTransformation: DivPageTransformation?,
    restrictParentScroll: Expression<Bool>?,
    reuseId: Expression<String>?,
    rowSpan: Expression<Int>?,
    selectedActions: [DivAction]?,
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
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha ?? .value(1.0)
    self.background = background
    self.border = border
    self.columnSpan = columnSpan
    self.defaultItem = defaultItem ?? .value(0)
    self.disappearActions = disappearActions
    self.extensions = extensions
    self.focus = focus
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.id = id
    self.infiniteScroll = infiniteScroll ?? .value(false)
    self.itemBuilder = itemBuilder
    self.itemSpacing = itemSpacing ?? DivFixedSize(value: .value(0))
    self.items = items
    self.layoutMode = layoutMode
    self.layoutProvider = layoutProvider
    self.margins = margins
    self.orientation = orientation ?? .value(.horizontal)
    self.paddings = paddings
    self.pageTransformation = pageTransformation
    self.restrictParentScroll = restrictParentScroll ?? .value(false)
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
      lhs.itemBuilder == rhs.itemBuilder
    else {
      return false
    }
    guard
      lhs.itemSpacing == rhs.itemSpacing,
      lhs.items == rhs.items,
      lhs.layoutMode == rhs.layoutMode
    else {
      return false
    }
    guard
      lhs.layoutProvider == rhs.layoutProvider,
      lhs.margins == rhs.margins,
      lhs.orientation == rhs.orientation
    else {
      return false
    }
    guard
      lhs.paddings == rhs.paddings,
      lhs.pageTransformation == rhs.pageTransformation,
      lhs.restrictParentScroll == rhs.restrictParentScroll
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

extension DivPager: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["accessibility"] = accessibility?.toDictionary()
    result["alignment_horizontal"] = alignmentHorizontal?.toValidSerializationValue()
    result["alignment_vertical"] = alignmentVertical?.toValidSerializationValue()
    result["alpha"] = alpha.toValidSerializationValue()
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border?.toDictionary()
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["default_item"] = defaultItem.toValidSerializationValue()
    result["disappear_actions"] = disappearActions?.map { $0.toDictionary() }
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["height"] = height.toDictionary()
    result["id"] = id
    result["infinite_scroll"] = infiniteScroll.toValidSerializationValue()
    result["item_builder"] = itemBuilder?.toDictionary()
    result["item_spacing"] = itemSpacing.toDictionary()
    result["items"] = items?.map { $0.toDictionary() }
    result["layout_mode"] = layoutMode.toDictionary()
    result["layout_provider"] = layoutProvider?.toDictionary()
    result["margins"] = margins?.toDictionary()
    result["orientation"] = orientation.toValidSerializationValue()
    result["paddings"] = paddings?.toDictionary()
    result["page_transformation"] = pageTransformation?.toDictionary()
    result["restrict_parent_scroll"] = restrictParentScroll.toValidSerializationValue()
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
