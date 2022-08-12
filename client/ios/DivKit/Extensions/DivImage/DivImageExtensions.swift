// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

import CommonCore
import LayoutKit

extension DivImage: DivBlockModeling, DivImageProtocol {
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
      resolveImageUrl(expressionResolver),
      resolvePlaceholder(expressionResolver)
    )
    return ImageBlock(
      imageHolder: imageHolder,
      widthTrait: makeContentWidthTrait(with: expressionResolver),
      height: resolveHeight(expressionResolver),
      contentMode: resolveContentMode(expressionResolver),
      tintColor: resolveTintColor(expressionResolver),
      appearanceAnimation: appearanceAnimation?.makeAppearanceAnimation(with: expressionResolver)
    )
  }
}
