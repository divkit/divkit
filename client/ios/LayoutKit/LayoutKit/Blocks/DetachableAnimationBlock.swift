import CommonCorePublic

public struct ChangeBoundsTransition: Equatable {
  public let duration: Duration
  public let delay: Delay
  public let timingFunction: TimingFunction

  public init(
    duration: Duration,
    delay: Delay,
    timingFunction: TimingFunction
  ) {
    self.duration = duration
    self.delay = delay
    self.timingFunction = timingFunction
  }
}

public final class DetachableAnimationBlock: WrapperBlock, LayoutCachingDefaultImpl {
  public let child: Block
  public let id: String
  public let animationIn: [TransitioningAnimation]?
  public let animationOut: [TransitioningAnimation]?
  public let animationChange: ChangeBoundsTransition?

  public init(
    child: Block,
    id: String,
    animationIn: [TransitioningAnimation]?,
    animationOut: [TransitioningAnimation]?,
    animationChange: ChangeBoundsTransition?
  ) {
    self.child = child
    self.id = id
    self.animationIn = animationIn
    self.animationOut = animationOut
    self.animationChange = animationChange
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? DetachableAnimationBlock else {
      return false
    }
    return self == other
  }

  public func makeCopy(wrapping child: Block) -> DetachableAnimationBlock {
    DetachableAnimationBlock(
      child: child,
      id: id,
      animationIn: animationIn,
      animationOut: animationOut,
      animationChange: animationChange
    )
  }
}

extension DetachableAnimationBlock: Equatable {
  public static func ==(
    lhs: DetachableAnimationBlock,
    rhs: DetachableAnimationBlock
  ) -> Bool {
    lhs.child == rhs.child
      && lhs.id == rhs.id
      && lhs.animationIn == rhs.animationIn
      && lhs.animationOut == rhs.animationOut
      && lhs.animationChange == rhs.animationChange
  }
}

extension DetachableAnimationBlock: CustomDebugStringConvertible {
  public var debugDescription: String { "DetachableAnimationBlock" }
}
