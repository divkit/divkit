import CoreGraphics

import CommonCorePublic

public struct BlockAlignment2D: Equatable {
  public let horizontal: Alignment
  public let vertical: Alignment
  public static let `default` = BlockAlignment2D.topLeft
  public static let topLeft = BlockAlignment2D(horizontal: .leading, vertical: .leading)
  public static let topCenter = BlockAlignment2D(horizontal: .center, vertical: .leading)
  public static let topRight = BlockAlignment2D(horizontal: .trailing, vertical: .leading)
  public static let centerLeft = BlockAlignment2D(horizontal: .leading, vertical: .center)
  public static let center = BlockAlignment2D(horizontal: .center, vertical: .center)
  public static let centerRight = BlockAlignment2D(horizontal: .trailing, vertical: .center)
  public static let bottomLeft = BlockAlignment2D(horizontal: .leading, vertical: .trailing)
  public static let bottomCenter = BlockAlignment2D(horizontal: .center, vertical: .trailing)
  public static let bottomRight = BlockAlignment2D(horizontal: .trailing, vertical: .trailing)

  public init(
    horizontal: Alignment = .leading,
    vertical: Alignment = .leading
  ) {
    self.horizontal = horizontal
    self.vertical = vertical
  }
}

extension BlockAlignment2D {
  func offset(forAvailableSpace availableSpace: CGSize) -> CGPoint {
    CGPoint(
      x: horizontal.offset(forAvailableSpace: availableSpace.width),
      y: vertical.offset(forAvailableSpace: availableSpace.height)
    )
  }
}
