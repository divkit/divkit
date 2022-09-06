import Foundation
import DivKitExtensions
import Lottie

final class LottieAnimationFactory: AnimatableViewFactory {
  public func createAnimatableView(withMode mode: AnimationRepeatMode, repeatCount count: Float) -> AnimatableView {
    let animationView = AnimationView()
    switch mode {
    case .restart:
      animationView.loopMode = count == -1 ? .loop : .repeat(count)
    case .reverse:
      animationView.loopMode = count == -1 ? .autoReverse : .repeatBackwards(count)
    }
    return animationView
  }
}

extension Lottie.AnimationView: AnimatableView {
  public func play() {
    self.play(completion: nil)
    self.forceDisplayUpdate()
  }

  public func setSource(_ source: AnimationSourceType) {
    var animation: Lottie.Animation?
    switch source {
    case .data(let data):
      animation = try? JSONDecoder().decode(Animation.self, from: data)
    case .json(let json):
      animation = try? Lottie.Animation(dictionary: json)
    }
    self.animation = animation
  }
}
