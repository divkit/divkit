// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class DivWrapContentSize {
  public static let type: String = "wrap_content"
  public let constrained: Expression<Bool>?

  public func resolveConstrained(_ resolver: ExpressionResolver) -> Bool? {
    resolver.resolveNumericValue(expression: constrained)
  }

  static let constrainedValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  init(constrained: Expression<Bool>? = nil) {
    self.constrained = constrained
  }
}

#if DEBUG
extension DivWrapContentSize: Equatable {
  public static func ==(lhs: DivWrapContentSize, rhs: DivWrapContentSize) -> Bool {
    guard
      lhs.constrained == rhs.constrained
    else {
      return false
    }
    return true
  }
}
#endif

extension DivWrapContentSize: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["constrained"] = constrained?.toValidSerializationValue()
    return result
  }
}
