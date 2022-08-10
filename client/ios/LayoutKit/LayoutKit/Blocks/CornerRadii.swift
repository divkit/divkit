import CoreGraphics

public struct CornerRadii: Equatable {
  public let topLeft: CGFloat
  public let topRight: CGFloat
  public let bottomLeft: CGFloat
  public let bottomRight: CGFloat

  public init(
    topLeft: CGFloat,
    topRight: CGFloat,
    bottomLeft: CGFloat,
    bottomRight: CGFloat
  ) {
    self.topLeft = topLeft
    self.topRight = topRight
    self.bottomLeft = bottomLeft
    self.bottomRight = bottomRight
  }
}

extension CornerRadii: ExpressibleByFloatLiteral {
  public init(floatLiteral value: CGFloat.NativeType) {
    self.init(CGFloat(value))
  }

  public init(_ value: CGFloat) {
    self.init(
      topLeft: value,
      topRight: value,
      bottomLeft: value,
      bottomRight: value
    )
  }
}
