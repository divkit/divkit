import Foundation
import VGSL

public final class TimeMeasure: @unchecked Sendable {
  public struct Time {
    public var value: Int
    public var status: Status

    public var description: String {
      "\(status.rawValue.capitalized): \(value)"
    }
  }

  public enum Status: String {
    case cold
    case warm
  }

  public private(set) var time: Time?

  private var startTime: Date?
  private var status: Status = .cold

  init() {}

  public func updateMeasure<T>(action: () throws -> T) throws -> T {
    start()
    let result = try action()
    stop()
    return result
  }

  private func start() {
    startTime = Date()
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
}
