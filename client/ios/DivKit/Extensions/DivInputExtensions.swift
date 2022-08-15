import CoreFoundation
import CoreGraphics
import Foundation

import BaseUI
import CommonCore
import LayoutKit

extension DivInput: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actions: nil,
      actionAnimation: nil,
      doubleTapActions: nil,
      longTapActions: nil
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

    let alignment = resolveAlignmentHorizontal(context.expressionResolver)?.system ?? .left
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

    let hintText = resolveHintText(context.expressionResolver) ?? ""
    let attributedText = hintText.with(typo: typo)

    return TextInputBlock(
      widthTrait: makeContentWidthTrait(with: context.expressionResolver),
      heightTrait: makeContentHeightTrait(with: context.expressionResolver),
      text: attributedText
    )
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
