import CoreGraphics
import Foundation

import CommonCore

public struct BlockBorder: Equatable {
  public let color: Color
  public let width: CGFloat

  public init(
    color: Color,
    width: CGFloat = 1
  ) {
    self.color = color
    self.width = width
  }
}
