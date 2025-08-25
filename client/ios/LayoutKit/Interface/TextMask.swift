import CoreGraphics
import Foundation

public struct TextMask {
  public enum MaskType: Equatable {
    case solid
    case particles(CGFloat, CGFloat)
  }

  public let type: MaskType
  public let color: CGColor
  public let range: CFRange

  public let isAnimated: Bool
  public let isEnabled: Bool

  public init(
    color: CGColor,
    range: CFRange,
    isEnabled: Bool
  ) {
    self.type = .solid
    self.color = color
    self.range = range
    self.isEnabled = isEnabled
    self.isAnimated = false
  }

  public init(
    color: CGColor,
    range: CFRange,
    isAnimated: Bool,
    particleSize: CGFloat,
    density: CGFloat,
    isEnabled: Bool
  ) {
    self.type = .particles(particleSize, density)
    self.color = color
    self.range = range
    self.isAnimated = isAnimated
    self.isEnabled = isEnabled
  }
}

extension TextMask: Equatable {
  public static func ==(lhs: TextMask, rhs: TextMask) -> Bool {
    lhs.type == rhs.type
      && lhs.color == rhs.color
      && lhs.range.length == rhs.range.length
      && lhs.range.location == rhs.range.location
      && lhs.isAnimated == rhs.isAnimated
      && lhs.isEnabled == rhs.isEnabled
  }
}
