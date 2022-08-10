public struct Delay: ExpressibleByFloatLiteral, ExpressibleByIntegerLiteral, Equatable {
  public let value: Double

  public init(_ value: Double) {
    precondition(value >= 0, "Delay must be positive number or zero")
    self.value = value
  }

  public init(floatLiteral value: Double) {
    self.init(value)
  }

  public init(integerLiteral value: IntegerLiteralType) {
    self.init(Double(value))
  }
}
