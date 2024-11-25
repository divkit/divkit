import UIKit

import VGSL

class ShimmerView: UIView {
  enum GradientIdleState {
    case start
    case middle
  }

  struct AnimationParams {
    var effectBeginTime: CFTimeInterval = CACurrentMediaTime()
    var repeatCount: CGFloat = 0
    var duration: CGFloat = 1
    var timingFunction: CAMediaTimingFunction = .linear
    var interval: CGFloat = 0
  }

  private static let animationKey = "ShimmerEffect"

  private var animation: CAAnimation?

  private var gradientLayer = CAGradientLayer()
  private var prevBounds: CGRect = .zero

  override init(frame: CGRect) {
    super.init(frame: frame)

    layer.addSublayer(gradientLayer)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    scaleGradientLayerToAspectFill()

    if prevBounds != bounds {
      if gradientLayer.animation(forKey: Self.animationKey) != nil {
        gradientLayer.removeAnimation(forKey: Self.animationKey)
        if let animation {
          gradientLayer.add(animation, forKey: Self.animationKey)
        }
      }
    }
    prevBounds = bounds
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    if window == nil {
      stopAnimation()
    } else {
      startAnimation()
    }
  }

  func configureShimmer(
    colorsAndLocations: [ColorAndLocation],
    angle: CGFloat,
    gradientIdleState: GradientIdleState,
    animationParams: AnimationParams
  ) {
    stopAnimation()
    animation = nil

    let basicAnimation = CABasicAnimation(keyPath: #keyPath(CAGradientLayer.locations))
    basicAnimation.fromValue = colorsAndLocations.fromValues
    basicAnimation.toValue = colorsAndLocations.toValues
    basicAnimation.timingFunction = animationParams.timingFunction
    basicAnimation.duration = animationParams.duration

    let animation: CAAnimation
    if animationParams.interval > 0 {
      let groupAnimation = CAAnimationGroup()
      groupAnimation.animations = [basicAnimation]
      groupAnimation.duration = animationParams.duration + animationParams.interval
      animation = groupAnimation
    } else {
      animation = basicAnimation
    }
    animation.beginTime = animationParams.effectBeginTime
    animation.repeatCount = animationParams.repeatCount == 0
      ? .greatestFiniteMagnitude
      : Float(animationParams.repeatCount)

    self.animation = animation

    configureGradient(
      colorsAndLocations: colorsAndLocations,
      angle: angle,
      gradientIdleState: gradientIdleState
    )
  }

  func startAnimation() {
    guard gradientLayer.animation(forKey: Self.animationKey) == nil
    else { return }
    if let animation {
      gradientLayer.add(animation, forKey: Self.animationKey)
    }
  }

  func stopAnimation() {
    gradientLayer.removeAllAnimations()
  }

  private func configureGradient(
    colorsAndLocations: [ColorAndLocation],
    angle: CGFloat,
    gradientIdleState: GradientIdleState
  ) {
    gradientLayer.calculatePoints(for: angle)
    gradientLayer.colors = colorsAndLocations.map(\.color.cgColor)
    gradientLayer.locations = switch gradientIdleState {
    case .start:
      colorsAndLocations.fromValues
    case .middle:
      colorsAndLocations
        .map(\.location)
        .map { NSNumber(value: $0.native) }
    }
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

extension [ColorAndLocation] {
  fileprivate var fromValues: [NSNumber] {
    guard let max = locations.max() else { return [] }
    return locations.map { NSNumber(value: ($0 - max).native) }
  }

  fileprivate var toValues: [NSNumber] {
    guard let min = locations.min() else { return [] }
    let addToAll = 1 - min
    return locations.map { NSNumber(value: ($0 + addToAll).native) }
  }

  private var locations: [CGFloat] {
    map(\.location)
  }
}

extension CAGradientLayer {
  fileprivate func calculatePoints(for angle: CGFloat) {
    var ang = (-angle).truncatingRemainder(dividingBy: 360)

    if ang < 0 { ang = 360 + ang }

    let n: CGFloat = 0.5

    switch ang {
    case 0...45, 315...360:
      startPoint = CGPoint(x: 0, y: n * tanx(ang) + n)
      endPoint = CGPoint(x: 1, y: n * tanx(-ang) + n)

    case 45...135:
      startPoint = CGPoint(x: n * tanx(ang - 90) + n, y: 1)
      endPoint = CGPoint(x: n * tanx(-ang - 90) + n, y: 0)

    case 135...225:
      startPoint = CGPoint(x: 1, y: n * tanx(-ang) + n)
      endPoint = CGPoint(x: 0, y: n * tanx(ang) + n)

    case 225...315:
      startPoint = CGPoint(x: n * tanx(-ang - 90) + n, y: 0)
      endPoint = CGPoint(x: n * tanx(ang - 90) + n, y: 1)

    default:
      startPoint = CGPoint(x: 0, y: n)
      endPoint = CGPoint(x: 1, y: n)
    }
  }

  private func tanx(_ theta: CGFloat) -> CGFloat {
    tan(theta * CGFloat.pi / 180)
  }
}
