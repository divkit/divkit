// Copyright 2019 Yandex LLC. All rights reserved.

import UIKit

public protocol ViewProtocol: AnyObject {
  static func animate(
    duration: TimeInterval,
    animations: @escaping Action,
    completion: @escaping (Bool) -> Void
  )

  static func animate(
    duration: TimeInterval,
    usingSpringWithDamping: CGFloat,
    initialSpringVelocity: CGFloat,
    animations: @escaping Action,
    completion: @escaping (Bool) -> Void
  )

  var isUserInteractionEnabled: Bool { get set }
  var frame: CGRect { get set }
  var alpha: CGFloat { get set }
  var backgroundColor: SystemColor? { get set }

  func insertSubview(_ subview: Self, belowSubview: Self)
  func removeFromSuperview()

  init(frame: CGRect)
}

extension UIView: ViewProtocol {
  public static func animate(
    duration: TimeInterval,
    animations: @escaping () -> Void,
    completion: @escaping (Bool) -> Void
  ) {
    self.animate(
      withDuration: duration,
      delay: 0,
      options: [],
      animations: animations,
      completion: completion
    )
  }

  public static func animate(
    duration: TimeInterval,
    usingSpringWithDamping: CGFloat,
    initialSpringVelocity: CGFloat,
    animations: @escaping Action,
    completion: @escaping (Bool) -> Void
  ) {
    self.animate(
      withDuration: duration,
      delay: 0,
      usingSpringWithDamping: usingSpringWithDamping,
      initialSpringVelocity: initialSpringVelocity,
      options: [],
      animations: animations,
      completion: completion
    )
  }
}
