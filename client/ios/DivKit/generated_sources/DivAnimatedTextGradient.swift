// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivAnimatedTextGradient: Sendable {
  public static let type: String = "animated"
  public let duration: Expression<Int> // constraint: number >= 0; default value: 1600
  public let gradient: DivStaticTextGradient

  public func resolveDuration(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(duration) ?? 1600
  }

  static let durationValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      duration: try dictionary.getOptionalExpressionField("duration", validator: Self.durationValidator, context: context),
      gradient: try dictionary.getField("gradient", transform: { (dict: [String: Any]) in try DivStaticTextGradient(dictionary: dict, context: context) }, context: context)
    )
  }

  init(
    duration: Expression<Int>? = nil,
    gradient: DivStaticTextGradient
  ) {
    self.duration = duration ?? .value(1600)
    self.gradient = gradient
  }
}

#if DEBUG
extension DivAnimatedTextGradient: Equatable {
  public static func ==(lhs: DivAnimatedTextGradient, rhs: DivAnimatedTextGradient) -> Bool {
    guard
      lhs.duration == rhs.duration,
      lhs.gradient == rhs.gradient
    else {
      return false
    }
    return true
  }
}
#endif

extension DivAnimatedTextGradient: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["duration"] = duration.toValidSerializationValue()
    result["gradient"] = gradient.toDictionary()
    return result
  }
}
