// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivIndicator: DivBase {
  @frozen
  public enum Animation: String, CaseIterable {
    case scale = "scale"
    case worm = "worm"
    case slider = "slider"
  }

  public static let type: String = "indicator"
  public let accessibility: DivAccessibility?
  public let activeItemColor: Expression<Color> // default value: #ffdc60
  public let activeItemSize: Expression<Double> // constraint: number > 0; default value: 1.3
  public let activeShape: DivRoundedRectangleShape?
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let animation: Expression<Animation> // default value: scale
  public let animators: [DivAnimator]?
  public let background: [DivBackground]?
  public let border: DivBorder?
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let disappearActions: [DivDisappearAction]?
  public let extensions: [DivExtension]?
  public let focus: DivFocus?
  public let functions: [DivFunction]?
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: String?
  public let inactiveItemColor: Expression<Color> // default value: #33919cb5
  public let inactiveMinimumShape: DivRoundedRectangleShape?
  public let inactiveShape: DivRoundedRectangleShape?
  public let itemsPlacement: DivIndicatorItemPlacement?
  public let layoutProvider: DivLayoutProvider?
  public let margins: DivEdgeInsets?
  public let minimumItemSize: Expression<Double> // constraint: number > 0; default value: 0.5
  public let paddings: DivEdgeInsets?
  public let pagerId: String?
  public let reuseId: Expression<String>?
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let selectedActions: [DivAction]?
  public let shape: DivShape // default value: .divRoundedRectangleShape(DivRoundedRectangleShape())
  public let spaceBetweenCenters: DivFixedSize // default value: DivFixedSize(value: .value(15))
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

  public func resolveReuseId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(reuseId)
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
    animators: [DivAnimator]? = nil,
    background: [DivBackground]? = nil,
    border: DivBorder? = nil,
    columnSpan: Expression<Int>? = nil,
    disappearActions: [DivDisappearAction]? = nil,
    extensions: [DivExtension]? = nil,
    focus: DivFocus? = nil,
    functions: [DivFunction]? = nil,
    height: DivSize? = nil,
    id: String? = nil,
    inactiveItemColor: Expression<Color>? = nil,
    inactiveMinimumShape: DivRoundedRectangleShape? = nil,
    inactiveShape: DivRoundedRectangleShape? = nil,
    itemsPlacement: DivIndicatorItemPlacement? = nil,
    layoutProvider: DivLayoutProvider? = nil,
    margins: DivEdgeInsets? = nil,
    minimumItemSize: Expression<Double>? = nil,
    paddings: DivEdgeInsets? = nil,
    pagerId: String? = nil,
    reuseId: Expression<String>? = nil,
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
    variableTriggers: [DivTrigger]? = nil,
    variables: [DivVariable]? = nil,
    visibility: Expression<DivVisibility>? = nil,
    visibilityAction: DivVisibilityAction? = nil,
    visibilityActions: [DivVisibilityAction]? = nil,
    width: DivSize? = nil
  ) {
    self.accessibility = accessibility
    self.activeItemColor = activeItemColor ?? .value(Color.colorWithARGBHexCode(0xFFFFDC60))
    self.activeItemSize = activeItemSize ?? .value(1.3)
    self.activeShape = activeShape
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha ?? .value(1.0)
    self.animation = animation ?? .value(.scale)
    self.animators = animators
    self.background = background
    self.border = border
    self.columnSpan = columnSpan
    self.disappearActions = disappearActions
    self.extensions = extensions
    self.focus = focus
    self.functions = functions
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.id = id
    self.inactiveItemColor = inactiveItemColor ?? .value(Color.colorWithARGBHexCode(0x33919CB5))
    self.inactiveMinimumShape = inactiveMinimumShape
    self.inactiveShape = inactiveShape
    self.itemsPlacement = itemsPlacement
    self.layoutProvider = layoutProvider
    self.margins = margins
    self.minimumItemSize = minimumItemSize ?? .value(0.5)
    self.paddings = paddings
    self.pagerId = pagerId
    self.reuseId = reuseId
    self.rowSpan = rowSpan
    self.selectedActions = selectedActions
    self.shape = shape ?? .divRoundedRectangleShape(DivRoundedRectangleShape())
    self.spaceBetweenCenters = spaceBetweenCenters ?? DivFixedSize(value: .value(15))
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
      lhs.animators == rhs.animators
    else {
      return false
    }
    guard
      lhs.background == rhs.background,
      lhs.border == rhs.border,
      lhs.columnSpan == rhs.columnSpan
    else {
      return false
    }
    guard
      lhs.disappearActions == rhs.disappearActions,
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
      lhs.inactiveItemColor == rhs.inactiveItemColor,
      lhs.inactiveMinimumShape == rhs.inactiveMinimumShape,
      lhs.inactiveShape == rhs.inactiveShape
    else {
      return false
    }
    guard
      lhs.itemsPlacement == rhs.itemsPlacement,
      lhs.layoutProvider == rhs.layoutProvider,
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
      lhs.reuseId == rhs.reuseId,
      lhs.rowSpan == rhs.rowSpan,
      lhs.selectedActions == rhs.selectedActions
    else {
      return false
    }
    guard
      lhs.shape == rhs.shape,
      lhs.spaceBetweenCenters == rhs.spaceBetweenCenters,
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
      lhs.variableTriggers == rhs.variableTriggers
    else {
      return false
    }
    guard
      lhs.variables == rhs.variables,
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
    result["accessibility"] = accessibility?.toDictionary()
    result["active_item_color"] = activeItemColor.toValidSerializationValue()
    result["active_item_size"] = activeItemSize.toValidSerializationValue()
    result["active_shape"] = activeShape?.toDictionary()
    result["alignment_horizontal"] = alignmentHorizontal?.toValidSerializationValue()
    result["alignment_vertical"] = alignmentVertical?.toValidSerializationValue()
    result["alpha"] = alpha.toValidSerializationValue()
    result["animation"] = animation.toValidSerializationValue()
    result["animators"] = animators?.map { $0.toDictionary() }
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border?.toDictionary()
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["disappear_actions"] = disappearActions?.map { $0.toDictionary() }
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["functions"] = functions?.map { $0.toDictionary() }
    result["height"] = height.toDictionary()
    result["id"] = id
    result["inactive_item_color"] = inactiveItemColor.toValidSerializationValue()
    result["inactive_minimum_shape"] = inactiveMinimumShape?.toDictionary()
    result["inactive_shape"] = inactiveShape?.toDictionary()
    result["items_placement"] = itemsPlacement?.toDictionary()
    result["layout_provider"] = layoutProvider?.toDictionary()
    result["margins"] = margins?.toDictionary()
    result["minimum_item_size"] = minimumItemSize.toValidSerializationValue()
    result["paddings"] = paddings?.toDictionary()
    result["pager_id"] = pagerId
    result["reuse_id"] = reuseId?.toValidSerializationValue()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
    result["shape"] = shape.toDictionary()
    result["space_between_centers"] = spaceBetweenCenters.toDictionary()
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
