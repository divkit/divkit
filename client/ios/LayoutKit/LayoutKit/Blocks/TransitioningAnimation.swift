import CoreGraphics
import Foundation
import VGSL

public struct TransitioningAnimation: Equatable {
  @frozen
  public enum Kind: String, Equatable, CaseIterable {
    case fade
    case scaleXY
    case translationX
    case translationY
  }

  public static let empty = Self(
    kind: .fade,
    start: 1,
    end: 1,
    duration: 0.01,
    delay: 0,
    timingFunction: .linear
  )

  public static let defaultLeadingSlideDistance = -Double.infinity
  public static let defaultTrailingSlideDistance = Double.infinity

  public let kind: Kind
  public let start: Double
  public let end: Double
  public let duration: TimeInterval
  public let delay: TimeInterval
  public let timingFunction: TimingFunction

  public init(
    kind: Kind,
    start: Double,
    end: Double,
    duration: TimeInterval,
    delay: TimeInterval,
    timingFunction: TimingFunction
  ) {
    self.kind = kind
    self.start = start
    self.end = end
    self.duration = duration
    self.delay = delay
    self.timingFunction = timingFunction
  }
}

extension [TransitioningAnimation] {
  func withDelay(_ delay: Double) -> [TransitioningAnimation] {
    if delay == 0 {
      return self
    }
    return map {
      TransitioningAnimation(
        kind: $0.kind,
        start: $0.start,
        end: $0.end,
        duration: $0.duration,
        delay: $0.delay + delay,
        timingFunction: $0.timingFunction
      )
    }
  }

  func sortedChronologically() -> Self {
    stableSort { $0.delay <= $1.delay }
  }
}
