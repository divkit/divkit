import Foundation

import BaseTinyPublic

public final class TimeMeasure {
  private var startTime: Date?
  private var status: Status = .cold

  public private(set) var time: Time?

  public init() {}

  private func start() {
    self.startTime = Date()
  }

  private func stop() {
    if let startTime {
      time = Time(
        value: Int(Date().timeIntervalSince(startTime) * 1000),
        status: status
      )
      status = .warm
    }
  }

  public func updateMeasure<T>(action: () throws -> T) throws -> T {
    start()
    let result = try action()
    stop()
    return result
  }

  public enum Status: String {
    case cold
    case warm
  }

  public struct Time {
    public var value: Int
    public var status: Status

    public var description: String {
      "\(status.rawValue.capitalized): \(value)"
    }
  }
}
