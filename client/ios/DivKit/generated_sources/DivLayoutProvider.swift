// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivLayoutProvider {
  public let heightVariableName: String?
  public let widthVariableName: String?

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
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["height_variable_name"] = heightVariableName
    result["width_variable_name"] = widthVariableName
    return result
  }
}
