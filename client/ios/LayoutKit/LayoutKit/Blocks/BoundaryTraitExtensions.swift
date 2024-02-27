import QuartzCore

import CommonCorePublic

typealias BoundaryInfo = (radius: CGFloat, corners: CACornerMask, layer: CALayer?)

extension BoundaryTrait {
  var clipsToBounds: Bool {
    switch self {
    case .noClip:
      false
    case .clipCorner, .clipPath:
      true
    }
  }

  func makeInfo(for size: CGSize) -> BoundaryInfo {
    switch self {
    case .noClip:
      return (radius: 0, corners: .allCorners, layer: nil)
    case .clipPath:
      return (radius: 0, corners: .allCorners, layer: makeMaskLayer(for: size))
    case let .clipCorner(cornerRadii):
      if let unifiedRadius = cornerRadii.unifiedRadius {
        let radius = clamp(
          unifiedRadius,
          min: 0,
          max: size.minDimension.half
        )
        return (radius: radius, corners: cornerRadii.maskedCorners, layer: nil)
      } else {
        return (radius: 0, corners: .allCorners, layer: makeMaskLayer(for: size))
      }
    }
  }

  func makeMaskLayer(for size: CGSize) -> CALayer? {
    guard let path = makeMaskPath(for: size, inset: 0) else {
      return nil
    }

    let layer = CAShapeLayer()
    layer.path = path
    return layer
  }

  func makeBorderLayer(for size: CGSize, border: BlockBorder) -> CALayer? {
    guard let path = makeMaskPath(for: size, inset: border.width / 2) else {
      return nil
    }

    let layer = CAShapeLayer()
    layer.path = path
    layer.strokeColor = border.color.cgColor
    layer.fillColor = Color.clear.cgColor
    layer.lineWidth = border.width
    return layer
  }

  private func makeMaskPath(for size: CGSize, inset: CGFloat) -> CGPath? {
    switch self {
    case let .clipPath(path):
      path
    case let .clipCorner(cornerRadii):
      .roundedRect(
        size: size,
        cornerRadii: cornerRadii,
        inset: inset
      )
    case .noClip:
      nil
    }
  }
}
