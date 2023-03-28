import CoreGraphics

import CommonCorePublic

extension BlockTooltip {
  public func calculateFrame(targeting targetRect: CGRect) -> CGRect {
    CGRect(
      coordinate: targetRect.coordinate(of: position),
      ofPosition: position.opposite,
      size: block.intrinsicSize
    ).offset(by: offset)
  }
}

extension CGRect {
  fileprivate func coordinate(of position: BlockTooltip.Position) -> CGPoint {
    switch position {
    case .left: return CGPoint(x: minX, y: midY)
    case .topLeft: return coordinate(ofCorner: .topLeft)
    case .top: return CGPoint(x: midX, y: minY)
    case .topRight: return coordinate(ofCorner: .topRight)
    case .right: return CGPoint(x: maxX, y: midY)
    case .bottomRight: return coordinate(ofCorner: .bottomRight)
    case .bottom: return CGPoint(x: midX, y: maxY)
    case .bottomLeft: return coordinate(ofCorner: .bottomLeft)
    }
  }
}

extension BlockTooltip.Position {
  fileprivate var opposite: Self {
    switch self {
    case .left: return .right
    case .topLeft: return .bottomRight
    case .top: return .bottom
    case .topRight: return .bottomLeft
    case .right: return .left
    case .bottomRight: return .topLeft
    case .bottom: return .top
    case .bottomLeft: return .topRight
    }
  }
}

extension CGRect {
  fileprivate init(
    coordinate: CGPoint,
    ofPosition position: BlockTooltip.Position,
    size: CGSize
  ) {
    let origin: CGPoint
    switch position {
    case .left:
      origin = coordinate.movingY(by: -0.5 * size.height)
    case .topLeft:
      origin = coordinate
    case .top:
      origin = coordinate.movingX(by: -0.5 * size.width)
    case .topRight:
      origin = coordinate.movingX(by: -size.width)
    case .right:
      origin = coordinate.movingX(by: -size.width).movingY(by: -0.5 * size.height)
    case .bottomRight:
      origin = coordinate.movingX(by: -size.width).movingY(by: -size.height)
    case .bottom:
      origin = coordinate.movingX(by: -0.5 * size.width).movingY(by: -size.height)
    case .bottomLeft:
      origin = coordinate.movingY(by: -size.height)
    }
    self.init(origin: origin, size: size)
  }
}
