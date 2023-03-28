import CoreGraphics

import CommonCorePublic

public struct TransitioningAnimation: Equatable {
  @frozen
  public enum Kind: String, Equatable, CaseIterable {
    case fade
    case scaleXY
    case translationX
    case translationY
  }

  public let kind: Kind
  public let start: Double
  public let end: Double
  public let duration: Duration
  public let delay: Delay
  public let timingFunction: TimingFunction

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

  public init(
    kind: Kind,
    start: Double,
    end: Double,
    duration: Duration,
    delay: Delay,
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

extension Array where Element == TransitioningAnimation {
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
        delay: Delay($0.delay.value + delay),
        timingFunction: $0.timingFunction
      )
    }
  }

  func sortedChronologically() -> Self {
    stableSort { $0.delay.value <= $1.delay.value }
  }
}
