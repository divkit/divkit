import CoreFoundation
import CoreGraphics
import Foundation

import BasePublic
import BaseUIPublic
import LayoutKit
import NetworkingPublic

extension DivText: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    let expressionResolver = context.expressionResolver
    return try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actions: makeActions(context: context),
      actionAnimation: actionAnimation.makeActionAnimation(with: expressionResolver),
      doubleTapActions: makeDoubleTapActions(context: context),
      longTapActions: makeLongTapActions(context: context),
      customA11yElement: makeCustomA11yElement(with: expressionResolver)
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    let expressionResolver = context.expressionResolver

    let font = context.fontProvider.font(
      family: resolveFontFamily(expressionResolver) ?? "",
      weight: resolveFontWeight(expressionResolver),
      size: resolveFontSizeUnit(expressionResolver)
        .makeScaledValue(resolveFontSize(expressionResolver))
    )
    var typo = Typo(font: font).allowHeightOverrun

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
      text: resolveText(expressionResolver) ?? ("" as CFString),
      typo: typo,
      ranges: ranges,
      actions: nil,
      context: context
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
        actions: $0.makeActions(context: context),
        context: context
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
      widthTrait: makeContentWidthTrait(with: context),
      heightTrait: makeContentHeightTrait(with: context),
      text: attributedString,
      textGradient: makeGradient(expressionResolver),
      verticalAlignment:
      resolveTextAlignmentVertical(expressionResolver).alignment,
      maxIntrinsicNumberOfLines: resolveMaxLines(expressionResolver) ?? .max,
      minNumberOfHiddenLines: resolveMinHiddenLines(expressionResolver) ?? 0,
      images: images,
      truncationToken: truncationToken,
      truncationImages: truncationImages,
      canSelect: resolveSelectable(expressionResolver)
    )
  }

  private func makeAttributedString(
    text: CFString,
    typo: Typo,
    ranges: [Range]?,
    actions: NonEmptyArray<UserInterfaceAction>?,
    context: DivBlockModelingContext
  ) -> NSAttributedString {
    let length = CFStringGetLength(text)
    let attributedString = CFAttributedStringCreateMutable(kCFAllocatorDefault, length)!
    CFAttributedStringBeginEditing(attributedString)
    CFAttributedStringReplaceString(attributedString, CFRange(), text)
    let fullRange = CFRange(location: 0, length: length)
    typo.apply(to: attributedString, at: fullRange)
    ranges?.forEach { apply($0, to: attributedString, context: context) }
    attributedString.apply(actions, at: fullRange)
    CFAttributedStringEndEditing(attributedString)
    return attributedString
  }

  private func makeInlineImages(
    context: DivBlockModelingContext,
    images: [Image]?,
    text: NSAttributedString?
  ) -> [TextBlock.InlineImage] {
    guard let text = text else {
      return []
    }
    return (images ?? [])
      .filter {
        let start = $0.resolveStart(context.expressionResolver) ?? 0
        return start <= CFAttributedStringGetLength(text)
      }
      .map { $0.makeImage(
        withFactory: context.imageHolderFactory,
        expressionResolver: context.expressionResolver
      ) }
  }

  private func apply(
    _ range: DivText.Range,
    to string: CFMutableAttributedString,
    context: DivBlockModelingContext
  ) {
    let expressionResolver = context.expressionResolver

    let start = range.resolveStart(expressionResolver) ?? 0
    let end = range.resolveEnd(expressionResolver) ?? 0
    guard end > start && start < CFAttributedStringGetLength(string) else {
      return
    }

    let fontTypo: Typo?
    if range.fontSize != nil || range.fontWeight != nil {
      let font = context.fontProvider.font(
        family: range.resolveFontFamily(expressionResolver)
          ?? resolveFontFamily(expressionResolver) ?? "",
        weight:
        range.resolveFontWeight(expressionResolver)
          ?? resolveFontWeight(expressionResolver),
        size: CGFloat(
          range.resolveFontSize(expressionResolver)
            ?? resolveFontSize(expressionResolver)
        )
      )
      fontTypo = Typo(font: font)
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
    let actions = range.makeActions(context: context)
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

  private func makeGradient(_ expressionResolver: ExpressionResolver) -> Gradient? {
    guard let textGradient = textGradient else {
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

  private func makeCustomA11yElement(with expressionResolver: ExpressionResolver)
    -> AccessibilityElement? {
    guard accessibility.resolveDescription(expressionResolver) == nil else {
      return nil
    }
    return AccessibilityElement(
      traits: accessibility.type?.cast() ?? .none,
      strings: AccessibilityElement.Strings(
        label: resolveText(expressionResolver) as String?,
        hint: accessibility.resolveHint(expressionResolver),
        value: accessibility.resolveStateDescription(expressionResolver),
        identifier: id
      ),
      hideElementWithChildren: accessibility.resolveMode(expressionResolver).isExclude
    )
  }
}

extension CFMutableAttributedString {
  fileprivate func apply(
    _ actions: NonEmptyArray<UserInterfaceAction>?,
    at range: CFRange
  ) {
    if let actions = actions {
      ActionsAttribute(actions: actions.asArray()).apply(to: self, at: range)
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
      return uiLayoutDirection == .leftToRight ? .left : .right
    case .end:
      return uiLayoutDirection == .leftToRight ? .right : .left
    case .left:
      return .left
    case .center:
      return .center
    case .right:
      return .right
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
    withFactory factory: ImageHolderFactory,
    expressionResolver: ExpressionResolver
  ) -> TextBlock.InlineImage {
    TextBlock.InlineImage(
      size: CGSize(
        width: CGFloat(width.resolveValue(expressionResolver) ?? 0),
        height: CGFloat(height.resolveValue(expressionResolver) ?? 0)
      ),
      holder: factory.make(resolveUrl(expressionResolver)),
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
    case .none: return []
    case .single: return .single
    }
  }
}
