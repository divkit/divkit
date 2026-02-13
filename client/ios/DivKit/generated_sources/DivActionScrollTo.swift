// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionScrollTo: Sendable {
  public static let type: String = "scroll_to"
  public let animated: Expression<Bool> // default value: true
  public let destination: DivActionScrollDestination
  public let id: Expression<String>

  public func resolveAnimated(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(animated) ?? true
  }

  public func resolveId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(id)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      animated: try dictionary.getOptionalExpressionField("animated", context: context),
      destination: try dictionary.getField("destination", transform: { (dict: [String: Any]) in try DivActionScrollDestination(dictionary: dict, context: context) }),
      id: try dictionary.getExpressionField("id", context: context)
    )
  }

  init(
    animated: Expression<Bool>? = nil,
    destination: DivActionScrollDestination,
    id: Expression<String>
  ) {
    self.animated = animated ?? .value(true)
    self.destination = destination
    self.id = id
  }
}

#if DEBUG
extension DivActionScrollTo: Equatable {
  public static func ==(lhs: DivActionScrollTo, rhs: DivActionScrollTo) -> Bool {
    guard
      lhs.animated == rhs.animated,
      lhs.destination == rhs.destination,
      lhs.id == rhs.id
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionScrollTo: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["animated"] = animated.toValidSerializationValue()
    result["destination"] = destination.toDictionary()
    result["id"] = id.toValidSerializationValue()
    return result
  }
}
