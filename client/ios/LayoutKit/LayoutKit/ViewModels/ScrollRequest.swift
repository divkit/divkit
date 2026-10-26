import CoreGraphics
import Foundation
import VGSL

/// A command identity retained through state copies and consumed by the owning view.
public final class ScrollRequest: Equatable, Sendable {
  public enum Destination: Sendable {
    case item(CGFloat)
    case offset(CGFloat)
    case relative(ScrollDelta)
  }

  public let duration: TimeInterval?
  public let delta: ScrollDelta?

  let destinations: [Destination]

  private let applied = AllocatedUnfairLock(initialState: false)

  private var pendingDestinations: [Destination] {
    applied.withLock { $0 ? [] : destinations }
  }

  public init(duration: TimeInterval? = nil, delta: ScrollDelta? = nil) {
    self.duration = duration
    self.delta = delta
    destinations = delta.map { [.relative($0)] } ?? []
  }

  /// Only commands not yet applied to a view may precede this command.
  public init(
    duration: TimeInterval? = nil,
    destination: Destination,
    preceding: ScrollRequest? = nil
  ) {
    self.duration = duration
    if case let .relative(delta) = destination {
      self.delta = delta
    } else {
      delta = nil
    }
    destinations = (preceding?.pendingDestinations ?? []) + [destination]
  }

  public static func ==(lhs: ScrollRequest, rhs: ScrollRequest) -> Bool {
    lhs === rhs
  }

  func markApplied() {
    applied.withLock { $0 = true }
  }

  func destinationIndex(from index: CGFloat, count: Int) -> CGFloat {
    var destination = index
    for command in destinations {
      switch command {
      case let .item(index):
        destination = max(0, min(index, CGFloat(count - 1)))
      case let .relative(delta):
        destination = delta.destinationIndex(from: destination, count: count)
      case .offset:
        break
      }
    }
    return destination
  }
}
