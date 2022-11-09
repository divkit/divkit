import Foundation

import CommonCore
import Networking
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
    let highPriority = resolveHighPriorityPreviewShow(expressionResolver)
    let imageHolderFactory: ImageHolderFactory
    if highPriority, let highPriorityImageHolderFactory = context.highPriorityImageHolderFactory {
      imageHolderFactory = highPriorityImageHolderFactory
    } else {
      imageHolderFactory = context.imageHolderFactory
    }
    
    let imageHolder = imageHolderFactory.make(
      resolveImageUrl(expressionResolver),
      resolvePlaceholder(expressionResolver)
    )
    return ImageBlock(
      imageHolder: imageHolder,
      widthTrait: makeContentWidthTrait(with: context),
      height: resolveHeight(context),
      contentMode: resolveContentMode(expressionResolver),
      tintColor: resolveTintColor(expressionResolver),
      metalImageRenderingEnabled: context.flagsInfo.metalImageRenderingEnabled,
      appearanceAnimation: appearanceAnimation?.makeAppearanceAnimation(with: expressionResolver)
    )
  }
}
