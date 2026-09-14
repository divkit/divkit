#if os(iOS)
import CoreGraphics
import Foundation
import UIKit
import VGSL

extension UIView {
  func perform(
    _ animations: [TransitioningAnimation]?,
    animated: Bool,
    completion: Action? = nil
  ) {
    guard let animations, !animations.isEmpty else {
      completion?()
      return
    }
    let animationsParams = makeAnimationParams(animations: animations)
    let accumulator = CompletionAccumulator()
    for animationParam in animationsParams {
      let partialCompletion = accumulator.getPartialCompletion()
      if animated, window != nil {
        UIView.animateTransition(
          withDuration: animationParam.duration,
          delay: animationParam.delay,
          options: animationParam.options,
          animations: animationParam.block,
          completion: { _ in partialCompletion() }
        )
      } else {
        animationParam.block()
        partialCompletion()
      }
    }

    accumulator.whenAllPartialCompletionsCalled {
      completion?()
    }
  }

  func setInitialParamsAndAnimate(
    animations: [TransitioningAnimation]?,
    completion: Action? = nil
  ) {
    guard let animations else {
      completion?()
      return
    }

    UIView.performWithoutAnimation {
      for kind in TransitioningAnimation.Kind.allCases {
        if let firstOfKind = animations.first(where: { $0.kind == kind }) {
          setValue(firstOfKind.start, for: kind)
        }
      }
    }
    perform(animations, animated: true, completion: completion)
  }

  private func setValue(
    _ value: CGFloat,
    for animationKind: TransitioningAnimation.Kind
  ) {
    switch animationKind {
    case .fade:
      setOpacity(value)
    case .scaleXY:
      // https://medium.com/@sartha.tayade/problem-when-animating-a-uiview-to-scale-to-zero-using-cgaffinetransform-on-ios-2f50717add18
      let nonZeroValue = value.isZero ? .ulpOfOne : value
      transform = CGAffineTransform(scale: nonZeroValue)
    case .translationX:
      let x: CGFloat = if value == TransitioningAnimation.defaultLeadingSlideDistance {
        -bounds.width
      } else if value == TransitioningAnimation.defaultTrailingSlideDistance {
        bounds.width
      } else {
        value
      }
      transform = CGAffineTransform(translationX: x, y: 0)
    case .translationY:
      let y: CGFloat = if value == TransitioningAnimation.defaultLeadingSlideDistance {
        -bounds.height
      } else if value == TransitioningAnimation.defaultTrailingSlideDistance {
        bounds.height
      } else {
        value
      }
      transform = CGAffineTransform(translationX: 0, y: y)
    }
  }

  private func makeAnimationParams(
    animations: [TransitioningAnimation]
  ) -> [AnimationParams] {
    animations.map { animation in
      (
        block: { self.setValue(animation.end, for: animation.kind) },
        duration: animation.duration,
        delay: animation.delay,
        options: [
          .allowUserInteraction,
          animation.timingFunction.cast(),
        ]
      )
    }
  }

  @objc func setOpacity(_ value: CGFloat) {
    self.alpha = value
  }
}

private typealias AnimationParams = (
  block: Action,
  duration: TimeInterval,
  delay: TimeInterval,
  options: UIView.AnimationOptions
)

extension TimingFunction {
  func cast() -> UIView.AnimationOptions {
    switch self {
    case .linear: .curveLinear
    case .easeIn: .curveEaseIn
    case .easeOut: .curveEaseOut
    case .easeInEaseOut: .curveEaseInOut
    }
  }
}

extension UIView {
  /// Depth of `withoutInheritedAnimation` regions currently on the stack.
  private static var inheritedAnimationSuppressionDepth = 0

  /// Runs `body` with implicit animations disabled, so that the view changes
  /// made inside it (a new subtree being built or laid out) do not inherit an
  /// enclosing `UIView.animate` block. Explicit transition animations started
  /// inside `body` go through `animateTransition`, which lifts the suppression
  /// for its own animation only, so nested `div-state` switches and
  /// `transition_in` of new views keep animating with their own parameters.
  static func withoutInheritedAnimation(_ body: () -> Void) {
    guard inheritedAnimationDuration > 0 else {
      body()
      return
    }
    inheritedAnimationSuppressionDepth += 1
    defer { inheritedAnimationSuppressionDepth -= 1 }
    performWithoutAnimation(body)
  }

  /// `UIView.animate` for an explicit transition: the transition keeps its own
  /// duration and curve inside a host animation block, and still runs when it
  /// is started from a `withoutInheritedAnimation` region.
  static func animateTransition(
    withDuration duration: TimeInterval,
    delay: TimeInterval,
    options: UIView.AnimationOptions,
    animations: @escaping () -> Void,
    completion: ((Bool) -> Void)? = nil
  ) {
    let liftsSuppression = inheritedAnimationSuppressionDepth > 0 && !areAnimationsEnabled
    if liftsSuppression {
      setAnimationsEnabled(true)
    }
    defer {
      if liftsSuppression {
        setAnimationsEnabled(false)
      }
    }
    animate(
      withDuration: duration,
      delay: delay,
      options: options.union([.overrideInheritedDuration, .overrideInheritedCurve]),
      animations: animations,
      completion: completion
    )
  }
}
#endif
