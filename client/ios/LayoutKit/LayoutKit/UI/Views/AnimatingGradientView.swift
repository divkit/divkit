import UIKit

import CommonCorePublic

public final class AnimatingGradientView: UIView {
  private let gradientView: LinearGradientView
  private var gradientLayer: CALayer {
    gradientView.layer
  }

  public private(set) var isAnimationActive = false
  private var willEnterForegroundNotificationObserver: AnyObject!

  public init(color: Color = Color(white: 1, alpha: 0.8)) {
    let clear = color.withAlphaComponent(0)
    gradientView = LinearGradientView(.init(
      startColor: clear,
      intermediateColors: [color],
      endColor: clear,
      direction: .horizontal
    ))

    super.init(frame: .zero)
    isUserInteractionEnabled = false
    clipsToBounds = true
    addSubview(gradientView)
    gradientLayer.setValue(animationStartValue, forKeyPath: animationKeyPath)
    willEnterForegroundNotificationObserver = NotificationCenter.default.addObserver(
      forName: UIApplication.willEnterForegroundNotification,
      object: nil,
      queue: nil,
      using: { [unowned self] _ in
        self.restartAnimationIfNeeded()
      }
    )
  }

  deinit {
    NotificationCenter.default.removeObserver(willEnterForegroundNotificationObserver as Any)
  }

  public override func layoutSubviews() {
    super.layoutSubviews()
    gradientView.bounds = bounds
    gradientView.center = bounds.center
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public override func willMove(toWindow newWindow: UIWindow?) {
    if newWindow != nil {
      restartAnimationIfNeeded()
    }
  }

  private func restartAnimationIfNeeded() {
    if isAnimationActive {
      startAnimating()
    }
  }

  public func startAnimating() {
    gradientLayer.removeAnimation(forKey: animationKey)
    let animation = makeAnimation()
    gradientLayer.add(animation, forKey: animationKey)
    isAnimationActive = true
  }

  public func stopAnimating() {
    gradientLayer.removeAnimation(forKey: animationKey)
    isAnimationActive = false
  }
}

private let animationKey = "AnimatingGradientViewAnimation"
private let animationKeyPath = "anchorPoint.x"
private let animationStartValue: CGFloat = 1.5
private let animationEndValue: CGFloat = -0.5

private func makeAnimation() -> CABasicAnimation {
  let animation = CABasicAnimation(keyPath: animationKeyPath)
  animation.timingFunction = .linear
  animation.repeatCount = .infinity
  animation.duration = 1.5
  animation.fromValue = animationStartValue
  animation.toValue = animationEndValue
  return animation
}
