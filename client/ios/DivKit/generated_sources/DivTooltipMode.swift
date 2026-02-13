// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivTooltipMode: Sendable {
  case divTooltipModeNonModal(DivTooltipModeNonModal)
  case divTooltipModeModal(DivTooltipModeModal)

  public var value: Serializable {
    switch self {
    case let .divTooltipModeNonModal(value):
      return value
    case let .divTooltipModeModal(value):
      return value
    }
  }
}

extension DivTooltipMode {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivTooltipModeNonModal.type:
      self = .divTooltipModeNonModal(try DivTooltipModeNonModal(dictionary: dictionary, context: context))
    case DivTooltipModeModal.type:
      self = .divTooltipModeModal(try DivTooltipModeModal(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-tooltip-mode", representation: dictionary)
    }
  }
}

#if DEBUG
extension DivTooltipMode: Equatable {
  public static func ==(lhs: DivTooltipMode, rhs: DivTooltipMode) -> Bool {
    switch (lhs, rhs) {
    case let (.divTooltipModeNonModal(l), .divTooltipModeNonModal(r)):
      return l == r
    case let (.divTooltipModeModal(l), .divTooltipModeModal(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivTooltipMode: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
