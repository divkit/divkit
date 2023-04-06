// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivText: DivBase {
  public final class Ellipsis {
    public let actions: [DivAction]? // at least 1 elements
    public let images: [Image]? // at least 1 elements
    public let ranges: [Range]? // at least 1 elements
    public let text: Expression<String> // at least 1 char

    public func resolveText(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveStringBasedValue(expression: text, initializer: { $0 })
    }

    static let actionsValidator: AnyArrayValueValidator<DivAction> =
      makeArrayValidator(minItems: 1)

    static let imagesValidator: AnyArrayValueValidator<DivText.Image> =
      makeArrayValidator(minItems: 1)

    static let rangesValidator: AnyArrayValueValidator<DivText.Range> =
      makeArrayValidator(minItems: 1)

    static let textValidator: AnyValueValidator<String> =
      makeStringValidator(minLength: 1)

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
    public let start: Expression<Int> // constraint: number >= 0
    public let tintColor: Expression<Color>?
    public let tintMode: Expression<DivBlendMode> // default value: source_in
    public let url: Expression<URL>
    public let width: DivFixedSize // default value: DivFixedSize(value: .value(20))

    public func resolveStart(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumericValue(expression: start)
    }

    public func resolveTintColor(_ resolver: ExpressionResolver) -> Color? {
      resolver.resolveStringBasedValue(expression: tintColor, initializer: Color.color(withHexString:))
    }

    public func resolveTintMode(_ resolver: ExpressionResolver) -> DivBlendMode {
      resolver.resolveStringBasedValue(expression: tintMode, initializer: DivBlendMode.init(rawValue:)) ?? DivBlendMode.sourceIn
    }

    public func resolveUrl(_ resolver: ExpressionResolver) -> URL? {
      resolver.resolveStringBasedValue(expression: url, initializer: URL.init(string:))
    }

    static let heightValidator: AnyValueValidator<DivFixedSize> =
      makeNoOpValueValidator()

    static let startValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let tintColorValidator: AnyValueValidator<Color> =
      makeNoOpValueValidator()

    static let tintModeValidator: AnyValueValidator<DivBlendMode> =
      makeNoOpValueValidator()

    static let widthValidator: AnyValueValidator<DivFixedSize> =
      makeNoOpValueValidator()

    init(
      height: DivFixedSize? = nil,
      start: Expression<Int>,
      tintColor: Expression<Color>? = nil,
      tintMode: Expression<DivBlendMode>? = nil,
      url: Expression<URL>,
      width: DivFixedSize? = nil
    ) {
      self.height = height ?? DivFixedSize(value: .value(20))
      self.start = start
      self.tintColor = tintColor
      self.tintMode = tintMode ?? .value(.sourceIn)
      self.url = url
      self.width = width ?? DivFixedSize(value: .value(20))
    }
  }

  public final class Range {
    public let actions: [DivAction]? // at least 1 elements
    public let background: DivTextRangeBackground?
    public let border: DivTextRangeBorder?
    public let end: Expression<Int> // constraint: number > 0
    public let fontFamily: Expression<DivFontFamily>?
    public let fontSize: Expression<Int>? // constraint: number >= 0
    public let fontSizeUnit: Expression<DivSizeUnit> // default value: sp
    public let fontWeight: Expression<DivFontWeight>?
    public let letterSpacing: Expression<Double>?
    public let lineHeight: Expression<Int>? // constraint: number >= 0
    public let start: Expression<Int> // constraint: number >= 0
    public let strike: Expression<DivLineStyle>?
    public let textColor: Expression<Color>?
    public let topOffset: Expression<Int>? // constraint: number >= 0
    public let underline: Expression<DivLineStyle>?

    public func resolveEnd(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumericValue(expression: end)
    }

    public func resolveFontFamily(_ resolver: ExpressionResolver) -> DivFontFamily? {
      resolver.resolveStringBasedValue(expression: fontFamily, initializer: DivFontFamily.init(rawValue:))
    }

    public func resolveFontSize(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumericValue(expression: fontSize)
    }

    public func resolveFontSizeUnit(_ resolver: ExpressionResolver) -> DivSizeUnit {
      resolver.resolveStringBasedValue(expression: fontSizeUnit, initializer: DivSizeUnit.init(rawValue:)) ?? DivSizeUnit.sp
    }

    public func resolveFontWeight(_ resolver: ExpressionResolver) -> DivFontWeight? {
      resolver.resolveStringBasedValue(expression: fontWeight, initializer: DivFontWeight.init(rawValue:))
    }

    public func resolveLetterSpacing(_ resolver: ExpressionResolver) -> Double? {
      resolver.resolveNumericValue(expression: letterSpacing)
    }

    public func resolveLineHeight(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumericValue(expression: lineHeight)
    }

    public func resolveStart(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumericValue(expression: start)
    }

    public func resolveStrike(_ resolver: ExpressionResolver) -> DivLineStyle? {
      resolver.resolveStringBasedValue(expression: strike, initializer: DivLineStyle.init(rawValue:))
    }

    public func resolveTextColor(_ resolver: ExpressionResolver) -> Color? {
      resolver.resolveStringBasedValue(expression: textColor, initializer: Color.color(withHexString:))
    }

    public func resolveTopOffset(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumericValue(expression: topOffset)
    }

    public func resolveUnderline(_ resolver: ExpressionResolver) -> DivLineStyle? {
      resolver.resolveStringBasedValue(expression: underline, initializer: DivLineStyle.init(rawValue:))
    }

    static let actionsValidator: AnyArrayValueValidator<DivAction> =
      makeArrayValidator(minItems: 1)

    static let backgroundValidator: AnyValueValidator<DivTextRangeBackground> =
      makeNoOpValueValidator()

    static let borderValidator: AnyValueValidator<DivTextRangeBorder> =
      makeNoOpValueValidator()

    static let endValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 > 0 })

    static let fontFamilyValidator: AnyValueValidator<DivFontFamily> =
      makeNoOpValueValidator()

    static let fontSizeValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let fontSizeUnitValidator: AnyValueValidator<DivSizeUnit> =
      makeNoOpValueValidator()

    static let fontWeightValidator: AnyValueValidator<DivFontWeight> =
      makeNoOpValueValidator()

    static let lineHeightValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let startValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let strikeValidator: AnyValueValidator<DivLineStyle> =
      makeNoOpValueValidator()

    static let textColorValidator: AnyValueValidator<Color> =
      makeNoOpValueValidator()

    static let topOffsetValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let underlineValidator: AnyValueValidator<DivLineStyle> =
      makeNoOpValueValidator()

    init(
      actions: [DivAction]? = nil,
      background: DivTextRangeBackground? = nil,
      border: DivTextRangeBorder? = nil,
      end: Expression<Int>,
      fontFamily: Expression<DivFontFamily>? = nil,
      fontSize: Expression<Int>? = nil,
      fontSizeUnit: Expression<DivSizeUnit>? = nil,
      fontWeight: Expression<DivFontWeight>? = nil,
      letterSpacing: Expression<Double>? = nil,
      lineHeight: Expression<Int>? = nil,
      start: Expression<Int>,
      strike: Expression<DivLineStyle>? = nil,
      textColor: Expression<Color>? = nil,
      topOffset: Expression<Int>? = nil,
      underline: Expression<DivLineStyle>? = nil
    ) {
      self.actions = actions
      self.background = background
      self.border = border
      self.end = end
      self.fontFamily = fontFamily
      self.fontSize = fontSize
      self.fontSizeUnit = fontSizeUnit ?? .value(.sp)
      self.fontWeight = fontWeight
      self.letterSpacing = letterSpacing
      self.lineHeight = lineHeight
      self.start = start
      self.strike = strike
      self.textColor = textColor
      self.topOffset = topOffset
      self.underline = underline
    }
  }

  public static let type: String = "text"
  public let accessibility: DivAccessibility
  public let action: DivAction?
  public let actionAnimation: DivAnimation // default value: DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
  public let actions: [DivAction]? // at least 1 elements
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let autoEllipsize: Expression<Bool>?
  public let background: [DivBackground]? // at least 1 elements
  public let border: DivBorder
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let doubletapActions: [DivAction]? // at least 1 elements
  public let ellipsis: Ellipsis?
  public let extensions: [DivExtension]? // at least 1 elements
  public let focus: DivFocus?
  public let focusedTextColor: Expression<Color>?
  public let fontFamily: Expression<DivFontFamily> // default value: text
  public let fontSize: Expression<Int> // constraint: number >= 0; default value: 12
  public let fontSizeUnit: Expression<DivSizeUnit> // default value: sp
  public let fontWeight: Expression<DivFontWeight> // default value: regular
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: String? // at least 1 char
  public let images: [Image]? // at least 1 elements
  public let letterSpacing: Expression<Double> // default value: 0
  public let lineHeight: Expression<Int>? // constraint: number >= 0
  public let longtapActions: [DivAction]? // at least 1 elements
  public let margins: DivEdgeInsets
  public let maxLines: Expression<Int>? // constraint: number >= 0
  public let minHiddenLines: Expression<Int>? // constraint: number >= 0
  public let paddings: DivEdgeInsets
  public let ranges: [Range]? // at least 1 elements
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let selectable: Expression<Bool> // default value: false
  public let selectedActions: [DivAction]? // at least 1 elements
  public let strike: Expression<DivLineStyle> // default value: none
  public let text: Expression<CFString> // at least 1 char
  public let textAlignmentHorizontal: Expression<DivAlignmentHorizontal> // default value: left
  public let textAlignmentVertical: Expression<DivAlignmentVertical> // default value: top
  public let textColor: Expression<Color> // default value: #FF000000
  public let textGradient: DivTextGradient?
  public let tooltips: [DivTooltip]? // at least 1 elements
  public let transform: DivTransform
  public let transitionChange: DivChangeTransition?
  public let transitionIn: DivAppearanceTransition?
  public let transitionOut: DivAppearanceTransition?
  public let transitionTriggers: [DivTransitionTrigger]? // at least 1 elements
  public let underline: Expression<DivLineStyle> // default value: none
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

  public func resolveAutoEllipsize(_ resolver: ExpressionResolver) -> Bool? {
    resolver.resolveNumericValue(expression: autoEllipsize)
  }

  public func resolveColumnSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: columnSpan)
  }

  public func resolveFocusedTextColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveStringBasedValue(expression: focusedTextColor, initializer: Color.color(withHexString:))
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

  public func resolveLetterSpacing(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumericValue(expression: letterSpacing) ?? 0
  }

  public func resolveLineHeight(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: lineHeight)
  }

  public func resolveMaxLines(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: maxLines)
  }

  public func resolveMinHiddenLines(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: minHiddenLines)
  }

  public func resolveRowSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: rowSpan)
  }

  public func resolveSelectable(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumericValue(expression: selectable) ?? false
  }

  public func resolveStrike(_ resolver: ExpressionResolver) -> DivLineStyle {
    resolver.resolveStringBasedValue(expression: strike, initializer: DivLineStyle.init(rawValue:)) ?? DivLineStyle.none
  }

  public func resolveText(_ resolver: ExpressionResolver) -> CFString? {
    resolver.resolveStringBasedValue(expression: text, initializer: { $0 as CFString})
  }

  public func resolveTextAlignmentHorizontal(_ resolver: ExpressionResolver) -> DivAlignmentHorizontal {
    resolver.resolveStringBasedValue(expression: textAlignmentHorizontal, initializer: DivAlignmentHorizontal.init(rawValue:)) ?? DivAlignmentHorizontal.left
  }

  public func resolveTextAlignmentVertical(_ resolver: ExpressionResolver) -> DivAlignmentVertical {
    resolver.resolveStringBasedValue(expression: textAlignmentVertical, initializer: DivAlignmentVertical.init(rawValue:)) ?? DivAlignmentVertical.top
  }

  public func resolveTextColor(_ resolver: ExpressionResolver) -> Color {
    resolver.resolveStringBasedValue(expression: textColor, initializer: Color.color(withHexString:)) ?? Color.colorWithARGBHexCode(0xFF000000)
  }

  public func resolveUnderline(_ resolver: ExpressionResolver) -> DivLineStyle {
    resolver.resolveStringBasedValue(expression: underline, initializer: DivLineStyle.init(rawValue:)) ?? DivLineStyle.none
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

  static let autoEllipsizeValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let backgroundValidator: AnyArrayValueValidator<DivBackground> =
    makeArrayValidator(minItems: 1)

  static let borderValidator: AnyValueValidator<DivBorder> =
    makeNoOpValueValidator()

  static let columnSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let doubletapActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let ellipsisValidator: AnyValueValidator<DivText.Ellipsis> =
    makeNoOpValueValidator()

  static let extensionsValidator: AnyArrayValueValidator<DivExtension> =
    makeArrayValidator(minItems: 1)

  static let focusValidator: AnyValueValidator<DivFocus> =
    makeNoOpValueValidator()

  static let focusedTextColorValidator: AnyValueValidator<Color> =
    makeNoOpValueValidator()

  static let fontFamilyValidator: AnyValueValidator<DivFontFamily> =
    makeNoOpValueValidator()

  static let fontSizeValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let fontSizeUnitValidator: AnyValueValidator<DivSizeUnit> =
    makeNoOpValueValidator()

  static let fontWeightValidator: AnyValueValidator<DivFontWeight> =
    makeNoOpValueValidator()

  static let heightValidator: AnyValueValidator<DivSize> =
    makeNoOpValueValidator()

  static let idValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let imagesValidator: AnyArrayValueValidator<DivText.Image> =
    makeArrayValidator(minItems: 1)

  static let lineHeightValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let longtapActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let marginsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let maxLinesValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let minHiddenLinesValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let paddingsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let rangesValidator: AnyArrayValueValidator<DivText.Range> =
    makeArrayValidator(minItems: 1)

  static let rowSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let selectableValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let selectedActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let strikeValidator: AnyValueValidator<DivLineStyle> =
    makeNoOpValueValidator()

  static let textValidator: AnyValueValidator<CFString> =
    makeCFStringValidator(minLength: 1)

  static let textAlignmentHorizontalValidator: AnyValueValidator<DivAlignmentHorizontal> =
    makeNoOpValueValidator()

  static let textAlignmentVerticalValidator: AnyValueValidator<DivAlignmentVertical> =
    makeNoOpValueValidator()

  static let textColorValidator: AnyValueValidator<Color> =
    makeNoOpValueValidator()

  static let textGradientValidator: AnyValueValidator<DivTextGradient> =
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

  static let underlineValidator: AnyValueValidator<DivLineStyle> =
    makeNoOpValueValidator()

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
    autoEllipsize: Expression<Bool>? = nil,
    background: [DivBackground]? = nil,
    border: DivBorder? = nil,
    columnSpan: Expression<Int>? = nil,
    doubletapActions: [DivAction]? = nil,
    ellipsis: Ellipsis? = nil,
    extensions: [DivExtension]? = nil,
    focus: DivFocus? = nil,
    focusedTextColor: Expression<Color>? = nil,
    fontFamily: Expression<DivFontFamily>? = nil,
    fontSize: Expression<Int>? = nil,
    fontSizeUnit: Expression<DivSizeUnit>? = nil,
    fontWeight: Expression<DivFontWeight>? = nil,
    height: DivSize? = nil,
    id: String? = nil,
    images: [Image]? = nil,
    letterSpacing: Expression<Double>? = nil,
    lineHeight: Expression<Int>? = nil,
    longtapActions: [DivAction]? = nil,
    margins: DivEdgeInsets? = nil,
    maxLines: Expression<Int>? = nil,
    minHiddenLines: Expression<Int>? = nil,
    paddings: DivEdgeInsets? = nil,
    ranges: [Range]? = nil,
    rowSpan: Expression<Int>? = nil,
    selectable: Expression<Bool>? = nil,
    selectedActions: [DivAction]? = nil,
    strike: Expression<DivLineStyle>? = nil,
    text: Expression<CFString>,
    textAlignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    textAlignmentVertical: Expression<DivAlignmentVertical>? = nil,
    textColor: Expression<Color>? = nil,
    textGradient: DivTextGradient? = nil,
    tooltips: [DivTooltip]? = nil,
    transform: DivTransform? = nil,
    transitionChange: DivChangeTransition? = nil,
    transitionIn: DivAppearanceTransition? = nil,
    transitionOut: DivAppearanceTransition? = nil,
    transitionTriggers: [DivTransitionTrigger]? = nil,
    underline: Expression<DivLineStyle>? = nil,
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
    self.autoEllipsize = autoEllipsize
    self.background = background
    self.border = border ?? DivBorder()
    self.columnSpan = columnSpan
    self.doubletapActions = doubletapActions
    self.ellipsis = ellipsis
    self.extensions = extensions
    self.focus = focus
    self.focusedTextColor = focusedTextColor
    self.fontFamily = fontFamily ?? .value(.text)
    self.fontSize = fontSize ?? .value(12)
    self.fontSizeUnit = fontSizeUnit ?? .value(.sp)
    self.fontWeight = fontWeight ?? .value(.regular)
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.id = id
    self.images = images
    self.letterSpacing = letterSpacing ?? .value(0)
    self.lineHeight = lineHeight
    self.longtapActions = longtapActions
    self.margins = margins ?? DivEdgeInsets()
    self.maxLines = maxLines
    self.minHiddenLines = minHiddenLines
    self.paddings = paddings ?? DivEdgeInsets()
    self.ranges = ranges
    self.rowSpan = rowSpan
    self.selectable = selectable ?? .value(false)
    self.selectedActions = selectedActions
    self.strike = strike ?? .value(.none)
    self.text = text
    self.textAlignmentHorizontal = textAlignmentHorizontal ?? .value(.left)
    self.textAlignmentVertical = textAlignmentVertical ?? .value(.top)
    self.textColor = textColor ?? .value(Color.colorWithARGBHexCode(0xFF000000))
    self.textGradient = textGradient
    self.tooltips = tooltips
    self.transform = transform ?? DivTransform()
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.underline = underline ?? .value(.none)
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
      lhs.doubletapActions == rhs.doubletapActions
    else {
      return false
    }
    guard
      lhs.ellipsis == rhs.ellipsis,
      lhs.extensions == rhs.extensions,
      lhs.focus == rhs.focus
    else {
      return false
    }
    guard
      lhs.focusedTextColor == rhs.focusedTextColor,
      lhs.fontFamily == rhs.fontFamily,
      lhs.fontSize == rhs.fontSize
    else {
      return false
    }
    guard
      lhs.fontSizeUnit == rhs.fontSizeUnit,
      lhs.fontWeight == rhs.fontWeight,
      lhs.height == rhs.height
    else {
      return false
    }
    guard
      lhs.id == rhs.id,
      lhs.images == rhs.images,
      lhs.letterSpacing == rhs.letterSpacing
    else {
      return false
    }
    guard
      lhs.lineHeight == rhs.lineHeight,
      lhs.longtapActions == rhs.longtapActions,
      lhs.margins == rhs.margins
    else {
      return false
    }
    guard
      lhs.maxLines == rhs.maxLines,
      lhs.minHiddenLines == rhs.minHiddenLines,
      lhs.paddings == rhs.paddings
    else {
      return false
    }
    guard
      lhs.ranges == rhs.ranges,
      lhs.rowSpan == rhs.rowSpan,
      lhs.selectable == rhs.selectable
    else {
      return false
    }
    guard
      lhs.selectedActions == rhs.selectedActions,
      lhs.strike == rhs.strike,
      lhs.text == rhs.text
    else {
      return false
    }
    guard
      lhs.textAlignmentHorizontal == rhs.textAlignmentHorizontal,
      lhs.textAlignmentVertical == rhs.textAlignmentVertical,
      lhs.textColor == rhs.textColor
    else {
      return false
    }
    guard
      lhs.textGradient == rhs.textGradient,
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
      lhs.visibility == rhs.visibility
    else {
      return false
    }
    guard
      lhs.visibilityAction == rhs.visibilityAction,
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
    result["accessibility"] = accessibility.toDictionary()
    result["action"] = action?.toDictionary()
    result["action_animation"] = actionAnimation.toDictionary()
    result["actions"] = actions?.map { $0.toDictionary() }
    result["alignment_horizontal"] = alignmentHorizontal?.toValidSerializationValue()
    result["alignment_vertical"] = alignmentVertical?.toValidSerializationValue()
    result["alpha"] = alpha.toValidSerializationValue()
    result["auto_ellipsize"] = autoEllipsize?.toValidSerializationValue()
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border.toDictionary()
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["doubletap_actions"] = doubletapActions?.map { $0.toDictionary() }
    result["ellipsis"] = ellipsis?.toDictionary()
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["focused_text_color"] = focusedTextColor?.toValidSerializationValue()
    result["font_family"] = fontFamily.toValidSerializationValue()
    result["font_size"] = fontSize.toValidSerializationValue()
    result["font_size_unit"] = fontSizeUnit.toValidSerializationValue()
    result["font_weight"] = fontWeight.toValidSerializationValue()
    result["height"] = height.toDictionary()
    result["id"] = id
    result["images"] = images?.map { $0.toDictionary() }
    result["letter_spacing"] = letterSpacing.toValidSerializationValue()
    result["line_height"] = lineHeight?.toValidSerializationValue()
    result["longtap_actions"] = longtapActions?.map { $0.toDictionary() }
    result["margins"] = margins.toDictionary()
    result["max_lines"] = maxLines?.toValidSerializationValue()
    result["min_hidden_lines"] = minHiddenLines?.toValidSerializationValue()
    result["paddings"] = paddings.toDictionary()
    result["ranges"] = ranges?.map { $0.toDictionary() }
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["selectable"] = selectable.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
    result["strike"] = strike.toValidSerializationValue()
    result["text"] = text.toValidSerializationValue()
    result["text_alignment_horizontal"] = textAlignmentHorizontal.toValidSerializationValue()
    result["text_alignment_vertical"] = textAlignmentVertical.toValidSerializationValue()
    result["text_color"] = textColor.toValidSerializationValue()
    result["text_gradient"] = textGradient?.toDictionary()
    result["tooltips"] = tooltips?.map { $0.toDictionary() }
    result["transform"] = transform.toDictionary()
    result["transition_change"] = transitionChange?.toDictionary()
    result["transition_in"] = transitionIn?.toDictionary()
    result["transition_out"] = transitionOut?.toDictionary()
    result["transition_triggers"] = transitionTriggers?.map { $0.rawValue }
    result["underline"] = underline.toValidSerializationValue()
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
      lhs.start == rhs.start,
      lhs.tintColor == rhs.tintColor
    else {
      return false
    }
    guard
      lhs.tintMode == rhs.tintMode,
      lhs.url == rhs.url,
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
      lhs.fontSize == rhs.fontSize
    else {
      return false
    }
    guard
      lhs.fontSizeUnit == rhs.fontSizeUnit,
      lhs.fontWeight == rhs.fontWeight,
      lhs.letterSpacing == rhs.letterSpacing
    else {
      return false
    }
    guard
      lhs.lineHeight == rhs.lineHeight,
      lhs.start == rhs.start,
      lhs.strike == rhs.strike
    else {
      return false
    }
    guard
      lhs.textColor == rhs.textColor,
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
    result["font_size"] = fontSize?.toValidSerializationValue()
    result["font_size_unit"] = fontSizeUnit.toValidSerializationValue()
    result["font_weight"] = fontWeight?.toValidSerializationValue()
    result["letter_spacing"] = letterSpacing?.toValidSerializationValue()
    result["line_height"] = lineHeight?.toValidSerializationValue()
    result["start"] = start.toValidSerializationValue()
    result["strike"] = strike?.toValidSerializationValue()
    result["text_color"] = textColor?.toValidSerializationValue()
    result["top_offset"] = topOffset?.toValidSerializationValue()
    result["underline"] = underline?.toValidSerializationValue()
    return result
  }
}
