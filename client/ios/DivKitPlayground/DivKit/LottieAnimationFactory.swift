import DivKitExtensions
import Foundation
import Lottie

final class LottieAnimationFactory: AsyncSourceAnimatableViewFactory {
  func createAsyncSourceAnimatableView(
    withMode mode: AnimationRepeatMode,
    repeatCount count: Float
  ) -> AsyncSourceAnimatableView {
    let animationView = LottieAnimationView()
    switch mode {
    case .restart:
      animationView.loopMode = count == -1 ? .loop : .repeat(count)
    case .reverse:
      animationView.loopMode = count == -1 ? .autoReverse : .repeatBackwards(count / 2)
    }
    return animationView
  }
}

extension LottieAnimationView: DivKitExtensions.AsyncSourceAnimatableView {
  public func play() {
    self.play(completion: nil)
    self.forceDisplayUpdate()
  }

  public func setSourceAsync(_ source: AnimationSourceType) async {
    guard let source = source as? LottieAnimationSourceType else {
      return
    }
    animation = await Task.detached(priority: .userInitiated) {
      switch source {
      case let .data(data):
        try? JSONDecoder().decode(LottieAnimation.self, from: data)
      case let .json(json):
        try? LottieAnimation(dictionary: json)
      }
    }.value
  }
}
