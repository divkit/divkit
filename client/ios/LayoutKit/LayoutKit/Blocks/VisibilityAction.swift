import CoreGraphics
import Foundation
import VGSL

public struct VisibilityAction {
  public let logId: String?

  let uiAction: UserInterfaceAction
  let requiredDuration: TimeInterval
  let targetPercentage: Int
  let limiter: ActionLimiter
  let actionType: VisibilityActionType
  let pendingTimer: Property<PendingVisibilityTimer?>

  public init(
    logId: String? = nil,
    uiAction: UserInterfaceAction,
    requiredDuration: TimeInterval,
    targetPercentage: Int,
    limiter: ActionLimiter,
    actionType: VisibilityActionType,
    pendingTimer: Property<PendingVisibilityTimer?> = Property()
  ) {
    self.logId = logId
    self.uiAction = uiAction
    self.requiredDuration = requiredDuration
    self.targetPercentage = targetPercentage
    self.limiter = limiter
    self.actionType = actionType
    self.pendingTimer = pendingTimer
  }
}

public struct PendingVisibilityTimer {
  let timer: TimerType
  let pause: (date: Date, remainingDuration: TimeInterval)?
}

extension VisibilityAction: Equatable {
  public static func ==(_ lhs: VisibilityAction, _ rhs: VisibilityAction) -> Bool {
    lhs.logId == rhs.logId
      && lhs.uiAction == rhs.uiAction
      && lhs.requiredDuration == rhs.requiredDuration
      && lhs.targetPercentage == rhs.targetPercentage
      && lhs.actionType == rhs.actionType
  }
}
