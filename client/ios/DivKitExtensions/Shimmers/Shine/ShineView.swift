import UIKit

import LayoutKit
import VGSL

final class ShineView: UIView {
  private static let animationKey = "ShineEffect"

  private var childView: BlockView!
  private var maskLayer = CALayer()
  private var gradientLayer = CAGradientLayer()

  private var params: ShineExtensionParams?
  private var animation: CAAnimation?
  private var maskImageHolder: ImageHolder?
  private var maskImageRequest: Cancellable?

  private var repeatCount: CGFloat = 0

  init() {
    super.init(frame: .zero)

    gradientLayer.mask = maskLayer
    layer.addSublayer(gradientLayer)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    childView.frame = bounds
    maskLayer.frame = bounds
    gradientLayer.frame =
      ShimmerGradientGeometry.frameScaledToAspectFill(for: bounds)
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    if window == nil {
      stopAnimation()
    } else {
      startAnimation()
    }
  }

  func configure(
    child: Block,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?,
    params: ShineExtensionParams,
    maskImageHolder: ImageHolder
  ) {
    maskImageRequest?.cancel()
    prepareForReuse(params: params)
    childView = child.reuse(
      childView,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      superview: self,
      subviewPosition: .index(0)
    )

    self.maskImageHolder = maskImageHolder
    maskImageRequest = maskImageHolder.requestImageWithCompletion {
      [weak self] image in
      guard let self, maskImageHolder === self.maskImageHolder, let image else {
        return
      }

      maskLayer.contents = image.cgImage
      configureGradientAndAnimation(params: params)
      startAnimation()
    }

    setNeedsLayout()
  }
}

extension ShineView: CAAnimationDelegate {
  func animationDidStart(_: CAAnimation) {
    repeatCount -= 1
    params?.onCycleStartActions?.forEach { $0.perform(sendingFrom: self) }
  }

  func animationDidStop(_: CAAnimation, finished flag: Bool) {
    guard flag, let animation, repeatCount > 0 else { return }

    animation.beginTime = CACurrentMediaTime()
    restartAnimation()
  }
}

extension ShineView {
  private func startAnimation() {
    guard gradientLayer.animation(forKey: Self.animationKey) == nil
    else { return }
    if let animation {
      gradientLayer.add(animation, forKey: Self.animationKey)
    }
  }

  private func stopAnimation() {
    gradientLayer.removeAnimation(forKey: Self.animationKey)
  }

  private func restartAnimation() {
    if gradientLayer.animation(forKey: Self.animationKey) != nil {
      stopAnimation()
      startAnimation()
    }
  }

  private func prepareForReuse(params: ShineExtensionParams) {
    guard self.params != params else { return }

    self.params = nil
    self.animation = nil
    self.maskImageHolder = nil
    self.maskImageRequest = nil
    self.repeatCount = 0
    self.maskLayer.contents = nil

    stopAnimation()
  }

  private func configureGradientAndAnimation(params: ShineExtensionParams) {
    guard self.params != params, params.isEnabled else { return }

    let (startPoint, endPoint) = ShimmerGradientGeometry.points(for: params.figmaAngle)
    gradientLayer.startPoint = startPoint
    gradientLayer.endPoint = endPoint
    gradientLayer.colors = params.colorsAndLocations.colors.map(\.cgColor)
    gradientLayer.locations = params.colorsAndLocations.toValues

    let basicAnimation = CABasicAnimation(keyPath: #keyPath(CAGradientLayer.locations))
    basicAnimation.fromValue = params.colorsAndLocations.fromValues
    basicAnimation.toValue = params.colorsAndLocations.toValues
    basicAnimation.duration = params.durationInSeconds
    basicAnimation.timingFunction = .easeInEaseOut

    let groupAnimation = CAAnimationGroup()
    groupAnimation.animations = [basicAnimation]
    groupAnimation.duration = basicAnimation.duration + params.intervalInSeconds
    groupAnimation.beginTime = CACurrentMediaTime() + params.beginAfterInSeconds
    groupAnimation.fillMode = .forwards
    groupAnimation.isRemovedOnCompletion = false
    groupAnimation.delegate = self

    self.params = params
    self.animation = groupAnimation
    self.repeatCount = params.repetionsWithZeroAsInfinite
  }
}

extension ShineView: BlockViewProtocol {
  var effectiveBackgroundColor: UIColor? {
    childView.backgroundColor
  }
}

extension ShineView: VisibleBoundsTrackingContainer {
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    [childView]
  }
}

extension ShineExtensionParams {
  fileprivate var figmaAngle: CGFloat {
    360 - angle + 90
  }

  fileprivate var durationInSeconds: CGFloat {
    duration.msToSeconds
  }

  fileprivate var intervalInSeconds: CGFloat {
    interval.msToSeconds
  }

  fileprivate var beginAfterInSeconds: CGFloat {
    beginAfter.msToSeconds
  }

  fileprivate var repetionsWithZeroAsInfinite: CGFloat {
    repetitions == 0
      ? .greatestFiniteMagnitude
      : repetitions
  }
}

extension CGFloat {
  fileprivate var msToSeconds: CGFloat {
    self / 1000
  }
}
