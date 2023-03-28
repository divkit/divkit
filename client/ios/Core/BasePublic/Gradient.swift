// Copyright 2016 Yandex LLC. All rights reserved.

import CoreGraphics

import BaseUIPublic

@frozen
public enum Gradient: Equatable {
  public typealias Point = (color: Color, location: CGFloat)

  public struct Linear: Equatable {
    public struct Direction: Equatable {
      public let from: RelativePoint
      public let to: RelativePoint

      public static let vertical = Direction(from: .midTop, to: .midBottom)
      public static let horizontal = Direction(from: .midLeft, to: .midRight)

      // ↘︎
      public static let mainDiagonal = Direction(from: .topLeft, to: .bottomRight)
      // ↙︎
      public static let antidiagonal = Direction(from: .topRight, to: .bottomLeft)

      public init(from: RelativePoint, to: RelativePoint) {
        self.from = from
        self.to = to
      }

      public static func ==(lhs: Direction, rhs: Direction) -> Bool {
        lhs.from.isApproximatelyEqualTo(rhs.from)
          && lhs.to.isApproximatelyEqualTo(rhs.to)
      }
    }

    public let startColor: Color
    public let intermediatePoints: [Point]
    public let endColor: Color
    public let direction: Direction

    public init(
      startColor: Color,
      intermediatePoints: [Point],
      endColor: Color,
      direction: Direction
    ) {
      let positions = intermediatePoints.map { $0.location }
      assert(positions == positions.sorted())

      self.startColor = startColor
      self.intermediatePoints = intermediatePoints
      self.endColor = endColor
      self.direction = direction
    }
  }

  public struct Radial: Equatable {
    public let centerX: CenterPoint
    public let centerY: CenterPoint
    public let end: Radius
    public let centerColor: Color
    public let intermediatePoints: [Point]
    public let outerColor: Color
    public let shape: Shape

    public init(
      center: RelativePoint = .mid,
      end: RelativePoint? = nil,
      centerColor: Color,
      intermediatePoints: [Point] = [],
      outerColor: Color? = nil,
      shape: Shape? = nil
    ) {
      let positions = intermediatePoints.map { $0.location }
      assert(positions == positions.sorted())

      self.centerX = .relative(center.x)
      self.centerY = .relative(center.y)
      self.end = .relativeToSize(end ?? RelativeRect.full.radialEndPoint(for: center))
      self.centerColor = centerColor
      self.intermediatePoints = intermediatePoints
      self.outerColor = outerColor ?? centerColor.withAlphaComponent(0)
      self.shape = shape ?? .ellipse
    }

    public init(
      centerX: CenterPoint,
      centerY: CenterPoint,
      end: Radius,
      centerColor: Color,
      intermediatePoints: [Point] = [],
      outerColor: Color? = nil
    ) {
      let positions = intermediatePoints.map { $0.location }
      assert(positions == positions.sorted())

      self.centerX = centerX
      self.centerY = centerY
      self.end = end
      self.centerColor = centerColor
      self.intermediatePoints = intermediatePoints
      self.outerColor = outerColor ?? centerColor.withAlphaComponent(0)
      self.shape = .circle
    }

    public enum CenterPoint: Equatable {
      case relative(CGFloat)
      case absolute(Int)
    }

    public enum Radius: Equatable {
      case relativeToBorders(RelativeToBorder)
      case relativeToSize(RelativePoint)
      case absolute(Int)

      public enum RelativeToBorder {
        case nearestCorner
        case farthestCorner
        case nearestSide
        case farthestSide
      }
    }

    public enum Shape {
      case circle
      case ellipse
    }
  }

  case radial(Radial)
  case box(Color)
  case linear(Linear)
}

extension Gradient.Linear {
  public static func ==(lhs: Gradient.Linear, rhs: Gradient.Linear) -> Bool {
    lhs.startColor == rhs.startColor &&
      lhs.intermediatePoints == rhs.intermediatePoints &&
      lhs.endColor == rhs.endColor &&
      lhs.direction == rhs.direction
  }

  public init(
    startColor: Color,
    intermediateColors: [Color] = [],
    endColor: Color,
    direction: Direction
  ) {
    let step = 1 / CGFloat(intermediateColors.count + 1)
    let points = intermediateColors.enumerated().map {
      (color: $0.element, location: CGFloat($0.offset + 1) * step)
    }
    self.init(
      startColor: startColor,
      intermediatePoints: points,
      endColor: endColor,
      direction: direction
    )
  }
}

extension Gradient.Radial {
  public static func ==(lhs: Gradient.Radial, rhs: Gradient.Radial) -> Bool {
    lhs.centerX == rhs.centerX
      && lhs.centerY == rhs.centerY
      && lhs.end == rhs.end
      && lhs.centerColor == rhs.centerColor
      && lhs.intermediatePoints == rhs.intermediatePoints
      && lhs.outerColor == rhs.outerColor
  }
}

extension Gradient.Linear.Direction: CustomDebugStringConvertible {
  public var debugDescription: String {
    if self == .vertical {
      return "↓"
    } else if self == .horizontal {
      return "⟶"
    } else if self == .mainDiagonal {
      return "↘︎"
    } else if self == .antidiagonal {
      return "↙︎"
    }

    let step: CGFloat = 10e-3
    return "(\(from.x.rounded(toStep: step)), \(from.y.rounded(toStep: step))) ⟶ (\(to.x.rounded(toStep: step)), \(to.y.rounded(toStep: step)))"
  }
}

extension Gradient.Linear: CustomDebugStringConvertible {
  public var debugDescription: String {
    var path: [String] = []
    path.append(startColor.debugDescription)
    path += intermediatePoints.map { "(\($0.location):\($0.color.debugDescription))" }
    path.append(endColor.debugDescription)
    return "Linear \(direction), \(path.joined(separator: ".."))"
  }
}

extension Gradient.Radial: CustomDebugStringConvertible {
  public var debugDescription: String {
    var path: [String] = []
    path.append(centerColor.debugDescription)
    path += intermediatePoints.map { "(\($0.location):\($0.color.debugDescription)" }
    path.append(outerColor.debugDescription)
    return "Radial c: \(centerX), \(centerY), \(path.joined(separator: ".."))"
  }
}

extension Gradient: CustomDebugStringConvertible {
  public var debugDescription: String {
    switch self {
    case let .box(color):
      return "Box \(color)"
    case let .radial(radial):
      return radial.debugDescription
    case let .linear(linear):
      return linear.debugDescription
    }
  }
}

extension RelativeRect {
  fileprivate func radialEndPoint(for center: RelativePoint) -> RelativePoint {
    RelativePoint(
      x: abs(center.x - minX) > abs(center.x - maxX) ? minX : maxX,
      y: abs(center.y - minY) > abs(center.y - maxY) ? minY : maxY
    )
  }
}

extension Gradient.Linear {
  public var colors: [Color] {
    let intermediateColors = intermediatePoints.map { $0.color }
    return [startColor] + intermediateColors + [endColor]
  }

  public var locations: [CGFloat] {
    [0] + intermediatePoints.map { $0.location } + [1]
  }
}
