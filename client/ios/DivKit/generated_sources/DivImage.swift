// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivImage: DivBase, Sendable {
  public static let type: String = "image"
  public let accessibility: DivAccessibility?
  public let action: DivAction?
  public let actionAnimation: DivAnimation // default value: DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
  public let actions: [DivAction]?
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let animators: [DivAnimator]?
  public let appearanceAnimation: DivFadeTransition?
  public let aspect: DivAspect?
  public let background: [DivBackground]?
  public let border: DivBorder?
  public let captureFocusOnAction: Expression<Bool> // default value: true
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let contentAlignmentHorizontal: Expression<DivAlignmentHorizontal> // default value: center
  public let contentAlignmentVertical: Expression<DivAlignmentVertical> // default value: center
  public let disappearActions: [DivDisappearAction]?
  public let doubletapActions: [DivAction]?
  public let extensions: [DivExtension]?
  public let filters: [DivFilter]?
  public let focus: DivFocus?
  public let functions: [DivFunction]?
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let highPriorityPreviewShow: Expression<Bool> // default value: false
  public let hoverEndActions: [DivAction]?
  public let hoverStartActions: [DivAction]?
  public let id: String?
  public let imageUrl: Expression<URL>
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
  public let tintColor: Expression<Color>?
  public let tintMode: Expression<DivBlendMode> // default value: source_in
  public let tooltips: [DivTooltip]?
  public let transform: DivTransform?
  public let transformations: [DivTransformation]?
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

  public func resolveCaptureFocusOnAction(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(captureFocusOnAction) ?? true
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

  public func resolveHighPriorityPreviewShow(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(highPriorityPreviewShow) ?? false
  }

  public func resolveImageUrl(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveUrl(imageUrl)
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

  public func resolveTintColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveColor(tintColor)
  }

  public func resolveTintMode(_ resolver: ExpressionResolver) -> DivBlendMode {
    resolver.resolveEnum(tintMode) ?? DivBlendMode.sourceIn
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

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      accessibility: try dictionary.getOptionalField("accessibility", transform: { (dict: [String: Any]) in try DivAccessibility(dictionary: dict, context: context) }),
      action: try dictionary.getOptionalField("action", transform: { (dict: [String: Any]) in try DivAction(dictionary: dict, context: context) }),
      actionAnimation: try dictionary.getOptionalField("action_animation", transform: { (dict: [String: Any]) in try DivAnimation(dictionary: dict, context: context) }),
      actions: try dictionary.getOptionalArray("actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      alignmentHorizontal: try dictionary.getOptionalExpressionField("alignment_horizontal", context: context),
      alignmentVertical: try dictionary.getOptionalExpressionField("alignment_vertical", context: context),
      alpha: try dictionary.getOptionalExpressionField("alpha", validator: Self.alphaValidator, context: context),
      animators: try dictionary.getOptionalArray("animators", transform: { (dict: [String: Any]) in try? DivAnimator(dictionary: dict, context: context) }),
      appearanceAnimation: try dictionary.getOptionalField("appearance_animation", transform: { (dict: [String: Any]) in try DivFadeTransition(dictionary: dict, context: context) }),
      aspect: try dictionary.getOptionalField("aspect", transform: { (dict: [String: Any]) in try DivAspect(dictionary: dict, context: context) }),
      background: try dictionary.getOptionalArray("background", transform: { (dict: [String: Any]) in try? DivBackground(dictionary: dict, context: context) }),
      border: try dictionary.getOptionalField("border", transform: { (dict: [String: Any]) in try DivBorder(dictionary: dict, context: context) }),
      captureFocusOnAction: try dictionary.getOptionalExpressionField("capture_focus_on_action", context: context),
      columnSpan: try dictionary.getOptionalExpressionField("column_span", validator: Self.columnSpanValidator, context: context),
      contentAlignmentHorizontal: try dictionary.getOptionalExpressionField("content_alignment_horizontal", context: context),
      contentAlignmentVertical: try dictionary.getOptionalExpressionField("content_alignment_vertical", context: context),
      disappearActions: try dictionary.getOptionalArray("disappear_actions", transform: { (dict: [String: Any]) in try? DivDisappearAction(dictionary: dict, context: context) }),
      doubletapActions: try dictionary.getOptionalArray("doubletap_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      extensions: try dictionary.getOptionalArray("extensions", transform: { (dict: [String: Any]) in try? DivExtension(dictionary: dict, context: context) }),
      filters: try dictionary.getOptionalArray("filters", transform: { (dict: [String: Any]) in try? DivFilter(dictionary: dict, context: context) }),
      focus: try dictionary.getOptionalField("focus", transform: { (dict: [String: Any]) in try DivFocus(dictionary: dict, context: context) }),
      functions: try dictionary.getOptionalArray("functions", transform: { (dict: [String: Any]) in try? DivFunction(dictionary: dict, context: context) }),
      height: try dictionary.getOptionalField("height", transform: { (dict: [String: Any]) in try DivSize(dictionary: dict, context: context) }),
      highPriorityPreviewShow: try dictionary.getOptionalExpressionField("high_priority_preview_show", context: context),
      hoverEndActions: try dictionary.getOptionalArray("hover_end_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      hoverStartActions: try dictionary.getOptionalArray("hover_start_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      id: try dictionary.getOptionalField("id", context: context),
      imageUrl: try dictionary.getExpressionField("image_url", transform: URL.makeFromNonEncodedString, context: context),
      layoutProvider: try dictionary.getOptionalField("layout_provider", transform: { (dict: [String: Any]) in try DivLayoutProvider(dictionary: dict, context: context) }),
      longtapActions: try dictionary.getOptionalArray("longtap_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      margins: try dictionary.getOptionalField("margins", transform: { (dict: [String: Any]) in try DivEdgeInsets(dictionary: dict, context: context) }),
      paddings: try dictionary.getOptionalField("paddings", transform: { (dict: [String: Any]) in try DivEdgeInsets(dictionary: dict, context: context) }),
      placeholderColor: try dictionary.getOptionalExpressionField("placeholder_color", transform: Color.color(withHexString:), context: context),
      preloadRequired: try dictionary.getOptionalExpressionField("preload_required", context: context),
      pressEndActions: try dictionary.getOptionalArray("press_end_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      pressStartActions: try dictionary.getOptionalArray("press_start_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      preview: try dictionary.getOptionalExpressionField("preview", context: context),
      reuseId: try dictionary.getOptionalExpressionField("reuse_id", context: context),
      rowSpan: try dictionary.getOptionalExpressionField("row_span", validator: Self.rowSpanValidator, context: context),
      scale: try dictionary.getOptionalExpressionField("scale", context: context),
      selectedActions: try dictionary.getOptionalArray("selected_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      tintColor: try dictionary.getOptionalExpressionField("tint_color", transform: Color.color(withHexString:), context: context),
      tintMode: try dictionary.getOptionalExpressionField("tint_mode", context: context),
      tooltips: try dictionary.getOptionalArray("tooltips", transform: { (dict: [String: Any]) in try? DivTooltip(dictionary: dict, context: context) }),
      transform: try dictionary.getOptionalField("transform", transform: { (dict: [String: Any]) in try DivTransform(dictionary: dict, context: context) }),
      transformations: try dictionary.getOptionalArray("transformations", transform: { (dict: [String: Any]) in try? DivTransformation(dictionary: dict, context: context) }),
      transitionChange: try dictionary.getOptionalField("transition_change", transform: { (dict: [String: Any]) in try DivChangeTransition(dictionary: dict, context: context) }),
      transitionIn: try dictionary.getOptionalField("transition_in", transform: { (dict: [String: Any]) in try DivAppearanceTransition(dictionary: dict, context: context) }),
      transitionOut: try dictionary.getOptionalField("transition_out", transform: { (dict: [String: Any]) in try DivAppearanceTransition(dictionary: dict, context: context) }),
      transitionTriggers: try dictionary.getOptionalArray("transition_triggers", validator: Self.transitionTriggersValidator, context: context),
      variableTriggers: try dictionary.getOptionalArray("variable_triggers", transform: { (dict: [String: Any]) in try? DivTrigger(dictionary: dict, context: context) }),
      variables: try dictionary.getOptionalArray("variables", transform: { (dict: [String: Any]) in try? DivVariable(dictionary: dict, context: context) }),
      visibility: try dictionary.getOptionalExpressionField("visibility", context: context),
      visibilityAction: try dictionary.getOptionalField("visibility_action", transform: { (dict: [String: Any]) in try DivVisibilityAction(dictionary: dict, context: context) }),
      visibilityActions: try dictionary.getOptionalArray("visibility_actions", transform: { (dict: [String: Any]) in try? DivVisibilityAction(dictionary: dict, context: context) }),
      width: try dictionary.getOptionalField("width", transform: { (dict: [String: Any]) in try DivSize(dictionary: dict, context: context) })
    )
  }

  init(
    accessibility: DivAccessibility? = nil,
    action: DivAction? = nil,
    actionAnimation: DivAnimation? = nil,
    actions: [DivAction]? = nil,
    alignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    alignmentVertical: Expression<DivAlignmentVertical>? = nil,
    alpha: Expression<Double>? = nil,
    animators: [DivAnimator]? = nil,
    appearanceAnimation: DivFadeTransition? = nil,
    aspect: DivAspect? = nil,
    background: [DivBackground]? = nil,
    border: DivBorder? = nil,
    captureFocusOnAction: Expression<Bool>? = nil,
    columnSpan: Expression<Int>? = nil,
    contentAlignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    contentAlignmentVertical: Expression<DivAlignmentVertical>? = nil,
    disappearActions: [DivDisappearAction]? = nil,
    doubletapActions: [DivAction]? = nil,
    extensions: [DivExtension]? = nil,
    filters: [DivFilter]? = nil,
    focus: DivFocus? = nil,
    functions: [DivFunction]? = nil,
    height: DivSize? = nil,
    highPriorityPreviewShow: Expression<Bool>? = nil,
    hoverEndActions: [DivAction]? = nil,
    hoverStartActions: [DivAction]? = nil,
    id: String? = nil,
    imageUrl: Expression<URL>,
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
    tintColor: Expression<Color>? = nil,
    tintMode: Expression<DivBlendMode>? = nil,
    tooltips: [DivTooltip]? = nil,
    transform: DivTransform? = nil,
    transformations: [DivTransformation]? = nil,
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
    self.appearanceAnimation = appearanceAnimation
    self.aspect = aspect
    self.background = background
    self.border = border
    self.captureFocusOnAction = captureFocusOnAction ?? .value(true)
    self.columnSpan = columnSpan
    self.contentAlignmentHorizontal = contentAlignmentHorizontal ?? .value(.center)
    self.contentAlignmentVertical = contentAlignmentVertical ?? .value(.center)
    self.disappearActions = disappearActions
    self.doubletapActions = doubletapActions
    self.extensions = extensions
    self.filters = filters
    self.focus = focus
    self.functions = functions
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.highPriorityPreviewShow = highPriorityPreviewShow ?? .value(false)
    self.hoverEndActions = hoverEndActions
    self.hoverStartActions = hoverStartActions
    self.id = id
    self.imageUrl = imageUrl
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
    self.tintColor = tintColor
    self.tintMode = tintMode ?? .value(.sourceIn)
    self.tooltips = tooltips
    self.transform = transform
    self.transformations = transformations
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
      lhs.animators == rhs.animators,
      lhs.appearanceAnimation == rhs.appearanceAnimation
    else {
      return false
    }
    guard
      lhs.aspect == rhs.aspect,
      lhs.background == rhs.background,
      lhs.border == rhs.border
    else {
      return false
    }
    guard
      lhs.captureFocusOnAction == rhs.captureFocusOnAction,
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
      lhs.filters == rhs.filters,
      lhs.focus == rhs.focus
    else {
      return false
    }
    guard
      lhs.functions == rhs.functions,
      lhs.height == rhs.height,
      lhs.highPriorityPreviewShow == rhs.highPriorityPreviewShow
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
      lhs.imageUrl == rhs.imageUrl,
      lhs.layoutProvider == rhs.layoutProvider,
      lhs.longtapActions == rhs.longtapActions
    else {
      return false
    }
    guard
      lhs.margins == rhs.margins,
      lhs.paddings == rhs.paddings,
      lhs.placeholderColor == rhs.placeholderColor
    else {
      return false
    }
    guard
      lhs.preloadRequired == rhs.preloadRequired,
      lhs.pressEndActions == rhs.pressEndActions,
      lhs.pressStartActions == rhs.pressStartActions
    else {
      return false
    }
    guard
      lhs.preview == rhs.preview,
      lhs.reuseId == rhs.reuseId,
      lhs.rowSpan == rhs.rowSpan
    else {
      return false
    }
    guard
      lhs.scale == rhs.scale,
      lhs.selectedActions == rhs.selectedActions,
      lhs.tintColor == rhs.tintColor
    else {
      return false
    }
    guard
      lhs.tintMode == rhs.tintMode,
      lhs.tooltips == rhs.tooltips,
      lhs.transform == rhs.transform
    else {
      return false
    }
    guard
      lhs.transformations == rhs.transformations,
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

extension DivImage: Serializable {
  @_optimize(size)
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
    result["appearance_animation"] = appearanceAnimation?.toDictionary()
    result["aspect"] = aspect?.toDictionary()
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border?.toDictionary()
    result["capture_focus_on_action"] = captureFocusOnAction.toValidSerializationValue()
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["content_alignment_horizontal"] = contentAlignmentHorizontal.toValidSerializationValue()
    result["content_alignment_vertical"] = contentAlignmentVertical.toValidSerializationValue()
    result["disappear_actions"] = disappearActions?.map { $0.toDictionary() }
    result["doubletap_actions"] = doubletapActions?.map { $0.toDictionary() }
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["filters"] = filters?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["functions"] = functions?.map { $0.toDictionary() }
    result["height"] = height.toDictionary()
    result["high_priority_preview_show"] = highPriorityPreviewShow.toValidSerializationValue()
    result["hover_end_actions"] = hoverEndActions?.map { $0.toDictionary() }
    result["hover_start_actions"] = hoverStartActions?.map { $0.toDictionary() }
    result["id"] = id
    result["image_url"] = imageUrl.toValidSerializationValue()
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
    result["tint_color"] = tintColor?.toValidSerializationValue()
    result["tint_mode"] = tintMode.toValidSerializationValue()
    result["tooltips"] = tooltips?.map { $0.toDictionary() }
    result["transform"] = transform?.toDictionary()
    result["transformations"] = transformations?.map { $0.toDictionary() }
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
