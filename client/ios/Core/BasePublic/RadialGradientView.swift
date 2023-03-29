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
    fatalError("init(coder:) has not been implemented")
  }

  private func setGradientProperties() {
    gradientLayer.colors = [gradient.centerColor.cgColor]
      + gradient.intermediatePoints.map { $0.color.cgColor }
      + [gradient.outerColor.cgColor]
    gradientLayer.locations =
      ([0] + gradient.intermediatePoints.map { $0.location } + [1]) as [NSNumber]
  }

  public override func layoutSubviews() {
    super.layoutSubviews()
    gradientLayer.startPoint = startPoint
    gradientLayer.endPoint = endPoint
  }

  private var startPoint: CGPoint {
    let centerX: CGFloat
    switch gradient.centerX {
    case let .relative(x):
      centerX = x
    case let .absolute(x):
      centerX = CGFloat(x) / bounds.width
    }
    let centerY: CGFloat
    switch gradient.centerY {
    case let .relative(y):
      centerY = y
    case let .absolute(y):
      centerY = CGFloat(y) / bounds.height
    }
    return CGPoint(x: centerX, y: centerY)
  }

  private var endPoint: CGPoint {
    let startPoint = startPoint
    let maxSize = max(bounds.width, bounds.height)
    let kx = maxSize / bounds.width
    let ky = maxSize / bounds.height
    switch gradient.end {
    case let .relativeToBorders(relativeRaduis):
      let radius: CGFloat
      switch relativeRaduis {
      case .nearestSide:
        if startPoint.x.isOnBorder || startPoint.y.isOnBorder {
          return CGPoint(x: startPoint.x + .ulpOfOne, y: startPoint.y + .ulpOfOne)
        }
        radius = min(startPoint.x.nearestDistance / kx, startPoint.y.nearestDistance / ky)
      case .nearestCorner:
        if startPoint.x.isOnBorder, startPoint.y.isOnBorder {
          return CGPoint(x: startPoint.x + .ulpOfOne, y: startPoint.y + .ulpOfOne)
        }
        radius =
          sqrt(
            pow(startPoint.x.nearestDistance / kx, 2) +
              pow(startPoint.y.nearestDistance / ky, 2)
          )
      case .farthestCorner:
        radius =
          sqrt(
            pow(startPoint.x.farthestDistance / kx, 2) +
              pow(startPoint.y.farthestDistance / ky, 2)
          )
      case .farthestSide:
        radius = max(startPoint.x.farthestDistance / kx, startPoint.y.farthestDistance / ky)
      }
      return CGPoint(x: startPoint.x + radius * kx, y: startPoint.y + radius * ky)
    case let .relativeToSize(point):
      switch gradient.shape {
      case .ellipse:
        return point.rawValue
      case .circle:
        return CGPoint(x: point.x * kx, y: point.y * ky)
      }
    case let .absolute(radius):
      let x = CGFloat(radius) / bounds.width + startPoint.x
      let y = CGFloat(radius) / bounds.height + startPoint.y
      return CGPoint(x: x, y: y)
    }
  }
}

extension CGFloat {
  fileprivate var nearest: CGFloat {
    self < 0.5 ? 0 : 1
  }

  fileprivate var farthest: CGFloat {
    self > 0.5 ? 0 : 1
  }

  fileprivate var nearestDistance: CGFloat {
    abs(self.nearest - self)
  }

  fileprivate var farthestDistance: CGFloat {
    abs(self.farthest - self)
  }

  fileprivate var isOnBorder: Bool {
    self == 0 || self == 1
  }
}
