@testable import DivKit
import Foundation
import VGSL
import XCTest

public final class TestTimer: TimerType {
  public let timeInterval: TimeInterval
  public var fireDate: Date
  public private(set) var isValid = true

  private let block: () -> Void
  private let repeating: Bool

  public init(
    fireDate: Date = .distantFuture,
    timeInterval: TimeInterval,
    block: @escaping () -> Void,
    repeating: Bool
  ) {
    self.fireDate = fireDate
    self.timeInterval = timeInterval
    self.block = block
    self.repeating = repeating
  }

  public func fire() {
    guard isValid else {
      XCTFail("Timer is not valid.")
      return
    }
    block()
    if !repeating {
      invalidate()
    }
  }

  public func invalidate() {
    isValid = false
  }
}

public final class TestTimerScheduler: Scheduling {
  public var timers: [TestTimer] = []

  public func makeTimer(fireDate: Date, handler: @escaping () -> Void) -> TimerType {
    let timer = TestTimer(
      fireDate: fireDate,
      timeInterval: 0,
      block: handler,
      repeating: false
    )
    timers.append(timer)
    return timer
  }

  public func makeTimer(
    delay: TimeInterval,
    handler: @escaping () -> Void
  ) -> TimerType {
    makeTimer(
      timeInterval: delay,
      handler: handler,
      repeating: false
    )
  }

  public func makeRepeatingTimer(
    interval: TimeInterval,
    handler: @escaping () -> Void
  ) -> TimerType {
    makeTimer(
      timeInterval: interval,
      handler: handler,
      repeating: true
    )
  }

  private func makeTimer(
    timeInterval: TimeInterval,
    handler: @escaping () -> Void,
    repeating: Bool
  ) -> TimerType {
    let timer = TestTimer(
      timeInterval: timeInterval,
      block: handler,
      repeating: repeating
    )
    timers.append(timer)
    return timer
  }
}

public final class TestTimeMeasuring: TimeMeasuring {
  var passedTime: TimeInterval = 0
  var started = 0
  var paused = 0

  public func start() {
    started += 1
    paused = 0
  }

  public func pause() {
    paused += 1
  }

  public func resume() {}

  public func passedInterval() -> TimeInterval {
    passedTime
  }
}
