// Copyright 2016 Yandex LLC. All rights reserved.

import Foundation
import UIKit

public final class LinearGradientView: UIView {
  private var gradientLayer: LinearGradientLayer {
    layer as! LinearGradientLayer
  }

  public func set(gradient: Gradient.Linear) {
    gradientLayer.gradient = gradient
  }

  public init(_ gradient: Gradient.Linear) {
    super.init(frame: .zero)
    set(gradient: gradient)
    isOpaque = false
    layer.needsDisplayOnBoundsChange = true
  }

  public convenience init(
    startColor: Color,
    endColor: Color,
    direction: Gradient.Linear.Direction
  ) {
    self.init(.init(
      startColor: startColor,
      endColor: endColor,
      direction: direction
    ))
  }

  @available(*, unavailable)
  public required init(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public override class var layerClass: AnyClass {
    LinearGradientLayer.self
  }
}

// https://stackoverflow.com/questions/38821631/cagradientlayer-diagonal-gradient/43176174
public final class LinearGradientLayer: CALayer {
  public var gradient: Gradient.Linear? {
    didSet {
      guard gradient != oldValue else { return }
      setNeedsDisplay()
    }
  }

  public override func draw(in ctx: CGContext) {
    guard let gradient = gradient else {
      return
    }

    guard let cgGradient = CGGradient(
      colorsSpace: CGColorSpaceCreateDeviceRGB(),
      colors: gradient.colors.map { $0.cgColor } as CFArray,
      locations: gradient.locations
    ) else {
      assertionFailure()
      return
    }

    ctx.drawLinearGradient(
      cgGradient,
      start: gradient.direction.from.absolutePosition(in: bounds),
      end: gradient.direction.to.absolutePosition(in: bounds),
      options: [.drawsBeforeStartLocation, .drawsAfterEndLocation]
    )
  }
}
