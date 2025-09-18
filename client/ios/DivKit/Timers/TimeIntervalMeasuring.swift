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

  func start() {
    startDate = Date()
    passedTimeInterval = 0
    paused = false
  }

  func pause() {
    paused = true
    if let startDate {
      passedTimeInterval += Date().timeIntervalSince(startDate)
    }
  }

  func resume() {
    paused = false
    startDate = Date()
  }

  func passedInterval() -> TimeInterval {
    if paused {
      return passedTimeInterval
    }
    if let startDate {
      return passedTimeInterval + Date().timeIntervalSince(startDate)
    }
    return 0
  }
}
