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
  public let background: [DivBackground]? // at least 1 elements
  public let border: DivBorder
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let extensions: [DivExtension]? // at least 1 elements
  public let focus: DivFocus?
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: String? // at least 1 char
  public let inactiveItemColor: Expression<Color> // default value: #33919cb5
  public let inactiveMinimumShape: DivRoundedRectangleShape?
  public let inactiveShape: DivRoundedRectangleShape?
  public let itemsPlacement: DivIndicatorItemPlacement?
  public let margins: DivEdgeInsets
  public let minimumItemSize: Expression<Double> // constraint: number > 0; default value: 0.5
  public let paddings: DivEdgeInsets
  public let pagerId: String?
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let selectedActions: [DivAction]? // at least 1 elements
  public let shape: DivShape // default value: .divRoundedRectangleShape(DivRoundedRectangleShape())
  public let spaceBetweenCenters: DivFixedSize // default value: DivFixedSize(value: .value(15))
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

  public func resolveActiveItemColor(_ resolver: ExpressionResolver) -> Color {
    resolver.resolveStringBasedValue(expression: activeItemColor, initializer: Color.color(withHexString:)) ?? Color.colorWithARGBHexCode(0xFFFFDC60)
  }

  public func resolveActiveItemSize(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumericValue(expression: activeItemSize) ?? 1.3
  }

  public func resolveAlignmentHorizontal(_ resolver: ExpressionResolver) -> DivAlignmentHorizontal? {
    resolver.resolveStringBasedValue(expression: alignmentHorizontal, initializer: DivAlignmentHorizontal.init(rawValue:))
  }

  public func resolveAlignmentVertical(_ resolver: ExpressionResolver) -> DivAlignmentVertical? {
    resolver.resolveStringBasedValue(expression: alignmentVertical, initializer: DivAlignmentVertical.init(rawValue:))
  }

  public func resolveAlpha(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumericValue(expression: alpha) ?? 1.0
  }

  public func resolveAnimation(_ resolver: ExpressionResolver) -> Animation {
    resolver.resolveStringBasedValue(expression: animation, initializer: Animation.init(rawValue:)) ?? Animation.scale
  }

  public func resolveColumnSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: columnSpan)
  }

  public func resolveInactiveItemColor(_ resolver: ExpressionResolver) -> Color {
    resolver.resolveStringBasedValue(expression: inactiveItemColor, initializer: Color.color(withHexString:)) ?? Color.colorWithARGBHexCode(0x33919CB5)
  }

  public func resolveMinimumItemSize(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumericValue(expression: minimumItemSize) ?? 0.5
  }

  public func resolveRowSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: rowSpan)
  }

  public func resolveVisibility(_ resolver: ExpressionResolver) -> DivVisibility {
    resolver.resolveStringBasedValue(expression: visibility, initializer: DivVisibility.init(rawValue:)) ?? DivVisibility.visible
  }

  static let accessibilityValidator: AnyValueValidator<DivAccessibility> =
    makeNoOpValueValidator()

  static let activeItemColorValidator: AnyValueValidator<Color> =
    makeNoOpValueValidator()

  static let activeItemSizeValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 > 0 })

  static let activeShapeValidator: AnyValueValidator<DivRoundedRectangleShape> =
    makeNoOpValueValidator()

  static let alignmentHorizontalValidator: AnyValueValidator<DivAlignmentHorizontal> =
    makeNoOpValueValidator()

  static let alignmentVerticalValidator: AnyValueValidator<DivAlignmentVertical> =
    makeNoOpValueValidator()

  static let alphaValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 && $0 <= 1.0 })

  static let animationValidator: AnyValueValidator<DivIndicator.Animation> =
    makeNoOpValueValidator()

  static let backgroundValidator: AnyArrayValueValidator<DivBackground> =
    makeArrayValidator(minItems: 1)

  static let borderValidator: AnyValueValidator<DivBorder> =
    makeNoOpValueValidator()

  static let columnSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let extensionsValidator: AnyArrayValueValidator<DivExtension> =
    makeArrayValidator(minItems: 1)

  static let focusValidator: AnyValueValidator<DivFocus> =
    makeNoOpValueValidator()

  static let heightValidator: AnyValueValidator<DivSize> =
    makeNoOpValueValidator()

  static let idValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let inactiveItemColorValidator: AnyValueValidator<Color> =
    makeNoOpValueValidator()

  static let inactiveMinimumShapeValidator: AnyValueValidator<DivRoundedRectangleShape> =
    makeNoOpValueValidator()

  static let inactiveShapeValidator: AnyValueValidator<DivRoundedRectangleShape> =
    makeNoOpValueValidator()

  static let itemsPlacementValidator: AnyValueValidator<DivIndicatorItemPlacement> =
    makeNoOpValueValidator()

  static let marginsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let minimumItemSizeValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 > 0 })

  static let paddingsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let pagerIdValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let rowSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let selectedActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let shapeValidator: AnyValueValidator<DivShape> =
    makeNoOpValueValidator()

  static let spaceBetweenCentersValidator: AnyValueValidator<DivFixedSize> =
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
      lhs.inactiveItemColor == rhs.inactiveItemColor,
      lhs.inactiveMinimumShape == rhs.inactiveMinimumShape,
      lhs.inactiveShape == rhs.inactiveShape
    else {
      return false
    }
    guard
      lhs.itemsPlacement == rhs.itemsPlacement,
      lhs.margins == rhs.margins,
      lhs.minimumItemSize == rhs.minimumItemSize
    else {
      return false
    }
    guard
      lhs.paddings == rhs.paddings,
      lhs.pagerId == rhs.pagerId,
      lhs.rowSpan == rhs.rowSpan
    else {
      return false
    }
    guard
      lhs.selectedActions == rhs.selectedActions,
      lhs.shape == rhs.shape,
      lhs.spaceBetweenCenters == rhs.spaceBetweenCenters
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
