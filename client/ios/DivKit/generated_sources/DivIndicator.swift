// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivIndicator: DivBase {
  @frozen
  public enum Animation: String, CaseIterable {
    case scale = "scale"
    case worm = "worm"
    case slider = "slider"
  }

  public static let type: String = "indicator"
  public let accessibility: DivAccessibility
  public let activeItemColor: Expression<Color> // default value: #ffdc60
  public let activeItemSize: Expression<Double> // constraint: number > 0; default value: 1.3
  public let activeShape: DivRoundedRectangleShape?
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let animation: Expression<Animation> // default value: scale
  public let background: [DivBackground]?
  public let border: DivBorder
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let disappearActions: [DivDisappearAction]?
  public let extensions: [DivExtension]?
  public let focus: DivFocus?
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: String?
  public let inactiveItemColor: Expression<Color> // default value: #33919cb5
  public let inactiveMinimumShape: DivRoundedRectangleShape?
  public let inactiveShape: DivRoundedRectangleShape?
  public let itemsPlacement: DivIndicatorItemPlacement?
  public let margins: DivEdgeInsets
  public let minimumItemSize: Expression<Double> // constraint: number > 0; default value: 0.5
  public let paddings: DivEdgeInsets
  public let pagerId: String?
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let selectedActions: [DivAction]?
  public let shape: DivShape // default value: .divRoundedRectangleShape(DivRoundedRectangleShape())
  public let spaceBetweenCenters: DivFixedSize // default value: DivFixedSize(value: .value(15))
  public let tooltips: [DivTooltip]?
  public let transform: DivTransform
  public let transitionChange: DivChangeTransition?
  public let transitionIn: DivAppearanceTransition?
  public let transitionOut: DivAppearanceTransition?
  public let transitionTriggers: [DivTransitionTrigger]? // at least 1 elements
  public let visibility: Expression<DivVisibility> // default value: visible
  public let visibilityAction: DivVisibilityAction?
  public let visibilityActions: [DivVisibilityAction]?
  public let width: DivSize // default value: .divMatchParentSize(DivMatchParentSize())

  public func resolveActiveItemColor(_ resolver: ExpressionResolver) -> Color {
    resolver.resolveColor(activeItemColor) ?? Color.colorWithARGBHexCode(0xFFFFDC60)
  }

  public func resolveActiveItemSize(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumeric(activeItemSize) ?? 1.3
  }

  public func resolveAlignmentHorizontal(_ resolver: ExpressionResolver) -> DivAlignmentHorizontal? {
    resolver.resolveEnum(alignmentHorizontal)
  }

  public func resolveAlignmentVertical(_ resolver: ExpressionResolver) -> DivAlignmentVertical? {
    resolver.resolveEnum(alignmentVertical)
  }

  public func resolveAlpha(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumeric(alpha) ?? 1.0
  }

  public func resolveAnimation(_ resolver: ExpressionResolver) -> Animation {
    resolver.resolveEnum(animation) ?? Animation.scale
  }

  public func resolveColumnSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(columnSpan)
  }

  public func resolveInactiveItemColor(_ resolver: ExpressionResolver) -> Color {
    resolver.resolveColor(inactiveItemColor) ?? Color.colorWithARGBHexCode(0x33919CB5)
  }

  public func resolveMinimumItemSize(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumeric(minimumItemSize) ?? 0.5
  }

  public func resolveRowSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(rowSpan)
  }

  public func resolveVisibility(_ resolver: ExpressionResolver) -> DivVisibility {
    resolver.resolveEnum(visibility) ?? DivVisibility.visible
  }

  static let activeItemSizeValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 > 0 })

  static let alphaValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 && $0 <= 1.0 })

  static let columnSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let minimumItemSizeValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 > 0 })

  static let rowSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let transitionTriggersValidator: AnyArrayValueValidator<DivTransitionTrigger> =
    makeArrayValidator(minItems: 1)

  init(
    accessibility: DivAccessibility? = nil,
    activeItemColor: Expression<Color>? = nil,
    activeItemSize: Expression<Double>? = nil,
    activeShape: DivRoundedRectangleShape? = nil,
    alignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    alignmentVertical: Expression<DivAlignmentVertical>? = nil,
    alpha: Expression<Double>? = nil,
    animation: Expression<Animation>? = nil,
    background: [DivBackground]? = nil,
    border: DivBorder? = nil,
    columnSpan: Expression<Int>? = nil,
    disappearActions: [DivDisappearAction]? = nil,
    extensions: [DivExtension]? = nil,
    focus: DivFocus? = nil,
    height: DivSize? = nil,
    id: String? = nil,
    inactiveItemColor: Expression<Color>? = nil,
    inactiveMinimumShape: DivRoundedRectangleShape? = nil,
    inactiveShape: DivRoundedRectangleShape? = nil,
    itemsPlacement: DivIndicatorItemPlacement? = nil,
    margins: DivEdgeInsets? = nil,
    minimumItemSize: Expression<Double>? = nil,
    paddings: DivEdgeInsets? = nil,
    pagerId: String? = nil,
    rowSpan: Expression<Int>? = nil,
    selectedActions: [DivAction]? = nil,
    shape: DivShape? = nil,
    spaceBetweenCenters: DivFixedSize? = nil,
    tooltips: [DivTooltip]? = nil,
    transform: DivTransform? = nil,
    transitionChange: DivChangeTransition? = nil,
    transitionIn: DivAppearanceTransition? = nil,
    transitionOut: DivAppearanceTransition? = nil,
    transitionTriggers: [DivTransitionTrigger]? = nil,
    visibility: Expression<DivVisibility>? = nil,
    visibilityAction: DivVisibilityAction? = nil,
    visibilityActions: [DivVisibilityAction]? = nil,
    width: DivSize? = nil
  ) {
    self.accessibility = accessibility ?? DivAccessibility()
    self.activeItemColor = activeItemColor ?? .value(Color.colorWithARGBHexCode(0xFFFFDC60))
    self.activeItemSize = activeItemSize ?? .value(1.3)
    self.activeShape = activeShape
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha ?? .value(1.0)
    self.animation = animation ?? .value(.scale)
    self.background = background
    self.border = border ?? DivBorder()
    self.columnSpan = columnSpan
    self.disappearActions = disappearActions
    self.extensions = extensions
    self.focus = focus
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.id = id
    self.inactiveItemColor = inactiveItemColor ?? .value(Color.colorWithARGBHexCode(0x33919CB5))
    self.inactiveMinimumShape = inactiveMinimumShape
    self.inactiveShape = inactiveShape
    self.itemsPlacement = itemsPlacement
    self.margins = margins ?? DivEdgeInsets()
    self.minimumItemSize = minimumItemSize ?? .value(0.5)
    self.paddings = paddings ?? DivEdgeInsets()
    self.pagerId = pagerId
    self.rowSpan = rowSpan
    self.selectedActions = selectedActions
    self.shape = shape ?? .divRoundedRectangleShape(DivRoundedRectangleShape())
    self.spaceBetweenCenters = spaceBetweenCenters ?? DivFixedSize(value: .value(15))
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
extension DivIndicator: Equatable {
  public static func ==(lhs: DivIndicator, rhs: DivIndicator) -> Bool {
    guard
      lhs.accessibility == rhs.accessibility,
      lhs.activeItemColor == rhs.activeItemColor,
      lhs.activeItemSize == rhs.activeItemSize
    else {
      return false
    }
    guard
      lhs.activeShape == rhs.activeShape,
      lhs.alignmentHorizontal == rhs.alignmentHorizontal,
      lhs.alignmentVertical == rhs.alignmentVertical
    else {
      return false
    }
    guard
      lhs.alpha == rhs.alpha,
      lhs.animation == rhs.animation,
      lhs.background == rhs.background
    else {
      return false
    }
    guard
      lhs.border == rhs.border,
      lhs.columnSpan == rhs.columnSpan,
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
      lhs.inactiveItemColor == rhs.inactiveItemColor,
      lhs.inactiveMinimumShape == rhs.inactiveMinimumShape
    else {
      return false
    }
    guard
      lhs.inactiveShape == rhs.inactiveShape,
      lhs.itemsPlacement == rhs.itemsPlacement,
      lhs.margins == rhs.margins
    else {
      return false
    }
    guard
      lhs.minimumItemSize == rhs.minimumItemSize,
      lhs.paddings == rhs.paddings,
      lhs.pagerId == rhs.pagerId
    else {
      return false
    }
    guard
      lhs.rowSpan == rhs.rowSpan,
      lhs.selectedActions == rhs.selectedActions,
      lhs.shape == rhs.shape
    else {
      return false
    }
    guard
      lhs.spaceBetweenCenters == rhs.spaceBetweenCenters,
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

extension DivIndicator: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["accessibility"] = accessibility.toDictionary()
    result["active_item_color"] = activeItemColor.toValidSerializationValue()
    result["active_item_size"] = activeItemSize.toValidSerializationValue()
    result["active_shape"] = activeShape?.toDictionary()
    result["alignment_horizontal"] = alignmentHorizontal?.toValidSerializationValue()
    result["alignment_vertical"] = alignmentVertical?.toValidSerializationValue()
    result["alpha"] = alpha.toValidSerializationValue()
    result["animation"] = animation.toValidSerializationValue()
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border.toDictionary()
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["disappear_actions"] = disappearActions?.map { $0.toDictionary() }
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["height"] = height.toDictionary()
    result["id"] = id
    result["inactive_item_color"] = inactiveItemColor.toValidSerializationValue()
    result["inactive_minimum_shape"] = inactiveMinimumShape?.toDictionary()
    result["inactive_shape"] = inactiveShape?.toDictionary()
    result["items_placement"] = itemsPlacement?.toDictionary()
    result["margins"] = margins.toDictionary()
    result["minimum_item_size"] = minimumItemSize.toValidSerializationValue()
    result["paddings"] = paddings.toDictionary()
    result["pager_id"] = pagerId
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
    result["shape"] = shape.toDictionary()
    result["space_between_centers"] = spaceBetweenCenters.toDictionary()
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
