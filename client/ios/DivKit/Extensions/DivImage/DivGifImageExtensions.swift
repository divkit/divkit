import Foundation
import LayoutKit
import VGSL

extension DivGifImage: DivBlockModeling, DivImageProtocol {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    let context = modifiedContextParentPath(context)
    return try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actionsHolder: self,
      customAccessibilityParams: CustomAccessibilityParams(defaultTraits: .image)
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    let expressionResolver = context.expressionResolver
    let imageHolder = context.imageHolderFactory.make(
      resolveGifUrl(expressionResolver),
      resolvePlaceholder(expressionResolver)
    )
    let widthTrait = resolveContentWidthTrait(context)
    let height = resolveHeight(context)

    return AnimatableImageBlock(
      imageHolder: imageHolder,
      widthTrait: widthTrait,
      height: height,
      contentMode: resolveContentMode(context),
      path: context.path
    )
  }
}
