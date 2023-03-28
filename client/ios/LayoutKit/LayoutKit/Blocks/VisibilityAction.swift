import CoreGraphics
import Foundation

import BasePublic
import LayoutKitInterface

public struct VisibilityAction {
  public struct Limiter {
    let canSend: () -> Bool
    let markSent: Action

    public init(
      canSend: @escaping () -> Bool,
      markSent: @escaping Action
    ) {
      self.canSend = canSend
      self.markSent = markSent
    }
  }

  let uiAction: UserInterfaceAction
  let requiredVisibilityDuration: TimeInterval
  let targetPercentage: Int
  let limiter: Limiter

  public init(
    uiAction: UserInterfaceAction,
    requiredVisibilityDuration: TimeInterval,
    targetPercentage: Int,
    limiter: Limiter
  ) {
    self.uiAction = uiAction
    self.requiredVisibilityDuration = requiredVisibilityDuration
    self.targetPercentage = targetPercentage
    self.limiter = limiter
  }
}

extension VisibilityAction: Equatable {
  public static func ==(_ lhs: VisibilityAction, _ rhs: VisibilityAction) -> Bool {
    lhs.uiAction == rhs.uiAction
      && lhs.requiredVisibilityDuration == rhs.requiredVisibilityDuration
      && lhs.targetPercentage == rhs.targetPercentage
  }
}
