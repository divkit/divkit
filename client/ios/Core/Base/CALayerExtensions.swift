// Copyright 2018 Yandex LLC. All rights reserved.

import QuartzCore

extension CALayer {
  // FMI:
  // https://developer.apple.com/library/content/documentation/Cocoa/Conceptual/CoreAnimation_guide/AdvancedAnimationTricks/AdvancedAnimationTricks.html#//apple_ref/doc/uid/TP40004514-CH8-SW2
  public func setSpeedAdjusted(_ speed: Float) {
    let current = CACurrentMediaTime()
    timeOffset = convertTime(current, from: nil)
    beginTime = current
    self.speed = speed
  }

  public var currentTime: TimeInterval {
    convertTime(CACurrentMediaTime(), from: nil)
  }

  public func addSublayers(_ layers: [CALayer]) {
    for layer in layers {
      addSublayer(layer)
    }
  }

  public func addAnimation(_ animation: CAAnimation, forKey key: AnimationKey) {
    add(animation, forKey: key.value)
  }

  public func addAnimation(_ animation: CAAnimation) {
    add(animation, forKey: nil)
  }

  public func removeAnimation(forKey key: AnimationKey) {
    removeAnimation(forKey: key.value)
  }

  public func disableImplicitAnimations() {
    actions = [
      #keyPath(CALayer.frame): NSNull(),
      #keyPath(CALayer.bounds): NSNull(),
      #keyPath(CALayer.position): NSNull(),
      #keyPath(CALayer.opacity): NSNull(),
    ]
  }
}

public struct AnimationKey {
  fileprivate let value: String

  public init(_ value: String) {
    self.value = value
  }
}

extension CACornerMask {
  public static let allCorners: CACornerMask = [
    .layerMinXMinYCorner, .layerMaxXMinYCorner, .layerMinXMaxYCorner, .layerMaxXMaxYCorner,
  ]
}
