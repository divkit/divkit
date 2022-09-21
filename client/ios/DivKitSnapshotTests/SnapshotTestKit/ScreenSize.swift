import CoreGraphics
import Foundation

public struct ScreenSize {
  public let size: CGSize
  public let scale: CGFloat

  public static let portrait = [
    ScreenSize(size: CGSize(width: 320, height: 480), scale: 2),
    ScreenSize(size: CGSize(width: 375, height: 667), scale: 2),
    ScreenSize(size: CGSize(width: 375, height: 812), scale: 3),
    ScreenSize(size: CGSize(width: 414, height: 896), scale: 2),
    ScreenSize(size: CGSize(width: 414, height: 736), scale: 3),
  ]
}

extension Array where Element == ScreenSize {
  public var uniqueWidths: [CGFloat] {
    map { $0.size.width }.uniqueElements
  }
}
