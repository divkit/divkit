// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivLayoutProvider: Sendable {
  public let heightVariableName: String?
  public let widthVariableName: String?

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      heightVariableName: try dictionary.getOptionalField("height_variable_name", context: context),
      widthVariableName: try dictionary.getOptionalField("width_variable_name", context: context)
    )
  }

  init(
    heightVariableName: String? = nil,
    widthVariableName: String? = nil
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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["height_variable_name"] = heightVariableName
    result["width_variable_name"] = widthVariableName
    return result
  }
}
