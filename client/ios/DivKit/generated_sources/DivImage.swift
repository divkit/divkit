// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivImage: DivBase {
  public static let type: String = "image"
  public let accessibility: DivAccessibility
  public let action: DivAction?
  public let actionAnimation: DivAnimation // default value: DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
  public let actions: [DivAction]? // at least 1 elements
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let appearanceAnimation: DivFadeTransition?
  public let aspect: DivAspect?
  public let background: [DivBackground]? // at least 1 elements
  public let border: DivBorder
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let contentAlignmentHorizontal: Expression<DivAlignmentHorizontal> // default value: center
  public let contentAlignmentVertical: Expression<DivAlignmentVertical> // default value: center
  public let doubletapActions: [DivAction]? // at least 1 elements
  public let extensions: [DivExtension]? // at least 1 elements
  public let filters: [DivFilter]? // at least 1 elements
  public let focus: DivFocus?
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let highPriorityPreviewShow: Expression<Bool> // default value: false
  public let id: String? // at least 1 char
  public let imageUrl: Expression<URL>
  public let longtapActions: [DivAction]? // at least 1 elements
  public let margins: DivEdgeInsets
  public let paddings: DivEdgeInsets
  public let placeholderColor: Expression<Color> // default value: #14000000
  public let preloadRequired: Expression<Bool> // default value: false
  public let preview: Expression<String>? // at least 1 char
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let scale: Expression<DivImageScale> // default value: fill
  public let selectedActions: [DivAction]? // at least 1 elements
  public let tintColor: Expression<Color>?
  public let tintMode: Expression<DivBlendMode> // default value: source_in
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

  public func resolveContentAlignmentHorizontal(_ resolver: ExpressionResolver) -> DivAlignmentHorizontal {
    resolver.resolveStringBasedValue(expression: contentAlignmentHorizontal, initializer: DivAlignmentHorizontal.init(rawValue:)) ?? DivAlignmentHorizontal.center
  }

  public func resolveContentAlignmentVertical(_ resolver: ExpressionResolver) -> DivAlignmentVertical {
    resolver.resolveStringBasedValue(expression: contentAlignmentVertical, initializer: DivAlignmentVertical.init(rawValue:)) ?? DivAlignmentVertical.center
  }

  public func resolveHighPriorityPreviewShow(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumericValue(expression: highPriorityPreviewShow) ?? false
  }

  public func resolveImageUrl(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveStringBasedValue(expression: imageUrl, initializer: URL.init(string:))
  }

  public func resolvePlaceholderColor(_ resolver: ExpressionResolver) -> Color {
    resolver.resolveStringBasedValue(expression: placeholderColor, initializer: Color.color(withHexString:)) ?? Color.colorWithARGBHexCode(0x14000000)
  }

  public func resolvePreloadRequired(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumericValue(expression: preloadRequired) ?? false
  }

  public func resolvePreview(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveStringBasedValue(expression: preview, initializer: { $0 })
  }

  public func resolveRowSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: rowSpan)
  }

  public func resolveScale(_ resolver: ExpressionResolver) -> DivImageScale {
    resolver.resolveStringBasedValue(expression: scale, initializer: DivImageScale.init(rawValue:)) ?? DivImageScale.fill
  }

  public func resolveTintColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveStringBasedValue(expression: tintColor, initializer: Color.color(withHexString:))
  }

  public func resolveTintMode(_ resolver: ExpressionResolver) -> DivBlendMode {
    resolver.resolveStringBasedValue(expression: tintMode, initializer: DivBlendMode.init(rawValue:)) ?? DivBlendMode.sourceIn
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

  static let appearanceAnimationValidator: AnyValueValidator<DivFadeTransition> =
    makeNoOpValueValidator()

  static let aspectValidator: AnyValueValidator<DivAspect> =
    makeNoOpValueValidator()

  static let backgroundValidator: AnyArrayValueValidator<DivBackground> =
    makeArrayValidator(minItems: 1)

  static let borderValidator: AnyValueValidator<DivBorder> =
    makeNoOpValueValidator()

  static let columnSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let contentAlignmentHorizontalValidator: AnyValueValidator<DivAlignmentHorizontal> =
    makeNoOpValueValidator()

  static let contentAlignmentVerticalValidator: AnyValueValidator<DivAlignmentVertical> =
    makeNoOpValueValidator()

  static let doubletapActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let extensionsValidator: AnyArrayValueValidator<DivExtension> =
    makeArrayValidator(minItems: 1)

  static let filtersValidator: AnyArrayValueValidator<DivFilter> =
    makeArrayValidator(minItems: 1)

  static let focusValidator: AnyValueValidator<DivFocus> =
    makeNoOpValueValidator()

  static let heightValidator: AnyValueValidator<DivSize> =
    makeNoOpValueValidator()

  static let highPriorityPreviewShowValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let idValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let longtapActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let marginsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let paddingsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let placeholderColorValidator: AnyValueValidator<Color> =
    makeNoOpValueValidator()

  static let preloadRequiredValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let previewValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let rowSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let scaleValidator: AnyValueValidator<DivImageScale> =
    makeNoOpValueValidator()

  static let selectedActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let tintColorValidator: AnyValueValidator<Color> =
    makeNoOpValueValidator()

  static let tintModeValidator: AnyValueValidator<DivBlendMode> =
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
    action: DivAction? = nil,
    actionAnimation: DivAnimation? = nil,
    actions: [DivAction]? = nil,
    alignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    alignmentVertical: Expression<DivAlignmentVertical>? = nil,
    alpha: Expression<Double>? = nil,
    appearanceAnimation: DivFadeTransition? = nil,
    aspect: DivAspect? = nil,
    background: [DivBackground]? = nil,
    border: DivBorder? = nil,
    columnSpan: Expression<Int>? = nil,
    contentAlignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    contentAlignmentVertical: Expression<DivAlignmentVertical>? = nil,
    doubletapActions: [DivAction]? = nil,
    extensions: [DivExtension]? = nil,
    filters: [DivFilter]? = nil,
    focus: DivFocus? = nil,
    height: DivSize? = nil,
    highPriorityPreviewShow: Expression<Bool>? = nil,
    id: String? = nil,
    imageUrl: Expression<URL>,
    longtapActions: [DivAction]? = nil,
    margins: DivEdgeInsets? = nil,
    paddings: DivEdgeInsets? = nil,
    placeholderColor: Expression<Color>? = nil,
    preloadRequired: Expression<Bool>? = nil,
    preview: Expression<String>? = nil,
    rowSpan: Expression<Int>? = nil,
    scale: Expression<DivImageScale>? = nil,
    selectedActions: [DivAction]? = nil,
    tintColor: Expression<Color>? = nil,
    tintMode: Expression<DivBlendMode>? = nil,
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
    self.action = action
    self.actionAnimation = actionAnimation ?? DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
    self.actions = actions
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha ?? .value(1.0)
    self.appearanceAnimation = appearanceAnimation
    self.aspect = aspect
    self.background = background
    self.border = border ?? DivBorder()
    self.columnSpan = columnSpan
    self.contentAlignmentHorizontal = contentAlignmentHorizontal ?? .value(.center)
    self.contentAlignmentVertical = contentAlignmentVertical ?? .value(.center)
    self.doubletapActions = doubletapActions
    self.extensions = extensions
    self.filters = filters
    self.focus = focus
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.highPriorityPreviewShow = highPriorityPreviewShow ?? .value(false)
    self.id = id
    self.imageUrl = imageUrl
    self.longtapActions = longtapActions
    self.margins = margins ?? DivEdgeInsets()
    self.paddings = paddings ?? DivEdgeInsets()
    self.placeholderColor = placeholderColor ?? .value(Color.colorWithARGBHexCode(0x14000000))
    self.preloadRequired = preloadRequired ?? .value(false)
    self.preview = preview
    self.rowSpan = rowSpan
    self.scale = scale ?? .value(.fill)
    self.selectedActions = selectedActions
    self.tintColor = tintColor
    self.tintMode = tintMode ?? .value(.sourceIn)
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
extension DivImage: Equatable {
  public static func ==(lhs: DivImage, rhs: DivImage) -> Bool {
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
      lhs.appearanceAnimation == rhs.appearanceAnimation,
      lhs.aspect == rhs.aspect
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
      lhs.contentAlignmentHorizontal == rhs.contentAlignmentHorizontal,
      lhs.contentAlignmentVertical == rhs.contentAlignmentVertical,
      lhs.doubletapActions == rhs.doubletapActions
    else {
      return false
    }
    guard
      lhs.extensions == rhs.extensions,
      lhs.filters == rhs.filters,
      lhs.focus == rhs.focus
    else {
      return false
    }
    guard
      lhs.height == rhs.height,
      lhs.highPriorityPreviewShow == rhs.highPriorityPreviewShow,
      lhs.id == rhs.id
    else {
      return false
    }
    guard
      lhs.imageUrl == rhs.imageUrl,
      lhs.longtapActions == rhs.longtapActions,
      lhs.margins == rhs.margins
    else {
      return false
    }
    guard
      lhs.paddings == rhs.paddings,
      lhs.placeholderColor == rhs.placeholderColor,
      lhs.preloadRequired == rhs.preloadRequired
    else {
      return false
    }
    guard
      lhs.preview == rhs.preview,
      lhs.rowSpan == rhs.rowSpan,
      lhs.scale == rhs.scale
    else {
      return false
    }
    guard
      lhs.selectedActions == rhs.selectedActions,
      lhs.tintColor == rhs.tintColor,
      lhs.tintMode == rhs.tintMode
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

extension DivImage: Serializable {
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
    result["appearance_animation"] = appearanceAnimation?.toDictionary()
    result["aspect"] = aspect?.toDictionary()
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border.toDictionary()
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["content_alignment_horizontal"] = contentAlignmentHorizontal.toValidSerializationValue()
    result["content_alignment_vertical"] = contentAlignmentVertical.toValidSerializationValue()
    result["doubletap_actions"] = doubletapActions?.map { $0.toDictionary() }
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["filters"] = filters?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["height"] = height.toDictionary()
    result["high_priority_preview_show"] = highPriorityPreviewShow.toValidSerializationValue()
    result["id"] = id
    result["image_url"] = imageUrl.toValidSerializationValue()
    result["longtap_actions"] = longtapActions?.map { $0.toDictionary() }
    result["margins"] = margins.toDictionary()
    result["paddings"] = paddings.toDictionary()
    result["placeholder_color"] = placeholderColor.toValidSerializationValue()
    result["preload_required"] = preloadRequired.toValidSerializationValue()
    result["preview"] = preview?.toValidSerializationValue()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["scale"] = scale.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
    result["tint_color"] = tintColor?.toValidSerializationValue()
    result["tint_mode"] = tintMode.toValidSerializationValue()
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
