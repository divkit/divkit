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

extension Gradient.Radial {
  fileprivate init(
    colors: [Color],
    end: Gradient.Radial.Radius,
    centerX: Gradient.Radial.CenterPoint,
    centerY: Gradient.Radial.CenterPoint
  ) {
    var intermediateColors = colors
    let centerColor = intermediateColors.removeFirst()
    let outerColor = intermediateColors.removeLast()
    let step = 1 / CGFloat(intermediateColors.count + 1)
    let points = intermediateColors.enumerated().map {
      (color: $0.element, location: CGFloat($0.offset + 1) * step)
    }
    self.init(
      centerX: centerX,
      centerY: centerY,
      end: end,
      centerColor: centerColor,
      intermediatePoints: points,
      outerColor: outerColor
    )
  }
}

extension DivRadialGradient {
  fileprivate func resolveRadius(_ resolver: ExpressionResolver) -> Gradient.Radial.Radius {
    switch radius {
    case let .divFixedSize(size):
      return .absolute(size.resolveValue(resolver) ?? 0)
    case let .divRadialGradientRelativeRadius(radius):
      switch radius.resolveValue(resolver) ?? .farthestCorner {
      case .farthestCorner:
        return .relativeToBorders(.farthestCorner)
      case .farthestSide:
        return .relativeToBorders(.farthestSide)
      case .nearestCorner:
        return .relativeToBorders(.nearestCorner)
      case .nearestSide:
        return .relativeToBorders(.nearestSide)
      }
    }
  }

  fileprivate func resolveCenterX(_ resolver: ExpressionResolver) -> Gradient.Radial.CenterPoint {
    switch (centerX) {
    case let (.divRadialGradientRelativeCenter(x)):
      return .relative(x.resolveValue(resolver) ?? 0)
    case let (.divRadialGradientFixedCenter(x)):
      return .absolute(x.resolveValue(resolver) ?? 0)
    }
  }

  fileprivate func resolveCenterY(_ resolver: ExpressionResolver) -> Gradient.Radial.CenterPoint {
    switch (centerY) {
    case let (.divRadialGradientRelativeCenter(y)):
      return .relative(y.resolveValue(resolver) ?? 0)
    case let (.divRadialGradientFixedCenter(y)):
      return .absolute(y.resolveValue(resolver) ?? 0)
    }
  }
}
