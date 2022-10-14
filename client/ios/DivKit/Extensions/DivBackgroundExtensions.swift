import CoreGraphics
import Foundation

import Base
import BaseUI
import LayoutKit
import Networking

extension DivBackground {
  func makeBlockBackground(
    with imageHolderFactory: ImageHolderFactory,
    expressionResolver: ExpressionResolver
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
        alpha: imageBackground.resolveAlpha(expressionResolver)
      )
      return .image(image)
    case let .divSolidBackground(solidBackground):
      return .solidColor(solidBackground.resolveColor(expressionResolver) ?? .black)
    }
  }
}

extension DivImageBackground: DivImageContentMode {}
