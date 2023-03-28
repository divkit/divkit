import CoreGraphics
import Foundation
import UIKit

import CommonCorePublic

extension UIView {
  func perform(
    _ animations: [TransitioningAnimation]?,
    animated: Bool,
    completion: Action? = nil
  ) {
    guard let animations = animations, !animations.isEmpty else {
      completion?()
      return
    }
    let animationsParams = makeAnimationParams(animations: animations)
    let accumulator = CompletionAccumulator()
    for animationParam in animationsParams {
      let partialCompletion = accumulator.getPartialCompletion()
      if animated && window != nil {
        UIView.animate(
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
    animations: [TransitioningAnimation],
    completion: Action? = nil
  ) {
    for kind in TransitioningAnimation.Kind.allCases {
      if let firstOfKind = animations.first(where: { $0.kind == kind }) {
        setValue(firstOfKind.start, for: kind)
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
      alpha = value
    case .scaleXY:
      // https://medium.com/@sartha.tayade/problem-when-animating-a-uiview-to-scale-to-zero-using-cgaffinetransform-on-ios-2f50717add18
      let nonZeroValue = value.isZero ? .ulpOfOne : value
      transform = CGAffineTransform(scale: nonZeroValue)
    case .translationX:
      let x: CGFloat
      if value == TransitioningAnimation.defaultLeadingSlideDistance {
        x = -bounds.width
      } else if value == TransitioningAnimation.defaultTrailingSlideDistance {
        x = bounds.width
      } else {
        x = value
      }
      transform = CGAffineTransform(translationX: x, y: 0)
    case .translationY:
      let y: CGFloat
      if value == TransitioningAnimation.defaultLeadingSlideDistance {
        y = -bounds.height
      } else if value == TransitioningAnimation.defaultTrailingSlideDistance {
        y = bounds.height
      } else {
        y = value
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
        duration: animation.duration.value,
        delay: animation.delay.value,
        options: [
          .allowUserInteraction,
          animation.timingFunction.cast(),
        ]
      )
    }
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
    case .linear: return .curveLinear
    case .easeIn: return .curveEaseIn
    case .easeOut: return .curveEaseOut
    case .easeInEaseOut: return .curveEaseInOut
    }
  }
}
