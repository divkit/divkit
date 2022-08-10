// Copyright 2018 Yandex LLC. All rights reserved.

import QuartzCore

@objc
extension CAMediaTimingFunction {
  public static let linear =
    CAMediaTimingFunction(name: .linear)
  public static let easeIn =
    CAMediaTimingFunction(name: .easeIn)
  public static let easeOut =
    CAMediaTimingFunction(name: .easeOut)
  public static let easeInEaseOut =
    CAMediaTimingFunction(name: .easeInEaseOut)
}
