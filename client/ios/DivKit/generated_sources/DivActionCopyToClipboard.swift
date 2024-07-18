// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionCopyToClipboard {
  public static let type: String = "copy_to_clipboard"
  public let content: DivActionCopyToClipboardContent

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
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["content"] = content.toDictionary()
    return result
  }
}
