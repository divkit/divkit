// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithSimpleProperties: EntityProtocol {
  public static let type: String = "entity_with_simple_properties"
  public let boolean: Expression<Bool>?
  public let booleanInt: Expression<Bool>?
  public let color: Expression<Color>?
  public let double: Expression<Double>?
  public let id: Int // default value: 0
  public let integer: Expression<Int> // default value: 0
  public let positiveInteger: Expression<Int>? // constraint: number > 0
  public let string: Expression<String>? // at least 1 char
  public let url: Expression<URL>?

  public func resolveBoolean(_ resolver: ExpressionResolver) -> Bool? {
    resolver.resolveNumericValue(expression: boolean)
  }

  public func resolveBooleanInt(_ resolver: ExpressionResolver) -> Bool? {
    resolver.resolveNumericValue(expression: booleanInt)
  }

  public func resolveColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveStringBasedValue(expression: color, initializer: Color.color(withHexString:))
  }

  public func resolveDouble(_ resolver: ExpressionResolver) -> Double? {
    resolver.resolveNumericValue(expression: double)
  }

  public func resolveInteger(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: integer) ?? 0
  }

  public func resolvePositiveInteger(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: positiveInteger)
  }

  public func resolveString(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveStringBasedValue(expression: string, initializer: { $0 })
  }

  public func resolveUrl(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveStringBasedValue(expression: url, initializer: URL.init(string:))
  }

  static let booleanValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let booleanIntValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let colorValidator: AnyValueValidator<Color> =
    makeNoOpValueValidator()

  static let positiveIntegerValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 > 0 })

  static let stringValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let urlValidator: AnyValueValidator<URL> =
    makeNoOpValueValidator()

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

extension EntityWithSimpleProperties: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["boolean"] = boolean?.toValidSerializationValue()
    result["boolean_int"] = booleanInt?.toValidSerializationValue()
    result["color"] = color?.toValidSerializationValue()
    result["double"] = double?.toValidSerializationValue()
    result["id"] = id
    result["integer"] = integer.toValidSerializationValue()
    result["positive_integer"] = positiveInteger?.toValidSerializationValue()
    result["string"] = string?.toValidSerializationValue()
    result["url"] = url?.toValidSerializationValue()
    return result
  }
}
