import QuartzCore
import VGSL

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
    let rawPattern = border.style.rawPattern

    let layer = CAShapeLayer()
    layer.path = path
    layer.lineDashPattern = rawPattern?.calculateDashPattern(for: getLength(for: size))
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

  private func getLength(for size: CGSize) -> CGFloat {
    switch self {
    case .noClip, .clipPath: (size.width + size.height) * 2
    case let .clipCorner(cornerRadius):
      (size.width + size.height) * 2 + (.pi / 2 - 2) * cornerRadius.sum
    }
  }
}

extension BlockBorder.Style {
  fileprivate var rawPattern: [CGFloat]? {
    switch self {
    case .dashed: [6, 2]
    case .solid: nil
    }
  }
}

extension [CGFloat] {
  fileprivate func calculateDashPattern(for length: CGFloat) -> [NSNumber] {
    guard !isEmpty else { return [] }
    let sum = reduce(0, +)

    let n = Swift.max(1, Int(round(length / sum)))
    let factor = length / (CGFloat(n) * sum)
    return map { NSNumber(value: $0 * factor) }
  }
}

extension CornerRadii {
  fileprivate var sum: CGFloat {
    topLeft + topRight + bottomLeft + bottomRight
  }
}
