// Copyright 2018 Yandex LLC. All rights reserved.

import CoreFoundation
import CoreGraphics
import Foundation

import Base
import BaseUI
import LayoutKit
import Networking

extension DivText: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actions: makeActions(context: context.actionContext),
      actionAnimation: actionAnimation.makeActionAnimation(with: context.expressionResolver),
      doubleTapActions: makeDoubleTapActions(context: context.actionContext),
      longTapActions: makeLongTapActions(context: context.actionContext),
      customA11yElement: makeCustomA11yElement(with: context.expressionResolver)
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    let font = context.fontSpecifiers.font(
      family: resolveFontFamily(context.expressionResolver).fontFamily,
      weight: resolveFontWeight(context.expressionResolver).fontWeight,
      size: resolveFontSizeUnit(context.expressionResolver)
        .makeScaledValue(resolveFontSize(context.expressionResolver))
    )
    var typo = Typo(font: font).allowHeightOverrun

    let alignment = resolveTextAlignmentHorizontal(context.expressionResolver).system
    if alignment != .left {
      typo = typo.with(alignment: alignment)
    }

    let kern = CGFloat(resolveLetterSpacing(context.expressionResolver))
    if !kern.isApproximatelyEqualTo(0) {
      typo = typo.kerned(kern)
    }

    let resolvedColor: Color = resolveTextColor(context.expressionResolver)

    if resolvedColor != .black {
      typo = typo.with(color: resolvedColor)
    }

    if let lineHeight = resolveLineHeight(context.expressionResolver) {
      typo = typo.with(height: CGFloat(lineHeight))
    }

    switch resolveStrike(context.expressionResolver) {
    case .none: break
    case .single: typo = typo.struckThrough(.single)
    }

    switch resolveUnderline(context.expressionResolver) {
    case .none: break
    case .single: typo = typo.underlined(.single)
    }

    let attributedString = makeAttributedString(
      text: resolveText(context.expressionResolver) ?? ("" as CFString),
      typo: typo,
      ranges: ranges,
      actions: nil,
      context: context
    )

    let images = (self.images ?? [])
      .filter {
        let start = $0.resolveStart(context.expressionResolver) ?? 0
        return start <= CFAttributedStringGetLength(attributedString)
      }
      .map { $0.makeImage(
        withFactory: context.imageHolderFactory,
        expressionResolver: context.expressionResolver
      ) }

    let truncationToken = ellipsis.map {
      makeAttributedString(
        text: ($0.resolveText(context.expressionResolver) ?? "") as CFString,
        typo: typo,
        ranges: $0.ranges,
        actions: $0.makeActions(context: context.actionContext),
        context: context
      )
    }

    return TextBlock(
      widthTrait: makeContentWidthTrait(with: context.expressionResolver),
      heightTrait: makeContentHeightTrait(with: context.expressionResolver),
      text: attributedString,
      verticalAlignment:
      resolveTextAlignmentVertical(context.expressionResolver).alignment,
      maxIntrinsicNumberOfLines: resolveMaxLines(context.expressionResolver) ?? .max,
      minNumberOfHiddenLines: resolveMinHiddenLines(context.expressionResolver) ?? 0,
      images: images,
      truncationToken: truncationToken,
      canSelect: resolveSelectable(context.expressionResolver) && context.flagsInfo
        .isTextSelectingEnabled
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

  private func apply(
    _ range: DivText.Range,
    to string: CFMutableAttributedString,
    context: DivBlockModelingContext
  ) {
    let start = range.resolveStart(context.expressionResolver) ?? 0
    let end = range.resolveEnd(context.expressionResolver) ?? 0
    guard end > start && start < CFAttributedStringGetLength(string) else {
      return
    }

    let fontTypo: Typo?
    if range.fontSize != nil || range.fontWeight != nil {
      let font = context.fontSpecifiers.font(
        family: range.resolveFontFamily(context.expressionResolver)?.fontFamily
          ?? resolveFontFamily(context.expressionResolver).fontFamily,
        weight: (
          range.resolveFontWeight(context.expressionResolver)
            ?? resolveFontWeight(context.expressionResolver)
        ).fontWeight,
        size: CGFloat(
          range.resolveFontSize(context.expressionResolver)
            ?? resolveFontSize(context.expressionResolver)
        )
      )
      fontTypo = Typo(font: font)
    } else {
      fontTypo = nil
    }
    let colorTypo = range.resolveTextColor(context.expressionResolver)
      .map { Typo(color: $0) }
    let heightTypo = range.resolveLineHeight(context.expressionResolver)
      .map { Typo(height: CGFloat($0)) }
    let spacingTypo = range.resolveLetterSpacing(context.expressionResolver)
      .map { Typo(kern: CGFloat($0)) }
    let strikethroughTypo = range.resolveStrike(context.expressionResolver)
      .map { Typo(strikethrough: $0.underlineStyle) }
    let underlineTypo = range.resolveUnderline(context.expressionResolver)
      .map { Typo(underline: $0.underlineStyle) }
    let typos = [fontTypo, colorTypo, heightTypo, spacingTypo, strikethroughTypo, underlineTypo]
      .compactMap { $0 }
    let actions = range.makeActions(context: context.actionContext)
    if typos.isEmpty, actions == nil {
      return
    }

    let actualEnd = min(end, CFAttributedStringGetLength(string))
    let cfRange = CFRange(location: start, length: actualEnd - start)
    typos.forEach { $0.apply(to: string, at: cfRange) }
    string.apply(actions, at: cfRange)
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
  fileprivate var system: TextAlignment {
    switch self {
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

extension DivLineStyle {
  fileprivate var underlineStyle: UnderlineStyle {
    switch self {
    case .none: return []
    case .single: return .single
    }
  }
}
