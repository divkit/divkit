// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionHaptic: Sendable {
  @frozen
  public enum Feedback: String, CaseIterable, Sendable {
    case light = "light"
    case medium = "medium"
    case heavy = "heavy"
    case success = "success"
    case error = "error"
  }

  public static let type: String = "haptic"
  public let feedback: Expression<Feedback> // default value: light

  public func resolveFeedback(_ resolver: ExpressionResolver) -> Feedback {
    resolver.resolveEnum(feedback) ?? Feedback.light
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      feedback: try dictionary.getOptionalExpressionField("feedback", context: context)
    )
  }

  init(
    feedback: Expression<Feedback>? = nil
  ) {
    self.feedback = feedback ?? .value(.light)
  }
}

#if DEBUG
extension DivActionHaptic: Equatable {
  public static func ==(lhs: DivActionHaptic, rhs: DivActionHaptic) -> Bool {
    guard
      lhs.feedback == rhs.feedback
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionHaptic: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["feedback"] = feedback.toValidSerializationValue()
    return result
  }
}
