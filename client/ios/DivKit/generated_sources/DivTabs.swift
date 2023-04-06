// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivTabs: DivBase {
  public final class Item {
    public let div: Div
    public let title: Expression<String> // at least 1 char
    public let titleClickAction: DivAction?

    public func resolveTitle(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveStringBasedValue(expression: title, initializer: { $0 })
    }

    static let titleValidator: AnyValueValidator<String> =
      makeStringValidator(minLength: 1)

    static let titleClickActionValidator: AnyValueValidator<DivAction> =
      makeNoOpValueValidator()

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

  public final class TabTitleStyle {
    @frozen
    public enum AnimationType: String, CaseIterable {
      case slide = "slide"
      case fade = "fade"
      case none = "none"
    }

    public let activeBackgroundColor: Expression<Color> // default value: #FFFFDC60
    public let activeFontWeight: Expression<DivFontWeight>?
    public let activeTextColor: Expression<Color> // default value: #CC000000
    public let animationDuration: Expression<Int> // constraint: number >= 0; default value: 300
    public let animationType: Expression<AnimationType> // default value: slide
    public let cornerRadius: Expression<Int>? // constraint: number >= 0
    public let cornersRadius: DivCornersRadius?
    public let fontFamily: Expression<DivFontFamily> // default value: text
    public let fontSize: Expression<Int> // constraint: number >= 0; default value: 12
    public let fontSizeUnit: Expression<DivSizeUnit> // default value: sp
    public let fontWeight: Expression<DivFontWeight> // default value: regular
    public let inactiveBackgroundColor: Expression<Color>?
    public let inactiveFontWeight: Expression<DivFontWeight>?
    public let inactiveTextColor: Expression<Color> // default value: #80000000
    public let itemSpacing: Expression<Int> // constraint: number >= 0; default value: 0
    public let letterSpacing: Expression<Double> // default value: 0
    public let lineHeight: Expression<Int>? // constraint: number >= 0
    public let paddings: DivEdgeInsets // default value: DivEdgeInsets(bottom: .value(6), left: .value(8), right: .value(8), top: .value(6))

    public func resolveActiveBackgroundColor(_ resolver: ExpressionResolver) -> Color {
      resolver.resolveStringBasedValue(expression: activeBackgroundColor, initializer: Color.color(withHexString:)) ?? Color.colorWithARGBHexCode(0xFFFFDC60)
    }

    public func resolveActiveFontWeight(_ resolver: ExpressionResolver) -> DivFontWeight? {
      resolver.resolveStringBasedValue(expression: activeFontWeight, initializer: DivFontWeight.init(rawValue:))
    }

    public func resolveActiveTextColor(_ resolver: ExpressionResolver) -> Color {
      resolver.resolveStringBasedValue(expression: activeTextColor, initializer: Color.color(withHexString:)) ?? Color.colorWithARGBHexCode(0xCC000000)
    }

    public func resolveAnimationDuration(_ resolver: ExpressionResolver) -> Int {
      resolver.resolveNumericValue(expression: animationDuration) ?? 300
    }

    public func resolveAnimationType(_ resolver: ExpressionResolver) -> AnimationType {
      resolver.resolveStringBasedValue(expression: animationType, initializer: AnimationType.init(rawValue:)) ?? AnimationType.slide
    }

    public func resolveCornerRadius(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumericValue(expression: cornerRadius)
    }

    public func resolveFontFamily(_ resolver: ExpressionResolver) -> DivFontFamily {
      resolver.resolveStringBasedValue(expression: fontFamily, initializer: DivFontFamily.init(rawValue:)) ?? DivFontFamily.text
    }

    public func resolveFontSize(_ resolver: ExpressionResolver) -> Int {
      resolver.resolveNumericValue(expression: fontSize) ?? 12
    }

    public func resolveFontSizeUnit(_ resolver: ExpressionResolver) -> DivSizeUnit {
      resolver.resolveStringBasedValue(expression: fontSizeUnit, initializer: DivSizeUnit.init(rawValue:)) ?? DivSizeUnit.sp
    }

    public func resolveFontWeight(_ resolver: ExpressionResolver) -> DivFontWeight {
      resolver.resolveStringBasedValue(expression: fontWeight, initializer: DivFontWeight.init(rawValue:)) ?? DivFontWeight.regular
    }

    public func resolveInactiveBackgroundColor(_ resolver: ExpressionResolver) -> Color? {
      resolver.resolveStringBasedValue(expression: inactiveBackgroundColor, initializer: Color.color(withHexString:))
    }

    public func resolveInactiveFontWeight(_ resolver: ExpressionResolver) -> DivFontWeight? {
      resolver.resolveStringBasedValue(expression: inactiveFontWeight, initializer: DivFontWeight.init(rawValue:))
    }

    public func resolveInactiveTextColor(_ resolver: ExpressionResolver) -> Color {
      resolver.resolveStringBasedValue(expression: inactiveTextColor, initializer: Color.color(withHexString:)) ?? Color.colorWithARGBHexCode(0x80000000)
    }

    public func resolveItemSpacing(_ resolver: ExpressionResolver) -> Int {
      resolver.resolveNumericValue(expression: itemSpacing) ?? 0
    }

    public func resolveLetterSpacing(_ resolver: ExpressionResolver) -> Double {
      resolver.resolveNumericValue(expression: letterSpacing) ?? 0
    }

    public func resolveLineHeight(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumericValue(expression: lineHeight)
    }

    static let activeBackgroundColorValidator: AnyValueValidator<Color> =
      makeNoOpValueValidator()

    static let activeFontWeightValidator: AnyValueValidator<DivFontWeight> =
      makeNoOpValueValidator()

    static let activeTextColorValidator: AnyValueValidator<Color> =
      makeNoOpValueValidator()

    static let animationDurationValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let animationTypeValidator: AnyValueValidator<DivTabs.TabTitleStyle.AnimationType> =
      makeNoOpValueValidator()

    static let cornerRadiusValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let cornersRadiusValidator: AnyValueValidator<DivCornersRadius> =
      makeNoOpValueValidator()

    static let fontFamilyValidator: AnyValueValidator<DivFontFamily> =
      makeNoOpValueValidator()

    static let fontSizeValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let fontSizeUnitValidator: AnyValueValidator<DivSizeUnit> =
      makeNoOpValueValidator()

    static let fontWeightValidator: AnyValueValidator<DivFontWeight> =
      makeNoOpValueValidator()

    static let inactiveBackgroundColorValidator: AnyValueValidator<Color> =
      makeNoOpValueValidator()

    static let inactiveFontWeightValidator: AnyValueValidator<DivFontWeight> =
      makeNoOpValueValidator()

    static let inactiveTextColorValidator: AnyValueValidator<Color> =
      makeNoOpValueValidator()

    static let itemSpacingValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let lineHeightValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let paddingsValidator: AnyValueValidator<DivEdgeInsets> =
      makeNoOpValueValidator()

    init(
      activeBackgroundColor: Expression<Color>? = nil,
      activeFontWeight: Expression<DivFontWeight>? = nil,
      activeTextColor: Expression<Color>? = nil,
      animationDuration: Expression<Int>? = nil,
      animationType: Expression<AnimationType>? = nil,
      cornerRadius: Expression<Int>? = nil,
      cornersRadius: DivCornersRadius? = nil,
      fontFamily: Expression<DivFontFamily>? = nil,
      fontSize: Expression<Int>? = nil,
      fontSizeUnit: Expression<DivSizeUnit>? = nil,
      fontWeight: Expression<DivFontWeight>? = nil,
      inactiveBackgroundColor: Expression<Color>? = nil,
      inactiveFontWeight: Expression<DivFontWeight>? = nil,
      inactiveTextColor: Expression<Color>? = nil,
      itemSpacing: Expression<Int>? = nil,
      letterSpacing: Expression<Double>? = nil,
      lineHeight: Expression<Int>? = nil,
      paddings: DivEdgeInsets? = nil
    ) {
      self.activeBackgroundColor = activeBackgroundColor ?? .value(Color.colorWithARGBHexCode(0xFFFFDC60))
      self.activeFontWeight = activeFontWeight
      self.activeTextColor = activeTextColor ?? .value(Color.colorWithARGBHexCode(0xCC000000))
      self.animationDuration = animationDuration ?? .value(300)
      self.animationType = animationType ?? .value(.slide)
      self.cornerRadius = cornerRadius
      self.cornersRadius = cornersRadius
      self.fontFamily = fontFamily ?? .value(.text)
      self.fontSize = fontSize ?? .value(12)
      self.fontSizeUnit = fontSizeUnit ?? .value(.sp)
      self.fontWeight = fontWeight ?? .value(.regular)
      self.inactiveBackgroundColor = inactiveBackgroundColor
      self.inactiveFontWeight = inactiveFontWeight
      self.inactiveTextColor = inactiveTextColor ?? .value(Color.colorWithARGBHexCode(0x80000000))
      self.itemSpacing = itemSpacing ?? .value(0)
      self.letterSpacing = letterSpacing ?? .value(0)
      self.lineHeight = lineHeight
      self.paddings = paddings ?? DivEdgeInsets(bottom: .value(6), left: .value(8), right: .value(8), top: .value(6))
    }
  }

  public static let type: String = "tabs"
  public let accessibility: DivAccessibility
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let background: [DivBackground]? // at least 1 elements
  public let border: DivBorder
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let dynamicHeight: Expression<Bool> // default value: false
  public let extensions: [DivExtension]? // at least 1 elements
  public let focus: DivFocus?
  public let hasSeparator: Expression<Bool> // default value: false
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: String? // at least 1 char
  public let items: [Item] // at least 1 elements
  public let margins: DivEdgeInsets
  public let paddings: DivEdgeInsets
  public let restrictParentScroll: Expression<Bool> // default value: false
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let selectedActions: [DivAction]? // at least 1 elements
  public let selectedTab: Expression<Int> // constraint: number >= 0; default value: 0
  public let separatorColor: Expression<Color> // default value: #14000000
  public let separatorPaddings: DivEdgeInsets // default value: DivEdgeInsets(bottom: .value(0), left: .value(12), right: .value(12), top: .value(0))
  public let switchTabsByContentSwipeEnabled: Expression<Bool> // default value: true
  public let tabTitleStyle: TabTitleStyle
  public let titlePaddings: DivEdgeInsets // default value: DivEdgeInsets(bottom: .value(8), left: .value(12), right: .value(12), top: .value(0))
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

  public func resolveDynamicHeight(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumericValue(expression: dynamicHeight) ?? false
  }

  public func resolveHasSeparator(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumericValue(expression: hasSeparator) ?? false
  }

  public func resolveRestrictParentScroll(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumericValue(expression: restrictParentScroll) ?? false
  }

  public func resolveRowSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: rowSpan)
  }

  public func resolveSelectedTab(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: selectedTab) ?? 0
  }

  public func resolveSeparatorColor(_ resolver: ExpressionResolver) -> Color {
    resolver.resolveStringBasedValue(expression: separatorColor, initializer: Color.color(withHexString:)) ?? Color.colorWithARGBHexCode(0x14000000)
  }

  public func resolveSwitchTabsByContentSwipeEnabled(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumericValue(expression: switchTabsByContentSwipeEnabled) ?? true
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

  static let columnSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let dynamicHeightValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let extensionsValidator: AnyArrayValueValidator<DivExtension> =
    makeArrayValidator(minItems: 1)

  static let focusValidator: AnyValueValidator<DivFocus> =
    makeNoOpValueValidator()

  static let hasSeparatorValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let heightValidator: AnyValueValidator<DivSize> =
    makeNoOpValueValidator()

  static let idValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let itemsValidator: AnyArrayValueValidator<DivTabs.Item> =
    makeArrayValidator(minItems: 1)

  static let marginsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let paddingsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let restrictParentScrollValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let rowSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let selectedActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let selectedTabValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let separatorColorValidator: AnyValueValidator<Color> =
    makeNoOpValueValidator()

  static let separatorPaddingsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let switchTabsByContentSwipeEnabledValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let tabTitleStyleValidator: AnyValueValidator<DivTabs.TabTitleStyle> =
    makeNoOpValueValidator()

  static let titlePaddingsValidator: AnyValueValidator<DivEdgeInsets> =
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
    accessibility: DivAccessibility?,
    alignmentHorizontal: Expression<DivAlignmentHorizontal>?,
    alignmentVertical: Expression<DivAlignmentVertical>?,
    alpha: Expression<Double>?,
    background: [DivBackground]?,
    border: DivBorder?,
    columnSpan: Expression<Int>?,
    dynamicHeight: Expression<Bool>?,
    extensions: [DivExtension]?,
    focus: DivFocus?,
    hasSeparator: Expression<Bool>?,
    height: DivSize?,
    id: String?,
    items: [Item],
    margins: DivEdgeInsets?,
    paddings: DivEdgeInsets?,
    restrictParentScroll: Expression<Bool>?,
    rowSpan: Expression<Int>?,
    selectedActions: [DivAction]?,
    selectedTab: Expression<Int>?,
    separatorColor: Expression<Color>?,
    separatorPaddings: DivEdgeInsets?,
    switchTabsByContentSwipeEnabled: Expression<Bool>?,
    tabTitleStyle: TabTitleStyle?,
    titlePaddings: DivEdgeInsets?,
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
    self.columnSpan = columnSpan
    self.dynamicHeight = dynamicHeight ?? .value(false)
    self.extensions = extensions
    self.focus = focus
    self.hasSeparator = hasSeparator ?? .value(false)
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.id = id
    self.items = items
    self.margins = margins ?? DivEdgeInsets()
    self.paddings = paddings ?? DivEdgeInsets()
    self.restrictParentScroll = restrictParentScroll ?? .value(false)
    self.rowSpan = rowSpan
    self.selectedActions = selectedActions
    self.selectedTab = selectedTab ?? .value(0)
    self.separatorColor = separatorColor ?? .value(Color.colorWithARGBHexCode(0x14000000))
    self.separatorPaddings = separatorPaddings ?? DivEdgeInsets(bottom: .value(0), left: .value(12), right: .value(12), top: .value(0))
    self.switchTabsByContentSwipeEnabled = switchTabsByContentSwipeEnabled ?? .value(true)
    self.tabTitleStyle = tabTitleStyle ?? DivTabs.TabTitleStyle()
    self.titlePaddings = titlePaddings ?? DivEdgeInsets(bottom: .value(8), left: .value(12), right: .value(12), top: .value(0))
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
      lhs.background == rhs.background,
      lhs.border == rhs.border
    else {
      return false
    }
    guard
      lhs.columnSpan == rhs.columnSpan,
      lhs.dynamicHeight == rhs.dynamicHeight,
      lhs.extensions == rhs.extensions
    else {
      return false
    }
    guard
      lhs.focus == rhs.focus,
      lhs.hasSeparator == rhs.hasSeparator,
      lhs.height == rhs.height
    else {
      return false
    }
    guard
      lhs.id == rhs.id,
      lhs.items == rhs.items,
      lhs.margins == rhs.margins
    else {
      return false
    }
    guard
      lhs.paddings == rhs.paddings,
      lhs.restrictParentScroll == rhs.restrictParentScroll,
      lhs.rowSpan == rhs.rowSpan
    else {
      return false
    }
    guard
      lhs.selectedActions == rhs.selectedActions,
      lhs.selectedTab == rhs.selectedTab,
      lhs.separatorColor == rhs.separatorColor
    else {
      return false
    }
    guard
      lhs.separatorPaddings == rhs.separatorPaddings,
      lhs.switchTabsByContentSwipeEnabled == rhs.switchTabsByContentSwipeEnabled,
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

extension DivTabs: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["accessibility"] = accessibility.toDictionary()
    result["alignment_horizontal"] = alignmentHorizontal?.toValidSerializationValue()
    result["alignment_vertical"] = alignmentVertical?.toValidSerializationValue()
    result["alpha"] = alpha.toValidSerializationValue()
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border.toDictionary()
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["dynamic_height"] = dynamicHeight.toValidSerializationValue()
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["has_separator"] = hasSeparator.toValidSerializationValue()
    result["height"] = height.toDictionary()
    result["id"] = id
    result["items"] = items.map { $0.toDictionary() }
    result["margins"] = margins.toDictionary()
    result["paddings"] = paddings.toDictionary()
    result["restrict_parent_scroll"] = restrictParentScroll.toValidSerializationValue()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
    result["selected_tab"] = selectedTab.toValidSerializationValue()
    result["separator_color"] = separatorColor.toValidSerializationValue()
    result["separator_paddings"] = separatorPaddings.toDictionary()
    result["switch_tabs_by_content_swipe_enabled"] = switchTabsByContentSwipeEnabled.toValidSerializationValue()
    result["tab_title_style"] = tabTitleStyle.toDictionary()
    result["title_paddings"] = titlePaddings.toDictionary()
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

extension DivTabs.Item: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["div"] = div.toDictionary()
    result["title"] = title.toValidSerializationValue()
    result["title_click_action"] = titleClickAction?.toDictionary()
    return result
  }
}

extension DivTabs.TabTitleStyle: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["active_background_color"] = activeBackgroundColor.toValidSerializationValue()
    result["active_font_weight"] = activeFontWeight?.toValidSerializationValue()
    result["active_text_color"] = activeTextColor.toValidSerializationValue()
    result["animation_duration"] = animationDuration.toValidSerializationValue()
    result["animation_type"] = animationType.toValidSerializationValue()
    result["corner_radius"] = cornerRadius?.toValidSerializationValue()
    result["corners_radius"] = cornersRadius?.toDictionary()
    result["font_family"] = fontFamily.toValidSerializationValue()
    result["font_size"] = fontSize.toValidSerializationValue()
    result["font_size_unit"] = fontSizeUnit.toValidSerializationValue()
    result["font_weight"] = fontWeight.toValidSerializationValue()
    result["inactive_background_color"] = inactiveBackgroundColor?.toValidSerializationValue()
    result["inactive_font_weight"] = inactiveFontWeight?.toValidSerializationValue()
    result["inactive_text_color"] = inactiveTextColor.toValidSerializationValue()
    result["item_spacing"] = itemSpacing.toValidSerializationValue()
    result["letter_spacing"] = letterSpacing.toValidSerializationValue()
    result["line_height"] = lineHeight?.toValidSerializationValue()
    result["paddings"] = paddings.toDictionary()
    return result
  }
}
