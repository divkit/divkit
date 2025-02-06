import DivKitExtensions
import Foundation
import Lottie

final class LottieAnimationFactory: AsyncSourceAnimatableViewFactory {
  public func createAsyncSourceAnimatableView(
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
    animation = await withCheckedContinuation { continuation in
      // We need to make sure we parse the source on a background thread
      // but LottieAnimation isn't sendable. So to transfer it from the bg
      // thread back to the main thread we use `withCheckedContinuation`
      // because it has its return type marked as `sending` which allows
      // us to transfer a non-sendable type.
      // We can't achieve the same with a bare `Task{...}.value`.
      Task.detached(priority: .userInitiated) {
        let animation =
          switch source {
        case let .data(data):
          try? JSONDecoder().decode(LottieAnimation.self, from: data)
        case let .json(json):
          try? LottieAnimation(dictionary: json)
        }
        continuation.resume(returning: animation)
      }
    }
  }
}
