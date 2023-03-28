import CoreGraphics
import QuartzCore

import CommonCorePublic

public enum BoundaryTrait: Equatable {
  case noClip
  case clipCorner(CornerRadii)
  case clipPath(CGPath)

  public static let clips = BoundaryTrait.cornerRadius(0)

  public static func clipCorner(
    radius: CGFloat,
    corners: CGRect.Corners = .all
  ) -> BoundaryTrait {
    BoundaryTrait.cornerRadius(radius, corners: corners)
  }

  public static func cornerRadius(
    _ radius: CGFloat,
    corners: CGRect.Corners = .all
  ) -> BoundaryTrait {
    .clipCorner(
      CornerRadii(radius: radius, corners: corners)
    )
  }

  public static func path(_ path: CGPath) -> BoundaryTrait {
    .clipPath(path)
  }
}

extension BoundaryTrait {
  func allCornersAreApproximatelyEqualToZero() -> Bool {
    switch self {
    case .noClip, .clipPath:
      return true
    case let .clipCorner(radii):
      return radii.allCornersAreApproximatelyEqualToZero()
    }
  }
}

extension CornerRadii {
  init(
    radius: CGFloat,
    corners: CGRect.Corners
  ) {
    self.init(
      topLeft: corners.contains(.topLeft) ? radius : 0,
      topRight: corners.contains(.topRight) ? radius : 0,
      bottomLeft: corners.contains(.bottomLeft) ? radius : 0,
      bottomRight: corners.contains(.bottomRight) ? radius : 0
    )
  }

  func allCornersAreApproximatelyEqualToZero() -> Bool {
    topLeft.isApproximatelyEqualTo(0) &&
      topRight.isApproximatelyEqualTo(0) &&
      bottomLeft.isApproximatelyEqualTo(0) &&
      bottomRight.isApproximatelyEqualTo(0)
  }
}
