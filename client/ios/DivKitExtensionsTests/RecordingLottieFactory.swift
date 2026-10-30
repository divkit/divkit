import DivKitExtensions

@MainActor
final class RecordingLottieFactory: @MainActor AsyncSourceAnimatableViewFactory {
  var holdSource = false
  var supportsActions = true
  private(set) var views: [RecordingLegacyAnimationView] = []
  private(set) var repeatModes: [AnimationRepeatMode] = []
  private(set) var repeatCounts: [Float] = []

  func createAsyncSourceAnimatableView(
    withMode mode: AnimationRepeatMode,
    repeatCount: Float
  ) -> AsyncSourceAnimatableView {
    let view = supportsActions
      ? RecordingLottieView(holdSource: holdSource)
      : RecordingLegacyAnimationView(holdSource: holdSource)
    views.append(view)
    repeatModes.append(mode)
    repeatCounts.append(repeatCount)
    return view
  }
}
