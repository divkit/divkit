// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivDownloadCallbacks {
  public let onFailActions: [DivAction]? // at least 1 elements
  public let onSuccessActions: [DivAction]? // at least 1 elements

  static let onFailActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let onSuccessActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

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
