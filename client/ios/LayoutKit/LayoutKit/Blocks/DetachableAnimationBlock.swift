import Foundation
import VGSL

public struct ChangeBoundsTransition: Equatable {
  public let duration: TimeInterval
  public let delay: TimeInterval
  public let timingFunction: TimingFunction

  public init(
    duration: TimeInterval,
    delay: TimeInterval,
    timingFunction: TimingFunction
  ) {
    self.duration = duration
    self.delay = delay
    self.timingFunction = timingFunction
  }
}

public final class DetachableAnimationBlock: WrapperBlock, LayoutCachingDefaultImpl, Identifiable {
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

extension DetachableAnimationBlock: ElementStateUpdating {
  public func updated(withStates _: BlocksState) throws -> Self {
    guard animationIn != nil else { return self }

    return Self(
      child: child,
      id: id,
      animationIn: nil,
      animationOut: animationOut,
      animationChange: animationChange
    )
  }
}

extension DetachableAnimationBlock: ElementFocusUpdating {
  public func updated(path: UIElementPath, isFocused: Bool) throws -> Self {
    let newChild = try child.updated(path: path, isFocused: isFocused)
    if newChild === child {
      return self
    }

    return Self(
      child: newChild,
      id: id,
      animationIn: nil,
      animationOut: animationOut,
      animationChange: animationChange
    )
  }
}
