// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionSetStoredValue: Sendable {
  @frozen
  public enum Scope: String, CaseIterable, Sendable {
    case global = "global"
    case card = "card"
  }

  public static let type: String = "set_stored_value"
  public let lifetime: Expression<Int>
  public let name: Expression<String>
  public let scope: Expression<Scope>?
  public let value: DivTypedValue

  public func resolveLifetime(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(lifetime)
  }

  public func resolveName(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(name)
  }

  public func resolveScope(_ resolver: ExpressionResolver) -> Scope? {
    resolver.resolveEnum(scope)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      lifetime: try dictionary.getExpressionField("lifetime", context: context),
      name: try dictionary.getExpressionField("name", context: context),
      scope: try dictionary.getOptionalExpressionField("scope", context: context),
      value: try dictionary.getField("value", transform: { (dict: [String: Any]) in try DivTypedValue(dictionary: dict, context: context) }, context: context)
    )
  }

  init(
    lifetime: Expression<Int>,
    name: Expression<String>,
    scope: Expression<Scope>? = nil,
    value: DivTypedValue
  ) {
    self.lifetime = lifetime
    self.name = name
    self.scope = scope
    self.value = value
  }
}

#if DEBUG
extension DivActionSetStoredValue: Equatable {
  public static func ==(lhs: DivActionSetStoredValue, rhs: DivActionSetStoredValue) -> Bool {
    guard
      lhs.lifetime == rhs.lifetime,
      lhs.name == rhs.name,
      lhs.scope == rhs.scope
    else {
      return false
    }
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionSetStoredValue: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["lifetime"] = lifetime.toValidSerializationValue()
    result["name"] = name.toValidSerializationValue()
    result["scope"] = scope?.toValidSerializationValue()
    result["value"] = value.toDictionary()
    return result
  }
}
