import Foundation

import LayoutKit
import VGSL

extension DivGifImage: DivBlockModeling, DivImageProtocol {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actionsHolder: self,
      customAccessibilityParams: CustomAccessibilityParams(defaultTraits: .image)
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
      widthTrait: resolveContentWidthTrait(context),
      height: resolveHeight(context),
      contentMode: resolveContentMode(context)
    )
  }
}
