import CoreGraphics
import Foundation

import VGSL

import LayoutKit

extension DivBackground {
  func makeBlockBackground(
    context: DivBlockModelingContext
  ) -> LayoutKit.Background? {
    let expressionResolver = context.expressionResolver
    switch self {
    case let .divLinearGradient(gradient):
      return Gradient.Linear(
        colors: gradient.resolveColors(expressionResolver) ?? [],
        angle: gradient.resolveAngle(expressionResolver)
      ).map { .gradient(.linear($0)) }
    case let .divRadialGradient(gradient):
      return Gradient.Radial(
        colors: gradient.resolveColors(expressionResolver) ?? [],
        end: gradient.resolveRadius(expressionResolver),
        centerX: gradient.resolveCenterX(expressionResolver),
        centerY: gradient.resolveCenterY(expressionResolver)
      ).map { .gradient(.radial($0)) }
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
