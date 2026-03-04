// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivActionCopyToClipboardContent: Sendable {
  case contentText(ContentText)
  case contentUrl(ContentUrl)

  public var value: Serializable {
    switch self {
    case let .contentText(value):
      return value
    case let .contentUrl(value):
      return value
    }
  }
}

extension DivActionCopyToClipboardContent {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let dictionary = context.templateResolver?(dictionary) ?? dictionary
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case ContentText.type:
      self = .contentText(try ContentText(dictionary: dictionary, context: context))
    case ContentUrl.type:
      self = .contentUrl(try ContentUrl(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.requiredFieldIsMissing(field: "type")
    }
  }
}

#if DEBUG
extension DivActionCopyToClipboardContent: Equatable {
  public static func ==(lhs: DivActionCopyToClipboardContent, rhs: DivActionCopyToClipboardContent) -> Bool {
    switch (lhs, rhs) {
    case let (.contentText(l), .contentText(r)):
      return l == r
    case let (.contentUrl(l), .contentUrl(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivActionCopyToClipboardContent: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
