// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithPropertyWithDefaultValue: Sendable {
  public final class Nested: Sendable {
    public let int: Expression<Int> // constraint: number >= 0; default value: 0
    public let nonOptional: Expression<String>
    public let url: Expression<URL> // valid schemes: [https]; default value: https://yandex.ru

    public func resolveInt(_ resolver: ExpressionResolver) -> Int {
      resolver.resolveNumeric(int) ?? 0
    }

    public func resolveNonOptional(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(nonOptional)
    }

    public func resolveUrl(_ resolver: ExpressionResolver) -> URL {
      resolver.resolveUrl(url) ?? URL(string: "https://yandex.ru")!
    }

    static let intValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let urlValidator: AnyValueValidator<URL> =
      makeURLValidator(schemes: ["https"])

    public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
      self.init(
        int: try dictionary.getOptionalExpressionField("int", validator: Self.intValidator, context: context),
        nonOptional: try dictionary.getExpressionField("non_optional", context: context),
        url: try dictionary.getOptionalExpressionField("url", transform: URL.makeFromNonEncodedString, validator: Self.urlValidator, context: context)
      )
    }

    init(
      int: Expression<Int>? = nil,
      nonOptional: Expression<String>,
      url: Expression<URL>? = nil
    ) {
      self.int = int ?? .value(0)
      self.nonOptional = nonOptional
      self.url = url ?? .value(URL(string: "https://yandex.ru")!)
    }
  }

  public static let type: String = "entity_with_property_with_default_value"
  public let int: Expression<Int> // constraint: number >= 0; default value: 0
  public let nested: Nested?
  public let url: Expression<URL> // valid schemes: [https]; default value: https://yandex.ru

  public func resolveInt(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(int) ?? 0
  }

  public func resolveUrl(_ resolver: ExpressionResolver) -> URL {
    resolver.resolveUrl(url) ?? URL(string: "https://yandex.ru")!
  }

  static let intValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let urlValidator: AnyValueValidator<URL> =
    makeURLValidator(schemes: ["https"])

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      int: try dictionary.getOptionalExpressionField("int", validator: Self.intValidator, context: context),
      nested: try dictionary.getOptionalField("nested", transform: { (dict: [String: Any]) in try EntityWithPropertyWithDefaultValue.Nested(dictionary: dict, context: context) }),
      url: try dictionary.getOptionalExpressionField("url", transform: URL.makeFromNonEncodedString, validator: Self.urlValidator, context: context)
    )
  }

  init(
    int: Expression<Int>? = nil,
    nested: Nested? = nil,
    url: Expression<URL>? = nil
  ) {
    self.int = int ?? .value(0)
    self.nested = nested
    self.url = url ?? .value(URL(string: "https://yandex.ru")!)
  }
}

#if DEBUG
extension EntityWithPropertyWithDefaultValue: Equatable {
  public static func ==(lhs: EntityWithPropertyWithDefaultValue, rhs: EntityWithPropertyWithDefaultValue) -> Bool {
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

extension EntityWithPropertyWithDefaultValue: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["int"] = int.toValidSerializationValue()
    result["nested"] = nested?.toDictionary()
    result["url"] = url.toValidSerializationValue()
    return result
  }
}

#if DEBUG
extension EntityWithPropertyWithDefaultValue.Nested: Equatable {
  public static func ==(lhs: EntityWithPropertyWithDefaultValue.Nested, rhs: EntityWithPropertyWithDefaultValue.Nested) -> Bool {
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

extension EntityWithPropertyWithDefaultValue.Nested: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["int"] = int.toValidSerializationValue()
    result["non_optional"] = nonOptional.toValidSerializationValue()
    result["url"] = url.toValidSerializationValue()
    return result
  }
}
