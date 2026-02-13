// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivGallery: DivBase, Sendable {
  @frozen
  public enum CrossContentAlignment: String, CaseIterable, Sendable {
    case start = "start"
    case center = "center"
    case end = "end"
  }

  @frozen
  public enum Orientation: String, CaseIterable, Sendable {
    case horizontal = "horizontal"
    case vertical = "vertical"
  }

  @frozen
  public enum ScrollMode: String, CaseIterable, Sendable {
    case paging = "paging"
    case `default` = "default"
  }

  @frozen
  public enum Scrollbar: String, CaseIterable, Sendable {
    case none = "none"
    case auto = "auto"
  }

  public static let type: String = "gallery"
  public let accessibility: DivAccessibility?
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let animators: [DivAnimator]?
  public let background: [DivBackground]?
  public let border: DivBorder?
  public let columnCount: Expression<Int>? // constraint: number > 0
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let crossContentAlignment: Expression<CrossContentAlignment> // default value: start
  public let crossSpacing: Expression<Int>? // constraint: number >= 0
  public let defaultItem: Expression<Int> // constraint: number >= 0; default value: 0
  public let disappearActions: [DivDisappearAction]?
  public let extensions: [DivExtension]?
  public let focus: DivFocus?
  public let functions: [DivFunction]?
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: String?
  public let itemBuilder: DivCollectionItemBuilder?
  public let itemSpacing: Expression<Int> // constraint: number >= 0; default value: 8
  public let items: [Div]?
  public let layoutProvider: DivLayoutProvider?
  public let margins: DivEdgeInsets?
  public let orientation: Expression<Orientation> // default value: horizontal
  public let paddings: DivEdgeInsets?
  public let restrictParentScroll: Expression<Bool> // default value: false
  public let reuseId: Expression<String>?
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let scrollMode: Expression<ScrollMode> // default value: default
  public let scrollbar: Expression<Scrollbar> // default value: none
  public let selectedActions: [DivAction]?
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

  public func resolveColumnCount(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(columnCount)
  }

  public func resolveColumnSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(columnSpan)
  }

  public func resolveCrossContentAlignment(_ resolver: ExpressionResolver) -> CrossContentAlignment {
    resolver.resolveEnum(crossContentAlignment) ?? CrossContentAlignment.start
  }

  public func resolveCrossSpacing(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(crossSpacing)
  }

  public func resolveDefaultItem(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(defaultItem) ?? 0
  }

  public func resolveItemSpacing(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(itemSpacing) ?? 8
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

  public func resolveScrollMode(_ resolver: ExpressionResolver) -> ScrollMode {
    resolver.resolveEnum(scrollMode) ?? ScrollMode.default
  }

  public func resolveScrollbar(_ resolver: ExpressionResolver) -> Scrollbar {
    resolver.resolveEnum(scrollbar) ?? Scrollbar.none
  }

  public func resolveVisibility(_ resolver: ExpressionResolver) -> DivVisibility {
    resolver.resolveEnum(visibility) ?? DivVisibility.visible
  }

  static let alphaValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 && $0 <= 1.0 })

  static let columnCountValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 > 0 })

  static let columnSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let crossSpacingValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let defaultItemValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let itemSpacingValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let rowSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let transitionTriggersValidator: AnyArrayValueValidator<DivTransitionTrigger> =
    makeArrayValidator(minItems: 1)

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      accessibility: try dictionary.getOptionalField("accessibility", transform: { (dict: [String: Any]) in try DivAccessibility(dictionary: dict, context: context) }),
      alignmentHorizontal: try dictionary.getOptionalExpressionField("alignment_horizontal", context: context),
      alignmentVertical: try dictionary.getOptionalExpressionField("alignment_vertical", context: context),
      alpha: try dictionary.getOptionalExpressionField("alpha", validator: Self.alphaValidator, context: context),
      animators: try dictionary.getOptionalArray("animators", transform: { (dict: [String: Any]) in try? DivAnimator(dictionary: dict, context: context) }),
      background: try dictionary.getOptionalArray("background", transform: { (dict: [String: Any]) in try? DivBackground(dictionary: dict, context: context) }),
      border: try dictionary.getOptionalField("border", transform: { (dict: [String: Any]) in try DivBorder(dictionary: dict, context: context) }),
      columnCount: try dictionary.getOptionalExpressionField("column_count", validator: Self.columnCountValidator, context: context),
      columnSpan: try dictionary.getOptionalExpressionField("column_span", validator: Self.columnSpanValidator, context: context),
      crossContentAlignment: try dictionary.getOptionalExpressionField("cross_content_alignment", context: context),
      crossSpacing: try dictionary.getOptionalExpressionField("cross_spacing", validator: Self.crossSpacingValidator, context: context),
      defaultItem: try dictionary.getOptionalExpressionField("default_item", validator: Self.defaultItemValidator, context: context),
      disappearActions: try dictionary.getOptionalArray("disappear_actions", transform: { (dict: [String: Any]) in try? DivDisappearAction(dictionary: dict, context: context) }),
      extensions: try dictionary.getOptionalArray("extensions", transform: { (dict: [String: Any]) in try? DivExtension(dictionary: dict, context: context) }),
      focus: try dictionary.getOptionalField("focus", transform: { (dict: [String: Any]) in try DivFocus(dictionary: dict, context: context) }),
      functions: try dictionary.getOptionalArray("functions", transform: { (dict: [String: Any]) in try? DivFunction(dictionary: dict, context: context) }),
      height: try dictionary.getOptionalField("height", transform: { (dict: [String: Any]) in try DivSize(dictionary: dict, context: context) }),
      id: try dictionary.getOptionalField("id", context: context),
      itemBuilder: try dictionary.getOptionalField("item_builder", transform: { (dict: [String: Any]) in try DivCollectionItemBuilder(dictionary: dict, context: context) }),
      itemSpacing: try dictionary.getOptionalExpressionField("item_spacing", validator: Self.itemSpacingValidator, context: context),
      items: try dictionary.getOptionalArray("items", transform: { (dict: [String: Any]) in try? Div(dictionary: dict, context: context) }),
      layoutProvider: try dictionary.getOptionalField("layout_provider", transform: { (dict: [String: Any]) in try DivLayoutProvider(dictionary: dict, context: context) }),
      margins: try dictionary.getOptionalField("margins", transform: { (dict: [String: Any]) in try DivEdgeInsets(dictionary: dict, context: context) }),
      orientation: try dictionary.getOptionalExpressionField("orientation", context: context),
      paddings: try dictionary.getOptionalField("paddings", transform: { (dict: [String: Any]) in try DivEdgeInsets(dictionary: dict, context: context) }),
      restrictParentScroll: try dictionary.getOptionalExpressionField("restrict_parent_scroll", context: context),
      reuseId: try dictionary.getOptionalExpressionField("reuse_id", context: context),
      rowSpan: try dictionary.getOptionalExpressionField("row_span", validator: Self.rowSpanValidator, context: context),
      scrollMode: try dictionary.getOptionalExpressionField("scroll_mode", context: context),
      scrollbar: try dictionary.getOptionalExpressionField("scrollbar", context: context),
      selectedActions: try dictionary.getOptionalArray("selected_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
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
    accessibility: DivAccessibility?,
    alignmentHorizontal: Expression<DivAlignmentHorizontal>?,
    alignmentVertical: Expression<DivAlignmentVertical>?,
    alpha: Expression<Double>?,
    animators: [DivAnimator]?,
    background: [DivBackground]?,
    border: DivBorder?,
    columnCount: Expression<Int>?,
    columnSpan: Expression<Int>?,
    crossContentAlignment: Expression<CrossContentAlignment>?,
    crossSpacing: Expression<Int>?,
    defaultItem: Expression<Int>?,
    disappearActions: [DivDisappearAction]?,
    extensions: [DivExtension]?,
    focus: DivFocus?,
    functions: [DivFunction]?,
    height: DivSize?,
    id: String?,
    itemBuilder: DivCollectionItemBuilder?,
    itemSpacing: Expression<Int>?,
    items: [Div]?,
    layoutProvider: DivLayoutProvider?,
    margins: DivEdgeInsets?,
    orientation: Expression<Orientation>?,
    paddings: DivEdgeInsets?,
    restrictParentScroll: Expression<Bool>?,
    reuseId: Expression<String>?,
    rowSpan: Expression<Int>?,
    scrollMode: Expression<ScrollMode>?,
    scrollbar: Expression<Scrollbar>?,
    selectedActions: [DivAction]?,
    tooltips: [DivTooltip]?,
    transform: DivTransform?,
    transformations: [DivTransformation]?,
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
    self.animators = animators
    self.background = background
    self.border = border
    self.columnCount = columnCount
    self.columnSpan = columnSpan
    self.crossContentAlignment = crossContentAlignment ?? .value(.start)
    self.crossSpacing = crossSpacing
    self.defaultItem = defaultItem ?? .value(0)
    self.disappearActions = disappearActions
    self.extensions = extensions
    self.focus = focus
    self.functions = functions
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.id = id
    self.itemBuilder = itemBuilder
    self.itemSpacing = itemSpacing ?? .value(8)
    self.items = items
    self.layoutProvider = layoutProvider
    self.margins = margins
    self.orientation = orientation ?? .value(.horizontal)
    self.paddings = paddings
    self.restrictParentScroll = restrictParentScroll ?? .value(false)
    self.reuseId = reuseId
    self.rowSpan = rowSpan
    self.scrollMode = scrollMode ?? .value(.default)
    self.scrollbar = scrollbar ?? .value(.none)
    self.selectedActions = selectedActions
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
      lhs.animators == rhs.animators,
      lhs.background == rhs.background
    else {
      return false
    }
    guard
      lhs.border == rhs.border,
      lhs.columnCount == rhs.columnCount,
      lhs.columnSpan == rhs.columnSpan
    else {
      return false
    }
    guard
      lhs.crossContentAlignment == rhs.crossContentAlignment,
      lhs.crossSpacing == rhs.crossSpacing,
      lhs.defaultItem == rhs.defaultItem
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
      lhs.itemBuilder == rhs.itemBuilder,
      lhs.itemSpacing == rhs.itemSpacing,
      lhs.items == rhs.items
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
      lhs.restrictParentScroll == rhs.restrictParentScroll,
      lhs.reuseId == rhs.reuseId
    else {
      return false
    }
    guard
      lhs.rowSpan == rhs.rowSpan,
      lhs.scrollMode == rhs.scrollMode,
      lhs.scrollbar == rhs.scrollbar
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

extension DivGallery: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["accessibility"] = accessibility?.toDictionary()
    result["alignment_horizontal"] = alignmentHorizontal?.toValidSerializationValue()
    result["alignment_vertical"] = alignmentVertical?.toValidSerializationValue()
    result["alpha"] = alpha.toValidSerializationValue()
    result["animators"] = animators?.map { $0.toDictionary() }
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border?.toDictionary()
    result["column_count"] = columnCount?.toValidSerializationValue()
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["cross_content_alignment"] = crossContentAlignment.toValidSerializationValue()
    result["cross_spacing"] = crossSpacing?.toValidSerializationValue()
    result["default_item"] = defaultItem.toValidSerializationValue()
    result["disappear_actions"] = disappearActions?.map { $0.toDictionary() }
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["functions"] = functions?.map { $0.toDictionary() }
    result["height"] = height.toDictionary()
    result["id"] = id
    result["item_builder"] = itemBuilder?.toDictionary()
    result["item_spacing"] = itemSpacing.toValidSerializationValue()
    result["items"] = items?.map { $0.toDictionary() }
    result["layout_provider"] = layoutProvider?.toDictionary()
    result["margins"] = margins?.toDictionary()
    result["orientation"] = orientation.toValidSerializationValue()
    result["paddings"] = paddings?.toDictionary()
    result["restrict_parent_scroll"] = restrictParentScroll.toValidSerializationValue()
    result["reuse_id"] = reuseId?.toValidSerializationValue()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["scroll_mode"] = scrollMode.toValidSerializationValue()
    result["scrollbar"] = scrollbar.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
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
