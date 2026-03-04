// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivFunction: Sendable {
  public let arguments: [DivFunctionArgument]
  public let body: String
  public let name: String // regex: ^[a-zA-Z_][a-zA-Z0-9_]*$
  public let returnType: DivEvaluableType

  static let nameValidator: AnyValueValidator<String> =
    makeStringValidator(regex: "^[a-zA-Z_][a-zA-Z0-9_]*$")

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      arguments: try dictionary.getArray("arguments", transform: { (dict: [String: Any]) in try? DivFunctionArgument(dictionary: dict, context: context) }, context: context),
      body: try dictionary.getField("body", context: context),
      name: try dictionary.getField("name", validator: Self.nameValidator, context: context),
      returnType: try dictionary.getField("return_type", context: context)
    )
  }

  init(
    arguments: [DivFunctionArgument],
    body: String,
    name: String,
    returnType: DivEvaluableType
  ) {
    self.arguments = arguments
    self.body = body
    self.name = name
    self.returnType = returnType
  }
}

#if DEBUG
extension DivFunction: Equatable {
  public static func ==(lhs: DivFunction, rhs: DivFunction) -> Bool {
    guard
      lhs.arguments == rhs.arguments,
      lhs.body == rhs.body,
      lhs.name == rhs.name
    else {
      return false
    }
    guard
      lhs.returnType == rhs.returnType
    else {
      return false
    }
    return true
  }
}
#endif

extension DivFunction: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["arguments"] = arguments.map { $0.toDictionary() }
    result["body"] = body
    result["name"] = name
    result["return_type"] = returnType.rawValue
    return result
  }
}
