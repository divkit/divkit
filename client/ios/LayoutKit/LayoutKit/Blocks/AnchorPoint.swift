import CoreGraphics

public struct AnchorPoint: Equatable {
  let x, y: AnchorValue
  public init(x: AnchorValue, y: AnchorValue) {
    self.x = x
    self.y = y
  }

  public func calculateCGPoint(for rect: CGRect) -> CGPoint {
    CGPoint(x: x.value(for: rect.width), y: y.value(for: rect.height))
  }
}

public enum AnchorValue: Equatable {
  case relative(value: CGFloat)
  case absolute(value: CGFloat)

  public func value(for length: CGFloat) -> CGFloat {
    switch self {
    case let .absolute(value):
      return value / (length == 0 ? .infinity : length)
    case let .relative(value):
      return value / 100
    }
  }
}
