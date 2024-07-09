// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivLayoutProvider {
  public let heightVariableName: Expression<String>?
  public let widthVariableName: Expression<String>?

  public func resolveHeightVariableName(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(heightVariableName)
  }

  public func resolveWidthVariableName(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(widthVariableName)
  }

  init(
    heightVariableName: Expression<String>? = nil,
    widthVariableName: Expression<String>? = nil
  ) {
    self.heightVariableName = heightVariableName
    self.widthVariableName = widthVariableName
  }
}

#if DEBUG
extension DivLayoutProvider: Equatable {
  public static func ==(lhs: DivLayoutProvider, rhs: DivLayoutProvider) -> Bool {
    guard
      lhs.heightVariableName == rhs.heightVariableName,
      lhs.widthVariableName == rhs.widthVariableName
    else {
      return false
    }
    return true
  }
}
#endif

extension DivLayoutProvider: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["height_variable_name"] = heightVariableName?.toValidSerializationValue()
    result["width_variable_name"] = widthVariableName?.toValidSerializationValue()
    return result
  }
}
