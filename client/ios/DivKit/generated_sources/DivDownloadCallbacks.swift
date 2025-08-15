// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivDownloadCallbacks: Sendable {
  public let onFailActions: [DivAction]?
  public let onSuccessActions: [DivAction]?

  init(
    onFailActions: [DivAction]? = nil,
    onSuccessActions: [DivAction]? = nil
  ) {
    self.onFailActions = onFailActions
    self.onSuccessActions = onSuccessActions
  }
}

#if DEBUG
extension DivDownloadCallbacks: Equatable {
  public static func ==(lhs: DivDownloadCallbacks, rhs: DivDownloadCallbacks) -> Bool {
    guard
      lhs.onFailActions == rhs.onFailActions,
      lhs.onSuccessActions == rhs.onSuccessActions
    else {
      return false
    }
    return true
  }
}
#endif

extension DivDownloadCallbacks: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["on_fail_actions"] = onFailActions?.map { $0.toDictionary() }
    result["on_success_actions"] = onSuccessActions?.map { $0.toDictionary() }
    return result
  }
}
