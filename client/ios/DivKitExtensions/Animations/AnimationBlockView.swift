import UIKit

import CommonCorePublic
import LayoutKit

final class AnimationBlockView: BlockView {
  var animatableView: AnimatableView? {
    didSet {
      if let animatablView = animatableView {
        oldValue?.removeFromSuperview()
        addSubview(animatablView)
      }
    }
  }

  private var animationRequest: Cancellable?
  var animationHolder: AnimationHolder? {
    didSet {
      animationRequest?.cancel()

      let newValue = animationHolder
      animationRequest = animationHolder?
        .requestAnimationWithCompletion { [weak self] animationSource in
          guard let self,
                newValue === self.animationHolder,
                let animationSource else {
            return
          }

          self.animatableView?.setSource(animationSource)
          self.animatableView?.play()
        }
    }
  }

  init() {
    super.init(frame: .zero)
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    animatableView?.frame = bounds
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  let effectiveBackgroundColor: UIColor? = nil
}

extension AnimationBlockView: VisibleBoundsTrackingLeaf {}
