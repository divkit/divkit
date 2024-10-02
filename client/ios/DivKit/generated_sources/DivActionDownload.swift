// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionDownload {
  public static let type: String = "download"
  public let onFailActions: [DivAction]?
  public let onSuccessActions: [DivAction]?
  public let url: Expression<String>

  public func resolveUrl(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(url)
  }

  init(
    onFailActions: [DivAction]? = nil,
    onSuccessActions: [DivAction]? = nil,
    url: Expression<String>
  ) {
    self.onFailActions = onFailActions
    self.onSuccessActions = onSuccessActions
    self.url = url
  }
}

#if DEBUG
extension DivActionDownload: Equatable {
  public static func ==(lhs: DivActionDownload, rhs: DivActionDownload) -> Bool {
    guard
      lhs.onFailActions == rhs.onFailActions,
      lhs.onSuccessActions == rhs.onSuccessActions,
      lhs.url == rhs.url
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionDownload: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["on_fail_actions"] = onFailActions?.map { $0.toDictionary() }
    result["on_success_actions"] = onSuccessActions?.map { $0.toDictionary() }
    result["url"] = url.toValidSerializationValue()
    return result
  }
}
