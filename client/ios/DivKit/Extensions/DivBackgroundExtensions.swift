import CoreGraphics
import Foundation
import LayoutKit
import VGSL

extension DivBackground {
  func makeBlockBackground(
    context: DivBlockModelingContext
  ) -> LayoutKit.Background? {
    let expressionResolver = context.expressionResolver
    switch self {
    case let .divLinearGradient(gradient):
      return gradient.makeBlockLinearGradient(context).map { .gradient(.linear($0)) }
    case let .divRadialGradient(gradient):
      return gradient.makeBlockRadialGradient(context).map { .gradient(.radial($0)) }
    case let .divImageBackground(imageBackground):
      let image = BackgroundImage(
        imageHolder: context.imageHolderFactory.make(
          imageBackground.resolveImageUrl(expressionResolver)
        ),
        contentMode: imageBackground.resolveContentMode(context),
        alpha: imageBackground.resolveAlpha(expressionResolver),
        effects: imageBackground.resolveEffects(expressionResolver)
      )
      return .image(image)
    case let .divSolidBackground(solidBackground):
      return solidBackground.resolveColor(expressionResolver).map { .solidColor($0) }
    case let .divNinePatchBackground(ninePatchBackground):
      let image = NinePatchImage(
        imageHolder: context.imageHolderFactory.make(
          ninePatchBackground.resolveImageUrl(expressionResolver)
        ),
        insets: ninePatchBackground.insets.resolve(expressionResolver)
      )
      return .ninePatchImage(image)
    }
  }
}

extension DivImageBackground: DivImageContentMode {}

extension DivImageBackground {
  fileprivate func resolveEffects(_ expressionResolver: ExpressionResolver) -> [ImageEffect] {
    filters?.compactMap { $0.resolveEffect(expressionResolver) } ?? []
  }
}
