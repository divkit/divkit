import CoreGraphics
import Foundation

public struct VisibilityAction {
  let uiAction: UserInterfaceAction
  let requiredDuration: TimeInterval
  let targetPercentage: Int
  let limiter: ActionLimiter
  let actionType: VisibilityActionType

  public init(
    uiAction: UserInterfaceAction,
    requiredDuration: TimeInterval,
    targetPercentage: Int,
    limiter: ActionLimiter,
    actionType: VisibilityActionType
  ) {
    self.uiAction = uiAction
    self.requiredDuration = requiredDuration
    self.targetPercentage = targetPercentage
    self.limiter = limiter
    self.actionType = actionType
  }
}

extension VisibilityAction: Equatable {
  public static func ==(_ lhs: VisibilityAction, _ rhs: VisibilityAction) -> Bool {
    lhs.uiAction == rhs.uiAction
      && lhs.requiredDuration == rhs.requiredDuration
      && lhs.targetPercentage == rhs.targetPercentage
      && lhs.actionType == rhs.actionType
  }
}
