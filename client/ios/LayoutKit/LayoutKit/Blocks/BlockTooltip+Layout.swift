import CoreGraphics
import VGSL

extension BlockTooltip {
  public func calculateFrame(
    targeting targetRect: CGRect,
    constrainedBy bounds: CGRect
  ) -> CGRect {
    let size = block.intrinsicSize
    var result = CGRect(
      coordinate: targetRect.coordinate(of: position),
      ofPosition: position.opposite,
      size: size
    ).offset(by: offset)

    if result.intersection(bounds) == result {
      return result
    } else {
      result.move(to: bounds)
      return result
    }
  }
}

extension CGRect {
  fileprivate mutating func move(to bounds: CGRect) {
    for direction in MoveDirection.allCases {
      let (minPos, maxPos) = minAndMaxPos(direction)
      let (minBound, maxBound) = bounds.minAndMaxPos(direction)

      var newOrigin: CGFloat = origin(direction)

      if self.size(direction) > bounds.size(direction) {
        switch (minPos, maxPos) {
        case let (min, _) where min > minBound:
          newOrigin = minBound
        case let (_, max) where max < maxBound:
          newOrigin -= maxBound - max
        default:
          break
        }
      } else {
        switch (minPos, maxPos) {
        case let (min, _) where min < minBound:
          newOrigin = minBound
        case let (_, max) where max > maxBound:
          newOrigin -= max - maxBound
        default:
          break
        }
      }

      switch direction {
      case .horizontal:
        origin.x = newOrigin
      case .vertical:
        origin.y = newOrigin
      }
    }
  }

  private func origin(_ direction: MoveDirection) -> CGFloat {
    switch direction {
    case .horizontal:
      origin.x
    case .vertical:
      origin.y
    }
  }

  private func size(_ direction: MoveDirection) -> CGFloat {
    switch direction {
    case .horizontal:
      width
    case .vertical:
      height
    }
  }

  private func minAndMaxPos(
    _ direction: MoveDirection
  ) -> (min: CGFloat, max: CGFloat) {
    switch direction {
    case .horizontal:
      (minX, maxX)
    case .vertical:
      (minY, maxY)
    }
  }

  fileprivate typealias MoveDirection = ScrollDirection
}

extension CGRect {
  fileprivate func coordinate(of position: BlockTooltip.Position) -> CGPoint {
    switch position {
    case .left: CGPoint(x: minX, y: midY)
    case .topLeft: coordinate(ofCorner: .topLeft)
    case .top: CGPoint(x: midX, y: minY)
    case .topRight: coordinate(ofCorner: .topRight)
    case .right: CGPoint(x: maxX, y: midY)
    case .bottomRight: coordinate(ofCorner: .bottomRight)
    case .bottom: CGPoint(x: midX, y: maxY)
    case .bottomLeft: coordinate(ofCorner: .bottomLeft)
    case .center: CGPoint(x: midX, y: midY)
    }
  }
}

extension BlockTooltip.Position {
  fileprivate var opposite: Self {
    switch self {
    case .left: .right
    case .topLeft: .bottomRight
    case .top: .bottom
    case .topRight: .bottomLeft
    case .right: .left
    case .bottomRight: .topLeft
    case .bottom: .top
    case .bottomLeft: .topRight
    case .center: .center
    }
  }
}

extension CGRect {
  fileprivate init(
    coordinate: CGPoint,
    ofPosition position: BlockTooltip.Position,
    size: CGSize
  ) {
    let origin: CGPoint = switch position {
    case .left:
      coordinate.movingY(by: -0.5 * size.height)
    case .topLeft:
      coordinate
    case .top:
      coordinate.movingX(by: -0.5 * size.width)
    case .topRight:
      coordinate.movingX(by: -size.width)
    case .right:
      coordinate.movingX(by: -size.width).movingY(by: -0.5 * size.height)
    case .bottomRight:
      coordinate.movingX(by: -size.width).movingY(by: -size.height)
    case .bottom:
      coordinate.movingX(by: -0.5 * size.width).movingY(by: -size.height)
    case .bottomLeft:
      coordinate.movingY(by: -size.height)
    case .center:
      coordinate.movingX(by: -0.5 * size.width).movingY(by: -size.height / 2)
    }
    self.init(origin: origin, size: size)
  }
}
