// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class EntityWithPropertyWithDefaultValue {
  public final class Nested {
    public let int: Int // constraint: number >= 0; default value: 0
    public let nonOptional: String
    public let url: URL // valid schemes: [https]; default value: https://yandex.ru

    static let intValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let urlValidator: AnyValueValidator<URL> =
      makeURLValidator(schemes: ["https"])

    init(
      int: Int? = nil,
      nonOptional: String,
      url: URL? = nil
    ) {
      self.int = int ?? 0
      self.nonOptional = nonOptional
      self.url = url ?? URL(string: "https://yandex.ru")!
    }
  }

  public static let type: String = "entity_with_property_with_default_value"
  public let int: Int // constraint: number >= 0; default value: 0
  public let nested: Nested?
  public let url: URL // valid schemes: [https]; default value: https://yandex.ru

  static let intValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let nestedValidator: AnyValueValidator<EntityWithPropertyWithDefaultValue.Nested> =
    makeNoOpValueValidator()

  static let urlValidator: AnyValueValidator<URL> =
    makeURLValidator(schemes: ["https"])

  init(
    int: Int? = nil,
    nested: Nested? = nil,
    url: URL? = nil
  ) {
    self.int = int ?? 0
    self.nested = nested
    self.url = url ?? URL(string: "https://yandex.ru")!
  }
}

#if DEBUG
extension EntityWithPropertyWithDefaultValue: Equatable {
  public static func ==(
    lhs: EntityWithPropertyWithDefaultValue,
    rhs: EntityWithPropertyWithDefaultValue
  ) -> Bool {
    guard
      lhs.int == rhs.int,
      lhs.nested == rhs.nested,
      lhs.url == rhs.url
    else {
      return false
    }
    return true
  }
}
#endif

#if DEBUG
extension EntityWithPropertyWithDefaultValue.Nested: Equatable {
  public static func ==(
    lhs: EntityWithPropertyWithDefaultValue.Nested,
    rhs: EntityWithPropertyWithDefaultValue.Nested
  ) -> Bool {
    guard
      lhs.int == rhs.int,
      lhs.nonOptional == rhs.nonOptional,
      lhs.url == rhs.url
    else {
      return false
    }
    return true
  }
}
#endif
