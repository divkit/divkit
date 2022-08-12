// Copyright 2015 Yandex LLC. All rights reserved.

import UIKit

public final class RadialGradientView: UIView {
  private lazy var gradientLayer: CAGradientLayer = layer as! CAGradientLayer

  public override class var layerClass: AnyClass {
    CAGradientLayer.self
  }

  public var gradient: Gradient.Radial {
    didSet {
      guard oldValue != gradient else { return }
      setGradientProperties()
      setNeedsLayout()
    }
  }

  public init(_ gradient: Gradient.Radial) {
    self.gradient = gradient

    super.init(frame: .zero)

    gradientLayer.type = .radial
    setGradientProperties()
  }

  @available(*, unavailable)
  public required init(coder _: NSCoder) {
    fatalError()
  }

  private func setGradientProperties() {
    gradientLayer.colors = [gradient.centerColor.cgColor]
      + gradient.intermediatePoints.map { $0.color.cgColor }
      + [gradient.outerColor.cgColor]
    gradientLayer.locations =
      ([0] + gradient.intermediatePoints.map { $0.location } + [1]) as [NSNumber]
    gradientLayer.startPoint = gradient.center.rawValue
    gradientLayer.endPoint = gradient.end.rawValue
  }
}
