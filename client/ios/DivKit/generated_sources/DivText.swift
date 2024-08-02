// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivText: DivBase {
  public final class Ellipsis {
    public let actions: [DivAction]?
    public let images: [Image]?
    public let ranges: [Range]?
    public let text: Expression<String>

    public func resolveText(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(text)
    }

    init(
      actions: [DivAction]? = nil,
      images: [Image]? = nil,
      ranges: [Range]? = nil,
      text: Expression<String>
    ) {
      self.actions = actions
      self.images = images
      self.ranges = ranges
      self.text = text
    }
  }

  public final class Image {
    public let height: DivFixedSize // default value: DivFixedSize(value: .value(20))
    public let preloadRequired: Expression<Bool> // default value: false
    public let start: Expression<Int> // constraint: number >= 0
    public let tintColor: Expression<Color>?
    public let tintMode: Expression<DivBlendMode> // default value: source_in
    public let url: Expression<URL>
    public let width: DivFixedSize // default value: DivFixedSize(value: .value(20))

    public func resolvePreloadRequired(_ resolver: ExpressionResolver) -> Bool {
      resolver.resolveNumeric(preloadRequired) ?? false
    }

    public func resolveStart(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(start)
    }

    public func resolveTintColor(_ resolver: ExpressionResolver) -> Color? {
      resolver.resolveColor(tintColor)
    }

    public func resolveTintMode(_ resolver: ExpressionResolver) -> DivBlendMode {
      resolver.resolveEnum(tintMode) ?? DivBlendMode.sourceIn
    }

    public func resolveUrl(_ resolver: ExpressionResolver) -> URL? {
      resolver.resolveUrl(url)
    }

    static let startValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    init(
      height: DivFixedSize? = nil,
      preloadRequired: Expression<Bool>? = nil,
      start: Expression<Int>,
      tintColor: Expression<Color>? = nil,
      tintMode: Expression<DivBlendMode>? = nil,
      url: Expression<URL>,
      width: DivFixedSize? = nil
    ) {
      self.height = height ?? DivFixedSize(value: .value(20))
      self.preloadRequired = preloadRequired ?? .value(false)
      self.start = start
      self.tintColor = tintColor
      self.tintMode = tintMode ?? .value(.sourceIn)
      self.url = url
      self.width = width ?? DivFixedSize(value: .value(20))
    }
  }

  public final class Range {
    public let actions: [DivAction]?
    public let background: DivTextRangeBackground?
    public let border: DivTextRangeBorder?
    public let end: Expression<Int> // constraint: number > 0
    public let fontFamily: Expression<String>?
    public let fontFeatureSettings: Expression<String>?
    public let fontSize: Expression<Int>? // constraint: number >= 0
    public let fontSizeUnit: Expression<DivSizeUnit> // default value: sp
    public let fontWeight: Expression<DivFontWeight>?
    public let fontWeightValue: Expression<Int>? // constraint: number > 0
    public let letterSpacing: Expression<Double>?
    public let lineHeight: Expression<Int>? // constraint: number >= 0
    public let start: Expression<Int> // constraint: number >= 0
    public let strike: Expression<DivLineStyle>?
    public let textColor: Expression<Color>?
    public let textShadow: DivShadow?
    public let topOffset: Expression<Int>? // constraint: number >= 0
    public let underline: Expression<DivLineStyle>?

    public func resolveEnd(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(end)
    }

    public func resolveFontFamily(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(fontFamily)
    }

    public func resolveFontFeatureSettings(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(fontFeatureSettings)
    }

    public func resolveFontSize(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(fontSize)
    }

    public func resolveFontSizeUnit(_ resolver: ExpressionResolver) -> DivSizeUnit {
      resolver.resolveEnum(fontSizeUnit) ?? DivSizeUnit.sp
    }

    public func resolveFontWeight(_ resolver: ExpressionResolver) -> DivFontWeight? {
      resolver.resolveEnum(fontWeight)
    }

    public func resolveFontWeightValue(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(fontWeightValue)
    }

    public func resolveLetterSpacing(_ resolver: ExpressionResolver) -> Double? {
      resolver.resolveNumeric(letterSpacing)
    }

    public func resolveLineHeight(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(lineHeight)
    }

    public func resolveStart(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(start)
    }

    public func resolveStrike(_ resolver: ExpressionResolver) -> DivLineStyle? {
      resolver.resolveEnum(strike)
    }

    public func resolveTextColor(_ resolver: ExpressionResolver) -> Color? {
      resolver.resolveColor(textColor)
    }

    public func resolveTopOffset(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(topOffset)
    }

    public func resolveUnderline(_ resolver: ExpressionResolver) -> DivLineStyle? {
      resolver.resolveEnum(underline)
    }

    static let endValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 > 0 })

    static let fontSizeValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let fontWeightValueValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 > 0 })

    static let lineHeightValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let startValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let topOffsetValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    init(
      actions: [DivAction]? = nil,
      background: DivTextRangeBackground? = nil,
      border: DivTextRangeBorder? = nil,
      end: Expression<Int>,
      fontFamily: Expression<String>? = nil,
      fontFeatureSettings: Expression<String>? = nil,
      fontSize: Expression<Int>? = nil,
      fontSizeUnit: Expression<DivSizeUnit>? = nil,
      fontWeight: Expression<DivFontWeight>? = nil,
      fontWeightValue: Expression<Int>? = nil,
      letterSpacing: Expression<Double>? = nil,
      lineHeight: Expression<Int>? = nil,
      start: Expression<Int>,
      strike: Expression<DivLineStyle>? = nil,
      textColor: Expression<Color>? = nil,
      textShadow: DivShadow? = nil,
      topOffset: Expression<Int>? = nil,
      underline: Expression<DivLineStyle>? = nil
    ) {
      self.actions = actions
      self.background = background
      self.border = border
      self.end = end
      self.fontFamily = fontFamily
      self.fontFeatureSettings = fontFeatureSettings
      self.fontSize = fontSize
      self.fontSizeUnit = fontSizeUnit ?? .value(.sp)
      self.fontWeight = fontWeight
      self.fontWeightValue = fontWeightValue
      self.letterSpacing = letterSpacing
      self.lineHeight = lineHeight
      self.start = start
      self.strike = strike
      self.textColor = textColor
      self.textShadow = textShadow
      self.topOffset = topOffset
      self.underline = underline
    }
  }

  public static let type: String = "text"
  public let accessibility: DivAccessibility?
  public let action: DivAction?
  public let actionAnimation: DivAnimation // default value: DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
  public let actions: [DivAction]?
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let autoEllipsize: Expression<Bool>?
  public let background: [DivBackground]?
  public let border: DivBorder?
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let disappearActions: [DivDisappearAction]?
  public let doubletapActions: [DivAction]?
  public let ellipsis: Ellipsis?
  public let extensions: [DivExtension]?
  public let focus: DivFocus?
  public let focusedTextColor: Expression<Color>?
  public let fontFamily: Expression<String>?
  public let fontFeatureSettings: Expression<String>?
  public let fontSize: Expression<Int> // constraint: number >= 0; default value: 12
  public let fontSizeUnit: Expression<DivSizeUnit> // default value: sp
  public let fontWeight: Expression<DivFontWeight> // default value: regular
  public let fontWeightValue: Expression<Int>? // constraint: number > 0
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: String?
  public let images: [Image]?
  public let layoutProvider: DivLayoutProvider?
  public let letterSpacing: Expression<Double> // default value: 0
  public let lineHeight: Expression<Int>? // constraint: number >= 0
  public let longtapActions: [DivAction]?
  public let margins: DivEdgeInsets?
  public let maxLines: Expression<Int>? // constraint: number >= 0
  public let minHiddenLines: Expression<Int>? // constraint: number >= 0
  public let paddings: DivEdgeInsets?
  public let ranges: [Range]?
  public let reuseId: Expression<String>?
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let selectable: Expression<Bool> // default value: false
  public let selectedActions: [DivAction]?
  public let strike: Expression<DivLineStyle> // default value: none
  public let text: Expression<String>
  public let textAlignmentHorizontal: Expression<DivAlignmentHorizontal> // default value: start
  public let textAlignmentVertical: Expression<DivAlignmentVertical> // default value: top
  public let textColor: Expression<Color> // default value: #FF000000
  public let textGradient: DivTextGradient?
  public let textShadow: DivShadow?
  public let tooltips: [DivTooltip]?
  public let transform: DivTransform?
  public let transitionChange: DivChangeTransition?
  public let transitionIn: DivAppearanceTransition?
  public let transitionOut: DivAppearanceTransition?
  public let transitionTriggers: [DivTransitionTrigger]? // at least 1 elements
  public let underline: Expression<DivLineStyle> // default value: none
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

  public func resolveAutoEllipsize(_ resolver: ExpressionResolver) -> Bool? {
    resolver.resolveNumeric(autoEllipsize)
  }

  public func resolveColumnSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(columnSpan)
  }

  public func resolveFocusedTextColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveColor(focusedTextColor)
  }

  public func resolveFontFamily(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(fontFamily)
  }

  public func resolveFontFeatureSettings(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(fontFeatureSettings)
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

  public func resolveFontWeightValue(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(fontWeightValue)
  }

  public func resolveLetterSpacing(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumeric(letterSpacing) ?? 0
  }

  public func resolveLineHeight(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(lineHeight)
  }

  public func resolveMaxLines(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(maxLines)
  }

  public func resolveMinHiddenLines(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(minHiddenLines)
  }

  public func resolveReuseId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(reuseId)
  }

  public func resolveRowSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(rowSpan)
  }

  public func resolveSelectable(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(selectable) ?? false
  }

  public func resolveStrike(_ resolver: ExpressionResolver) -> DivLineStyle {
    resolver.resolveEnum(strike) ?? DivLineStyle.none
  }

  public func resolveText(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(text)
  }

  public func resolveTextAlignmentHorizontal(_ resolver: ExpressionResolver) -> DivAlignmentHorizontal {
    resolver.resolveEnum(textAlignmentHorizontal) ?? DivAlignmentHorizontal.start
  }

  public func resolveTextAlignmentVertical(_ resolver: ExpressionResolver) -> DivAlignmentVertical {
    resolver.resolveEnum(textAlignmentVertical) ?? DivAlignmentVertical.top
  }

  public func resolveTextColor(_ resolver: ExpressionResolver) -> Color {
    resolver.resolveColor(textColor) ?? Color.colorWithARGBHexCode(0xFF000000)
  }

  public func resolveUnderline(_ resolver: ExpressionResolver) -> DivLineStyle {
    resolver.resolveEnum(underline) ?? DivLineStyle.none
  }

  public func resolveVisibility(_ resolver: ExpressionResolver) -> DivVisibility {
    resolver.resolveEnum(visibility) ?? DivVisibility.visible
  }

  static let alphaValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 && $0 <= 1.0 })

  static let columnSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let fontSizeValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let fontWeightValueValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 > 0 })

  static let lineHeightValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let maxLinesValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let minHiddenLinesValidator: AnyValueValidator<Int> =
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
    autoEllipsize: Expression<Bool>? = nil,
    background: [DivBackground]? = nil,
    border: DivBorder? = nil,
    columnSpan: Expression<Int>? = nil,
    disappearActions: [DivDisappearAction]? = nil,
    doubletapActions: [DivAction]? = nil,
    ellipsis: Ellipsis? = nil,
    extensions: [DivExtension]? = nil,
    focus: DivFocus? = nil,
    focusedTextColor: Expression<Color>? = nil,
    fontFamily: Expression<String>? = nil,
    fontFeatureSettings: Expression<String>? = nil,
    fontSize: Expression<Int>? = nil,
    fontSizeUnit: Expression<DivSizeUnit>? = nil,
    fontWeight: Expression<DivFontWeight>? = nil,
    fontWeightValue: Expression<Int>? = nil,
    height: DivSize? = nil,
    id: String? = nil,
    images: [Image]? = nil,
    layoutProvider: DivLayoutProvider? = nil,
    letterSpacing: Expression<Double>? = nil,
    lineHeight: Expression<Int>? = nil,
    longtapActions: [DivAction]? = nil,
    margins: DivEdgeInsets? = nil,
    maxLines: Expression<Int>? = nil,
    minHiddenLines: Expression<Int>? = nil,
    paddings: DivEdgeInsets? = nil,
    ranges: [Range]? = nil,
    reuseId: Expression<String>? = nil,
    rowSpan: Expression<Int>? = nil,
    selectable: Expression<Bool>? = nil,
    selectedActions: [DivAction]? = nil,
    strike: Expression<DivLineStyle>? = nil,
    text: Expression<String>,
    textAlignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    textAlignmentVertical: Expression<DivAlignmentVertical>? = nil,
    textColor: Expression<Color>? = nil,
    textGradient: DivTextGradient? = nil,
    textShadow: DivShadow? = nil,
    tooltips: [DivTooltip]? = nil,
    transform: DivTransform? = nil,
    transitionChange: DivChangeTransition? = nil,
    transitionIn: DivAppearanceTransition? = nil,
    transitionOut: DivAppearanceTransition? = nil,
    transitionTriggers: [DivTransitionTrigger]? = nil,
    underline: Expression<DivLineStyle>? = nil,
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
    self.autoEllipsize = autoEllipsize
    self.background = background
    self.border = border
    self.columnSpan = columnSpan
    self.disappearActions = disappearActions
    self.doubletapActions = doubletapActions
    self.ellipsis = ellipsis
    self.extensions = extensions
    self.focus = focus
    self.focusedTextColor = focusedTextColor
    self.fontFamily = fontFamily
    self.fontFeatureSettings = fontFeatureSettings
    self.fontSize = fontSize ?? .value(12)
    self.fontSizeUnit = fontSizeUnit ?? .value(.sp)
    self.fontWeight = fontWeight ?? .value(.regular)
    self.fontWeightValue = fontWeightValue
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.id = id
    self.images = images
    self.layoutProvider = layoutProvider
    self.letterSpacing = letterSpacing ?? .value(0)
    self.lineHeight = lineHeight
    self.longtapActions = longtapActions
    self.margins = margins
    self.maxLines = maxLines
    self.minHiddenLines = minHiddenLines
    self.paddings = paddings
    self.ranges = ranges
    self.reuseId = reuseId
    self.rowSpan = rowSpan
    self.selectable = selectable ?? .value(false)
    self.selectedActions = selectedActions
    self.strike = strike ?? .value(.none)
    self.text = text
    self.textAlignmentHorizontal = textAlignmentHorizontal ?? .value(.start)
    self.textAlignmentVertical = textAlignmentVertical ?? .value(.top)
    self.textColor = textColor ?? .value(Color.colorWithARGBHexCode(0xFF000000))
    self.textGradient = textGradient
    self.textShadow = textShadow
    self.tooltips = tooltips
    self.transform = transform
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.underline = underline ?? .value(.none)
    self.variableTriggers = variableTriggers
    self.variables = variables
    self.visibility = visibility ?? .value(.visible)
    self.visibilityAction = visibilityAction
    self.visibilityActions = visibilityActions
    self.width = width ?? .divMatchParentSize(DivMatchParentSize())
  }
}

#if DEBUG
extension DivText: Equatable {
  public static func ==(lhs: DivText, rhs: DivText) -> Bool {
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
      lhs.autoEllipsize == rhs.autoEllipsize,
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
      lhs.doubletapActions == rhs.doubletapActions,
      lhs.ellipsis == rhs.ellipsis,
      lhs.extensions == rhs.extensions
    else {
      return false
    }
    guard
      lhs.focus == rhs.focus,
      lhs.focusedTextColor == rhs.focusedTextColor,
      lhs.fontFamily == rhs.fontFamily
    else {
      return false
    }
    guard
      lhs.fontFeatureSettings == rhs.fontFeatureSettings,
      lhs.fontSize == rhs.fontSize,
      lhs.fontSizeUnit == rhs.fontSizeUnit
    else {
      return false
    }
    guard
      lhs.fontWeight == rhs.fontWeight,
      lhs.fontWeightValue == rhs.fontWeightValue,
      lhs.height == rhs.height
    else {
      return false
    }
    guard
      lhs.id == rhs.id,
      lhs.images == rhs.images,
      lhs.layoutProvider == rhs.layoutProvider
    else {
      return false
    }
    guard
      lhs.letterSpacing == rhs.letterSpacing,
      lhs.lineHeight == rhs.lineHeight,
      lhs.longtapActions == rhs.longtapActions
    else {
      return false
    }
    guard
      lhs.margins == rhs.margins,
      lhs.maxLines == rhs.maxLines,
      lhs.minHiddenLines == rhs.minHiddenLines
    else {
      return false
    }
    guard
      lhs.paddings == rhs.paddings,
      lhs.ranges == rhs.ranges,
      lhs.reuseId == rhs.reuseId
    else {
      return false
    }
    guard
      lhs.rowSpan == rhs.rowSpan,
      lhs.selectable == rhs.selectable,
      lhs.selectedActions == rhs.selectedActions
    else {
      return false
    }
    guard
      lhs.strike == rhs.strike,
      lhs.text == rhs.text,
      lhs.textAlignmentHorizontal == rhs.textAlignmentHorizontal
    else {
      return false
    }
    guard
      lhs.textAlignmentVertical == rhs.textAlignmentVertical,
      lhs.textColor == rhs.textColor,
      lhs.textGradient == rhs.textGradient
    else {
      return false
    }
    guard
      lhs.textShadow == rhs.textShadow,
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
      lhs.underline == rhs.underline,
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

extension DivText: Serializable {
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
    result["auto_ellipsize"] = autoEllipsize?.toValidSerializationValue()
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border?.toDictionary()
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["disappear_actions"] = disappearActions?.map { $0.toDictionary() }
    result["doubletap_actions"] = doubletapActions?.map { $0.toDictionary() }
    result["ellipsis"] = ellipsis?.toDictionary()
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["focused_text_color"] = focusedTextColor?.toValidSerializationValue()
    result["font_family"] = fontFamily?.toValidSerializationValue()
    result["font_feature_settings"] = fontFeatureSettings?.toValidSerializationValue()
    result["font_size"] = fontSize.toValidSerializationValue()
    result["font_size_unit"] = fontSizeUnit.toValidSerializationValue()
    result["font_weight"] = fontWeight.toValidSerializationValue()
    result["font_weight_value"] = fontWeightValue?.toValidSerializationValue()
    result["height"] = height.toDictionary()
    result["id"] = id
    result["images"] = images?.map { $0.toDictionary() }
    result["layout_provider"] = layoutProvider?.toDictionary()
    result["letter_spacing"] = letterSpacing.toValidSerializationValue()
    result["line_height"] = lineHeight?.toValidSerializationValue()
    result["longtap_actions"] = longtapActions?.map { $0.toDictionary() }
    result["margins"] = margins?.toDictionary()
    result["max_lines"] = maxLines?.toValidSerializationValue()
    result["min_hidden_lines"] = minHiddenLines?.toValidSerializationValue()
    result["paddings"] = paddings?.toDictionary()
    result["ranges"] = ranges?.map { $0.toDictionary() }
    result["reuse_id"] = reuseId?.toValidSerializationValue()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["selectable"] = selectable.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
    result["strike"] = strike.toValidSerializationValue()
    result["text"] = text.toValidSerializationValue()
    result["text_alignment_horizontal"] = textAlignmentHorizontal.toValidSerializationValue()
    result["text_alignment_vertical"] = textAlignmentVertical.toValidSerializationValue()
    result["text_color"] = textColor.toValidSerializationValue()
    result["text_gradient"] = textGradient?.toDictionary()
    result["text_shadow"] = textShadow?.toDictionary()
    result["tooltips"] = tooltips?.map { $0.toDictionary() }
    result["transform"] = transform?.toDictionary()
    result["transition_change"] = transitionChange?.toDictionary()
    result["transition_in"] = transitionIn?.toDictionary()
    result["transition_out"] = transitionOut?.toDictionary()
    result["transition_triggers"] = transitionTriggers?.map { $0.rawValue }
    result["underline"] = underline.toValidSerializationValue()
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
extension DivText.Ellipsis: Equatable {
  public static func ==(lhs: DivText.Ellipsis, rhs: DivText.Ellipsis) -> Bool {
    guard
      lhs.actions == rhs.actions,
      lhs.images == rhs.images,
      lhs.ranges == rhs.ranges
    else {
      return false
    }
    guard
      lhs.text == rhs.text
    else {
      return false
    }
    return true
  }
}
#endif

#if DEBUG
extension DivText.Image: Equatable {
  public static func ==(lhs: DivText.Image, rhs: DivText.Image) -> Bool {
    guard
      lhs.height == rhs.height,
      lhs.preloadRequired == rhs.preloadRequired,
      lhs.start == rhs.start
    else {
      return false
    }
    guard
      lhs.tintColor == rhs.tintColor,
      lhs.tintMode == rhs.tintMode,
      lhs.url == rhs.url
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

#if DEBUG
extension DivText.Range: Equatable {
  public static func ==(lhs: DivText.Range, rhs: DivText.Range) -> Bool {
    guard
      lhs.actions == rhs.actions,
      lhs.background == rhs.background,
      lhs.border == rhs.border
    else {
      return false
    }
    guard
      lhs.end == rhs.end,
      lhs.fontFamily == rhs.fontFamily,
      lhs.fontFeatureSettings == rhs.fontFeatureSettings
    else {
      return false
    }
    guard
      lhs.fontSize == rhs.fontSize,
      lhs.fontSizeUnit == rhs.fontSizeUnit,
      lhs.fontWeight == rhs.fontWeight
    else {
      return false
    }
    guard
      lhs.fontWeightValue == rhs.fontWeightValue,
      lhs.letterSpacing == rhs.letterSpacing,
      lhs.lineHeight == rhs.lineHeight
    else {
      return false
    }
    guard
      lhs.start == rhs.start,
      lhs.strike == rhs.strike,
      lhs.textColor == rhs.textColor
    else {
      return false
    }
    guard
      lhs.textShadow == rhs.textShadow,
      lhs.topOffset == rhs.topOffset,
      lhs.underline == rhs.underline
    else {
      return false
    }
    return true
  }
}
#endif

extension DivText.Ellipsis: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["actions"] = actions?.map { $0.toDictionary() }
    result["images"] = images?.map { $0.toDictionary() }
    result["ranges"] = ranges?.map { $0.toDictionary() }
    result["text"] = text.toValidSerializationValue()
    return result
  }
}

extension DivText.Image: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["height"] = height.toDictionary()
    result["preload_required"] = preloadRequired.toValidSerializationValue()
    result["start"] = start.toValidSerializationValue()
    result["tint_color"] = tintColor?.toValidSerializationValue()
    result["tint_mode"] = tintMode.toValidSerializationValue()
    result["url"] = url.toValidSerializationValue()
    result["width"] = width.toDictionary()
    return result
  }
}

extension DivText.Range: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["actions"] = actions?.map { $0.toDictionary() }
    result["background"] = background?.toDictionary()
    result["border"] = border?.toDictionary()
    result["end"] = end.toValidSerializationValue()
    result["font_family"] = fontFamily?.toValidSerializationValue()
    result["font_feature_settings"] = fontFeatureSettings?.toValidSerializationValue()
    result["font_size"] = fontSize?.toValidSerializationValue()
    result["font_size_unit"] = fontSizeUnit.toValidSerializationValue()
    result["font_weight"] = fontWeight?.toValidSerializationValue()
    result["font_weight_value"] = fontWeightValue?.toValidSerializationValue()
    result["letter_spacing"] = letterSpacing?.toValidSerializationValue()
    result["line_height"] = lineHeight?.toValidSerializationValue()
    result["start"] = start.toValidSerializationValue()
    result["strike"] = strike?.toValidSerializationValue()
    result["text_color"] = textColor?.toValidSerializationValue()
    result["text_shadow"] = textShadow?.toDictionary()
    result["top_offset"] = topOffset?.toValidSerializationValue()
    result["underline"] = underline?.toValidSerializationValue()
    return result
  }
}
