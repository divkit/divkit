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
  ) -> LayoutKit.Background {
    switch self {
    case let .divGradientBackground(gradientBackground):
      return .gradient(
        .linear(.init(
          colors: gradientBackground.resolveColors(expressionResolver) ?? [],
          angle: gradientBackground.resolveAngle(expressionResolver)
        ))
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

extension Gradient.Linear {
  fileprivate init(colors: [Color], angle: Int) {
    var intermediateColors = colors
    let start = intermediateColors.removeFirst()
    let end = intermediateColors.removeLast()
    self.init(
      startColor: start,
      intermediateColors: intermediateColors,
      endColor: end,
      direction: .init(angle: angle)
    )
  }
}

extension Gradient.Linear.Direction {
  fileprivate init(angle: Int) {
    let alpha = Double(angle) / 180.0 * .pi
    let toRaw = CGPoint(x: cos(alpha), y: sin(alpha))
    let scale = 0.5 / max(abs(toRaw.x), abs(toRaw.y))
    let toNormalized = toRaw * scale
    let to = RelativePoint(
      x: toNormalized.x + 0.5,
      y: 1 - (toNormalized.y + 0.5)
    ) // 1 - a is to flip geometry
    let from = RelativePoint(
      x: -toNormalized.x + 0.5,
      y: 1 - (-toNormalized.y + 0.5)
    )
    self.init(from: from, to: to)
  }
}

extension DivImageBackground: DivImageContentMode {}
