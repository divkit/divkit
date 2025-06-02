import UIKit
import VGSL

public struct TabTitleDelimiterStyle: Equatable {
  public let imageHolder: ImageHolder
  public let width: CGFloat
  public let height: CGFloat

  public init(imageHolder: ImageHolder, width: CGFloat, height: CGFloat) {
    self.imageHolder = imageHolder
    self.width = width
    self.height = height
  }

  public static func ==(lhs: TabTitleDelimiterStyle, rhs: TabTitleDelimiterStyle) -> Bool {
    lhs.imageHolder.equals(rhs.imageHolder) &&
      lhs.width == rhs.width &&
      lhs.height == rhs.height
  }
}
