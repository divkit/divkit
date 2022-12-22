import Foundation

protocol TimeMeasuring {
  func start()
  func pause()
  func resume()
  func passedInterval() -> TimeInterval
}

final class TimeIntervalMeasuring: TimeMeasuring {
  private var startDate: Date?
  private var passedTimeInterval: TimeInterval = 0
  private var paused = false

  public func start() {
    startDate = Date()
    passedTimeInterval = 0
    paused = false
  }

  public func pause() {
    paused = true
    if let startDate = startDate {
      passedTimeInterval += Date().timeIntervalSince(startDate)
    }
  }

  public func resume() {
    paused = false
    startDate = Date()
  }

  public func passedInterval() -> TimeInterval {
    if paused {
      return passedTimeInterval
    }
    if let startDate = startDate {
      return passedTimeInterval + Date().timeIntervalSince(startDate)
    }
    return 0
  }
}
