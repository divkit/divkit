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
  private let pendingTimer: Property<PendingVisibilityTimer?>

  init(
    requiredDuration: TimeInterval,
    targetVisibilityPercentage: Int,
    limiter: ActionLimiter,
    action: @escaping Action,
    type: VisibilityActionType,
    timerScheduler: Scheduling,
    pendingTimer: Property<PendingVisibilityTimer?>
  ) {
    self.requiredDuration = requiredDuration
    self.targetPercentage = targetVisibilityPercentage
    self.limiter = limiter
    self.action = action
    self.type = type
    self.timerScheduler = timerScheduler
    self.pendingTimer = pendingTimer
  }

  deinit {
    guard let visibilityTimer, visibilityTimer.isValid else { return }
    if pendingTimer.value?.timer === visibilityTimer {
      pendingTimer.value = PendingVisibilityTimer(
        timer: visibilityTimer,
        pause: (Date(), visibilityTimer.fireDate.timeIntervalSinceNow)
      )
    }
    visibilityTimer.invalidate()
  }

  func onVisibleBoundsChanged(
    visibleAreaPercentageBefore: Int,
    visibleAreaPercentageAfter: Int
  ) {
    guard matchesTarget(visibleAreaPercentageAfter) else {
      invalidateTimer()
      return
    }

    guard visibilityTimer == nil, limiter.canSend() else { return }

    if !matchesTarget(visibleAreaPercentageBefore) {
      startTimer()
    } else if let pause = pendingTimer.value?.pause {
      if Date().timeIntervalSince(pause.date) < 0.5 {
        schedule(delay: pause.remainingDuration)
      } else {
        startTimer()
      }
    }
  }

  private func matchesTarget(_ percentage: Int) -> Bool {
    switch type {
    case .appear: percentage >= targetPercentage
    case .disappear: percentage <= targetPercentage
    }
  }

  private func startTimer() {
    if requiredDuration > 0 {
      schedule(delay: requiredDuration)
    } else {
      perform()
    }
  }

  private func schedule(delay: TimeInterval) {
    let timer = timerScheduler.makeTimer(delay: delay) { [weak self] in self?.perform() }
    visibilityTimer = timer
    pendingTimer.value = PendingVisibilityTimer(timer: timer, pause: nil)
  }

  private func perform() {
    if pendingTimer.value?.timer === visibilityTimer {
      pendingTimer.value = nil
    }
    limiter.markSent()
    action()
  }

  private func invalidateTimer() {
    visibilityTimer?.invalidate()
    visibilityTimer = nil
    if pendingTimer.value?.timer.isValid != true {
      pendingTimer.value = nil
    }
  }
}
