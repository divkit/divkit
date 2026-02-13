// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivWrapContentSize: Sendable {
  public static let type: String = "wrap_content"
  public let constrained: Expression<Bool>?
  public let maxSize: DivSizeUnitValue?
  public let minSize: DivSizeUnitValue?

  public func resolveConstrained(_ resolver: ExpressionResolver) -> Bool? {
    resolver.resolveNumeric(constrained)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      constrained: try dictionary.getOptionalExpressionField("constrained", context: context),
      maxSize: try dictionary.getOptionalField("max_size", transform: { (dict: [String: Any]) in try DivSizeUnitValue(dictionary: dict, context: context) }),
      minSize: try dictionary.getOptionalField("min_size", transform: { (dict: [String: Any]) in try DivSizeUnitValue(dictionary: dict, context: context) })
    )
  }

  init(
    constrained: Expression<Bool>? = nil,
    maxSize: DivSizeUnitValue? = nil,
    minSize: DivSizeUnitValue? = nil
  ) {
    self.constrained = constrained
    self.maxSize = maxSize
    self.minSize = minSize
  }
}

#if DEBUG
extension DivWrapContentSize: Equatable {
  public static func ==(lhs: DivWrapContentSize, rhs: DivWrapContentSize) -> Bool {
    guard
      lhs.constrained == rhs.constrained,
      lhs.maxSize == rhs.maxSize,
      lhs.minSize == rhs.minSize
    else {
      return false
    }
    return true
  }
}
#endif

extension DivWrapContentSize: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["constrained"] = constrained?.toValidSerializationValue()
    result["max_size"] = maxSize?.toDictionary()
    result["min_size"] = minSize?.toDictionary()
    return result
  }
}
