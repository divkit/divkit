// Copyright 2015 Yandex LLC. All rights reserved.

import Foundation

public protocol Scheduling: AnyObject {
  func makeTimer(fireDate: Date, handler: @escaping () -> Void) -> TimerType
  func makeTimer(delay: TimeInterval, handler: @escaping () -> Void) -> TimerType
  func makeRepeatingTimer(interval: TimeInterval, handler: @escaping () -> Void) -> TimerType
}

public protocol TimerType: AnyObject {
  var isValid: Bool { get }
  var timeInterval: TimeInterval { get }
  var fireDate: Date { get }
  func fire()
  func invalidate()
}

private final class TimerCallback {
  private let block: () -> Void

  init(block: @escaping () -> Void) {
    self.block = block
  }

  @objc func fire(with _: Timer) {
    block()
  }
}

public final class TimerScheduler: Scheduling {
  public init() {}

  public func makeTimer(fireDate: Date, handler: @escaping () -> Void) -> TimerType {
    let callback = TimerCallback(block: handler)
    let timer = Timer(
      fireAt: fireDate,
      interval: 0,
      target: callback,
      selector: #selector(TimerCallback.fire(with:)),
      userInfo: nil,
      repeats: false
    )
    RunLoop.main.add(timer, forMode: .common)

    return timer
  }

  public func makeTimer(delay: TimeInterval, handler: @escaping () -> Void) -> TimerType {
    makeTimer(interval: delay, repeats: false, handler: handler)
  }

  public func makeRepeatingTimer(
    interval: TimeInterval,
    handler: @escaping () -> Void
  ) -> TimerType {
    makeTimer(interval: interval, repeats: true, handler: handler)
  }

  private func makeTimer(
    interval: TimeInterval,
    repeats: Bool,
    handler: @escaping () -> Void
  ) -> TimerType {
    let timer = Timer.make(
      withTimeInterval: interval,
      handler: handler,
      userInfo: nil,
      repeats: repeats
    )
    RunLoop.main.add(timer, forMode: .common)
    return timer
  }
}

extension Timer: TimerType {}

extension Timer {
  public static func make(
    withTimeInterval ti: TimeInterval,
    handler: @escaping () -> Void,
    userInfo: Any?,
    repeats yesOrNo: Bool
  ) -> Timer {
    let callback = TimerCallback(block: handler)
    return Timer(
      timeInterval: ti,
      target: callback,
      selector: #selector(TimerCallback.fire(with:)),
      userInfo: userInfo,
      repeats: yesOrNo
    )
  }
}
