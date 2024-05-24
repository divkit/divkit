import CoreFoundation
import CoreGraphics
import Foundation

import BasePublic
import BaseUIPublic
import LayoutKit

extension DivText: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    let expressionResolver = context.expressionResolver
    let lazyText = Lazy(getter: { [unowned self] in
      resolveText(expressionResolver) ?? ""
    })
    return try applyBaseProperties(
      to: { try makeBaseBlock(context: context, text: lazyText) },
      context: context,
      actionsHolder: self,
      customAccessibilityParams: CustomAccessibilityParams(
        defaultTraits: .staticText
      ) { [unowned self] in
        accessibility?.resolveDescription(expressionResolver) ?? lazyText.value
      }
    )
  }

  private func makeBaseBlock(
    context: DivBlockModelingContext,
    text: Lazy<String>
  ) throws -> Block {
    let expressionResolver = context.expressionResolver

    let fontParams = FontParams(
      family: resolveFontFamily(expressionResolver) ?? "",
      weight: resolveFontWeight(expressionResolver),
      size: resolveFontSize(expressionResolver),
      unit: resolveFontSizeUnit(expressionResolver),
      featureSettings: resolveFontFeatureSettings(expressionResolver)
    )
    var typo = fontParams.makeTypo(fontProvider: context.fontProvider).allowHeightOverrun

    let alignment = resolveTextAlignmentHorizontal(expressionResolver)
      .makeTextAlignment(uiLayoutDirection: context.layoutDirection)
    if alignment != .left {
      typo = typo.with(alignment: alignment)
    }

    let kern = CGFloat(resolveLetterSpacing(expressionResolver))
    if !kern.isApproximatelyEqualTo(0) {
      typo = typo.kerned(kern)
    }

    let resolvedColor: Color = resolveTextColor(expressionResolver)

    if resolvedColor != .black {
      typo = typo.with(color: resolvedColor)
    }

    if let lineHeight = resolveLineHeight(expressionResolver) {
      typo = typo.with(height: CGFloat(lineHeight))
    }

    switch resolveStrike(expressionResolver) {
    case .none: break
    case .single: typo = typo.struckThrough(.single)
    }

    switch resolveUnderline(expressionResolver) {
    case .none: break
    case .single: typo = typo.underlined(.single)
    }

    let attributedString = makeAttributedString(
      text: text.value as CFString,
      typo: typo,
      ranges: ranges,
      actions: nil,
      context: context,
      fontParams: fontParams
    )

    let images = makeInlineImages(
      context: context,
      images: self.images,
      text: attributedString
    )

    let truncationToken = ellipsis.map {
      makeAttributedString(
        text: ($0.resolveText(expressionResolver) ?? "") as CFString,
        typo: typo,
        ranges: $0.ranges,
        actions: $0.actions?.uiActions(context: context),
        context: context,
        fontParams: fontParams
      )
    }
    let truncationImages = makeInlineImages(
      context: context,
      images: ellipsis?.images,
      text: truncationToken
    )

    if let id {
      context.blockStateStorage.setState(
        id: id,
        cardId: context.cardId,
        state: TextBlockViewState(text: attributedString.string)
      )
    }

    return TextBlock(
      widthTrait: resolveContentWidthTrait(context),
      heightTrait: resolveContentHeightTrait(context),
      text: attributedString,
      textGradient: resolveGradient(expressionResolver),
      verticalAlignment: resolveTextAlignmentVertical(expressionResolver).alignment,
      maxIntrinsicNumberOfLines: resolveMaxLines(expressionResolver) ?? .max,
      minNumberOfHiddenLines: resolveMinHiddenLines(expressionResolver) ?? 0,
      images: images,
      accessibilityElement: nil,
      truncationToken: truncationToken,
      truncationImages: truncationImages,
      canSelect: resolveSelectable(expressionResolver)
    )
  }

  private func makeAttributedString(
    text: CFString,
    typo: Typo,
    ranges: [Range]?,
    actions: [UserInterfaceAction]?,
    context: DivBlockModelingContext,
    fontParams: FontParams
  ) -> NSAttributedString {
    let length = CFStringGetLength(text)
    let attributedString = CFAttributedStringCreateMutable(kCFAllocatorDefault, length)!
    CFAttributedStringBeginEditing(attributedString)
    CFAttributedStringReplaceString(attributedString, CFRange(), text)
    let fullRange = CFRange(location: 0, length: length)
    typo.apply(to: attributedString, at: fullRange)
    ranges?.forEach { apply($0, to: attributedString, context: context, fontParams: fontParams) }
    attributedString.apply(actions, at: fullRange)
    CFAttributedStringEndEditing(attributedString)
    return attributedString
  }

  private func makeInlineImages(
    context: DivBlockModelingContext,
    images: [Image]?,
    text: NSAttributedString?
  ) -> [TextBlock.InlineImage] {
    guard let text else {
      return []
    }
    return (images ?? [])
      .filter {
        let start = $0.resolveStart(context.expressionResolver) ?? 0
        return start <= CFAttributedStringGetLength(text)
      }
      .map { $0.makeImage(context: context) }
  }

  private func apply(
    _ range: DivText.Range,
    to string: CFMutableAttributedString,
    context: DivBlockModelingContext,
    fontParams: FontParams
  ) {
    let expressionResolver = context.expressionResolver

    let start = range.resolveStart(expressionResolver) ?? 0
    let end = range.resolveEnd(expressionResolver) ?? 0
    guard end > start && start < CFAttributedStringGetLength(string) else {
      return
    }

    let fontTypo: Typo?
    if range.fontFamily != nil
      || range.fontFeatureSettings != nil
      || range.fontSize != nil
      || range.fontWeight != nil {
      let rangeFontParams = FontParams(
        family: range.resolveFontFamily(expressionResolver) ?? fontParams.family,
        weight: range.resolveFontWeight(expressionResolver) ?? fontParams.weight,
        size: range.resolveFontSize(expressionResolver) ?? fontParams.size,
        unit: range.resolveFontSizeUnit(expressionResolver),
        featureSettings: range.resolveFontFeatureSettings(expressionResolver)
          ?? fontParams.featureSettings
      )
      fontTypo = rangeFontParams.makeTypo(fontProvider: context.fontProvider)
    } else {
      fontTypo = nil
    }
    let colorTypo = range.resolveTextColor(expressionResolver)
      .map { Typo(color: $0) }
    let heightTypo = range.resolveLineHeight(expressionResolver)
      .map { Typo(height: CGFloat($0)) }
    let spacingTypo = range.resolveLetterSpacing(expressionResolver)
      .map { Typo(kern: CGFloat($0)) }
    let strikethroughTypo = range.resolveStrike(expressionResolver)
      .map { Typo(strikethrough: $0.underlineStyle) }
    let underlineTypo = range.resolveUnderline(expressionResolver)
      .map { Typo(underline: $0.underlineStyle) }
    let typos = [fontTypo, colorTypo, heightTypo, spacingTypo, strikethroughTypo, underlineTypo]
      .compactMap { $0 }
    let actions = range.actions?.uiActions(context: context)
    if typos.isEmpty, actions == nil, range.background == nil, range.border == nil {
      return
    }

    let actualEnd = min(end, CFAttributedStringGetLength(string))
    let cfRange = CFRange(location: start, length: actualEnd - start)
    typos.forEach { $0.apply(to: string, at: cfRange) }
    string.apply(actions, at: cfRange)

    range.makeBackground(range: cfRange, resolver: expressionResolver)?
      .apply(to: string, at: cfRange)

    range.makeBorder(range: cfRange, resolver: expressionResolver)?
      .apply(to: string, at: cfRange)
  }

  private func resolveGradient(_ expressionResolver: ExpressionResolver) -> Gradient? {
    guard let textGradient else {
      return nil
    }
    switch textGradient {
    case let .divLinearGradient(gradient):
      return Gradient.Linear(
        colors: gradient.resolveColors(expressionResolver) ?? [],
        angle: gradient.resolveAngle(expressionResolver)
      ).map { .linear($0) }
    case let .divRadialGradient(gradient):
      return Gradient.Radial(
        colors: gradient.resolveColors(expressionResolver) ?? [],
        end: gradient.resolveRadius(expressionResolver),
        centerX: gradient.resolveCenterX(expressionResolver),
        centerY: gradient.resolveCenterY(expressionResolver)
      ).map { .radial($0) }
    }
  }
}

private struct FontParams {
  let family: String
  let weight: DivFontWeight
  let size: Int
  let unit: DivSizeUnit
  let featureSettings: String?

  func makeTypo(fontProvider: DivFontProvider) -> Typo {
    var font = fontProvider.font(
      family: family,
      weight: weight,
      size: unit.makeScaledValue(size)
    )
    if let featureSettings {
      font = font.withFontFeatureSettings(featureSettings)
    }
    return Typo(font: font)
  }
}

extension CFMutableAttributedString {
  fileprivate func apply(
    _ actions: [UserInterfaceAction]?,
    at range: CFRange
  ) {
    if let actions {
      ActionsAttribute(actions: actions).apply(to: self, at: range)
    }
  }
}

extension DivAlignmentHorizontal {
  fileprivate func makeTextAlignment(
    uiLayoutDirection: UserInterfaceLayoutDirection =
      .system
  ) -> TextAlignment {
    switch self {
    case .start:
      uiLayoutDirection == .leftToRight ? .left : .right
    case .end:
      uiLayoutDirection == .leftToRight ? .right : .left
    case .left:
      .left
    case .center:
      .center
    case .right:
      .right
    }
  }
}

extension URLQueryItem {
  init(name: String, value: DivCardID?) {
    self.init(name: name, value: value?.rawValue)
  }
}

extension DivText.Image {
  fileprivate func makeImage(
    context: DivBlockModelingContext
  ) -> TextBlock.InlineImage {
    let expressionResolver = context.expressionResolver
    return TextBlock.InlineImage(
      size: CGSize(
        width: CGFloat(width.resolveValue(expressionResolver) ?? 0),
        height: CGFloat(height.resolveValue(expressionResolver) ?? 0)
      ),
      holder: context.imageHolderFactory.make(resolveUrl(expressionResolver)),
      location: resolveStart(expressionResolver) ?? 0,
      tintColor: resolveTintColor(expressionResolver)
    )
  }
}

extension DivText.Range {
  fileprivate func makeBorder(
    range: CFRange,
    resolver: ExpressionResolver
  ) -> BorderAttribute? {
    guard let border else { return nil }
    let color = border.stroke?.resolveColor(resolver)
    let width = border.stroke?.resolveWidth(resolver)
    let cornerRadius = border.resolveCornerRadius(resolver)
    return BorderAttribute(
      color: color?.cgColor,
      width: width.flatMap(CGFloat.init),
      cornerRadius:
      cornerRadius.flatMap(CGFloat.init),
      range: range
    )
  }

  fileprivate func makeBackground(
    range: CFRange,
    resolver: ExpressionResolver
  ) -> BackgroundAttribute? {
    guard let background else { return nil }
    switch background {
    case let .divSolidBackground(solid):
      let color = solid.resolveColor(resolver) ?? .clear
      return BackgroundAttribute(color: color.cgColor, range: range)
    }
  }
}

extension DivLineStyle {
  fileprivate var underlineStyle: UnderlineStyle {
    switch self {
    case .none: []
    case .single: .single
    }
  }
}
