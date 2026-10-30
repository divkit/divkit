// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivExtensionAction: Sendable {
  public static let type: String = "extension_action"
  public let divId: Expression<String>
  public let extensionId: Expression<String>

  public func resolveDivId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(divId)
  }

  public func resolveExtensionId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(extensionId)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      divId: try dictionary.getExpressionField("div_id", context: context),
      extensionId: try dictionary.getExpressionField("extension_id", context: context)
    )
  }

  init(
    divId: Expression<String>,
    extensionId: Expression<String>
  ) {
    self.divId = divId
    self.extensionId = extensionId
  }
}

#if DEBUG
extension DivExtensionAction: Equatable {
  public static func ==(lhs: DivExtensionAction, rhs: DivExtensionAction) -> Bool {
    guard
      lhs.divId == rhs.divId,
      lhs.extensionId == rhs.extensionId
    else {
      return false
    }
    return true
  }
}
#endif

extension DivExtensionAction: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["div_id"] = divId.toValidSerializationValue()
    result["extension_id"] = extensionId.toValidSerializationValue()
    return result
  }
}
