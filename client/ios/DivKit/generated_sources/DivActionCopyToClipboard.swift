// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionCopyToClipboard: Sendable {
  public static let type: String = "copy_to_clipboard"
  public let content: DivActionCopyToClipboardContent

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      content: try dictionary.getField("content", transform: { (dict: [String: Any]) in try DivActionCopyToClipboardContent(dictionary: dict, context: context) })
    )
  }

  init(
    content: DivActionCopyToClipboardContent
  ) {
    self.content = content
  }
}

#if DEBUG
extension DivActionCopyToClipboard: Equatable {
  public static func ==(lhs: DivActionCopyToClipboard, rhs: DivActionCopyToClipboard) -> Bool {
    guard
      lhs.content == rhs.content
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionCopyToClipboard: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["content"] = content.toDictionary()
    return result
  }
}
