import Foundation

import VGSL

public enum VisibilityActionType: String {
  case appear
  case disappear
}

final class VisibilityActionPerformer {
  private var visibilityTimer: TimerType?
  private let requiredDuration: TimeInterval
  private let targetPercentage: Int
  private let limiter: ActionLimiter
  private let action: Action
  private let timerScheduler: Scheduling
  private let type: VisibilityActionType

  init(
    requiredDuration: TimeInterval,
    targetVisibilityPercentage: Int,
    limiter: ActionLimiter,
    action: @escaping Action,
    type: VisibilityActionType,
    timerScheduler: Scheduling
  ) {
    self.requiredDuration = requiredDuration
    self.targetPercentage = targetVisibilityPercentage
    self.limiter = limiter
    self.action = action
    self.type = type
    self.timerScheduler = timerScheduler
  }

  func onVisibleBoundsChanged(
    visibleAreaPercentageBefore: Int,
    visibleAreaPercentageAfter: Int
  ) {
    switch type {
    case .appear:
      guard visibleAreaPercentageAfter >= targetPercentage,
            visibleAreaPercentageBefore < targetPercentage
      else {
        if visibleAreaPercentageAfter < targetPercentage {
          invalidateTimer()
        }
        return
      }
    case .disappear:
      guard visibleAreaPercentageAfter <= targetPercentage,
            visibleAreaPercentageBefore > targetPercentage
      else {
        if visibleAreaPercentageAfter > targetPercentage {
          invalidateTimer()
        }
        return
      }
    }

    guard visibilityTimer == nil, limiter.canSend() else { return }
    visibilityTimer = timerScheduler.makeTimer(
      delay: requiredDuration,
      handler: { [weak self] in
        self?.limiter.markSent()
        self?.action()
      }
    )
  }

  private func invalidateTimer() {
    visibilityTimer?.invalidate()
    visibilityTimer = nil
  }

  deinit {
    visibilityTimer?.invalidate()
  }
}
