// Copyright 2022 Yandex LLC. All rights reserved.

import UIKit

public protocol ImageViewProtocol {
  var appearanceAnimation: ImageViewAnimation? { get set }
  var imageRedrawingStyle: ImageRedrawingStyle? { get set }
  var imageContentMode: ImageContentMode { get set }
}

public struct ImageViewAnimation {
  let duration: Double
  let delay: Double
  let startAlpha: Double
  let endAlpha: Double
  let options: UIView.AnimationOptions

  public init(
    duration: Double,
    delay: Double,
    startAlpha: Double,
    endAlpha: Double,
    options: UIView.AnimationOptions
  ) {
    self.duration = duration
    self.delay = delay
    self.startAlpha = startAlpha
    self.endAlpha = endAlpha
    self.options = options
  }
}
