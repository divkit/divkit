import Foundation

import CommonCorePublic
import LayoutKit

extension DivGifImage: DivBlockModeling, DivImageProtocol {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actions: makeActions(context: context.actionContext),
      actionAnimation: actionAnimation.makeActionAnimation(with: context.expressionResolver),
      doubleTapActions: makeDoubleTapActions(context: context.actionContext),
      longTapActions: makeLongTapActions(context: context.actionContext)
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    try checkLayoutTraits(context: context)

    let expressionResolver = context.expressionResolver
    let imageHolder = context.imageHolderFactory.make(
      resolveGifUrl(expressionResolver),
      resolvePlaceholder(expressionResolver)
    )
    return AnimatableImageBlock(
      imageHolder: imageHolder,
      widthTrait: makeContentWidthTrait(with: context),
      height: resolveHeight(context),
      contentMode: resolveContentMode(expressionResolver)
    )
  }
}
