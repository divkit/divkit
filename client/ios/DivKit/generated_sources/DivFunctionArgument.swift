// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivFunctionArgument: Sendable {
  public let name: String
  public let type: DivEvaluableType

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      name: try dictionary.getField("name", context: context),
      type: try dictionary.getField("type", context: context)
    )
  }

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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["name"] = name
    result["type"] = type.rawValue
    return result
  }
}
