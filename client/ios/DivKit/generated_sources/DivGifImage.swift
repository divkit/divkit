// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivGifImage: DivBase {
  public static let type: String = "gif"
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
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let contentAlignmentHorizontal: Expression<DivAlignmentHorizontal> // default value: center
  public let contentAlignmentVertical: Expression<DivAlignmentVertical> // default value: center
  public let disappearActions: [DivDisappearAction]?
  public let doubletapActions: [DivAction]?
  public let extensions: [DivExtension]?
  public let focus: DivFocus?
  public let functions: [DivFunction]?
  public let gifUrl: Expression<URL>
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let hoverEndActions: [DivAction]?
  public let hoverStartActions: [DivAction]?
  public let id: String?
  public let layoutProvider: DivLayoutProvider?
  public let longtapActions: [DivAction]?
  public let margins: DivEdgeInsets?
  public let paddings: DivEdgeInsets?
  public let placeholderColor: Expression<Color> // default value: #14000000
  public let preloadRequired: Expression<Bool> // default value: false
  public let pressEndActions: [DivAction]?
  public let pressStartActions: [DivAction]?
  public let preview: Expression<String>?
  public let reuseId: Expression<String>?
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let scale: Expression<DivImageScale> // default value: fill
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

  public func resolveContentAlignmentHorizontal(_ resolver: ExpressionResolver) -> DivAlignmentHorizontal {
    resolver.resolveEnum(contentAlignmentHorizontal) ?? DivAlignmentHorizontal.center
  }

  public func resolveContentAlignmentVertical(_ resolver: ExpressionResolver) -> DivAlignmentVertical {
    resolver.resolveEnum(contentAlignmentVertical) ?? DivAlignmentVertical.center
  }

  public func resolveGifUrl(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveUrl(gifUrl)
  }

  public func resolvePlaceholderColor(_ resolver: ExpressionResolver) -> Color {
    resolver.resolveColor(placeholderColor) ?? Color.colorWithARGBHexCode(0x14000000)
  }

  public func resolvePreloadRequired(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(preloadRequired) ?? false
  }

  public func resolvePreview(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(preview)
  }

  public func resolveReuseId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(reuseId)
  }

  public func resolveRowSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(rowSpan)
  }

  public func resolveScale(_ resolver: ExpressionResolver) -> DivImageScale {
    resolver.resolveEnum(scale) ?? DivImageScale.fill
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
    accessibility: DivAccessibility? = nil,
    action: DivAction? = nil,
    actionAnimation: DivAnimation? = nil,
    actions: [DivAction]? = nil,
    alignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    alignmentVertical: Expression<DivAlignmentVertical>? = nil,
    alpha: Expression<Double>? = nil,
    animators: [DivAnimator]? = nil,
    aspect: DivAspect? = nil,
    background: [DivBackground]? = nil,
    border: DivBorder? = nil,
    columnSpan: Expression<Int>? = nil,
    contentAlignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    contentAlignmentVertical: Expression<DivAlignmentVertical>? = nil,
    disappearActions: [DivDisappearAction]? = nil,
    doubletapActions: [DivAction]? = nil,
    extensions: [DivExtension]? = nil,
    focus: DivFocus? = nil,
    functions: [DivFunction]? = nil,
    gifUrl: Expression<URL>,
    height: DivSize? = nil,
    hoverEndActions: [DivAction]? = nil,
    hoverStartActions: [DivAction]? = nil,
    id: String? = nil,
    layoutProvider: DivLayoutProvider? = nil,
    longtapActions: [DivAction]? = nil,
    margins: DivEdgeInsets? = nil,
    paddings: DivEdgeInsets? = nil,
    placeholderColor: Expression<Color>? = nil,
    preloadRequired: Expression<Bool>? = nil,
    pressEndActions: [DivAction]? = nil,
    pressStartActions: [DivAction]? = nil,
    preview: Expression<String>? = nil,
    reuseId: Expression<String>? = nil,
    rowSpan: Expression<Int>? = nil,
    scale: Expression<DivImageScale>? = nil,
    selectedActions: [DivAction]? = nil,
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
    self.columnSpan = columnSpan
    self.contentAlignmentHorizontal = contentAlignmentHorizontal ?? .value(.center)
    self.contentAlignmentVertical = contentAlignmentVertical ?? .value(.center)
    self.disappearActions = disappearActions
    self.doubletapActions = doubletapActions
    self.extensions = extensions
    self.focus = focus
    self.functions = functions
    self.gifUrl = gifUrl
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.hoverEndActions = hoverEndActions
    self.hoverStartActions = hoverStartActions
    self.id = id
    self.layoutProvider = layoutProvider
    self.longtapActions = longtapActions
    self.margins = margins
    self.paddings = paddings
    self.placeholderColor = placeholderColor ?? .value(Color.colorWithARGBHexCode(0x14000000))
    self.preloadRequired = preloadRequired ?? .value(false)
    self.pressEndActions = pressEndActions
    self.pressStartActions = pressStartActions
    self.preview = preview
    self.reuseId = reuseId
    self.rowSpan = rowSpan
    self.scale = scale ?? .value(.fill)
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
extension DivGifImage: Equatable {
  public static func ==(lhs: DivGifImage, rhs: DivGifImage) -> Bool {
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
      lhs.columnSpan == rhs.columnSpan
    else {
      return false
    }
    guard
      lhs.contentAlignmentHorizontal == rhs.contentAlignmentHorizontal,
      lhs.contentAlignmentVertical == rhs.contentAlignmentVertical,
      lhs.disappearActions == rhs.disappearActions
    else {
      return false
    }
    guard
      lhs.doubletapActions == rhs.doubletapActions,
      lhs.extensions == rhs.extensions,
      lhs.focus == rhs.focus
    else {
      return false
    }
    guard
      lhs.functions == rhs.functions,
      lhs.gifUrl == rhs.gifUrl,
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
      lhs.layoutProvider == rhs.layoutProvider,
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
      lhs.pressEndActions == rhs.pressEndActions,
      lhs.pressStartActions == rhs.pressStartActions,
      lhs.preview == rhs.preview
    else {
      return false
    }
    guard
      lhs.reuseId == rhs.reuseId,
      lhs.rowSpan == rhs.rowSpan,
      lhs.scale == rhs.scale
    else {
      return false
    }
    guard
      lhs.selectedActions == rhs.selectedActions,
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
      lhs.variableTriggers == rhs.variableTriggers,
      lhs.variables == rhs.variables
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

extension DivGifImage: Serializable {
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
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["content_alignment_horizontal"] = contentAlignmentHorizontal.toValidSerializationValue()
    result["content_alignment_vertical"] = contentAlignmentVertical.toValidSerializationValue()
    result["disappear_actions"] = disappearActions?.map { $0.toDictionary() }
    result["doubletap_actions"] = doubletapActions?.map { $0.toDictionary() }
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["functions"] = functions?.map { $0.toDictionary() }
    result["gif_url"] = gifUrl.toValidSerializationValue()
    result["height"] = height.toDictionary()
    result["hover_end_actions"] = hoverEndActions?.map { $0.toDictionary() }
    result["hover_start_actions"] = hoverStartActions?.map { $0.toDictionary() }
    result["id"] = id
    result["layout_provider"] = layoutProvider?.toDictionary()
    result["longtap_actions"] = longtapActions?.map { $0.toDictionary() }
    result["margins"] = margins?.toDictionary()
    result["paddings"] = paddings?.toDictionary()
    result["placeholder_color"] = placeholderColor.toValidSerializationValue()
    result["preload_required"] = preloadRequired.toValidSerializationValue()
    result["press_end_actions"] = pressEndActions?.map { $0.toDictionary() }
    result["press_start_actions"] = pressStartActions?.map { $0.toDictionary() }
    result["preview"] = preview?.toValidSerializationValue()
    result["reuse_id"] = reuseId?.toValidSerializationValue()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["scale"] = scale.toValidSerializationValue()
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
