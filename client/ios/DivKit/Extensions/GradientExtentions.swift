import CoreGraphics
import Foundation
import VGSL

extension Gradient.Linear {
  init?(points: [Gradient.Point], angle: Int) {
    guard points.count >= 2 else { return nil }
    let sortedPoints = points.stableSort(isLessOrEqual: { $0.location <= $1.location })

    guard let start = sortedPoints.first, let end = sortedPoints.last
    else { return nil }

    self.init(
      startColor: start.color,
      intermediatePoints: sortedPoints,
      endColor: end.color,
      direction: .angle(Double(angle))
    )
  }

  init?(colors: [Color], angle: Int) {
    guard colors.count >= 2 else {
      return nil
    }
    var intermediateColors = colors
    let start = intermediateColors.removeFirst()
    let end = intermediateColors.removeLast()
    self.init(
      startColor: start,
      intermediateColors: intermediateColors,
      endColor: end,
      direction: .angle(Double(angle))
    )
  }
}

extension Gradient.Radial {
  init?(
    points: [Gradient.Point],
    end: Gradient.Radial.Radius,
    centerX: Gradient.Radial.CenterPoint,
    centerY: Gradient.Radial.CenterPoint
  ) {
    guard points.count >= 2 else { return nil }
    let sortedPoints = points.stableSort(isLessOrEqual: { $0.location <= $1.location })

    guard let first = sortedPoints.first, let last = sortedPoints.last
    else { return nil }

    self.init(
      centerX: centerX,
      centerY: centerY,
      end: end,
      centerColor: first.color,
      intermediatePoints: sortedPoints,
      outerColor: last.color
    )
  }

  init?(
    colors: [Color],
    end: Gradient.Radial.Radius,
    centerX: Gradient.Radial.CenterPoint,
    centerY: Gradient.Radial.CenterPoint
  ) {
    guard colors.count >= 2 else {
      return nil
    }
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

extension DivLinearGradient {
  func makeBlockLinearGradient(
    _ context: DivBlockModelingContext
  ) -> Gradient.Linear? {
    let expressionResolver = context.expressionResolver

    if let colorMap {
      let points: [Gradient.Point] = colorMap.compactMap { positionedColor -> Gradient.Point? in
        if let color = positionedColor.resolveColor(expressionResolver),
           let position = positionedColor.resolvePosition(expressionResolver) {
          return (color: color, location: CGFloat(position))
        }
        return nil
      }

      return Gradient.Linear(
        points: points,
        angle: resolveAngle(expressionResolver)
      )
    } else if let colors = resolveColors(expressionResolver) {
      return Gradient.Linear(
        colors: colors,
        angle: resolveAngle(expressionResolver)
      )
    } else {
      context.addError(
        message: "No colors specified in the linear gradient in the `colors` or `color_map` fields"
      )
      return nil
    }
  }
}

extension DivRadialGradient {
  func makeBlockRadialGradient(
    _ context: DivBlockModelingContext
  ) -> Gradient.Radial? {
    let expressionResolver = context.expressionResolver

    if let colorMap {
      let points: [Gradient.Point] = colorMap.compactMap { positionedColor -> Gradient.Point? in
        if let color = positionedColor.resolveColor(expressionResolver),
           let position = positionedColor.resolvePosition(expressionResolver) {
          return (color: color, location: CGFloat(position))
        }
        return nil
      }

      return Gradient.Radial(
        points: points,
        end: resolveRadius(expressionResolver),
        centerX: resolveCenterX(expressionResolver),
        centerY: resolveCenterY(expressionResolver)
      )
    } else if let colors = resolveColors(expressionResolver) {
      return Gradient.Radial(
        colors: colors,
        end: resolveRadius(expressionResolver),
        centerX: resolveCenterX(expressionResolver),
        centerY: resolveCenterY(expressionResolver)
      )
    } else {
      context.addError(
        message: "No colors specified in the radial gradient in the `colors` or `color_map` fields"
      )
      return nil
    }
  }

  func resolveRadius(_ resolver: ExpressionResolver) -> Gradient.Radial.Radius {
    switch radius {
    case let .divFixedSize(size):
      .absolute(size.resolveValue(resolver) ?? 0)
    case let .divRadialGradientRelativeRadius(radius):
      switch radius.resolveValue(resolver) ?? .farthestCorner {
      case .farthestCorner:
        .relativeToBorders(.farthestCorner)
      case .farthestSide:
        .relativeToBorders(.farthestSide)
      case .nearestCorner:
        .relativeToBorders(.nearestCorner)
      case .nearestSide:
        .relativeToBorders(.nearestSide)
      }
    }
  }

  func resolveCenterX(_ resolver: ExpressionResolver) -> Gradient.Radial.CenterPoint {
    switch centerX {
    case let .divRadialGradientRelativeCenter(x):
      .relative(x.resolveValue(resolver) ?? 0)
    case let .divRadialGradientFixedCenter(x):
      .absolute(x.resolveValue(resolver) ?? 0)
    }
  }

  func resolveCenterY(_ resolver: ExpressionResolver) -> Gradient.Radial.CenterPoint {
    switch centerY {
    case let .divRadialGradientRelativeCenter(y):
      .relative(y.resolveValue(resolver) ?? 0)
    case let .divRadialGradientFixedCenter(y):
      .absolute(y.resolveValue(resolver) ?? 0)
    }
  }
}
