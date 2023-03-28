import Foundation

import CommonCorePublic

final class VisibilityActionPerformer {
  private var visibilityTimer: TimerType?
  private let requiredVisibilityDuration: TimeInterval
  private let targetPercentage: Int
  private let limiter: VisibilityAction.Limiter
  private let action: Action
  private let timerScheduler: Scheduling

  init(
    requiredVisibilityDuration: TimeInterval,
    targetVisibilityPercentage: Int,
    limiter: VisibilityAction.Limiter,
    action: @escaping Action,
    timerScheduler: Scheduling = TimerScheduler()
  ) {
    self.requiredVisibilityDuration = requiredVisibilityDuration
    self.targetPercentage = targetVisibilityPercentage
    self.limiter = limiter
    self.action = action
    self.timerScheduler = timerScheduler
  }

  func onVisibleBoundsChanged(visibleAreaPercentage: Int) {
    guard visibleAreaPercentage >= targetPercentage else {
      visibilityTimer?.invalidate()
      visibilityTimer = nil
      return
    }
    guard visibilityTimer == nil, limiter.canSend() else { return }
    visibilityTimer = timerScheduler.makeTimer(
      delay: requiredVisibilityDuration,
      handler: { [weak self] in
        self?.limiter.markSent()
        self?.action()
      }
    )
  }

  deinit {
    visibilityTimer?.invalidate()
  }
}
