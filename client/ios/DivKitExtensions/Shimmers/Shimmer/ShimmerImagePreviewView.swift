import UIKit
import VGSLFundamentals

final class ShimmerImagePreviewView: UIView {
  private static let animationKey = "ShimmerEffect"

  private var gradientLayer: CAGradientLayer
  private let style: ShimmerImagePreviewStyle
  private var prevBounds: CGRect = .zero
  private let effectBeginTime: CFTimeInterval

  private lazy var animation: CABasicAnimation = {
    let animation = CABasicAnimation(keyPath: #keyPath(CAGradientLayer.locations))
    animation.beginTime = effectBeginTime
    return animation
  }()

  init(style: ShimmerImagePreviewStyle?, effectBeginTime: CFTimeInterval?) {
    self.style = style ?? defaultStyle
    self.effectBeginTime = effectBeginTime ?? CACurrentMediaTime()
    self.gradientLayer = CAGradientLayer()
    super.init(frame: .zero)

    layer.addSublayer(gradientLayer)
  }

  required init?(coder _: NSCoder) {
    preconditionFailure("init(coder:) has not been implemented")
    return nil
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    if let cornerRadius = style.cornerRadius {
      if let unifiedRadius = cornerRadius.unifiedRadius {
        let radius = clamp(
          unifiedRadius,
          min: 0,
          max: bounds.size.minDimension.half
        )

        layer.cornerRadius = radius
        layer.masksToBounds = true
      } else {
        let maskLayer = CAShapeLayer()
        maskLayer.path = .roundedRect(size: bounds.size, cornerRadii: cornerRadius)
        layer.mask = maskLayer
        layer.maskedCorners = .allCorners
      }
    }

    gradientLayer.frame = ShimmerGradientGeometry.frameScaledToAspectFill(for: bounds)
    animation.apply(style: style)
    gradientLayer.apply(style: style)

    if prevBounds != bounds {
      if gradientLayer.animation(forKey: Self.animationKey) != nil {
        gradientLayer.removeAnimation(forKey: Self.animationKey)
        gradientLayer.add(animation, forKey: Self.animationKey)
      }
    }
    prevBounds = bounds
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    if window == nil {
      stopShimmerAnimation()
    } else {
      startShimmerAnimation()
    }
  }

  private func startShimmerAnimation() {
    animation.apply(style: style)
    guard
      gradientLayer.animation(forKey: Self.animationKey) == nil
    else { return }
    gradientLayer.add(animation, forKey: Self.animationKey)
  }

  private func stopShimmerAnimation() {
    gradientLayer.removeAllAnimations()
  }
}

extension CAGradientLayer {
  fileprivate func apply(style: ShimmerImagePreviewStyle) {
    (startPoint, endPoint) = ShimmerGradientGeometry.points(for: style.angle)

    colors = style.colorsAndLocations.colors.map(\.cgColor)
    locations = style.colorsAndLocations.locations.map { NSNumber(value: $0.native) }
  }
}

extension CABasicAnimation {
  fileprivate func apply(style: ShimmerImagePreviewStyle) {
    fromValue = style.colorsAndLocations.fromValues
    toValue = style.colorsAndLocations.toValues
    repeatCount = .infinity
    duration = CFTimeInterval(style.duration)
  }
}

private let defaultStyle = ShimmerImagePreviewStyle(
  colorsAndLocations: [
    ColorAndLocation(color: .gray, location: 0.3),
    ColorAndLocation(color: .lightGray, location: 0.5),
    ColorAndLocation(color: .gray, location: 0.7),
  ],
  angle: 0,
  duration: 1.6
)
