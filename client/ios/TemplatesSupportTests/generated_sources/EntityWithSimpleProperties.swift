// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class EntityWithSimpleProperties: EntityProtocol {
  public static let type: String = "entity_with_simple_properties"
  public let boolean: Bool?
  public let color: Color?
  public let double: Double?
  public let id: Int?
  public let integer: Int?
  public let positiveInteger: Int? // constraint: number > 0
  public let string: String? // at least 1 char
  public let url: URL?

  static let booleanValidator: AnyValueValidator<Bool> =
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
    boolean: Bool? = nil,
    color: Color? = nil,
    double: Double? = nil,
    id: Int? = nil,
    integer: Int? = nil,
    positiveInteger: Int? = nil,
    string: String? = nil,
    url: URL? = nil
  ) {
    self.boolean = boolean
    self.color = color
    self.double = double
    self.id = id
    self.integer = integer
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
      lhs.color == rhs.color,
      lhs.double == rhs.double
    else {
      return false
    }
    guard
      lhs.id == rhs.id,
      lhs.integer == rhs.integer,
      lhs.positiveInteger == rhs.positiveInteger
    else {
      return false
    }
    guard
      lhs.string == rhs.string,
      lhs.url == rhs.url
    else {
      return false
    }
    return true
  }
}
#endif
