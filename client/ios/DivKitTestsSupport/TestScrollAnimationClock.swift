import Foundation
@testable import LayoutKit

public final class TestScrollAnimationClock: ScrollAnimationClock {
  public private(set) var time: TimeInterval = 0

  private var tick: ((TimeInterval) -> Void)?

  public init() {}

  public func start(_ tick: @escaping (TimeInterval) -> Void) {
    self.tick = tick
  }

  public func stop() {
    tick = nil
  }

  public func advance(to time: TimeInterval) {
    self.time = time
    tick?(time)
  }
}
