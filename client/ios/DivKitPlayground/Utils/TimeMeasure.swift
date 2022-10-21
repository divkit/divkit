import Foundation

final class TimeMeasure {
  private var startTime: Date?
  private var status: Status = .cold

  private(set) var time: Time?

  func start() {
    self.startTime = Date()
  }

  func stop() {
    if let startTime = startTime {
      time = Time(
        value: Int(Date().timeIntervalSince(startTime) * 1000),
        status: status
      )
      status = .warm
    }
  }

  enum Status: String {
    case cold
    case warm
  }

  struct Time {
    public var value: Int
    public var status: Status
  }
}

