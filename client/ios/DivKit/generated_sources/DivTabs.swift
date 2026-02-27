// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTabs: DivBase, Sendable {
  public final class Item: Sendable {
    public let div: Div
    public let title: Expression<String>
    public let titleClickAction: DivAction?

    public func resolveTitle(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(title)
    }

    public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
      self.init(
        div: try dictionary.getField("div", transform: { (dict: [String: Any]) in try Div(dictionary: dict, context: context) }, context: context),
        title: try dictionary.getExpressionField("title", context: context),
        titleClickAction: try dictionary.getOptionalField("title_click_action", transform: { (dict: [String: Any]) in try DivAction(dictionary: dict, context: context) })
      )
    }

    init(
      div: Div,
      title: Expression<String>,
      titleClickAction: DivAction?
    ) {
      self.div = div
      self.title = title
      self.titleClickAction = titleClickAction
    }
  }

  public final class TabTitleDelimiter: Sendable {
    public let height: DivFixedSize // default value: DivFixedSize(value: .value(12))
    public let imageUrl: Expression<URL>
    public let width: DivFixedSize // default value: DivFixedSize(value: .value(12))

    public func resolveImageUrl(_ resolver: ExpressionResolver) -> URL? {
      resolver.resolveUrl(imageUrl)
    }

    public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
      self.init(
        height: try dictionary.getOptionalField("height", transform: { (dict: [String: Any]) in try DivFixedSize(dictionary: dict, context: context) }),
        imageUrl: try dictionary.getExpressionField("image_url", transform: URL.makeFromNonEncodedString, context: context),
        width: try dictionary.getOptionalField("width", transform: { (dict: [String: Any]) in try DivFixedSize(dictionary: dict, context: context) })
      )
    }

    init(
      height: DivFixedSize? = nil,
      imageUrl: Expression<URL>,
      width: DivFixedSize? = nil
    ) {
      self.height = height ?? DivFixedSize(value: .value(12))
      self.imageUrl = imageUrl
      self.width = width ?? DivFixedSize(value: .value(12))
    }
  }

  public final class TabTitleStyle: @unchecked Sendable {
    @frozen
    public enum AnimationType: String, CaseIterable, Sendable {
      case slide = "slide"
      case fade = "fade"
      case none = "none"
    }

    public let activeBackgroundColor: Expression<Color> // default value: #FFFFDC60
    public let activeFontVariationSettings: Expression<[String: Any]>?
    public let activeFontWeight: Expression<DivFontWeight>?
    public let activeTextColor: Expression<Color> // default value: #CC000000
    public let animationDuration: Expression<Int> // constraint: number >= 0; default value: 300
    public let animationType: Expression<AnimationType> // default value: slide
    public let cornerRadius: Expression<Int>? // constraint: number >= 0
    public let cornersRadius: DivCornersRadius?
    public let fontFamily: Expression<String>?
    public let fontSize: Expression<Int> // constraint: number >= 0; default value: 12
    public let fontSizeUnit: Expression<DivSizeUnit> // default value: sp
    public let fontWeight: Expression<DivFontWeight> // default value: regular
    public let inactiveBackgroundColor: Expression<Color>?
    public let inactiveFontVariationSettings: Expression<[String: Any]>?
    public let inactiveFontWeight: Expression<DivFontWeight>?
    public let inactiveTextColor: Expression<Color> // default value: #80000000
    public let itemSpacing: Expression<Int> // constraint: number >= 0; default value: 0
    public let letterSpacing: Expression<Double> // default value: 0
    public let lineHeight: Expression<Int>? // constraint: number >= 0
    public let paddings: DivEdgeInsets // default value: DivEdgeInsets(bottom: .value(6), left: .value(8), right: .value(8), top: .value(6))

    public func resolveActiveBackgroundColor(_ resolver: ExpressionResolver) -> Color {
      resolver.resolveColor(activeBackgroundColor) ?? Color.colorWithARGBHexCode(0xFFFFDC60)
    }

    public func resolveActiveFontVariationSettings(_ resolver: ExpressionResolver) -> [String: Any]? {
      resolver.resolveDict(activeFontVariationSettings)
    }

    public func resolveActiveFontWeight(_ resolver: ExpressionResolver) -> DivFontWeight? {
      resolver.resolveEnum(activeFontWeight)
    }

    public func resolveActiveTextColor(_ resolver: ExpressionResolver) -> Color {
      resolver.resolveColor(activeTextColor) ?? Color.colorWithARGBHexCode(0xCC000000)
    }

    public func resolveAnimationDuration(_ resolver: ExpressionResolver) -> Int {
      resolver.resolveNumeric(animationDuration) ?? 300
    }

    public func resolveAnimationType(_ resolver: ExpressionResolver) -> AnimationType {
      resolver.resolveEnum(animationType) ?? AnimationType.slide
    }

    public func resolveCornerRadius(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(cornerRadius)
    }

    public func resolveFontFamily(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(fontFamily)
    }

    public func resolveFontSize(_ resolver: ExpressionResolver) -> Int {
      resolver.resolveNumeric(fontSize) ?? 12
    }

    public func resolveFontSizeUnit(_ resolver: ExpressionResolver) -> DivSizeUnit {
      resolver.resolveEnum(fontSizeUnit) ?? DivSizeUnit.sp
    }

    public func resolveFontWeight(_ resolver: ExpressionResolver) -> DivFontWeight {
      resolver.resolveEnum(fontWeight) ?? DivFontWeight.regular
    }

    public func resolveInactiveBackgroundColor(_ resolver: ExpressionResolver) -> Color? {
      resolver.resolveColor(inactiveBackgroundColor)
    }

    public func resolveInactiveFontVariationSettings(_ resolver: ExpressionResolver) -> [String: Any]? {
      resolver.resolveDict(inactiveFontVariationSettings)
    }

    public func resolveInactiveFontWeight(_ resolver: ExpressionResolver) -> DivFontWeight? {
      resolver.resolveEnum(inactiveFontWeight)
    }

    public func resolveInactiveTextColor(_ resolver: ExpressionResolver) -> Color {
      resolver.resolveColor(inactiveTextColor) ?? Color.colorWithARGBHexCode(0x80000000)
    }

    public func resolveItemSpacing(_ resolver: ExpressionResolver) -> Int {
      resolver.resolveNumeric(itemSpacing) ?? 0
    }

    public func resolveLetterSpacing(_ resolver: ExpressionResolver) -> Double {
      resolver.resolveNumeric(letterSpacing) ?? 0
    }

    public func resolveLineHeight(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(lineHeight)
    }

    static let animationDurationValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let cornerRadiusValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let fontSizeValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let itemSpacingValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let lineHeightValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
      self.init(
        activeBackgroundColor: try dictionary.getOptionalExpressionField("active_background_color", transform: Color.color(withHexString:), context: context),
        activeFontVariationSettings: try dictionary.getOptionalExpressionField("active_font_variation_settings", context: context),
        activeFontWeight: try dictionary.getOptionalExpressionField("active_font_weight", context: context),
        activeTextColor: try dictionary.getOptionalExpressionField("active_text_color", transform: Color.color(withHexString:), context: context),
        animationDuration: try dictionary.getOptionalExpressionField("animation_duration", validator: Self.animationDurationValidator, context: context),
        animationType: try dictionary.getOptionalExpressionField("animation_type", context: context),
        cornerRadius: try dictionary.getOptionalExpressionField("corner_radius", validator: Self.cornerRadiusValidator, context: context),
        cornersRadius: try dictionary.getOptionalField("corners_radius", transform: { (dict: [String: Any]) in try DivCornersRadius(dictionary: dict, context: context) }),
        fontFamily: try dictionary.getOptionalExpressionField("font_family", context: context),
        fontSize: try dictionary.getOptionalExpressionField("font_size", validator: Self.fontSizeValidator, context: context),
        fontSizeUnit: try dictionary.getOptionalExpressionField("font_size_unit", context: context),
        fontWeight: try dictionary.getOptionalExpressionField("font_weight", context: context),
        inactiveBackgroundColor: try dictionary.getOptionalExpressionField("inactive_background_color", transform: Color.color(withHexString:), context: context),
        inactiveFontVariationSettings: try dictionary.getOptionalExpressionField("inactive_font_variation_settings", context: context),
        inactiveFontWeight: try dictionary.getOptionalExpressionField("inactive_font_weight", context: context),
        inactiveTextColor: try dictionary.getOptionalExpressionField("inactive_text_color", transform: Color.color(withHexString:), context: context),
        itemSpacing: try dictionary.getOptionalExpressionField("item_spacing", validator: Self.itemSpacingValidator, context: context),
        letterSpacing: try dictionary.getOptionalExpressionField("letter_spacing", context: context),
        lineHeight: try dictionary.getOptionalExpressionField("line_height", validator: Self.lineHeightValidator, context: context),
        paddings: try dictionary.getOptionalField("paddings", transform: { (dict: [String: Any]) in try DivEdgeInsets(dictionary: dict, context: context) })
      )
    }

    init(
      activeBackgroundColor: Expression<Color>? = nil,
      activeFontVariationSettings: Expression<[String: Any]>? = nil,
      activeFontWeight: Expression<DivFontWeight>? = nil,
      activeTextColor: Expression<Color>? = nil,
      animationDuration: Expression<Int>? = nil,
      animationType: Expression<AnimationType>? = nil,
      cornerRadius: Expression<Int>? = nil,
      cornersRadius: DivCornersRadius? = nil,
      fontFamily: Expression<String>? = nil,
      fontSize: Expression<Int>? = nil,
      fontSizeUnit: Expression<DivSizeUnit>? = nil,
      fontWeight: Expression<DivFontWeight>? = nil,
      inactiveBackgroundColor: Expression<Color>? = nil,
      inactiveFontVariationSettings: Expression<[String: Any]>? = nil,
      inactiveFontWeight: Expression<DivFontWeight>? = nil,
      inactiveTextColor: Expression<Color>? = nil,
      itemSpacing: Expression<Int>? = nil,
      letterSpacing: Expression<Double>? = nil,
      lineHeight: Expression<Int>? = nil,
      paddings: DivEdgeInsets? = nil
    ) {
      self.activeBackgroundColor = activeBackgroundColor ?? .value(Color.colorWithARGBHexCode(0xFFFFDC60))
      self.activeFontVariationSettings = activeFontVariationSettings
      self.activeFontWeight = activeFontWeight
      self.activeTextColor = activeTextColor ?? .value(Color.colorWithARGBHexCode(0xCC000000))
      self.animationDuration = animationDuration ?? .value(300)
      self.animationType = animationType ?? .value(.slide)
      self.cornerRadius = cornerRadius
      self.cornersRadius = cornersRadius
      self.fontFamily = fontFamily
      self.fontSize = fontSize ?? .value(12)
      self.fontSizeUnit = fontSizeUnit ?? .value(.sp)
      self.fontWeight = fontWeight ?? .value(.regular)
      self.inactiveBackgroundColor = inactiveBackgroundColor
      self.inactiveFontVariationSettings = inactiveFontVariationSettings
      self.inactiveFontWeight = inactiveFontWeight
      self.inactiveTextColor = inactiveTextColor ?? .value(Color.colorWithARGBHexCode(0x80000000))
      self.itemSpacing = itemSpacing ?? .value(0)
      self.letterSpacing = letterSpacing ?? .value(0)
      self.lineHeight = lineHeight
      self.paddings = paddings ?? DivEdgeInsets(bottom: .value(6), left: .value(8), right: .value(8), top: .value(6))
    }
  }

  public static let type: String = "tabs"
  public let accessibility: DivAccessibility?
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let animators: [DivAnimator]?
  public let background: [DivBackground]?
  public let border: DivBorder?
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let disappearActions: [DivDisappearAction]?
  public let dynamicHeight: Expression<Bool> // default value: false
  public let extensions: [DivExtension]?
  public let focus: DivFocus?
  public let functions: [DivFunction]?
  public let hasSeparator: Expression<Bool> // default value: false
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: String?
  public let items: [Item] // at least 1 elements
  public let layoutProvider: DivLayoutProvider?
  public let margins: DivEdgeInsets?
  public let paddings: DivEdgeInsets?
  public let restrictParentScroll: Expression<Bool> // default value: false
  public let reuseId: Expression<String>?
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let selectedActions: [DivAction]?
  public let selectedTab: Expression<Int> // constraint: number >= 0; default value: 0
  public let separatorColor: Expression<Color> // default value: #14000000
  public let separatorPaddings: DivEdgeInsets // default value: DivEdgeInsets(bottom: .value(0), left: .value(12), right: .value(12), top: .value(0))
  public let switchTabsByContentSwipeEnabled: Expression<Bool> // default value: true
  public let tabTitleDelimiter: TabTitleDelimiter?
  public let tabTitleStyle: TabTitleStyle?
  public let titlePaddings: DivEdgeInsets // default value: DivEdgeInsets(bottom: .value(8), left: .value(12), right: .value(12), top: .value(0))
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

  public func resolveColumnSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(columnSpan)
  }

  public func resolveDynamicHeight(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(dynamicHeight) ?? false
  }

  public func resolveHasSeparator(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(hasSeparator) ?? false
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

  public func resolveSelectedTab(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(selectedTab) ?? 0
  }

  public func resolveSeparatorColor(_ resolver: ExpressionResolver) -> Color {
    resolver.resolveColor(separatorColor) ?? Color.colorWithARGBHexCode(0x14000000)
  }

  public func resolveSwitchTabsByContentSwipeEnabled(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(switchTabsByContentSwipeEnabled) ?? true
  }

  public func resolveVisibility(_ resolver: ExpressionResolver) -> DivVisibility {
    resolver.resolveEnum(visibility) ?? DivVisibility.visible
  }

  static let alphaValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 && $0 <= 1.0 })

  static let columnSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let itemsValidator: AnyArrayValueValidator<DivTabs.Item> =
    makeArrayValidator(minItems: 1)

  static let rowSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let selectedTabValidator: AnyValueValidator<Int> =
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
      columnSpan: try dictionary.getOptionalExpressionField("column_span", validator: Self.columnSpanValidator, context: context),
      disappearActions: try dictionary.getOptionalArray("disappear_actions", transform: { (dict: [String: Any]) in try? DivDisappearAction(dictionary: dict, context: context) }),
      dynamicHeight: try dictionary.getOptionalExpressionField("dynamic_height", context: context),
      extensions: try dictionary.getOptionalArray("extensions", transform: { (dict: [String: Any]) in try? DivExtension(dictionary: dict, context: context) }),
      focus: try dictionary.getOptionalField("focus", transform: { (dict: [String: Any]) in try DivFocus(dictionary: dict, context: context) }),
      functions: try dictionary.getOptionalArray("functions", transform: { (dict: [String: Any]) in try? DivFunction(dictionary: dict, context: context) }),
      hasSeparator: try dictionary.getOptionalExpressionField("has_separator", context: context),
      height: try dictionary.getOptionalField("height", transform: { (dict: [String: Any]) in try DivSize(dictionary: dict, context: context) }),
      id: try dictionary.getOptionalField("id", context: context),
      items: try dictionary.getArray("items", transform: { (dict: [String: Any]) in try? DivTabs.Item(dictionary: dict, context: context) }, validator: Self.itemsValidator, context: context),
      layoutProvider: try dictionary.getOptionalField("layout_provider", transform: { (dict: [String: Any]) in try DivLayoutProvider(dictionary: dict, context: context) }),
      margins: try dictionary.getOptionalField("margins", transform: { (dict: [String: Any]) in try DivEdgeInsets(dictionary: dict, context: context) }),
      paddings: try dictionary.getOptionalField("paddings", transform: { (dict: [String: Any]) in try DivEdgeInsets(dictionary: dict, context: context) }),
      restrictParentScroll: try dictionary.getOptionalExpressionField("restrict_parent_scroll", context: context),
      reuseId: try dictionary.getOptionalExpressionField("reuse_id", context: context),
      rowSpan: try dictionary.getOptionalExpressionField("row_span", validator: Self.rowSpanValidator, context: context),
      selectedActions: try dictionary.getOptionalArray("selected_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      selectedTab: try dictionary.getOptionalExpressionField("selected_tab", validator: Self.selectedTabValidator, context: context),
      separatorColor: try dictionary.getOptionalExpressionField("separator_color", transform: Color.color(withHexString:), context: context),
      separatorPaddings: try dictionary.getOptionalField("separator_paddings", transform: { (dict: [String: Any]) in try DivEdgeInsets(dictionary: dict, context: context) }),
      switchTabsByContentSwipeEnabled: try dictionary.getOptionalExpressionField("switch_tabs_by_content_swipe_enabled", context: context),
      tabTitleDelimiter: try dictionary.getOptionalField("tab_title_delimiter", transform: { (dict: [String: Any]) in try DivTabs.TabTitleDelimiter(dictionary: dict, context: context) }),
      tabTitleStyle: try dictionary.getOptionalField("tab_title_style", transform: { (dict: [String: Any]) in try DivTabs.TabTitleStyle(dictionary: dict, context: context) }),
      titlePaddings: try dictionary.getOptionalField("title_paddings", transform: { (dict: [String: Any]) in try DivEdgeInsets(dictionary: dict, context: context) }),
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
    columnSpan: Expression<Int>?,
    disappearActions: [DivDisappearAction]?,
    dynamicHeight: Expression<Bool>?,
    extensions: [DivExtension]?,
    focus: DivFocus?,
    functions: [DivFunction]?,
    hasSeparator: Expression<Bool>?,
    height: DivSize?,
    id: String?,
    items: [Item],
    layoutProvider: DivLayoutProvider?,
    margins: DivEdgeInsets?,
    paddings: DivEdgeInsets?,
    restrictParentScroll: Expression<Bool>?,
    reuseId: Expression<String>?,
    rowSpan: Expression<Int>?,
    selectedActions: [DivAction]?,
    selectedTab: Expression<Int>?,
    separatorColor: Expression<Color>?,
    separatorPaddings: DivEdgeInsets?,
    switchTabsByContentSwipeEnabled: Expression<Bool>?,
    tabTitleDelimiter: TabTitleDelimiter?,
    tabTitleStyle: TabTitleStyle?,
    titlePaddings: DivEdgeInsets?,
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
    self.columnSpan = columnSpan
    self.disappearActions = disappearActions
    self.dynamicHeight = dynamicHeight ?? .value(false)
    self.extensions = extensions
    self.focus = focus
    self.functions = functions
    self.hasSeparator = hasSeparator ?? .value(false)
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.id = id
    self.items = items
    self.layoutProvider = layoutProvider
    self.margins = margins
    self.paddings = paddings
    self.restrictParentScroll = restrictParentScroll ?? .value(false)
    self.reuseId = reuseId
    self.rowSpan = rowSpan
    self.selectedActions = selectedActions
    self.selectedTab = selectedTab ?? .value(0)
    self.separatorColor = separatorColor ?? .value(Color.colorWithARGBHexCode(0x14000000))
    self.separatorPaddings = separatorPaddings ?? DivEdgeInsets(bottom: .value(0), left: .value(12), right: .value(12), top: .value(0))
    self.switchTabsByContentSwipeEnabled = switchTabsByContentSwipeEnabled ?? .value(true)
    self.tabTitleDelimiter = tabTitleDelimiter
    self.tabTitleStyle = tabTitleStyle
    self.titlePaddings = titlePaddings ?? DivEdgeInsets(bottom: .value(8), left: .value(12), right: .value(12), top: .value(0))
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
extension DivTabs: Equatable {
  public static func ==(lhs: DivTabs, rhs: DivTabs) -> Bool {
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
      lhs.columnSpan == rhs.columnSpan,
      lhs.disappearActions == rhs.disappearActions
    else {
      return false
    }
    guard
      lhs.dynamicHeight == rhs.dynamicHeight,
      lhs.extensions == rhs.extensions,
      lhs.focus == rhs.focus
    else {
      return false
    }
    guard
      lhs.functions == rhs.functions,
      lhs.hasSeparator == rhs.hasSeparator,
      lhs.height == rhs.height
    else {
      return false
    }
    guard
      lhs.id == rhs.id,
      lhs.items == rhs.items,
      lhs.layoutProvider == rhs.layoutProvider
    else {
      return false
    }
    guard
      lhs.margins == rhs.margins,
      lhs.paddings == rhs.paddings,
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
      lhs.selectedTab == rhs.selectedTab,
      lhs.separatorColor == rhs.separatorColor,
      lhs.separatorPaddings == rhs.separatorPaddings
    else {
      return false
    }
    guard
      lhs.switchTabsByContentSwipeEnabled == rhs.switchTabsByContentSwipeEnabled,
      lhs.tabTitleDelimiter == rhs.tabTitleDelimiter,
      lhs.tabTitleStyle == rhs.tabTitleStyle
    else {
      return false
    }
    guard
      lhs.titlePaddings == rhs.titlePaddings,
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

extension DivTabs: Serializable {
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
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["disappear_actions"] = disappearActions?.map { $0.toDictionary() }
    result["dynamic_height"] = dynamicHeight.toValidSerializationValue()
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["functions"] = functions?.map { $0.toDictionary() }
    result["has_separator"] = hasSeparator.toValidSerializationValue()
    result["height"] = height.toDictionary()
    result["id"] = id
    result["items"] = items.map { $0.toDictionary() }
    result["layout_provider"] = layoutProvider?.toDictionary()
    result["margins"] = margins?.toDictionary()
    result["paddings"] = paddings?.toDictionary()
    result["restrict_parent_scroll"] = restrictParentScroll.toValidSerializationValue()
    result["reuse_id"] = reuseId?.toValidSerializationValue()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
    result["selected_tab"] = selectedTab.toValidSerializationValue()
    result["separator_color"] = separatorColor.toValidSerializationValue()
    result["separator_paddings"] = separatorPaddings.toDictionary()
    result["switch_tabs_by_content_swipe_enabled"] = switchTabsByContentSwipeEnabled.toValidSerializationValue()
    result["tab_title_delimiter"] = tabTitleDelimiter?.toDictionary()
    result["tab_title_style"] = tabTitleStyle?.toDictionary()
    result["title_paddings"] = titlePaddings.toDictionary()
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

#if DEBUG
// WARNING: this == is incomplete because of [String: Any] in class fields
extension DivTabs.TabTitleStyle: Equatable {
  public static func ==(lhs: DivTabs.TabTitleStyle, rhs: DivTabs.TabTitleStyle) -> Bool {
    guard
      lhs.activeBackgroundColor == rhs.activeBackgroundColor,
      lhs.activeFontWeight == rhs.activeFontWeight,
      lhs.activeTextColor == rhs.activeTextColor
    else {
      return false
    }
    guard
      lhs.animationDuration == rhs.animationDuration,
      lhs.animationType == rhs.animationType,
      lhs.cornerRadius == rhs.cornerRadius
    else {
      return false
    }
    guard
      lhs.cornersRadius == rhs.cornersRadius,
      lhs.fontFamily == rhs.fontFamily,
      lhs.fontSize == rhs.fontSize
    else {
      return false
    }
    guard
      lhs.fontSizeUnit == rhs.fontSizeUnit,
      lhs.fontWeight == rhs.fontWeight,
      lhs.inactiveBackgroundColor == rhs.inactiveBackgroundColor
    else {
      return false
    }
    guard
      lhs.inactiveFontWeight == rhs.inactiveFontWeight,
      lhs.inactiveTextColor == rhs.inactiveTextColor,
      lhs.itemSpacing == rhs.itemSpacing
    else {
      return false
    }
    guard
      lhs.letterSpacing == rhs.letterSpacing,
      lhs.lineHeight == rhs.lineHeight,
      lhs.paddings == rhs.paddings
    else {
      return false
    }
    return true
  }
}
#endif

#if DEBUG
extension DivTabs.Item: Equatable {
  public static func ==(lhs: DivTabs.Item, rhs: DivTabs.Item) -> Bool {
    guard
      lhs.div == rhs.div,
      lhs.title == rhs.title,
      lhs.titleClickAction == rhs.titleClickAction
    else {
      return false
    }
    return true
  }
}
#endif

#if DEBUG
extension DivTabs.TabTitleDelimiter: Equatable {
  public static func ==(lhs: DivTabs.TabTitleDelimiter, rhs: DivTabs.TabTitleDelimiter) -> Bool {
    guard
      lhs.height == rhs.height,
      lhs.imageUrl == rhs.imageUrl,
      lhs.width == rhs.width
    else {
      return false
    }
    return true
  }
}
#endif

extension DivTabs.Item: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["div"] = div.toDictionary()
    result["title"] = title.toValidSerializationValue()
    result["title_click_action"] = titleClickAction?.toDictionary()
    return result
  }
}

extension DivTabs.TabTitleDelimiter: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["height"] = height.toDictionary()
    result["image_url"] = imageUrl.toValidSerializationValue()
    result["width"] = width.toDictionary()
    return result
  }
}

extension DivTabs.TabTitleStyle: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["active_background_color"] = activeBackgroundColor.toValidSerializationValue()
    result["active_font_variation_settings"] = activeFontVariationSettings?.toValidSerializationValue()
    result["active_font_weight"] = activeFontWeight?.toValidSerializationValue()
    result["active_text_color"] = activeTextColor.toValidSerializationValue()
    result["animation_duration"] = animationDuration.toValidSerializationValue()
    result["animation_type"] = animationType.toValidSerializationValue()
    result["corner_radius"] = cornerRadius?.toValidSerializationValue()
    result["corners_radius"] = cornersRadius?.toDictionary()
    result["font_family"] = fontFamily?.toValidSerializationValue()
    result["font_size"] = fontSize.toValidSerializationValue()
    result["font_size_unit"] = fontSizeUnit.toValidSerializationValue()
    result["font_weight"] = fontWeight.toValidSerializationValue()
    result["inactive_background_color"] = inactiveBackgroundColor?.toValidSerializationValue()
    result["inactive_font_variation_settings"] = inactiveFontVariationSettings?.toValidSerializationValue()
    result["inactive_font_weight"] = inactiveFontWeight?.toValidSerializationValue()
    result["inactive_text_color"] = inactiveTextColor.toValidSerializationValue()
    result["item_spacing"] = itemSpacing.toValidSerializationValue()
    result["letter_spacing"] = letterSpacing.toValidSerializationValue()
    result["line_height"] = lineHeight?.toValidSerializationValue()
    result["paddings"] = paddings.toDictionary()
    return result
  }
}
