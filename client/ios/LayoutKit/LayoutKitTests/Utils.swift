import Foundation
import XCTest

import CommonCorePublic

public final class TestTimer: TimerType {
  public let timeInterval: TimeInterval
  private let block: () -> Void
  private let repeating: Bool
  public var fireDate: Date

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

  public private(set) var isValid = true

  public func fire() {
    precondition(isValid)
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

public func XCTAssertThrowsError<T: Equatable & Error>(
  _ expression: @autoclosure () throws -> some Any,
  _ expectedError: T
) {
  XCTAssertThrowsError(try expression()) { error in
    XCTAssertEqual(error as? T, expectedError)
  }
}
