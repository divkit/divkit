// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivAnimator: Sendable {
  case divColorAnimator(DivColorAnimator)
  case divNumberAnimator(DivNumberAnimator)

  public var value: Serializable & DivAnimatorBase {
    switch self {
    case let .divColorAnimator(value):
      return value
    case let .divNumberAnimator(value):
      return value
    }
  }

  public var id: String {
    switch self {
    case let .divColorAnimator(value):
      return value.id
    case let .divNumberAnimator(value):
      return value.id
    }
  }
}

extension DivAnimator {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let dictionary = context.templateResolver?(dictionary) ?? dictionary
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivColorAnimator.type:
      self = .divColorAnimator(try DivColorAnimator(dictionary: dictionary, context: context))
    case DivNumberAnimator.type:
      self = .divNumberAnimator(try DivNumberAnimator(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.requiredFieldIsMissing(field: "type")
    }
  }
}

#if DEBUG
extension DivAnimator: Equatable {
  public static func ==(lhs: DivAnimator, rhs: DivAnimator) -> Bool {
    switch (lhs, rhs) {
    case let (.divColorAnimator(l), .divColorAnimator(r)):
      return l == r
    case let (.divNumberAnimator(l), .divNumberAnimator(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivAnimator: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
