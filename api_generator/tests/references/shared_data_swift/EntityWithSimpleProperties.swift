// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithSimpleProperties: EntityProtocol, Sendable {
  public static let type: String = "entity_with_simple_properties"
  public let boolean: Expression<Bool>?
  public let booleanInt: Expression<Bool>?
  public let color: Expression<Color>?
  public let double: Expression<Double>?
  public let id: Int // default value: 0
  public let integer: Expression<Int> // default value: 0
  public let positiveInteger: Expression<Int>? // constraint: number > 0
  public let string: Expression<String>?
  public let url: Expression<URL>?

  public func resolveBoolean(_ resolver: ExpressionResolver) -> Bool? {
    resolver.resolveNumeric(boolean)
  }

  public func resolveBooleanInt(_ resolver: ExpressionResolver) -> Bool? {
    resolver.resolveNumeric(booleanInt)
  }

  public func resolveColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveColor(color)
  }

  public func resolveDouble(_ resolver: ExpressionResolver) -> Double? {
    resolver.resolveNumeric(double)
  }

  public func resolveInteger(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(integer) ?? 0
  }

  public func resolvePositiveInteger(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(positiveInteger)
  }

  public func resolveString(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(string)
  }

  public func resolveUrl(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveUrl(url)
  }

  static let positiveIntegerValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 > 0 })

  init(
    boolean: Expression<Bool>? = nil,
    booleanInt: Expression<Bool>? = nil,
    color: Expression<Color>? = nil,
    double: Expression<Double>? = nil,
    id: Int? = nil,
    integer: Expression<Int>? = nil,
    positiveInteger: Expression<Int>? = nil,
    string: Expression<String>? = nil,
    url: Expression<URL>? = nil
  ) {
    self.boolean = boolean
    self.booleanInt = booleanInt
    self.color = color
    self.double = double
    self.id = id ?? 0
    self.integer = integer ?? .value(0)
    self.positiveInteger = positiveInteger
    self.string = string
    self.url = url
  }
}

#if DEBUG
extension EntityWithSimpleProperties: Equatable {
  public static func ==(lhs: EntityWithSimpleProperties, rhs: EntityWithSimpleProperties) -> Bool {
    guard
      lhs.boolean == rhs.boolean,
      lhs.booleanInt == rhs.booleanInt,
      lhs.color == rhs.color
    else {
      return false
    }
    guard
      lhs.double == rhs.double,
      lhs.id == rhs.id,
      lhs.integer == rhs.integer
    else {
      return false
    }
    guard
      lhs.positiveInteger == rhs.positiveInteger,
      lhs.string == rhs.string,
      lhs.url == rhs.url
    else {
      return false
    }
    return true
  }
}
#endif
