// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivFunctionArgument {
  public let name: String
  public let type: DivEvaluableType

  init(
    name: String,
    type: DivEvaluableType
  ) {
    self.name = name
    self.type = type
  }
}

#if DEBUG
extension DivFunctionArgument: Equatable {
  public static func ==(lhs: DivFunctionArgument, rhs: DivFunctionArgument) -> Bool {
    guard
      lhs.name == rhs.name,
      lhs.type == rhs.type
    else {
      return false
    }
    return true
  }
}
#endif

extension DivFunctionArgument: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["name"] = name
    result["type"] = type.rawValue
    return result
  }
}
