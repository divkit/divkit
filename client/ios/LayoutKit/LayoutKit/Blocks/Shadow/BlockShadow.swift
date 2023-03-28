import CoreGraphics

import CommonCorePublic

public struct BlockShadow: Equatable {
  public enum Defaults {
    public static let blurRadius: CGFloat = 1
    public static let offset = CGPoint(x: 0, y: 1)
    public static let opacity: Float = 0.19
    public static let color: Color = .black
  }

  public static let maxBlurRadius: CGFloat = 20
  public static let maxOffset: CGFloat = 8

  public let cornerRadii: CornerRadii
  public let blurRadius: CGFloat
  public let offset: CGPoint
  public let opacity: Float
  public let color: Color

  public init(
    cornerRadii: CornerRadii,
    blurRadius: CGFloat = Defaults.blurRadius,
    offset: CGPoint = Defaults.offset,
    opacity: Float = Defaults.opacity,
    color: Color = Defaults.color
  ) {
    self.cornerRadii = cornerRadii
    self.blurRadius = blurRadius
    self.offset = offset
    self.opacity = opacity
    self.color = color
  }

  public static func ==(lhs: BlockShadow, rhs: BlockShadow) -> Bool {
    lhs.cornerRadii == rhs.cornerRadii &&
      lhs.blurRadius == rhs.blurRadius &&
      lhs.offset == rhs.offset &&
      lhs.opacity == rhs.opacity &&
      lhs.color == rhs.color
  }
}

extension BlockShadow {
  public init(
    cornerRadius: CGFloat,
    blurRadius: CGFloat = Defaults.blurRadius,
    offset: CGPoint = Defaults.offset,
    opacity: Float = Defaults.opacity,
    color: Color = Defaults.color
  ) {
    self.init(
      cornerRadii: CornerRadii(cornerRadius),
      blurRadius: blurRadius,
      offset: offset,
      opacity: opacity,
      color: color
    )
  }
}
