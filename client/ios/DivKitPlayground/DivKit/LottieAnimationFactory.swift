import DivKitExtensions
import Foundation
import Lottie

final class LottieAnimationFactory: AnimatableViewFactory {
  public func createAnimatableView(
    withMode mode: AnimationRepeatMode,
    repeatCount count: Float
  ) -> AnimatableView {
    let animationView = LottieAnimationView()
    switch mode {
    case .restart:
      animationView.loopMode = count == -1 ? .loop : .repeat(count)
    case .reverse:
      animationView.loopMode = count == -1 ? .autoReverse : .repeatBackwards(count)
    }
    return animationView
  }
}

extension LottieAnimationView: AnimatableView {
  public func play() {
    self.play(completion: nil)
    self.forceDisplayUpdate()
  }

  public func setSource(_ source: AnimationSourceType) {
    var animation: LottieAnimation?
    if let source = source as? LottieAnimationSourceType {
      switch source {
      case let .data(data):
        animation = try? JSONDecoder().decode(LottieAnimation.self, from: data)
      case let .json(json):
        animation = try? LottieAnimation(dictionary: json)
      }
      self.animation = animation
    }
  }
}
