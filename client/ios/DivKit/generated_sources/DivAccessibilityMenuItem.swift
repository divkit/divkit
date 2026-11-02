// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivAccessibilityMenuItem: Sendable {
  public let actions: [DivAction]
  public let label: Expression<String>

  public func resolveLabel(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(label)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      actions: try dictionary.getArray("actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }, context: context),
      label: try dictionary.getExpressionField("label", context: context)
    )
  }

  init(
    actions: [DivAction],
    label: Expression<String>
  ) {
    self.actions = actions
    self.label = label
  }
}

#if DEBUG
extension DivAccessibilityMenuItem: Equatable {
  public static func ==(lhs: DivAccessibilityMenuItem, rhs: DivAccessibilityMenuItem) -> Bool {
    guard
      lhs.actions == rhs.actions,
      lhs.label == rhs.label
    else {
      return false
    }
    return true
  }
}
#endif

extension DivAccessibilityMenuItem: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["actions"] = actions.map { $0.toDictionary() }
    result["label"] = label.toValidSerializationValue()
    return result
  }
}
