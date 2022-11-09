import UIKit

import Base

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

public struct ImageRedrawingStyle: Equatable {
  let tintColor: Color
  let tintMode: TintMode?

  public init(tintColor: Color, tintMode: TintMode? = nil) {
    self.tintColor = tintColor
    self.tintMode = tintMode
  }

  public enum TintMode {
    case sourceIn
    case sourceAtop
    case darken
    case lighten
    case multiply
    case screen
  }
}
