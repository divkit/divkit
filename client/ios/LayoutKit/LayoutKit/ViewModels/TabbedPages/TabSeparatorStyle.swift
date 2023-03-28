import CoreGraphics

import CommonCorePublic

public struct TabSeparatorStyle: Equatable {
  public let color: Color
  public let insets: EdgeInsets

  public init(
    color: Color = Color(red: 0, green: 0, blue: 0, alpha: 0.08),
    insets: EdgeInsets = EdgeInsets(top: 0, left: 0, bottom: 16, right: 0)
  ) {
    self.color = color
    self.insets = insets
  }
}

extension TabSeparatorStyle: CustomDebugStringConvertible {
  public var debugDescription: String {
    """
    { color: \(color),
      insets: \(insets) }
    """
  }
}
