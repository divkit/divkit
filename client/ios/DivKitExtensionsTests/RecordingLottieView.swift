import DivKitExtensions

@MainActor
final class RecordingLottieView: RecordingLegacyAnimationView,
  @MainActor LottiePlaybackControlling {
  var animationProgress: Double = 0

  func reset() {
    pause()
    animationProgress = 0
  }

  func finish(progress: Double) {
    animationProgress = progress
    isPlaying = false
  }
}
