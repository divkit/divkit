import CoreGraphics

import VGSL

extension BlockTooltip {
  public func calculateFrame(
    targeting targetRect: CGRect,
    constrainedBy bounds: CGRect
  ) -> CGRect {
    let size = block.intrinsicSize
    let result = CGRect(
      coordinate: targetRect.coordinate(of: position),
      ofPosition: position.opposite,
      size: size
    ).offset(by: offset)

    if result.intersection(bounds) == result {
      return result
    }

    var alternativePosition = position
    if result.maxY > bounds.maxY {
      alternativePosition = alternativePosition.movingUp
    }
    if result.minY < bounds.minY {
      alternativePosition = alternativePosition.movingDown
    }
    if result.minX < bounds.minX {
      alternativePosition = alternativePosition.movingRight
    }
    if result.maxX > bounds.maxX {
      alternativePosition = alternativePosition.movingLeft
    }

    let alternativeFrame = CGRect(
      coordinate: targetRect.coordinate(of: alternativePosition),
      ofPosition: alternativePosition.opposite,
      size: size
    ).offset(by: offset)
    return alternativeFrame.intersection(bounds) == alternativeFrame
      ? alternativeFrame
      : result
  }
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

  fileprivate var movingUp: Self {
    switch self {
    case .topLeft, .left, .bottomLeft: .topLeft
    case .top, .center, .bottom: .top
    case .topRight, .right, .bottomRight: .topRight
    }
  }

  fileprivate var movingDown: Self {
    switch self {
    case .topLeft, .left, .bottomLeft: .bottomLeft
    case .top, .center, .bottom: .bottom
    case .topRight, .right, .bottomRight: .bottomRight
    }
  }

  fileprivate var movingLeft: Self {
    switch self {
    case .topLeft, .top, .topRight: .topLeft
    case .left, .center, .right: .left
    case .bottomLeft, .bottom, .bottomRight: .bottomLeft
    }
  }

  fileprivate var movingRight: Self {
    switch self {
    case .topLeft, .top, .topRight: .topRight
    case .left, .center, .right: .right
    case .bottomLeft, .bottom, .bottomRight: .bottomRight
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
