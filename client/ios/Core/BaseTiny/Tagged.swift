// Copyright 2018 Yandex LLC. All rights reserved.

public struct Tagged<Tag, RawValue>: RawRepresentable {
  public var rawValue: RawValue

  public init(rawValue: RawValue) {
    self.rawValue = rawValue
  }
}

public protocol NumericTag {}

extension Tagged: Equatable where RawValue: Equatable {}

extension Tagged: Hashable where RawValue: Hashable {}

extension Tagged: Comparable where RawValue: Comparable {
  @inlinable
  public static func <(lhs: Tagged, rhs: Tagged) -> Bool {
    lhs.rawValue < rhs.rawValue
  }
}

extension Tagged: CustomStringConvertible {
  public var description: String {
    String(describing: rawValue)
  }
}

extension Tagged: CustomDebugStringConvertible {
  public var debugDescription: String {
    String(reflecting: rawValue)
  }
}

extension Tagged: Encodable where RawValue: Encodable {
  public func encode(to encoder: Encoder) throws {
    try rawValue.encode(to: encoder)
  }
}

extension Tagged: Decodable where RawValue: Decodable {
  public init(from decoder: Decoder) throws {
    self.init(rawValue: try .init(from: decoder))
  }
}

extension Tagged: ExpressibleByIntegerLiteral where RawValue: ExpressibleByIntegerLiteral {
  public typealias IntegerLiteralType = RawValue.IntegerLiteralType

  @inlinable
  public init(integerLiteral value: IntegerLiteralType) {
    self.init(rawValue: .init(integerLiteral: value))
  }
}

extension Tagged: ExpressibleByFloatLiteral where RawValue: ExpressibleByFloatLiteral {
  public typealias FloatLiteralType = RawValue.FloatLiteralType

  @inlinable
  public init(floatLiteral value: FloatLiteralType) {
    self.init(rawValue: .init(floatLiteral: value))
  }
}

extension Tagged: ExpressibleByBooleanLiteral where RawValue: ExpressibleByBooleanLiteral {
  public typealias BooleanLiteralType = RawValue.BooleanLiteralType

  @inlinable
  public init(booleanLiteral value: BooleanLiteralType) {
    self.init(rawValue: .init(booleanLiteral: value))
  }
}

extension Tagged: ExpressibleByNilLiteral where RawValue: ExpressibleByNilLiteral {
  public init(nilLiteral _: Void) {
    self.init(rawValue: nil)
  }
}

extension Tagged: ExpressibleByUnicodeScalarLiteral
  where RawValue: ExpressibleByUnicodeScalarLiteral {
  public typealias UnicodeScalarLiteralType = RawValue.UnicodeScalarLiteralType

  @inlinable
  public init(unicodeScalarLiteral value: UnicodeScalarLiteralType) {
    self.init(rawValue: .init(unicodeScalarLiteral: value))
  }
}

extension Tagged: ExpressibleByExtendedGraphemeClusterLiteral
  where RawValue: ExpressibleByExtendedGraphemeClusterLiteral {
  public typealias ExtendedGraphemeClusterLiteralType = RawValue.ExtendedGraphemeClusterLiteralType

  @inlinable
  public init(extendedGraphemeClusterLiteral value: ExtendedGraphemeClusterLiteralType) {
    self.init(rawValue: .init(extendedGraphemeClusterLiteral: value))
  }
}

extension Tagged: ExpressibleByStringLiteral where RawValue: ExpressibleByStringLiteral {
  public typealias StringLiteralType = RawValue.StringLiteralType

  @inlinable
  public init(stringLiteral value: StringLiteralType) {
    self.init(rawValue: .init(stringLiteral: value))
  }
}

extension Tagged {
  @inlinable
  public func cast<U>() -> Tagged<U, RawValue> {
    Tagged<U, RawValue>(rawValue: rawValue)
  }

  @inlinable
  public func cast() -> Tagged<Tag, RawValue> {
    assertionFailure("You don't need to cast value to same type")
    return self
  }
}

// Conforming to ExpressibleByArrayLiteral & ExpressibleByDictionaryLiteral is impossible
// due to https://bugs.swift.org/browse/SR-128

#if compiler(>=5)
extension Tagged: AdditiveArithmetic where Tag: NumericTag, RawValue: AdditiveArithmetic {
  @inlinable
  public static func +(lhs: Tagged, rhs: Tagged) -> Tagged {
    .init(rawValue: lhs.rawValue + rhs.rawValue)
  }

  @inlinable
  public static func +=(lhs: inout Tagged, rhs: Tagged) {
    lhs = lhs + rhs
  }

  @inlinable
  public static func -(lhs: Tagged, rhs: Tagged) -> Tagged {
    .init(rawValue: lhs.rawValue - rhs.rawValue)
  }

  @inlinable
  public static func -=(lhs: inout Tagged, rhs: Tagged) {
    lhs = lhs - rhs
  }

  @inlinable
  public static var zero: Tagged {
    .init(rawValue: RawValue.zero)
  }
}
#endif // compiler(>=5)

extension Tagged: Numeric where Tag: NumericTag, RawValue: Numeric {
  public typealias Magnitude = RawValue.Magnitude

  @inlinable
  public init?<T>(exactly source: T) where T: BinaryInteger {
    guard let value = RawValue(exactly: source) else {
      return nil
    }
    self.init(rawValue: value)
  }

  public var magnitude: Magnitude {
    rawValue.magnitude
  }

  #if compiler(>=5)
  #else
  @inlinable
  public static func +(lhs: Tagged, rhs: Tagged) -> Tagged {
    .init(rawValue: lhs.rawValue + rhs.rawValue)
  }

  @inlinable
  public static func +=(lhs: inout Tagged, rhs: Tagged) {
    lhs = lhs + rhs
  }

  @inlinable
  public static func -(lhs: Tagged, rhs: Tagged) -> Tagged {
    .init(rawValue: lhs.rawValue - rhs.rawValue)
  }

  @inlinable
  public static func -=(lhs: inout Tagged, rhs: Tagged) {
    lhs = lhs - rhs
  }
  #endif // compiler(>=5)

  @inlinable
  public static func *(lhs: Tagged, rhs: Tagged) -> Tagged {
    .init(rawValue: lhs.rawValue * rhs.rawValue)
  }

  @inlinable
  public static func *=(lhs: inout Tagged, rhs: Tagged) {
    lhs = lhs * rhs
  }
}
