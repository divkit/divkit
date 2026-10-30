/// Opt-in support for Lottie start/stop extension actions.
@MainActor
public protocol LottiePlaybackControlling: AsyncSourceAnimatableView {
  /// Stops playback and restores the first frame and forward direction, preserving repeats.
  func reset()
}
