import CoreGraphics
import Foundation

import BasePublic
import BaseUIPublic
import LayoutKit
import NetworkingPublic

extension DivBackground {
  func makeBlockBackground(
    with imageHolderFactory: ImageHolderFactory,
    expressionResolver: ExpressionResolver,
    metalImageRenderingEnabled: Bool
  ) -> LayoutKit.Background? {
    switch self {
    case let .divLinearGradient(gradient):
      return .gradient(
        .linear(
          Gradient.Linear(
            colors: gradient.resolveColors(expressionResolver) ?? [],
            angle: gradient.resolveAngle(expressionResolver)
          )
        )
      )
    case let .divRadialGradient(gradient):
      return .gradient(
        .radial(
          Gradient.Radial(
            colors: gradient.resolveColors(expressionResolver) ?? [],
            end: gradient.resolveRadius(expressionResolver),
            centerX: gradient.resolveCenterX(expressionResolver),
            centerY: gradient.resolveCenterY(expressionResolver)
          )
        )
      )
    case let .divImageBackground(imageBackground):
      let image = BackgroundImage(
        imageHolder: imageHolderFactory.make(
          imageBackground.resolveImageUrl(expressionResolver)
        ),
        contentMode: imageBackground.resolveContentMode(expressionResolver),
        alpha: imageBackground.resolveAlpha(expressionResolver),
        effects: imageBackground.makeEffects(with: expressionResolver),
        metalImageRenderingEnabled: metalImageRenderingEnabled
      )
      return .image(image)
    case let .divSolidBackground(solidBackground):
      return .solidColor(solidBackground.resolveColor(expressionResolver) ?? .black)
    case let .divNinePatchBackground(ninePatchBackground):
      let image = NinePatchImage(
        imageHolder: imageHolderFactory.make(
          ninePatchBackground.resolveImageUrl(expressionResolver)
        ),
        insets: ninePatchBackground.insets.makeEdgeInsets(with: expressionResolver)
      )
      return .ninePatchImage(image)
    }
  }
}

extension DivImageBackground: DivImageContentMode {}

extension DivImageBackground {
  fileprivate func makeEffects(with resolver: ExpressionResolver) -> [ImageEffect] {
    filters?.compactMap { $0.makeImageEffect(with: resolver) } ?? []
  }
}
