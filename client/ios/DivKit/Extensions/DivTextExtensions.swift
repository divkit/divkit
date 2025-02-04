import CoreFoundation
import Foundation
import LayoutKit
import VGSL

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

    let fontParams = resolveFontParams(expressionResolver)
    var typo = Typo(font: context.fontProvider.font(fontParams)).allowHeightOverrun

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
    if let textShadow = textShadow?.resolve(expressionResolver) {
      typo = typo.shaded(textShadow.typoShadow)
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

    let additionalTextInsets = additionalTextInsets(context: context)

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
      additionalTextInsets: additionalTextInsets,
      canSelect: resolveSelectable(expressionResolver),
      tightenWidth: resolveTightenWidth(expressionResolver)
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

    let stringLength = CFAttributedStringGetLength(string)
    let start = range.resolveStart(expressionResolver)
    let end = range.resolveEnd(expressionResolver) ?? stringLength
    guard end > start, start < stringLength else {
      return
    }

    let rangeFontParams = FontParams(
      family: range.resolveFontFamily(expressionResolver) ?? fontParams.family,
      weight: range.resolveFontWeightValue(expressionResolver)
        ?? range.resolveFontWeight(expressionResolver)?.toInt()
        ?? fontParams.weight,
      size: range.resolveFontSize(expressionResolver) ?? fontParams.size,
      unit: range.resolveFontSizeUnit(expressionResolver),
      featureSettings: range.resolveFontFeatureSettings(expressionResolver)
        ?? fontParams.featureSettings
    )
    let fontTypo = rangeFontParams == fontParams
      ? nil
      : Typo(font: context.fontProvider.font(rangeFontParams))
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
    let baselineOffsetTypo = Typo(
      baselineOffset: range.resolveBaselineOffset(expressionResolver)
    )
    let blockShadow = range.textShadow?.resolve(expressionResolver).typoShadow
    let shadowTypo = blockShadow.map { Typo(shadow: $0) }
    let typos = [
      fontTypo,
      colorTypo,
      heightTypo,
      spacingTypo,
      strikethroughTypo,
      underlineTypo,
      baselineOffsetTypo,
      shadowTypo,
    ].compactMap { $0 }
    let actions = range.actions?.uiActions(context: context)
    if typos.isEmpty, actions == nil, range.background == nil, range.border == nil {
      return
    }

    let actualEnd = min(end, stringLength)
    let cfRange = CFRange(location: start, length: actualEnd - start)
    typos.forEach { $0.apply(to: string, at: cfRange) }
    string.apply(actions, at: cfRange)

    [
      range.makeBackground(context: context, range: cfRange),
      range.makeBorder(range: cfRange, resolver: expressionResolver),
    ].forEach {
      $0?.apply(to: string, at: cfRange)
    }
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

  private func additionalTextInsets(
    context: DivBlockModelingContext
  ) -> EdgeInsets {
    var additionalInsets: EdgeInsets = .zero
    ranges?.forEach {
      if case let .divCloudBackground(background) = $0.background {
        let backgroundInsets = background.paddings?.resolve(context) ?? .zero
        additionalInsets = EdgeInsets(
          top: max(additionalInsets.top, backgroundInsets.top),

          left: max(additionalInsets.left, backgroundInsets.left),
          bottom: max(additionalInsets.bottom, backgroundInsets.bottom),
          right: max(additionalInsets.right, backgroundInsets.right)
        )
      }
    }
    return additionalInsets
  }
}

extension DivText: FontParamsProvider {}

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
    context: DivBlockModelingContext,
    range: CFRange
  ) -> StringAttribute? {
    guard let background else { return nil }
    let resolver = context.expressionResolver
    switch background {
    case let .divSolidBackground(solid):
      let color = solid.resolveColor(resolver) ?? .clear
      return BackgroundAttribute(color: color.cgColor, range: range)
    case let .divCloudBackground(cloud):
      guard let color = cloud.resolveColor(resolver),
            let cornerRadius = cloud.resolveCornerRadius(resolver)
      else { return nil }
      let insets = cloud.paddings?.resolve(context)
      return CloudBackgroundAttribute(
        color: color,
        cornerRadius: CGFloat(cornerRadius),
        range: range.location..<(range.location + range.length),
        insets: insets
      )
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

extension BlockShadow {
  fileprivate var typoShadow: Shadow {
    Shadow(
      offset: CGSize(width: offset.x, height: -offset.y),
      blurRadius: blurRadius,
      color: color.withAlphaComponent(CGFloat(opacity))
    )
  }
}
