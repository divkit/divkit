import Foundation

import CommonCorePublic

struct VisibilityCheckParam {
  let requiredDuration: TimeInterval
  let targetPercentage: Int
  let limiter: ActionLimiter
  let action: Action
  let type: VisibilityActionType
}
