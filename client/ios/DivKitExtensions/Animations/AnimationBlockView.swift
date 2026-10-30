#if os(iOS)
import DivKit
import LayoutKit
import UIKit
import VGSL

final class AnimationBlockView: BlockView {
  var animatableView: AsyncSourceAnimatableView? {
    didSet {
      if let animatablView = animatableView {
        oldValue?.removeFrom(self)
        addSubview(animatablView)
      }
    }
  }

  var animationContentMode: UIView.ContentMode = .scaleAspectFit {
    didSet {
      animatableView?.contentMode = animationContentMode
    }
  }

  var isPlaying: Bool = true {
    didSet {
      guard oldValue != isPlaying, !hasPlaybackCommand else {
        return
      }
      if isPlaying {
        animatableView?.play()
      } else {
        animatableView?.pause()
      }
    }
  }

  var playbackState: LottiePlaybackState? {
    didSet {
      guard oldValue != playbackState, isSourceReady else {
        return
      }
      applyPlayback()
    }
  }

  var animationHolder: AnimationHolder? {
    didSet {
      animationRequest?.cancel()
      isSourceReady = false

      let newValue = animationHolder
      animationRequest = animationHolder?
        .requestAnimationWithCompletion { [weak self] animationSource in
          guard let self,
                newValue === self.animationHolder,
                let animationSource,
                let view = self.animatableView else {
            return
          }

          view.contentMode = animationContentMode
          Task { @MainActor in
            await view.setSourceAsync(animationSource)
            guard newValue === self.animationHolder, view === self.animatableView else {
              return
            }
            self.isSourceReady = true
            self.applyPlayback()
          }
        }
    }
  }

  let effectiveBackgroundColor: UIColor? = nil

  private var animationRequest: Cancellable?
  private var isSourceReady = false

  private var hasPlaybackCommand: Bool {
    playbackState != nil && animatableView is LottiePlaybackControlling
  }

  init() {
    super.init(frame: .zero)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    animatableView?.frame = bounds
  }
  private func applyPlayback() {
    guard let view = animatableView else {
      return
    }
    guard let playbackState, let controllable = view as? LottiePlaybackControlling else {
      if playbackState != nil {
        DivKitLogger.error("Lottie view must implement LottiePlaybackControlling for extension actions")
      }
      if isPlaying {
        view.play()
      } else {
        view.pause()
      }
      return
    }
    controllable.reset()
    if playbackState.command == .start {
      controllable.play()
    }
  }
}

extension AnimationBlockView: VisibleBoundsTrackingLeaf {}
#endif
