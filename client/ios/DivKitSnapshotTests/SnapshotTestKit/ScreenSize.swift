import CoreGraphics
import Foundation

public struct ScreenSize {
  public let size: CGSize
  public let scale: CGFloat

  public static let portrait = [
    ScreenSize(size: CGSize(width: 375, height: 667), scale: 2),
    ScreenSize(size: CGSize(width: 414, height: 736), scale: 3),
  ]
}
