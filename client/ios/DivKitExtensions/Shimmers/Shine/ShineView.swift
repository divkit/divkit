import UIKit

import VGSL

final class ShineView: ShimmerView {
  private var maskLayer = CALayer()
  private var maskImageHolder: ImageHolder?
  private var maskImageRequest: Cancellable?

  init() {
    super.init(frame: .zero)

    layer.mask = maskLayer
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    maskLayer.frame = bounds
  }

  func configureShine(
    style: ShineStyle,
    effectBeginTime: CFTimeInterval,
    maskImageHolder: ImageHolder
  ) {
    maskImageRequest?.cancel()
    configureShimmer(
      colorsAndLocations: style.colorsAndLocations,
      angle: style.figmaAngle,
      gradientIdleState: .start,
      animationParams: ShimmerView.AnimationParams(
        effectBeginTime: effectBeginTime + style.beginAfter,
        repeatCount: style.repetitions,
        duration: style.duration,
        timingFunction: .easeInEaseOut,
        interval: style.interval
      )
    )

    self.maskImageHolder = maskImageHolder
    maskImageRequest = maskImageHolder.requestImageWithCompletion { [weak self] image in
      guard let self,
            maskImageHolder === self.maskImageHolder else {
        return
      }

      if let image {
        maskLayer.contents = image.cgImage
        startAnimation()
      }
    }
  }
}

extension ShineStyle {
  fileprivate var figmaAngle: CGFloat {
    360 - angle + 90
  }
}
