import UIKit

final class ShimmerView: UIView {
  private static let animationKey = "ShimmerEffect"

  private var gradientLayer: CAGradientLayer
  private let style: ShimmerStyle
  private var prevBounds: CGRect = .zero
  private let effectBeginTime: CFTimeInterval

  private lazy var animation: CABasicAnimation = {
    let animation = CABasicAnimation(keyPath: #keyPath(CAGradientLayer.locations))
    animation.beginTime = effectBeginTime
    return animation
  }()

  init(style: ShimmerStyle?, effectBeginTime: CFTimeInterval?) {
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

    gradientLayer.frame = bounds
    animation.apply(style: style)
    gradientLayer.apply(style: style)

    scaleGradientLayerToAspectFill()

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

  private func scaleGradientLayerToAspectFill() {
    if frame.width > frame.height {
      gradientLayer.frame.origin = CGPoint(x: 0, y: -(frame.width - frame.height) / 2)
      gradientLayer.frame.size = CGSize(width: frame.width, height: frame.width)
    } else if frame.height > frame.width {
      gradientLayer.frame.origin = CGPoint(x: -(frame.height - frame.width) / 2, y: 0)
      gradientLayer.frame.size = CGSize(width: frame.height, height: frame.height)
    } else {
      gradientLayer.frame = bounds
    }
  }
}

extension CAGradientLayer {
  fileprivate func apply(style: ShimmerStyle) {
    calculatePoints(for: style.angle)

    colors = style.colorsAndLocations.map { $0.0.cgColor }
    locations = style.colorsAndLocations.map(\.1).map { NSNumber(value: $0.native) }
  }
}

extension CABasicAnimation {
  fileprivate func apply(style: ShimmerStyle) {
    fromValue = style.fromValues
    toValue = style.toValues
    repeatCount = .infinity
    duration = CFTimeInterval(style.duration)
  }
}

extension ShimmerStyle {
  fileprivate var fromValues: [NSNumber] {
    guard let max = colorsAndLocations.map(\.1).max() else { return [] }
    return colorsAndLocations.map(\.1).map { NSNumber(value: ($0 - max).native) }
  }

  fileprivate var toValues: [NSNumber] {
    guard let min = colorsAndLocations.map(\.1).min() else { return [] }
    let addToAll = 1 - min
    return colorsAndLocations.map(\.1).map { NSNumber(value: ($0 + addToAll).native) }
  }
}

extension CAGradientLayer {
  fileprivate func calculatePoints(for angle: CGFloat) {
    var ang = (-angle).truncatingRemainder(dividingBy: 360)

    if ang < 0 { ang = 360 + ang }

    let n: CGFloat = 0.5

    switch ang {
    case 0...45, 315...360:
      let a = CGPoint(x: 0, y: n * tanx(ang) + n)
      let b = CGPoint(x: 1, y: n * tanx(-ang) + n)
      startPoint = a
      endPoint = b

    case 45...135:
      let a = CGPoint(x: n * tanx(ang - 90) + n, y: 1)
      let b = CGPoint(x: n * tanx(-ang - 90) + n, y: 0)
      startPoint = a
      endPoint = b

    case 135...225:
      let a = CGPoint(x: 1, y: n * tanx(-ang) + n)
      let b = CGPoint(x: 0, y: n * tanx(ang) + n)
      startPoint = a
      endPoint = b

    case 225...315:
      let a = CGPoint(x: n * tanx(-ang - 90) + n, y: 0)
      let b = CGPoint(x: n * tanx(ang - 90) + n, y: 1)
      startPoint = a
      endPoint = b

    default:
      let a = CGPoint(x: 0, y: n)
      let b = CGPoint(x: 1, y: n)
      startPoint = a
      endPoint = b
    }
  }

  private func tanx(_ theta: CGFloat) -> CGFloat {
    tan(theta * CGFloat.pi / 180)
  }
}

private let defaultStyle = ShimmerStyle(
  colorsAndLocations: [(.gray, 0.3), (.lightGray, 0.5), (.gray, 0.7)],
  angle: 0,
  duration: 1.6
)
